package base.locale;

public enum EbayLanguage {

    ENGLISH("english"),
    ITALIAN("italian"),
    SPANISH("spanish");

    private String name;

    private EbayLanguage(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
