package com.github.tubus.ui.data.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.github.tubus.ui.data.dto.account.UserAccount;

import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional(transactionManager = "gcsTransactionManager")
public interface UserAccountRepository extends JpaRepository<UserAccount, UUID> {

    @Query(value = "select (count(ua) > 0) from UserAccount ua " +
            "inner join AccountRole ar on ar.account = ua where ar.role.name = 'SUPER_ADMINISTRATOR'")
    boolean superAdminExists();

    Optional<UserAccount> findOneByName(String name);
}