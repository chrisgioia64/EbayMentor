package base.locale;

import java.util.HashMap;
import java.util.Map;

public class LanguageItem {

    private Map<EbayLanguage, String> map;

    public LanguageItem() {
        map = new HashMap<>();
    }

    public void putValue(EbayLanguage language, String value) {
        map.put(language, value);
    }

    public String getValue(EbayLanguage language) {
        return map.get(language);
    }
}
