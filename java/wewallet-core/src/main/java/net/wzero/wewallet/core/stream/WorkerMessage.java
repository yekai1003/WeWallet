package net.wzero.wewallet.core.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

/**
 * 在Spring cloud stream binder 注册一个订阅通道
 * 
 * @author yjjie
 *
 */
public interface WorkerMessage {
	// transfer
	String TRANSFER_JOB_INPUT = "transferJobInput";
	String TRANSFER_JOB_CALLBACK_OUTPUT = "transferJobCallbackOutput";
	// Receipt
	String GET_TRANSACTION_RECEIPT_INPUT  = "getTransactionReceiptInput";
	String GET_TRANSACTION_RECEIPT_CALLBACK_OUTPUT = "getTransactionReceiptCallbackOutput";
	// Refresh
	String REFRESH_JOB_INPUT = "refreshJobInput";
	String REFRESH_JOB_CALLBACK_OUTPUT = "refreshJobCallbackOutput";
	// sms
	String SMS_JOB_INPUT = "smsJobInput";

	@Input(WorkerMessage.TRANSFER_JOB_INPUT)
	SubscribableChannel transferJob();
	
	@Output(WorkerMessage.TRANSFER_JOB_CALLBACK_OUTPUT)
	MessageChannel transferJobCallback();

	@Input(WorkerMessage.GET_TRANSACTION_RECEIPT_INPUT)
	SubscribableChannel getTransactionByHash();
	
	@Output(WorkerMessage.GET_TRANSACTION_RECEIPT_CALLBACK_OUTPUT)
	MessageChannel getTransactionByHashCallback();

	@Input(WorkerMessage.REFRESH_JOB_INPUT)
	SubscribableChannel refreshJob();
	
	@Output(WorkerMessage.REFRESH_JOB_CALLBACK_OUTPUT)
	MessageChannel refreshJobCallback();
	
	@Input(WorkerMessage.SMS_JOB_INPUT)
	SubscribableChannel smsJob();
}
