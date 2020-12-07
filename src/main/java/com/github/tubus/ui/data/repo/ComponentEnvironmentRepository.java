package com.github.tubus.ui.data.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.github.tubus.ui.data.dto.component.Component;
import com.github.tubus.ui.data.dto.component.ComponentEnvironment;
import com.github.tubus.ui.data.dto.env.Environment;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional(transactionManager = "gcsTransactionManager")
public interface ComponentEnvironmentRepository extends JpaRepository<ComponentEnvironment,
        ComponentEnvironment.ComponentEnvironmentId> {

    Optional<ComponentEnvironment> findOneByComponentAndEnvironment(Component component, Environment environment);

    List<ComponentEnvironment> findAllByComponent(Component componentId);
}