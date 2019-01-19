package net.wzero.wewallet.core.serv.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import net.wzero.wewallet.core.domain.Transaction;
import net.wzero.wewallet.core.repo.TransactionRepository;
import net.wzero.wewallet.core.serv.TransactionQueryService;
import net.wzero.wewallet.query.TransactionQuery;

@Service
public class TransactionQueryServiceImpl implements TransactionQueryService {
	
	@Resource  
	TransactionRepository transactionRepository;

	@Override
	public Page<Transaction> findTransactionCriteria(Integer page, Integer size, TransactionQuery transactionQuery) {
		Pageable pageable = new PageRequest(page, size, Sort.Direction.DESC, "created");
        Page<Transaction> pages = transactionRepository.findAll(new Specification<Transaction>(){
            @Override
            public Predicate toPredicate(Root<Transaction> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<Predicate>();
                if (transactionQuery.getMemberId() != null){
                	list.add(criteriaBuilder.equal(root.get("account").get("memberId").as(Integer.class), transactionQuery.getMemberId()));
                }
                if (transactionQuery.getAccountId() != null){
                	list.add(criteriaBuilder.equal(root.get("account").get("id").as(Integer.class), transactionQuery.getAccountId()));
                }
                if (transactionQuery.getStauts() != null){
                	list.add(criteriaBuilder.equal(root.get("status").as(String.class), transactionQuery.getStauts()));
                }
                if (transactionQuery.getBegin() != null && transactionQuery.getEnd() == null) {
                	list.add(criteriaBuilder.and(criteriaBuilder.conjunction(), criteriaBuilder.greaterThanOrEqualTo(root.<Date>get("created"), transactionQuery.getBegin())));
                } else if(transactionQuery.getBegin() == null && transactionQuery.getEnd() != null) {
                	list.add(criteriaBuilder.and(criteriaBuilder.conjunction(), criteriaBuilder.lessThanOrEqualTo(root.<Date>get("created"), transactionQuery.getEnd())));
                } else if(transactionQuery.getBegin() != null && transactionQuery.getEnd() != null) {
                	Predicate begin = criteriaBuilder.and(criteriaBuilder.conjunction(), criteriaBuilder.greaterThanOrEqualTo(root.<Date>get("created"), transactionQuery.getBegin()));
                	Predicate finish = criteriaBuilder.and(criteriaBuilder.conjunction(), criteriaBuilder.lessThanOrEqualTo(root.<Date>get("created"), transactionQuery.getEnd()));
            		list.add(criteriaBuilder.or(begin, finish));
                }
                Predicate[] p = new Predicate[list.size()];
                return criteriaBuilder.and(list.toArray(p));
            }
        }, pageable);
        return pages;
	}

}
