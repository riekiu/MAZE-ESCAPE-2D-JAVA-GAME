package com.disorient.manager;

import com.disorient.model.LeaderboardEntry;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LeaderboardManager {

    private static final int TOTAL_LEVELS = GameManager.TOTAL_LEVELS;

    private final Map<Integer, LeaderboardEntry> entries;
    private String playerName = "PLAYER";

    public LeaderboardManager() {
        entries = new HashMap<>();
    }

    public void recordCompletionTime(int level, int completionTimeSeconds) {
        if (level < 1 || level > TOTAL_LEVELS || completionTimeSeconds < 0) {
            return;
        }

        LeaderboardEntry existing = entries.get(level);
        if (existing == null) {
            entries.put(level, new LeaderboardEntry(playerName, completionTimeSeconds, level));
        } else if (completionTimeSeconds < existing.getCompletionTime()) {
            existing.setCompletionTime(completionTimeSeconds);
            existing.setPlayerName(playerName);
        }
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        if (playerName == null || playerName.isBlank()) {
            return;
        }
        this.playerName = playerName.strip();
        entries.values().forEach(entry -> entry.setPlayerName(this.playerName));
    }

    public List<LeaderboardEntry> getEntries() {
        List<LeaderboardEntry> result = new ArrayList<>(entries.values());
        result.sort(Comparator.comparingInt(LeaderboardEntry::getLevel));
        return result;
    }

    public int getBestTime(int level) {
        LeaderboardEntry entry = entries.get(level);
        return entry == null ? -1 : entry.getCompletionTime();
    }

    public void setBestTime(int level, int completionTimeSeconds) {
        if (level >= 1 && level <= TOTAL_LEVELS && completionTimeSeconds >= 0) {
            entries.put(level, new LeaderboardEntry(playerName, completionTimeSeconds, level));
        }
    }

    public void setBestTime(int level, int completionTimeSeconds, String playerName) {
        if (level >= 1 && level <= TOTAL_LEVELS && completionTimeSeconds >= 0) {
            entries.put(level, new LeaderboardEntry(playerName, completionTimeSeconds, level));
        }
    }

    public void clearLeaderboard() {
        entries.clear();
    }

    public int getEntryCount() {
        return entries.size();
    }

}
