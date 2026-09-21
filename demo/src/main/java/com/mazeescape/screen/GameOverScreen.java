package com.mazeescape.screen;

import com.mazeescape.manager.ScreenManager;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class GameOverScreen extends StackPane {

    public GameOverScreen(int level) {
        this(level, ScreenManager.getActive().getGameManager().getLives(), ScreenManager.getActive());
    }

    public GameOverScreen(int level, ScreenManager screenManager) {
        this(level, screenManager.getGameManager().getLives(), screenManager);
    }

    public GameOverScreen(int level, int remainingLives, ScreenManager screenManager) {

        // =========================
        // BACKGROUND
        // =========================
        setStyle(
                "-fx-background-color: linear-gradient("
                + "to bottom, #050008, #180008, #07000d);"
        );

        // =========================
        // TITLE
        // =========================
        Label title = new Label("GAME OVER");

        title.setStyle(
                "-fx-text-fill: white;"
                + "-fx-font-size: 48px;"
                + "-fx-font-weight: bold;"
                + "-fx-letter-spacing: 5px;"
                + "-fx-effect: dropshadow(gaussian, #ff1744, 20, 0.6, 0, 0);"
        );

        // =========================
        // MESSAGE
        // =========================
        Label message = new Label(
                "THE HAUNTED HOUSE HAS CLAIMED YOU..."
        );

        message.setStyle(
                "-fx-text-fill: #b99acb;"
                + "-fx-font-size: 15px;"
                + "-fx-letter-spacing: 2px;"
        );

        // =========================
        // LEVEL
        // =========================
        Label levelLabel = new Label(
                "LEVEL " + level
        );

        levelLabel.setStyle(
                "-fx-text-fill: #ff8fa3;"
                + "-fx-font-size: 22px;"
                + "-fx-font-weight: bold;"
        );

        Label livesLabel = new Label("LIVES REMAINING: " + createLivesText(remainingLives));
        livesLabel.setStyle(
                "-fx-text-fill: #ff8fa3;"
                + "-fx-font-size: 18px;"
                + "-fx-font-weight: bold;"
                + "-fx-letter-spacing: 1px;"
        );

        // =========================
        // RETRY
        // =========================
        Button retryButton
                = createButton("RETRY");

        retryButton.setOnAction(event -> {
            screenManager.retryLevel(level, remainingLives);
        });

        // =========================
        // CAMPAIGN
        // =========================
        Button campaignButton
                = createButton("CAMPAIGN");

        campaignButton.setOnAction(event -> {
            screenManager.showCampaign();
        });

        // =========================
        // MAIN MENU
        // =========================
        Button mainMenuButton
                = createButton("MAIN MENU");

        mainMenuButton.setOnAction(event -> {
            screenManager.showMainMenu();
        });

        // =========================
        // BUTTONS
        // =========================
        VBox buttons = new VBox(
                12,
                retryButton,
                campaignButton,
                mainMenuButton
        );

        buttons.setAlignment(Pos.CENTER);

        // =========================
        // CONTENT
        // =========================
        VBox content = new VBox(
                16,
                title,
                message,
                levelLabel,
                livesLabel,
                buttons
        );

        content.setAlignment(Pos.CENTER);

        getChildren().add(content);
    }

    private String createLivesText(int remainingLives) {
        StringBuilder text = new StringBuilder();
        for (int index = 0; index < 3; index++) {
            if (index > 0) {
                text.append(' ');
            }
            text.append(index < Math.max(0, remainingLives) ? '♥' : '♡');
        }
        return text.toString();
    }

    // =========================
    // BUTTON CREATOR
    // =========================
    private Button createButton(String text) {

        Button button = new Button(text);
        button.setOnMouseClicked(event -> ScreenManager.getActive().getAudioManager().playClick());

        button.setPrefWidth(280);
        button.setPrefHeight(48);

        button.setStyle(
                "-fx-background-color: #170d22;"
                + "-fx-border-color: #6d3a91;"
                + "-fx-border-width: 1.5px;"
                + "-fx-border-radius: 8px;"
                + "-fx-background-radius: 8px;"
                + "-fx-text-fill: white;"
                + "-fx-font-size: 14px;"
                + "-fx-font-weight: bold;"
                + "-fx-letter-spacing: 2px;"
                + "-fx-cursor: hand;"
        );

        button.setOnMouseEntered(event -> {

            button.setStyle(
                    "-fx-background-color: #35104a;"
                    + "-fx-border-color: #ff477e;"
                    + "-fx-border-width: 2px;"
                    + "-fx-border-radius: 8px;"
                    + "-fx-background-radius: 8px;"
                    + "-fx-text-fill: white;"
                    + "-fx-font-size: 14px;"
                    + "-fx-font-weight: bold;"
                    + "-fx-letter-spacing: 2px;"
                    + "-fx-cursor: hand;"
                    + "-fx-effect: dropshadow(gaussian, #ff1744, 15, 0.5, 0, 0);"
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
                    + "-fx-font-size: 14px;"
                    + "-fx-font-weight: bold;"
                    + "-fx-letter-spacing: 2px;"
                    + "-fx-cursor: hand;"
            );
        });

        return button;
    }
}
