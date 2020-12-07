package com.github.tubus.configserver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration(value = "DbConfig")
@EnableJpaRepositories(
        entityManagerFactoryRef = "gcsEntityManagerFactory",
        transactionManagerRef = "gcsTransactionManager",
        basePackages = "com.github.tubus.ui.data.repo"
)
@EnableTransactionManagement
public class DbConfig {

    @Bean
    @ConfigurationProperties("spring.datasource.gcs")
    @Qualifier("gcs-db")
    @Primary
    public DataSource gcsDataSource(){
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "gcsEntityManagerFactory")
    @Primary
    @Autowired
    public LocalContainerEntityManagerFactoryBean gcsEntityManagerFactory(
            final EntityManagerFactoryBuilder builder,
            final @Qualifier("gcs-db") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("com.github.tubus.ui.data.dto")
                .persistenceUnit("gcsDb")
                .build();
    }

    @Bean(name = "gcsTransactionManager")
    public PlatformTransactionManager gcsTransactionManager(
            @Qualifier("gcsEntityManagerFactory")
                    EntityManagerFactory gcsEntityManagerFactory) {
        return new JpaTransactionManager(gcsEntityManagerFactory);
    }

    @Bean(name = "gcs")
    public JdbcTemplate gcsJdbcTemplate(@Qualifier("gcs-db") DataSource dsGcs) {
        return new JdbcTemplate(dsGcs);
    }
}