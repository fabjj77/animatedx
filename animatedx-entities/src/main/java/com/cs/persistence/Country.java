package com.cs.persistence;

import javax.annotation.Nonnull;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;

/**
 * @author Omid Alaepour
 */
@SuppressWarnings("UnusedDeclaration")
@XmlRootElement
@XmlEnum
public enum Country {
    AFGHANISTAN("AF", "AFG", "fa_AF"),
    ÅLAND_ISLANDS("AX", "ALA", "sv_SE"),
    ALBANIA("AL", "ALB", "sq_AL"),
    ALGERIA("DZ", "DZA", "ar_DZ"),
    AMERICAN_SAMOA("AS", "ASM", "ca_ES"),
    ANDORRA("AD", "AND", "ca_AD"),
    ANGOLA("AO", "AGO", "pt_AO"),
    ANGUILLA("AI", "AIA", "en_GB"),
    ANTIGUA_AND_BARBUDA("AG", "ATG", "en_GB"),
    ARGENTINA("AR", "ARG", "es_AR"),
    ARMENIA("AM", "ARM", "hy_AM"),
    ARUBA("AW", "ABW", "nl_AW"),
    AUSTRALIA("AU", "AUS", "en_AU"),
    AUSTRIA("AT", "AUT", "de_AT"),
    AZERBAIJAN("AZ", "AZE", "az"),
    BAHAMAS("BS", "BHS", "en_GB"),
    BAHRAIN("BH", "BHR", "ar_BH"),
    BANGLADESH("BD", "BGD", "bn_BD"),
    BARBADOS("BB", "BRB", "en_BB"),
    BELARUS("BY", "BLR", "be_BY"),
    BELGIUM("BE", "BEL", "nl_BE"),
    BELIZE("BZ", "BLZ", "en_BZ"),
    BENIN("BJ", "BEN", "fr_BJ"),
    BERMUDA("BM", "BMU", "en_BM"),
    BHUTAN("BT", "BTN", "dz_BT"),
    BOLIVIA("BO", "BOL", "es_BO"),
    BONAIRE_SINT_EUSTATIUS_AND_SABA("BQ", "BES", "nl_NL"),
    BOSNIA_AND_HERZEGOVINA("BA", "BIH", "bs_BA"),
    BOTSWANA("BW", "BWA", "en_BW"),
    BOUVET_ISLAND("BV", "BVT", "no_NO"),
    BRAZIL("BR", "BRA", "pt_BR"),
    BRITISH_INDIAN_OCEAN_TERRITORY("IO", "IOT", "en_GB"),
    BRUNEI_DARUSSALAM("BN", "BRN", "ms_MY"),
    BULGARIA("BG", "BGR", "bg_BG"),
    BURKINA_FASO("BF", "BFA", "fr_FR"),
    BURUNDI("BI", "BDI", "fr_BI"),
    CAMBODIA("KH", "KHM", "km_KH"),
    CAMEROON("CM", "CMR", "ksf_CM"),
    CANADA("CA", "CAN", "en_CA"),
    CAPE_VERDE("CV", "CPV", "kea_CV"),
    CAYMAN_ISLANDS("KY", "CYM", "en_GB"),
    CENTRAL_AFRICAN_REPUBLIC("CF", "CAF", "fr_FR"),
    CHAD("TD", "TCD", "fr_TD"),
    CHILE("CL", "RCH", "es_CL"),
    CHINA("CN", "CHN", "zh_Hant_HK"),
    COCOS_KEELING_ISLANDS("CC", "CCK", "en_GB"),
    COLOMBIA("CO", "COL", "es_CO"),
    COMOROS("KM", "COM", "fr_KM"),
    CONGO("CG", "COG", "fr_CD"),
    CONGO_THE_DEMOCRATIC_REPUBLIC_OF_THE("CD", "COD", "fr_CG"),
    COOK_ISLANDS("CK", "COK", "en_GB"),
    COSTA_RICA("CR", "CRI", "es_CR"),
    CROATIA("HR", "HRV", "hr_HR"),
    CUBA("CU", "CUB", "es_ES"),
    CURAÇAO("CW", "CUW", "nl_CW"),
    CYPRUS("CY", "CYP", "el_CY"),
    CZECH_REPUBLIC("CZ", "CZE", "cs_CZ"),
    DENMARK("DK", "DNK", "da_DK"),
    DJIBOUTI("DJ", "DJI", "fr_DJ"),
    DOMINICAN_REPUBLIC("DO", "DOM", "es_DO"),
    ECUADOR("EC", "ECU", "es_EC"),
    EGYPT("EG", "EGY", "ar_EG"),
    EL_SALVADOR("SV", "SLV", "es_SV"),
    EQUATORIAL_GUINEA("GQ", "GNQ", "es_ES"),
    ERITREA("ER", "ERI", "ti_ER"),
    ESTONIA("EE", "EST", "et_EE", 21),
    ETHIOPIA("ET", "ETH", "ti_ET"),
    FALKLAND_ISLANDS_MALVINAS("FK", "FLK", "en_GB"),
    FAROE_ISLANDS("FO", "FRO", "da_DK"),
    FIJI("FJ", "FJI", "en_IN"),
    FINLAND("FI", "FIN", "fi_FI"),
    FRANCE("FR", "FRA", "fr_FR"),
    FRENCH_GUIANA("GF", "GUF", "fr_GF"),
    FRENCH_POLYNESIA("PF", "PYF", "fr_FR"),
    GABON("GA", "GAB", "fr_GA"),
    GAMBIA("GM", "GMB", "en_GB"),
    GEORGIA("GE", "GEO", "ka_GE"),
    GERMANY("DE", "DEU", "de_DE"),
    GHANA("GH", "GHA", "ak_GH"),
    GIBRALTAR("GI", "GIB", "en_GB"),
    GREECE("GR", "GRC", "el_GR"),
    GREENLAND("GL", "GRL", "kl_GL"),
    GRENADA("GD", "GRD", "en_GB"),
    GUADELOUPE("GP", "GLP", "fr_GP"),
    GUAM("GU", "GUM", "en_GU"),
    GUATEMALA("GT", "GTM", "es_GT"),
    GUERNSEY("GG", "GGY", "en_GB"),
    GUINEA("GN", "GIN", "fr_GN"),
    GUINEA_BISSAU("GW", "GNB", "fr_GQ"),
    GUYANA("GY", "GUY", "en_GY"),
    HAITI("HT", "HTI", "fr_FR"),
    HONDURAS("HN", "HND", "es_HN"),
    HONG_KONG("HK", "HKG", "zh_Hant_HK"),
    HUNGARY("HU", "HUN", "hu_HU"),
    ICELAND("IS", "ISL", "is_IS"),
    INDIA("IN", "IND", "en_IN"),
    INDONESIA("ID", "IDN", "id_ID"),
    IRAN("IR", "IRN", "pe_IR"),
    IRAQ("IQ", "IRQ", "ar_IQ"),
    IRELAND("IE", "IRL", "en_IE"),
    ISLE_OF_MAN("IM", "IMN", "en_GB"),
    ISRAEL("IL", "ISR", "he_IL"),
    ITALY("IT", "ITA", "it_IT"),
    JAMAICA("JM", "JAM", "en_JM"),
    JAPAN("JP", "JPN", "ja_JP"),
    JERSEY("JE", "JEY", "en_GB"),
    JORDAN("JO", "JOR", "ar_JO"),
    KAZAKHSTAN("KZ", "KAZ", "kk_Cyrl_KZ"),
    KENYA("KE", "KEN", "sw_KE"),
    KIRIBATI("KI", "KIR", "en_GB"),
    SOUTH_KOREA("KR", "KOR", "ko_KR"),
    KUWAIT("KW", "KWT", "ar_KW"),
    KYRGYZSTAN("KG", "KGZ", "ru_RU"),
    LAO_PEOPLES_DEMOCRATIC_REPUBLIC("LA", "LAO", "th_TH"),
    LATVIA("LV", "LVA", "lv_LV"),
    LEBANON("LB", "LBN", "ar_LB"),
    LESOTHO("LS", "LSO", "en_GB"),
    LIBERIA("LR", "LBR", "vai_Latn_LR"),
    LIBYA("LY", "LBY", "ar_LY"),
    LIECHTENSTEIN("LI", "LIE", "de_LI"),
    LITHUANIA("LT", "LTU", "lt_LT"),
    LUXEMBOURG("LU", "LUX", "fr_LU"),
    MACAO("MO", "MAC", "en_GB"),
    MACEDONIA("MK", "MKD", "mk_MK"),
    MADAGASCAR("MG", "MDG", "fr_MG"),
    MALAWI("MW", "MWI", "en_GB"),
    MALAYSIA("MY", "MYS", "ms_MY"),
    MALDIVES("MV", "MDV", "en_GB"),
    MALI("ML", "MLI", "bm_ML"),
    MALTA("MT", "MLT", "en_MT"),
    MARSHALL_ISLANDS("MH", "MHL", "en_GB"),
    MARTINIQUE("MQ", "MTQ", "fr_MQ"),
    MAURITANIA("MR", "MRT", "ar_EG"),
    MAURITIUS("MU", "MUS", "en_MU"),
    MAYOTTE("YT", "MYT", "fr_YT"),
    MEXICO("MX", "MEX", "es_MX"),
    MICRONESIA_FEDERATED_STATES_OF("FM", "FSM", "en_GB"),
    MOLDOVA("MD", "MDA", "ro_MD"),
    MONACO("MC", "MCO", "fr_MC"),
    MONGOLIA("MN", "MNG", "en_GB"),
    MONTENEGRO("ME", "MNE", "sr_Cyrl_ME"),
    MONTSERRAT("MS", "MSR", "en_GB"),
    MOROCCO("MA", "MAR", "ar_MA"),
    MOZAMBIQUE("MZ", "MOZ", "pt_MZ"),
    MYANMAR("MM", "MMR", "my_MM"),
    NAMIBIA("NA", "NAM", "af_NA"),
    NAURU("NR", "NRU", "en_GB"),
    NEPAL("NP", "NPL", "ne_NP"),
    NETHERLANDS("NL", "NLD", "nl_NL"),
    NEW_CALEDONIA("NC", "NCL", "fr_FR"),
    NEW_ZEALAND("NZ", "NZL", "en_NZ"),
    NICARAGUA("NI", "NIC", "es_NI"),
    NIGER("NE", "NER", "fr_NE"),
    NIGERIA("NG", "NGA", "ha_Latn_NG"),
    NIUE("NU", "NIU", "en_GB"),
    NORFOLK_ISLAND("NF", "NFK", "en_GB"),
    NORTHERN_MARIANA_ISLANDS("MP", "MNP", "en_US"),
    NORWAY("NO", "NOR", "nb_NO"),
    OMAN("OM", "OMN", "ar_OM"),
    PAKISTAN("PK", "PAK", "en_PK"),
    PALAU("PW", "PLW", "en_GB"),
    PALESTINE("PS", "PSE", "ar_EG"),
    PANAMA("PA", "PAN", "es_PA"),
    PAPUA_NEW_GUINEA("PG", "PNG", "en_GB"),
    PARAGUAY("PY", "PRY", "es_PY"),
    PERU("PE", "PER", "es_PE"),
    PHILIPPINES("PH", "PHL", "en_PH"),
    PITCAIRN("PN", "PCN", "en_GB"),
    POLAND("PL", "POL", "pl_PL"),
    PORTUGAL("PT", "PRT", "pt_PT"),
    PUERTO_RICO("PR", "PRI", "es_PR"),
    QATAR("QA", "QAT", "ar_QA"),
    RÉUNION("RE", "REU", "fr_FR"),
    ROMANIA("RO", "ROU", "ro_RO"),
    RUSSIA("RU", "RUS", "ru_RU"),
    RWANDA("RW", "RWA", "fr_RW"),
    SAINT_KITTS_AND_NEVIS("KN", "KNA", "en_GB"),
    SAINT_LUCIA("LC", "LCA", "en_GB"),
    SAINT_MARTIN_FRENCH_PART("MF", "MAF", "fr_FR"),
    SAINT_PIERRE_AND_MIQUELON("PM", "SPM", "fr_FR"),
    SAINT_VINCENT_AND_THE_GRENADINES("VC", "VCT", "en_GB"),
    SAMOA("WS", "WSM", "en_AS"),
    SAN_MARINO("SM", "SMR", "it_IT"),
    SAO_TOME_AND_PRINCIPE("ST", "STP", "pt_PT"),
    SAUDI_ARABIA("SA", "SAU", "ar_SA"),
    SENEGAL("SN", "SEN", "fr_SN"),
    SERBIA("RS", "SRB", "sr_Cyrl"),
    SEYCHELLES("SC", "SYC", "fr_FR"),
    SIERRA_LEONE("SL", "SLE", "en_GB"),
    SINGAPORE("SG", "SGP", "en_SG"),
    SLOVAKIA("SK", "SVK", "sk_SK"),
    SLOVENIA("SI", "SVN", "sl_SI"),
    SOLOMON_ISLANDS("SB", "SLB", "en_GB"),
    SOMALIA("SO", "SOM", "so_SO"),
    SOUTH_AFRICA("ZA", "ZAF", "af_ZA"),
    SOUTH_GEORGIA_AND_THE_SOUTH_SANDWICH_ISLANDS("GS", "SGS", "en_GB"),
    SOUTH_SUDAN("SS", "SSD", "ar_SD"),
    SPAIN("ES", "ESP", "es_ES"),
    SRI_LANKA("LK", "LKA", "si_LK"),
    SUDAN("SD", "SDN", "ar_SD"),
    SURINAME("SR", "SUR", "nl_NL"),
    SWAZILAND("SZ", "SWZ", "en_GB"),
    SWEDEN("SE", "SWE", "sv_SE"),
    SWITZERLAND("CH", "CHE", "fr_CH"),
    SYRIAN_ARAB_REPUBLIC("SY", "SYR", "ar_SY"),
    TAIWAN("TW", "TWN", "zh_Hant_TW"),
    TAJIKISTAN("TJ", "TJK", "ru_RU"),
    TANZANIA("TZ", "TZA", "asa_TZ"),
    THAILAND("TH", "THA", "th_TH"),
    TIMOR_LESTE("TL", "TLS", "pt_PT"),
    TOGO("TG" , "TGO", "ee_TG"),
    TOKELAU("TK", "TKL", "en_GB"),
    TONGA("TO", "TON", "to_TO"),
    TRINIDAD_AND_TOBAGO("TT", "TTO", "en_TT"),
    TUNISIA("TN", "TUN", "ar_TN"),
    TURKEY("TR", "TUR", "tr_TR"),
    TURKMENISTAN("TM", "TKM", "ru_RU"),
    TURKS_AND_CAICOS_ISLANDS("TC", "TCA", "en_GB"),
    TUVALU("TV", "TUV", "en_GB"),
    UGANDA("UG", "UGA", "cgg_UG"),
    UKRAINE("UA", "UKR", "ru_UA"),
    UNITED_ARAB_EMIRATES("AE", "ARE", "ar_AE"),
    UNITED_KINGDOM("GB", "GBR", "en_GB"),
    UNITED_STATES("US", "USA", "en_US"),
    UNITED_STATES_MINOR_OUTLYING_ISLANDS("UM", "UMI", "en_US"),
    URUGUAY("UY", "URY", "es_UY"),
    UZBEKISTAN("UZ", "UZB", "uz_Cyrl_UZ"),
    VANUATU("VU", "VUT", "fr_FR"),
    VENEZUELA("VE", "VEN", "es_VE"),
    VIETNAM("VN", "VNM", "vi_VN"),
    VIRGIN_ISLANDS_BRITISH("VG", "VGB", "en_VI"),
    VIRGIN_ISLANDS("VI", "VIR", "en_VI"),
    YEMEN("YE", "YEM", "ar_YE"),
    ZAMBIA("ZM", "ZMB", "bem_ZM"),
    ZIMBABWE("ZW", "ZWE", "en_ZW");

