package net.wzero.wewallet.gateway.res;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class AuthorizeResponse {

	private String token;
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	private Date expired;
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public Date getExpired() {
		return expired;
	}
	public void setExpired(Date expired) {
		this.expired = expired;
	}	
}
