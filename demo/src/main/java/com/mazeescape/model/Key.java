package com.mazeescape.model;

public class Key {

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
    private boolean collected;

    // =========================
    // CONSTRUCTOR
    // =========================
    public Key(double x, double y) {
        this.x = x;
        this.y = y;

        this.width = 20;
        this.height = 20;

        this.collected = false;
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
    // COLLECTION
    // =========================
    public boolean isCollected() {
        return collected;
    }

    public void collect() {
        collected = true;
    }

    public void reset() {
        collected = false;
    }
}
