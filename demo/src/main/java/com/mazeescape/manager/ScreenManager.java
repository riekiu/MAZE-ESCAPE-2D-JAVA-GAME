package com.mazeescape.manager;

import com.mazeescape.screen.CreditsScreen;
import com.mazeescape.screen.GameOverScreen;
import com.mazeescape.screen.GameScreen;
import com.mazeescape.screen.GameSettingsScreen;
import com.mazeescape.screen.HowToPlayScreen;
import com.mazeescape.screen.LeaderboardScreen;
import com.mazeescape.screen.LevelCompleteScreen;
import com.mazeescape.screen.MainMenuScreen;
import com.mazeescape.screen.SettingsScreen;
import com.mazeescape.screen.VictoryScreen;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ScreenManager {

    private static ScreenManager active;

    private final Stage stage;
    private final double width;
    private final double height;
    private final GameManager gameManager;
    private final SettingsManager settingsManager;
    private final LeaderboardManager leaderboardManager;
    private final SaveManager saveManager;
    private final AudioManager audioManager;
    private Parent currentRoot;

    public ScreenManager(Stage stage, double width, double height) {
        this.stage = stage;
        this.width = width;
        this.height = height;
        this.gameManager = new GameManager();
        this.settingsManager = new SettingsManager();
        this.leaderboardManager = new LeaderboardManager();
        this.saveManager = new SaveManager();
        this.saveManager.loadLeaderboard(leaderboardManager);
        this.audioManager = new AudioManager();
        active = this;
    }

    /**
     * Changes the current screen.
     *
     * @param root the root node of the new screen
     */
    public void switchScreen(Parent root) {
        if (currentRoot instanceof GameScreen gameScreen) {
            saveProgress();
            if (root instanceof SettingsScreen) {
                gameScreen.pauseForSettings();
            } else {
                gameScreen.stopGame();
            }
        }
        Scene scene = new Scene(root, width, height);

        stage.setScene(scene);
        stage.show();
        currentRoot = root;
        audioManager.syncVolumes(
                settingsManager.getMasterVolume(),
                settingsManager.getMusicVolume(),
                settingsManager.getSfxVolume());
        if (root instanceof MainMenuScreen) {
            audioManager.playMainMenuMusic();
        } else if (root instanceof GameScreen) {
            audioManager.playHauntedMusic();
            ((GameScreen) root).restoreAfterSettings();
        } else {
            audioManager.stopBackgroundMusic();
        }
    }

    /**
     * Returns the main application window.
     */
    public Stage getStage() {
        return stage;
    }

    /**
     * Returns the default screen width.
     */
    public double getWidth() {
        return width;
    }

    /**
     * Returns the default screen height.
     */
    public double getHeight() {
        return height;
    }

    public static ScreenManager getActive() {
        return active;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public SettingsManager getSettingsManager() {
        return settingsManager;
    }

    public LeaderboardManager getLeaderboardManager() {
        return leaderboardManager;
    }

    public SaveManager getSaveManager() {
        return saveManager;
    }

    public AudioManager getAudioManager() {
        return audioManager;
    }

    public void showMainMenu() {
        switchScreen(new MainMenuScreen(this));
    }

    public void showCampaign() {
        switchScreen(new MainMenuScreen(this, true));
    }

    public void startNewGame() {
        gameManager.resetGame();
        startLevel(1);
    }

    public void continueGame() {
        saveManager.loadGame(gameManager);
        startLevel(gameManager.getCurrentLevel(), false);
    }

    public void startLevel(int level) {
        startLevel(level, true);
    }

    private void startLevel(int level, boolean resetLives) {
        if (!gameManager.isLevelUnlocked(level)) {
            return;
        }
        gameManager.setCurrentLevel(level);
        if (resetLives) {
            gameManager.setLives(3);
        }
        switchScreen(new GameScreen(level, this));
    }

    public void retryLevel(int level) {
        retryLevel(level, 3);
    }

    public void retryLevel(int level, int remainingLives) {
        gameManager.setCurrentLevel(level);
        gameManager.setLives(Math.max(0, remainingLives));
        switchScreen(new GameScreen(level, this));
    }

    public void completeLevel(int level, int score, int remainingTime, int remainingLives) {
        gameManager.setCurrentLevel(level);
        gameManager.addScore(score);
        gameManager.setLives(remainingLives);
        int completionTime = Math.max(0, new com.mazeescape.model.LevelData(level).getTimeLimit()
                - remainingTime);
        leaderboardManager.recordCompletionTime(level, completionTime);
        saveManager.saveLeaderboard(leaderboardManager);
        if (level >= gameManager.getTotalLevels()) {
            saveManager.saveGame(gameManager);
            saveManager.saveLeaderboard(leaderboardManager);
            switchScreen(new VictoryScreen(
                    gameManager.getTotalScore(),
                    gameManager.getTotalLevels(),
                    gameManager.getTotalScore(),
                    this
            ));
            return;
        }

        gameManager.unlockNextLevel();
        gameManager.setCurrentLevel(level + 1);
        saveManager.saveGame(gameManager);
        saveManager.saveLeaderboard(leaderboardManager);
        switchScreen(new LevelCompleteScreen(
                level,
                score,
                remainingTime,
                remainingLives,
                this
        ));
    }

    public void gameOver(int level) {
        gameOver(level, gameManager.getLives());
    }

    public void gameOver(int level, int remainingLives) {
        gameManager.setCurrentLevel(level);
        gameManager.setLives(Math.max(0, remainingLives));
        saveManager.saveGame(gameManager);
        switchScreen(new GameOverScreen(level, remainingLives, this));
    }

    public void showSettings() {
        switchScreen(new SettingsScreen(settingsManager, this));
    }

    public void showSettings(Runnable onBack) {
        switchScreen(new GameSettingsScreen(this, onBack));
    }

    public void returnToGame(GameScreen gameScreen) {
        Scene scene = new Scene(gameScreen, width, height);
        stage.setScene(scene);
        stage.show();
        currentRoot = gameScreen;
        audioManager.syncVolumes(
                settingsManager.getMasterVolume(),
                settingsManager.getMusicVolume(),
                settingsManager.getSfxVolume());
        audioManager.playHauntedMusic();
        gameScreen.restoreAfterSettings();
    }

    public void showLeaderboard() {
        switchScreen(new LeaderboardScreen(leaderboardManager, this));
    }

    public void showCredits() {
        switchScreen(new CreditsScreen(this));
    }

    public void showHowToPlay() {
        switchScreen(new HowToPlayScreen(this));
    }

    public void resetProgress() {
        gameManager.resetGame();
        saveManager.deleteSave();
        leaderboardManager.clearLeaderboard();
    }

    public void saveProgress() {
        if (currentRoot instanceof GameScreen gameScreen) {
            gameManager.setLives(gameScreen.getLives());
        }
        saveManager.saveGame(gameManager);
        saveManager.saveLeaderboard(leaderboardManager);
    }
}
