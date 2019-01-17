package net.wzero.wewallet.core.stream.worker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

import net.wzero.wewallet.WalletException;
import net.wzero.wewallet.core.domain.EthereumAccount;
import net.wzero.wewallet.core.domain.Token;
import net.wzero.wewallet.core.domain.Transaction;
import net.wzero.wewallet.core.serv.EthService;
import net.wzero.wewallet.core.stream.WorkerMessage;
import net.wzero.wewallet.utils.AppConstants;

@Component
public class EthJobsWorker {

	@Autowired
	private EthService ethService;

	@StreamListener(WorkerMessage.GET_TRANSACTION_RECEIPT_INPUT)
	@SendTo(WorkerMessage.GET_TRANSACTION_RECEIPT_CALLBACK_OUTPUT)
	public Transaction refreshTransaction(Transaction transaction) {
		return this.ethService.getTransactionReceipt(transaction);
	}
	@StreamListener(value=WorkerMessage.REFRESH_JOB_INPUT)
	@SendTo(value=WorkerMessage.REFRESH_JOB_CALLBACK_OUTPUT)
	public Object refreshJob(Object param,@Header(name="jobType")int jobType,@Header(name="envs")String envs) {
		if(jobType == AppConstants.JOB_TYPE_REFRESH_ACCOUNT)
			return this.ethService.refreshEthBalance((EthereumAccount)param,envs);
		else if(jobType == AppConstants.JOB_TYPE_REFRESH_TOKEN)
			return this.ethService.refreshTokenBalance((Token)param);
		else
			throw new WalletException("job_type_undefine","工作类型未定义");
	}
}
