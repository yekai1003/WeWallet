package net.wzero.wewallet.core.serv;

import java.math.BigDecimal;
import java.math.BigInteger;

import net.wzero.wewallet.core.domain.Transaction;
import net.wzero.wewallet.utils.AppConstants.EthEnv;

public interface TxService {
	
	/**
	 * 创建交易，并发送任务消息
	 * @param memberId
	 * @param accountId
	 * @param to
	 * @param value 这个应该已经换算成 wei单位
	 * @param env
	 * @param pwd 不保存到数据库，直接通过消息发出去（只有交易的时候需要）查询的时候不需要
	 * @return
	 */
	Transaction createTransaction(Integer memberId,Integer accountId,String to,BigDecimal value,EthEnv env,String pwd);
	/**
	 * 
	 * @param memberId
	 * @param tokenId
	 * @param to
	 * @param value 这个应该是没有单位的
	 * @param env
	 * @param pwd
	 * @return
	 */
	Transaction createTokenTransaction(Integer memberId,Integer tokenId,String to,BigInteger value,EthEnv env,String pwd);
	/**
	 * 刷新交易
	 * @param tx
	 */
	void refreshTransaction(Transaction tx);
}
