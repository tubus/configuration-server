package com.github.tubus.ui.config;

import ch.carnet.kasparscherrer.LanguageSelect;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import org.springframework.stereotype.Component;

@Component
public class ApplicationServiceInitListener implements VaadinServiceInitListener {

    @Override
    public void serviceInit(ServiceInitEvent event) {
        LanguageSelect.readLanguageCookies(event);
    }
}