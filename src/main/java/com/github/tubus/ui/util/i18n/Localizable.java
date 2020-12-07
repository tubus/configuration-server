package com.github.tubus.ui.util.i18n;

import com.vaadin.flow.component.UI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import javax.validation.constraints.NotNull;
import java.util.function.Consumer;

/**
 * Wrapper for convenient localizable objects storage
 *
 * @author mujum
 */
@Validated @Slf4j
public class Localizable {

    private final Consumer<String> translationSetter;
    private final String resourceKey;

    public Localizable(@NotNull final Consumer<String> translationSetter, @NotNull final String resourceKey) {

        this.translationSetter = translationSetter;
        this.resourceKey = resourceKey;
    }

    public void localize() {
        String translation = UI.getCurrent().getTranslation(resourceKey);
        try {
            translationSetter.accept(translation);
        } catch (Exception exception) {
            log.error("Translation error", exception);
        }
    }
}