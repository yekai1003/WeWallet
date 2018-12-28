package net.wzero.wewallet.gateway.serv;

public interface MemberSessionService {

	void saveMemberSession(Integer memberId, String token);
	
	void deleteMemberSession(Integer memberId);
	
	void updateMemberSession(Integer memberId, String token);
	
	String getMemberSession(Integer memberId);
}
