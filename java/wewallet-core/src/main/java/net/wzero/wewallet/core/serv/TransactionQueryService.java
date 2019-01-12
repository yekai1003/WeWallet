package net.wzero.wewallet.core.serv;

import org.springframework.data.domain.Page;

import net.wzero.wewallet.core.domain.Transaction;
import net.wzero.wewallet.query.TransactionQuery;

public interface TransactionQueryService {
	
	Page<Transaction> findTransactionCriteria(Integer page, Integer size, TransactionQuery transactionQuery);

}
