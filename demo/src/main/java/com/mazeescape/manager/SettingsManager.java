package com.mazeescape.manager;

public class SettingsManager {

    // Audio settings
    private double masterVolume = 1.0;
    private double musicVolume = 1.0;
    private double sfxVolume = 1.0;

    // Display/gameplay settings
    private boolean showTimer = true;
    private boolean screenShake = true;
    private boolean animations = true;

    // =========================
    // MASTER VOLUME
    // =========================
    public double getMasterVolume() {
        return masterVolume;
    }

    public void setMasterVolume(double volume) {
        masterVolume = clamp(volume);
    }

    // =========================
    // MUSIC VOLUME
    // =========================
    public double getMusicVolume() {
        return musicVolume;
    }

    public void setMusicVolume(double volume) {
        musicVolume = clamp(volume);
    }

    // =========================
    // SFX VOLUME
    // =========================
    public double getSfxVolume() {
        return sfxVolume;
    }

    public void setSfxVolume(double volume) {
        sfxVolume = clamp(volume);
    }

    // =========================
    // SHOW TIMER
    // =========================
    public boolean isShowTimer() {
        return showTimer;
    }

    public void setShowTimer(boolean showTimer) {
        this.showTimer = showTimer;
    }

    // =========================
    // SCREEN SHAKE
    // =========================
    public boolean isScreenShake() {
        return screenShake;
    }

    public void setScreenShake(boolean screenShake) {
        this.screenShake = screenShake;
    }

    // =========================
    // ANIMATIONS
    // =========================
    public boolean isAnimations() {
        return animations;
    }

    public void setAnimations(boolean animations) {
        this.animations = animations;
    }

    // =========================
    // RESET SETTINGS
    // =========================
    public void resetSettings() {
        masterVolume = 1.0;
        musicVolume = 1.0;
        sfxVolume = 1.0;

        showTimer = true;
        screenShake = true;
        animations = true;
    }

    // =========================
    // HELPER
    // =========================
    private double clamp(double value) {
        return Math.max(0.0, Math.min(1.0, value));
    }
}
