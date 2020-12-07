package com.github.tubus.ui.data.repo;

import com.github.tubus.ui.data.dto.env.Environment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional(transactionManager = "gcsTransactionManager")
public interface EnvironmentRepository extends JpaRepository<Environment, UUID> {

    Optional<Environment> findOneByNameEquals(String name);

    List<Environment> findAllByNameNotIn(List<String> names);

    Optional<Environment> findByName(String name);

    @Query(value = "SELECT e from Environment e " +
            "where lower(e.name) like '%' || lower(:searchPattern) || '%' or " +
            " lower(e.description) like '%' || lower(:searchPattern) || '%'")
    List<Environment> search(String searchPattern);
}