package com.mazeescape.screen;

import com.mazeescape.manager.ScreenManager;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class VictoryScreen extends StackPane {

    public VictoryScreen(int totalScore, int levelsCompleted, int bestScore) {
        this(totalScore, levelsCompleted, bestScore, ScreenManager.getActive());
    }

    public VictoryScreen(int totalScore, int levelsCompleted, int bestScore, ScreenManager screenManager) {

        // =========================
        // BACKGROUND
        // =========================
        setStyle(
                "-fx-background-color: linear-gradient("
                + "to bottom, #050008, #16051f, #050008);"
        );

        // =========================
        // TITLE
        // =========================
        Label title = new Label("YOU ESCAPED!");

        title.setStyle(
                "-fx-text-fill: white;"
                + "-fx-font-size: 50px;"
                + "-fx-font-weight: bold;"
                + "-fx-letter-spacing: 5px;"
                + "-fx-effect: dropshadow(gaussian, #b84cff, 25, 0.7, 0, 0);"
        );

        // =========================
        // SUBTITLE
        // =========================
        Label subtitle = new Label(
                "YOU SURVIVED THE HAUNTED HOUSE"
        );

        subtitle.setStyle(
                "-fx-text-fill: #c9a8d8;"
                + "-fx-font-size: 15px;"
                + "-fx-letter-spacing: 2px;"
        );

        // =========================
        // STATISTICS
        // =========================
        Label scoreLabel = new Label(
                "TOTAL SCORE: " + totalScore
        );

        Label levelsLabel = new Label(
                "LEVELS COMPLETED: " + levelsCompleted + " / 20"
        );

        Label bestScoreLabel = new Label(
                "BEST SCORE: " + bestScore
        );

        String statStyle
                = "-fx-text-fill: white;"
                + "-fx-font-size: 18px;"
                + "-fx-font-weight: bold;";

        scoreLabel.setStyle(statStyle);
        levelsLabel.setStyle(statStyle);
        bestScoreLabel.setStyle(statStyle);

        VBox statistics = new VBox(
                8,
                scoreLabel,
                levelsLabel,
                bestScoreLabel
        );

        statistics.setAlignment(Pos.CENTER);

        // =========================
        // PLAY AGAIN
        // =========================
        Button playAgainButton
                = createButton("PLAY AGAIN", screenManager);

        playAgainButton.setOnAction(event -> {
            screenManager.startNewGame();
        });

        // =========================
        // CAMPAIGN
        // =========================
        Button campaignButton
                = createButton("CAMPAIGN", screenManager);

        campaignButton.setOnAction(event -> {
            screenManager.showCampaign();
        });

        // =========================
        // LEADERBOARD
        // =========================
        Button leaderboardButton
                = createButton("LEADERBOARD", screenManager);

        leaderboardButton.setOnAction(event -> {
            screenManager.showLeaderboard();
        });

        // =========================
        // MAIN MENU
        // =========================
        Button mainMenuButton
                = createButton("MAIN MENU", screenManager);

        mainMenuButton.setOnAction(event -> {
            screenManager.showMainMenu();
        });

        // =========================
        // BUTTON CONTAINER
        // =========================
        VBox buttons = new VBox(
                10,
                playAgainButton,
                campaignButton,
                leaderboardButton,
                mainMenuButton
        );

        buttons.setAlignment(Pos.CENTER);

        // =========================
        // MAIN CONTENT
        // =========================
        VBox content = new VBox(
                15,
                title,
                subtitle,
                statistics,
                buttons
        );

        content.setAlignment(Pos.CENTER);

        getChildren().add(content);
    }

    // =========================
    // BUTTON CREATOR
    // =========================
    private Button createButton(String text, ScreenManager screenManager) {

        Button button = new Button(text);
        button.setOnMousePressed(event -> screenManager.getAudioManager().playClick());

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
                    + "-fx-border-color: #b84cff;"
                    + "-fx-border-width: 2px;"
                    + "-fx-border-radius: 8px;"
                    + "-fx-background-radius: 8px;"
                    + "-fx-text-fill: white;"
                    + "-fx-font-size: 14px;"
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
                    + "-fx-font-size: 14px;"
                    + "-fx-font-weight: bold;"
                    + "-fx-letter-spacing: 2px;"
                    + "-fx-cursor: hand;"
            );
        });

        return button;
    }
}
