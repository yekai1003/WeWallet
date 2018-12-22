package com.quincysx.crypto;

import java.security.SecureRandom;
import java.util.Arrays;

/**
 * @author QuincySx
 * @date 2018/3/9 下午2:57
 */
public class SecureCharSequence implements CharSequence {
    private char[] chars;

    public SecureCharSequence(CharSequence charSequence) {
        this(charSequence, 0, charSequence.length());
    }

    public SecureCharSequence(char[] chars) {
        wipe();
        this.chars = chars;
    }

    private SecureCharSequence(CharSequence charSequence, int start, int end) {
        // pulled from http://stackoverflow.com/a/15844273
        wipe();
        int length = end - start;
        chars = new char[length];
        for (int i = start;
             i < end;
             i++) {
            chars[i - start] = charSequence.charAt(i);
        }
    }

    public void wipe() {
        if (chars != null) {
            Arrays.fill(chars, ' ');
            SecureRandom r = new SecureRandom();
            byte[] bytes = new byte[chars.length];
            r.nextBytes(bytes);
            for (int i = 0;
                 i < chars.length;
                 i++) {
                chars[i] = (char) bytes[i];
            }
            Arrays.fill(chars, ' ');
        }
    }

    protected void finalize() {
        wipe();
    }

    @Override
    public int length() {
        if (chars != null) {
            return chars.length;
        }
        return 0;
    }

    @Override
    public char charAt(int index) {
        if (chars != null) {
            return chars[index];
        }
        return 0;
    }

    @Override
    public String toString() {
        return String.valueOf(this.chars);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof SecureCharSequence) {
            return Arrays.equals(chars, ((SecureCharSequence) o).chars);
        }
        return false;
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        SecureCharSequence s = new SecureCharSequence(this, start, end);
        return s;
    }
}
