package com.quincysx.crypto.bip39.validation;

/**
 * @author QuincySx
 * @date 2018/5/15 上午11:19
 */
public class InvalidChecksumException extends Exception {
    public InvalidChecksumException() {
        super("Invalid checksum");
    }
}
