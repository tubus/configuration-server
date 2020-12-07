package com.github.tubus.configserver;

import com.github.tubus.ui.data.dto.configuration.ConfigurationValue;
import com.github.tubus.ui.data.repo.ConfigurationValueRepository;
import org.springframework.cloud.config.environment.Environment;
import org.springframework.cloud.config.environment.PropertySource;
import org.springframework.cloud.config.server.environment.EnvironmentRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PostgresEnvironmentRepository implements EnvironmentRepository {

    private final ConfigurationValueRepository configurationValueRepository;

    public PostgresEnvironmentRepository(final ConfigurationValueRepository configurationValueRepository) {
        this.configurationValueRepository = configurationValueRepository;
    }

    @Override
    public Environment findOne(String application, String profile, String label)
    {
        Environment environment = new Environment(application, profile);

        final Map<String, String> properties = loadProperties(application, profile);
        environment.add(new PropertySource("gcs", properties));
        return environment;
    }

    private Map<String, String> loadProperties(String application, String profiles) {

        Map<String, String> properties = new HashMap<>();

        for (String profile : profiles.split(",")) {
                List<ConfigurationValue> configs = configurationValueRepository
                        .findAllByComponentNameAndProfile(application, profile);
                configs.forEach(property -> properties.put(
                        property.getComponentConfiguration().getConfiguration().getPath(),
                        property.getValue()));
        }
        return properties;
    }
}
