package com.ravenforge.clipvault.component;

import com.ravenforge.clipvault.model.Tab;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.GridPane;

import java.util.function.BiConsumer;

public class PasswordDialogComponent {

    public static void createPasswordDialogComponent(BiConsumer<Tab, String> openSecretTabConsumer, Tab tab) {
        Dialog<String> dialog = new Dialog<>();
        DialogUtils.applyAppIcon(dialog);
        dialog.setTitle("Open Secret Tab: " + tab.getName());

        ButtonType okButtonType = new ButtonType("OK", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, okButtonType);

        PasswordField secret = new PasswordField();
        secret.setPromptText("(password here)");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 80, 10, 80));

        grid.add(new Label("Password:"), 0, 0);
        grid.add(secret, 1, 0);
        grid.setAlignment(Pos.CENTER);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                openSecretTabConsumer.accept(tab, secret.getText());
            }
            return null;
        });

        dialog.showAndWait();
    }
}
