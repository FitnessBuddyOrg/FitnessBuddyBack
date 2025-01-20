package api.fitnessbuddyback.enumeration;

import lombok.Getter;

@Getter
public enum Language {
    ENGLISH("gb"),
    FRENCH("fr"),
    RUSSIAN("ru"),
    CUSTOM("custom", true);

    private final String localeString;
    private final boolean isCustom;

    Language(String localeString) {
        this(localeString, false);
    }

    Language(String localeString, boolean isCustom) {
        this.localeString = localeString;
        this.isCustom = isCustom;
    }

    @Override
    public String toString() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }

    public static Language getLanguage(String languageString) {
        for (Language language : values()) {
            if (language.name().equalsIgnoreCase(languageString)) {
                return language;
            }
        }
        return ENGLISH;
    }
}