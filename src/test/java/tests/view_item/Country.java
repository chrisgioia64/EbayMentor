package tests.view_item;

import base.EnvironmentProperties;

public enum Country {

    US("United States"),
    UK("United Kingdom"),
    AU("Australia"),
    JAPAN("Japan"),
    VENEZUELA("Venezuela"),
    SWEDEN("Sweden");

    private Country(String displayName) {
        this.displayName = displayName;
    }

    private String displayName;

//    public String getDisplayName() {
//        return displayName;
//    }

    public String getLocaleName() {
        return EnvironmentProperties.getInstance().getLocaleProperties().getCountry(displayName);
    }
}
