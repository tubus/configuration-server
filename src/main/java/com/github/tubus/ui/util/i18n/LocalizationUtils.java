package com.github.tubus.ui.util.i18n;

import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * Localization utils for i18n
 *
 * @author mujum
 */
public final class LocalizationUtils {

    private static final int DEFAULT_LETTER_PIXELS = 20;

    private LocalizationUtils() {
    }

    /**
     * Batch localization
     * @param localizables - list of localizables
     */
    public static void localize(List<Localizable> localizables) {
        localizables.forEach(Localizable::localize);
    }

    /**
     * Create localization, apply translation add to list and return corresponding object
     * @param object - Object for localization
     * @param translationSetter - method to apply localization to object
     * @param localizationKey - Resource bundle key
     * @param localizables - list of localizables
     * @param <T> - Type of localizable object
     * @return object
     */
    public static <T> T createLocalization(@NotNull final T object, @NotNull final BiConsumer<T, String> translationSetter,
                                           @NotNull final String localizationKey, @NotNull final List<Localizable> localizables) {
        translationSetter.accept(object, UI.getCurrent().getTranslation(localizationKey));
        Localizable localizable = new Localizable((name) -> translationSetter.accept(object, name), localizationKey);
        localizables.add(localizable);
        return object;
    }

    /**
     * Create localization, apply translation add to list and return corresponding object
     * @param object - Object for localization
     * @param translationSetter - method to apply localization to object
     * @param localizationKey - Resource bundle key
     * @param localizables - list of localizables
     * @param <T> - Type of localizable object
     * @return object
     */
    public static <T extends HasSize> T createScalableLocalization(
            @NotNull final T object, @NotNull final BiConsumer<T, String> translationSetter,
            @NotNull final String localizationKey, @NotNull final List<Localizable> localizables) {
        String name = UI.getCurrent().getTranslation(localizationKey);
        translationSetter.accept(object, name);
        object.setWidth(name.length() * DEFAULT_LETTER_PIXELS, Unit.PIXELS);
        Localizable localizable = new Localizable((newName) -> {
            translationSetter.accept(object, newName);
            object.setWidth(newName.length() * DEFAULT_LETTER_PIXELS, Unit.PIXELS);
        }, localizationKey);
        localizables.add(localizable);
        return object;
    }
}