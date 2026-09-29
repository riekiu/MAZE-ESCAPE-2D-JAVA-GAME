package com.disorient.manager;

public class GameManager {

    // Total number of levels in the game
    public static final int TOTAL_LEVELS = 50;

    // Current game state
    private int currentLevel;
    private int highestUnlockedLevel;
    private int totalScore;
    private int lives;
    private final int[] levelStars = new int[TOTAL_LEVELS + 1];

    public GameManager() {
        currentLevel = 1;
        highestUnlockedLevel = TOTAL_LEVELS;
        totalScore = 0;
        lives = 3;
    }

    // -------------------------
    // Level Management
    // -------------------------
    public int getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(int level) {
        if (level >= 1 && level <= TOTAL_LEVELS) {
            currentLevel = level;
        }
    }

    public int getHighestUnlockedLevel() {
        return highestUnlockedLevel;
    }

    public void setHighestUnlockedLevel(int level) {
        if (level >= 1 && level <= TOTAL_LEVELS) {
            highestUnlockedLevel = level;
        }
    }

    public void unlockNextLevel() {
        if (highestUnlockedLevel < TOTAL_LEVELS) {
            highestUnlockedLevel++;
        }
    }

    public boolean isLevelUnlocked(int level) {
        return level >= 1 && level <= highestUnlockedLevel;
    }

    public boolean isFinalLevel() {
        return currentLevel == TOTAL_LEVELS;
    }

    public int getTotalLevels() {
        return TOTAL_LEVELS;
    }

    public int getStarsForLevel(int level) {
        return isValidLevel(level) ? levelStars[level] : 0;
    }

    public void recordLevelStars(int level, int stars) {
        if (isValidLevel(level)) {
            levelStars[level] = Math.max(levelStars[level], Math.min(3, Math.max(0, stars)));
        }
    }

    public void setStarsForLevel(int level, int stars) {
        if (isValidLevel(level)) {
            levelStars[level] = Math.min(3, Math.max(0, stars));
        }
    }

    private boolean isValidLevel(int level) {
        return level >= 1 && level <= TOTAL_LEVELS;
    }

    public void resetLevelStars() {
        java.util.Arrays.fill(levelStars, 0);
    }

    // -------------------------
    // Score Management
    // -------------------------
    public int getTotalScore() {
        return totalScore;
    }

    public void addScore(int score) {
        if (score > 0) {
            totalScore += score;
        }
    }

    public void setTotalScore(int score) {
        totalScore = Math.max(0, score);
    }

    public void resetScore() {
        totalScore = 0;
    }

    // -------------------------
    // Life Management
    // -------------------------
    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        this.lives = Math.max(0, lives);
    }

    public void loseLife() {
        if (lives > 0) {
            lives--;
        }
    }

    public void resetLives() {
        lives = 3;
    }

    public boolean hasLivesLeft() {
        return lives > 0;
    }

    // -------------------------
    // Game Progression
    // -------------------------
    public void completeCurrentLevel() {

        if (currentLevel < TOTAL_LEVELS) {
            unlockNextLevel();
            currentLevel++;
        }
    }

    public void restartLevel() {
        lives = 3;
    }

    public void resetGame() {
        currentLevel = 1;
        highestUnlockedLevel = TOTAL_LEVELS;
        totalScore = 0;
        lives = 3;
        resetLevelStars();
    }
}
