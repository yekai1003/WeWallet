package net.wzero.wewallet.res;


public class ErrorResponse extends WalletResponse {

	private String errCode;
	private String errMsg;
	
	public ErrorResponse() {};
	public ErrorResponse(String code,String msg) {
		this.errCode=code;
		this.errMsg = msg;
	}
	
	public String getErrCode() {
		return errCode;
	}
	public void setErrCode(String errCode) {
		this.errCode = errCode;
	}
	public String getErrMsg() {
		return errMsg;
	}
	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}
}
