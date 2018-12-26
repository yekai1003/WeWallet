package net.wzero.wewallet;

public class WalletException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1092450483234761724L;
	private String errCode;
	private String errMsg;
	public WalletException() {
		super();
	}
	public WalletException(String code,String msg) {
		super(msg);
		this.errCode = code;
		this.errMsg = msg;
	}
	public String getErrCode() {
		return errCode;
	}
	public String getErrMsg() {
		return errMsg;
	}
}
