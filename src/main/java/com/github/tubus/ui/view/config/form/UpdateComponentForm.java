package com.github.tubus.ui.view.config.form;

import com.github.tubus.ui.data.dto.component.Component;
import com.github.tubus.ui.data.dto.configuration.ComponentConfiguration;
import com.github.tubus.ui.data.dto.configuration.Configuration;
import com.github.tubus.ui.data.repo.ComponentConfigurationRepository;
import com.github.tubus.ui.data.repo.ComponentRepository;
import com.github.tubus.ui.data.repo.ConfigurationRepository;
import com.github.tubus.ui.data.repo.ConfigurationValueRepository;
import com.github.tubus.ui.service.ConfigurationServiceImpl;
import com.github.tubus.ui.service.provider.ComponentConfigurationProvider;
import com.github.tubus.ui.service.tree.ConfigurationImportServiceImpl;
import com.github.tubus.ui.util.provider.VaadinButtonProvider;
import com.github.tubus.ui.view.main.MainView;
import com.vaadin.flow.component.HasText;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.provider.*;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.*;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import java.util.stream.Stream;
import static com.github.tubus.ui.util.constant.CssStyleId.COMMON_HEADER_TEXT;
import static com.github.tubus.ui.util.i18n.LocalizationUtils.createLocalization;
import static com.github.tubus.ui.util.i18n.LocalizationUtils.createScalableLocalization;

