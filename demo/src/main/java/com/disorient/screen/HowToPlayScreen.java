package com.disorient.screen;

import com.disorient.manager.ScreenManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

public class HowToPlayScreen extends StackPane {

    private static final String MUTED = "#bba9c8";
    private static final String TEXT = "#eadff0";
    private static final String GOLD = "#f4d58d";

    public HowToPlayScreen() {
        this(ScreenManager.getActive());
    }

    public HowToPlayScreen(ScreenManager screenManager) {
        HorrorUiTheme.applyBackdrop(this);

        Button back = new Button("BACK TO MAIN MENU");
        back.setOnAction(event -> screenManager.showMainMenu());
        HorrorUiTheme.styleButton(back, screenManager.getAudioManager());

        Label eyebrow = new Label("FIELD GUIDE  /  DISORIENT");
        eyebrow.setStyle("-fx-text-fill: #b983d0;-fx-font-size: 10px;-fx-font-weight: bold;"
                + "-fx-letter-spacing: 2.5px;");
        Label title = new Label("HOW TO SURVIVE");
        title.setStyle("-fx-text-fill: #fff8eb;-fx-font-size: 34px;-fx-font-weight: bold;"
                + "-fx-letter-spacing: 4px;-fx-effect: dropshadow(gaussian, #8b2cff, 18, 0.4, 0, 0);");
        Label subtitle = new Label("Find the key. The maze will change. Get out before the spirit catches you.");
        subtitle.setStyle("-fx-text-fill: " + MUTED + ";-fx-font-size: 13px;");
        VBox heading = new VBox(5, eyebrow, title, subtitle);
        heading.setAlignment(Pos.CENTER_LEFT);

        HBox top = new HBox(24, back, heading);
        top.setAlignment(Pos.CENTER_LEFT);
        top.setPadding(new Insets(0, 0, 18, 0));

        HBox quickFacts = new HBox(10,
                createFact("50", "LEVELS"),
                createFact("3", "STARTING HEARTS"),
                createFact("60-360s", "TIME LIMITS"),
                createFact("1-3", "STARS PER CLEAR"));
        quickFacts.setAlignment(Pos.CENTER_LEFT);
        quickFacts.setPadding(new Insets(0, 0, 18, 0));

        TilePane cards = new TilePane();
        cards.setHgap(12);
        cards.setVgap(12);
        cards.setPrefColumns(2);
        cards.setPrefTileWidth(490);
        cards.setTileAlignment(Pos.TOP_LEFT);
        cards.getChildren().addAll(
                createSection("01  /  OBJECTIVE", "Find a way out",
                        "Move through the maze and collect its key. The maze rearranges, the exit moves, "
                                + "and a pursuing spirit appears. Reach the newly opened exit to clear the level."),
                createSection("02  /  CONTROLS", "Move carefully",
                        "W, A, S, D or the arrow keys move you. Press ESC or the PAUSE button to pause. "
                                + "Movement is about 200 pixels per second; diagonal movement is not faster. "
                                + "Walls and the maze border block your path."),
                createSection("03  /  VISIBILITY", "Use the light and Field Notes",
                        "Your light shows a circle around you. It starts at 165 pixels and shrinks by 5 pixels "
                                + "per level to a 70-pixel minimum; its edge fades into the fog. The minimap "
                                + "keeps explored areas dimly visible. Light does not stop at walls."),
                createSection("04  /  SPIRIT", "Protect your hearts",
                        "The spirit appears after you take the key and follows reachable paths toward you. "
                                + "A collision costs one heart. It retreats and reappears farther away after "
                                + "a hit. Losing all three hearts ends the run."),
                createSection("05  /  TIMER", "Watch the countdown",
                        "The timer pauses with the game. Limits range from 60 seconds on early levels to "
                                + "360 seconds on Levels 46-50. If time reaches zero, the game ends without "
                                + "removing a heart. Retry starts the level with three hearts."),
                createSection("06  /  LEVELS", "Difficulty keeps rising",
                        "There are 50 mazes, from 9 x 9 to 58 x 58. Difficulty ranges from Easy through "
                                + "Easy-Medium, Medium, Medium-Hard, Hard, Very Hard, and Extreme. The "
                                + "campaign currently makes every level available."),
                createSection("07  /  SCORE AND STARS", "Finish fast and stay alive",
                        "A clear scores 1,000 points, plus 10 for each second left and 250 for each heart "
                                + "left. You earn one star per remaining heart, up to three. The campaign "
                                + "keeps your best star result for each level."),
                createSection("08  /  PERSONAL RECORDS", "Beat your best time",
                        "The leaderboard stores your fastest completion time for each level. Its rating "
                                + "compares your time with that level's limit: Excellent, Good, Average, "
                                + "Below Average, or Poor. Set the name shown beside your records in Settings."),
                createSection("09  /  SAVE AND RETRY", "Continue when you return",
                        "Play resumes your saved level, score, hearts, progress, stars, and best times. "
                                + "Campaign lets you preview level difficulty, maze size, time, and best stars. "
                                + "Retry restarts the selected level with three hearts."),
                createSection("10  /  SOUND AND SETTINGS", "Set the mood",
                        "Adjust master, music, and sound-effect volume in Settings. The game includes menu "
                                + "music, haunted ambience, footsteps, clicks, key pickup, exit unlock, "
                                + "level-clear, and game-over sounds."));
        cards.setPadding(new Insets(4, 2, 18, 2));

        ScrollPane guideScroll = new ScrollPane(cards);
        guideScroll.setFitToWidth(true);
        guideScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        guideScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        guideScroll.setStyle("-fx-background: transparent;-fx-background-color: transparent;"
                + "-fx-border-color: transparent;");
        VBox.setVgrow(guideScroll, Priority.ALWAYS);

        BorderPane layout = new BorderPane();
        layout.setPadding(new Insets(26, 42, 24, 42));
        layout.setTop(new VBox(top, quickFacts));
        layout.setCenter(guideScroll);
        layout.setMaxWidth(1120);
        layout.setStyle("-fx-background-color: rgba(13, 8, 19, 0.96);"
                + "-fx-background-radius: 14px;-fx-border-color: #987452;-fx-border-radius: 14px;"
                + "-fx-border-width: 1.5px;");
        StackPane.setAlignment(layout, Pos.CENTER);
        StackPane.setMargin(layout, new Insets(24));
        getChildren().add(layout);
    }

