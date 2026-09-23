package com.mazeescape.screen;

import com.mazeescape.manager.ScreenManager;
import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
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
        double scale = Math.min(screenManager.getWidth() / 1365.0,
                screenManager.getHeight() / 768.0);
        scale = Math.max(0.75, Math.min(scale, 1.5));

        ImageView logo = createImageView("/img/menu/logo.png", 570 * scale, 321 * scale);

        Button play = createImageButton("play_button", 370 * scale, 104 * scale);
        play.setOnAction(event -> screenManager.playLastPlayedLevel());
        Button campaign = createImageButton("campaign_button", 370 * scale, 92 * scale);
        campaign.setOnAction(event -> screenManager.showCampaign());
        Button settings = createImageButton("settings_button", 100 * scale, 78 * scale);
        settings.setOnAction(event -> screenManager.showSettings());
        Button howToPlay = createImageButton("howtoplay_button", 370 * scale, 80 * scale);
        howToPlay.setOnAction(event -> screenManager.showHowToPlay());
        Button leaderboard = createImageButton("leaderboard_button", 370 * scale, 80 * scale);
        leaderboard.setOnAction(event -> screenManager.showLeaderboard());
        Button credits = createImageButton("credits_button", 100 * scale, 78 * scale);
        credits.setOnAction(event -> screenManager.showCredits());
        Button exit = createImageButton("exit_button", 100 * scale, 78 * scale);
        exit.setOnAction(event -> screenManager.getStage().close());

        VBox buttons = new VBox(8 * scale, play, campaign, howToPlay, leaderboard);
        buttons.setAlignment(Pos.CENTER);
        VBox content = new VBox(12 * scale, logo, buttons);
        content.setAlignment(Pos.CENTER);

        Label settingsLabel = createIconLabel("SETTINGS", scale);
        Label creditsLabel = createIconLabel("CREDITS", scale);
        Label exitLabel = createIconLabel("QUIT", scale);
        Label versionLabel = new Label("v1.0.0");
        versionLabel.setStyle("-fx-text-fill: #b8a4c7;-fx-font-size: " + (11 * scale) + "px;"
                + "-fx-font-family: serif;-fx-effect: dropshadow(gaussian, #100719, 3, 0.8, 0, 1);");
        VBox settingsItem = new VBox(-2 * scale, settings, settingsLabel);
        VBox creditsItem = new VBox(-2 * scale, credits, creditsLabel);
        VBox exitItem = new VBox(-2 * scale, exit, exitLabel);
        settingsItem.setAlignment(Pos.CENTER);
        creditsItem.setAlignment(Pos.CENTER);
        exitItem.setAlignment(Pos.CENTER);
        HBox utilityButtons = new HBox(18 * scale, settingsItem, creditsItem);
        utilityButtons.setAlignment(Pos.CENTER);

        getChildren().add(content);
        AnchorPane cornerControls = new AnchorPane(utilityButtons, exitItem, versionLabel);
        cornerControls.setPickOnBounds(false);
        AnchorPane.setTopAnchor(utilityButtons, 14 * scale);
        AnchorPane.setRightAnchor(utilityButtons, 20 * scale);
        AnchorPane.setBottomAnchor(exitItem, 14 * scale);
        AnchorPane.setRightAnchor(exitItem, 20 * scale);
        AnchorPane.setBottomAnchor(versionLabel, 22 * scale);
        AnchorPane.setLeftAnchor(versionLabel, 28 * scale);
        getChildren().add(cornerControls);
        fadeIn(content);
    }

    private Label createIconLabel(String text, double scale) {
        Label label = new Label(text);
        label.setStyle("-fx-text-fill: #eee5f2;-fx-font-size: " + (11 * scale) + "px;"
                + "-fx-font-family: serif;-fx-font-weight: bold;-fx-letter-spacing: 1px;"
                + "-fx-effect: dropshadow(gaussian, #100719, 3, 0.8, 0, 1);");
        return label;
    }

    private Button createImageButton(String assetName, double fitWidth, double fitHeight) {
        Image normalImage = loadButtonImage(assetName + ".png");
        Image hoverImage = loadButtonImage(assetName + "_hover.png");
        ImageView imageView = new ImageView(normalImage);
        imageView.setFitWidth(fitWidth);
        imageView.setFitHeight(fitHeight);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);

        Button button = new Button();
        button.setGraphic(imageView);
        button.setContentDisplay(javafx.scene.control.ContentDisplay.GRAPHIC_ONLY);
        button.setPadding(Insets.EMPTY);
        button.setStyle("-fx-background-color: transparent;-fx-background-insets: 0;"
                + "-fx-border-color: transparent;-fx-border-insets: 0;-fx-cursor: hand;");
        button.setOnMousePressed(event -> screenManager.getAudioManager().playClick());
        button.setOnMouseEntered(event -> imageView.setImage(hoverImage));
        button.setOnMouseExited(event -> imageView.setImage(normalImage));
        button.setFocusTraversable(false);
        return button;
    }

    private Image loadButtonImage(String fileName) {
        String resourcePath = "/img/buttons/" + fileName;
        var resource = getClass().getResource(resourcePath);
        if (resource == null) {
            throw new IllegalStateException("Missing image resource: " + resourcePath);
        }
        Image image = new Image(resource.toExternalForm(), false);
        PixelReader pixels = image.getPixelReader();
        if (pixels == null) {
            return image;
        }

        int left = (int) image.getWidth();
        int top = (int) image.getHeight();
        int right = -1;
        int bottom = -1;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                if (pixels.getArgb(x, y) >>> 24 != 0) {
                    left = Math.min(left, x);
                    top = Math.min(top, y);
                    right = Math.max(right, x);
                    bottom = Math.max(bottom, y);
                }
            }
        }

        if (right < left || bottom < top) {
            return image;
        }
        return new WritableImage(pixels, left, top, right - left + 1, bottom - top + 1);
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
