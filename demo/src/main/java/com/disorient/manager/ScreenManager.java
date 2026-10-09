package com.disorient.manager;

import com.disorient.screen.CreditsScreen;
import com.disorient.screen.CampaignScreen;
import com.disorient.screen.CampaignMapScreen;
import com.disorient.screen.GameOverScreen;
import com.disorient.screen.GameScreen;
import com.disorient.screen.GameSettingsScreen;
import com.disorient.screen.HowToPlayScreen;
import com.disorient.screen.LeaderboardScreen;
import com.disorient.screen.LevelCompleteScreen;
import com.disorient.screen.MainMenuScreen;
import com.disorient.screen.SettingsScreen;
import com.disorient.screen.VictoryScreen;
import com.disorient.model.CampaignMap;
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
    private CampaignMap activeCampaignMap = CampaignMap.MANSION;

    public ScreenManager(Stage stage, double width, double height) {
        this.stage = stage;
        this.width = width;
        this.height = height;
        this.gameManager = new GameManager();
        this.settingsManager = new SettingsManager();
        this.leaderboardManager = new LeaderboardManager();
        this.saveManager = new SaveManager();
        this.saveManager.loadLeaderboard(leaderboardManager);
        settingsManager.setPlayerName(saveManager.loadPlayerName());
        leaderboardManager.setPlayerName(settingsManager.getPlayerName());
        if (this.saveManager.hasSaveData()) {
            this.saveManager.loadGame(gameManager);
        } else {
            this.saveManager.saveGame(gameManager);
        }
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
        switchScreen(new CampaignScreen(this));
    }

    public void showCampaignMaps() {
        switchScreen(new CampaignMapScreen(this));
    }

    public void showCampaignMapLevels(CampaignMap map) {
        if (!gameManager.isCampaignMapUnlocked(map)) {
            showCampaignMaps();
            return;
        }
        activeCampaignMap = map;
        switchScreen(new CampaignScreen(this, map));
    }

    public void startNewGame() {
        gameManager.resetGame();
        activeCampaignMap = CampaignMap.MANSION;
        startLevel(1);
    }

    public void playLastPlayedLevel() {
        saveManager.loadGame(gameManager);
        activeCampaignMap = CampaignMap.MANSION;
        startLevel(gameManager.getCurrentLevel(), false);
    }

    public void startLevel(int level) {
        activeCampaignMap = CampaignMap.MANSION;
        startLevel(level, true);
    }

    private void startLevel(int level, boolean resetLives) {
        if (!gameManager.isLevelUnlocked(CampaignMap.MANSION, level)) {
            return;
        }
        gameManager.setCurrentLevel(CampaignMap.MANSION, level);
        if (resetLives) {
            gameManager.setLives(3);
        }
        saveManager.saveGame(gameManager);
        switchScreen(new GameScreen(level, this));
    }

    public void startCampaignMap(CampaignMap map) {
        if (!gameManager.isCampaignMapUnlocked(map)) {
            return;
        }
        activeCampaignMap = map;
        switchScreen(new CampaignScreen(this, map));
    }

    public void startCampaignMapLevel(CampaignMap map, int level) {
        if (!gameManager.isCampaignMapUnlocked(map)
                || !gameManager.isLevelUnlocked(map, level)) {
            return;
        }
        activeCampaignMap = map;
        gameManager.setCurrentLevel(map, level);
        gameManager.setLives(3);
        saveManager.saveGame(gameManager);
        switchScreen(map == CampaignMap.MANSION
                ? new GameScreen(level, this)
                : new GameScreen(level, this, map));
    }

    public void retryLevel(int level) {
        gameManager.setCurrentLevel(activeCampaignMap, level);
        gameManager.resetLives();
        switchScreen(new GameScreen(level, this, activeCampaignMap));
    }

    public void completeLevel(int level, int score, int remainingTime, int remainingLives) {
        if (activeCampaignMap == CampaignMap.MANSION) {
            gameManager.completeCampaignMap(CampaignMap.MANSION);
        }
        gameManager.setCurrentLevel(activeCampaignMap, level);
        gameManager.addScore(score);
        gameManager.setLives(remainingLives);
        gameManager.recordLevelStars(activeCampaignMap, level, remainingLives);
        int completionTime = Math.max(0, new com.disorient.model.LevelData(level).getTimeLimit()
                - remainingTime);
        leaderboardManager.recordCompletionTime(level, completionTime);
        saveManager.saveLeaderboard(leaderboardManager);
        if (level >= gameManager.getTotalLevels()) {
            gameManager.completeCampaignMap(activeCampaignMap);
            saveManager.saveGame(gameManager);
            saveManager.saveLeaderboard(leaderboardManager);
            switchScreen(new VictoryScreen(
                    gameManager.getTotalScore(),
                    gameManager.getTotalLevels(),
                    gameManager.getTotalScore(),
                    this,
                    activeCampaignMap
            ));
            return;
        }

        gameManager.unlockNextLevel(activeCampaignMap);
        gameManager.setCurrentLevel(activeCampaignMap, level + 1);
        saveManager.saveGame(gameManager);
        saveManager.saveLeaderboard(leaderboardManager);
        switchScreen(new LevelCompleteScreen(
                level,
                score,
                remainingTime,
                remainingLives,
                this,
                activeCampaignMap
        ));
    }

    public void gameOver(int level) {
        gameOver(level, gameManager.getLives());
    }

    public void gameOver(int level, int remainingLives) {
        gameManager.setCurrentLevel(activeCampaignMap, level);
        gameManager.setLives(Math.max(0, remainingLives));
        saveManager.saveGame(gameManager);
        switchScreen(new GameOverScreen(level, remainingLives, this, activeCampaignMap));
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
        saveManager.savePlayerName(settingsManager.getPlayerName());
    }

    public void saveProgress() {
        if (currentRoot instanceof GameScreen gameScreen) {
            gameManager.setLives(gameScreen.getLives());
        }
        saveManager.saveGame(gameManager);
        saveManager.saveLeaderboard(leaderboardManager);
        saveManager.savePlayerName(settingsManager.getPlayerName());
    }

    public void savePlayerName(String playerName) {
        settingsManager.setPlayerName(playerName);
        leaderboardManager.setPlayerName(settingsManager.getPlayerName());
        saveManager.savePlayerName(settingsManager.getPlayerName());
        saveManager.saveLeaderboard(leaderboardManager);
    }
}
