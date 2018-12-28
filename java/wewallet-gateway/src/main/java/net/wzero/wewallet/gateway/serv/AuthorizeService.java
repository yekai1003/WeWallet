 package net.wzero.wewallet.gateway.serv;

import me.chanjar.weixin.common.error.WxErrorException;
import net.wzero.wewallet.domain.MemberInfo;
import net.wzero.wewallet.domain.SessionData;
import net.wzero.wewallet.gateway.domain.Member;

public interface AuthorizeService {
	SessionData login(int userId);
	SessionData login(int userId,boolean isTest);
	SessionData login(String username,String password);
	SessionData weixinLogin(String code) throws NumberFormatException, WxErrorException;
	SessionData phoneLogin(String phone,String verificationCode);
	void sendVerificationCode(String phone);
	void sendVerificationCode(Integer customerId, String phone);
	public MemberInfo createMemberInfo(Member member);
}
