package net.wzero.wewallet.gateway.serv.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.wzero.wewallet.gateway.domain.WechatApp;
import net.wzero.wewallet.gateway.repo.WechatAppRepository;
import net.wzero.wewallet.gateway.serv.WechatAppService;

@Service("wechatAppService")
public class WechatAppServiceImpl implements WechatAppService {
	
	@Autowired
	private WechatAppRepository wechatAppRepository;

	@Override
	public WechatApp findById(Integer wechatAppId) {
		return this.wechatAppRepository.findOne(wechatAppId);
	}

}
