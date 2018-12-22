package com.quincysx.crypto.bip38;


import com.quincysx.crypto.bip32.ValidationException;
import com.quincysx.crypto.bitcoin.BitCoinECKeyPair;
import com.quincysx.crypto.utils.Base58;
import com.quincysx.crypto.utils.HexUtils;
import com.quincysx.crypto.utils.SHA256;

import org.spongycastle.crypto.generators.SCrypt;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * @author QuincySx
 * @date 2018/3/8 下午2:41
 */
public class Bip38 {
    //官方推荐参数 性能差的手机耗时特别长
    private static final int SCRYPT_N = 16384;
    private static final int SCRYPT_R = 8;
    private static final int SCRYPT_P = 8;

    //性能差的手机也秒出，安全性差
//    private static final int SCRYPT_N = 1024;
//    private static final int SCRYPT_R = 1;
//    private static final int SCRYPT_P = 1;

    private static final int SCRYPT_LENGTH = 64;

    private static final BigInteger n = new BigInteger(1, HexUtils.fromHex("fffffffffffffffffffffffffffffffebaaedce6af48a03bbfd25e8cd0364141"));

    /**
     * Encrypt a SIPA formatted private key with a passphrase using BIP38.
     * <p/>
     * This is a helper function that does everything in one go. You can call the
     * individual functions if you wish to separate it into more phases.
     *
     * @throws InterruptedException
     */
    public static String encryptNoEcMultiply(CharSequence passphrase, String base58EncodedPrivateKey) throws ValidationException, InterruptedException {
        byte[] tmp = Base58.decode(base58EncodedPrivateKey);

        int version = tmp[0] & 0xFF;

        byte[] bytes = new byte[tmp.length - 4 - 1];
        System.arraycopy(tmp, 1, bytes, 0, bytes.length);
        boolean compressed = true;
        if (bytes.length == 33 && bytes[32] == 1) {
            compressed = true;
            bytes = java.util.Arrays.copyOf(bytes, 32);  // Chop off the additional marker byte.
        } else if (bytes.length == 32) {
            compressed = false;
        }

        boolean testNet = false;
        if (version == BitCoinECKeyPair.TEST_NET_PRIVATE_KEY_PREFIX) {
            testNet = true;
        } else if (version == BitCoinECKeyPair.MAIN_NET_PRIVATE_KEY_PREFIX) {
            testNet = false;
        }

        Arrays.fill(tmp, (byte) 0);

        BitCoinECKeyPair bitCoinECKeyPair = new BitCoinECKeyPair(bytes, testNet, compressed);

        byte[] salt = Bip38.calculateScryptSalt(bitCoinECKeyPair.getAddress());
        byte[] stretchedKeyMaterial = bip38Stretch1(passphrase, salt, SCRYPT_LENGTH);

        return encryptNoEcMultiply(stretchedKeyMaterial, bitCoinECKeyPair, salt);
    }

    /**
     * Perform BIP38 compatible password stretching on a password to derive the
     * BIP38 key material
     *
     * @throws InterruptedException
     */
    public static byte[] bip38Stretch1(CharSequence passphrase, byte[] salt, int outputSize)
            throws InterruptedException {
        byte[] passwordBytes = null;
        byte[] derived;
        try {
            passwordBytes = convertToByteArray(passphrase);
            derived = SCrypt.generate(passwordBytes, salt, SCRYPT_N, SCRYPT_R, SCRYPT_P, outputSize
            );
            return derived;
        } finally {
            // Zero the password bytes.
            if (passwordBytes != null) {
                java.util.Arrays.fill(passwordBytes, (byte) 0);
            }
        }
    }

    private static byte[] convertToByteArray(CharSequence charSequence) {
        if (charSequence == null) {
            throw new RuntimeException("charSequence not NULL");
        }
        ByteBuffer bb = Charset.forName("UTF-8").encode(CharBuffer.wrap(charSequence));
        byte[] result = new byte[bb.remaining()];
        bb.get(result);
        bb.clear();
        byte[] clearTest = new byte[bb.remaining()];
        java.util.Arrays.fill(clearTest, (byte) 0);
        bb.put(clearTest);
        return result;
    }

