package com.github.tubus.ui.view.config.tab;

import com.github.tubus.ui.data.dto.component.Component;
import com.github.tubus.ui.data.dto.component.ComponentEnvironment;
import com.github.tubus.ui.data.dto.configuration.ComponentConfiguration;
import com.github.tubus.ui.data.dto.configuration.ConfigurationValue;
import com.github.tubus.ui.data.dto.env.Environment;
import com.github.tubus.ui.data.dto.env.EnvironmentProfile;
import com.github.tubus.ui.data.dto.state.GridState;
import com.github.tubus.ui.data.repo.*;
import com.github.tubus.ui.util.provider.ConfigComponentElementsProvider;
import com.github.tubus.ui.util.provider.VaadinButtonProvider;
import com.github.tubus.ui.util.provider.VaadinFieldsProvider;
import com.github.tubus.ui.view.config.ConfigurationServer;
import com.github.tubus.ui.view.config.form.UpdateComponentForm;
import com.github.tubus.ui.view.config.form.UpdateConfigurationValueForm;
import com.github.tubus.ui.view.main.MainView;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.i18n.I18NProvider;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.*;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static com.github.tubus.ui.util.constant.CssStyleId.CLASSIC_VAADIN_GRID;
import static com.github.tubus.ui.util.constant.CssStyleId.CONFIGURATION_SERVER;

