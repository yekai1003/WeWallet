package com.quincysx.crypto.bip39;

import com.quincysx.crypto.utils.SHA256;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author QuincySx
 * @date 2018/5/15 上午10:21
 */
public final class MnemonicGenerator {
    private final WordList wordList;

    /**
     * Create a generator using the given word list.
     *
     * @param wordList A known ordered list of 2048 words to select from.
     */
    public MnemonicGenerator(final WordList wordList) {
        this.wordList = wordList;
    }

    public List<String> createMnemonic(
            final CharSequence entropyHex) {
        final int length = entropyHex.length();
        if (length % 2 == 1)
            throw new RuntimeException("Length of hex chars must be divisible by 2");
        final byte[] entropy = new byte[length / 2];
        try {
            for (int i = 0, j = 0; i < length; i += 2, j++) {
                entropy[j] = (byte) (parseHex(entropyHex.charAt(i)) << 4 | parseHex(entropyHex.charAt(i + 1)));
            }
            return createMnemonic(entropy);
        } finally {
            Arrays.fill(entropy, (byte) 0);
        }
    }

    public List<String> createMnemonic(
            final byte[] entropy) {
        final int[] wordIndexes = wordIndexes(entropy);
        try {
            return createMnemonic(wordIndexes);
        } finally {
            Arrays.fill(wordIndexes, 0);
        }
    }

    private List<String> createMnemonic(
            final int[] wordIndexes) {
        List<String> mnemonicList = new ArrayList<>();
        final String space = String.valueOf(wordList.getSpace());
        for (int i = 0; i < wordIndexes.length; i++) {
            mnemonicList.add(wordList.getWord(wordIndexes[i]));
        }
        return mnemonicList;
    }

    private static int[] wordIndexes(byte[] entropy) {
        final int ent = entropy.length * 8;
        entropyLengthPreChecks(ent);

        final byte[] entropyWithChecksum = Arrays.copyOf(entropy, entropy.length + 1);
        entropyWithChecksum[entropy.length] = firstByteOfSha256(entropy);

        //checksum length
        final int cs = ent / 32;
        //mnemonic length
        final int ms = (ent + cs) / 11;

        //get the indexes into the word list
        final int[] wordIndexes = new int[ms];
        for (int i = 0, wi = 0; wi < ms; i += 11, wi++) {
            wordIndexes[wi] = ByteUtils.next11Bits(entropyWithChecksum, i);
        }
        return wordIndexes;
    }

    static byte firstByteOfSha256(final byte[] entropy) {
        final byte[] hash = SHA256.sha256(entropy);
        final byte firstByte = hash[0];
        Arrays.fill(hash, (byte) 0);
        return firstByte;
    }

    private static void entropyLengthPreChecks(final int ent) {
        if (ent < 128)
            throw new RuntimeException("Entropy too low, 128-256 bits allowed");
        if (ent > 256)
            throw new RuntimeException("Entropy too high, 128-256 bits allowed");
        if (ent % 32 > 0)
            throw new RuntimeException("Number of entropy bits must be divisible by 32");
    }

    private static int parseHex(char c) {
        if (c >= '0' && c <= '9') return c - '0';
        if (c >= 'a' && c <= 'f') return (c - 'a') + 10;
        if (c >= 'A' && c <= 'F') return (c - 'A') + 10;
        throw new RuntimeException("Invalid hex char '" + c + '\'');
    }
}
