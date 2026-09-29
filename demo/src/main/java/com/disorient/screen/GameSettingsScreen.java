package com.disorient.screen;

import com.disorient.manager.ScreenManager;

public class GameSettingsScreen extends SettingsScreen {

    public GameSettingsScreen(ScreenManager screenManager, Runnable onBack) {
        super(
                screenManager.getSettingsManager(),
                screenManager,
                onBack,
                "GAME SETTINGS",
                "ADJUST YOUR HAUNTED HOUSE EXPERIENCE",
                "BACK TO GAME",
                false
        );
    }
}
