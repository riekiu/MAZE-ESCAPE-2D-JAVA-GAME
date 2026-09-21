package com.mazeescape.screen;

import com.mazeescape.manager.ScreenManager;

public class GameSettingsScreen extends SettingsScreen {

    public GameSettingsScreen(ScreenManager screenManager, Runnable onBack) {
        super(
                screenManager.getSettingsManager(),
                screenManager,
                onBack,
                "GAME SETTINGS",
                "ADJUST YOUR HAUNTED HOUSE EXPERIENCE",
                "BACK TO GAME",
                false,
                false
        );
    }
}
