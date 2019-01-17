package net.wzero.wewallet.core.domain;

import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import net.wzero.wewallet.domain.converter.KeyValueStringConverter;

@Entity
@PrimaryKeyJoinColumn
@Table(name="ethereum_accounts")
public class EthereumAccount extends Account {
	/**
	 * 不同币种可能对余额的表达不一样
	 * 不同环境不同的余额。。。
	 */
	@Column
	@Convert(converter=KeyValueStringConverter.class)
	private Map<String,Balance> balances;
	/**
	 * 以太坊 使用keystore存储
	 */
	@Column(columnDefinition="text")
	private String keystore;

	public Map<String, Balance> getBalances() {
		return balances;
	}
	public void setBalances(Map<String, Balance> balances) {
		this.balances = balances;
	}
	public String getKeystore() {
		return keystore;
	}
	public void setKeystore(String keystore) {
		this.keystore = keystore;
	}
	
}
