package net.wzero.wewallet.gateway.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import net.wzero.wewallet.gateway.domain.MemberPhone;

public interface MemberPhoneRepository extends JpaRepository<MemberPhone, Integer> {
	
	MemberPhone findByNumber(String phone);
	MemberPhone findByMemberId(Integer memberId);
}
