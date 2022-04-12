package base;

public enum EbayLanguage {

    ENGLISH("English"),
    ITALIAN("Italian");

    private String name;

    private EbayLanguage(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
