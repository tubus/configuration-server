package com.github.tubus.ui.util;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import org.apache.commons.lang3.StringUtils;
import org.passay.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Field validation utils
 */
public final class FieldValuesValidator {

    private FieldValuesValidator() {
    }

    /**
     * Validate name
     * @param name - Name input
     * @param errorsDetected - Boolean flag wrapper
     */
    public static void validateName(TextField name, AtomicBoolean errorsDetected) {
        if (StringUtils.isBlank(name.getValue())) {
            Notification.show("Имя не задано", 1500, Notification.Position.MIDDLE);
            name.setErrorMessage("Имя не задано");
            name.setInvalid(true);
            errorsDetected.set(true);
        }
    }

    /**
     * Validate password
     * @param password - Password input
     * @param errorsDetected - Boolean flag wrapper
     */
    public static void validatePassword(PasswordField password, AtomicBoolean errorsDetected) {
        List<String> valid = getValidationErrors(password.getValue());
        if (!valid.isEmpty()) {
            valid.forEach(message -> {
                Notification.show(message, 1500, Notification.Position.MIDDLE);
            });
            password.setErrorMessage(String.join("\n", valid));
            password.setInvalid(true);
            errorsDetected.set(true);
        }
    }

    private static List<String> getValidationErrors(String password) {
        PasswordValidator validator = new PasswordValidator(Arrays.asList(
                new LengthRule(8, 30),
                new WhitespaceRule()));

        RuleResult result = validator.validate(new PasswordData(password));
        if (result.isValid()) {
            return Collections.emptyList();
        }

        return validator.getMessages(result);
    }
}