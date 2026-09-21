package com.mazeescape.manager;

public class TimerManager {

    private double timeRemaining;
    private boolean running;
    private boolean paused;

    // =========================
    // START TIMER
    // =========================
    public void start(int seconds) {
        timeRemaining = Math.max(0, seconds);
        running = true;
        paused = false;
    }

    // =========================
    // UPDATE TIMER
    // =========================
    /**
     * Updates the timer using elapsed time in seconds.
     *
     * @param delta elapsed time since the previous game update
     */
    public void update(double delta) {

        if (!running || paused) {
            return;
        }

        timeRemaining -= delta;

        if (timeRemaining <= 0) {
            timeRemaining = 0;
            running = false;
        }
    }

    // =========================
    // PAUSE / RESUME
    // =========================
    public void pause() {
        if (running) {
            paused = true;
        }
    }

    public void resume() {
        if (running) {
            paused = false;
        }
    }

    // =========================
    // STOP
    // =========================
    public void stop() {
        running = false;
        paused = false;
    }

    // =========================
    // RESET
    // =========================
    public void reset() {
        timeRemaining = 0;
        running = false;
        paused = false;
    }

    // =========================
    // GET TIME
    // =========================
    public double getTimeRemaining() {
        return timeRemaining;
    }

    /**
     * Returns the remaining time as whole seconds.
     */
    public int getRemainingSeconds() {
        return (int) Math.ceil(timeRemaining);
    }

    // =========================
    // TIMER STATE
    // =========================
    public boolean isRunning() {
        return running;
    }

    public boolean isPaused() {
        return paused;
    }

    public boolean isTimeUp() {
        return timeRemaining <= 0;
    }

    // =========================
    // FORMAT TIME
    // =========================
    /**
     * Converts the remaining time into MM:SS format.
     */
    public String getFormattedTime() {

        int totalSeconds = getRemainingSeconds();

        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;

        return String.format(
                "%02d:%02d",
                minutes,
                seconds
        );
    }
}
