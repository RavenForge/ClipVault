package com.ravenforge.clipvault.component;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Window;

public class ConfirmationDialogComponent {

    public static boolean createConfirmationDialog(Window window, String title, String message) {
        var alert = new Alert(Alert.AlertType.CONFIRMATION);
        DialogUtils.applyAppIcon(alert);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(window);

        ButtonType noBtn = new ButtonType("No", ButtonBar.ButtonData.NO);
        ButtonType yesBtn = new ButtonType("Yes", ButtonBar.ButtonData.YES);

        alert.getButtonTypes().clear();
        alert.getButtonTypes().addAll(noBtn, yesBtn);
        var result = alert.showAndWait();

        return result.isPresent() && result.get().equals(yesBtn);
    }
}
