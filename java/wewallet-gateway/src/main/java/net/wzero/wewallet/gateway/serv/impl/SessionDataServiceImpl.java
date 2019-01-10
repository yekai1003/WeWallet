package net.wzero.wewallet.gateway.serv.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.wzero.wewallet.domain.MemberInfo;
import net.wzero.wewallet.domain.SessionData;
import net.wzero.wewallet.gateway.serv.SessionDataService;
import net.wzero.wewallet.serv.SessionService;
import net.wzero.wewallet.utils.AppConstants.EthEnv;

@Service("sessionDataService")
public class SessionDataServiceImpl implements SessionDataService {
	
	@Autowired
	private SessionService sessionService;

	@Override
	public SessionData save(Integer loginType, Integer clientId, MemberInfo memberInfo) {
		return this.save(new SessionData(), loginType, clientId, memberInfo);
	}
	
	@Override
	public SessionData save(SessionData sessionData, Integer loginType, Integer clientId, MemberInfo memberInfo) {
		sessionData.setLoginType(loginType);
		sessionData.setClientId(clientId);
		sessionData.setMember(memberInfo);
		return this.sessionService.save(sessionData);
	}
	
	@Override
	public SessionData update(SessionData sessionData, String envName) {
		EthEnv env = EthEnv.valueOf(envName);
		return this.update(sessionData, env);
	}
	
	@Override
	public SessionData update(SessionData sessionData, EthEnv env) {
		sessionData.getMember().setCurrEnv(env.getName());
		return this.sessionService.save(sessionData);
	}

}
