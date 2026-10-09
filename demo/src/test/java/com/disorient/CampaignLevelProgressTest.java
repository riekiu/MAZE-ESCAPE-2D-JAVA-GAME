package com.disorient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.disorient.manager.GameManager;
import com.disorient.model.CampaignMap;
import org.junit.jupiter.api.Test;

class CampaignLevelProgressTest {

    @Test
    void levelUnlocksStarsAndCurrentLevelAreIndependentPerMap() {
        GameManager gameManager = new GameManager();

        for (CampaignMap map : CampaignMap.values()) {
            assertTrue(gameManager.isLevelUnlocked(map, 1));
            assertTrue(gameManager.isLevelUnlocked(map, GameManager.TOTAL_LEVELS));
        }

        gameManager.setCurrentLevel(CampaignMap.MANSION, 2);
        gameManager.unlockNextLevel(CampaignMap.MANSION);
        gameManager.recordLevelStars(CampaignMap.MANSION, 1, 3);

        assertEquals(2, gameManager.getCurrentLevel(CampaignMap.MANSION));
        assertEquals(1, gameManager.getCurrentLevel(CampaignMap.CEMETERY));
        assertEquals(1, gameManager.getCurrentLevel(CampaignMap.HOSPITAL));
        assertTrue(gameManager.isLevelUnlocked(CampaignMap.MANSION, 2));
        assertTrue(gameManager.isLevelUnlocked(CampaignMap.CEMETERY, 2));
        assertTrue(gameManager.isLevelUnlocked(CampaignMap.HOSPITAL, 2));
        assertEquals(3, gameManager.getStarsForLevel(CampaignMap.MANSION, 1));
        assertEquals(0, gameManager.getStarsForLevel(CampaignMap.CEMETERY, 1));
        assertEquals(0, gameManager.getStarsForLevel(CampaignMap.HOSPITAL, 1));
    }
}
