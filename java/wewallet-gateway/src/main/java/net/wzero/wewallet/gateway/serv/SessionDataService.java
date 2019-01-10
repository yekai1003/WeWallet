package net.wzero.wewallet.gateway.serv;

import net.wzero.wewallet.domain.MemberInfo;
import net.wzero.wewallet.domain.SessionData;
import net.wzero.wewallet.utils.AppConstants.EthEnv;

public interface SessionDataService {

	SessionData save(Integer loginType, Integer clientId, MemberInfo memberInfo);
	
	SessionData save(SessionData sessionData, Integer loginType, Integer clientId, MemberInfo memberInfo);
	
	SessionData update(SessionData sessionData, String envName);
	
	SessionData update(SessionData sessionData, EthEnv env);
}
