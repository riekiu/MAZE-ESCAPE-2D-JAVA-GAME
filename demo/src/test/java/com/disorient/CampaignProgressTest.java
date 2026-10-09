package com.disorient;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.disorient.manager.GameManager;
import com.disorient.model.CampaignMap;
import org.junit.jupiter.api.Test;

class CampaignProgressTest {

    @Test
    void allCampaignLocationsStartUnlockedAndCompletionIsTracked() {
        GameManager gameManager = new GameManager();

        assertTrue(gameManager.isCampaignMapUnlocked(CampaignMap.MANSION));
        assertTrue(gameManager.isCampaignMapUnlocked(CampaignMap.CEMETERY));
        assertTrue(gameManager.isCampaignMapUnlocked(CampaignMap.HOSPITAL));

        gameManager.completeCampaignMap(CampaignMap.MANSION);
        gameManager.completeCampaignMap(CampaignMap.CEMETERY);
        gameManager.completeCampaignMap(CampaignMap.HOSPITAL);
        assertTrue(gameManager.isCampaignCompleted());
    }
}
