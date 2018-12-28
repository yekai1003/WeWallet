package net.wzero.wewallet.gateway.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import net.wzero.wewallet.gateway.domain.Member;

public interface MemberRepository extends JpaRepository<Member, Integer> {
	
	Page<Member> findByNicknameLikeOrPhoneLike(String keywords,String keywords2,Pageable pageable);
	
	List<Member> findByGroupId(Integer gid);
	
	Member findByPhone(String phone);
}