    private HBox createFact(String value, String caption) {
        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-text-fill: " + GOLD + ";-fx-font-size: 17px;-fx-font-weight: bold;");
        Label captionLabel = new Label(caption);
        captionLabel.setStyle("-fx-text-fill: " + MUTED + ";-fx-font-size: 9px;-fx-font-weight: bold;"
                + "-fx-letter-spacing: 1px;");
        VBox text = new VBox(3, valueLabel, captionLabel);
        HBox fact = new HBox(text);
        fact.setAlignment(Pos.CENTER_LEFT);
        fact.setPadding(new Insets(9, 14, 9, 14));
        fact.setStyle("-fx-background-color: rgba(49, 31, 62, 0.78);-fx-border-color: #493259;"
                + "-fx-border-radius: 7px;-fx-background-radius: 7px;");
        return fact;
    }

    private VBox createSection(String tag, String title, String body) {
        Label tagLabel = new Label(tag);
        tagLabel.setStyle("-fx-text-fill: #c58bdd;-fx-font-size: 9px;-fx-font-weight: bold;"
                + "-fx-letter-spacing: 1.5px;");
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-text-fill: #fff1d4;-fx-font-size: 17px;-fx-font-weight: bold;");
        Label bodyLabel = new Label(body);
        bodyLabel.setWrapText(true);
        bodyLabel.setLineSpacing(3);
        bodyLabel.setStyle("-fx-text-fill: " + TEXT + ";-fx-font-size: 12px;");
        VBox card = new VBox(7, tagLabel, titleLabel, bodyLabel);
        card.setPrefWidth(490);
        card.setMinHeight(132);
        card.setPadding(new Insets(15, 17, 15, 17));
        card.setStyle("-fx-background-color: linear-gradient(to bottom right, #21152c, #17101f);"
                + "-fx-border-color: #493259;-fx-border-radius: 9px;-fx-background-radius: 9px;");
        return card;
    }
}