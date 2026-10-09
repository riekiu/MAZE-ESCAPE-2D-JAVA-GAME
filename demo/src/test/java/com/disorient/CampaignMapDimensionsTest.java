package com.disorient;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.disorient.model.CampaignMap;
import com.disorient.model.LevelData;
import org.junit.jupiter.api.Test;

class CampaignMapDimensionsTest {

    @Test
    void dimensionsMatchTheGridSizeUsedForEachMap() {
        LevelData level = new LevelData(1);

        assertEquals(level.getGridWidth(), CampaignMap.MANSION.getGridWidth(level));
        assertEquals(level.getGridHeight(), CampaignMap.MANSION.getGridHeight(level));
        assertEquals(level.getGridWidth() + 1, CampaignMap.CEMETERY.getGridWidth(level));
        assertEquals(level.getGridHeight() + 1, CampaignMap.CEMETERY.getGridHeight(level));
        assertEquals(level.getGridWidth() + 2, CampaignMap.HOSPITAL.getGridWidth(level));
        assertEquals(level.getGridHeight() + 2, CampaignMap.HOSPITAL.getGridHeight(level));
        assertEquals(10, CampaignMap.CEMETERY.getGridWidth(level));
        assertEquals(10, CampaignMap.CEMETERY.getGridHeight(level));
        assertEquals(11, CampaignMap.HOSPITAL.getGridWidth(level));
        assertEquals(11, CampaignMap.HOSPITAL.getGridHeight(level));
    }
}
