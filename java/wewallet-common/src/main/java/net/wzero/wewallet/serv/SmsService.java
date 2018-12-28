package net.wzero.wewallet.serv;

import java.util.Map;

public interface SmsService {
	void send(String to,String template,Map<String, Object> params);
}
