package com.disorient.model;

public enum CampaignMap {
    MANSION(
            "Mansion",
            "A sprawling haunted house. Find the key, evade the spirit, and reach the shifting exit.",
            "FIND THE KEY",
            "REACH THE EXIT",
            0L),
    CEMETERY(
            "Cemetery",
            "Wind through forgotten graves, broken stone fences, and moonlit paths to find the gate key.",
            "FIND THE GATE KEY",
            "OPEN THE CEMETERY GATE",
            0x2468ACE1L),
    HOSPITAL(
            "Hospital",
            "Search the abandoned wards for a security key and escape through the sealed emergency exit.",
            "FIND THE SECURITY KEY",
            "UNLOCK THE EMERGENCY EXIT",
            0x51A7E11L);

    private final String displayName;
    private final String description;
    private final String keyObjective;
    private final String exitObjective;
    private final long mazeSeedOffset;

    CampaignMap(
            String displayName,
            String description,
            String keyObjective,
            String exitObjective,
            long mazeSeedOffset) {
        this.displayName = displayName;
        this.description = description;
        this.keyObjective = keyObjective;
        this.exitObjective = exitObjective;
        this.mazeSeedOffset = mazeSeedOffset;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public String getKeyObjective() {
        return keyObjective;
    }

    public String getExitObjective() {
        return exitObjective;
    }

    public long getMazeSeedOffset() {
        return mazeSeedOffset;
    }

    public int getGridWidth(LevelData levelData) {
        return levelData.getGridWidth() + getGridSizeOffset();
    }

    public int getGridHeight(LevelData levelData) {
        return levelData.getGridHeight() + getGridSizeOffset();
    }

    private int getGridSizeOffset() {
        return switch (this) {
            case CEMETERY -> 1;
            case HOSPITAL -> 2;
            case MANSION -> 0;
        };
    }
}
