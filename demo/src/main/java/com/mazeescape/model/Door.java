package com.mazeescape.model;

public class Door {

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
    private boolean locked;
    private boolean opened;

    // =========================
    // CONSTRUCTOR
    // =========================
    public Door(double x, double y) {
        this.x = x;
        this.y = y;

        this.width = 24;
        this.height = 36;

        this.locked = true;
        this.opened = false;
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
    // STATE
    // =========================
    public boolean isLocked() {
        return locked;
    }

    public boolean isOpened() {
        return opened;
    }

    // =========================
    // OPEN DOOR
    // =========================
    public boolean unlock(boolean playerHasKey) {

        if (!locked) {
            return true;
        }

        if (playerHasKey) {
            locked = false;
            opened = true;
            return true;
        }

        return false;
    }

    // =========================
    // CLOSE / RESET
    // =========================
    public void close() {
        opened = false;
    }

    public void lock() {
        locked = true;
        opened = false;
    }

    public void reset() {
        locked = true;
        opened = false;
    }
}