    public static String encryptNoEcMultiply(byte[] stretcedKeyMaterial, BitCoinECKeyPair key, byte[] salt) {
        int checksumLength = 4;
        byte[] encoded = new byte[39 + checksumLength];
        int index = 0;
        encoded[index++] = (byte) 0x01;
        encoded[index++] = (byte) 0x42;

        byte non_EC_multiplied = (byte) 0xC0;
        byte compressedPublicKey = key.isCompressed() ? (byte) 0x20 : (byte) 0;
        encoded[index++] = (byte) (non_EC_multiplied | compressedPublicKey);

        // Salt
        System.arraycopy(salt, 0, encoded, index, salt.length);
        index += salt.length;

        // Derive Keys
        byte[] derivedHalf1 = new byte[32];
        System.arraycopy(stretcedKeyMaterial, 0, derivedHalf1, 0, 32);
        byte[] derivedHalf2 = new byte[32];
        System.arraycopy(stretcedKeyMaterial, 32, derivedHalf2, 0, 32);

        // Initialize AES key
        Rijndael aes = new Rijndael();
        aes.makeKey(derivedHalf2, 256);

        // Get private key bytes
        byte[] complete = key.getRawPrivateKey();

        // Insert first encrypted key part
        byte[] toEncryptPart1 = new byte[16];
        for (int i = 0; i < 16; i++) {
            toEncryptPart1[i] = (byte) ((((int) complete[i]) & 0xFF) ^ (((int) derivedHalf1[i]) & 0xFF));
        }
        byte[] encryptedHalf1 = new byte[16];
        aes.encrypt(toEncryptPart1, encryptedHalf1);
        System.arraycopy(encryptedHalf1, 0, encoded, index, encryptedHalf1.length);
        index += encryptedHalf1.length;

        // Insert second encrypted key part
        byte[] toEncryptPart2 = new byte[16];
        for (int i = 0; i < 16; i++) {
            toEncryptPart2[i] = (byte) ((((int) complete[16 + i]) & 0xFF) ^ (((int) derivedHalf1[16 + i]) & 0xFF));
        }
        byte[] encryptedHalf2 = new byte[16];
        aes.encrypt(toEncryptPart2, encryptedHalf2);
        System.arraycopy(encryptedHalf2, 0, encoded, index, encryptedHalf2.length);
        index += encryptedHalf2.length;

        // Checksum
        byte[] checkSum = SHA256.doubleSha256(encoded, 0, 39);

        byte[] start = new byte[4];
        System.arraycopy(checkSum, 0, start, 0, 4);

        System.arraycopy(start, 0, encoded, 39, checksumLength);

        // Base58 encode
        return Base58.encode(encoded);
    }

    public static boolean isBip38PrivateKey(String bip38PrivateKey) {
        return parseBip38PrivateKey(bip38PrivateKey) != null;
    }

    public static class Bip38PrivateKey {
        public boolean ecMultiply;
        public boolean compressed;
        public boolean lotSequence;
        public byte[] salt;
        public byte[] data;

        public Bip38PrivateKey(boolean ecMultiply, boolean compressed, boolean lotSequence, byte[] salt, byte[] data) {
            this.ecMultiply = ecMultiply;
            this.compressed = compressed;
            this.lotSequence = lotSequence;
            this.salt = salt;
            this.data = data;
        }
    }

    public static Bip38PrivateKey parseBip38PrivateKey(String bip38PrivateKey) {
        // Decode Base 58
        byte[] decoded = Base58.decode(bip38PrivateKey);
        if (decoded == null) {
            return null;
        }

        //Validate length
        if (!(decoded.length == 39 || decoded.length == 43)) {
            return null;
        }

        int index = 0;

        // Validate BIP 38 prefix
        if (decoded[index++] != (byte) 0x01) {
            return null;
        }
        boolean ecMultiply;
        if (decoded[index] == (byte) 0x42) {
            ecMultiply = false;
        } else if (decoded[index] == (byte) 0x43) {
            ecMultiply = true;
        } else {
            return null;
        }
        index++;

        // Validate flags and determine whether we have a compressed key
        int flags = ((int) decoded[index++]) & 0x00ff;

        boolean lotSequence;

        if (ecMultiply) {
            if ((flags | 0x0024) != 0x24) {
                // Only bit 3 and 6 can be set for EC-multiply keys
                return null;
            }
            lotSequence = (flags & 0x0004) == 0 ? false : true;
        } else {
            if ((flags | 0x00E0) != 0xE0) {
                // Only bit 6 7 and 8 can be set for non-EC-multiply keys
                return null;
            }
            if ((flags & 0x00c0) != 0x00c0) {
                // Upper two bits must be set for non-EC-multiplied key
                return null;
            }
            lotSequence = false;
        }

        boolean compressed = (flags & 0x0020) == 0 ? false : true;

        // Fetch salt
        byte[] salt = new byte[4];
        salt[0] = decoded[index++];
        salt[1] = decoded[index++];
        salt[2] = decoded[index++];
        salt[3] = decoded[index++];

        // Fetch data
        byte[] data = new byte[32];
        System.arraycopy(decoded, index, data, 0, data.length);
        index += data.length;

        return new Bip38PrivateKey(ecMultiply, compressed, lotSequence, salt, data);
    }

