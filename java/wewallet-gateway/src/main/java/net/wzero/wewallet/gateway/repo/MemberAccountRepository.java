package net.wzero.wewallet.gateway.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.wzero.wewallet.gateway.domain.MemberAccount;

public interface MemberAccountRepository extends JpaRepository<MemberAccount, Integer> {

	@Query("from MemberAccount ma where ma.loginName=:username and ma.loginPwd=:password")
	MemberAccount findByLogin(@Param("username") String username,@Param("password") String password);
	
	MemberAccount findByLoginName(String loginname);
	
	MemberAccount findByMemberId(Integer memberId);
}
