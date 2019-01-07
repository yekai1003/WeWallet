package net.wzero.wewallet.core.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

@Entity
@PrimaryKeyJoinColumn
@Table(name="ethereum_cards")
public class EthereumCard extends Card {
	/**
	 * 不同币种可能对余额的表达不一样
	 */
	private String balance;
	/**
	 * 以太坊 使用keystore存储
	 */
	@Column(columnDefinition="text")
	private String keystore;

	public String getBalance() {
		return balance;
	}
	public void setBalance(String balance) {
		this.balance = balance;
	}
	public String getKeystore() {
		return keystore;
	}
	public void setKeystore(String keystore) {
		this.keystore = keystore;
	}
	
}
