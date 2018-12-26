package net.wzero.wewallet.res;

public class OkResponse extends WalletResponse {
	private String code;
	private String msg;
	public OkResponse() {
		code = "success";
		msg = "成功";
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
}
