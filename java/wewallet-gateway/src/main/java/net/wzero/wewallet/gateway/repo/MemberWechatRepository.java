package net.wzero.wewallet.gateway.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import net.wzero.wewallet.gateway.domain.MemberWechat;

public interface MemberWechatRepository extends JpaRepository<MemberWechat, Integer> {
	
//	@Query("select mw from MemberWechat mw,Client c where mw.appId=c.appId and mw.member.id=?1 and c.id=?2")
//	MemberWechat findByMemberIdAndClientId(Integer memberId, Integer clientId);
//	
//	@Query("select mw from MemberWechat mw,Client c where mw.appId=c.appId and mw.member.id=?1 and c.clientType.code=?2")
//	List<MemberWechat> findByMemberIdAndClientTypeCode(Integer memberId, String clientTypeCode);
//	
	MemberWechat findByOpenId(String openId);
	
	List<MemberWechat> findByUnionId(String unionId);
	
	List<MemberWechat> findByMemberId(int memberid);

}
