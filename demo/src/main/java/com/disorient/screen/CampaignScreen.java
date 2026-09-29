package com.disorient.screen;

import com.disorient.manager.ScreenManager;
import com.disorient.model.LevelData;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.effect.DropShadow;

/** Level selection and progress overview for the haunted-house campaign. */
public class CampaignScreen extends BorderPane {

    private static final String GOLD = "#e8c98a";
    private static final String MUTED = "#ad9ab7";

    private final ScreenManager screenManager;
    private Label previewChapter;
    private Label previewDetail;
    private Button enterButton;
    private int previewLevel;

    public CampaignScreen(ScreenManager screenManager) {
        this.screenManager = screenManager;
        setPadding(new Insets(24, 38, 20, 38));
        setStyle("-fx-background-color: linear-gradient(to bottom right, #08050e, #1c1028, #09060f);");

        setTop(createHeader());
        setCenter(createLevelMap());
        setBottom(createFooter());
    }

    private VBox createHeader() {
        Button back = createActionButton("←  MAIN MENU", false);
        back.setOnAction(event -> screenManager.showMainMenu());

        Label eyebrow = new Label("THE HAUNTED HOUSE COLLECTION");
        eyebrow.setStyle("-fx-text-fill: #ad8ac2;-fx-font-size: 10px;-fx-font-weight: bold;"
                + "-fx-letter-spacing: 3px;");
        Label title = new Label("CAMPAIGN");
        title.setStyle("-fx-text-fill: #f4e7ce;-fx-font-size: 34px;-fx-font-weight: bold;"
                + "-fx-letter-spacing: 7px;-fx-effect: dropshadow(gaussian, #8b2cff, 18, 0.45, 0, 0);");
        VBox heading = new VBox(5, eyebrow, title);
        heading.setAlignment(Pos.CENTER);

        int total = screenManager.getGameManager().getTotalLevels();
        int unlocked = screenManager.getGameManager().getHighestUnlockedLevel();
        int current = screenManager.getGameManager().getCurrentLevel();
        Label progressText = new Label(String.format("HOUSE %02d / %02d", unlocked, total));
        progressText.setStyle("-fx-text-fill: " + GOLD + ";-fx-font-size: 11px;-fx-font-weight: bold;"
                + "-fx-letter-spacing: 1px;");
        ProgressBar progress = new ProgressBar((double) unlocked / total);
        progress.setPrefWidth(170);
        progress.setStyle("-fx-accent: #b464d6;");
        Label currentText = new Label(String.format("CURRENT: %02d", current));
        currentText.setStyle("-fx-text-fill: " + MUTED + ";-fx-font-size: 10px;-fx-font-weight: bold;"
                + "-fx-letter-spacing: 1px;");
        VBox progressBox = new VBox(7, progressText, progress, currentText);
        progressBox.setAlignment(Pos.CENTER_RIGHT);

        HBox row = new HBox(20, back, heading, progressBox);
        row.setAlignment(Pos.CENTER);
        HBox.setHgrow(heading, Priority.ALWAYS);
        row.setPadding(new Insets(0, 0, 18, 0));
        return new VBox(row, createContinueCard(current));
    }

