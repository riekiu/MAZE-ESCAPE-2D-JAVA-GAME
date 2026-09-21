package com.mazeescape.controller;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;

import java.util.HashSet;
import java.util.Set;

public class InputManager {

    private final Set<KeyCode> pressedKeys;

    public InputManager() {
        pressedKeys = new HashSet<>();
    }

    // =========================
    // CONNECT TO SCENE
    // =========================
    public void initialize(Scene scene) {

        scene.setOnKeyPressed(event
                -> pressedKeys.add(event.getCode())
        );

        scene.setOnKeyReleased(event
                -> pressedKeys.remove(event.getCode())
        );

        scene.setOnMouseClicked(event
                -> scene.getRoot().requestFocus()
        );

        scene.getRoot().requestFocus();
    }

    // =========================
    // KEY CHECK
    // =========================
    public boolean isPressed(KeyCode key) {
        return pressedKeys.contains(key);
    }

    // =========================
    // MOVEMENT
    // =========================
    public boolean isMovingUp() {

        return isPressed(KeyCode.W)
                || isPressed(KeyCode.UP);
    }

    public boolean isMovingDown() {

        return isPressed(KeyCode.S)
                || isPressed(KeyCode.DOWN);
    }

    public boolean isMovingLeft() {

        return isPressed(KeyCode.A)
                || isPressed(KeyCode.LEFT);
    }

    public boolean isMovingRight() {

        return isPressed(KeyCode.D)
                || isPressed(KeyCode.RIGHT);
    }

    // =========================
    // PAUSE
    // =========================
    public boolean isPausePressed() {

        return isPressed(KeyCode.ESCAPE);
    }

    // =========================
    // CLEAR
    // =========================
    public void clearKeys() {
        pressedKeys.clear();
    }
}
