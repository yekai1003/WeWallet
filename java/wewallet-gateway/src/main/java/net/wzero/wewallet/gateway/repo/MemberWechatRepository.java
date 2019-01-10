package net.wzero.wewallet.gateway.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import net.wzero.wewallet.gateway.domain.MemberWechat;

public interface MemberWechatRepository extends JpaRepository<MemberWechat, Integer> {
	
	MemberWechat findByOpenId(String openId);
	
	MemberWechat findByMemberIdAndClientId(Integer memberI, Integer clientId);
	
	List<MemberWechat> findByUnionId(String unionId);
	
	List<MemberWechat> findByMemberId(Integer memberid);

}
