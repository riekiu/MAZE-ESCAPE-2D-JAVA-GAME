package com.mazeescape.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.AccessDeniedException;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Properties;

public class SaveManager {

    private static final String SAVE_FILE
            = System.getProperty("user.home") + File.separator + "MazeEscapeSave.properties";
    private static final Object SAVE_LOCK = new Object();

    private final Properties properties;

    public SaveManager() {
        properties = new Properties();
        loadProperties();
    }

    // =========================
    // SAVE GAME
    // =========================
    public void saveGame(GameManager gameManager) {
        properties.setProperty("gameSaved", "true");

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
        for (int level = 1; level <= GameManager.TOTAL_LEVELS; level++) {
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
        gameManager.setHighestUnlockedLevel(highestUnlockedLevel);
        gameManager.setTotalScore(totalScore);
        gameManager.setLives(lives);
    }

    public void loadLeaderboard(LeaderboardManager leaderboardManager) {
        leaderboardManager.clearLeaderboard();
        for (int level = 1; level <= GameManager.TOTAL_LEVELS; level++) {
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
        return new File(SAVE_FILE).exists()
                && properties.containsKey("currentLevel");
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
        Path savePath = Path.of(SAVE_FILE);
        Path lockPath = savePath.resolveSibling(savePath.getFileName() + ".lock");
        Path temporaryPath = null;

        // The JVM lock handles multiple SaveManager objects in this game process;
        // the file lock also serializes saves from separate game instances.
        synchronized (SAVE_LOCK) {
            try (FileChannel lockChannel = FileChannel.open(
                    lockPath, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
                 FileLock ignored = lockChannel.lock()) {
                temporaryPath = Files.createTempFile(
                        savePath.getParent(), "MazeEscapeSave-", ".tmp");

                try (FileOutputStream output = new FileOutputStream(temporaryPath.toFile())) {
                    properties.store(output, "Maze Escape Save Data");
                    output.getFD().sync();
                }

                moveSaveWithRetry(temporaryPath, savePath);
                temporaryPath = null;
            } catch (IOException e) {
                System.err.println("Could not save game: " + e.getMessage());
            } finally {
                if (temporaryPath != null) {
                    try {
                        Files.deleteIfExists(temporaryPath);
                    } catch (IOException cleanupError) {
                        System.err.println("Could not remove temporary save file: " + cleanupError.getMessage());
                    }
                }
            }
        }
    }

    private void moveSaveWithRetry(Path temporaryPath, Path savePath) throws IOException {
        for (int attempt = 0; ; attempt++) {
            try {
                try {
                    Files.move(temporaryPath, savePath,
                            StandardCopyOption.REPLACE_EXISTING,
                            StandardCopyOption.ATOMIC_MOVE);
                } catch (AtomicMoveNotSupportedException e) {
                    Files.move(temporaryPath, savePath, StandardCopyOption.REPLACE_EXISTING);
                }
                return;
            } catch (AccessDeniedException e) {
                if (attempt >= 4) {
                    throw e;
                }
                try {
                    Thread.sleep(100L * (attempt + 1));
                } catch (InterruptedException interrupted) {
                    Thread.currentThread().interrupt();
                    throw new IOException("Interrupted while waiting to write the save file", interrupted);
                }
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
