package com.github.tubus.ui.data.exception;

import java.util.ArrayList;
import java.util.List;

/**
 * Exception specific for this module
 */
public abstract class InternalConfigurationServerException extends RuntimeException {

    protected final List<String> details = new ArrayList<>();

    public InternalConfigurationServerException() {
        details.add("Ошибка заполнения поля");
    }

    public InternalConfigurationServerException(String detail) {
        this.details.add(detail);
    }

    public InternalConfigurationServerException(List<String> details) {
        this.details.addAll(details);
    }

    /**
     * Details messages for ui notifications
     *
     * @return List<String> - list of messages
     */
    public List<String> getDetails() {
        return details;
    }
}