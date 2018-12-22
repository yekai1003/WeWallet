package com.quincysx.crypto.eos;

import com.quincysx.crypto.ECKeyPair;
import com.quincysx.crypto.Key;
import com.quincysx.crypto.bip32.ValidationException;
import com.quincysx.crypto.utils.Base58;
import com.quincysx.crypto.utils.KECCAK256;
import com.quincysx.crypto.utils.RIPEMD160;
import com.quincysx.crypto.utils.SHA256;

import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.Arrays;

/**
 * @author QuincySx
 * @date 2018/6/13 上午11:28
 */
public class EOSECKeyPair extends ECKeyPair {
    public static final String address_prefix = "EOS";
    public static final int PRIVATE_KEY_PREFIX = 0x80;

    public static EOSECKeyPair parse(Key keyPair) {
        return new EOSECKeyPair(keyPair);
    }

    public static EOSECKeyPair parse(String privateKey) throws ValidationException {
        byte[] decode = Base58.decode(privateKey);
        byte[] checksum = SHA256.doubleSha256(decode, 0, decode.length - 4);
        for (int i = 0; i < 4; i++) {
            if (decode[decode.length - 4 + i] != checksum[i]) {
                throw new ValidationException("私钥验证失败");
            }
        }
        byte[] key = new byte[decode.length - 5];
        System.arraycopy(decode, 1, key, 0, key.length);
        BigInteger integer = new BigInteger(1, key);

        Arrays.fill(key, (byte) 0);
        Arrays.fill(decode, (byte) 0);
        Arrays.fill(checksum, (byte) 0);
        return new EOSECKeyPair(integer);
    }

    public EOSECKeyPair(byte[] p) throws ValidationException {
        super(p, true);
    }

    public EOSECKeyPair(BigInteger priv) {
        super(priv, true);
    }

    public EOSECKeyPair(Key keyPair) {
        super(keyPair);
    }

    @Override
    public byte[] getRawPrivateKey() {
        return super.getRawPrivateKey();
    }

    @Override
    public String getPrivateKey() {
        byte[] rawPrivateKey = getRawPrivateKey();
        byte[] privateKey = new byte[rawPrivateKey.length + 5];

        privateKey[0] = (byte) PRIVATE_KEY_PREFIX;
        System.arraycopy(rawPrivateKey, 0, privateKey, 1, rawPrivateKey.length);

        byte[] checksum = SHA256.doubleSha256(privateKey, 0, privateKey.length - 4);
        System.arraycopy(checksum, 0, privateKey, rawPrivateKey.length + 1, 4);

        Arrays.fill(checksum, (byte) 0);
        Arrays.fill(rawPrivateKey, (byte) 0);
        return Base58.encode(privateKey);
    }

    @Override
    public byte[] getRawPublicKey() {
        byte[] publicKey = new byte[pubComp.length];
        System.arraycopy(pubComp, 0, publicKey, 0, publicKey.length);
        return publicKey;
    }

    @Override
    public String getPublicKey() {
        byte[] rawPublicKey = getRawPublicKey();
        byte[] publicKey = new byte[rawPublicKey.length + 4];
        System.arraycopy(rawPublicKey, 0, publicKey, 0, rawPublicKey.length);

        byte[] bytes = RIPEMD160.ripemd160(rawPublicKey);
        System.arraycopy(bytes, 0, publicKey, rawPublicKey.length, 4);

        StringBuffer bf = new StringBuffer(address_prefix);
        bf.append(Base58.encode(publicKey));

        Arrays.fill(rawPublicKey, (byte) 0);
        Arrays.fill(publicKey, (byte) 0);
        Arrays.fill(bytes, (byte) 0);
        return bf.toString();
    }

    @Override
    public byte[] getRawAddress() {
        byte[] byteAddress = KECCAK256.keccak256(getRawPublicKey());
        byte[] address = new byte[20];
        System.arraycopy(byteAddress, 12, address, 0, address.length);
        return address;
    }

    @Override
    public String getAddress() {
        return getPublicKey();
    }
}
