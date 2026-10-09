package com.disorient.model;

public class LeaderboardEntry {

    // =========================
    // ENTRY DATA
    // =========================
    private String playerName;
    private int score;
    private int level;

    // =========================
    // CONSTRUCTOR
    // =========================
    public LeaderboardEntry(
            String playerName,
            int score,
            int level
    ) {
        setPlayerName(playerName);
        this.score = Math.max(0, score);
        this.level = Math.max(1, Math.min(50, level));
    }

    // =========================
    // PLAYER NAME
    // =========================
    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {

        if (playerName == null || playerName.isBlank()) {
            this.playerName = "PLAYER";
        } else {
            String normalized = playerName.strip();
            this.playerName = normalized.substring(0, Math.min(20, normalized.length()));
        }
    }

    // =========================
    // SCORE
    // =========================
    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = Math.max(0, score);
    }

    public int getCompletionTime() {
        return score;
    }

    public void setCompletionTime(int seconds) {
        score = Math.max(0, seconds);
    }

    // =========================
    // LEVEL
    // =========================
    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = Math.max(
                1,
                Math.min(50, level)
        );
    }
}
