package net.wzero.wewallet.core.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import net.wzero.wewallet.core.domain.EthereumAccount;

public interface EthereumAccountRepository extends JpaRepository<EthereumAccount, Integer> {

}
