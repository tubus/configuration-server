package com.github.tubus.ui.view.config.tab;

import com.github.tubus.ui.data.dto.env.Environment;
import com.github.tubus.ui.data.repo.EnvironmentRepository;
import com.github.tubus.ui.view.config.ConfigurationServer;
import com.github.tubus.ui.view.main.MainView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.*;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

import javax.annotation.Resource;
import java.util.List;

@Route(value = "configserver/setup", layout = MainView.class)
@SpringComponent @UIScope
public class ConfigSetup extends ConfigurationServer implements AfterNavigationObserver, HasDynamicTitle, LocaleChangeObserver {

    @Resource
    private EnvironmentRepository environmentRepository;

    public ConfigSetup() {
        H1 h1 = new H1();
        h1.setText("Страница ещё не создана");
        Button magicButton = new Button("Магическая кнопка");
        magicButton.addClickListener(event -> {
            List<Environment> all = environmentRepository.findAll();
            System.out.println(all);
        });
        add(h1, magicButton);
    }

    @Override
    public void localeChange(LocaleChangeEvent event) {
        super.localeChange(event);
        UI.getCurrent().getPage().setTitle(getPageTitle());
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        tabs.setSelectedTab(setupTab);
        UI.getCurrent().getPage().setTitle(getPageTitle());
    }

    @Override
    public String getPageTitle() {
        return getTranslation("main.logo.title.settings");
    }
}