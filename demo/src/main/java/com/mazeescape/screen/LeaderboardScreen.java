package com.mazeescape.screen;

import java.util.List;

import com.mazeescape.manager.LeaderboardManager;
import com.mazeescape.manager.ScreenManager;
import com.mazeescape.model.LevelData;
import com.mazeescape.model.LeaderboardEntry;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class LeaderboardScreen extends StackPane {

    private final LeaderboardManager leaderboardManager;

    public LeaderboardScreen(LeaderboardManager leaderboardManager) {
        this(leaderboardManager, ScreenManager.getActive());
    }

    public LeaderboardScreen(LeaderboardManager leaderboardManager, ScreenManager screenManager) {

        this.leaderboardManager = java.util.Objects.requireNonNull(leaderboardManager, "leaderboardManager");

        // =========================
        // BACKGROUND
        // =========================
        setStyle(
                "-fx-background-color: linear-gradient("
                + "to bottom, #050008, #12051a, #050008);"
        );

        // =========================
        // TITLE
        // =========================
        Label title = new Label("LEADERBOARD");

        title.setStyle(
                "-fx-text-fill: white;"
                + "-fx-font-size: 42px;"
                + "-fx-font-weight: bold;"
                + "-fx-letter-spacing: 5px;"
                + "-fx-effect: dropshadow(gaussian, #b84cff, 20, 0.6, 0, 0);"
        );

        Label subtitle = new Label(
                "PERSONAL BEST TIMES • ME VS ME"
        );

        subtitle.setStyle(
                "-fx-text-fill: #b99acb;"
                + "-fx-font-size: 13px;"
                + "-fx-letter-spacing: 2px;"
        );

        // =========================
        // LEADERBOARD LIST
        // =========================
        LeaderboardManager activeLeaderboard = this.leaderboardManager;

        VBox leaderboardBox = new VBox(6);

        leaderboardBox.setAlignment(Pos.CENTER);

        List<LeaderboardEntry> entries
                = activeLeaderboard.getEntries();

        if (entries.isEmpty()) {

            Label emptyLabel = new Label(
                    "NO LEVEL TIMES RECORDED YET"
            );

            emptyLabel.setStyle(
                    "-fx-text-fill: #8f719d;"
                    + "-fx-font-size: 16px;"
                    + "-fx-font-weight: bold;"
            );

            leaderboardBox.getChildren().add(emptyLabel);

        } else {

            // Header
            HBox header = createRow(
                    "LEVEL",
                    "BEST TIME",
                    "STATUS"
            );

            header.setStyle(
                    "-fx-background-color: #24102f;"
                    + "-fx-background-radius: 6px;"
                    + "-fx-border-color: #6d3a91;"
                    + "-fx-border-width: 1px;"
                    + "-fx-border-radius: 6px;"
            );

            leaderboardBox.getChildren().add(header);

            // Entries
            for (LeaderboardEntry entry : entries) {

                HBox row = createRow(
                        String.format("LEVEL %02d", entry.getLevel()),
                        formatTime(entry.getCompletionTime()),
                        formatSpeedRating(entry)
                );

                leaderboardBox.getChildren().add(row);
            }
        }

        ScrollPane leaderboardScroll = new ScrollPane(leaderboardBox);
        leaderboardScroll.setFitToWidth(true);
        leaderboardScroll.setMaxHeight(520);
        leaderboardScroll.setPrefViewportHeight(520);
        leaderboardScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        leaderboardScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        leaderboardScroll.setStyle("-fx-background: transparent;-fx-background-color: transparent;");

        // =========================
        // BACK BUTTON
        // =========================
        Button backButton
                = createButton("BACK");

        backButton.setOnAction(event
                -> screenManager.showMainMenu()
        );

        // =========================
        // CONTENT
        // =========================
        VBox content = new VBox(
                12,
                title,
                subtitle,
                leaderboardScroll,
                backButton
        );

        content.setAlignment(Pos.CENTER);

        content.setMaxWidth(700);

        getChildren().add(content);
    }

    // =========================
    // CREATE LEADERBOARD ROW
    // =========================
    private HBox createRow(
            String level,
            String bestTime,
            String status
    ) {

        Label rankLabel = createCell(
                level,
                180
        );

        Label playerLabel = createCell(
                bestTime,
                180
        );

        Label statusLabel = createCell(
                status,
                220
        );

        HBox row = new HBox(
                rankLabel,
                playerLabel,
                statusLabel
        );

        row.setAlignment(Pos.CENTER);

        row.setPrefHeight(42);

        row.setStyle(
                "-fx-background-color: #100817;"
                + "-fx-background-radius: 6px;"
                + "-fx-border-color: #321b40;"
                + "-fx-border-width: 1px;"
                + "-fx-border-radius: 6px;"
        );

        return row;
    }

    // =========================
    // CREATE CELL
    // =========================
    private Label createCell(
            String text,
            double width
    ) {

        Label label = new Label(text);

        label.setPrefWidth(width);

        label.setAlignment(Pos.CENTER);

        label.setStyle(
                "-fx-text-fill: white;"
                + "-fx-font-size: 14px;"
                + "-fx-font-weight: bold;"
        );

        return label;
    }

    private String formatTime(int seconds) {
        return String.format("%02d:%02d", seconds / 60, seconds % 60);
    }

    private String formatSpeedRating(LeaderboardEntry entry) {
        int timeLimit = new LevelData(entry.getLevel()).getTimeLimit();
        double completionRatio = (double) entry.getCompletionTime() / timeLimit;

        if (completionRatio <= 0.20) {
            return "EXCELLENT";
        }
        if (completionRatio <= 0.40) {
            return "GOOD";
        }
        if (completionRatio <= 0.60) {
            return "AVERAGE";
        }
        if (completionRatio <0.80) {
            return "BELOW AVERAGE";
        }
        return "POOR";
    }

    // =========================
    // BUTTON
    // =========================
    private Button createButton(String text) {

        Button button = new Button(text);
        button.setOnMouseClicked(event -> ScreenManager.getActive().getAudioManager().playClick());

        button.setPrefWidth(280);
        button.setPrefHeight(46);

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
