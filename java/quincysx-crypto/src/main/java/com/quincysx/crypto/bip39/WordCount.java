package com.quincysx.crypto.bip39;

/**
 * @author QuincySx
 * @date 2018/3/2 下午4:27
 * 词语个数的枚举
 */
public enum WordCount {
    /**
     * 3个助记词
     */
//    THREE(27),
    /**
     * 6个助记词
     */
//    SIX(54),
    /**
     * 9个助记词
     */
//    NINE(72),
    /**
     * 12个助记词
     */
    TWELVE(128),
    /**
     * 15个助记词
     */
    FIFTEEN(160),
    /**
     * 18个助记词
     */
    EIGHTEEN(192),
    /**
     * 21个助记词
     */
    TWENTY_ONE(224),
    /**
     * 24个助记词
     */
    TWENTY_FOUR(256);

    private final int bitLength;

    WordCount(int bitLength) {
        this.bitLength = bitLength;
    }

    public int bitLength() {
        return bitLength;
    }

    public int byteLength() {
        return bitLength / 8;
    }
}
