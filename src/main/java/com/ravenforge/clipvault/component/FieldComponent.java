package com.ravenforge.clipvault.component;

import javafx.beans.value.ChangeListener;
import javafx.scene.control.TextArea;

import java.util.HashMap;
import java.util.Map;

public class FieldComponent {

    private static final Map<TextArea, ChangeListener<String>> listeners = new HashMap<>();

    public static TextArea createField(String text) {
        TextArea field = new TextArea(text);
        field.setEditable(false);

        int lines = text.split("\n").length;
        field.setPrefRowCount(Math.min(lines, 10));

        return field;
    }

    public static TextArea createEditableField() {
        TextArea field = new TextArea();
        field.setEditable(true);
        field.setPrefRowCount(1);

        field.textProperty().addListener((_, _, _) -> {
            int lines = field.getText().split("\n").length;
            field.setPrefRowCount(Math.min(lines, 10));
        });

        return field;
    }

    public static void addFieldListener(TextArea field) {
        ChangeListener<String> listener = (_, _, _) -> {
            int lines = field.getText().split("\n").length;
            field.setPrefRowCount(Math.min(lines, 10));
        };
        field.textProperty().addListener(listener);

        listeners.put(field, listener);
    }

    public static void removeFieldListener(TextArea field) {
        var listener = listeners.get(field);

        if (null != listener)
            field.textProperty().removeListener(listener);
    }
}
