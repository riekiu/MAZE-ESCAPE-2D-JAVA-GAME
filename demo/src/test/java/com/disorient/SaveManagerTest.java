package com.disorient;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.disorient.manager.GameManager;
import com.disorient.manager.LeaderboardManager;
import com.disorient.manager.SaveManager;
import com.disorient.model.CampaignMap;

class SaveManagerTest {

    @Test
    void loadGameRestoresExactSavedProgress() {
        SaveManager saveManager = new SaveManager();
        GameManager gameManager = new GameManager();
        LeaderboardManager leaderboardManager = new LeaderboardManager();

        gameManager.setCurrentLevel(4);
        gameManager.setHighestUnlockedLevel(4);
        gameManager.setCurrentLevel(CampaignMap.CEMETERY, 2);
        gameManager.setHighestUnlockedLevel(CampaignMap.CEMETERY, 2);
        gameManager.recordLevelStars(CampaignMap.MANSION, 1, 3);
        gameManager.recordLevelStars(CampaignMap.CEMETERY, 1, 1);
        gameManager.setTotalScore(1200);
        gameManager.setLives(2);
        leaderboardManager.setPlayerName("Mara");
        leaderboardManager.recordCompletionTime(1, 42);
        saveManager.savePlayerName("Mara");
        saveManager.saveLeaderboard(leaderboardManager);
        gameManager.completeCampaignMap(CampaignMap.MANSION);
        gameManager.completeCampaignMap(CampaignMap.CEMETERY);
        saveManager.saveGame(gameManager);

        GameManager restoredGame = new GameManager();
        restoredGame.setCurrentLevel(1);
        restoredGame.setTotalScore(999);
        restoredGame.setLives(1);

        saveManager.loadGame(restoredGame);

        assertEquals(4, restoredGame.getCurrentLevel());
        assertEquals(2, restoredGame.getCurrentLevel(CampaignMap.CEMETERY));
        assertEquals(1, restoredGame.getCurrentLevel(CampaignMap.HOSPITAL));
        assertEquals(2, restoredGame.getLives());
        assertEquals(1200, restoredGame.getTotalScore());
        assertEquals(4, restoredGame.getHighestUnlockedLevel());
        assertEquals(GameManager.TOTAL_LEVELS,
                restoredGame.getHighestUnlockedLevel(CampaignMap.CEMETERY));
        assertEquals(GameManager.TOTAL_LEVELS,
                restoredGame.getHighestUnlockedLevel(CampaignMap.HOSPITAL));
        assertEquals(3, restoredGame.getStarsForLevel(CampaignMap.MANSION, 1));
        assertEquals(1, restoredGame.getStarsForLevel(CampaignMap.CEMETERY, 1));
        assertEquals(0, restoredGame.getStarsForLevel(CampaignMap.HOSPITAL, 1));
        assertEquals("Mara", saveManager.loadPlayerName());
        LeaderboardManager restoredLeaderboard = new LeaderboardManager();
        saveManager.loadLeaderboard(restoredLeaderboard);
        assertEquals("Mara", restoredLeaderboard.getEntries().get(0).getPlayerName());
        assertTrue(restoredGame.isCampaignMapUnlocked(CampaignMap.CEMETERY));
        assertTrue(restoredGame.isCampaignMapUnlocked(CampaignMap.HOSPITAL));
        assertFalse(restoredGame.isCampaignCompleted());

        restoredGame.completeCampaignMap(CampaignMap.HOSPITAL);
        saveManager.saveGame(restoredGame);
        GameManager completedCampaign = new GameManager();
        saveManager.loadGame(completedCampaign);
        assertTrue(completedCampaign.isCampaignCompleted());

        File saveFile = new File(System.getProperty("user.home") + File.separator + "DisorientSave.properties");
        if (saveFile.exists()) {
            saveFile.delete();
        }
    }
}
