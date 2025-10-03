package com.ravenforge.clipvault.component;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

import java.util.function.BiConsumer;

public class NewTabDialogComponent {

    public static void createNewTabDialog(BiConsumer<String, String> newTabConsumer) {
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        DialogUtils.applyAppIcon(dialog);
        dialog.setTitle("Create New Tab");

        ButtonType okButtonType = new ButtonType("OK", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, okButtonType);

        TextField name = new TextField();
        name.setPromptText("Tab Name");

        TextField secret = new TextField();
        secret.setPromptText("(Optional)");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 80, 10, 80));

        grid.add(new Label("Tab Name:"), 0, 0);
        grid.add(name, 1, 0);
        grid.add(new Label("Tab Password:"), 0, 1);
        grid.add(secret, 1, 1);
        grid.setAlignment(Pos.CENTER);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                newTabConsumer.accept(name.getText(), secret.getText());
            }
            return null;
        });

        dialog.showAndWait();
    }
}
