package com.ravenforge.clipvault.component;

import atlantafx.base.controls.Notification;
import atlantafx.base.theme.Styles;
import atlantafx.base.util.Animations;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.javafx.FontIcon;

public class NotificationComponent {

    public static Double SCROLL_MARGIN = 0d;

    public static void createNotification(ObservableList<Node> componentToAdd, Ikon icon, String message, String messageAccent) {
        final var msg = new Notification(message, new FontIcon(icon));
        msg.getStyleClass().addAll(
                messageAccent,
                Styles.ELEVATED_1
        );
        msg.setPrefHeight(Region.USE_PREF_SIZE);
        msg.setMaxHeight(Region.USE_PREF_SIZE);
        StackPane.setAlignment(msg, Pos.TOP_RIGHT);
        StackPane.setMargin(msg, new Insets(10 + SCROLL_MARGIN.intValue(), 10, 0, 0));

        var in = Animations.slideInDown(msg, Duration.millis(2000));
        if (!componentToAdd.contains(msg)) {
            componentToAdd.add(msg);
        }

        in.setOnFinished(_ -> {
            var out = Animations.slideOutUp(msg, Duration.millis(2000));
            out.setOnFinished(_ -> {
                componentToAdd.remove(msg);
            });
            out.playFromStart();
        });
        in.playFromStart();
    }
}
