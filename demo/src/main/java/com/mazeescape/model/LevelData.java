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

        if (levelNumber < 1 || levelNumber > 20) {
            throw new IllegalArgumentException(
                    "Level must be between 1 and 20."
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

        if (level <= 10) {
            return 120;
        }
        if (level <= 15) {
            return 150;
        }
        return 180;
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
        if (levelNumber <= 5) return "EASY";
        if (levelNumber <= 10) return "NORMAL";
        if (levelNumber <= 15) return "HARD";
        return "NIGHTMARE";
    }

    // =========================
    // FINAL LEVEL CHECK
    // =========================
    public boolean isFinalLevel() {
        return levelNumber == 20;
    }
}
