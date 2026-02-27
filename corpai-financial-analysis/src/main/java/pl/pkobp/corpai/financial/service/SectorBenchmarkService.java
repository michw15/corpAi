package pl.pkobp.corpai.financial.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.pkobp.corpai.financial.domain.SectorBenchmark;

/**
 * Compares company financial indicators to sector averages.
 */
@Service
@Slf4j
public class SectorBenchmarkService {

    /**
     * Compares the company's indicators against the sector benchmark.
     *
     * @param nip       company NIP
     * @param sectorPkd PKD sector code
     * @return sector benchmark comparison
     */
    public SectorBenchmark compare(String nip, String sectorPkd) {
        log.info("Comparing NIP: {} against sector: {}", nip, sectorPkd);
        // In real implementation, would load sector averages from a database
        // populated from GUS/NBP statistical data
        return SectorBenchmark.builder()
                .companyNip(nip)
                .sectorPkd(sectorPkd)
                .build();
    }
}
