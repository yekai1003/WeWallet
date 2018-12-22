package com.quincysx.crypto.bip39;

/**
 * @author QuincySx
 * @date 2018/5/15 上午10:18
 */
public interface PBKDF2WithHmacSHA512 {
    byte[] hash(final char[] chars, final byte[] salt);
}
