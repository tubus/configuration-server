package com.github.tubus.ui.data.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.github.tubus.ui.data.dto.env.Environment;
import com.github.tubus.ui.data.dto.env.EnvironmentProfile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional(transactionManager = "gcsTransactionManager")
public interface EnvironmentProfileRepository extends JpaRepository<EnvironmentProfile, UUID> {

    Optional<EnvironmentProfile> findOneByNameEqualsAndEnvironmentEquals(String name, Environment environment);

    List<EnvironmentProfile> findAllByEnvironment(Environment environment);
}