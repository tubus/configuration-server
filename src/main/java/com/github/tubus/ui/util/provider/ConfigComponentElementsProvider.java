package com.github.tubus.ui.util.provider;

import com.github.tubus.ui.data.dto.env.Environment;
import com.github.tubus.ui.data.dto.env.EnvironmentProfile;
import com.github.tubus.ui.data.repo.EnvironmentRepository;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.i18n.I18NProvider;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Утилиты генерации элементов для веб-элементов Конфигурации компонентов
 */
public final class ConfigComponentElementsProvider {

    private ConfigComponentElementsProvider() {
    }

    public static <T> Grid<T> provideBaseGrid(List<T> items,
                                              ValueProvider<T, ? extends Component> componentProvider) {
        Grid<T> grid = new Grid<>();

        grid.setHeight("100%");
        grid.setWidthFull();
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_NO_ROW_BORDERS);

        grid.addComponentColumn(componentProvider);
        grid.setVerticalScrollingEnabled(true);

        grid.setSelectionMode(Grid.SelectionMode.NONE);
        grid.setItems(items);
        grid.setVisible(true);

        return grid;
    }

    public static ComboBox<Environment> createEnvironmentsBox(EnvironmentRepository environmentRepository,
            @Nullable com.github.tubus.ui.data.dto.component.Component component,
                                                              I18NProvider i18NProvider) {
        List<Environment> availableEnvironments = environmentRepository.findAll().stream()
                .filter(env -> component == null ||
                               component.getEnvironments() == null ||
                              !component.getEnvironments().contains(env))
                .collect(Collectors.toList());
        return createEnvironmentComboBox(availableEnvironments, i18NProvider);
    }

    public static ComboBox<Environment> createOwnEnvironmentsBox(EnvironmentRepository environmentRepository,
            @Nullable com.github.tubus.ui.data.dto.component.Component component,
            I18NProvider i18NProvider) {
        List<Environment> availableEnvironments = environmentRepository.findAll().stream()
                .filter(env -> component == null ||
                        component.getEnvironments() == null ||
                        component.getEnvironments().contains(env))
                .collect(Collectors.toList());
        return createEnvironmentComboBox(availableEnvironments, i18NProvider);
    }

    private static ComboBox<Environment> createEnvironmentComboBox(List<Environment> availableEnvironments,
                                                                   I18NProvider i18NProvider) {
        ComboBox<Environment> addEnvironmentBox = new ComboBox<>(i18NProvider
                .getTranslation("input.combobox.environment", UI.getCurrent().getLocale()), availableEnvironments);
        addEnvironmentBox.setClearButtonVisible(true);
        addEnvironmentBox.setItemLabelGenerator(Environment::getName);

        if (availableEnvironments.size() == 0) {
            addEnvironmentBox.setPlaceholder(i18NProvider.getTranslation("input.combobox.environment.no",
                    UI.getCurrent().getLocale()));
        }

        return addEnvironmentBox;
    }

    public static ComboBox<EnvironmentProfile> createEnvironmentProfilesBox(Environment environment, I18NProvider i18NProvider) {
        List<EnvironmentProfile> profiles = environment.getProfiles();
        ComboBox<EnvironmentProfile> addEnvironmentBox = new ComboBox<>(i18NProvider
                .getTranslation("input.combobox.profile", UI.getCurrent().getLocale()), profiles);
        addEnvironmentBox.setClearButtonVisible(true);
        addEnvironmentBox.setItemLabelGenerator(EnvironmentProfile::getName);

        if (profiles.size() == 0) {
            addEnvironmentBox.setPlaceholder(i18NProvider.getTranslation("input.combobox.environment.no",
                    UI.getCurrent().getLocale()));
        }

        return addEnvironmentBox;
    }
}