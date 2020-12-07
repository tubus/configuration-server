package com.github.tubus.ui.data.dto.state;

import lombok.Data;

@Data
public class ButtonsState {

    private boolean editEnabled = true;
    private boolean applyEnabled = true;

    public ButtonsState(boolean editEnabled, boolean applyEnabled) {
        this.editEnabled = editEnabled;
        this.applyEnabled = applyEnabled;
    }

    public boolean disableAndGetEdit() {
        editEnabled = false;
        return false;
    }

    public boolean enableAndGetEdit() {
        editEnabled = true;
        return true;
    }

    public boolean disableAndGetApply() {
        applyEnabled = false;
        return false;
    }

    public boolean enableAndGetApply() {
        applyEnabled = true;
        return true;
    }
}