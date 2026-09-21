package com.mazeescape.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.StandardCopyOption;
import java.util.Properties;

public class SaveManager {

    private static final String SAVE_FILE
            = System.getProperty("user.home") + File.separator + "MazeEscapeSave.properties";

    private final Properties properties;

    public SaveManager() {
        properties = new Properties();
        loadProperties();
    }

    // =========================
    // SAVE GAME
    // =========================
    public void saveGame(GameManager gameManager) {

        properties.setProperty(
                "currentLevel",
                String.valueOf(gameManager.getCurrentLevel())
        );

        properties.setProperty(
                "highestUnlockedLevel",
                String.valueOf(gameManager.getHighestUnlockedLevel())
        );

        properties.setProperty(
                "totalScore",
                String.valueOf(gameManager.getTotalScore())
        );

        properties.setProperty(
                "lives",
                String.valueOf(gameManager.getLives())
        );

        saveProperties();
    }

    public void saveLeaderboard(LeaderboardManager leaderboardManager) {
        for (int level = 1; level <= 20; level++) {
            properties.remove("bestTime." + level);
            int bestTime = leaderboardManager.getBestTime(level);
            if (bestTime >= 0) {
                properties.setProperty("bestTime." + level, String.valueOf(bestTime));
            }
        }
        saveProperties();
    }

    // =========================
    // LOAD GAME
    // =========================
    public void loadGame(GameManager gameManager) {

        int currentLevel = getInt("currentLevel", 1);
        int highestUnlockedLevel = getInt("highestUnlockedLevel", gameManager.getTotalLevels());
        int totalScore = getInt("totalScore", 0);
        int lives = getInt("lives", 3);

        gameManager.setCurrentLevel(currentLevel);
        gameManager.setHighestUnlockedLevel(
                Math.max(highestUnlockedLevel, gameManager.getTotalLevels()));
        gameManager.setTotalScore(totalScore);
        gameManager.setLives(lives);
    }

    public void loadLeaderboard(LeaderboardManager leaderboardManager) {
        leaderboardManager.clearLeaderboard();
        for (int level = 1; level <= 20; level++) {
            int bestTime = getInt("bestTime." + level, -1);
            if (bestTime >= 0) {
                leaderboardManager.setBestTime(level, bestTime);
            }
        }
    }

    // =========================
    // CHECK SAVE
    // =========================
    public boolean hasSaveData() {
        return new File(SAVE_FILE).exists();
    }

    // =========================
    // DELETE SAVE
    // =========================
    public void deleteSave() {

        File saveFile = new File(SAVE_FILE);

        if (saveFile.exists()) {
            if (!saveFile.delete()) {
                System.err.println("Unable to delete save file.");
            }
        }

        properties.clear();
    }

    // =========================
    // FILE OPERATIONS
    // =========================
    private void saveProperties() {
        File saveFile = new File(SAVE_FILE);
        File temporaryFile = new File(SAVE_FILE + ".tmp");
        try (FileOutputStream output = new FileOutputStream(temporaryFile)) {
            properties.store(
                    output,
                    "Maze Escape Save Data"
            );
            output.getFD().sync();
            try {
                Files.move(
                        temporaryFile.toPath(),
                        saveFile.toPath(),
                        StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE
                );
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(
                        temporaryFile.toPath(),
                        saveFile.toPath(),
                        StandardCopyOption.REPLACE_EXISTING
                );
            }
        } catch (IOException e) {
            System.err.println(
                    "Could not save game: " + e.getMessage()
            );
            if (temporaryFile.exists() && !temporaryFile.delete()) {
                System.err.println("Could not remove temporary save file.");
            }
        }
    }

    private void loadProperties() {

        File saveFile = new File(SAVE_FILE);

        if (!saveFile.exists()) {
            return;
        }

        try (FileInputStream input
                = new FileInputStream(SAVE_FILE)) {

            properties.load(input);

        } catch (IOException e) {
            System.err.println(
                    "Could not load save data: " + e.getMessage()
            );
        }
    }

    // =========================
    // HELPER
    // =========================
    private int getInt(String key, int defaultValue) {

        String value = properties.getProperty(key);

        if (value == null) {
            return defaultValue;
        }

        try {
            return Integer.parseInt(value);

        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
