package com.mazeescape.manager;

import com.mazeescape.model.LevelData;

public class LevelManager {

    private static final int TOTAL_LEVELS = 20;

    private int currentLevel;

    public LevelManager() {
        currentLevel = 1;
    }

    // -------------------------
    // Current Level
    // -------------------------
    public int getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(int level) {
        if (level >= 1 && level <= TOTAL_LEVELS) {
            currentLevel = level;
        }
    }

    public void nextLevel() {
        if (currentLevel < TOTAL_LEVELS) {
            currentLevel++;
        }
    }

    public void previousLevel() {
        if (currentLevel > 1) {
            currentLevel--;
        }
    }

    // -------------------------
    // Level Information
    // -------------------------
    public int getTotalLevels() {
        return TOTAL_LEVELS;
    }

    public boolean isFirstLevel() {
        return currentLevel == 1;
    }

    public boolean isFinalLevel() {
        return currentLevel == TOTAL_LEVELS;
    }

    public boolean isValidLevel(int level) {
        return level >= 1 && level <= TOTAL_LEVELS;
    }

    // -------------------------
    // Level Data
    // -------------------------
    /**
     * Creates the data for the requested level. The actual maze layouts will be
     * added later.
     */
    public LevelData getLevelData(int level) {

        if (!isValidLevel(level)) {
            throw new IllegalArgumentException(
                    "Level must be between 1 and " + TOTAL_LEVELS
            );
        }

        return new LevelData(level);
    }

    /**
     * Gets the data for the current level.
     */
    public LevelData getCurrentLevelData() {
        return getLevelData(currentLevel);
    }

    // -------------------------
    // Reset
    // -------------------------
    public void reset() {
        currentLevel = 1;
    }
}
