package net.wzero.wewallet.core.serv.impl;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

import net.wzero.wewallet.core.domain.Card;
import net.wzero.wewallet.core.domain.Transaction;
import net.wzero.wewallet.core.repo.CardRepository;
import net.wzero.wewallet.core.repo.TransactionRepository;
import net.wzero.wewallet.core.serv.TxService;
import net.wzero.wewallet.core.stream.CoreMessage;
import net.wzero.wewallet.utils.AppConstants.EthEnv;

@Service
public class TxServiceImpl implements TxService {

	@Autowired
	private CardRepository cardRepository;
	@Autowired
	private TransactionRepository transactionRepository;
	
	@Autowired
	private CoreMessage coreMessage;
	
	@Override
	public Transaction createTransaction(Integer memberId, Integer cardId, String to, BigDecimal value,EthEnv env, String pwd) {
		// memberId 校验应该放在 controller 里做好
		// 获取卡片信息
		Card card = this.cardRepository.findOne(cardId);
		
		// 插入数据库
		Transaction tx = new Transaction();
		tx.setMemberId(memberId);
		tx.setEnv(env.getName());
		tx.setStatus("created");
		tx.setFromAddr(card.getAddr());//还不知道 addr的格式 是否以0x 开头
		tx.setToAddr(to);
		tx.setValue(value.toString());//wei 单位
//		tx.setTxHash(txHash); 此时无法获得，等待交易发送完成后返回，因此交易消息 应该带 上 Transaction 的id号
		// 保存
		tx = this.transactionRepository.save(tx);
		// 发送 交易发送 业务 消息 到RabbitMQ
		this.coreMessage.transferJob().send(MessageBuilder.withPayload(tx).setHeader("p1", pwd).build());
		return tx;
	}

}
