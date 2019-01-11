package net.wzero.wewallet.core.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import net.wzero.wewallet.domain.EntityBase;

@Entity
@Table(name="tokens")
public class Token extends EntityBase{
	@Id
	@GeneratedValue
	private Integer id;
	
	@JoinColumn(name="account_id")
	@ManyToOne
	private Account account;
	/**
	 * 是呼 合约还和网络有关系
	 */
	private String env;
	/**
	 * token类型，erc20
	 */
	private String standard;
	
	/**
	 * token合约地址，很重要
	 */
	private String contractAddr;

	private String balance;
	
	private String icon;
	
	private String name;
	private String symbol;
	@Column(columnDefinition = "SMALLINT")
	private Integer decimals;
	private String totalSupply;

	@Column(columnDefinition="tinyint(1) default 0")
	private Boolean isRefreshing;
	
	@Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", updatable = false)
	private Date created;
	@Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP", updatable = false)
	private Date updated;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Account getAccount() {
		return account;
	}
	public void setAccount(Account account) {
		this.account = account;
	}
	public String getStandard() {
		return standard;
	}
	public void setStandard(String standard) {
		this.standard = standard;
	}
	public String getContractAddr() {
		return contractAddr;
	}
	public void setContractAddr(String contractAddr) {
		this.contractAddr = contractAddr;
	}
	public String getBalance() {
		return balance;
	}
	public void setBalance(String balance) {
		this.balance = balance;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public Integer getDecimals() {
		return decimals;
	}
	public void setDecimals(Integer decimals) {
		this.decimals = decimals;
	}
	public String getTotalSupply() {
		return totalSupply;
	}
	public void setTotalSupply(String totalSupply) {
		this.totalSupply = totalSupply;
	}
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	public Date getUpdated() {
		return updated;
	}
	public void setUpdated(Date updated) {
		this.updated = updated;
	}
	public Boolean getIsRefreshing() {
		return isRefreshing;
	}
	public void setIsRefreshing(Boolean isRefreshing) {
		this.isRefreshing = isRefreshing;
	}
	public String getEnv() {
		return env;
	}
	public void setEnv(String env) {
		this.env = env;
	}
	
}