@Route(value = "configserver/components", layout = MainView.class)
@SpringComponent @UIScope @Slf4j
@CssImport("./styles/views/configurations/configurations-component-view.css")
public class ConfigComponent extends ConfigurationServer implements AfterNavigationObserver, LocaleChangeObserver,
        HasDynamicTitle {

    private final ComponentRepository componentRepository;
    private final ComponentEnvironmentRepository componentEnvironmentRepository;
    private final ComponentConfigurationRepository componentConfigurationRepository;
    private final ConfigurationValueRepository configurationValueRepository;
    private final EnvironmentRepository environmentRepository;
    private final I18NProvider i18NProvider;
    private final Grid<Component> grid;
    private final GridState<Component> gridState;
    private final TextField search = VaadinFieldsProvider.provideSearchField();

    private final Map<String, Button> buttons = new HashMap<>();
    private final Map<String, TextField> textFields = new HashMap<>();

    public ConfigComponent(final ComponentRepository componentRepository,
                           final ComponentEnvironmentRepository componentEnvironmentRepository,
                           final ComponentConfigurationRepository componentConfigurationRepository,
                           final ConfigurationValueRepository configurationValueRepository,
                           final EnvironmentRepository environmentRepository,
                           final I18NProvider i18NProvider) {
        this.componentRepository = componentRepository;
        this.componentEnvironmentRepository = componentEnvironmentRepository;
        this.componentConfigurationRepository = componentConfigurationRepository;
        this.configurationValueRepository = configurationValueRepository;
        this.environmentRepository = environmentRepository;
        this.i18NProvider = i18NProvider;

        List<Component> components = componentRepository.findAll();
        grid = ConfigComponentElementsProvider.provideBaseGrid(components, this::createComponentRow);
        grid.setId(CLASSIC_VAADIN_GRID);
        gridState = new GridState<>(new HashSet<>(components));

        setId(CONFIGURATION_SERVER);
        addClassName(CONFIGURATION_SERVER);
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        add(createComponentAddButton(), grid);
    }

    private HorizontalLayout createComponentAddButton() {
        HorizontalLayout addComponentLayout = new HorizontalLayout();

        HorizontalLayout searchLayout = new HorizontalLayout();
        searchLayout.add(search);
        search.addValueChangeListener(event -> updateGrid());

        searchLayout.setSizeFull();
        searchLayout.setJustifyContentMode(JustifyContentMode.START);

        Button addComponentButton = VaadinButtonProvider.provideAddButton(getTranslation("input.add.component"));
        buttons.put("input.add.component", addComponentButton);
        addComponentButton.addClickListener(event -> addComponentLayout.replace(addComponentButton,
                createAddComponentForm(addComponentLayout, addComponentButton)));

        addComponentLayout.setWidthFull();
        addComponentLayout.setAlignItems(Alignment.CENTER);
        addComponentLayout.add(searchLayout, addComponentButton);
        addComponentLayout.setJustifyContentMode(JustifyContentMode.CENTER);

        return addComponentLayout;
    }

    private FormLayout createAddComponentForm(HorizontalLayout addComponentLayout, Button addComponentButton) {
        FormLayout nameLayout = new FormLayout();

        TextField componentInput = new TextField();
        textFields.put("input.component.label", componentInput);

        componentInput.setLabel(getTranslation("input.component.label"));
        componentInput.setPlaceholder("rest-service");
        componentInput.setClearButtonVisible(true);
        componentInput.setRequired(true);
        ComboBox<Environment> environmentsBox = ConfigComponentElementsProvider
                .createEnvironmentsBox(environmentRepository, null, i18NProvider);
        Button saveButton = createComponentSaveButton(addComponentLayout, addComponentButton, nameLayout, componentInput, environmentsBox);

        Button cancelButton = createCancelComponentCreationButton(addComponentLayout, addComponentButton, nameLayout);

        nameLayout.add(componentInput, environmentsBox, saveButton, cancelButton);

        nameLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("25em", 1),
                new FormLayout.ResponsiveStep("32em", 2),
                new FormLayout.ResponsiveStep("40em", 3));

        return nameLayout;
    }

    private Button createCancelComponentCreationButton(HorizontalLayout addComponentLayout, Button addComponentButton, FormLayout nameLayout) {
        Icon cancelButtonIcon = VaadinIcon.MINUS.create();
        cancelButtonIcon.setColor("red");
        Button cancelButton = new Button(getTranslation("input.cancel"), cancelButtonIcon);
        buttons.put("input.cancel", cancelButton);
        cancelButton.addClickListener(event -> {
            addComponentLayout.replace(nameLayout, addComponentButton);
        });
        return cancelButton;
    }

    private Button createComponentSaveButton(HorizontalLayout addComponentLayout,
                                             Button addComponentButton,
                                             FormLayout nameLayout,
                                             TextField titleField,
                                             ComboBox<Environment> environmentsBox) {
        Button saveButton = new Button(getTranslation("input.save"));
        buttons.put("input.save", saveButton);
        Icon saveButtonIcon = VaadinIcon.PLUS.create();
        saveButtonIcon.setColor("Green");
        saveButton.setIcon(saveButtonIcon);
        saveButton.addClickListener(event ->
                saveComponentAction(addComponentLayout, addComponentButton, nameLayout, titleField, environmentsBox));
        return saveButton;
    }

    private void saveComponentAction(HorizontalLayout addComponentLayout, Button addComponentButton, FormLayout nameLayout, TextField titleField, ComboBox<Environment> environmentsBox) {
        if (StringUtils.isBlank(titleField.getValue())) {
            titleField.setErrorMessage(getTranslation("input.error.absent.name"));
            titleField.setInvalid(true);
            return;
        } else if (componentRepository.existsByName(titleField.getValue().trim())) {
            titleField.setErrorMessage(getTranslation("input.error.already.exists"));
            titleField.setInvalid(true);
            return;
        }
        Component component = new Component();
        component.setName(titleField.getValue().trim());
        Component saved = componentRepository.save(component);
        if (environmentsBox.getValue() != null) {
            ComponentEnvironment componentEnvironment = new ComponentEnvironment(
                    new ComponentEnvironment.ComponentEnvironmentId(
                            environmentsBox.getValue().getId(), saved.getId()),
                    saved, environmentsBox.getValue(), Instant.now());
            componentEnvironmentRepository.save(componentEnvironment);
        }
        updateGrid();
        addComponentLayout.replace(nameLayout, addComponentButton);
    }

    private VerticalLayout createComponentRow(Component component) {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setId("component-row");
        verticalLayout.setWidthFull();

        HorizontalLayout componentHeader = createComponentHeader(component);
        verticalLayout.addAndExpand(componentHeader);

        verticalLayout.addAndExpand(createComponentEnvironments(component));
        Button editConfigurations = createEditConfigurationsButton(component);
        HorizontalLayout editValues = createEditConfigurationValuesButton(component);
        verticalLayout.add(editConfigurations, editValues);

        return verticalLayout;
    }

    private HorizontalLayout createComponentHeader(Component component) {
        HorizontalLayout componentHeader = new HorizontalLayout();
        Span componentName = createSpan(component.getName(), "black", "2.5rem");
        Button deleteComponent = createDeleteComponentButton(component);
        HorizontalLayout rightDeck = new HorizontalLayout();
        rightDeck.add(deleteComponent);
        rightDeck.setJustifyContentMode(JustifyContentMode.END);

        componentHeader.add(componentName);
        componentHeader.addAndExpand(rightDeck);

        return componentHeader;
    }

    private Button createEditConfigurationsButton(Component component) {
        Icon icon = VaadinIcon.EDIT.create();
        icon.setColor("purple");
        Button editConfigurations = new Button(getTranslation("input.component.configuration"), icon);
        editConfigurations.addClickListener(event -> {
            UI.getCurrent().navigate(UpdateComponentForm.class, component.getName());
        });
        return editConfigurations;
    }

    private HorizontalLayout createEditConfigurationValuesButton(Component component) {
        HorizontalLayout layout = new HorizontalLayout();

        ComboBox<Environment> environmentComboBox = ConfigComponentElementsProvider
                .createOwnEnvironmentsBox(environmentRepository, component, i18NProvider);
        environmentComboBox.addValueChangeListener(event -> {
            if (event.getValue() != null && layout.getComponentCount() == 2) {
                layout.addComponentAtIndex(2, ConfigComponentElementsProvider.createEnvironmentProfilesBox(event.getValue(), i18NProvider));
            } else if (event.getValue() != null && layout.getComponentCount() == 3) {
                layout.remove(layout.getComponentAt(2));
                layout.addComponentAtIndex(2, ConfigComponentElementsProvider.createEnvironmentProfilesBox(event.getValue(), i18NProvider));
            } else if (layout.getComponentCount() == 3) {
                layout.remove(layout.getComponentAt(2));
            }
        });

        Icon icon = VaadinIcon.EDIT.create();
        icon.setColor("purple");
        Button editConfigurations = new Button(getTranslation("input.edit.values"), icon);
        editConfigurations.addClickListener(event -> {
            if (environmentComboBox.getValue() != null && layout.getComponentAt(2) != null &&
                    ((ComboBox<EnvironmentProfile>) layout.getComponentAt(2)).getValue() != null) {
                UI.getCurrent().navigate(UpdateConfigurationValueForm.class,
                        component.getName() + "," + environmentComboBox.getValue().getName() + "," +
                        ((ComboBox<EnvironmentProfile>) layout.getComponentAt(2)).getValue().getName());
            } else {
                Notification.show(getTranslation("input.error.no.profile"), 1000, Notification.Position.MIDDLE);
            }
        });

        layout.add(editConfigurations, environmentComboBox);
        return layout;
    }

    private HorizontalLayout createComponentEnvironments(Component component) {
        HorizontalLayout header = new HorizontalLayout();

        HorizontalLayout environments = createEnvironmentsLayout(component);
        header.add(environments);

        if (gridState.isEdit(component)) {
            VerticalLayout addEnvLayout = new VerticalLayout();

            ComboBox<Environment> addEnvironmentBox = ConfigComponentElementsProvider
                    .createEnvironmentsBox(environmentRepository, component, i18NProvider);
            Button addEnvButton = new Button(getTranslation("input.add"), VaadinIcon.PLUS_CIRCLE.create());
            Button cancelAddEnvButton = new Button(getTranslation("input.cancel"), VaadinIcon.CLOSE_SMALL.create());
            HorizontalLayout addEnvButtonsLayout = new HorizontalLayout();
            addEnvButtonsLayout.add(addEnvButton, cancelAddEnvButton);
            addEnvLayout.add(addEnvironmentBox, addEnvButtonsLayout);
            addEnvButton.addClickListener(addEvent -> addEnvButtonAction(component, addEnvironmentBox));
            cancelAddEnvButton.addClickListener(cancelAddEvent -> {
                gridState.setNotEdit(component);
                updateGrid();
            });

            header.add(addEnvLayout);
        } else {
            Button addEnvironmentButton = createEnvironmentAddButton();
            addEnvironmentButton.addClickListener(event -> {
                if (!gridState.isEdit(component)) {
                    gridState.setEdit(component);
                    updateGrid();
                }
            });
            header.add(addEnvironmentButton);
        }

        header.setAlignItems(Alignment.BASELINE);

        header.setWidthFull();
        return header;
    }

    private Button createDeleteComponentButton(Component component) {
        Button deleteComponent = new Button(getTranslation("input.delete.component"));
        Icon icon = VaadinIcon.CLOSE_BIG.create();
        icon.setColor("red");
        deleteComponent.setIcon(icon);
        deleteComponent.addClickListener(event -> {
            List<ComponentConfiguration> configurations = componentConfigurationRepository.findAllByComponent(component);
            List<ConfigurationValue> values = configurationValueRepository.findAllByComponentConfigurationIn(configurations);
            configurationValueRepository.deleteAll(values);
            componentConfigurationRepository.deleteAll(configurations);
            List<ComponentEnvironment> environments = componentEnvironmentRepository
                    .findAllByComponent(component);
            componentEnvironmentRepository.deleteAll(environments);
            componentRepository.deleteById(component.getId());
            updateGrid();
        });
        return deleteComponent;
    }

    private void addEnvButtonAction(Component component, ComboBox<Environment> addEnvironmentBox) {
        Environment value = addEnvironmentBox.getValue();
        if (value == null) {
            Notification.show(getTranslation("input.error.no.environment"), 3000, Notification.Position.TOP_CENTER);
            return;
        }
        ComponentEnvironment componentEnvironment = new ComponentEnvironment(
                new ComponentEnvironment.ComponentEnvironmentId(value.getId(), component.getId()),
                component, value, Instant.now());
        componentEnvironmentRepository.save(componentEnvironment);

        gridState.setNotEdit(component);
        updateGrid();
    }

    private Button createEnvironmentAddButton() {
        Icon addIcon = VaadinIcon.PLUS.create();
        addIcon.setColor("green");
        return new Button(getTranslation("button.add.environment"), addIcon);
    }

    private HorizontalLayout createEnvironmentsLayout(Component component) {
        HorizontalLayout environments = new HorizontalLayout();

        Span envHeader = createSpan(getTranslation("span.environments") + " : ", "blue", "1rem");

        environments.add(envHeader);
        environments.setSpacing(true);
        environments.setMargin(true);
        environments.setJustifyContentMode(JustifyContentMode.CENTER);

        Tabs tabs = new Tabs();
        tabs.setWidthFull();
        tabs.setAutoselect(false);
        component.getEnvironments().forEach(env -> {
            HorizontalLayout layout = new HorizontalLayout();
            Button button = createComponentEnvDeleteButton(env, component);
            Text text = new Text(env.getName());
            layout.add(text, button);
            layout.setAlignItems(Alignment.BASELINE);
            layout.setJustifyContentMode(JustifyContentMode.CENTER);
            tabs.add(layout);
        });

        environments.add(tabs);
        environments.setAlignItems(Alignment.BASELINE);
        return environments;
    }

    private Span createSpan(String text, String color, String fontSize) {
        Span componentName = new Span(text);
        componentName.getElement().getStyle().set("color", color);
        componentName.getElement().getStyle().set("font-family", "arial");
        componentName.getElement().getStyle().set("font-size", fontSize);
        componentName.getElement().getStyle().set("font-weight", "bold");
        return componentName;
    }


    private void updateGrid() {
        if (search.getValue() == null) {
            grid.setItems(componentRepository.findAll());
        } else {
            grid.setItems(componentRepository.searchByName(search.getValue()));
        }
    }

    private Button createComponentEnvDeleteButton(Environment env, Component component) {
        Button deleteEnv = new Button();
        Icon deleteIcon = VaadinIcon.MINUS.create();
        deleteIcon.setColor("red");
        deleteEnv.setIcon(deleteIcon);
        deleteEnv.addClickListener(event -> {
            componentEnvironmentRepository.findOneByComponentAndEnvironment(component, env)
            .ifPresentOrElse(componentEnvironmentRepository::delete, () -> {
                Notification.show(getTranslation("notification.already.deleted"), 3000, Notification.Position.TOP_CENTER);
            });
            updateGrid();
        });
        deleteEnv.setSizeFull();
        return deleteEnv;
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        UI.getCurrent().getPage().setTitle(getPageTitle());
        tabs.setSelectedTab(configTab);
        updateGrid();
    }

    @Override
    public void localeChange(LocaleChangeEvent event) {
        super.localeChange(event);
        UI.getCurrent().getPage().setTitle(getPageTitle());
        buttons.forEach((key, button) -> button.setText(getTranslation(key)));
        textFields.forEach((key, button) -> button.setPlaceholder(getTranslation(key)));
        textFields.forEach((key, button) -> button.setTitle(getTranslation(key)));
        updateGrid();
    }

    @Override
    public String getPageTitle() {
        return getTranslation("main.logo.title.components");
    }
}