package net.wzero.wewallet.gateway.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.wzero.wewallet.gateway.domain.Client;

public interface ClientRepository extends JpaRepository<Client, Integer> {
	@Query("from Client c where c.id=:id and c.isPublic=:isPublic")
	Client findByIdAndIsPublic(@Param("id") int id,@Param("isPublic") boolean isPublic);
	List<Client> findByClientTypeId(int clientTypeId);
}
