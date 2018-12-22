package com.quincysx.crypto.utils;

import org.spongycastle.util.Strings;

import java.math.BigInteger;

/**
 * @author QuincySx
 * @date 2018/3/1 下午5:59
 */
public class Base64 {
    public static String decode(String input) {
        return Strings.fromByteArray(org.spongycastle.util.encoders.Base64.decode(input));
    }

    public static String encode(BigInteger input) {
        return String.format("%064x", input);
    }

    public static String encode(byte[] input) {
        return Strings.fromByteArray(org.spongycastle.util.encoders.Base64.encode(input));
    }
}
