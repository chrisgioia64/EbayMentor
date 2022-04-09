package base;

public enum EbayLocale {

    US("us", "United States"),
    UK("uk", "United Kingdom"),
    IT("it", "Italy");

    private EbayLocale(String abbreviation, String fullName) {
        this.abbreviation = abbreviation;
        this.fullName = fullName;
    }

    private String abbreviation;
    private String fullName;

    /**
     * Get the 2-letter abbreviation for the locale
     */
    public String getAbbreviation() {
       return abbreviation;
    }

    public String getFullName() {
        return fullName;
    }

    /**
     * Returns true if we are currently using this locale
     */
    public boolean isInUse() {
       String localeString = EnvironmentProperties.getInstance().getLocaleProperties().getProperty(LocaleProperties.KEY_LOCALE);
       return abbreviation.equals(localeString);
    }
}
