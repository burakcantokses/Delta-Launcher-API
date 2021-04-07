package net.deltahook.launcher.lang;

public class LangEngine {
    static Lang lang = Lang.ENGLISH;

    public static void setLang(Lang newLang) {
        lang = newLang;
    }

    public static String translate(Expression expression, Object... args) {
        return String.format(expression.getText(), args);
    }

    public enum Expression {
        COULD_NOT_FIND_FOLDER("Couldn't find %s folder", "%s klasörü bulunamadı"),
        WRONG_NUMBER_OF_ARGS("Wrong number of arguments", "Yanlış sayıda argüman");
        //Try not to use this with complex sentences

        private final String english;
        private final String turkish;

        Expression(String english, String turkish) {
            this.english = english;
            this.turkish = turkish;
        }

        public String getEnglish() {
            return this.english;
        }

        public String getTurkish() {
            return this.turkish;
        }

        public String getText() {
            return lang == Lang.ENGLISH ? getEnglish() : getTurkish();
        }
    }

    public enum Lang {
        ENGLISH,
        TURKISH;
    }
}
