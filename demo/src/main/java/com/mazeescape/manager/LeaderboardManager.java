package com.mazeescape.manager;

import com.mazeescape.model.LeaderboardEntry;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LeaderboardManager {

    private static final int TOTAL_LEVELS = 20;

    private final Map<Integer, LeaderboardEntry> entries;

    public LeaderboardManager() {
        entries = new HashMap<>();
    }

    public void recordCompletionTime(int level, int completionTimeSeconds) {
        if (level < 1 || level > TOTAL_LEVELS || completionTimeSeconds < 0) {
            return;
        }

        LeaderboardEntry existing = entries.get(level);
        if (existing == null) {
            entries.put(level, new LeaderboardEntry("PLAYER", completionTimeSeconds, level));
        } else if (completionTimeSeconds < existing.getCompletionTime()) {
            existing.setCompletionTime(completionTimeSeconds);
        }
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
            entries.put(level, new LeaderboardEntry("PLAYER", completionTimeSeconds, level));
        }
    }

    public void clearLeaderboard() {
        entries.clear();
    }

    public int getEntryCount() {
        return entries.size();
    }

}
