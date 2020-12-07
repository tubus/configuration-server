package com.github.tubus.ui.config;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.sql.DataSource;

@Configuration
public class LiquibaseConfig {

    @Bean
    public SpringLiquibase liquibase(@Qualifier(value = "gcs-db") DataSource gcsDataSource) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(gcsDataSource);
        liquibase.setChangeLog("classpath:sql/releases.xml");
        return liquibase;
    }
}