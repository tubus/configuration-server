package com.github.tubus.ui.util.provider;

import ch.carnet.kasparscherrer.LanguageSelect;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.textfield.Autocomplete;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.i18n.I18NProvider;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * Provider of vaadin input fields
 *
 * @author mujum
 */
public final class VaadinFieldsProvider {

    private VaadinFieldsProvider() {
    }

    /**
     * Provide vaadin text input for search
     * @return TextField - input
     */
    public static TextField provideSearchField() {
        TextField search = new TextField();
        search.setClearButtonVisible(true);
        search.setPlaceholder("Поиск");
        return search;
    }

    /**
     * Provide vaadin name input
     * @return TextField - input
     */
    public static TextField provideNameInputField(I18NProvider i18NProvider, Locale locale) {
        TextField name = new TextField();
        name.setTitle(i18NProvider.getTranslation("input.name.title", locale));
        name.setRequired(true);
        name.setClearButtonVisible(true);
        name.setPlaceholder(i18NProvider.getTranslation("input.name.placeholder", locale));
        name.setAutocomplete(Autocomplete.NAME);
        return name;
    }

    /**
     * Provide vaadin password input
     * @return PasswordField - input
     */
    public static PasswordField providePasswordField(I18NProvider i18NProvider, Locale locale) {
        PasswordField password = new PasswordField();
        password.setTitle(i18NProvider.getTranslation("input.password.title", locale));
        password.setPreventInvalidInput(true);
        password.setRequired(true);
        password.setAutocomplete(Autocomplete.NEW_PASSWORD);
        password.setClearButtonVisible(true);
        password.setPlaceholder(i18NProvider.getTranslation("input.password.placeholder", locale));
        return password;
    }

    /**
     * Language select combobox provider
     * @param i18NProvider - i18N-provider Service
     * @return LanguageSelect Vaadin Component
     */
    public static LanguageSelect provideLanguageCombobox(I18NProvider i18NProvider) {
        LanguageSelect select = new LanguageSelect(true,
                i18NProvider.getProvidedLocales().toArray(new Locale[0]));
        select.setId("language-select");
        final Locale locale = UI.getCurrent().getLocale();
        Optional<Locale> theSameLocale = i18NProvider.getProvidedLocales().stream()
                .filter(pl -> Objects.equals(locale, pl)).findAny();
        if (theSameLocale.isPresent()) {
            select.setValue(locale);
        } else {
            Optional<Locale> theSameLanguageLocale = i18NProvider.getProvidedLocales().stream()
                    .filter(pl -> Objects.equals(locale.getLanguage(), pl.getLanguage())).findAny();
            if (theSameLanguageLocale.isPresent()) {
                select.setValue(theSameLanguageLocale.get());
            } else {
                select.setValue(i18NProvider.getProvidedLocales().get(0));
            }
        }

        return select;
    }
}
