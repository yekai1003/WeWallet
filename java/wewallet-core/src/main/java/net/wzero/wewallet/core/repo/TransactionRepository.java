package net.wzero.wewallet.core.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import net.wzero.wewallet.core.domain.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
	List<Transaction> findByMemberId(Integer memberId);
}
