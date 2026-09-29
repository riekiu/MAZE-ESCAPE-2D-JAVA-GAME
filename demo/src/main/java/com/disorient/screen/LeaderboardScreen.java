package com.disorient.screen;

import java.util.List;

import com.disorient.manager.LeaderboardManager;
import com.disorient.manager.ScreenManager;
import com.disorient.model.LeaderboardEntry;
import com.disorient.model.LevelData;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class LeaderboardScreen extends StackPane {

    private static final String GOLD = "#f4d58d";
    private static final String MUTED = "#bba9c8";

    public LeaderboardScreen(LeaderboardManager leaderboardManager) {
        this(leaderboardManager, ScreenManager.getActive());
    }

    public LeaderboardScreen(LeaderboardManager leaderboardManager, ScreenManager screenManager) {
        List<LeaderboardEntry> entries = java.util.Objects.requireNonNull(
                leaderboardManager, "leaderboardManager").getEntries();
        setStyle("-fx-background-color: linear-gradient(to bottom right, #07040b, #1a1024, #08050d);");

        Button back = createButton("BACK TO MAIN MENU", screenManager);
        back.setOnAction(event -> screenManager.showMainMenu());

        Label eyebrow = new Label("FIELD NOTES  /  PERSONAL RECORDS");
        eyebrow.setStyle("-fx-text-fill: #b983d0;-fx-font-size: 10px;-fx-font-weight: bold;"
                + "-fx-letter-spacing: 2.5px;");
        Label title = new Label("LEADERBOARD");
        title.setStyle("-fx-text-fill: #fff8eb;-fx-font-size: 34px;-fx-font-weight: bold;"
                + "-fx-letter-spacing: 4px;-fx-effect: dropshadow(gaussian, #8b2cff, 18, 0.4, 0, 0);");
        Label subtitle = new Label("Your fastest escape for each level. Records stay on this device.");
        subtitle.setStyle("-fx-text-fill: " + MUTED + ";-fx-font-size: 13px;");
        VBox heading = new VBox(5, eyebrow, title, subtitle);
        heading.setAlignment(Pos.CENTER);

        int totalStars = 0;
        for (int level = 1; level <= screenManager.getGameManager().getTotalLevels(); level++) {
            totalStars += screenManager.getGameManager().getStarsForLevel(level);
        }
        int fastestSeconds = entries.stream()
                .mapToInt(LeaderboardEntry::getCompletionTime)
                .min()
                .orElse(-1);

        HBox summary = new HBox(12,
                createStat("LEVELS RECORDED",
                        String.format("%02d / %02d", entries.size(), screenManager.getGameManager().getTotalLevels())),
                createStat("FASTEST ESCAPE", fastestSeconds < 0 ? "--:--" : formatTime(fastestSeconds)),
                createStat("CAMPAIGN STARS", totalStars + " / 150"));
        summary.setAlignment(Pos.CENTER);
        summary.setPadding(new Insets(16, 0, 18, 0));

        VBox records = new VBox(7);
        records.setPadding(new Insets(14));
        records.setStyle("-fx-background-color: rgba(11, 7, 16, 0.7);"
                + "-fx-border-color: rgba(133,94,154,0.55);-fx-border-radius: 10px;"
                + "-fx-background-radius: 10px;");
        if (entries.isEmpty()) {
            Label emptyTitle = new Label("NO ESCAPES RECORDED YET");
            emptyTitle.setStyle("-fx-text-fill: #f4e7ce;-fx-font-size: 17px;-fx-font-weight: bold;");
            Label emptyBody = new Label("Complete a level to set your first personal best.");
            emptyBody.setStyle("-fx-text-fill: " + MUTED + ";-fx-font-size: 12px;");
            Button campaign = createButton("ENTER THE CAMPAIGN", screenManager);
            campaign.setOnAction(event -> screenManager.showCampaign());
            VBox empty = new VBox(10, emptyTitle, emptyBody, campaign);
            empty.setAlignment(Pos.CENTER);
            empty.setPadding(new Insets(42, 10, 42, 10));
            records.getChildren().add(empty);
        } else {
            HBox header = createRecordRow("LEVEL", "DIFFICULTY", "BEST TIME", "RATING", "STARS", true);
            records.getChildren().add(header);
            for (LeaderboardEntry entry : entries) {
                int stars = screenManager.getGameManager().getStarsForLevel(entry.getLevel());
                LevelData levelData = new LevelData(entry.getLevel());
                records.getChildren().add(createRecordRow(
                        String.format("%02d", entry.getLevel()),
                        levelData.getDifficulty().toUpperCase(),
                        formatTime(entry.getCompletionTime()),
                        formatSpeedRating(entry),
                        stars + " / 3",
                        false));
            }
        }

        ScrollPane scroll = new ScrollPane(records);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setStyle("-fx-background: transparent;-fx-background-color: transparent;"
                + "-fx-border-color: transparent;");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        VBox top = new VBox(heading, summary);
        BorderPane layout = new BorderPane();
        layout.setTop(top);
        layout.setCenter(scroll);
        Label ratingGuide = new Label("RATING GUIDE  |  0-20% EXCELLENT  |  21-40% GOOD  |  41-60% AVERAGE  |  61-79% BELOW AVERAGE  |  80%+ POOR");
        ratingGuide.setWrapText(true);
        ratingGuide.setMaxWidth(690);
        ratingGuide.setStyle("-fx-text-fill: " + MUTED + ";-fx-font-size: 9px;-fx-font-weight: bold;");
        Region footerSpacer = new Region();
        HBox footer = new HBox(16, ratingGuide, footerSpacer, back);
        footer.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(footerSpacer, Priority.ALWAYS);
        layout.setBottom(footer);
        BorderPane.setMargin(footer, new Insets(16, 0, 0, 0));
        layout.setPadding(new Insets(28, 38, 26, 38));
        layout.setMaxWidth(1120);
        layout.setStyle("-fx-background-color: rgba(13, 8, 19, 0.96);"
                + "-fx-background-radius: 14px;-fx-border-color: #704a86;-fx-border-radius: 14px;"
                + "-fx-border-width: 1.5px;");
        StackPane.setAlignment(layout, Pos.CENTER);
        StackPane.setMargin(layout, new Insets(24));
        getChildren().add(layout);
    }

    private HBox createStat(String caption, String value) {
        Label captionLabel = new Label(caption);
        captionLabel.setStyle("-fx-text-fill: " + MUTED + ";-fx-font-size: 9px;-fx-font-weight: bold;"
                + "-fx-letter-spacing: 1px;");
        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-text-fill: " + GOLD + ";-fx-font-size: 19px;-fx-font-weight: bold;");
        VBox text = new VBox(4, captionLabel, valueLabel);
        HBox card = new HBox(text);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(10, 16, 10, 16));
        card.setStyle("-fx-background-color: rgba(49, 31, 62, 0.78);-fx-border-color: #493259;"
                + "-fx-border-radius: 7px;-fx-background-radius: 7px;");
        HBox.setHgrow(card, Priority.ALWAYS);
        return card;
    }

    private HBox createRecordRow(
            String level, String difficulty, String time, String rating, String stars, boolean header
    ) {
        HBox row = new HBox(
                createCell(level, 90, header),
                createCell(difficulty, 200, header),
                createCell(time, 145, header),
                createCell(rating, 190, header),
                createCell(stars, 100, header));
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(0, 8, 0, 8));
        row.setMinHeight(header ? 42 : 40);
        row.setStyle(header
                ? "-fx-background-color: #352341;-fx-background-radius: 6px;"
                : "-fx-background-color: rgba(28, 19, 37, 0.82);-fx-border-color: #30213b;"
                        + "-fx-border-radius: 5px;-fx-background-radius: 5px;");
        return row;
    }

    private Label createCell(String value, double width, boolean header) {
        Label cell = new Label(value);
        cell.setPrefWidth(width);
        cell.setAlignment(Pos.CENTER_LEFT);
        cell.setStyle(header
                ? "-fx-text-fill: #c8a3da;-fx-font-size: 9px;-fx-font-weight: bold;-fx-letter-spacing: 1px;"
                : "-fx-text-fill: #f1e9f4;-fx-font-size: 12px;-fx-font-weight: bold;");
        return cell;
    }

    private String formatTime(int seconds) {
        return String.format("%02d:%02d", seconds / 60, seconds % 60);
    }

    private String formatSpeedRating(LeaderboardEntry entry) {
        double completionRatio = (double) entry.getCompletionTime()
                / new LevelData(entry.getLevel()).getTimeLimit();
        if (completionRatio <= 0.20) return "EXCELLENT";
        if (completionRatio <= 0.40) return "GOOD";
        if (completionRatio <= 0.60) return "AVERAGE";
        if (completionRatio < 0.80) return "BELOW AVERAGE";
        return "POOR";
    }

    private Button createButton(String text, ScreenManager screenManager) {
        Button button = new Button(text);
        button.setOnMousePressed(event -> screenManager.getAudioManager().playClick());
        button.setPrefWidth(250);
        button.setPrefHeight(42);
        button.setStyle("-fx-background-color: #21142d;-fx-border-color: #795692;"
                + "-fx-border-radius: 7px;-fx-background-radius: 7px;-fx-text-fill: #f4e7ce;"
                + "-fx-font-size: 11px;-fx-font-weight: bold;-fx-padding: 10px 16px;-fx-cursor: hand;");
        button.setOnMouseEntered(event -> button.setStyle(
                "-fx-background-color: #392449;-fx-border-color: #c18bdb;-fx-border-radius: 7px;"
                        + "-fx-background-radius: 7px;-fx-text-fill: white;-fx-font-size: 11px;"
                        + "-fx-font-weight: bold;-fx-padding: 10px 16px;-fx-cursor: hand;"));
        button.setOnMouseExited(event -> button.setStyle(
                "-fx-background-color: #21142d;-fx-border-color: #795692;-fx-border-radius: 7px;"
                        + "-fx-background-radius: 7px;-fx-text-fill: #f4e7ce;-fx-font-size: 11px;"
                        + "-fx-font-weight: bold;-fx-padding: 10px 16px;-fx-cursor: hand;"));
        return button;
    }
}
