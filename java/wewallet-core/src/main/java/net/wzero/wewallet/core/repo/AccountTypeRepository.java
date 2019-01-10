package net.wzero.wewallet.core.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import net.wzero.wewallet.core.domain.AccountType;

public interface AccountTypeRepository extends JpaRepository<AccountType, Integer> {

}
