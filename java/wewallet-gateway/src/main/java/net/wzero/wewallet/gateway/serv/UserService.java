package net.wzero.wewallet.gateway.serv;

import java.util.Date;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import net.wzero.wewallet.domain.MemberInfo;
import net.wzero.wewallet.gateway.domain.Client;
import net.wzero.wewallet.gateway.domain.Member;
import net.wzero.wewallet.gateway.domain.MemberPhone;
import net.wzero.wewallet.gateway.domain.UserGroup;

public interface UserService {
	
	Member addMemberAndMemberAccount(String userName, String loginName, String loginPwd, UserGroup userGroup, String phone);
	
	Member createMember(String userName, UserGroup userGroup, String phone, String loginIp, Date loginTime);
	
	Member createNormalMember();
	
	Member updateMemberLoginInfo(Member member);
	
	Member updateMember(Member member);
	
	Member updateMember(Member member, String phone);
	
	Member updateMember(Member member, String userName, Integer groupId, String phone, String email,
			Boolean enable, String loginName, String loginPwd);
	
	Member findByMemberId(Integer memberId);
	
	Member findByPhone(String phone);
	
	Page<Member> findByNicknameLikeOrPhoneLike(String keywords,String keywords2,Pageable pageable);
	
	MemberPhone createPhoneLogin(Integer memberId,String phone);
	
	MemberPhone createPhoneLogin(Member member,String phone);
	
	void access(Client client, MemberInfo member, Map<String, Object> map);
	
}
