package com.mazeescape.screen;

import com.mazeescape.manager.ScreenManager;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class LevelCompleteScreen extends StackPane {

    public LevelCompleteScreen(
            int level,
            int score,
            int remainingTime,
            int remainingLives
    ) {
        this(level, score, remainingTime, remainingLives, ScreenManager.getActive());
    }

    public LevelCompleteScreen(
            int level,
            int score,
            int remainingTime,
            int remainingLives,
            ScreenManager screenManager
    ) {

        // =========================
        // BACKGROUND
        // =========================
        setStyle("-fx-background-color: linear-gradient(to bottom, #050008, #16001f, #07000d);");

        // =========================
        // TITLE
        // =========================
        Label title
                = new Label("LEVEL COMPLETE!");

        title.setStyle(
                "-fx-text-fill: white;"
                + "-fx-font-size: 40px;"
                + "-fx-font-weight: bold;"
                + "-fx-letter-spacing: 4px;"
                + "-fx-effect: dropshadow(gaussian, #8b2cff, 20, 0.6, 0, 0);"
        );

        // =========================
        // LEVEL
        // =========================
        Label levelLabel
                = new Label(String.format("HAUNTED HOUSE • LEVEL %02d", level));

        levelLabel.setStyle(
                "-fx-text-fill: #b99acb;"
                + "-fx-font-size: 16px;"
                + "-fx-letter-spacing: 2px;"
        );

        // =========================
        // SCORE
        // =========================
        Label scoreLabel
                = new Label(String.format("SCORE: %04d", score));

        scoreLabel.setStyle(
                "-fx-text-fill: #ffd166;"
                + "-fx-font-size: 28px;"
                + "-fx-font-weight: bold;"
        );

        // =========================
        // TIME
        // =========================
        Label timeLabel
                = new Label("TIME: " + formatTime(remainingTime));

        timeLabel.setStyle(
                "-fx-text-fill: #8fffc1;"
                + "-fx-font-size: 17px;"
        );

        int completionTime = Math.max(0,
                new com.mazeescape.model.LevelData(level).getTimeLimit() - remainingTime);
        int personalBest = screenManager.getLeaderboardManager().getBestTime(level);
        Label recordLabel = new Label(
                personalBest == completionTime
                        ? "NEW PERSONAL BEST: " + formatTime(completionTime)
                        : "PERSONAL BEST: " + formatTime(personalBest)
        );
        recordLabel.setStyle(
                "-fx-text-fill: #d6a8ff;"
                + "-fx-font-size: 17px;"
                + "-fx-font-weight: bold;"
        );

        // =========================
        // LIVES
        // =========================
        Label livesLabel
                = new Label("LIVES REMAINING: " + createLivesText(remainingLives)
                        + "   •   BONUS: " + (remainingTime * 10 + remainingLives * 250));

        livesLabel.setStyle(
                "-fx-text-fill: #ff8fa3;"
                + "-fx-font-size: 16px;"
                + "-fx-font-weight: bold;"
        );

        // =========================
        // NEXT LEVEL
        // =========================
        Button nextLevelButton
                = createButton("NEXT LEVEL");
        nextLevelButton.setDisable(level >= screenManager.getGameManager().getTotalLevels());

        nextLevelButton.setOnAction(event -> {
            screenManager.startLevel(level + 1);
        });

        // =========================
        // REPLAY
        // =========================
        Button replayButton
                = createButton("REPLAY");

        replayButton.setOnAction(event -> {
            screenManager.retryLevel(level);
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
        // BUTTON CONTAINER
        // =========================
        VBox buttons = new VBox(
                10,
                nextLevelButton,
                replayButton,
                campaignButton,
                mainMenuButton
        );

        buttons.setAlignment(Pos.CENTER);

        // =========================
        // CONTENT
        // =========================
        VBox content = new VBox(
                14,
                title,
                levelLabel,
                new Label("★ ★ ★"),
                scoreLabel,
                timeLabel,
                recordLabel,
                livesLabel,
                buttons
        );

        content.setAlignment(Pos.CENTER);

        getChildren().add(content);
    }

    private String formatTime(int seconds) {
        return String.format("%02d:%02d", Math.max(0, seconds) / 60, Math.max(0, seconds) % 60);
    }

    private String createLivesText(int remainingLives) {
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            if (i > 0) {
                text.append(' ');
            }
            text.append(i < remainingLives ? '♥' : '♡');
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
                    + "-fx-border-color: #b65cff;"
                    + "-fx-border-width: 2px;"
                    + "-fx-border-radius: 8px;"
                    + "-fx-background-radius: 8px;"
                    + "-fx-text-fill: white;"
                    + "-fx-font-size: 14px;"
                    + "-fx-font-weight: bold;"
                    + "-fx-letter-spacing: 2px;"
                    + "-fx-cursor: hand;"
                    + "-fx-effect: dropshadow(gaussian, #8b2cff, 15, 0.5, 0, 0);"
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
