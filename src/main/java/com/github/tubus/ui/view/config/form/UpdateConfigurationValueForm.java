package com.github.tubus.ui.view.config.form;

import com.github.tubus.ui.data.dto.component.Component;
import com.github.tubus.ui.data.dto.configuration.ComponentConfiguration;
import com.github.tubus.ui.data.dto.configuration.ConfigurationValue;
import com.github.tubus.ui.data.dto.env.Environment;
import com.github.tubus.ui.data.dto.env.EnvironmentProfile;
import com.github.tubus.ui.data.repo.ComponentConfigurationRepository;
import com.github.tubus.ui.data.repo.ComponentRepository;
import com.github.tubus.ui.data.repo.ConfigurationValueRepository;
import com.github.tubus.ui.data.repo.EnvironmentRepository;
import com.github.tubus.ui.service.provider.ComponentConfigurationProvider;
import com.github.tubus.ui.service.tree.ConfigurationImportServiceImpl;
import com.github.tubus.ui.view.main.MainView;
import com.vaadin.flow.component.HasText;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.router.*;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.transaction.annotation.Transactional;
import java.util.Objects;
import java.util.Optional;

import static com.github.tubus.ui.util.i18n.LocalizationUtils.createLocalization;

@Route(value = "configserver/configurations/values/edit", layout = MainView.class)
@SpringComponent
@UIScope
@Transactional(transactionManager = "gcsTransactionManager")
public class UpdateConfigurationValueForm extends AbstractUpdateForm implements RouterLayout, HasUrlParameter<String> {

    private final ConfigurationValueRepository configurationValueRepository;
    private final EnvironmentRepository environmentRepository;

    private HorizontalLayout header;
    private H4 headerText;

    private Environment environment;
    private EnvironmentProfile environmentProfile;

    public UpdateConfigurationValueForm(final ComponentRepository componentRepository,
                                        final ComponentConfigurationRepository componentConfigurationRepository,
                                        final ConfigurationValueRepository configurationValueRepository,
                                        final EnvironmentRepository environmentRepository,
                                        final ConfigurationImportServiceImpl configurationImportService) {
        super(componentRepository, componentConfigurationRepository, configurationImportService);
        this.configurationValueRepository = configurationValueRepository;
        this.environmentRepository = environmentRepository;

        setUpGrid();
    }

    private void setUpGrid() {
        treeGrid.addColumn(new ComponentRenderer<>((configuration) -> {
            if (!configuration.getConfiguration().isGroup()) {
                TextField input = new TextField();
                Optional<ConfigurationValue> optionalValue = configurationValueRepository
                        .findOne(configuration.getId(), environment.getId(), environmentProfile.getId());
                optionalValue.ifPresent(value -> input.setValue(value.getValue()));
                input.addValueChangeListener(event -> {
                    Optional<ConfigurationValue> optionalCurrentValue = configurationValueRepository
                            .findOne(configuration.getId(), environment.getId(), environmentProfile.getId());
                    if (optionalCurrentValue.isPresent()) {
                        ConfigurationValue currentValue = optionalCurrentValue.get();
                        if (event.getValue() != null) {
                            currentValue.setValue(event.getValue());
                            configurationValueRepository.save(currentValue);
                        } else {
                            configurationValueRepository.delete(currentValue);
                        }
                    } else if (event.getValue() != null) {
                        ConfigurationValue configurationValue = new ConfigurationValue();
                        configurationValue.setComponentConfiguration(configuration);
                        configurationValue.setEnvironment(environment);
                        configurationValue.setEnvironmentProfile(environmentProfile);
                        configurationValue.setValue(event.getValue());
                        configurationValueRepository.save(configurationValue);
                    }
                });

                return input;
            } else {
                return new Span("");
            }
        }));
    }

    @Override
    protected HorizontalLayout createGridItem(ComponentConfiguration configuration) {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setJustifyContentMode(JustifyContentMode.END);
        horizontalLayout.setWidthFull();
        VerticalLayout configurationLine = createConfigurationTitle(configuration);
        horizontalLayout.add(configurationLine);
        return horizontalLayout;
    }

    @Override
    protected HorizontalLayout createGridTreeHeader() {
        header = new HorizontalLayout();
        header.setJustifyContentMode(JustifyContentMode.START);
        header.setWidthFull();

        headerText = createLocalization(new H4(), HasText::setText, "span.configuration.elements", localizables);

        header.add(headerText);

        return header;
    }

    @Override
    public void setParameter(BeforeEvent event, String parameter) {
        String[] parameters = parameter.split(",");
        if (parameters.length != 3) {
            Notification.show(getTranslation("notification.error"), 5000, Notification.Position.MIDDLE);
            UI.getCurrent().navigate("configserver/components");
            return;
        }
        Optional<Component> optionalComponent = componentRepository.findByName(parameters[0]);
        if (optionalComponent.isPresent()) {
            component = optionalComponent.get();
        } else {
            Notification.show(getTranslation("notification.component.not.found"), 5000, Notification.Position.MIDDLE);
            UI.getCurrent().navigate("configserver/components");
            return;
        }

        Optional<Environment> optionalEnvironment = environmentRepository.findByName(parameters[1]);
        if (optionalEnvironment.isPresent()) {
            environment = optionalEnvironment.get();
        } else {
            Notification.show(getTranslation("notification.environment.not.found"), 5000, Notification.Position.MIDDLE);
            UI.getCurrent().navigate("configserver/components");
            return;
        }

        Optional<EnvironmentProfile> optionalEnvironmentProfile = environment.getProfiles().stream()
                .filter(profile -> Objects.equals(profile.getName(), parameters[2])).findAny();
        if (optionalEnvironmentProfile.isPresent()) {
            environmentProfile = optionalEnvironmentProfile.get();
        } else {
            Notification.show(getTranslation("notification.profile.not.found"), 5000, Notification.Position.MIDDLE);
            UI.getCurrent().navigate("configserver/components");
            return;
        }

        H4 newHeader = createCustomHeader();
        header.replace(headerText, newHeader);
        headerText = newHeader;

        setDataProvider();
    }

    private H4 createCustomHeader() {
        H4 header = new H4(getTranslation("span.configuration") + component.getName() + " " +
                getTranslation("span.in.environment") + " \"" +  environment.getName() + "\" " +
                getTranslation("span.with.profile") + " " + environmentProfile.getName());
        header.setWidthFull();
        return header;
    }

    protected void importAction(ConfigurationImportServiceImpl configurationImportService,
                                MemoryBuffer memoryBuffer) {
        //TODO
        Notification.show("To be done", 1500, Notification.Position.MIDDLE);
    }

    @Override
    public String getPageTitle() {
        return getTranslation("main.update.title.configuration");
    }

    @Override
    public void localeChange(LocaleChangeEvent event) {
        super.localeChange(event);
        H4 newHeader = createCustomHeader();
        header.replace(headerText, newHeader);
        headerText = newHeader;
    }

    private void setDataProvider() {
        treeGrid.setDataProvider(new ComponentConfigurationProvider(component, componentConfigurationRepository));
    }
}