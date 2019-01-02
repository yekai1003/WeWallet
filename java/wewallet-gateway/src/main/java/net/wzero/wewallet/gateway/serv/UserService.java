package net.wzero.wewallet.gateway.serv;

import java.util.Map;

import net.wzero.wewallet.domain.MemberInfo;
import net.wzero.wewallet.gateway.domain.Client;
import net.wzero.wewallet.gateway.domain.Member;
import net.wzero.wewallet.gateway.domain.MemberPhone;

public interface UserService {
	
	MemberPhone createPhoneLogin(Integer memberId,String phone);
	
	MemberPhone createPhoneLogin(Member member,String phone);
	
	void access(Client client, MemberInfo member, Map<String, Object> map);
	
}
