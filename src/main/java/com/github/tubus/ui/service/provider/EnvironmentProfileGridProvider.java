package com.github.tubus.ui.service.provider;

import com.github.tubus.ui.data.dto.env.Environment;
import com.github.tubus.ui.data.dto.env.EnvironmentProfile;
import com.github.tubus.ui.data.dto.state.ButtonsState;
import com.github.tubus.ui.data.repo.EnvironmentProfileRepository;
import com.github.tubus.ui.data.repo.EnvironmentRepository;
import com.github.tubus.ui.util.i18n.Localizable;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.function.ValueProvider;

import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;


public class EnvironmentProfileGridProvider implements ValueProvider<Environment, Grid<EnvironmentProfile>> {

    private static final long serialVersionUID = 9038113446929480959L;

    private final EnvironmentRepository environmentRepository;
    private final Grid<Environment> externalGrid;
    private final EnvironmentProfileRepository environmentProfileRepository;

    private final ButtonsState buttonsState = new ButtonsState(true, false);

    public EnvironmentProfileGridProvider(final EnvironmentProfileRepository environmentProfileRepository,
                                          final EnvironmentRepository environmentRepository,
                                          final Grid<Environment> externalGrid) {
        this.environmentProfileRepository = environmentProfileRepository;
        this.environmentRepository = environmentRepository;
        this.externalGrid = externalGrid;
    }

    @Override
    public Grid<EnvironmentProfile> apply(Environment environment) {
        Grid<EnvironmentProfile> grid = new Grid<>();

        grid.getEditor().setBinder(new BeanValidationBinder<>(EnvironmentProfile.class));

        TextField nameEditor = new TextField();

        grid.addColumn(EnvironmentProfile::getName)
                .setHeader(UI.getCurrent().getTranslation("span.table.column.name"))
                .setSortable(true)
                .setEditorComponent(nameEditor);

        grid.addComponentColumn(env -> createEditButtons(env, environmentProfileRepository, grid))
                .setKey("environmentProfileGridEditButtons")
                .setWidth("55%")
                .setHeader(createCreationButton(environment, environmentProfileRepository));

        grid.getEditor().getBinder().forField(nameEditor).bind(EnvironmentProfile::getName, EnvironmentProfile::setName);

        grid.setItems(environmentProfileRepository.findAllByEnvironment(environment));
        grid.setPageSize(environment.getProfiles().size() + 1);
        grid.setHeightByRows(true);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_NO_ROW_BORDERS);
        grid.setWidthFull();
        grid.setSelectionMode(Grid.SelectionMode.NONE);
        grid.setRowsDraggable(false);

        return grid;
    }


    private Button createCreationButton(Environment environment,
                                        EnvironmentProfileRepository environmentProfileRepository) {
        Icon createEnvironmentProfileIcon = VaadinIcon.PLUS.create();
        createEnvironmentProfileIcon.setColor("green");
        Button button = new Button(UI.getCurrent().getTranslation("input.create"), createEnvironmentProfileIcon);
        Localizable inputCreate = new Localizable(button::setText, "input.create");

        button.addClickListener(event -> {
            EnvironmentProfile environmentProfile = new EnvironmentProfile();
            environmentProfile.setEnvironment(environment);
            String name = UI.getCurrent().getTranslation("span.new.profile");
            int index = 1;
            while (environmentProfileRepository.findOneByNameEqualsAndEnvironmentEquals(name, environment).isPresent()) {
                name = UI.getCurrent().getTranslation("span.new.profile") + " " + index;
                index++;
            }
            environmentProfile.setName(name);
            environmentProfileRepository.save(environmentProfile);

            externalGrid.setItems(environmentRepository.findAll());
        });

        return button;
    }

    private Component createEditButtons(EnvironmentProfile environment,
                                        EnvironmentProfileRepository environmentProfileRepository,
                                        Grid<EnvironmentProfile> grid) {
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

            Editor<EnvironmentProfile> editor = grid.getEditor();
            if (editor.isOpen()) {
                editor.cancel();
            }
            editor.editItem(environment);
        });

        applyButton.addClickListener(event -> {
            applyButton.setEnabled(buttonsState.disableAndGetApply());
            editButton.setEnabled(buttonsState.enableAndGetEdit());

            Editor<EnvironmentProfile> editor = grid.getEditor();
            editor.save();
            environmentProfileRepository.save(editor.getItem());
            grid.getEditor().closeEditor();

            grid.setHeightByRows(true);
            externalGrid.setItems(environmentRepository.findAll());
        });

        deleteButton.addClickListener(event -> {
            environmentProfileRepository.delete(environment);
            grid.setHeightByRows(true);
            externalGrid.setItems(environmentRepository.findAll());
        });

        buttons.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        buttons.add(editButton, applyButton, deleteButton);
        buttons.setWidthFull();

        return buttons;
    }
}