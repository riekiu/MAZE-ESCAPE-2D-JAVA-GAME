package com.mazeescape;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import com.mazeescape.manager.GameManager;
import com.mazeescape.manager.SaveManager;

class SaveManagerTest {

    @Test
    void loadGameRestoresExactSavedProgress() {
        SaveManager saveManager = new SaveManager();
        GameManager gameManager = new GameManager();

        gameManager.setCurrentLevel(4);
        gameManager.setHighestUnlockedLevel(4);
        gameManager.setTotalScore(1200);
        gameManager.setLives(2);
        saveManager.saveGame(gameManager);

        GameManager restoredGame = new GameManager();
        restoredGame.setCurrentLevel(1);
        restoredGame.setTotalScore(999);
        restoredGame.setLives(1);

        saveManager.loadGame(restoredGame);

        assertEquals(4, restoredGame.getCurrentLevel());
        assertEquals(2, restoredGame.getLives());
        assertEquals(1200, restoredGame.getTotalScore());
        assertEquals(4, restoredGame.getHighestUnlockedLevel());

        File saveFile = new File(System.getProperty("user.home") + File.separator + "MazeEscapeSave.properties");
        if (saveFile.exists()) {
            saveFile.delete();
        }
    }
}
