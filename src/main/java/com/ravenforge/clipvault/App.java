package com.ravenforge.clipvault;

import atlantafx.base.theme.PrimerDark;
import com.ravenforge.clipvault.view.MainView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.Objects;

public class App extends Application {

    public void start() {
        launch();
    }

    @Override
    public void start(Stage stage) {
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/safe_128.png"))));

        Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());
        MainView root = new MainView();
        Scene scene = new Scene(root, 800, 600);
        stage.setTitle("ClipVault");
        stage.setScene(scene);
        stage.show();

        Label myLabel = new Label("Hello World");
        Font labelFont = myLabel.getFont();

        System.out.println("Font Family: " + labelFont.getFamily());
        System.out.println("Font Name: " + labelFont.getName());
        System.out.println("Font Size: " + labelFont.getSize());
    }
}
