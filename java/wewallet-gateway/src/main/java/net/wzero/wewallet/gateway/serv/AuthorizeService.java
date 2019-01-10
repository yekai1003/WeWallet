 package net.wzero.wewallet.gateway.serv;

import me.chanjar.weixin.common.error.WxErrorException;
import net.wzero.wewallet.domain.MemberInfo;
import net.wzero.wewallet.domain.SessionData;
import net.wzero.wewallet.gateway.domain.Member;

public interface AuthorizeService {
	
	SessionData login(int userId);
	SessionData login(int userId,boolean isTest);
	SessionData login(String username,String password);
	SessionData phoneLogin(String phone,String verificationCode);
	SessionData weixinLogin(String code) throws NumberFormatException, WxErrorException;
	
	SessionData doLogin(Member member, Integer loginType, Integer clientId, MemberInfo memberInfo);
	SessionData doLogin(Member member, SessionData sessionData, Integer loginType, Integer clientId, MemberInfo memberInfo);
	
	void sendVerificationCode(String phone);
	Boolean checkVerificationCode(String phone,String verificationCode);
	void deleteVerificationCode(String phone);
	void sendVerificationCode(Integer customerId, String phone);
	public MemberInfo createMemberInfo(Member member);
	
}
