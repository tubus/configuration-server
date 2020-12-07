package com.github.tubus.ui.data.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.github.tubus.ui.data.dto.configuration.Configuration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional(transactionManager = "gcsTransactionManager")
public interface ConfigurationRepository extends JpaRepository<Configuration, UUID> {

    boolean existsByPath(String name);

    Optional<Configuration> findByPath(String name);

    @Query(nativeQuery = true, value =  "select count(c.*) from configuration c " +
            "left join component_configuration cc on c.id = cc.configuration_id and cc.component_id = :componentId " +
            "where lower(c.path) like lower(:pathFilter || '%') " +             // Only children
                "and lower(c.path) not like lower(:pathFilter || '_%.%') " +    // Only own children
                "and cc.id is null " +                                          // Only not already exists
                "and c.\"group\" = :group " +                                   // Only group
                "and (c.parent_id is null) = :root")                            // Only root
    int countSearch(UUID componentId, String pathFilter, boolean group, boolean root);

    @Query(nativeQuery = true, value =  "select c.* from configuration c " +
            "left join component_configuration cc on c.id = cc.configuration_id and cc.component_id = :componentId " +
            "where lower(c.path) like lower(:pathFilter || '%') " +             // Only children
            "and lower(c.path) not like lower(:pathFilter || '_%.%') " +        // Only own children
            "and cc.id is null " +                                              // Only not already exists
            "and c.\"group\" = :group " +                                       // Only group
            "and (c.parent_id is null) = :root " +                              // Only root
            "limit :limit offset :offset ")                                     // Paging
    List<Configuration> search(UUID componentId, String pathFilter, boolean group, boolean root, int limit, int offset);
}