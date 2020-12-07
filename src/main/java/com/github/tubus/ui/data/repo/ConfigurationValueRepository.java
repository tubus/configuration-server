package com.github.tubus.ui.data.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.github.tubus.ui.data.dto.configuration.ComponentConfiguration;
import com.github.tubus.ui.data.dto.configuration.ConfigurationValue;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional(transactionManager = "gcsTransactionManager")
public interface ConfigurationValueRepository extends JpaRepository<ConfigurationValue, UUID> {

    @Query(value = "select cv from ConfigurationValue cv " +
            "where cv.componentConfiguration.id = :componentConfigurationId " +
            "and cv.environment.id = :environmentId and cv.environmentProfile.id = :profileId")
    Optional<ConfigurationValue> findOne(UUID componentConfigurationId, UUID environmentId, UUID profileId);

    List<ConfigurationValue> findAllByComponentConfigurationIn(List<ComponentConfiguration> componentConfigurations);

    @Query(value = "select cv from ConfigurationValue cv " +
            "where cv.componentConfiguration.component.name = :name " +
            "and cv.environmentProfile.name = :profile")
    List<ConfigurationValue> findAllByComponentNameAndProfile(String name, String profile);

    @Query(value = "select cv from ConfigurationValue cv where cv.componentConfiguration.id = :id")
    List<ConfigurationValue> findAllByComponentConfigurationId(UUID id);
}