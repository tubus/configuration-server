package com.github.tubus.ui.data.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.github.tubus.ui.data.dto.account.AccountRole;
import com.github.tubus.ui.data.dto.account.UserAccount;
import java.util.List;
import java.util.UUID;

@Repository
@Transactional(transactionManager = "gcsTransactionManager")
public interface AccountRoleRepository extends JpaRepository<AccountRole, UUID> {

    List<AccountRole> findAllByAccount(UserAccount account);
}