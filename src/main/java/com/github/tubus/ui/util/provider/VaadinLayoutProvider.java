package com.github.tubus.ui.util.provider;

import com.github.tubus.ui.util.constant.CssStyleId;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.i18n.I18NProvider;

import static com.github.tubus.ui.util.constant.CssStyleId.COMMON_HEADER;
import static com.github.tubus.ui.util.constant.CssStyleId.LOGO;
import static com.github.tubus.ui.util.provider.VaadinFieldsProvider.provideLanguageCombobox;

/**
 * Provider of vaadin layouts
 */
public final class VaadinLayoutProvider {

    private VaadinLayoutProvider() {
    }

    /**
     * Provide grid and setUp containing layout for grid
     * @param component - Component to contain grid
     * @param valueProvider - Grid main column value provider
     * @param <T> - Grid value type
     * @return TreeGrid
     */
    public static <T> TreeGrid<T> provideGridAndSetupLayout(VerticalLayout component,
                                                            ValueProvider<T, Component> valueProvider) {
        final TreeGrid<T> treeGrid = new TreeGrid<>();
        component.setSizeFull();
        component.setEnabled(true);
        component.setAlignItems(FlexComponent.Alignment.CENTER);
        component.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        treeGrid.addComponentHierarchyColumn(valueProvider);
        treeGrid.setId(CssStyleId.TREE_GRID_CONFIGURATION);
        treeGrid.setSelectionMode(Grid.SelectionMode.NONE);
        treeGrid.setDetailsVisibleOnClick(false);
        treeGrid.setColumnReorderingAllowed(false);
        treeGrid.setVerticalScrollingEnabled(true);
        treeGrid.setRowsDraggable(false);

        component.add(treeGrid);
        return treeGrid;
    }

    /**
     * Provider of headers for main view
     * @param menuBar - Menu Bar In header
     * @return Component header
     */
    public static Component provideMainHeader(Component menuBar, I18NProvider i18NProvider) {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidthFull();
        layout.setId(COMMON_HEADER);
        layout.setWidthFull();
        layout.setSpacing(false);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.add(new DrawerToggle());

        HorizontalLayout userLayout = new HorizontalLayout();
        userLayout.setAlignItems(FlexComponent.Alignment.BASELINE);
        userLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        userLayout.add(provideLanguageCombobox(i18NProvider));
        userLayout.add(menuBar);
        layout.add(userLayout);
        layout.expand(userLayout);
        return layout;
    }

    /**
     * Provider of main view drawer
     * @param menu - Tabs menu
     * @return Component
     */
    public static Component provideDrawer(Tabs menu, H1 drawerHeader) {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setPadding(false);
        layout.setSpacing(false);
        layout.getThemeList().set("spacing-s", true);
        layout.setAlignItems(FlexComponent.Alignment.STRETCH);
        HorizontalLayout logoLayout = new HorizontalLayout();
        logoLayout.setId(LOGO);
        logoLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        VerticalLayout vert = new VerticalLayout();
        Image logoImage = new Image("icons/icon.svg", "");
        logoImage.setWidthFull();
        logoImage.setHeightFull();
        vert.add(logoImage);
        drawerHeader.setSizeFull();
        vert.addAndExpand(drawerHeader);
        logoLayout.add(vert);
        layout.add(logoLayout, menu);
        return layout;
    }
}