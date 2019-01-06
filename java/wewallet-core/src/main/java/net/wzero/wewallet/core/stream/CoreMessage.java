package net.wzero.wewallet.core.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

public interface CoreMessage {
	// transfer
	String TRANSFER_JOB_OUTPUT = "transferJobOutput";
	String TRANSFER_JOB_CALLBACK_INPUT = "transferJobCallbackInput";
	// Receipt
	String GET_TRANSACTION_RECEIPT_OUTPUT  = "getTransactionReceiptOutput";
	String GET_TRANSACTION_RECEIPT_CALLBACK_INPUT = "getTransactionReceiptCallbackInput";
	// sms
	String SMS_JOB_OUPUT = "smsJobOutput";

	@Output(CoreMessage.TRANSFER_JOB_OUTPUT)
	MessageChannel transferJob();
	
	@Input(CoreMessage.TRANSFER_JOB_CALLBACK_INPUT)
	SubscribableChannel transferJobCallback();
	
	@Output(CoreMessage.GET_TRANSACTION_RECEIPT_OUTPUT)
	MessageChannel getTransactionByHash();

	@Input(CoreMessage.GET_TRANSACTION_RECEIPT_CALLBACK_INPUT)
	SubscribableChannel getTransactionByHashCallback();
	
	@Output(CoreMessage.SMS_JOB_OUPUT)
	MessageChannel smsJob();
}
