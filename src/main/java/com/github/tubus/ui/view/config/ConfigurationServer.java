package com.github.tubus.ui.view.config;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.*;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import com.github.tubus.ui.view.config.tab.ConfigComponent;
import com.github.tubus.ui.view.config.tab.ConfigEnvironment;
import com.github.tubus.ui.view.config.tab.ConfigSetup;
import com.github.tubus.ui.view.main.MainView;

import java.util.HashMap;
import java.util.Map;

@Route(value = "configserver", layout = MainView.class)
@PageTitle("main.logo.title")
@CssImport(value = "./styles/views/configserver/configserver-view.css", include = "lumo-badge")
@JsModule("@vaadin/vaadin-lumo-styles/badge.js")
@SpringComponent @UIScope
public class ConfigurationServer extends VerticalLayout implements AfterNavigationObserver, RouterLayout,
        LocaleChangeObserver {

    protected final Tabs tabs = new Tabs();

    protected Tab environmentTab;
    protected Tab configTab;
    protected Tab setupTab;

    private final Map<String, RouterLink> innerRouterLinks = new HashMap<>();

    public ConfigurationServer() {
        VerticalLayout body = new VerticalLayout();
        body.setAlignItems(Alignment.STRETCH);
        body.add(createMenu());
        add(body);
        UI.getCurrent().navigate("configserver/components");
    }
    private Tabs createMenu() {
        tabs.setOrientation(Tabs.Orientation.HORIZONTAL);
        tabs.addThemeVariants(TabsVariant.LUMO_MINIMAL);
        tabs.setId("configTabs");
        tabs.add(createMenuItems());
        tabs.setAutoselect(true);
        return tabs;
    }

    private Component[] createMenuItems() {
        innerRouterLinks.clear();
        configTab = createTab("main.logo.title.components.tab", ConfigComponent.class);
        environmentTab = createTab("main.logo.title.environments.tab", ConfigEnvironment.class);
        setupTab = createTab("main.logo.title.settings.tab", ConfigSetup.class);
        return new Tab[] {
                configTab,
                environmentTab,
                setupTab
        };
    }

    private Tab createTab(String text, Class<? extends Component> navigationTarget) {
        final Tab tab = new Tab();
        RouterLink routerLink = new RouterLink(getTranslation(text), navigationTarget);
        innerRouterLinks.put(text, routerLink);
        tab.add(routerLink);
        ComponentUtil.setData(tab, Class.class, navigationTarget);
        return tab;
    }

    @Override
    public void localeChange(LocaleChangeEvent event) {
        innerRouterLinks.forEach((key, router) -> router.setText(getTranslation(key)));
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        innerRouterLinks.forEach((key, router) -> router.setText(getTranslation(key)));
        UI.getCurrent().navigate("configserver/components");
    }
}
