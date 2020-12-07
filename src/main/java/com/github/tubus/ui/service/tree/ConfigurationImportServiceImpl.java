package com.github.tubus.ui.service.tree;

import com.github.tubus.ui.data.dto.component.Component;
import com.github.tubus.ui.data.dto.configuration.ComponentConfiguration;
import com.github.tubus.ui.data.dto.configuration.Configuration;
import com.github.tubus.ui.data.repo.ComponentConfigurationRepository;
import com.github.tubus.ui.data.repo.ConfigurationRepository;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service @Slf4j
public class ConfigurationImportServiceImpl {

    private final ComponentConfigurationRepository componentConfigurationRepository;
    private final ConfigurationRepository configurationRepository;

    public ConfigurationImportServiceImpl(final ComponentConfigurationRepository componentConfigurationRepository,
                                          final ConfigurationRepository configurationRepository) {
        this.componentConfigurationRepository = componentConfigurationRepository;
        this.configurationRepository = configurationRepository;
    }

    public void importComponentConfiguration(MemoryBuffer memoryBuffer, Component component) {
        InputStream inputStream = memoryBuffer.getInputStream();

        YamlPropertiesFactoryBean yamlFactory = new YamlPropertiesFactoryBean();
        yamlFactory.setResources(new InputStreamResource(inputStream));
        try {
            Properties properties = yamlFactory.getObject();
            if (properties != null) {
                properties.forEach((key, value) -> {
                    log.info("{}: {}", key, value);
                    createProperty(key.toString(), component);
                });
            }
        } catch (Exception exception) {
            Notification.show("Ошибка: Некорректное содержимое файла", 1500, Notification.Position.MIDDLE);
        }
    }

    private void createProperty(String key, Component component) {
        List<String> parts = Stream.of(key.split("\\.")).collect(Collectors.toList());

        StringBuilder builder = new StringBuilder();
        Optional<Configuration> parent = Optional.empty();
        if (parts.size() > 1) {
            for (String name : parts.subList(0, parts.size() - 1)) {
                builder.append(name);
                String path = builder.toString();
                Configuration configuration = findOrCreateConfiguration(path, name, parent, true);
                createConfigurationComponent(configuration, component);

                parent = Optional.of(configuration);
                builder.append(".");
            }
        }
        if (parts.size() > 0) {
            String name = parts.get(parts.size() - 1);
            builder.append(name);
            String path = builder.toString();
            Configuration configuration = findOrCreateConfiguration(path, name, parent, false);
            createConfigurationComponent(configuration, component);
        }
    }

    private Configuration findOrCreateConfiguration(String path, String name, Optional<Configuration> parent, boolean isGroup) {
        return configurationRepository.findByPath(path).orElseGet(() -> {
            Configuration configGroup = new Configuration();
            configGroup.setName(name);
            configGroup.setPath(path);
            parent.ifPresent(parentEntity -> configGroup.setParentId(parentEntity.getId()));
            parent.ifPresent(configGroup::setParent);
            configGroup.setGroup(isGroup);
            return configurationRepository.save(configGroup);
        });
    }

    private void createConfigurationComponent(Configuration configuration, Component component) {
        Optional<ComponentConfiguration> optional = componentConfigurationRepository
                .findOneByConfigurationIdAndComponentId(configuration.getId(), component.getId());
        if (optional.isEmpty()) {
            ComponentConfiguration componentConfiguration = new ComponentConfiguration();
            componentConfiguration.setComponent(component);
            componentConfiguration.setConfiguration(configuration);
            componentConfigurationRepository.save(componentConfiguration);
        }
    }
}