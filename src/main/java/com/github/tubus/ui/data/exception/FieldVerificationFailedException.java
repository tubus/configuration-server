package com.github.tubus.ui.data.exception;

import java.util.List;

public class FieldVerificationFailedException extends InternalConfigurationServerException {

    public FieldVerificationFailedException() {
        super("Ошибка заполнения поля");
    }

    public FieldVerificationFailedException(String detail) {
        super(detail);
    }

    public FieldVerificationFailedException(List<String> details) {
        super(details);
    }
}