package net.wzero.wewallet.domain;

public class VerificationCode extends SessionBase {

	/**
	 * 用于验证码 的缓存结构体
	 */
	private static final long serialVersionUID = 8524057904714099505L;
	private String phone;
	private String Code;
	private String comCode;
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getCode() {
		return Code;
	}
	public void setCode(String code) {
		Code = code;
	}
	public String getComCode() {
		return comCode;
	}
	public void setComCode(String comCode) {
		this.comCode = comCode;
	}
}
