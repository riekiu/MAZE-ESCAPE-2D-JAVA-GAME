package com.mazeescape.model;

public class Exit {

    // =========================
    // POSITION
    // =========================
    private double x;
    private double y;

    // =========================
    // SIZE
    // =========================
    private final double width;
    private final double height;

    // =========================
    // STATE
    // =========================
    private boolean active;
    private boolean reached;

    // =========================
    // CONSTRUCTOR
    // =========================
    public Exit(double x, double y) {
        this.x = x;
        this.y = y;

        this.width = 28;
        this.height = 36;

        this.active = false;
        this.reached = false;
    }

    // =========================
    // POSITION
    // =========================
    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // =========================
    // SIZE
    // =========================
    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    // =========================
    // EXIT STATE
    // =========================
    public boolean isReached() {
        return reached;
    }

    public boolean isActive() {
        return active;
    }

    public void activate(boolean playerHasKey) {
        if (playerHasKey) {
            active = true;
        }
    }

    public void reach() {
        if (active) {
            reached = true;
        }
    }

    // =========================
    // RESET
    // =========================
    public void reset() {
        active = false;
        reached = false;
    }
}
