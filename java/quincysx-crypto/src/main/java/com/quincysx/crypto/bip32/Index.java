package com.quincysx.crypto.bip32;

/**
 * @author QuincySx
 * @date 2018/3/5 下午4:29
 */
public final class Index {
    Index() {
    }

    public static int hard(final int index) {
        return index | 0x80000000;
    }

    public static boolean isHardened(final int i) {
        return (i & 0x80000000) != 0;
    }
}
