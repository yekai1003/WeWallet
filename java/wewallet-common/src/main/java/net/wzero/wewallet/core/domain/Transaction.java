package net.wzero.wewallet.core.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import net.wzero.wewallet.domain.EntityBase;

@Entity
@Table(name="transactions")
public class Transaction extends EntityBase {
	@Id
	@GeneratedValue
	private Integer id;
	
	private Integer memberId;
	private String txHash; 
	private String status;
	private String gasLimit;
	private String gasUsed;
	private String gasPrice;
	private String cumulativeGasUsed;
	/**
	 * 交易金额
	 */
	private String value;
	private String fromAddr;
	private String toAddr;
	private String env;
	private long nonce;

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
	public Integer getMemberId() {
		return memberId;
	}
	public void setMemberId(Integer memberId) {
		this.memberId = memberId;
	}
	public String getTxHash() {
		return txHash;
	}
	public void setTxHash(String txHash) {
		this.txHash = txHash;
	}
	public String getGasLimit() {
		return gasLimit;
	}
	public void setGasLimit(String gasLimit) {
		this.gasLimit = gasLimit;
	}
	public String getGasUsed() {
		return gasUsed;
	}
	public void setGasUsed(String gasUsed) {
		this.gasUsed = gasUsed;
	}
	public String getGasPrice() {
		return gasPrice;
	}
	public void setGasPrice(String gasPrice) {
		this.gasPrice = gasPrice;
	}
	public String getCumulativeGasUsed() {
		return cumulativeGasUsed;
	}
	public void setCumulativeGasUsed(String cumulativeGasUsed) {
		this.cumulativeGasUsed = cumulativeGasUsed;
	}
	public String getFromAddr() {
		return fromAddr;
	}
	public void setFromAddr(String fromAddr) {
		this.fromAddr = fromAddr;
	}
	public String getToAddr() {
		return toAddr;
	}
	public void setToAddr(String toAddr) {
		this.toAddr = toAddr;
	}
	public long getNonce() {
		return nonce;
	}
	public void setNonce(long nonce) {
		this.nonce = nonce;
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
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getEnv() {
		return env;
	}
	public void setEnv(String env) {
		this.env = env;
	}
}
