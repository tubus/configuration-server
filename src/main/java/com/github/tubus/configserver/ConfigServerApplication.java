package com.github.tubus.configserver;

import com.vaadin.flow.spring.SpringBootAutoConfiguration;
import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication(exclude = {LiquibaseAutoConfiguration.class, SpringBootAutoConfiguration.class},
        scanBasePackages = "com.github.tubus.configserver")
@EnableConfigServer
public class ConfigServerApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(ConfigServerApplication.class)
                .main(ConfigServerApplication.class)
                .profiles("config-server")
                .bannerMode(Banner.Mode.OFF)
                .run(args).registerShutdownHook();
    }
}