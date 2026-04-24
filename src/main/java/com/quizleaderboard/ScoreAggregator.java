package com.quizleaderboard;

import java.util.*;
import java.util.stream.Collectors;

public class ScoreAggregator {

    private final Set<String> seenKeys = new HashSet<>();
    private final Map<String, Long> scoreMap = new LinkedHashMap<>();

    public int ingest(List<QuizEvent> events) {
        int added = 0;
        for (QuizEvent event : events) {
            String key = event.getRoundId() + "::" + event.getParticipant();
            if (seenKeys.add(key)) {
                scoreMap.merge(event.getParticipant(), (long) event.getScore(), Long::sum);
                added++;
            }
        }
        return added;
    }

    public List<Participant> buildLeaderboard() {
        return scoreMap.entrySet().stream()
                .map(e -> new Participant(e.getKey(), e.getValue()))
                .sorted(Comparator.comparingLong(Participant::getTotalScore).reversed())
                .collect(Collectors.toList());
    }
}