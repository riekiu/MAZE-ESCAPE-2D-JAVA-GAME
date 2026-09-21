package com.mazeescape.screen;

import com.mazeescape.manager.ScreenManager;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class PauseMenu extends StackPane {

    public PauseMenu() {
        this(null, null, null, ScreenManager.getActive());
    }

    public PauseMenu(Runnable onResume, Runnable onSettings, ScreenManager screenManager) {
        this(onResume, onSettings, null, screenManager);
    }

    public PauseMenu(
            Runnable onResume,
            Runnable onSettings,
            Runnable onRestart,
            ScreenManager screenManager
    ) {

        // =========================
        // OVERLAY
        // =========================
        setStyle(
                "-fx-background-color: rgba(5, 0, 8, 0.88);"
        );

        // =========================
        // TITLE
        // =========================
        Label title = new Label("PAUSED");

        title.setStyle(
                "-fx-text-fill: white;"
                + "-fx-font-size: 42px;"
                + "-fx-font-weight: bold;"
                + "-fx-letter-spacing: 5px;"
                + "-fx-effect: dropshadow(gaussian, #b84cff, 20, 0.6, 0, 0);"
        );

        Label subtitle = new Label(
                "THE HAUNTED HOUSE AWAITS..."
        );

        subtitle.setStyle(
                "-fx-text-fill: #b99acb;"
                + "-fx-font-size: 13px;"
                + "-fx-letter-spacing: 2px;"
        );

        // =========================
        // RESUME
        // =========================
        Button resumeButton
                = createButton("RESUME");

        resumeButton.setOnAction(event -> {
            if (onResume != null) {
                onResume.run();
            }
        });

        // =========================
        // SETTINGS
        // =========================
        Button settingsButton
                = createButton("SETTINGS");

        settingsButton.setOnAction(event -> {
            if (onSettings != null) {
                onSettings.run();
            } else if (screenManager != null) {
                screenManager.showSettings();
            }
        });

        Button restartButton = createButton("RESTART LEVEL");
        restartButton.setOnAction(event -> {
            if (onRestart != null) {
                onRestart.run();
            }
        });

        // =========================
        // MAIN MENU
        // =========================
        Button mainMenuButton
                = createButton("MAIN MENU");

        mainMenuButton.setOnAction(event -> screenManager.showMainMenu());

        // =========================
        // BUTTONS
        // =========================
        VBox buttons = new VBox(
                10,
                resumeButton,
                restartButton,
                settingsButton,
                mainMenuButton
        );

        buttons.setAlignment(Pos.CENTER);

        // =========================
        // CONTENT
        // =========================
        VBox content = new VBox(
                15,
                title,
                subtitle,
                buttons
        );

        content.setAlignment(Pos.CENTER);

        content.setMaxWidth(400);

        getChildren().add(content);
    }

    // =========================
    // BUTTON CREATOR
    // =========================
    private Button createButton(String text) {

        Button button = new Button(text);
        button.setOnMousePressed(event -> ScreenManager.getActive().getAudioManager().playClick());

        button.setPrefWidth(280);
        button.setPrefHeight(48);

        button.setStyle(
                "-fx-background-color: #170d22;"
                + "-fx-border-color: #6d3a91;"
                + "-fx-border-width: 1.5px;"
                + "-fx-border-radius: 8px;"
                + "-fx-background-radius: 8px;"
                + "-fx-text-fill: white;"
                + "-fx-font-size: 13px;"
                + "-fx-font-weight: bold;"
                + "-fx-letter-spacing: 2px;"
                + "-fx-cursor: hand;"
        );

        button.setOnMouseEntered(event -> {

            button.setStyle(
                    "-fx-background-color: #35104a;"
                    + "-fx-border-color: #b84cff;"
                    + "-fx-border-width: 2px;"
                    + "-fx-border-radius: 8px;"
                    + "-fx-background-radius: 8px;"
                    + "-fx-text-fill: white;"
                    + "-fx-font-size: 13px;"
                    + "-fx-font-weight: bold;"
                    + "-fx-letter-spacing: 2px;"
                    + "-fx-cursor: hand;"
                    + "-fx-effect: dropshadow(gaussian, #b84cff, 15, 0.5, 0, 0);"
            );
        });

        button.setOnMouseExited(event -> {

            button.setStyle(
                    "-fx-background-color: #170d22;"
                    + "-fx-border-color: #6d3a91;"
                    + "-fx-border-width: 1.5px;"
                    + "-fx-border-radius: 8px;"
                    + "-fx-background-radius: 8px;"
                    + "-fx-text-fill: white;"
                    + "-fx-font-size: 13px;"
                    + "-fx-font-weight: bold;"
                    + "-fx-letter-spacing: 2px;"
                    + "-fx-cursor: hand;"
            );
        });

        return button;
    }
}
