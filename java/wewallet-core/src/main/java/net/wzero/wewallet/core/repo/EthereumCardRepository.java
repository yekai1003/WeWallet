package net.wzero.wewallet.core.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import net.wzero.wewallet.core.domain.EthereumCard;

public interface EthereumCardRepository extends JpaRepository<EthereumCard, Integer> {

}
