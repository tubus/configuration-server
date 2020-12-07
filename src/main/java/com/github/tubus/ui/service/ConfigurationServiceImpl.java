package com.github.tubus.ui.service;

import com.github.tubus.ui.data.dto.component.Component;
import com.github.tubus.ui.data.dto.configuration.ComponentConfiguration;
import com.github.tubus.ui.data.dto.configuration.Configuration;
import com.github.tubus.ui.data.repo.ComponentConfigurationRepository;
import com.github.tubus.ui.data.repo.ConfigurationRepository;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.Optional;

@Service
public class ConfigurationServiceImpl {

    private final ConfigurationRepository configurationRepository;
    private final ComponentConfigurationRepository componentConfigurationRepository;

    public ConfigurationServiceImpl(final ConfigurationRepository configurationRepository,
                                    final ComponentConfigurationRepository componentConfigurationRepository) {
        this.configurationRepository = configurationRepository;
        this.componentConfigurationRepository = componentConfigurationRepository;
    }

    public void createComponentConfiguration(Configuration configuration, Component component) {
        ComponentConfiguration componentConfiguration = new ComponentConfiguration();
        componentConfiguration.setComponent(component);
        componentConfiguration.setConfiguration(configuration);
        componentConfigurationRepository.save(componentConfiguration);
    }

    public Optional<Configuration> createOrGetConfiguration(@NotNull @NotEmpty String path, boolean notGroup, Optional<Configuration> parent) {
        if (configurationRepository.existsByPath(path)) {
            Optional<Configuration> configuration = configurationRepository.findByPath(path);
            if (configuration.isPresent() &&
                configuration.get().isGroup() == notGroup &&
                Objects.equals(configuration.get().getParentId(), parent.map(Configuration::getId).orElse(null))
            ) {
                return Optional.empty();
            }
            return configuration;
        }
        Configuration configuration = new Configuration();
        configuration.setPath(path);
        String[] names = path.split("\\.");
        configuration.setName(names[names.length - 1]);

        configuration.setGroup(!notGroup);
        configuration.setParentId(parent.map(Configuration::getId).orElse(null));
        configuration.setDescription(path);

        configurationRepository.save(configuration);

        return Optional.of(configuration);
    }
}