    private final String isoCode;
    private final String iso3Code;
    private final Integer minimumAge;
    private final String locale;
    private static final Collection<Country> blockedCountries =
            Collections.unmodifiableCollection(Arrays.asList(BELGIUM, CANADA, DENMARK, FRANCE, ITALY, IRAN, MALTA, SPAIN, UNITED_STATES, VIRGIN_ISLANDS,
                                                             UNITED_STATES_MINOR_OUTLYING_ISLANDS, SOUTH_KOREA, GIBRALTAR, ISLE_OF_MAN));


    private Country(final String isoCode, final String iso3Code, final String locale) {
        this(isoCode, iso3Code, locale, 18);
    }

    private Country(final String isoCode,final String iso3Code, final String locale, final Integer minimumAge) {
        this.isoCode = isoCode;
        this.iso3Code = iso3Code;
        this.minimumAge = minimumAge;
        this.locale = locale;
    }

    public boolean isAgeValid(final Date birthday) {
        final Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.YEAR, -minimumAge);

        return calendar.getTime().after(birthday);
    }

    public String toIso() {
        return isoCode;
    }

    public String toIso3() {
        return iso3Code;
    }

    public String getLocale() {
        return locale;
    }

    @Nonnull
    public static Country getCountryByIsoCode(final String countryIsoCode) {
        for (final Country country : Country.values()) {
            if (country.toIso().equals(countryIsoCode)){
                return country;
            }
        }
        throw new NullPointerException("Country with iso code " + countryIsoCode + " is not found");
    }

    public static List<Country> getAllCountries() {
        return new ArrayList<>(EnumSet.allOf(Country.class));
    }

    public static List<Country> getAllCountriesExcept(final Country country) {
        final List<Country> list = new ArrayList<>(EnumSet.allOf(Country.class));
        list.remove(country);
        return list;
    }

    public static List<Country> getAllCountriesExcept(final List<Country> countries) {
        final List<Country> list = new ArrayList<>(EnumSet.allOf(Country.class));
        list.removeAll(countries);
        return list;
    }

    public static Collection<Country> getBlockedCountries() {
        return blockedCountries;
    }
}
