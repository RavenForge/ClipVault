package com.ravenforge.clipvault.component;

import javafx.scene.control.Dialog;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Objects;

public class DialogUtils {
    private static final Image APP_ICON = new Image(Objects.requireNonNull(DialogUtils.class.getResourceAsStream("/safe_128.png")));

    public static void applyAppIcon(Dialog<?> dialog) {
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(APP_ICON);
    }
}
