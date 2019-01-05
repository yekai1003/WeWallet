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
public class TransferListener {

	@Autowired
	private TransactionRepository transactionRepository;
	
	@StreamListener(CoreMessage.TRANSFER_JOB_CALLBACK_INPUT)
	public void transferCallbackWorker(Transaction transaction) {
		log.info("transaction hash:\t"+transaction.getTxHash());
		this.transactionRepository.save(transaction);
	}
}
