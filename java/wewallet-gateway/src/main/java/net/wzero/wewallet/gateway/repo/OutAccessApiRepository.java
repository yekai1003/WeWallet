package net.wzero.wewallet.gateway.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import net.wzero.wewallet.gateway.domain.OutAccessApi;

public interface OutAccessApiRepository extends JpaRepository<OutAccessApi, Integer> {
	
	OutAccessApi findByUri(String uri);
	
	List<OutAccessApi> findByEnabled(Boolean enabled);
}
