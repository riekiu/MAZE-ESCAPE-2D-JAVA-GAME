package com.mazeescape;

import javafx.application.Application;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;
import com.mazeescape.manager.ScreenManager;
import com.mazeescape.screen.LoadingScreen;

public class MazeEscapeApplication extends Application {

    private ScreenManager screenManager;

    @Override
    public void start(Stage stage) {
        stage.setTitle("Maze Escape");
        stage.setResizable(false);
        stage.setFullScreenExitHint("");
        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        stage.setFullScreen(true);

        screenManager = new ScreenManager(stage, 1280, 720);
        stage.setOnCloseRequest(event -> screenManager.saveProgress());
        screenManager.switchScreen(new LoadingScreen(screenManager::showMainMenu));
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
