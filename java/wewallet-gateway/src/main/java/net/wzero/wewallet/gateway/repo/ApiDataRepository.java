package net.wzero.wewallet.gateway.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import net.wzero.wewallet.gateway.domain.ApiData;

public interface ApiDataRepository extends JpaRepository<ApiData, Integer>{
	ApiData findByUri(String uri);
	
	Page<ApiData> findAll(Pageable pageable);
	
	Page<ApiData> findByUriLike(String key, Pageable pageable);
}
