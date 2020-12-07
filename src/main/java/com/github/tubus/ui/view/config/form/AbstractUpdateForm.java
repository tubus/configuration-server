package com.github.tubus.ui.view.config.form;

import com.github.tubus.ui.data.dto.component.Component;
import com.github.tubus.ui.data.dto.configuration.ComponentConfiguration;
import com.github.tubus.ui.data.dto.configuration.Configuration;
import com.github.tubus.ui.data.repo.ComponentConfigurationRepository;
import com.github.tubus.ui.data.repo.ComponentRepository;
import com.github.tubus.ui.service.tree.ConfigurationImportServiceImpl;
import com.github.tubus.ui.util.i18n.Localizable;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasText;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.HasDynamicTitle;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import static com.github.tubus.ui.util.i18n.LocalizationUtils.createLocalization;
import static com.github.tubus.ui.util.i18n.LocalizationUtils.localize;
import static com.github.tubus.ui.util.provider.VaadinButtonProvider.provideBackButton;
import static com.github.tubus.ui.util.provider.VaadinButtonProvider.provideUploadButton;
import static com.github.tubus.ui.util.provider.VaadinLayoutProvider.provideGridAndSetupLayout;

@Slf4j
@CssImport("./styles/views/configurations/configurations-update-view.css")
public abstract class AbstractUpdateForm extends VerticalLayout implements LocaleChangeObserver, HasDynamicTitle {

    protected final ComponentConfigurationRepository componentConfigurationRepository;
    private final ConfigurationImportServiceImpl configurationImportService;
    protected final ComponentRepository componentRepository;

    protected final TreeGrid<ComponentConfiguration> treeGrid;

    protected final List<Localizable> localizables;

    protected Component component;

    protected AbstractUpdateForm(final ComponentRepository componentRepository,
                                 final ComponentConfigurationRepository componentConfigurationRepository,
                                 final ConfigurationImportServiceImpl configurationImportService) {
        this.componentRepository = componentRepository;
        this.componentConfigurationRepository = componentConfigurationRepository;
        this.configurationImportService = configurationImportService;
        this.localizables = new ArrayList<>();

        add(createHeaderButtons());
        add(createGridTreeHeader());
        this.treeGrid = provideGridAndSetupLayout(this, this::createGridItem);
    }

    protected abstract HorizontalLayout createGridItem(ComponentConfiguration configuration);

    protected abstract HorizontalLayout createGridTreeHeader();

    protected abstract void importAction(ConfigurationImportServiceImpl configurationImportService,
                                         MemoryBuffer memoryBuffer);

    private HorizontalLayout createHeaderButtons() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setWidthFull();

        Button backButton = createLocalization(provideBackButton(), HasText::setText, "input.back", localizables);
        MemoryBuffer memoryBuffer = new MemoryBuffer();

        Button load = createLocalization(new Button(), HasText::setText, "input.import.file", localizables);
        load.setEnabled(false);
        Upload upload = provideUploadButton(memoryBuffer, load);
        upload.addSucceededListener(event -> {
            load.setEnabled(true);
        });
        load.addClickListener(event -> {
            load.setEnabled(false);
            importAction(configurationImportService, memoryBuffer);
            refresh();
        });

        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.START);
        buttonLayout.setAlignItems(Alignment.CENTER);
        buttonLayout.add(backButton, upload);

        return buttonLayout;
    }

    protected VerticalLayout createConfigurationTitle(ComponentConfiguration configuration) {
        Span configurationName = new Span(configuration.createFullConfigurationName());
        Span configurationDescription = new Span(configuration.getConfiguration().getPath());
        configurationDescription.getStyle().set("color", "var(--lumo-secondary-text-color)");
        configurationDescription.getStyle().set("font-size", "var(--lumo-font-size-s)");
        VerticalLayout configurationLine = new VerticalLayout(configurationName, configurationDescription);
        configurationLine.setPadding(false);
        configurationLine.setSpacing(false);
        return configurationLine;
    }

    protected void refresh() {
        log.info("All refreshed");
        treeGrid.getDataProvider().refreshAll();
    }

    protected void refresh(Configuration item) {
        log.info("Item refreshing: {}", item);
        componentConfigurationRepository
                .findOneByConfigurationIdAndComponentId(item.getId(), component.getId())
                .ifPresentOrElse(cc -> treeGrid.getDataProvider().refreshItem(cc), this::refresh);
    }

    protected void refresh(ComponentConfiguration item) {
        int count = componentConfigurationRepository.countAllByConfigurationParentId(item.getConfiguration().getId(), item.getComponent());
        if (count == 0) {
            treeGrid.getDataCommunicator().collapse(item);
            if (item.getConfiguration().getParent() != null) {
                componentConfigurationRepository.findOneByConfigurationIdAndComponentId(
                        item.getConfiguration().getParentId(), item.getComponent().getId()
                ).ifPresent(this::refresh);
            } else {
                refresh();
            }
        } else {
            log.info("Item refreshed: {}", item);
            treeGrid.getDataProvider().refreshItem(item);
        }
    }

    @Override
    public abstract String getPageTitle();

    @Override
    public void localeChange(LocaleChangeEvent event) {
        UI.getCurrent().getPage().setTitle(getPageTitle());
        localize(localizables);
    }
}