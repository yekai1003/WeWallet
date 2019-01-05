package net.wzero.wewallet.core.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

public interface CoreMessage {
	String TRANSFER_JOB_OUTPUT = "transferJobOutput";
	String TRANSFER_JOB_CALLBACK_INPUT = "transferJobCallbackInput";
	String SMS_JOB_OUPUT = "smsJobOutput";

	@Output(CoreMessage.TRANSFER_JOB_OUTPUT)
	MessageChannel transferJob();
	
	@Input(CoreMessage.TRANSFER_JOB_CALLBACK_INPUT)
	SubscribableChannel transferJobCallback();
	
	@Output(CoreMessage.SMS_JOB_OUPUT)
	MessageChannel smsJob();
}
