package com.quincysx.crypto.bip39;

import java.util.Comparator;

/**
 * @author QuincySx
 * @date 2018/5/15 上午11:25
 */
enum CharSequenceComparators implements Comparator<CharSequence> {

    ALPHABETICAL {
        @Override
        public int compare(final CharSequence o1, final CharSequence o2) {
            final int length1 = o1.length();
            final int length2 = o2.length();
            final int length = Math.min(length1, length2);
            for (int i = 0; i < length; i++) {
                final int compare = Character.compare(o1.charAt(i), o2.charAt(i));
                if (compare != 0) return compare;
            }
            return Integer.compare(length1, length2);
        }
    }

}
