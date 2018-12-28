package net.wzero.wewallet.gateway.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import net.wzero.wewallet.gateway.domain.ClientType;

public interface ClientTypeRepository extends JpaRepository<ClientType, Integer> {

}
