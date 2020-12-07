package com.github.tubus.ui.data.exception;

public class DuplicateSuperAdministratorForbidden extends InternalConfigurationServerException {

    public DuplicateSuperAdministratorForbidden() {
        super("Зафиксирована попытка создать суперпользователя");
    }
}