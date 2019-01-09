package net.wzero.wewallet.gateway.serv;

import net.wzero.wewallet.gateway.domain.Member;
import net.wzero.wewallet.gateway.domain.MemberAccount;

public interface MemberAccountService {

	MemberAccount createMemberAccount(Member member, String loginName, String loginPwd);
	
	MemberAccount findByLogin(String username, String password);
	
	MemberAccount findByLoginName(String loginName);
	
	MemberAccount updateMemberAccount(Member member, String loginName, String loginPwd);
	
}
