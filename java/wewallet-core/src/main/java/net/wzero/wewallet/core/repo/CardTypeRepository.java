package net.wzero.wewallet.core.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import net.wzero.wewallet.core.domain.CardType;

public interface CardTypeRepository extends JpaRepository<CardType, Integer> {

}
