package com.mazeescape.screen;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

public class LoadingScreen extends StackPane {

    public LoadingScreen(Runnable onFinished) {

        // =========================
        // BACKGROUND
        // =========================
        setStyle(
                "-fx-background-color: linear-gradient("
                + "to bottom, #09000f, #16001f, #050008);"
        );

        // =========================
        // TITLE
        // =========================
        Label title = new Label("MAZE ESCAPE");

        title.setStyle(
                "-fx-text-fill: #ffffff;"
                + "-fx-font-size: 42px;"
                + "-fx-font-weight: bold;"
                + "-fx-letter-spacing: 6px;"
        );

        // =========================
        // LOADING TEXT
        // =========================
        Label loadingText = new Label("ENTERING THE HAUNTED HOUSE...");

        loadingText.setStyle(
                "-fx-text-fill: #b99acb;"
                + "-fx-font-size: 14px;"
                + "-fx-letter-spacing: 2px;"
        );

        // =========================
        // LOADING DOTS
        // =========================
        Circle dot1 = createDot();
        Circle dot2 = createDot();
        Circle dot3 = createDot();

        VBox content = new VBox(
                18,
                title,
                loadingText,
                dot1,
                dot2,
                dot3
        );

        content.setAlignment(Pos.CENTER);

        getChildren().add(content);

        // =========================
        // TITLE FADE
        // =========================
        FadeTransition fadeIn
                = new FadeTransition(
                        Duration.seconds(1.5),
                        content
                );

        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        fadeIn.play();

        // =========================
        // LOADING DELAY
        // =========================
        PauseTransition pause
                = new PauseTransition(
                        Duration.seconds(2.5)
                );

        pause.setOnFinished(event -> {

            if (onFinished != null) {
                onFinished.run();
            }

        });

        pause.play();
    }

    // =========================
    // CREATE LOADING DOT
    // =========================
    private Circle createDot() {

        Circle dot = new Circle(5);

        dot.setFill(Color.web("#c77dff"));

        FadeTransition animation
                = new FadeTransition(
                        Duration.seconds(0.8),
                        dot
                );

        animation.setFromValue(0.2);
        animation.setToValue(1.0);

        animation.setAutoReverse(true);
        animation.setCycleCount(
                FadeTransition.INDEFINITE
        );

        animation.play();

        return dot;
    }
}