    /**
     * 可能为 Null ，Null 代表密码不正确
     */
    public static BitCoinECKeyPair decrypt(String bip38PrivateKeyString, CharSequence passphrase) throws InterruptedException, ValidationException {
        Bip38PrivateKey bip38Key = parseBip38PrivateKey(bip38PrivateKeyString);
        if (bip38Key == null) {
            return null;
        }
        if (bip38Key.ecMultiply) {
            return decryptEcMultiply(bip38Key, passphrase);
        } else {
            byte[] stretcedKeyMaterial = bip38Stretch1(passphrase, bip38Key.salt, SCRYPT_LENGTH);
            return decryptNoEcMultiply(bip38Key, stretcedKeyMaterial);
        }
    }

    public static BitCoinECKeyPair decryptEcMultiply(Bip38PrivateKey bip38Key, CharSequence passphrase
    ) throws InterruptedException, ValidationException {
        // Get 8 byte Owner Salt
        byte[] ownerEntropy = new byte[8];
        System.arraycopy(bip38Key.data, 0, ownerEntropy, 0, 8);

        byte[] ownerSalt = ownerEntropy;
        if (bip38Key.lotSequence) {
            ownerSalt = new byte[4];
            System.arraycopy(ownerEntropy, 0, ownerSalt, 0, 4);
        }

        // Stretch to get Pass Factor
        byte[] passFactor = bip38Stretch1(passphrase, ownerSalt, 32);

        if (bip38Key.lotSequence) {
            byte[] tmp = new byte[40];
            System.arraycopy(passFactor, 0, tmp, 0, 32);
            System.arraycopy(ownerEntropy, 0, tmp, 32, 8);
            //we convert to byte[] here since this can be a sha256 or Scrypt result.
            // might make sense to introduce a 32 byte scrypt type
            passFactor = SHA256.doubleSha256(tmp);
        }
        // Determine Pass Point

        BitCoinECKeyPair bitCoinECKeyPair = new BitCoinECKeyPair(passFactor, false, bip38Key.compressed);
        byte[] passPoint = bitCoinECKeyPair.getRawPublicKey();

        // Get 8 byte encrypted part 1, only first half of encrypted part 1
        // (the rest is encrypted within encryptedpart2)
        byte[] encryptedPart1 = new byte[16];
        System.arraycopy(bip38Key.data, 8, encryptedPart1, 0, 8);
        // Get 16 byte encrypted part 2
        byte[] encryptedPart2 = new byte[16];
        System.arraycopy(bip38Key.data, 16, encryptedPart2, 0, 16);

        // Second stretch to derive decryption key
        byte[] saltPlusOwnerSalt = new byte[12];
        System.arraycopy(bip38Key.salt, 0, saltPlusOwnerSalt, 0, 4);
        System.arraycopy(ownerEntropy, 0, saltPlusOwnerSalt, 4, 8);
//        byte[] derived = SCrypt.generate(passPoint, saltPlusOwnerSalt, 1024, 1, 1, 64);
        byte[] derived = SCrypt.generate(passPoint, saltPlusOwnerSalt, SCRYPT_N, SCRYPT_R, SCRYPT_P, SCRYPT_LENGTH);
        byte[] derivedQuater1 = new byte[16];
        System.arraycopy(derived, 0, derivedQuater1, 0, 16);
        byte[] derivedQuater2 = new byte[16];
        System.arraycopy(derived, 16, derivedQuater2, 0, 16);
        byte[] derivedHalf2 = new byte[32];
        System.arraycopy(derived, 32, derivedHalf2, 0, 32);

        Rijndael aes = new Rijndael();
        aes.makeKey(derivedHalf2, 256);

        byte[] unencryptedPart2 = new byte[16];
        aes.decrypt(encryptedPart2, unencryptedPart2);
        xorBytes(derivedQuater2, unencryptedPart2);

        // Get second half of encrypted half 1
        System.arraycopy(unencryptedPart2, 0, encryptedPart1, 8, 8);

        // Decrypt part 1
        byte[] unencryptedPart1 = new byte[16];
        aes.decrypt(encryptedPart1, unencryptedPart1);
        xorBytes(derivedQuater1, unencryptedPart1);

        // Recover seedB
        byte[] seedB = new byte[24];
        System.arraycopy(unencryptedPart1, 0, seedB, 0, 16);
        System.arraycopy(unencryptedPart2, 8, seedB, 16, 8);

        // Generate factorB
        byte[] factorB = SHA256.doubleSha256(seedB);

        BigInteger privateKey = new BigInteger(1, passFactor).multiply(new BigInteger(1, factorB).mod(n));
        byte[] keyBytes = new byte[32];
        byte[] bytes = privateKey.toByteArray();
        if (bytes.length <= keyBytes.length) {
            System.arraycopy(bytes, 0, keyBytes, keyBytes.length - bytes.length, bytes.length);
        } else {
            assert bytes.length == 33 && bytes[0] == 0;
            System.arraycopy(bytes, 1, keyBytes, 0, bytes.length - 1);
        }

        BitCoinECKeyPair ecKeyPair = verify(keyBytes, bip38Key.salt, false, bip38Key.compressed);
        if (ecKeyPair == null) {
            return verify(keyBytes, bip38Key.salt, true, bip38Key.compressed);
        }
        return ecKeyPair;
    }

