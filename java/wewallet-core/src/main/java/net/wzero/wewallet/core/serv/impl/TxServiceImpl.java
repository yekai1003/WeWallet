package net.wzero.wewallet.core.serv.impl;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

import net.wzero.wewallet.WalletException;
import net.wzero.wewallet.core.domain.Account;
import net.wzero.wewallet.core.domain.Token;
import net.wzero.wewallet.core.domain.Transaction;
import net.wzero.wewallet.core.repo.AccountRepository;
import net.wzero.wewallet.core.repo.TokenRepository;
import net.wzero.wewallet.core.repo.TransactionRepository;
import net.wzero.wewallet.core.serv.TxService;
import net.wzero.wewallet.core.stream.CoreMessage;
import net.wzero.wewallet.utils.AppConstants.EthEnv;

@Service
public class TxServiceImpl implements TxService {

	@Autowired
	private AccountRepository accountRepository;
	@Autowired
	private TokenRepository tokenRepository;
	@Autowired
	private TransactionRepository transactionRepository;
	
	@Autowired
	private CoreMessage coreMessage;
	
	@Override
	public Transaction createTransaction(Integer memberId, Integer accountId, String to, BigDecimal value,EthEnv env, String pwd) {
		// memberId 校验应该放在 controller 里做好
		// 获取账户信息
		Account account = this.accountRepository.findOne(accountId);
		// 判断 memberId 是否可以 account匹配
		if(!account.getMemberId().equals(memberId)) throw new WalletException("session_error","本用户没有此账户！");
		// 插入数据库
		Transaction tx = new Transaction();
		tx.setAccount(account);
		tx.setEnv(env.getName());
		tx.setStatus("-1");
		tx.setFromAddr(account.getAddr());//还不知道 addr的格式 是否以0x 开头
		tx.setToAddr(to);
		tx.setValue(value.toBigInteger().toString());//wei 单位 所以去掉后面小数位，一般是 .0
//		tx.setTxHash(txHash); 此时无法获得，等待交易发送完成后返回，因此交易消息 应该带 上 Transaction 的id号
		// 保存
		tx = this.transactionRepository.save(tx);
		// 发送 交易发送 业务 消息 到RabbitMQ
		this.coreMessage.transferJob().send(MessageBuilder.withPayload(tx).setHeader("p1", pwd).build());
		return tx;
	}

	@Override
	public Transaction createTokenTransaction(Integer memberId, Integer tokenId, String to, BigInteger value,
			EthEnv env, String pwd) {
		//获取token信息
		Token token = this.tokenRepository.findOne(tokenId);
		// 判断 memberId 是否可以 account匹配
		if(!token.getAccount().getMemberId().equals(memberId)) throw new WalletException("session_error","本用户没有此账户！");
		// 插入数据库
		Transaction tx = new Transaction();
		tx.setContractAddr(token.getContractAddr());
		tx.setAccount(token.getAccount());
		tx.setEnv(env.getName());
		tx.setStatus("-1");
		tx.setFromAddr(token.getAccount().getAddr());
		tx.setToAddr(to);
		tx.setValue(value.toString());
		tx = this.transactionRepository.save(tx);
		// 发送 transafer 业务 消息 到RabbitMQ
		this.coreMessage.transferJob().send(MessageBuilder.withPayload(tx).setHeader("p1", pwd).build());
		return tx;
	}

	@Override
	public void refreshTransaction(Transaction tx) {
		if(tx.getTxHash() == null)
			throw new WalletException("tx_hash_empty","交易hash 值为空，一般交易还没广播成功");
		// 查询交易不需要授权
		this.coreMessage.getTransactionByHash().send(MessageBuilder.withPayload(tx).build());
	}

}
