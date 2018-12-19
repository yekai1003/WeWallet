package net.wzero.wewallet.domain;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class MemberInfo extends DomainBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1619021501482001505L;
	
	private Integer id;
	private String type;
	private String userName;
	private String phone;
	private Boolean enable;
	private String mark;
	private String lastLoginIp;
	private Date lastLoginTime;
	private Integer defaultCustomerId;
	private Map<String, String> mData;
	private List<Integer> apis;
	private List<Integer> clients;
	private List<Integer> companies;
	private String openId;
	private String unionId;
	private String email;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public Boolean getEnable() {
		return enable;
	}
	public void setEnable(Boolean enable) {
		this.enable = enable;
	}
	public String getMark() {
		return mark;
	}
	public void setMark(String mark) {
		this.mark = mark;
	}
	public String getLastLoginIp() {
		return lastLoginIp;
	}
	public void setLastLoginIp(String lastLoginIp) {
		this.lastLoginIp = lastLoginIp;
	}
	public Date getLastLoginTime() {
		return lastLoginTime;
	}
	public void setLastLoginTime(Date lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}
	public Integer getDefaultCustomerId() {
		return defaultCustomerId;
	}
	public void setDefaultCustomerId(Integer defaultCustomerId) {
		this.defaultCustomerId = defaultCustomerId;
	}
	public Map<String, String> getmData() {
		return mData;
	}
	public void setmData(Map<String, String> mData) {
		this.mData = mData;
	}
	public List<Integer> getApis() {
		return apis;
	}
	public void setApis(List<Integer> apis) {
		this.apis = apis;
	}
	public List<Integer> getClients() {
		return clients;
	}
	public void setClients(List<Integer> clients) {
		this.clients = clients;
	}
	public List<Integer> getCompanies() {
		return companies;
	}
	public void setCompanies(List<Integer> companies) {
		this.companies = companies;
	}
	public String getOpenId() {
		return openId;
	}
	public void setOpenId(String openId) {
		this.openId = openId;
	}
	public String getUnionId() {
		return unionId;
	}
	public void setUnionId(String unionId) {
		this.unionId = unionId;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
}
