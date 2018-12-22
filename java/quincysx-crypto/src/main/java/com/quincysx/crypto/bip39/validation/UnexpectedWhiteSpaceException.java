package com.quincysx.crypto.bip39.validation;

/**
 * @author QuincySx
 * @date 2018/5/15 上午11:20
 */
public class UnexpectedWhiteSpaceException extends Exception {
    public UnexpectedWhiteSpaceException() {
        super("Unexpected whitespace");
    }
}
