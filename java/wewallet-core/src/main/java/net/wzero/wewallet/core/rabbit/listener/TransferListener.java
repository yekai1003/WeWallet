package net.wzero.wewallet.core.rabbit.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import net.wzero.wewallet.core.domain.Transaction;
import net.wzero.wewallet.core.repo.TransactionRepository;
import net.wzero.wewallet.core.serv.TxService;
import net.wzero.wewallet.core.stream.CoreMessage;

@Slf4j
@Component
public class TransferListener {

	@Autowired
	private TransactionRepository transactionRepository;
	@Autowired
	private TxService txService;
	
	@StreamListener(CoreMessage.TRANSFER_JOB_CALLBACK_INPUT)
	public void transferCallbackWorker(Transaction transaction) {
		log.info("transaction hash:\t"+transaction.getTxHash());
		this.transactionRepository.save(transaction);
		// 当有 txHash 但是状态 还是 -1的时候 再次发个刷新的消息
		if(transaction.getTxHash() != null && transaction.getStatus().equals("-1"))
			this.txService.refreshTransaction(transaction);
	}
}
