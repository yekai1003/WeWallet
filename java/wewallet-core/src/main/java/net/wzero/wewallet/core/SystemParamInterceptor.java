package net.wzero.wewallet.core;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import net.wzero.wewallet.domain.SysParam;
import net.wzero.wewallet.serv.ThreadLocalService;
import net.wzero.wewallet.utils.AppConstants;

@Component
public class SystemParamInterceptor  extends HandlerInterceptorAdapter  {

	@SuppressWarnings("unused")
	private static final Logger logger = (Logger) LoggerFactory.getLogger(SystemParamInterceptor.class);
	
	@Autowired
	private ThreadLocalService sysParamService;
	
	public boolean preHandle(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response, Object handler) throws Exception 
	{
		//logger.info("[SystemParamInterceptor]  preHandle");
		//client 
		String clientIdStr = request.getParameter(AppConstants.URL_CLIENT_ID_KEY);
		//if(clientIdStr == null) throw new PlatformException("client_is_requested","必须传递clientid参数");
		String token = request.getParameter(AppConstants.URL_TOKEN_KEY);
		//保存到系统级参数
		SysParam sp = new SysParam();
		if(clientIdStr != null)
			sp.setClientId(Integer.parseInt(clientIdStr));
		else
			sp.setClientId(0);
		sp.setToken(token);
		sp.setUserIp(request.getHeader("x-real-ip"));
		if(sp.getUserIp()==null)
			sp.setUserIp(request.getRemoteAddr());
		this.sysParamService.set(sp);
		return true;
	}
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		//卸载 client 
		this.sysParamService.remove();
		super.afterCompletion(request, response, handler, ex);
	}
	
}
