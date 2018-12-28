package net.wzero.wewallet.wx;

import me.chanjar.weixin.mp.api.WxMpInMemoryConfigStorage;
import net.wzero.wewallet.gateway.domain.WechatApp;

public class WxMpDbConfigStorage extends WxMpInMemoryConfigStorage {
	public WxMpDbConfigStorage(WechatApp config) {
		
		this.appId = config.getAppid();
		this.secret = config.getAppsecret();
		this.token = config.getToken();
		this.aesKey = config.getEncodingaeskey();
		this.accessToken = config.getAccessToken();
		if(config.getExpiresTime() != null)
			this.expiresTime = config.getExpiresTime();
		else
			this.expiresTime = 0;
		//其他的再说
	}

	@Override
	public String toString() {
		return "WxMpDbConfigProvider [appId=" + this.appId + ", secret=" + this.secret + ", accessToken="
				+ this.accessToken + ", expiresTime=" + this.expiresTime + ", token=" + this.token + ", aesKey="
				+ this.aesKey + "]";
	}
}
