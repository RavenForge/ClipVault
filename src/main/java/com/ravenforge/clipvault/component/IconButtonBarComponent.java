package com.ravenforge.clipvault.component;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

public class IconButtonBarComponent {

    public static HBox createIconButtonBar(Button... buttons) {
        HBox buttonBox = new HBox(buttons);
        buttonBox.setSpacing(10);
        buttonBox.setAlignment(Pos.CENTER);

        return buttonBox;
    }
}