@Route(value = "configserver/components/edit", layout = MainView.class)
@SpringComponent
@UIScope @Slf4j
@Transactional(transactionManager = "gcsTransactionManager")
public class UpdateComponentForm extends AbstractUpdateForm
        implements RouterLayout, HasUrlParameter<String>{

    private static final int PIXELS_PER_HEADER_LETTER = 20;

    private final ConfigurationServiceImpl configurationService;
    private final ConfigurationRepository configurationRepository;
    private final ConfigurationValueRepository configurationValueRepository;

    private HorizontalLayout header;
    private H4 headerText;

    public UpdateComponentForm(final ComponentRepository componentRepository,
                               final ComponentConfigurationRepository componentConfigurationRepository,
                               final ConfigurationRepository configurationRepository,
                               final ConfigurationServiceImpl configurationService,
                               final ConfigurationImportServiceImpl configurationImportService,
                               final ConfigurationValueRepository configurationValueRepository) {
        super(componentRepository, componentConfigurationRepository, configurationImportService);
        this.configurationService = configurationService;
        this.configurationRepository = configurationRepository;
        this.configurationValueRepository = configurationValueRepository;
        setUpGrid();
    }

    @Override
    protected HorizontalLayout createGridItem(ComponentConfiguration configuration) {
        HorizontalLayout horizontalLayout = new HorizontalLayout(createConfigurationTitle(configuration));
        horizontalLayout.setJustifyContentMode(JustifyContentMode.END);
        horizontalLayout.setWidthFull();
        return horizontalLayout;
    }

    @Override
    protected HorizontalLayout createGridTreeHeader() {
        header = new HorizontalLayout();
        header.setJustifyContentMode(JustifyContentMode.START);
        header.setWidthFull();

        H4 headerText = createLocalization(new H4(), HasText::setText, "span.header.config.elements", localizables);
        headerText.setWidth(headerText.getText().length() * PIXELS_PER_HEADER_LETTER, Unit.PIXELS);

        HorizontalLayout headerButtons = new HorizontalLayout();
        headerButtons.setJustifyContentMode(JustifyContentMode.END);
        headerButtons.setWidthFull();

        Button rootElementAddButton = createScalableLocalization(VaadinButtonProvider.provideAddButton(), HasText::setText,
                "input.add.root.element", localizables);
        rootElementAddButton.addClickListener(event -> rootPropertyCreationAction(headerButtons, rootElementAddButton));

        headerButtons.add(rootElementAddButton);
        header.add(headerText, headerButtons);
        return header;
    }

    @Override
    public void setParameter(BeforeEvent event, String parameter) {
        if (headerText == null) {
            Optional<Component> optionalComponent = componentRepository.findByName(parameter);
            if (optionalComponent.isPresent()) {
                component = optionalComponent.get();
                headerText = new H4(component.getName());
                headerText.setWidth(headerText.getText().length() * PIXELS_PER_HEADER_LETTER, Unit.PIXELS);
                headerText.setId(COMMON_HEADER_TEXT);
                header.addComponentAtIndex(1, headerText);
                setDataProvider();
            } else {
                Notification.show(getTranslation("notification.component.not.found"), 5000, Notification.Position.MIDDLE);
                UI.getCurrent().navigate("configserver/components");
            }
        }
    }

    private void setUpGrid() {
        treeGrid.addColumn(new ComponentRenderer<>(this::providePropertyButtons));
        treeGrid.setDetailsVisibleOnClick(false);
        treeGrid.setItemDetailsRenderer(new ComponentRenderer<>(this::provideComponentForm));
    }

    private HorizontalLayout providePropertyButtons(ComponentConfiguration configuration) {
        HorizontalLayout buttons = new HorizontalLayout();
        buttons.setJustifyContentMode(JustifyContentMode.END);
        buttons.setAlignItems(Alignment.BASELINE);
        buttons.setWidthFull();

        if (configuration.getConfiguration().isGroup()) {
            buttons.add(createPropertyItemButton(configuration));
        }
        buttons.add(createDeleteItemButton(configuration));

        return buttons;
    }

    private Button createPropertyItemButton(ComponentConfiguration configuration) {
        Icon plusIcon = VaadinIcon.PLUS.create();
        plusIcon.setColor("green");
        plusIcon.setSize("20px");
        Button addElement = new Button(plusIcon);
        addElement.setWidth("25px");
        addElement.setHeight("25px");
        addElement.addClickListener(event -> {
            treeGrid.setDetailsVisible(configuration, true);
        });
        return addElement;
    }

    private Button createDeleteItemButton(ComponentConfiguration configuration) {
        Icon deleteIcon = VaadinIcon.CLOSE_SMALL.create();
        deleteIcon.setColor("red");
        deleteIcon.setSize("20px");
        Button deleteButton = new Button(deleteIcon);
        deleteButton.setWidth("25px");
        deleteButton.setHeight("25px");
        deleteButton.addClickListener(event -> deleteConfigurationButtonAction(configuration));
        return deleteButton;
    }

    public void deleteConfigurationButtonAction(ComponentConfiguration configuration) {
        configurationValueRepository.deleteAll(configurationValueRepository.findAllByComponentConfigurationId(configuration.getId()));
        componentConfigurationRepository.delete(configuration);
        if (configuration.getConfiguration().getParent() != null) {
            componentConfigurationRepository.findOneByConfigurationIdAndComponentId(
                    configuration.getConfiguration().getParentId(), configuration.getComponent().getId()
            ).ifPresent(this::refresh);
        } else {
            refresh();
        }
    }

    private void rootPropertyCreationAction(HorizontalLayout headerButtons, Button rootElementAddButton) {
        Span header = createLocalization(new Span(), HasText::setText, "span.header.add.root.setting", localizables);
        Checkbox isGroup = createLocalization(new Checkbox( false), Checkbox::setLabel, "span.checkbox.group", localizables);
        ComboBox<Configuration> configurationName = createConfigurationNameComboBox(
                provideSuggestions(true, isGroup, Optional.empty()), Optional.empty());
        Button ok = new Button("OK");
        Button cancel = createLocalization(new Button(), HasText::setText, "input.cancel", localizables);
        HorizontalLayout elementInputLayout = new HorizontalLayout(header, configurationName, isGroup, ok, cancel);
        ok.addClickListener(okEvent -> {
            createComponentConfiguration(configurationName, Boolean.FALSE.equals(isGroup.getValue()), Optional.empty());
            headerButtons.replace(elementInputLayout, rootElementAddButton);
        });
        cancel.addClickListener(okEvent -> headerButtons.replace(elementInputLayout, rootElementAddButton));

        elementInputLayout.setAlignItems(Alignment.BASELINE);
        headerButtons.replace(rootElementAddButton, elementInputLayout);
    }

    private CallbackDataProvider<Configuration, String> provideSuggestions(boolean root, Checkbox isGroup,
                                                                           Optional<ComponentConfiguration> componentConfiguration) {
        return DataProvider.fromFilteringCallbacks( (query) -> fetchConfigurations(query, root, isGroup, componentConfiguration),
                                                    (query) -> countConfigurations(query, root, isGroup, componentConfiguration));
    }

    private ComboBox<Configuration> createConfigurationNameComboBox(
            CallbackDataProvider<Configuration, String> suggestionsProvider, Optional<Configuration> parent) {
        ComboBox<Configuration> configurationName = createLocalization(new ComboBox<>(), ComboBox::setPlaceholder,
                "span.configuration.name", localizables);
        configurationName.setClearButtonVisible(true);
        configurationName.setDataProvider(suggestionsProvider);

        configurationName.addFocusListener(focusEvent -> {
            configurationName.getDataProvider().refreshAll();
        });
        configurationName.setItemLabelGenerator(config -> parent.map(configuration ->
                config.getPath().substring(configuration.getPath().length() + 1)).orElseGet(config::getPath));
        configurationName.addCustomValueSetListener(changeTextEvent -> {
            Configuration configuration = new Configuration();
            configuration.setName(changeTextEvent.getDetail());
            if (parent.isPresent()) {
                configuration.setPath(parent.get().getPath() + "." + changeTextEvent.getDetail());
            } else {
                configuration.setPath(changeTextEvent.getDetail());
            }
            configurationName.setValue(configuration);
        });
        return configurationName;
    }

    private void createComponentConfiguration(ComboBox<Configuration> configurationPath,
                                              boolean notGroup,
                                              Optional<Configuration> parent) {
        if (configurationPath.getValue() != null) {
            log.info("Selected: " + configurationPath.getValue().getName());
            Optional<Configuration> configuration = configurationService.createOrGetConfiguration(configurationPath.getValue().getPath(), notGroup, parent);
            if (configuration.isPresent()) {
                configurationService.createComponentConfiguration(configuration.get(), component);
                Notification.show(getTranslation("notification.done"), 3000, Notification.Position.MIDDLE);
                parent.ifPresentOrElse(this::refresh, this::refresh);
            } else {
                Notification.show(getTranslation("notification.error.not.saved"), 3000, Notification.Position.MIDDLE);
            }
        } else {
            Notification.show(getTranslation("notification.error.no.name"), 3000, Notification.Position.MIDDLE);
        }
    }

    private int countConfigurations(Query<Configuration, String> query, boolean root, Checkbox isGroup,
                                    Optional<ComponentConfiguration> componentConfiguration) {
        return configurationRepository.countSearch(component.getId(),
                preparePath(query, root, componentConfiguration), isGroup.getValue(), root);
    }

    private Stream<Configuration> fetchConfigurations(Query<Configuration, String> query, boolean root, Checkbox isGroup,
                                                      Optional<ComponentConfiguration> componentConfig) {
        return configurationRepository.search(component.getId(),
                preparePath(query, root, componentConfig), isGroup.getValue(), root,
                query.getLimit(), query.getOffset()).stream();
    }

    private String preparePath(Query<Configuration, String> query, boolean root,
                               Optional<ComponentConfiguration> componentConfiguration) {
        StringBuilder path = new StringBuilder("");
        componentConfiguration.ifPresent(cc -> path.append(cc.getConfiguration().getPath()));
        query.getFilter().ifPresent(filter -> {
            if (!StringUtils.isBlank(filter)) {
                if (!root) {
                    path.append(".");
                }
                path.append(filter);
            }
        });
        return path.toString();
    }

    protected void importAction(ConfigurationImportServiceImpl configurationImportService,
                                MemoryBuffer memoryBuffer) {
        configurationImportService.importComponentConfiguration(memoryBuffer, component);
    }

    private void setDataProvider() {
        treeGrid.setDataProvider(new ComponentConfigurationProvider(component, componentConfigurationRepository));
    }

    private HorizontalLayout provideComponentForm(ComponentConfiguration item) {
        Span header = new Span(getTranslation("span.configuration.create"));
        Checkbox isGroup = new Checkbox(getTranslation("span.checkbox.group"), false);
        ComboBox<Configuration> configurationName = createConfigurationNameComboBox(provideSuggestions(false,
                isGroup, Optional.of(item)), Optional.of(item.getConfiguration()));

        Button ok = new Button("OK");
        ok.addClickListener(okEvent -> {
            createComponentConfiguration(configurationName,
                    Boolean.FALSE.equals(isGroup.getValue()), Optional.of(item.getConfiguration()));
            treeGrid.setDetailsVisible(item, false);
        });
        Button cancel = new Button(getTranslation("input.cancel"));
        cancel.addClickListener(okEvent -> {
            treeGrid.setDetailsVisible(item, false);
        });

        HorizontalLayout elementInputLayout = new HorizontalLayout(header, configurationName, isGroup, ok, cancel);
        elementInputLayout.setAlignItems(Alignment.BASELINE);
        return elementInputLayout;
    }

    @Override
    public String getPageTitle() {
        return getTranslation("main.update.title.component");
    }
}
