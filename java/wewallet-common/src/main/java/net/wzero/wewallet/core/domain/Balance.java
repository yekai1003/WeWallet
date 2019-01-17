package net.wzero.wewallet.core.domain;

public class Balance {
	private String val;
	private Boolean isRefreshing;
	
	public Balance() {
		super();
	}
	public Balance(String val, Boolean isRefreshing) {
		super();
		this.val = val;
		this.isRefreshing = isRefreshing;
	}
	public String getVal() {
		return val;
	}
	public void setVal(String val) {
		this.val = val;
	}
	public Boolean getIsRefreshing() {
		return isRefreshing;
	}
	public void setIsRefreshing(Boolean isRefreshing) {
		this.isRefreshing = isRefreshing;
	}
}
