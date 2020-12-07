package com.github.tubus.ui.view.main;

import com.github.tubus.ui.view.config.ConfigurationServer;
import com.github.tubus.ui.view.wiki.WikiView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.i18n.I18NProvider;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import static com.github.tubus.ui.util.SecurityUtils.isAuthenticatedForUserAdministration;
import static com.github.tubus.ui.util.constant.CssStyleId.TABS;
import static com.github.tubus.ui.util.constant.CssStyleId.USER_MENU_BAR;
import static com.github.tubus.ui.util.provider.VaadinButtonProvider.*;
import static com.github.tubus.ui.util.provider.VaadinLayoutProvider.provideDrawer;
import static com.github.tubus.ui.util.provider.VaadinLayoutProvider.provideMainHeader;

@JsModule("./styles/shared-styles.js")
@CssImport("./styles/views/main/main-view.css")
@CssImport(value = "./styles/views/main/vaadin-context-menu-overlay.css", themeFor = "vaadin-context-menu-overlay")
@CssImport(value = "./styles/views/main/vaadin-combo-box-overlay.css", themeFor = "vaadin-combo-box-overlay")
@CssImport(value = "./styles/views/main/vaadin-combo-box-overlay.css", themeFor = "vaadin-select-overlay")
@CssImport(value = "./styles/views/main/vaadin-select.css", themeFor = "vaadin-select-text-field")
@SpringComponent @UIScope
public class MainView extends AppLayout implements LocaleChangeObserver {

    private final Tabs menu;
    private final H1 drawerHeader;
    private final I18NProvider i18NProvider;

    private Button menuLogoutButton;
    private Button userMenuUserManagement;
    private final Map<String, RouterLink> routerLinks = new HashMap<>();

    public MainView(final I18NProvider i18NProvider) {
        this.i18NProvider = i18NProvider;

        setPrimarySection(Section.DRAWER);
        addToNavbar(true, provideMainHeader(createMenuBar(), i18NProvider));
        menu = createMenu();
        drawerHeader = new H1(menu.getTranslation("main.logo.title"));
        addToDrawer(provideDrawer(menu, drawerHeader));
    }

    private MenuBar createMenuBar() {
        MenuBar bar = new MenuBar();
        bar.setId(USER_MENU_BAR);
        bar.setOpenOnHover(true);

        MenuItem menu = bar.addItem(new Image("images/user.svg", "Menu"));
        SubMenu elements = menu.getSubMenu();

        userMenuUserManagement = provideNavigateButton(getTranslation("input.management.users"), "/");
        if (isAuthenticatedForUserAdministration()) {
            elements.addItem(userMenuUserManagement); // TODO
        }

        menuLogoutButton = provideLogoutButton(i18NProvider);
        elements.addItem(wrap(menuLogoutButton));
        return bar;
    }

    private Tabs createMenu() {
        final Tabs tabs = new Tabs();
        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        tabs.addThemeVariants(TabsVariant.LUMO_MINIMAL);
        tabs.setId(TABS);
        tabs.add(createMenuItems());
        return tabs;
    }

    private Component[] createMenuItems() {
        routerLinks.clear();
        return new Tab[] {
                createTab("main.tab.config.server", ConfigurationServer.class),
                createTab("main.tab.wiki", WikiView.class),
        };
    }

    private Tab createTab(String text, Class<? extends Component> navigationTarget) {
        final Tab tab = new Tab();
        RouterLink routerLink = new RouterLink(text, navigationTarget);
        routerLinks.put(text, routerLink);
        tab.add(routerLink);
        ComponentUtil.setData(tab, Class.class, navigationTarget);
        return tab;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        getTabForComponent(getContent()).ifPresent(menu::setSelectedTab);
    }

    private Optional<Tab> getTabForComponent(Component component) {
        return menu.getChildren()
                .filter(tab -> ComponentUtil.getData(tab, Class.class)
                        .equals(component.getClass()))
                .findFirst().map(Tab.class::cast);
    }

    @Override
    public void localeChange(LocaleChangeEvent event) {
        userMenuUserManagement.setText(getTranslation("input.management.users"));
        menuLogoutButton.setText(getTranslation("input.logout.title"));
        routerLinks.forEach((key, rl) -> rl.setText(getTranslation(key)));
        drawerHeader.setText(getTranslation("main.logo.title"));
    }
}
