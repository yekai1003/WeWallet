package net.wzero.wewallet.core.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import net.wzero.wewallet.core.domain.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Integer>, JpaSpecificationExecutor<Transaction> {
	
	@Query("select t from Transaction t,Account a where t.account.id=a.id and a.memberId = ?1")
	List<Transaction> findByMemberId(Integer memberId);
	
}
