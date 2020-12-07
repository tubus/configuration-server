package com.github.tubus.ui.view.config.tab;

import com.github.tubus.ui.data.dto.env.Environment;
import com.github.tubus.ui.data.dto.env.EnvironmentProfile;
import com.github.tubus.ui.data.dto.state.ButtonsState;
import com.github.tubus.ui.data.repo.ComponentEnvironmentRepository;
import com.github.tubus.ui.data.repo.EnvironmentProfileRepository;
import com.github.tubus.ui.data.repo.EnvironmentRepository;
import com.github.tubus.ui.util.i18n.Localizable;
import com.github.tubus.ui.view.config.ConfigurationServer;
import com.github.tubus.ui.view.main.MainView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.*;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import com.github.tubus.ui.service.provider.EnvironmentProfileGridProvider;

import java.util.ArrayList;
import java.util.List;

import static com.github.tubus.ui.util.i18n.LocalizationUtils.localize;

@Route(value = "configserver/environments", layout = MainView.class)
@SpringComponent @UIScope
public class ConfigEnvironment extends ConfigurationServer
        implements AfterNavigationObserver, HasDynamicTitle, LocaleChangeObserver {

    private final Grid<Environment> grid;
    private final TextField search = new TextField(getTranslation("input.search"));
    private final ButtonsState buttonsState = new ButtonsState(true, false);
    private final EnvironmentRepository environmentRepository;
    private final ComponentEnvironmentRepository componentEnvironmentRepository;
    private final EnvironmentProfileRepository environmentProfileRepository;

    private final List<Localizable> localizables = new ArrayList<>();

    public ConfigEnvironment(final EnvironmentRepository environmentRepository,
                             final ComponentEnvironmentRepository componentEnvironmentRepository,
                             final EnvironmentProfileRepository environmentProfileRepository) {
        this.environmentRepository = environmentRepository;
        this.componentEnvironmentRepository = componentEnvironmentRepository;
        this.environmentProfileRepository = environmentProfileRepository;
        setSizeFull();
        localizables.add(new Localizable(search::setLabel, "input.search"));
        this.grid = createEnvironmentGrid();
        add(grid);
    }

    private Grid<Environment> createEnvironmentGrid() {
        Grid<Environment> grid = new Grid<>();

        grid.getEditor().setBinder(new BeanValidationBinder<>(Environment.class));

        TextField nameEditor = new TextField();
        TextField descriptionEditor = new TextField();

        Grid.Column<Environment> nameColumn = grid.addColumn(Environment::getName)
                .setHeader(getTranslation("span.table.column.name"))
                .setAutoWidth(true)
                .setSortable(true)
                .setEditorComponent(nameEditor);
        localizables.add(new Localizable(nameColumn::setHeader, "span.table.column.name"));

        Grid.Column<Environment> descColumn = grid.addColumn(Environment::getDescription)
                .setHeader(getTranslation("span.table.column.description"))
                .setAutoWidth(true)
                .setSortable(true)
                .setEditorComponent(descriptionEditor);
        localizables.add(new Localizable(descColumn::setHeader, "span.table.column.description"));

        ComponentRenderer<Grid<EnvironmentProfile>, Environment> innerGrid = new ComponentRenderer<>(
                new EnvironmentProfileGridProvider(environmentProfileRepository, environmentRepository, grid));
        Grid.Column<Environment> profileColumn = grid.addColumn(innerGrid)
                .setTextAlign(ColumnTextAlign.CENTER)
                .setWidth("40%")
                .setHeader(getTranslation("span.table.column.profiles.list"))
                .setSortable(false);
        localizables.add(new Localizable(profileColumn::setHeader, "span.table.column.profiles.list"));

        if (grid.getColumnByKey("environmentGridEditButtons") != null) {
            grid.removeColumnByKey("environmentGridEditButtons");
        }
        grid.addComponentColumn(environment -> createEditButtons(environment, environmentRepository))
                .setKey("environmentGridEditButtons")
                .setAutoWidth(true)
                .setHeader(createCreationButton());

        grid.getEditor().getBinder().forField(nameEditor).bind(Environment::getName, Environment::setName);
        grid.getEditor().getBinder().forField(descriptionEditor).bind(Environment::getDescription, Environment::setDescription);

        grid.setItems(findEnvironments(environmentRepository));

        grid.setRowsDraggable(false);
        grid.setSelectionMode(Grid.SelectionMode.NONE);
        grid.setColumnReorderingAllowed(true);
        grid.setSizeFull();
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_WRAP_CELL_CONTENT);

        return grid;
    }

    private List<Environment> findEnvironments(EnvironmentRepository environmentRepository) {
        if (search.getValue() == null) {
            return environmentRepository.findAll();
        } else {
            return environmentRepository.search(search.getValue());
        }
    }

    private HorizontalLayout createCreationButton() {
        Icon createButtonIcon = VaadinIcon.PLUS_CIRCLE.create();
        createButtonIcon.setColor("green");
        Button button = new Button(getTranslation("input.create"), createButtonIcon);
        localizables.add(new Localizable(button::setText, "input.create"));

        button.addClickListener(event -> {
            Environment environment = new Environment();
            String name = getTranslation("span.new.environment");
            int index = 1;
            while (environmentRepository.findOneByNameEquals(name).isPresent()) {
                name = getTranslation("span.new.environment") + " " + index;
                index++;
            }
            environment.setName(name);
            environmentRepository.save(environment);

            grid.setItems(findEnvironments(environmentRepository));
        });

        HorizontalLayout functionalPanel = new HorizontalLayout();

        search.addValueChangeListener(event -> {
            grid.setItems(findEnvironments(environmentRepository));
        });

        functionalPanel.add(search, button);
        functionalPanel.setWidthFull();
        functionalPanel.setAlignItems(Alignment.END);
        return functionalPanel;
    }

    private Component createEditButtons(Environment environment, EnvironmentRepository environmentRepository) {
        HorizontalLayout buttons = new HorizontalLayout();

        Icon editIcon = VaadinIcon.EDIT.create();
        editIcon.setColor("purple");
        Button editButton = new Button(editIcon);

        Icon applyIcon = VaadinIcon.CHECK.create();
        applyIcon.setColor("green");
        Button applyButton = new Button(applyIcon);

        Icon deleteIcon = VaadinIcon.CLOSE_SMALL.create();
        deleteIcon.setColor("red");
        Button deleteButton = new Button(deleteIcon);

        applyButton.setEnabled(buttonsState.isApplyEnabled());
        editButton.setEnabled(buttonsState.isEditEnabled());

        editButton.addClickListener(event -> {

            applyButton.setEnabled(buttonsState.enableAndGetApply());
            editButton.setEnabled(buttonsState.disableAndGetEdit());

            Editor<Environment> editor = grid.getEditor();
            if (editor.isOpen()) {
                editor.cancel();
            }
            editor.editItem(environment);
        });

        applyButton.addClickListener(event -> {
            applyButton.setEnabled(buttonsState.disableAndGetApply());
            editButton.setEnabled(buttonsState.enableAndGetEdit());

            Editor<Environment> editor = grid.getEditor();
            editor.save();
            environmentRepository.save(editor.getItem());
            grid.getEditor().closeEditor();

            UI.getCurrent().push();
            grid.setItems(findEnvironments(environmentRepository));
        });

        deleteButton.addClickListener(event -> {
            if (!environment.getProfiles().isEmpty()) {
                environmentProfileRepository.deleteAll(environment.getProfiles());
            }
            if (!environment.getComponents().isEmpty()) {
                componentEnvironmentRepository.deleteAll(environment.getComponents());
            }
            environmentRepository.findById(environment.getId()).ifPresent(environmentRepository::delete);
            UI.getCurrent().push();
            grid.setItems(findEnvironments(environmentRepository));
        });

        buttons.add(editButton, applyButton, deleteButton);

        buttons.setJustifyContentMode(JustifyContentMode.CENTER);
        buttons.setWidthFull();

        return buttons;
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        UI.getCurrent().getPage().setTitle(getPageTitle());
        tabs.setSelectedTab(environmentTab);
    }

    @Override
    public void localeChange(LocaleChangeEvent event) {
        super.localeChange(event);
        UI.getCurrent().getPage().setTitle(getPageTitle());
        localize(localizables);
        grid.getDataProvider().refreshAll();
    }

    @Override
    public String getPageTitle() {
        return getTranslation("main.logo.title.environments");
    }
}