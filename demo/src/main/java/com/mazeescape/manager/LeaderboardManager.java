package com.mazeescape.manager;

import com.mazeescape.model.LeaderboardEntry;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardManager {

    private static final int TOTAL_LEVELS = 20;

    private final List<LeaderboardEntry> entries;

    public LeaderboardManager() {
        entries = new ArrayList<>();
    }

    public void recordCompletionTime(int level, int completionTimeSeconds) {
        if (level < 1 || level > TOTAL_LEVELS || completionTimeSeconds < 0) {
            return;
        }

        LeaderboardEntry existing = findLevel(level);
        if (existing == null) {
            entries.add(new LeaderboardEntry("PLAYER", completionTimeSeconds, level));
        } else if (completionTimeSeconds < existing.getCompletionTime()) {
            existing.setCompletionTime(completionTimeSeconds);
        }
    }

    public List<LeaderboardEntry> getEntries() {
        List<LeaderboardEntry> result = new ArrayList<>(entries);
        result.sort(java.util.Comparator.comparingInt(LeaderboardEntry::getLevel));
        return result;
    }

    public int getBestTime(int level) {
        LeaderboardEntry entry = findLevel(level);
        return entry == null ? -1 : entry.getCompletionTime();
    }

    public void setBestTime(int level, int completionTimeSeconds) {
        if (level >= 1 && level <= TOTAL_LEVELS && completionTimeSeconds >= 0) {
            entries.removeIf(entry -> entry.getLevel() == level);
            entries.add(new LeaderboardEntry("PLAYER", completionTimeSeconds, level));
        }
    }

    public void clearLeaderboard() {
        entries.clear();
    }

    public int getEntryCount() {
        return entries.size();
    }

    private LeaderboardEntry findLevel(int level) {
        return entries.stream()
                .filter(entry -> entry.getLevel() == level)
                .findFirst()
                .orElse(null);
    }
}
