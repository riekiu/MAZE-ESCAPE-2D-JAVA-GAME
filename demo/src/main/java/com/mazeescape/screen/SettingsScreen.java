package com.mazeescape.screen;

import com.mazeescape.manager.SettingsManager;
import com.mazeescape.manager.ScreenManager;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
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
                "CUSTOMIZE YOUR HAUNTED HOUSE EXPERIENCE", "BACK", true, true);
    }

    protected SettingsScreen(
            SettingsManager settingsManager,
            ScreenManager screenManager,
            Runnable onBack,
            String titleText,
            String subtitleText,
            String backText,
            boolean showGameplaySettings,
            boolean showResetProgress
    ) {

        final SettingsManager manager = java.util.Objects.requireNonNull(settingsManager, "settingsManager");

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
        Label title = new Label(titleText);

        title.setStyle(
                "-fx-text-fill: white;"
                + "-fx-font-size: 42px;"
                + "-fx-font-weight: bold;"
                + "-fx-letter-spacing: 5px;"
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

        // =========================
        // GAMEPLAY SETTINGS
        // =========================
        Label gameplayTitle = createSectionTitle("GAMEPLAY");

        CheckBox showTimer
                = createCheckBox(
                        "Show Timer",
                        manager.isShowTimer()
                );

        CheckBox screenShake
                = createCheckBox(
                        "Screen Shake",
                        manager.isScreenShake()
                );

        CheckBox animations
                = createCheckBox(
                        "Animations",
                        manager.isAnimations()
                );

        showTimer.setOnAction(event
                -> manager.setShowTimer(
                        showTimer.isSelected()
                )
        );

        screenShake.setOnAction(event
                -> manager.setScreenShake(
                        screenShake.isSelected()
                )
        );

        animations.setOnAction(event
                -> manager.setAnimations(
                        animations.isSelected()
                )
        );

        VBox gameplayBox = new VBox(
                8,
                showTimer,
                screenShake,
                animations
        );
        gameplayBox.setVisible(showGameplaySettings);
        gameplayBox.setManaged(showGameplaySettings);
        gameplayTitle.setVisible(showGameplaySettings);
        gameplayTitle.setManaged(showGameplaySettings);

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

            showTimer.setSelected(
                    manager.isShowTimer()
            );

            screenShake.setSelected(
                    manager.isScreenShake()
            );

            animations.setSelected(
                    manager.isAnimations()
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
                12,
                title,
                subtitle,
                audioTitle,
                audioBox,
                gameplayTitle,
                gameplayBox,
                resetButtons,
                backButton
        );

        content.setAlignment(Pos.CENTER);

        content.setMaxWidth(500);

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
    // CHECKBOX
    // =========================
    private CheckBox createCheckBox(
            String text,
            boolean selected
    ) {

        CheckBox checkBox = new CheckBox(text);

        checkBox.setSelected(selected);
        checkBox.setOnMousePressed(event ->
                ScreenManager.getActive().getAudioManager().playClick());

        checkBox.setStyle(
                "-fx-text-fill: white;"
                + "-fx-font-size: 14px;"
                + "-fx-cursor: hand;"
        );

        return checkBox;
    }

    // =========================
    // BUTTON
    // =========================
    private Button createButton(String text) {

        Button button = new Button(text);
        button.setOnMousePressed(event -> ScreenManager.getActive().getAudioManager().playClick());

        button.setPrefWidth(180);
        button.setPrefHeight(42);

        button.setStyle(
                "-fx-background-color: #170d22;"
                + "-fx-border-color: #6d3a91;"
                + "-fx-border-width: 1.5px;"
                + "-fx-border-radius: 8px;"
                + "-fx-background-radius: 8px;"
                + "-fx-text-fill: white;"
                + "-fx-font-size: 12px;"
                + "-fx-font-weight: bold;"
                + "-fx-letter-spacing: 1.5px;"
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
                    + "-fx-font-size: 12px;"
                    + "-fx-font-weight: bold;"
                    + "-fx-letter-spacing: 1.5px;"
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
                    + "-fx-font-size: 12px;"
                    + "-fx-font-weight: bold;"
                    + "-fx-letter-spacing: 1.5px;"
                    + "-fx-cursor: hand;"
            );
        });

        return button;
    }
}
