package net.wzero.wewallet.gateway.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import net.wzero.wewallet.gateway.domain.UserGroup;

public interface UserGroupRepository extends JpaRepository<UserGroup, Integer> {

}
