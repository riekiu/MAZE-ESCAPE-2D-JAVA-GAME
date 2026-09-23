package com.mazeescape.model;

public class LevelData {

    // =========================
    // LEVEL INFORMATION
    // =========================
    private final int levelNumber;
    private final String name;
    private final int timeLimit;
    private final int lives;
    private final int gridSize;
    private final int keyCount;
    private final int doorCount;
    private final long generationSeed;

    // =========================
    // CONSTRUCTOR
    // =========================
    public LevelData(int levelNumber) {

        if (levelNumber < 1 || levelNumber > 50) {
            throw new IllegalArgumentException(
                    "Level must be between 1 and 50."
            );
        }

        this.levelNumber = levelNumber;
        this.name = "Haunted House - Level " + levelNumber;
        this.timeLimit = calculateTimeLimit(levelNumber);
        this.lives = 3;
        this.gridSize = 8 + levelNumber;
        this.keyCount = levelNumber >= 6 ? 2 : 1;
        this.doorCount = levelNumber >= 6 ? 2 : 1;
        this.generationSeed = 0x5EEDL + (levelNumber * 7919L);
    }

    // =========================
    // TIME LIMIT
    // =========================
    private int calculateTimeLimit(int level) {

        if (level <= 5) {
            return 60;
        }
        if (level <= 8) {
            return 90;
        }
        if (level <= 13) {
            return 120;
        }
        if (level <= 17) {
            return 150;
        }
        if (level <= 20) {
            return 180;
        }
        // Nightmare levels continue gaining 30 seconds for each five-level band.
        int nightmareBand = (level - 21) / 5;
        return 210 + nightmareBand * 30;
    }

    // =========================
    // GET LEVEL NUMBER
    // =========================
    public int getLevelNumber() {
        return levelNumber;
    }

    // =========================
    // GET LEVEL NAME
    // =========================
    public String getName() {
        return name;
    }

    // =========================
    // GET TIME LIMIT
    // =========================
    public int getTimeLimit() {
        return timeLimit;
    }

    // =========================
    // GET STARTING LIVES
    // =========================
    public int getLives() {
        return lives;
    }

    public int getGridSize() {
        return gridSize;
    }

    public int getGridWidth() {
        return gridSize;
    }

    public int getGridHeight() {
        return gridSize;
    }

    public int getKeyCount() {
        return keyCount;
    }

    public int getDoorCount() {
        return doorCount;
    }

    public long getGenerationSeed() {
        return generationSeed;
    }

    public String getDifficulty() {
        if (levelNumber <= 6) return "Easy";
        if (levelNumber <= 10) return "Easy–Medium";
        if (levelNumber <= 16) return "Medium";
        if (levelNumber <= 20) return "Medium–Hard";
        if (levelNumber <= 30) return "Hard";
        if (levelNumber <= 40) return "Very Hard";
        return "Extreme";
    }

    // =========================
    // FINAL LEVEL CHECK
    // =========================
    public boolean isFinalLevel() {
        return levelNumber == 50;
    }
}
