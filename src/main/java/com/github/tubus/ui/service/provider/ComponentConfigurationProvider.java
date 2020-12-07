package com.github.tubus.ui.service.provider;

import com.vaadin.flow.data.provider.DataChangeEvent;
import com.vaadin.flow.data.provider.DataProviderListener;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalDataProvider;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalQuery;
import com.vaadin.flow.shared.Registration;
import com.github.tubus.ui.data.dto.component.Component;
import com.github.tubus.ui.data.dto.configuration.ComponentConfiguration;
import com.github.tubus.ui.data.repo.ComponentConfigurationRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class ComponentConfigurationProvider implements HierarchicalDataProvider<ComponentConfiguration, String> {

    private final Component component;
    private final ComponentConfigurationRepository componentConfigurationRepository;
    private final List<DataProviderListener<ComponentConfiguration>> listenerList = new ArrayList<>();

    public ComponentConfigurationProvider(final Component component,
                                          final ComponentConfigurationRepository componentConfigurationRepository) {
        this.component = component;

        this.componentConfigurationRepository = componentConfigurationRepository;
    }


    @Override
    public int getChildCount(HierarchicalQuery<ComponentConfiguration, String> query) {
        ComponentConfiguration parent = query.getParent();
        if (parent == null) {
            return componentConfigurationRepository.countAllByConfigurationParentIdIsNull(component);
        } else {
            return componentConfigurationRepository.countAllByConfigurationParentId(parent.getConfiguration().getId(),
                    parent.getComponent());
        }
    }

    @Override
    public Stream<ComponentConfiguration> fetchChildren(HierarchicalQuery<ComponentConfiguration, String> query) {
        ComponentConfiguration parent = query.getParent();
        if (parent == null) {
            return componentConfigurationRepository.findAllByConfigurationParentIdIsNull(component).stream();
        } else {
            return componentConfigurationRepository.findAllByParentId(parent.getConfiguration().getId(),
                    parent.getComponent()).stream();
        }
    }

    @Override
    public boolean hasChildren(ComponentConfiguration item) {
        return componentConfigurationRepository
                .countAllByConfigurationParentId(item.getConfiguration().getId(),item.getComponent()) > 0;
    }

    @Override
    public boolean isInMemory() {
        return false;
    }

    @Override
    public void refreshItem(ComponentConfiguration item) {
        listenerList.forEach(listener -> {
            DataChangeEvent.DataRefreshEvent<ComponentConfiguration> event =
                    new DataChangeEvent.DataRefreshEvent<>(this, item, true);
            listener.onDataChange(event);
        });
    }

    @Override
    public void refreshAll() {
        listenerList.forEach(listener -> {
            DataChangeEvent<ComponentConfiguration> event = new DataChangeEvent<>(this);
            listener.onDataChange(event);
        });
    }

    @Override
    public Registration addDataProviderListener(DataProviderListener<ComponentConfiguration> listener) {
        listenerList.add(listener);
        return (Registration) () -> listenerList.remove(listener);
    }
}