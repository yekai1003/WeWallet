package com.quincysx.crypto.bip44;

import com.quincysx.crypto.bip32.Index;

/**
 * @author QuincySx
 * @date 2018/3/5 下午4:28
 */
public class AddressIndex {
    private final Change change;
    private final int addressIndex;
    private final boolean isHard;

    private final String string;

    AddressIndex(final Change change, final int addressIndex) {
        this(change, addressIndex, false);
    }

    AddressIndex(final Change change, final int addressIndex, final boolean head) {
        this.change = change;
        this.addressIndex = addressIndex;
        String s = "%s/%d";
        if (head) {
            s += "'";
        }
        string = String.format(s, change, addressIndex);
        this.isHard = head;
    }

    public int getValue() {
        if (isHard) {
            return Index.hard(addressIndex);
        }
        return addressIndex;
    }

    public Change getParent() {
        return change;
    }

    @Override
    public String toString() {
        return string;
    }
}
