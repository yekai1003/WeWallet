package com.quincysx.crypto.bip39;

import static com.quincysx.crypto.bip39.Normalization.normalizeNFKD;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

/**
 * @author QuincySx
 * @date 2018/5/15 上午10:38
 */
public final class SeedCalculator {
    private final byte[] fixedSalt = getUtf8Bytes("mnemonic");
    private final PBKDF2WithHmacSHA512 hashAlgorithm;

    public SeedCalculator(final PBKDF2WithHmacSHA512 hashAlgorithm) {
        this.hashAlgorithm = hashAlgorithm;
    }

    /**
     * Creates a seed calculator using {@link SpongyCastlePBKDF2WithHmacSHA512} which is the most compatible.
     * Use {@link SeedCalculator#SeedCalculator(PBKDF2WithHmacSHA512)} to supply another.
     */
    public SeedCalculator() {
        this(SpongyCastlePBKDF2WithHmacSHA512.INSTANCE);
    }

    public byte[] calculateSeed(final List<String> mnemonicList, final String passphrase) {
        if (mnemonicList == null || mnemonicList.size() < 3) {
            throw new RuntimeException("The dictionary cannot be empty and the number of words must not be less than 3");
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (String str : mnemonicList) {
            stringBuilder.append(str).append(" ");
        }
        String mnemonic = stringBuilder.substring(0, stringBuilder.length() - 1).toString();
        return calculateSeed(mnemonic, passphrase);
    }

    /**
     * Calculate the seed given a mnemonic and corresponding passphrase.
     * The phrase is not checked for validity here, for that use a {@link MnemonicValidator}.
     * <p>
     * Due to normalization, these need to be {@link String}, and not {@link CharSequence}, this is an open issue:
     * https://github.com/NovaCrypto/BIP39/issues/7
     * <p>
     * If you have a list of words selected from a word list, you can use {@link #withWordsFromWordList} then
     * {@link SeedCalculatorByWordListLookUp#calculateSeed}
     *
     * @param mnemonic   The memorable list of words
     * @param passphrase An optional passphrase, use "" if not required
     * @return a seed for HD wallet generation
     */
    public byte[] calculateSeed(final String mnemonic, final String passphrase) {
        final char[] chars = normalizeNFKD(mnemonic).toCharArray();
        try {
            return calculateSeed(chars, passphrase);
        } finally {
            Arrays.fill(chars, '\0');
        }
    }

    byte[] calculateSeed(final char[] mnemonicChars, final String passphrase) {
        final String normalizedPassphrase = normalizeNFKD(passphrase);
        final byte[] salt2 = getUtf8Bytes(normalizedPassphrase);
        final byte[] salt = combine(fixedSalt, salt2);
        clear(salt2);
        final byte[] encoded = hash(mnemonicChars, salt);
        clear(salt);
        return encoded;
    }

    public SeedCalculatorByWordListLookUp withWordsFromWordList(final WordList wordList) {
        return new SeedCalculatorByWordListLookUp(this, wordList);
    }

    private static byte[] combine(final byte[] array1, final byte[] array2) {
        final byte[] bytes = new byte[array1.length + array2.length];
        System.arraycopy(array1, 0, bytes, 0, array1.length);
        System.arraycopy(array2, 0, bytes, array1.length, bytes.length - array1.length);
        return bytes;
    }

    private static void clear(final byte[] salt) {
        Arrays.fill(salt, (byte) 0);
    }

    private byte[] hash(final char[] chars, final byte[] salt) {
        return hashAlgorithm.hash(chars, salt);
    }

    private static byte[] getUtf8Bytes(final String string) {
        try {
            return string.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
