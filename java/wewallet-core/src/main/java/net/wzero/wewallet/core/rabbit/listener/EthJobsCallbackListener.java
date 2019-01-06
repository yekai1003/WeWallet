package net.wzero.wewallet.core.rabbit.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import net.wzero.wewallet.core.domain.Transaction;
import net.wzero.wewallet.core.repo.TransactionRepository;
import net.wzero.wewallet.core.stream.CoreMessage;

@Slf4j
@Component
public class EthJobsCallbackListener {

	@Autowired
	private TransactionRepository transactionRepository;
	
	/**
	 * 刷新交易信息的 回调
	 * @param transaction
	 */
	@StreamListener(CoreMessage.GET_TRANSACTION_RECEIPT_CALLBACK_INPUT)
	public void getTransactionCallbackWorker(Transaction transaction) {
		log.info("get transaction callback hash:\t"+transaction.getTxHash());
		this.transactionRepository.save(transaction);
	}
}
