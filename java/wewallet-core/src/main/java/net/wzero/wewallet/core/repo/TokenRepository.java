package net.wzero.wewallet.core.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import net.wzero.wewallet.core.domain.Token;

public interface TokenRepository extends JpaRepository<Token, Integer> {
	Token findByAccountIdAndContractAddrAndEnv(Integer accountId,String contractAddress,String env);
	List<Token> findByAccountIdAndEnv(Integer accountId,String env);
}
