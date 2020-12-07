package com.github.tubus.ui.util.provider;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.upload.Receiver;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.i18n.I18NProvider;

import javax.validation.constraints.NotNull;

/**
 * Provider of vaadin button elements and containing layouts
 *
 * @author mujum
 */
public final class VaadinButtonProvider {

    private VaadinButtonProvider() {
    }

    /**
     * Provider of button for adding entity
     * @param name - Visible button name
     * @return Button
     */
    @Deprecated
    public static Button provideAddButton(String name) {
        Button elementAddButton = new Button(name);
        Icon elementAddIcon = VaadinIcon.PLUS_CIRCLE_O.create();
        elementAddIcon.setColor("green");
        elementAddButton.setIcon(elementAddIcon);
        if (name != null) {
            elementAddButton.setWidth(name.length() * 25, Unit.PIXELS);
        }
        return elementAddButton;
    }

    /**
     * Provider of button for adding entity
     * @return Button
     */
    public static Button provideAddButton() {
        Button elementAddButton = new Button();
        Icon elementAddIcon = VaadinIcon.PLUS_CIRCLE_O.create();
        elementAddIcon.setColor("green");
        elementAddButton.setIcon(elementAddIcon);
        elementAddButton.setWidthFull();
        return elementAddButton;
    }

    /**
     * Provider of upload file button
     * @param receiver - Input file reciever
     * @param load - Button for load action
     * @return Upload button
     */
    public static Upload provideUploadButton(Receiver receiver, Button load) {
        Upload upload = new Upload(receiver);
        upload.setDropLabel(new Span(""));
        upload.setUploadButton(load);
        upload.setAcceptedFileTypes(".yml", ".yaml");
        return upload;
    }

    /**
     * Provider of go back button
     * @return Button
     */
    public static Button provideBackButton() {
        Icon backIcon = VaadinIcon.BACKSPACE.create();
        Button backButton = new Button(backIcon);
        backButton.setWidth(250, Unit.PIXELS);
        backButton.addClickListener(event -> UI.getCurrent().getPage()
                .executeJs("window.history.back()"));

        return backButton;
    }

    /**
     * Provider of user logout button
     * @return Button
     */
    public static Button provideLogoutButton(I18NProvider i18NProvider) {
        return provideNavigateButton(i18NProvider.getTranslation("input.logout.title", UI.getCurrent().getLocale()),
                "/logout");
    }

    /**
     * Provider of simple navigation button
     * @param label - Text in button
     * @param target - Navigation Location
     * @return Button
     */
    public static Button provideNavigateButton(String label, String target) {
        Button button = new Button(label);
        button.addClickListener(event -> UI.getCurrent().getPage().setLocation(target));
        button.setSizeFull();
        return button;
    }

    /**
     * Button centered HorizontalLayout-Wrapper for given button
     * @param button - Button to wrap
     * @return HorizontalLayout
     */
    public static HorizontalLayout wrap(@NotNull Button button) {
        HorizontalLayout buttonWrapper = new HorizontalLayout(button);
        buttonWrapper.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        buttonWrapper.setWidthFull();
        button.setSizeFull();
        return buttonWrapper;
    }
}