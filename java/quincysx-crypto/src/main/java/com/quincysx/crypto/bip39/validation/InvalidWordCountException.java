package com.quincysx.crypto.bip39.validation;

/**
 * @author QuincySx
 * @date 2018/5/15 上午11:20
 */
public class InvalidWordCountException extends Exception {
    public InvalidWordCountException() {
        super("Not a correct number of words");
    }
}
