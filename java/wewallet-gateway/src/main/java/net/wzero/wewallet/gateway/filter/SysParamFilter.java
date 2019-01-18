package net.wzero.wewallet.gateway.filter;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

import net.wzero.wewallet.WalletException;
import net.wzero.wewallet.domain.SessionData;
import net.wzero.wewallet.gateway.domain.ApiData;
import net.wzero.wewallet.gateway.domain.Client;
import net.wzero.wewallet.gateway.serv.ClientService;
import net.wzero.wewallet.gateway.serv.OutAccessApiService;
import net.wzero.wewallet.serv.SessionService;
import net.wzero.wewallet.utils.AppConstants;

@Component
public class SysParamFilter extends ZuulFilter {
	
	private static Logger log = LoggerFactory.getLogger(SysParamFilter.class);
	@Autowired
	private SessionService sessionService;
	@Autowired
	private ClientService clientService;
	@Autowired
	private OutAccessApiService outAccessApiService;
	
	@Override
	public Object run() {
		// 过滤方法
		RequestContext ctx = RequestContext.getCurrentContext();
		HttpServletRequest req = ctx.getRequest();
		
		if(this.outAccessApiService.getOutAccessApi(req.getRequestURI()) != null) return null; // 判断是否为外部可访问的api
		
		String clientId = req.getParameter(AppConstants.URL_CLIENT_ID_KEY);
		String token = req.getParameter(AppConstants.URL_TOKEN_KEY);
		log.info("----Client ID:" +clientId);
		if(clientId == null) {
			log.info("client_id it empty!");
//			this.sender.sendSysLog(req.getHeader("x-real-ip"), req.getRequestURI(), req.getParameterMap(), req.getHeaderNames(), req, "客户端ID为空", "sysParam");
			throw new WalletException("client_id_is_empty","客户端ID为空!");
		}
		
		//获取当前api信息
		ApiData ad = (ApiData)ctx.get(AppConstants.CURRENT_API_DATA_KEY);
		if(ad.getNeedAuthorization()) { //需要API 授权 则token必须
			//检查token
			if(token==null) {
				throw new WalletException("token_is_empty","此API需要授权访问");
			}
			//获取data
			SessionData sd = this.sessionService.find(token);
			if(sd == null) {
				throw new WalletException("token_is_invalid","token错误或者已失效，请从新获取");
			}
			if(!ad.getIsPublic()) {
				if(sd.getMember().getGroupId() != 1) { // 不是超级管理员才需要检查api权限
					if(!this.hasApi(ad, sd.getMember().getApis())) {
						throw new WalletException("permission_denied","没有访问此api的权限");
					}
				}
			}
			//检查客户端权限
			Client client = this.clientService.getClient(Integer.parseInt(clientId));
			if(client == null) throw new WalletException("system_error", clientId + "客户端不存在");
			if(!client.getIsPublic()) {
				if(sd.getMember().getGroupId() != 1) { // 不是超级管理员才需要检查client权限
					if(sd.getMember().getClients()==null) {
						throw new WalletException("permission_denied","没有访问此客户端的权限");
					}
					if(!this.hasClient(client, sd.getMember().getClients())) {
						throw new WalletException("permission_denied","没有访问此客户端的权限");
					}
				}
			}
		}
		return null;
	}
	private boolean hasClient(Client currClient,List<Integer> clients) {
		log.info("---client list:"+clients);
		if(clients == null) return false;
		for(Integer id:clients)
			if(id.equals(currClient.getId()))return true;
		return false;
	}
	private boolean hasApi(ApiData currApi,List<Integer> apis) {
		if(apis == null) return false;
		for(Integer api : apis) {
			if(api.equals(currApi.getId()))
				return true;
		}
		return false;
	}
	@Override
	public boolean shouldFilter() {
		// 是否执行的开关
		return true;
	}

	@Override
	public int filterOrder() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public String filterType() {
		// TODO Auto-generated method stub
		return FilterConstants.PRE_TYPE;
	}

}
