package com.quincysx.crypto.utils;

import org.spongycastle.crypto.digests.RIPEMD160Digest;
import org.spongycastle.crypto.digests.SHA256Digest;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author QuincySx
 * @date 2018/3/1 下午5:00
 */
public final class RIPEMD160 {
    private static final int RIPEMD160_DIGEST_LENGTH = 20;

    public static byte[] ripemd160(byte[] bytes) {
        RIPEMD160Digest ripemd160Digest = new RIPEMD160Digest();
        ripemd160Digest.update(bytes, 0, bytes.length);
        byte[] hash160 = new byte[RIPEMD160_DIGEST_LENGTH];
        ripemd160Digest.doFinal(hash160, 0);
        return hash160;
    }

    public static byte[] hash160(final byte[] bytes) {
        return ripemd160(SHA256.sha256(bytes));
    }

}
