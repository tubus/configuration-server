package com.github.tubus.ui.data.exception;

import java.util.List;

public class WrongDbStateException extends InternalConfigurationServerException {

    public WrongDbStateException() {
        super("Ошибка хранилища данных");
    }

    public WrongDbStateException(String detail) {
        super(detail);
    }

    public WrongDbStateException(List<String> details) {
        super(details);
    }
}