    public static BitCoinECKeyPair decryptNoEcMultiply(Bip38PrivateKey bip38Key, byte[] stretcedKeyMaterial) throws ValidationException {
        // Derive Keys
        byte[] derivedHalf1 = new byte[32];
        System.arraycopy(stretcedKeyMaterial, 0, derivedHalf1, 0, 32);
        byte[] derivedHalf2 = new byte[32];
        System.arraycopy(stretcedKeyMaterial, 32, derivedHalf2, 0, 32);

        // Initialize AES key
        Rijndael aes = new Rijndael();
        aes.makeKey(derivedHalf2, 256);

        // Fetch first encrypted half
        byte[] encryptedHalf1 = new byte[16];
        System.arraycopy(bip38Key.data, 0, encryptedHalf1, 0, encryptedHalf1.length);

        // Fetch second encrypted half
        byte[] encryptedHalf2 = new byte[16];
        System.arraycopy(bip38Key.data, 16, encryptedHalf2, 0, encryptedHalf2.length);

        byte[] decryptedHalf1 = new byte[16];
        aes.decrypt(encryptedHalf1, decryptedHalf1);

        byte[] decryptedHalf2 = new byte[16];
        aes.decrypt(encryptedHalf2, decryptedHalf2);

        byte[] complete = new byte[32];
        for (int i = 0; i < 16; i++) {
            complete[i] = (byte) ((((int) decryptedHalf1[i]) & 0xFF) ^ (((int) derivedHalf1[i]) & 0xFF));
            complete[i + 16] = (byte) ((((int) decryptedHalf2[i]) & 0xFF) ^ (((int) derivedHalf1[i + 16]) & 0xFF));
        }

        BitCoinECKeyPair bitCoinECKeyPair = verify(complete, bip38Key.salt, false, bip38Key.compressed);
        if (bitCoinECKeyPair == null) {
            return verify(complete, bip38Key.salt, true, bip38Key.compressed);
        }
        return bitCoinECKeyPair;
    }

    private static BitCoinECKeyPair verify(byte[] complete, byte[] salt, boolean testNet, boolean compress) throws ValidationException {
        BitCoinECKeyPair bitCoinECKeyPair = new BitCoinECKeyPair(complete, testNet, compress);

        byte[] newSalt = calculateScryptSalt(bitCoinECKeyPair.getAddress());
        if (!java.util.Arrays.equals(salt, newSalt)) {
            // The passphrase is either invalid or we are on the wrong network
            return null;
        }
        return bitCoinECKeyPair;
    }


    public static byte[] calculateScryptSalt(String address) {
        byte[] hash = SHA256.doubleSha256(address.getBytes());
        byte[] ret = new byte[4];
        System.arraycopy(hash, 0, ret, 0, 4);
        return ret;
    }

    private static void xorBytes(byte[] toApply, byte[] target) {
        if (toApply.length != target.length) {
            throw new RuntimeException();
        }
        for (int i = 0; i < toApply.length; i++) {
            target[i] = (byte) (target[i] ^ toApply[i]);
        }
    }

}
