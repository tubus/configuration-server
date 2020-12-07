package com.github.tubus.ui.data.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.github.tubus.ui.data.dto.component.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional(transactionManager = "gcsTransactionManager")
public interface ComponentRepository extends JpaRepository<Component, UUID> {
    boolean existsByName(String name);

    Optional<Component> findByName(String componentName);

    @Query(value = "select c from Component c " +
            " where lower(c.name) like '%' || lower(:name) || '%' or " +
            " lower(c.description) like '%' || lower(:name) || '%' ")
    List<Component> searchByName(String name);
}