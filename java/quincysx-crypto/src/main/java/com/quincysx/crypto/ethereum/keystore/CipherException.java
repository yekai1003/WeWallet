package com.quincysx.crypto.ethereum.keystore;

public class CipherException extends Exception {
    /**
	 * 
	 */
	private static final long serialVersionUID = -8229131461013773164L;

	public CipherException(String message) {
        super(message);
    }

    public CipherException(Throwable cause) {
        super(cause);
    }

    public CipherException(String message, Throwable cause) {
        super(message, cause);
    }
}
