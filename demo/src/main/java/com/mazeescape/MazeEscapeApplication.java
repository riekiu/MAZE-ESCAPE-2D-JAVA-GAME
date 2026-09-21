package com.mazeescape;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.input.KeyCombination;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import com.mazeescape.manager.ScreenManager;
import com.mazeescape.screen.LoadingScreen;

public class MazeEscapeApplication extends Application {

    private ScreenManager screenManager;

    @Override
    public void start(Stage stage) {
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle("Maze Escape");
        stage.setResizable(true);
        stage.setFullScreenExitHint("");
        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        stage.setWidth(Screen.getPrimary().getBounds().getWidth());
        stage.setHeight(Screen.getPrimary().getBounds().getHeight());

        javafx.geometry.Rectangle2D screenBounds = Screen.getPrimary().getBounds();
        screenManager = new ScreenManager(
                stage,
                screenBounds.getWidth(),
                screenBounds.getHeight());
        stage.setOnCloseRequest(event -> screenManager.saveProgress());
        screenManager.switchScreen(new LoadingScreen(screenManager::showMainMenu));
        stage.setOnShown(event -> {
            stage.setMaximized(true);
            stage.setFullScreen(true);
            stage.toFront();
            stage.requestFocus();
        });
        Platform.runLater(() -> {
            stage.setMaximized(true);
            stage.setFullScreen(true);
            stage.toFront();
            stage.requestFocus();
        });
    }

    @Override
    public void stop() {
        if (screenManager != null) {
            screenManager.saveProgress();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
