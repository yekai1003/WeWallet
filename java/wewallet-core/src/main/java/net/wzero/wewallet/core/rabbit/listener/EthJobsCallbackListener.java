package net.wzero.wewallet.core.rabbit.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import net.wzero.wewallet.WalletException;
import net.wzero.wewallet.core.domain.EthereumAccount;
import net.wzero.wewallet.core.domain.Token;
import net.wzero.wewallet.core.domain.Transaction;
import net.wzero.wewallet.core.repo.AccountRepository;
import net.wzero.wewallet.core.repo.TokenRepository;
import net.wzero.wewallet.core.repo.TransactionRepository;
import net.wzero.wewallet.core.stream.CoreMessage;
import net.wzero.wewallet.utils.AppConstants;

@Slf4j
@Component
public class EthJobsCallbackListener {

	@Autowired
	private TransactionRepository transactionRepository;
	@Autowired
	private AccountRepository accountRepository;
	@Autowired
	private TokenRepository tokenRepository;
	
	/**
	 * 刷新交易信息的 回调
	 * @param transaction
	 */
	@StreamListener(CoreMessage.GET_TRANSACTION_RECEIPT_CALLBACK_INPUT)
	public void getTransactionCallbackWorker(Transaction transaction) {
		log.info("get transaction callback hash:\t"+transaction.getTxHash());
		this.transactionRepository.save(transaction);
	}
	@StreamListener(CoreMessage.REFRESH_JOB_CALLBACK_INPUT)
	public void refreshEthAccount(Object param,@Header(name="jobType")int jobType) {
		if(jobType == AppConstants.JOB_TYPE_REFRESH_ACCOUNT)
			this.accountRepository.save((EthereumAccount)param);
		else if(jobType == AppConstants.JOB_TYPE_REFRESH_TOKEN)
			this.tokenRepository.save((Token)param);
		else
			throw new WalletException("job_type_undefine","工作类型未定义");
	}
}
