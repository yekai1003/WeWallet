package net.wzero.wewallet.gateway.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import net.wzero.wewallet.gateway.domain.UserResource;

public interface UserResourceRepository  extends JpaRepository<UserResource, Integer>{
	
}
