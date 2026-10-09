package com.disorient.manager;

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
import com.disorient.model.CampaignMap;

public class SaveManager {

    private static final String CAMPAIGN_LEVEL_PROGRESS_VERSION = "campaignLevelProgressVersion";
    private static final int CURRENT_CAMPAIGN_LEVEL_PROGRESS_VERSION = 1;
    private static final String SAVE_FILE
            = System.getProperty("user.home") + File.separator + "DisorientSave.properties";
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
        properties.setProperty(CAMPAIGN_LEVEL_PROGRESS_VERSION,
                String.valueOf(CURRENT_CAMPAIGN_LEVEL_PROGRESS_VERSION));

        properties.setProperty(
                "currentLevel",
                String.valueOf(gameManager.getCurrentLevel())
        );

        properties.setProperty(
                "highestUnlockedLevel",
                String.valueOf(gameManager.getHighestUnlockedLevel(CampaignMap.MANSION))
        );

        properties.setProperty(
                "totalScore",
                String.valueOf(gameManager.getTotalScore())
        );

        properties.setProperty(
                "lives",
                String.valueOf(gameManager.getLives())
        );
        properties.setProperty(
                "campaignHighestUnlockedMap",
                String.valueOf(gameManager.getHighestUnlockedCampaignMap())
        );
        properties.setProperty(
                "campaignCompleted",
                String.valueOf(gameManager.isCampaignCompleted())
        );

        for (CampaignMap map : CampaignMap.values()) {
            String prefix = "progress." + map.name() + ".";
            properties.setProperty(prefix + "currentLevel",
                    String.valueOf(gameManager.getCurrentLevel(map)));
            properties.setProperty(prefix + "highestUnlockedLevel",
                    String.valueOf(gameManager.getHighestUnlockedLevel(map)));
            for (int level = 1; level <= gameManager.getTotalLevels(); level++) {
                String key = prefix + "stars." + level;
                properties.remove(key);
                int stars = gameManager.getStarsForLevel(map, level);
                if (stars > 0) {
                    properties.setProperty(key, String.valueOf(stars));
                }
                if (map == CampaignMap.MANSION) {
                    String legacyKey = "stars." + level;
                    properties.remove(legacyKey);
                    if (stars > 0) {
                        properties.setProperty(legacyKey, String.valueOf(stars));
                    }
                }
            }
        }

        saveProperties();
    }

    public void saveLeaderboard(LeaderboardManager leaderboardManager) {
        for (int level = 1; level <= GameManager.TOTAL_LEVELS; level++) {
            properties.remove("bestTime." + level);
            properties.remove("bestName." + level);
            int bestTime = leaderboardManager.getBestTime(level);
            if (bestTime >= 0) {
                properties.setProperty("bestTime." + level, String.valueOf(bestTime));
                for (var entry : leaderboardManager.getEntries()) {
                    if (entry.getLevel() == level) {
                        properties.setProperty("bestName." + level, entry.getPlayerName());
                        break;
                    }
                }
            }
        }
        saveProperties();
    }

    public String loadPlayerName() {
        return properties.getProperty("playerName", "PLAYER");
    }

    public void savePlayerName(String playerName) {
        properties.setProperty("playerName",
                playerName == null || playerName.isBlank() ? "PLAYER" : playerName.strip());
        saveProperties();
    }

    // =========================
    // LOAD GAME
    // =========================
    public void loadGame(GameManager gameManager) {

        boolean hasIndependentCampaignProgress =
                getInt(CAMPAIGN_LEVEL_PROGRESS_VERSION, 0)
                        == CURRENT_CAMPAIGN_LEVEL_PROGRESS_VERSION;
        int totalScore = getInt("totalScore", 0);
        int lives = getInt("lives", 3);

        gameManager.setTotalScore(totalScore);
        gameManager.setLives(lives);
        gameManager.setHighestUnlockedCampaignMap(Math.max(
                CampaignMap.HOSPITAL.ordinal(),
                getInt("campaignHighestUnlockedMap", CampaignMap.HOSPITAL.ordinal())));
        gameManager.setCampaignCompleted(Boolean.parseBoolean(
                properties.getProperty("campaignCompleted", "false")));
        for (CampaignMap map : CampaignMap.values()) {
            String prefix = "progress." + map.name() + ".";
            if (hasIndependentCampaignProgress && properties.containsKey(prefix + "currentLevel")) {
                gameManager.setCurrentLevel(map, getInt(prefix + "currentLevel", 1));
            }
            if (hasIndependentCampaignProgress && properties.containsKey(prefix + "highestUnlockedLevel")) {
                gameManager.setHighestUnlockedLevel(map,
                        getInt(prefix + "highestUnlockedLevel", 1));
            }
            for (int level = 1; level <= gameManager.getTotalLevels(); level++) {
                if (hasIndependentCampaignProgress) {
                    gameManager.setStarsForLevel(map, level,
                            getInt(prefix + "stars." + level, 0));
                }
            }
        }
    }

    public void loadLeaderboard(LeaderboardManager leaderboardManager) {
        leaderboardManager.clearLeaderboard();
        for (int level = 1; level <= GameManager.TOTAL_LEVELS; level++) {
            int bestTime = getInt("bestTime." + level, -1);
            if (bestTime >= 0) {
                leaderboardManager.setBestTime(level, bestTime,
                        properties.getProperty("bestName." + level, "PLAYER"));
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
                        savePath.getParent(), "DisorientSave-", ".tmp");

                try (FileOutputStream output = new FileOutputStream(temporaryPath.toFile())) {
                    properties.store(output, "DISORIENT Save Data");
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
