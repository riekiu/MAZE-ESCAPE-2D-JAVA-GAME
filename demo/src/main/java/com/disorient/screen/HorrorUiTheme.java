package com.disorient.screen;

import com.disorient.manager.AudioManager;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

final class HorrorUiTheme {

    private HorrorUiTheme() {
    }

    static void applyBackdrop(StackPane root) {
        var background = HorrorUiTheme.class.getResource("/img/menu/background.jpg");
        if (background != null) {
            Image image = new Image(background.toExternalForm(), false);
            root.setBackground(new Background(new BackgroundImage(
                    image,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.CENTER,
                    new BackgroundSize(100, 100, true, true, false, true))));
        } else {
            root.setStyle("-fx-background-color: linear-gradient(to bottom right, #08050e, #21112c, #08050e);");
        }
        Rectangle tint = new Rectangle();
        tint.widthProperty().bind(root.widthProperty());
        tint.heightProperty().bind(root.heightProperty());
        tint.setFill(Color.rgb(7, 4, 13, 0.72));
        tint.setMouseTransparent(true);
        root.getChildren().add(0, tint);
    }

    static void styleButton(Button button, AudioManager audioManager) {
        String base = "-fx-background-color: linear-gradient(to bottom, #372b40, #17121d);"
                + "-fx-border-color: #a9875d;-fx-border-width: 1.4px;-fx-border-radius: 6px;"
                + "-fx-background-radius: 6px;-fx-text-fill: #f2e5ce;-fx-font-family: Georgia;"
                + "-fx-font-size: 11px;-fx-font-weight: bold;-fx-letter-spacing: 1.2px;"
                + "-fx-padding: 11px 18px;-fx-cursor: hand;";
        button.setStyle(base);
        button.setOnMousePressed(event -> audioManager.playClick());
        button.setOnMouseEntered(event -> button.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #56365f, #271a31);"
                        + "-fx-border-color: #e5c488;-fx-border-width: 1.8px;-fx-border-radius: 6px;"
                        + "-fx-background-radius: 6px;-fx-text-fill: #fff3dc;-fx-font-family: Georgia;"
                        + "-fx-font-size: 11px;-fx-font-weight: bold;-fx-letter-spacing: 1.2px;"
                        + "-fx-padding: 11px 18px;-fx-cursor: hand;"
                        + "-fx-effect: dropshadow(gaussian, #9a55c4, 14, 0.32, 0, 0);"));
        button.setOnMouseExited(event -> button.setStyle(base));
    }
}