    private HBox createContinueCard(int currentLevel) {
        previewChapter = new Label();
        previewChapter.setStyle("-fx-text-fill: " + GOLD + ";-fx-font-size: 10px;-fx-font-weight: bold;"
                + "-fx-letter-spacing: 1.7px;");
        previewDetail = new Label();
        previewDetail.setStyle("-fx-text-fill: #c7b8d0;-fx-font-size: 12px;");
        VBox text = new VBox(6, previewChapter, previewDetail);

        enterButton = createActionButton("ENTER HOUSE", true);
        enterButton.setOnAction(event -> screenManager.startLevel(previewLevel));
        HBox card = new HBox(18, text, enterButton);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(13, 18, 13, 18));
        card.setStyle("-fx-background-color: linear-gradient(to right, rgba(72,35,91,0.82), rgba(24,14,34,0.94));"
                + "-fx-border-color: #9568a8;-fx-border-width: 1px;-fx-border-radius: 9px;"
                + "-fx-background-radius: 9px;");
        card.setEffect(new DropShadow(15, Color.rgb(105, 43, 145, 0.22)));
        HBox.setHgrow(text, Priority.ALWAYS);
        updateLevelPreview(currentLevel, screenManager.getGameManager().isLevelUnlocked(currentLevel));
        return card;
    }

    private void updateLevelPreview(int level, boolean unlocked) {
        previewLevel = level;
        LevelData levelData = new LevelData(level);
        int stars = screenManager.getGameManager().getStarsForLevel(level);
        String heading = level == screenManager.getGameManager().getCurrentLevel()
                ? "YOUR NEXT HAUNT" : unlocked ? "LEVEL PREVIEW" : "SEALED FLOOR";
        previewChapter.setText(String.format("%s  |  LEVEL %02d", heading, level));
        previewDetail.setText(String.format("%s  |  %d x %d maze  |  %d seconds  |  BEST: %d/3 STARS",
                levelData.getDifficulty().toUpperCase(), levelData.getGridSize(),
                levelData.getGridSize(), levelData.getTimeLimit(), stars));
        enterButton.setText(unlocked ? "ENTER HOUSE" : "SEALED");
        enterButton.setDisable(!unlocked);
    }

    private ScrollPane createLevelMap() {
        int total = screenManager.getGameManager().getTotalLevels();
        int current = screenManager.getGameManager().getCurrentLevel();

        Label sectionTitle = new Label("THE HOUSE DIRECTORY");
        sectionTitle.setStyle("-fx-text-fill: #f0dfc1;-fx-font-size: 14px;-fx-font-weight: bold;"
                + "-fx-letter-spacing: 2px;");
        Label sectionHint = new Label("Stars show your best life rating for each completed floor.");
        sectionHint.setStyle("-fx-text-fill: " + MUTED + ";-fx-font-size: 11px;");
        VBox sectionHeading = new VBox(4, sectionTitle, sectionHint);
        sectionHeading.setPadding(new Insets(17, 0, 11, 2));

        GridPane levels = new GridPane();
        levels.setHgap(12);
        levels.setVgap(12);
        levels.setAlignment(Pos.TOP_CENTER);
        for (int level = 1; level <= total; level++) {
            boolean unlocked = screenManager.getGameManager().isLevelUnlocked(level);
            boolean active = level == current;
            int stars = screenManager.getGameManager().getStarsForLevel(level);
            boolean cleared = stars > 0;
            Button tile = createLevelTile(level, unlocked, active, cleared, stars);
            int selectedLevel = level;
            tile.setOnAction(event -> {
                if (unlocked) screenManager.startLevel(selectedLevel);
            });
            levels.add(tile, (level - 1) % 5, (level - 1) / 5);
        }

        VBox mapContent = new VBox(sectionHeading, levels);
        mapContent.setPadding(new Insets(0, 16, 18, 16));
        mapContent.setStyle("-fx-background-color: rgba(10,7,16,0.55);"
                + "-fx-border-color: rgba(133,94,154,0.55);-fx-border-width: 1px;"
                + "-fx-border-radius: 10px;-fx-background-radius: 10px;");

        ScrollPane scroll = new ScrollPane(mapContent);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background: transparent;-fx-background-color: transparent;"
                + "-fx-border-color: transparent;");
        VBox.setVgrow(scroll, Priority.ALWAYS);
        return scroll;
    }

    private Button createLevelTile(int level, boolean unlocked, boolean active, boolean cleared, int stars) {
        String status = !unlocked ? "SEALED" : active ? "CURRENT" : cleared ? "CLEARED" : "OPEN";
        Label number = new Label(String.format("%02d", level));
        number.setStyle("-fx-text-fill: " + (unlocked ? "#f4e7ce" : "#746b7c")
                + ";-fx-font-size: 22px;-fx-font-weight: bold;-fx-font-family: serif;");
        Label statusLabel = new Label(status);
        statusLabel.setStyle("-fx-text-fill: " + (active ? GOLD : unlocked ? "#bd9bcc" : "#716879")
                + ";-fx-font-size: 9px;-fx-font-weight: bold;-fx-letter-spacing: 1.5px;");
        Label starsLabel = new Label("★".repeat(stars) + "☆".repeat(3 - stars));
        starsLabel.setStyle("-fx-text-fill: " + (stars > 0 ? GOLD : "#716879")
                + ";-fx-font-size: 12px;-fx-font-weight: bold;");
        VBox graphic = new VBox(2, number, statusLabel, starsLabel);
        graphic.setAlignment(Pos.CENTER);

        Button tile = new Button();
        tile.setGraphic(graphic);
        tile.setAccessibleText(String.format("Level %d, %s, %d stars", level, status.toLowerCase(), stars));
        tile.setPrefSize(142, 82);
        tile.setFocusTraversable(unlocked);
        tile.setStyle(levelTileStyle(unlocked, active));
        tile.setOnMousePressed(event -> {
            if (unlocked) screenManager.getAudioManager().playClick();
        });
        tile.setOnMouseEntered(event -> {
            if (unlocked) tile.setStyle(levelTileHoverStyle(active));
            updateLevelPreview(level, unlocked);
        });
        tile.setOnMouseExited(event -> tile.setStyle(levelTileStyle(unlocked, active)));
        return tile;
    }

    private String levelTileStyle(boolean unlocked, boolean active) {
        if (active) {
            return "-fx-background-color: linear-gradient(to bottom, #563467, #291934);"
                    + "-fx-border-color: #e4c280;-fx-border-width: 1.5px;-fx-border-radius: 7px;"
                    + "-fx-background-radius: 7px;-fx-cursor: hand;";
        }
        if (unlocked) {
            return "-fx-background-color: linear-gradient(to bottom, #281b31, #17111f);"
                    + "-fx-border-color: #674b75;-fx-border-width: 1px;-fx-border-radius: 7px;"
                    + "-fx-background-radius: 7px;-fx-cursor: hand;";
        }
        return "-fx-background-color: rgba(20,17,25,0.72);-fx-border-color: #37303c;"
                + "-fx-border-width: 1px;-fx-border-radius: 7px;-fx-background-radius: 7px;";
    }

    private String levelTileHoverStyle(boolean active) {
        return "-fx-background-color: linear-gradient(to bottom, #512a63, #2c1937);"
                + "-fx-border-color: " + (active ? GOLD : "#c18bdb") + ";-fx-border-width: 1.5px;"
                + "-fx-border-radius: 7px;-fx-background-radius: 7px;-fx-cursor: hand;"
                + "-fx-effect: dropshadow(gaussian, #8b2cff, 12, 0.3, 0, 0);";
    }

    private HBox createFooter() {
        Label legend = new Label("◆  CLEARED     ◉  CURRENT     ◇  OPEN     ▣  SEALED");
        legend.setStyle("-fx-text-fill: #a896b2;-fx-font-size: 9px;-fx-font-weight: bold;"
                + "-fx-letter-spacing: 1px;");
        HBox footer = new HBox(legend);
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(14, 0, 0, 0));
        return footer;
    }

    private Button createActionButton(String text, boolean primary) {
        Button button = new Button(text);
        button.setPadding(new Insets(10, 15, 10, 15));
        button.setStyle(primary
                ? "-fx-background-color: #6b3c7e;-fx-border-color: #d7b67a;-fx-border-radius: 6px;"
                    + "-fx-background-radius: 6px;-fx-text-fill: #fff1d4;-fx-font-size: 10px;"
                    + "-fx-font-weight: bold;-fx-letter-spacing: 1px;-fx-cursor: hand;"
                : "-fx-background-color: rgba(28,19,36,0.8);-fx-border-color: #6e5379;"
                    + "-fx-border-radius: 6px;-fx-background-radius: 6px;-fx-text-fill: #d6c6dc;"
                    + "-fx-font-size: 10px;-fx-font-weight: bold;-fx-letter-spacing: 1px;-fx-cursor: hand;");
        button.setOnMousePressed(event -> screenManager.getAudioManager().playClick());
        return button;
    }
}
