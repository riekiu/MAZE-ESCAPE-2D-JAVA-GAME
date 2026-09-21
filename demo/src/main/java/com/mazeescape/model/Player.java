package com.mazeescape.model;

public class Player {

    // =========================
    // PLAYER SIZE
    // =========================
    private static final double DEFAULT_SIZE = 32;

    // =========================
    // PLAYER POSITION
    // =========================
    private double x;
    private double y;

    private final double width;
    private final double height;

    // =========================
    // MOVEMENT
    // =========================
    private double speed;

    // =========================
    // STATE
    // =========================
    private boolean hasKey;

    // =========================
    // CONSTRUCTOR
    // =========================
    public Player(double x, double y) {
        this.x = x;
        this.y = y;

        this.width = DEFAULT_SIZE;
        this.height = DEFAULT_SIZE;

        this.speed = 200;

        this.hasKey = false;
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

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
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
    // MOVEMENT
    // =========================
    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = Math.max(0, speed);
    }

    public void move(double deltaX, double deltaY) {
        x += deltaX;
        y += deltaY;
    }

    // =========================
    // KEY
    // =========================
    public boolean hasKey() {
        return hasKey;
    }

    public void collectKey() {
        hasKey = true;
    }

    public void removeKey() {
        hasKey = false;
    }

    // =========================
    // RESET
    // =========================
    public void reset(double x, double y) {
        this.x = x;
        this.y = y;
        this.hasKey = false;
    }
}
