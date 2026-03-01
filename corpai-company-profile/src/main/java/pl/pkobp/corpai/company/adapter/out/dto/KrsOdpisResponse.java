package pl.pkobp.corpai.company.adapter.out.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * DTO classes mapping the KRS API OdpisAktualny JSON response structure.
 * Based on the official KRS API at https://api-krs.ms.gov.pl/api/krs/OdpisAktualny/{krs}
 * The API only accepts a KRS number (not NIP) as the identifier.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class KrsOdpisResponse {

    @JsonProperty("odpis")
    private Odpis odpis;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Odpis {

        @JsonProperty("dzial1")
        private Dzial1 dzial1;

        @JsonProperty("dzial2")
        private Dzial2 dzial2;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Dzial1 {

        @JsonProperty("danePodmiotu")
        private DanePodmiotu danePodmiotu;

        @JsonProperty("siedziba")
        private Siedziba siedziba;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DanePodmiotu {

        @JsonProperty("nazwa")
        private String nazwa;

        @JsonProperty("formaPrawna")
        private String formaPrawna;

        @JsonProperty("nip")
        private String nip;

        @JsonProperty("regon")
        private String regon;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Siedziba {

        @JsonProperty("kraj")
        private String kraj;

        @JsonProperty("wojewodztwo")
        private String wojewodztwo;

        @JsonProperty("miejscowosc")
        private String miejscowosc;

        @JsonProperty("adres")
        private Adres adres;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Adres {

        @JsonProperty("ulica")
        private String ulica;

        @JsonProperty("nrDomu")
        private String nrDomu;

        @JsonProperty("nrLokalu")
        private String nrLokalu;

        @JsonProperty("kodPocztowy")
        private String kodPocztowy;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Dzial2 {

        @JsonProperty("organReprezentujacy")
        private OrganReprezentujacy organReprezentujacy;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OrganReprezentujacy {

        @JsonProperty("nazwaOrganu")
        private String nazwaOrganu;

        @JsonProperty("sposobReprezentacji")
        private String sposobReprezentacji;

        @JsonProperty("czlonkowie")
        private List<Czlonek> czlonkowie;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Czlonek {

        @JsonProperty("imie")
        private String imie;

        @JsonProperty("nazwisko")
        private String nazwisko;

        @JsonProperty("funkcja")
        private String funkcja;

        @JsonProperty("dataPowolania")
        private String dataPowolania;
    }
}
