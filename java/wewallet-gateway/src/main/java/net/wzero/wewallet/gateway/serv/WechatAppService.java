package net.wzero.wewallet.gateway.serv;

import net.wzero.wewallet.gateway.domain.WechatApp;

public interface WechatAppService {

	WechatApp findById(Integer wechatAppId);
}
