package net.wzero.wewallet.gateway.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import net.wzero.wewallet.gateway.domain.Member;

public interface MemberRepository extends JpaRepository<Member, Integer> {

}
