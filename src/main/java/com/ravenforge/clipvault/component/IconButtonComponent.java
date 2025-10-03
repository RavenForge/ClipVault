package com.ravenforge.clipvault.component;

import javafx.scene.control.Button;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.javafx.FontIcon;

public class IconButtonComponent {

    public static Button createIconButton(Ikon icon, String buttonAccent) {
        Button button = new Button(null, new FontIcon(icon));

        if (buttonAccent != null)
            button.getStyleClass().add(buttonAccent);

        return button;
    }
}
