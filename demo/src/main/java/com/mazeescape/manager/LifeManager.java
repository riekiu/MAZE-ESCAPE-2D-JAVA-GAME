package com.mazeescape.manager;

public class LifeManager {

    private static final int DEFAULT_LIVES = 3;

    private int lives;

    // =========================
    // CONSTRUCTOR
    // =========================
    public LifeManager() {
        lives = DEFAULT_LIVES;
    }

    // =========================
    // LOSE LIFE
    // =========================
    public void loseLife() {
        if (lives > 0) {
            lives--;
        }
    }

    // =========================
    // ADD LIFE
    // =========================
    public void addLife() {
        lives++;
    }

    // =========================
    // SET LIVES
    // =========================
    public void setLives(int lives) {
        this.lives = Math.max(0, lives);
    }

    // =========================
    // GET LIVES
    // =========================
    public int getLives() {
        return lives;
    }

    // =========================
    // CHECK LIVES
    // =========================
    public boolean hasLivesLeft() {
        return lives > 0;
    }

    public boolean isGameOver() {
        return lives <= 0;
    }

    // =========================
    // RESET
    // =========================
    public void reset() {
        lives = DEFAULT_LIVES;
    }

    // =========================
    // DEFAULT LIVES
    // =========================
    public int getDefaultLives() {
        return DEFAULT_LIVES;
    }
}
