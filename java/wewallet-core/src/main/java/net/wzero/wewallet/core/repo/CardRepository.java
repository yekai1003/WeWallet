package net.wzero.wewallet.core.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import net.wzero.wewallet.core.domain.Card;

public interface CardRepository extends JpaRepository<Card, Integer> {
	List<Card> findByMemberId(Integer memberId);
}
