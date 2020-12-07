package com.github.tubus.ui.data.dto.state;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Custom gird edit state
 * @param <T> - Grid element
 */
public class GridState <T> {

    private final Map<T, Boolean> editState = new HashMap<>();

    public GridState(Set<T> items) {
        items.forEach(item -> editState.put(item, false));
    }

    public void setEdit(T item) {
        editState.put(item, true);
    }

    public void setNotEdit(T item) {
        editState.put(item, false);
    }

    public boolean isEdit(T item) {
        if (!editState.containsKey(item)) {
            editState.put(item, false);
        }
        return editState.get(item);
    }
}