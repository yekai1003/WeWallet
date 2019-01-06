package net.wzero.wewallet.core.serv;

import java.math.BigDecimal;

import net.wzero.wewallet.core.domain.Transaction;
import net.wzero.wewallet.utils.AppConstants.EthEnv;

public interface TxService {
	
	/**
	 * 创建交易，并发送任务消息
	 * @param memberId
	 * @param cardId
	 * @param to
	 * @param value 这个应该已经换算成 wei单位
	 * @param pwd 不保存到数据库，直接通过消息发出去（只有交易的时候需要）查询的时候不需要
	 * @return
	 */
	Transaction createTransaction(Integer memberId,Integer cardId,String to,BigDecimal value,EthEnv env,String pwd);
	
	void refreshTransaction(Transaction tx);
}
