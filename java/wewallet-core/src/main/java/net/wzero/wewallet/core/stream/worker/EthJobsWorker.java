package net.wzero.wewallet.core.stream.worker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

import net.wzero.wewallet.core.domain.Transaction;
import net.wzero.wewallet.core.serv.EthService;
import net.wzero.wewallet.core.stream.WorkerMessage;

@Component
public class EthJobsWorker {

	@Autowired
	private EthService ethService;

	@StreamListener(WorkerMessage.GET_TRANSACTION_RECEIPT_INPUT)
	@SendTo(WorkerMessage.GET_TRANSACTION_RECEIPT_CALLBACK_OUTPUT)
	public Transaction refreshTransaction(Transaction transaction) {
		return this.ethService.getTransactionReceipt(transaction);
	}
}
