package com.github.tubus.ui.data.repo;

import com.github.tubus.ui.data.dto.configuration.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.github.tubus.ui.data.dto.component.Component;
import com.github.tubus.ui.data.dto.configuration.ComponentConfiguration;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
@Transactional(transactionManager = "gcsTransactionManager")
public interface ComponentConfigurationRepository extends JpaRepository<ComponentConfiguration, UUID> {

    @Query(value =
            "select cc from ComponentConfiguration cc " +
            "where cc.configuration.id = :configurationId and cc.component.id = :componentId")
    Optional<ComponentConfiguration> findOneByConfigurationIdAndComponentId(UUID configurationId, UUID componentId);

    @Query(value =
            "select cc from ComponentConfiguration cc " +
                    "inner join Configuration c on c = cc.configuration " +
                    "where c.id = :configurationId and cc.component = :component")
    Optional<ComponentConfiguration> findOneByConfigurationIdAndComponent(UUID configurationId, Component component);

    @Query("select cc from ComponentConfiguration cc " +
           "where cc.component = :component and cc.configuration = :configuration")
    Optional<ComponentConfiguration> findOne(Component component, Configuration configuration);

    @Query(value =
            "select cc from ComponentConfiguration cc " +
            "inner join Component c on c = cc.component " +
            "where c.name = :name")
    List<ComponentConfiguration> findAllByName(String name);

    @Query(value =
            "select cc from ComponentConfiguration cc " +
            "where cc.component.id = :componentId and cc.configuration.parentId = :parentId ")
    Set<ComponentConfiguration> findAllChildrenByParentIdAndComponentId(UUID parentId, UUID componentId);

    @Query(value = "select count(cc) from ComponentConfiguration cc " +
            "where cc.configuration.parentId is null and cc.component = :component ")
    int countAllByConfigurationParentIdIsNull(Component component);

    @Query(value = "select count(cc) from ComponentConfiguration cc " +
            "where cc.configuration.parentId = :parentId and cc.component = :component")
    int countAllByConfigurationParentId(UUID parentId, Component component);

    @Query(value = "select cc from ComponentConfiguration cc " +
            "where cc.configuration.parentId is null and cc.component = :component ")
    List<ComponentConfiguration> findAllByConfigurationParentIdIsNull(Component component);

    @Query(value = "select cc from ComponentConfiguration cc " +
            "where cc.configuration.parentId = :parentId and cc.component = :component")
    List<ComponentConfiguration> findAllByParentId(UUID parentId, Component component);

    List<ComponentConfiguration> findAllByComponent(Component component);
}