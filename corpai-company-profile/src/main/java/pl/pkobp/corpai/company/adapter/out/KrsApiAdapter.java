package pl.pkobp.corpai.company.adapter.out;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import pl.pkobp.corpai.common.domain.BoardMember;
import pl.pkobp.corpai.common.domain.Company;
import pl.pkobp.corpai.company.adapter.out.dto.KrsOdpisResponse;
import pl.pkobp.corpai.company.port.out.KrsApiPort;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * HTTP adapter for the KRS (National Court Register) API.
 * Calls the real KRS API at https://api-krs.ms.gov.pl
 * The API only supports fetching by KRS number (not NIP).
 */
@Component
@Slf4j
public class KrsApiAdapter implements KrsApiPort {

    private final WebClient krsWebClient;

    public KrsApiAdapter(@Qualifier("krsWebClient") WebClient krsWebClient) {
        this.krsWebClient = krsWebClient;
    }

    @Override
    public Company fetchCompanyByKrs(String krs) {
        log.info("Fetching company from KRS API by KRS: {}", krs);
        try {
            KrsOdpisResponse response = krsWebClient
                    .get()
                    .uri("/api/krs/OdpisAktualny/{krs}?rejestr=P&format=json", krs)
                    .retrieve()
                    .bodyToMono(KrsOdpisResponse.class)
                    .block();
            return mapToCompany(krs, response);
        } catch (Exception e) {
            log.error("Failed to fetch company by KRS {} from KRS API: {}", krs, e.getMessage());
            return null;
        }
    }

    @Override
    public List<FinancialStatementReference> fetchFinancialStatementRefs(String krs) {
        log.info("Fetching financial statement refs from KRS API for KRS: {}", krs);
        return List.of();
    }

    private Company mapToCompany(String krs, KrsOdpisResponse response) {
        if (response == null || response.getOdpis() == null) {
            return null;
        }

        KrsOdpisResponse.Odpis odpis = response.getOdpis();
        KrsOdpisResponse.Dzial1 dzial1 = odpis.getDzial1();
        KrsOdpisResponse.Dzial2 dzial2 = odpis.getDzial2();

        Company.CompanyBuilder builder = Company.builder().krs(krs);

        if (dzial1 != null) {
            KrsOdpisResponse.DanePodmiotu dane = dzial1.getDanePodmiotu();
            if (dane != null) {
                builder.fullName(dane.getNazwa())
                        .legalForm(dane.getFormaPrawna())
                        .nip(dane.getNip())
                        .regon(dane.getRegon());
            }

            KrsOdpisResponse.Siedziba siedziba = dzial1.getSiedziba();
            if (siedziba != null) {
                builder.voivodeship(siedziba.getWojewodztwo())
                        .city(siedziba.getMiejscowosc());
                if (siedziba.getAdres() != null) {
                    builder.registeredAddress(buildAddress(siedziba));
                }
            }
        }

        if (dzial2 != null) {
            builder.boardMembers(mapBoardMembers(dzial2));
        }

        return builder.build();
    }

    private String buildAddress(KrsOdpisResponse.Siedziba siedziba) {
        KrsOdpisResponse.Adres adres = siedziba.getAdres();
        StringBuilder sb = new StringBuilder();
        if (adres.getUlica() != null) {
            sb.append(adres.getUlica());
        }
        if (adres.getNrDomu() != null) {
            sb.append(" ").append(adres.getNrDomu());
        }
        if (adres.getNrLokalu() != null) {
            sb.append("/").append(adres.getNrLokalu());
        }
        if (adres.getKodPocztowy() != null) {
            sb.append(", ").append(adres.getKodPocztowy());
        }
        if (siedziba.getMiejscowosc() != null) {
            sb.append(" ").append(siedziba.getMiejscowosc());
        }
        return sb.toString().trim();
    }

    private List<BoardMember> mapBoardMembers(KrsOdpisResponse.Dzial2 dzial2) {
        KrsOdpisResponse.OrganReprezentujacy organ = dzial2.getOrganReprezentujacy();
        if (organ == null || organ.getCzlonkowie() == null) {
            return Collections.emptyList();
        }
        return organ.getCzlonkowie().stream()
                .map(this::mapCzlonek)
                .collect(Collectors.toList());
    }

    private BoardMember mapCzlonek(KrsOdpisResponse.Czlonek czlonek) {
        return BoardMember.builder()
                .firstName(czlonek.getImie())
                .lastName(czlonek.getNazwisko())
                .position(czlonek.getFunkcja())
                .appointedDate(parseDate(czlonek.getDataPowolania()))
                .build();
    }

    private LocalDate parseDate(String date) {
        if (date == null || date.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(date);
        } catch (DateTimeParseException e) {
            log.warn("Could not parse date: {}", date);
            return null;
        }
    }
}

