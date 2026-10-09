package com.disorient.manager;

import com.disorient.model.CampaignMap;

public class GameManager {

    // Total number of levels in the game
    public static final int TOTAL_LEVELS = 50;

    // Current game state
    private int currentLevel;
    private int totalScore;
    private int lives;
    private final int[] currentCampaignLevels = new int[CampaignMap.values().length];
    private final int[] highestUnlockedCampaignLevels = new int[CampaignMap.values().length];
    private final int[][] campaignLevelStars =
            new int[CampaignMap.values().length][TOTAL_LEVELS + 1];
    private int highestUnlockedCampaignMap;
    private boolean campaignCompleted;

    public GameManager() {
        currentLevel = 1;
        totalScore = 0;
        lives = 3;
        java.util.Arrays.fill(currentCampaignLevels, 1);
        java.util.Arrays.fill(highestUnlockedCampaignLevels, TOTAL_LEVELS);
        highestUnlockedCampaignMap = CampaignMap.HOSPITAL.ordinal();
        campaignCompleted = false;
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
            currentCampaignLevels[CampaignMap.MANSION.ordinal()] = level;
        }
    }

    public int getCurrentLevel(CampaignMap map) {
        return map == null ? currentLevel : currentCampaignLevels[map.ordinal()];
    }

    public void setCurrentLevel(CampaignMap map, int level) {
        if (map != null && isValidLevel(level)) {
            currentCampaignLevels[map.ordinal()] = level;
            if (map == CampaignMap.MANSION) {
                currentLevel = level;
            }
        }
    }

    public int getHighestUnlockedLevel() {
        return getHighestUnlockedLevel(CampaignMap.MANSION);
    }

    public void setHighestUnlockedLevel(int level) {
        setHighestUnlockedLevel(CampaignMap.MANSION, level);
    }

    public int getHighestUnlockedLevel(CampaignMap map) {
        return map == null ? 1 : highestUnlockedCampaignLevels[map.ordinal()];
    }

    public void setHighestUnlockedLevel(CampaignMap map, int level) {
        if (map != null) {
            highestUnlockedCampaignLevels[map.ordinal()] = TOTAL_LEVELS;
        }
    }

    public void unlockNextLevel() {
        unlockNextLevel(CampaignMap.MANSION);
    }

    public void unlockNextLevel(CampaignMap map) {
        if (map != null && highestUnlockedCampaignLevels[map.ordinal()] < TOTAL_LEVELS) {
            highestUnlockedCampaignLevels[map.ordinal()]++;
        }
    }

    public boolean isLevelUnlocked(int level) {
        return isLevelUnlocked(CampaignMap.MANSION, level);
    }

    public boolean isLevelUnlocked(CampaignMap map, int level) {
        return map != null && level >= 1
                && level <= highestUnlockedCampaignLevels[map.ordinal()];
    }

    public boolean isFinalLevel() {
        return currentLevel == TOTAL_LEVELS;
    }

    public int getTotalLevels() {
        return TOTAL_LEVELS;
    }

    public int getStarsForLevel(int level) {
        return getStarsForLevel(CampaignMap.MANSION, level);
    }

    public int getStarsForLevel(CampaignMap map, int level) {
        return map != null && isValidLevel(level)
                ? campaignLevelStars[map.ordinal()][level] : 0;
    }

    public void recordLevelStars(int level, int stars) {
        recordLevelStars(CampaignMap.MANSION, level, stars);
    }

    public void recordLevelStars(CampaignMap map, int level, int stars) {
        if (map != null && isValidLevel(level)) {
            int mapIndex = map.ordinal();
            campaignLevelStars[mapIndex][level] = Math.max(
                    campaignLevelStars[mapIndex][level], Math.min(3, Math.max(0, stars)));
        }
    }

    public void setStarsForLevel(int level, int stars) {
        setStarsForLevel(CampaignMap.MANSION, level, stars);
    }

    public void setStarsForLevel(CampaignMap map, int level, int stars) {
        if (map != null && isValidLevel(level)) {
            campaignLevelStars[map.ordinal()][level] = Math.min(3, Math.max(0, stars));
        }
    }

    private boolean isValidLevel(int level) {
        return level >= 1 && level <= TOTAL_LEVELS;
    }

    public void resetLevelStars() {
        for (int[] mapStars : campaignLevelStars) {
            java.util.Arrays.fill(mapStars, 0);
        }
    }

    public int getHighestUnlockedCampaignMap() {
        return highestUnlockedCampaignMap;
    }

    public void setHighestUnlockedCampaignMap(int mapIndex) {
        highestUnlockedCampaignMap = Math.max(CampaignMap.MANSION.ordinal(),
                Math.min(CampaignMap.HOSPITAL.ordinal(), mapIndex));
    }

    public boolean isCampaignMapUnlocked(CampaignMap map) {
        return map != null && map.ordinal() <= highestUnlockedCampaignMap;
    }

    public void completeCampaignMap(CampaignMap map) {
        if (map == null || !isCampaignMapUnlocked(map)) {
            return;
        }
        if (map == CampaignMap.HOSPITAL) {
            campaignCompleted = true;
        } else {
            setHighestUnlockedCampaignMap(Math.max(
                    highestUnlockedCampaignMap, map.ordinal() + 1));
        }
    }

    public boolean isCampaignCompleted() {
        return campaignCompleted;
    }

    public void setCampaignCompleted(boolean completed) {
        campaignCompleted = completed;
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
            currentCampaignLevels[CampaignMap.MANSION.ordinal()] = currentLevel;
        }
    }

    public void restartLevel() {
        lives = 3;
    }

    public void resetGame() {
        currentLevel = 1;
        totalScore = 0;
        lives = 3;
        java.util.Arrays.fill(currentCampaignLevels, 1);
        java.util.Arrays.fill(highestUnlockedCampaignLevels, TOTAL_LEVELS);
        resetLevelStars();
        highestUnlockedCampaignMap = CampaignMap.HOSPITAL.ordinal();
        campaignCompleted = false;
    }
}
