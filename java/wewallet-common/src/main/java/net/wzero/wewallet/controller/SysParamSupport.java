package net.wzero.wewallet.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;
import net.wzero.wewallet.WalletException;
import net.wzero.wewallet.domain.MemberInfo;
import net.wzero.wewallet.domain.SessionData;
import net.wzero.wewallet.domain.SysParam;
import net.wzero.wewallet.gateway.domain.Client;
import net.wzero.wewallet.gateway.domain.Member;
import net.wzero.wewallet.serv.SessionService;
import net.wzero.wewallet.serv.ThreadLocalService;
import net.wzero.wewallet.utils.AppConstants;

@Slf4j
public class SysParamSupport {
	
	@Autowired
	private ThreadLocalService sysParamService;
	@Autowired
	protected SessionService sessionService;
	@Autowired
	private RestTemplate restTemplate;
	
	private Map<Integer,Client> clients = new HashMap<>();
	
	protected Client getClient() {
		int clientId = ((SysParam)this.sysParamService.get()).getClientId();
		Client clnt = null;
		if(this.clients.containsKey(clientId))
			clnt = this.clients.get(clientId);
		else {
			clnt = this.restTemplate.getForObject("http://wewallet-gateway/client/get?id="+clientId, Client.class);
			if(clnt != null)
				this.clients.put(clientId, clnt);
		}
		if(clnt == null) throw new WalletException("client_not_exist","客户端不存在");
		return clnt;
	}
	protected SysParam getSystemParam() {
		return (SysParam)this.sysParamService.get();
	}
	protected SessionData getSessionData() {
		String token = ((SysParam)this.sysParamService.get()).getToken();
		if(token == null) throw new WalletException("token_is_null","token不能为空");
		SessionData sd = this.sessionService.find(token);
		log.info("token->\t"+token);
		log.info("session->\t"+sd);
		if(sd == null)
			throw new WalletException("token_is_invalid","token错误或者已失效，请从新获取");
		return sd;
	}
	protected Member getMember(Integer memberId) {
		return this.restTemplate.getForObject("http://wewallet-gateway/user/getMember?mid="+memberId+
				"&token="+((SysParam)this.sysParamService.get()).getToken()+
				"&clientid="+((SysParam)this.sysParamService.get()).getClientId(), Member.class);
	}
	protected MemberInfo getMember() {
		return this.getSessionData().getMember();
	}
	protected Client getClient(Integer clientid) {
		return this.restTemplate.getForObject("http://wewallet-gateway/client/get?id="+clientid+
				"&token="+((SysParam)this.sysParamService.get()).getToken()+
				"&clientid="+((SysParam)this.sysParamService.get()).getClientId(), Client.class);
	}
	
	protected Integer getWxAppId(Integer clientId) {
		Client client = this.getClient(clientId);
		if(client == null || client.getId() == null) throw new WalletException("parap_error", clientId+"号clientId不存在");
		return getWxAppId(client);
	}
	
	protected Integer getWxAppId(Client client) {
		if(client.getClientData() == null) throw new WalletException("weixin_config_empty", "客户端没有配置微信");
		String wxAppIdStr = client.getClientData().get(AppConstants.WX_APP_ID_KEY); // 获取 wxAppId
		if(wxAppIdStr == null) throw new WalletException("weixin_login_not_allowed", "客户端不允许微信登录或者未配置微信登录");
		return Integer.parseInt(wxAppIdStr);
	}
}
