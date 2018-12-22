package com.quincysx.crypto.bip39.validation;

/**
 * @author QuincySx
 * @date 2018/5/15 上午11:20
 */
public class WordNotFoundException extends Exception {
    private final CharSequence word;
    private final CharSequence suggestion1;
    private final CharSequence suggestion2;

    public WordNotFoundException(
            final CharSequence word,
            final CharSequence suggestion1,
            final CharSequence suggestion2) {
        super(String.format(
                "Word not found in word list \"%s\", suggestions \"%s\", \"%s\"",
                word,
                suggestion1,
                suggestion2));
        this.word = word;
        this.suggestion1 = suggestion1;
        this.suggestion2 = suggestion2;
    }

    public CharSequence getWord() {
        return word;
    }

    public CharSequence getSuggestion1() {
        return suggestion1;
    }

    public CharSequence getSuggestion2() {
        return suggestion2;
    }
}
