package net.wzero.wewallet.domain;

import java.util.Date;

public class SessionBase extends DomainBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7694132883817703028L;

	private int clientId;
	private Date expired;

	public Date getExpired() {
		return expired;
	}
	public void setExpired(Date expired) {
		this.expired = expired;
	}
	public int getClientId() {
		return clientId;
	}
	public void setClientId(int clientId) {
		this.clientId = clientId;
	}
}
