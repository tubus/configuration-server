package com.github.tubus.ui.view.main;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;

@Route("")
public class Entry extends Div implements AfterNavigationObserver, RouterLayout {

    public Entry() {
        UI.getCurrent().navigate("configserver/components");
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        UI.getCurrent().navigate("configserver/components");
    }
}