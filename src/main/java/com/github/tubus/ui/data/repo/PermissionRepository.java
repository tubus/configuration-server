package com.github.tubus.ui.data.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.github.tubus.ui.data.dto.account.Permission;
import java.util.UUID;

@Repository
@Transactional(transactionManager = "gcsTransactionManager")
public interface PermissionRepository extends JpaRepository<Permission, UUID> {
}