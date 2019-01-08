package net.wzero.wewallet.core.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import net.wzero.wewallet.core.domain.Token;

public interface TokenRepository extends JpaRepository<Token, Integer> {
	Token findByCardIdAndContractAddr(Integer cardId,String contractAddress);
}
