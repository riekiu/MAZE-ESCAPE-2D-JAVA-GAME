package com.mazeescape.manager;

public class ScoreManager {

    // =========================
    // SCORE VALUES
    // =========================
    private static final int BASE_LEVEL_SCORE = 1000;
    private static final int TIME_BONUS_MULTIPLIER = 10;
    private static final int LIFE_BONUS = 250;

    // =========================
    // CURRENT LEVEL SCORE
    // =========================
    private int currentLevelScore;

    public ScoreManager() {
        currentLevelScore = 0;
    }

    // =========================
    // CALCULATE SCORE
    // =========================
    /**
     * Calculates the score earned after completing a level.
     *
     * @param remainingTime seconds remaining
     * @param remainingLives lives remaining
     * @return calculated level score
     */
    public int calculateLevelScore(int remainingTime, int remainingLives) {

        remainingTime = Math.max(0, remainingTime);
        remainingLives = Math.max(0, remainingLives);

        int baseScore = BASE_LEVEL_SCORE;

        int timeBonus
                = remainingTime * TIME_BONUS_MULTIPLIER;

        int lifeBonus
                = remainingLives * LIFE_BONUS;

        currentLevelScore
                = baseScore + timeBonus + lifeBonus;

        return currentLevelScore;
    }

    // =========================
    // GET CURRENT SCORE
    // =========================
    public int getCurrentLevelScore() {
        return currentLevelScore;
    }

    // =========================
    // RESET
    // =========================
    public void resetCurrentLevelScore() {
        currentLevelScore = 0;
    }

    // =========================
    // SCORE COMPONENTS
    // =========================
    public int getBaseLevelScore() {
        return BASE_LEVEL_SCORE;
    }

    public int getTimeBonus(int remainingTime) {
        return Math.max(0, remainingTime)
                * TIME_BONUS_MULTIPLIER;
    }

    public int getLifeBonus(int remainingLives) {
        return Math.max(0, remainingLives)
                * LIFE_BONUS;
    }
}
