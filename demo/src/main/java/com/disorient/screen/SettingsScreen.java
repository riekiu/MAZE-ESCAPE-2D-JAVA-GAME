package com.disorient.screen;

import com.disorient.manager.SettingsManager;
import com.disorient.manager.ScreenManager;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class SettingsScreen extends StackPane {

    public SettingsScreen(SettingsManager settingsManager) {
        this(settingsManager, ScreenManager.getActive());
    }

    public SettingsScreen(SettingsManager settingsManager, ScreenManager screenManager) {
        this(settingsManager, screenManager, screenManager::showMainMenu);
    }

    public SettingsScreen(
            SettingsManager settingsManager,
            ScreenManager screenManager,
            Runnable onBack
    ) {
        this(settingsManager, screenManager, onBack, "SETTINGS",
                "CUSTOMIZE YOUR HAUNTED HOUSE EXPERIENCE", "BACK", true);
    }

    protected SettingsScreen(
            SettingsManager settingsManager,
            ScreenManager screenManager,
            Runnable onBack,
            String titleText,
            String subtitleText,
            String backText,
            boolean showResetProgress
    ) {

        final SettingsManager manager = java.util.Objects.requireNonNull(settingsManager, "settingsManager");

        // =========================
        // BACKGROUND
        // =========================
        HorrorUiTheme.applyBackdrop(this);

        // =========================
        // TITLE
        // =========================
        Label title = new Label(titleText);

        title.setStyle(
                "-fx-text-fill: white;"
                + "-fx-font-size: 36px;"
                + "-fx-font-weight: bold;"
                + "-fx-letter-spacing: 5px;"
                + "-fx-font-family: Georgia;"
                + "-fx-effect: dropshadow(gaussian, #b84cff, 20, 0.6, 0, 0);"
        );

        Label subtitle = new Label(subtitleText);

        subtitle.setStyle(
                "-fx-text-fill: #b99acb;"
                + "-fx-font-size: 13px;"
                + "-fx-letter-spacing: 1.5px;"
        );

        // =========================
        // AUDIO SETTINGS
        // =========================
        Label audioTitle = createSectionTitle("AUDIO");

        Slider masterSlider = createSlider(
                manager.getMasterVolume()
        );

        Slider musicSlider = createSlider(
                manager.getMusicVolume()
        );

        Slider sfxSlider = createSlider(
                manager.getSfxVolume()
        );

        masterSlider.valueProperty().addListener(
                (observable, oldValue, newValue)
                -> {
                    double volume = newValue.doubleValue();
                    manager.setMasterVolume(volume);
                    screenManager.getAudioManager().setMasterVolume(volume);
                }
        );

        musicSlider.valueProperty().addListener(
                (observable, oldValue, newValue)
                -> {
                    double volume = newValue.doubleValue();
                    manager.setMusicVolume(volume);
                    screenManager.getAudioManager().setMusicVolume(volume);
                }
        );

        sfxSlider.valueProperty().addListener(
                (observable, oldValue, newValue)
                -> {
                    double volume = newValue.doubleValue();
                    manager.setSfxVolume(volume);
                    screenManager.getAudioManager().setSfxVolume(volume);
                }
        );

        VBox audioBox = new VBox(
                8,
                createSliderRow("Master Volume", masterSlider),
                createSliderRow("Music Volume", musicSlider),
                createSliderRow("SFX Volume", sfxSlider)
        );
        audioBox.setPadding(new javafx.geometry.Insets(14, 18, 14, 18));
        audioBox.setStyle("-fx-background-color: rgba(22, 15, 29, 0.88);"
                + "-fx-border-color: rgba(169, 135, 93, 0.75);-fx-border-radius: 8px;"
                + "-fx-background-radius: 8px;");

        Label playerLabel = new Label("LEADERBOARD NAME");
        playerLabel.setStyle("-fx-text-fill: #e8c98a;-fx-font-size: 10px;-fx-font-weight: bold;"
                + "-fx-letter-spacing: 1.5px;");
        TextField playerNameField = new TextField(manager.getPlayerName());
        playerNameField.setPromptText("Enter a name");
        playerNameField.setPrefColumnCount(16);
        playerNameField.setTextFormatter(new TextFormatter<String>(change ->
                change.getControlNewText().length() <= 20
                        && !change.getText().contains("\n") ? change : null));
        playerNameField.setStyle("-fx-background-color: #120d18;-fx-border-color: #72557d;"
                + "-fx-border-radius: 5px;-fx-background-radius: 5px;-fx-text-fill: #fff3dc;"
                + "-fx-prompt-text-fill: #887995;-fx-highlight-fill: #754c8b;");
        Label playerNameStatus = new Label("Displayed with your personal best times.");
        playerNameStatus.setStyle("-fx-text-fill: #ad9ab7;-fx-font-size: 10px;");
        Button savePlayerName = createButton("SAVE NAME");
        Runnable saveName = () -> {
            String requestedName = playerNameField.getText();
            if (requestedName == null || requestedName.isBlank()) {
                playerNameStatus.setText("Enter a name before saving.");
                playerNameStatus.setStyle("-fx-text-fill: #e99283;-fx-font-size: 10px;");
                return;
            }
            screenManager.savePlayerName(requestedName);
            playerNameField.setText(manager.getPlayerName());
            playerNameStatus.setText("Name saved for leaderboard records.");
            playerNameStatus.setStyle("-fx-text-fill: #9bd5ae;-fx-font-size: 10px;");
        };
        savePlayerName.setOnAction(event -> saveName.run());
        playerNameField.setOnAction(event -> saveName.run());
        HBox nameInput = new HBox(10, playerNameField, savePlayerName);
        nameInput.setAlignment(Pos.CENTER);
        VBox playerNameBox = new VBox(8, playerLabel, nameInput, playerNameStatus);
        playerNameBox.setAlignment(Pos.CENTER);
        playerNameBox.setPadding(new javafx.geometry.Insets(12, 18, 12, 18));
        playerNameBox.setStyle("-fx-background-color: rgba(22, 15, 29, 0.88);"
                + "-fx-border-color: rgba(169, 135, 93, 0.75);-fx-border-radius: 8px;"
                + "-fx-background-radius: 8px;");

        // =========================
        // RESET BUTTONS
        // =========================
        Button resetProgress
                = createButton("RESET PROGRESS");

        resetProgress.setOnAction(event
                -> screenManager.resetProgress()
        );
        resetProgress.setVisible(showResetProgress);
        resetProgress.setManaged(showResetProgress);

        Button resetSettings
                = createButton("RESET SETTINGS");

        resetSettings.setOnAction(event -> {

            manager.resetSettings();
            screenManager.getAudioManager().syncVolumes(
                    manager.getMasterVolume(),
                    manager.getMusicVolume(),
                    manager.getSfxVolume());

            masterSlider.setValue(
                    manager.getMasterVolume()
            );

            musicSlider.setValue(
                    manager.getMusicVolume()
            );

            sfxSlider.setValue(
                    manager.getSfxVolume()
            );

        });

        // =========================
        // BACK BUTTON
        // =========================
        Button backButton
                = createButton(backText);

        backButton.setOnAction(event -> onBack.run());

        // =========================
        // RESET CONTAINER
        // =========================
        HBox resetButtons = new HBox(
                12,
                resetProgress,
                resetSettings
        );

        resetButtons.setAlignment(Pos.CENTER);

        // =========================
        // MAIN CONTENT
        // =========================
        VBox content = new VBox(
                11,
                title,
                subtitle,
                audioTitle,
                audioBox,
                playerNameBox,
                resetButtons,
                backButton
        );

        content.setAlignment(Pos.CENTER);

        content.setMaxWidth(590);
        content.setPadding(new javafx.geometry.Insets(24, 30, 24, 30));
        content.setStyle("-fx-background-color: rgba(13, 8, 19, 0.94);"
                + "-fx-background-radius: 14px;-fx-border-color: #987452;"
                + "-fx-border-radius: 14px;-fx-border-width: 1.5px;");
        content.setEffect(new javafx.scene.effect.DropShadow(24, javafx.scene.paint.Color.rgb(0, 0, 0, 0.55)));

        getChildren().add(content);
    }

    // =========================
    // SECTION TITLE
    // =========================
    private Label createSectionTitle(String text) {

        Label label = new Label(text);

        label.setStyle(
                "-fx-text-fill: #c86cff;"
                + "-fx-font-size: 14px;"
                + "-fx-font-weight: bold;"
                + "-fx-letter-spacing: 3px;"
        );

        return label;
    }

    // =========================
    // SLIDER
    // =========================
    private Slider createSlider(double value) {

        Slider slider = new Slider(0, 1, value);
        slider.setOnMousePressed(event ->
                ScreenManager.getActive().getAudioManager().playClick());

        slider.setPrefWidth(250);

        slider.setShowTickLabels(false);
        slider.setShowTickMarks(false);

        return slider;
    }

    // =========================
    // SLIDER ROW
    // =========================
    private HBox createSliderRow(
            String text,
            Slider slider
    ) {

        Label label = new Label(text);

        label.setPrefWidth(130);

        label.setStyle(
                "-fx-text-fill: white;"
                + "-fx-font-size: 13px;"
        );

        HBox row = new HBox(
                15,
                label,
                slider
        );

        row.setAlignment(Pos.CENTER);

        return row;
    }

    // =========================
    // BUTTON
    // =========================
    private Button createButton(String text) {

        Button button = new Button(text);
        button.setPrefWidth(180);
        button.setPrefHeight(42);
        HorrorUiTheme.styleButton(button, ScreenManager.getActive().getAudioManager());

        return button;
    }
}
