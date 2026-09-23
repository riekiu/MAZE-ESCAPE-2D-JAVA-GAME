package com.mazeescape.screen;

import com.mazeescape.manager.ScreenManager;
import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class MainMenuScreen extends StackPane {

    private final ScreenManager screenManager;

    public MainMenuScreen() {
        this(ScreenManager.getActive());
    }

    public MainMenuScreen(ScreenManager screenManager) {
        this(screenManager, false);
    }

    public MainMenuScreen(ScreenManager screenManager, boolean campaign) {
        this.screenManager = screenManager;
        if (campaign) {
            setStyle("-fx-background-color: linear-gradient(to bottom, #050008, #12001c, #07000d);");
            buildCampaign();
        } else {
            buildMainMenu();
        }
    }

    private void buildMainMenu() {
        configureBackground();
        ImageView logo = createImageView("/img/menu/logo.png", 570, 321);

        Button play = createButton("PLAY");
        play.setOnAction(event -> screenManager.playLastPlayedLevel());
        Button campaign = createButton("CAMPAIGN");
        campaign.setOnAction(event -> screenManager.showCampaign());
        Button settings = createButton("SETTINGS");
        settings.setOnAction(event -> screenManager.showSettings());
        Button howToPlay = createButton("HOW TO PLAY");
        howToPlay.setOnAction(event -> screenManager.showHowToPlay());
        Button leaderboard = createButton("LEADERBOARD");
        leaderboard.setOnAction(event -> screenManager.showLeaderboard());
        Button credits = createButton("CREDITS");
        credits.setOnAction(event -> screenManager.showCredits());
        Button exit = createButton("EXIT");
        exit.setOnAction(event -> screenManager.getStage().close());

        VBox buttons = new VBox(4, play, campaign, settings, howToPlay,
                leaderboard, credits, exit);
        buttons.setAlignment(Pos.CENTER);
        VBox content = new VBox(12, logo, buttons);
        content.setAlignment(Pos.CENTER);
        getChildren().add(content);
        fadeIn(content);
    }

    private void configureBackground() {
        var resource = getClass().getResource("/img/menu/background.png");
        String resourcePath = "/img/menu/background.png";
        if (resource == null) {
            resourcePath = "/img/menu/background.jpg";
            resource = getClass().getResource(resourcePath);
        }
        if (resource == null) {
            throw new IllegalStateException("Missing image resource: " + resourcePath);
        }

        Image backgroundImage = new Image(resource.toExternalForm(), false);
        setBackground(new Background(new BackgroundImage(
                backgroundImage,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(100, 100, true, true, false, true)
        )));
    }

    private ImageView createImageView(String resourcePath, double fitWidth, double fitHeight) {
        var resource = getClass().getResource(resourcePath);
        if (resource == null) {
            throw new IllegalStateException("Missing image resource: " + resourcePath);
        }

        ImageView imageView = new ImageView(new Image(resource.toExternalForm(), true));
        imageView.setFitWidth(fitWidth);
        imageView.setFitHeight(fitHeight);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(false);
        return imageView;
    }

    private void buildCampaign() {
        Label title = new Label("CAMPAIGN");
        title.setStyle("-fx-text-fill: white;-fx-font-size: 42px;-fx-font-weight: bold;-fx-letter-spacing: 5px;"
                + "-fx-effect: dropshadow(gaussian, #8b2cff, 20, 0.6, 0, 0);");
        Label subtitle = new Label("CHOOSE A HAUNTED HOUSE LEVEL");
        subtitle.setStyle("-fx-text-fill: #b99acb;-fx-font-size: 14px;-fx-letter-spacing: 2px;");

        GridPane levels = new GridPane();
        levels.setHgap(10);
        levels.setVgap(10);
        levels.setAlignment(Pos.CENTER);
        for (int level = 1; level <= screenManager.getGameManager().getTotalLevels(); level++) {
            Button button = createButton(String.format("LEVEL %02d", level));
            button.setPrefWidth(150);
            button.setPrefHeight(42);
            button.setDisable(!screenManager.getGameManager().isLevelUnlocked(level));
            final int selectedLevel = level;
            button.setOnAction(event -> screenManager.startLevel(selectedLevel));
            levels.add(button, (level - 1) % 5, (level - 1) / 5);
        }

        Button back = createButton("BACK");
        back.setOnAction(event -> screenManager.showMainMenu());
        VBox content = new VBox(16, title, subtitle, levels, back);
        content.setAlignment(Pos.CENTER);
        getChildren().add(content);
        fadeIn(content);
    }

    private void fadeIn(VBox content) {
        FadeTransition fade = new FadeTransition(Duration.seconds(0.5), content);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }

    private Button createButton(String text) {
        Button button = new Button(text);
        button.setOnMousePressed(event -> screenManager.getAudioManager().playClick());
        button.setPrefWidth(300);
        button.setPrefHeight(44);
        button.setStyle("-fx-background-color: #170d22;-fx-border-color: #6d3a91;-fx-border-width: 1.5px;"
                + "-fx-border-radius: 8px;-fx-background-radius: 8px;-fx-text-fill: white;-fx-font-size: 14px;"
                + "-fx-font-weight: bold;-fx-letter-spacing: 2px;-fx-cursor: hand;");
        button.setOnMouseEntered(event -> button.setStyle("-fx-background-color: #35104a;-fx-border-color: #b65cff;"
                + "-fx-border-width: 2px;-fx-border-radius: 8px;-fx-background-radius: 8px;-fx-text-fill: white;"
                + "-fx-font-size: 14px;-fx-font-weight: bold;-fx-letter-spacing: 2px;-fx-cursor: hand;"
                + "-fx-effect: dropshadow(gaussian, #8b2cff, 15, 0.5, 0, 0);"));
        button.setOnMouseExited(event -> button.setStyle("-fx-background-color: #170d22;-fx-border-color: #6d3a91;"
                + "-fx-border-width: 1.5px;-fx-border-radius: 8px;-fx-background-radius: 8px;-fx-text-fill: white;"
                + "-fx-font-size: 14px;-fx-font-weight: bold;-fx-letter-spacing: 2px;-fx-cursor: hand;"));
        return button;
    }
}
