package net.wzero.wewallet.gateway.serv;

import net.wzero.wewallet.gateway.domain.Member;
import net.wzero.wewallet.gateway.domain.MemberPhone;

public interface MemberPhoneService {

	MemberPhone createMemberPhone(Member member, String phone);
	
	MemberPhone findByNumber(String phone);
	
	MemberPhone findByMemberId(Integer memberId);
	
	MemberPhone updateMemberPhone(Member member, String phone);
}
