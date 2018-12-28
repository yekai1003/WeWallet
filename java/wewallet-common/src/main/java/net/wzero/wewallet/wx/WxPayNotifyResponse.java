package net.wzero.wewallet.wx;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName="xml")
public class WxPayNotifyResponse {

	public WxPayNotifyResponse(String code,String msg) {
		
		this.returnCode = code;
		this.returnMsg = msg;
	}
	@JacksonXmlProperty(localName="return_code")
	private String returnCode;
	@JacksonXmlProperty(localName="return_msg")
	private String returnMsg;
	public String getReturnCode() {
		return returnCode;
	}
	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}
	public String getReturnMsg() {
		return returnMsg;
	}
	public void setReturnMsg(String returnMsg) {
		this.returnMsg = returnMsg;
	}
	
}
