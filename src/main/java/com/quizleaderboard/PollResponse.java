package com.quizleaderboard;

import java.util.*;
import java.util.regex.*;

public class PollResponse {

    private String regNo;
    private String setId;
    private int pollIndex;
    private List<QuizEvent> events = new ArrayList<>();

    public List<QuizEvent> getEvents() { return events; }
    public int getPollIndex() { return pollIndex; }

    public static PollResponse parse(String json) {
        PollResponse response = new PollResponse();
        response.regNo = extractString(json, "regNo");
        response.setId = extractString(json, "setId");
        response.pollIndex = extractInt(json, "pollIndex");
        response.events = parseEvents(json);
        return response;
    }

    private static List<QuizEvent> parseEvents(String json) {
        List<QuizEvent> events = new ArrayList<>();
        int start = json.indexOf("\"events\"");
        if (start == -1) return events;

        int arrayStart = json.indexOf('[', start);
        int arrayEnd = json.lastIndexOf(']');
        if (arrayStart == -1 || arrayEnd == -1) return events;

        String eventsJson = json.substring(arrayStart + 1, arrayEnd);
        String[] objects = eventsJson.split("\\},\\s*\\{");

        for (String obj : objects) {
            obj = obj.replace("{", "").replace("}", "").trim();
            if (obj.isEmpty()) continue;

            String roundId = extractString("{" + obj + "}", "roundId");
            String participant = extractString("{" + obj + "}", "participant");
            int score = extractInt("{" + obj + "}", "score");

            if (roundId != null && participant != null) {
                events.add(new QuizEvent(roundId, participant, score));
            }
        }
        return events;
    }

    private static String extractString(String json, String key) {
        Pattern p = Pattern.compile("\"" + key + "\"\\s*:\\s*\"([^\"]+)\"");
        Matcher m = p.matcher(json);
        return m.find() ? m.group(1) : null;
    }

    private static int extractInt(String json, String key) {
        Pattern p = Pattern.compile("\"" + key + "\"\\s*:\\s*(-?\\d+)");
        Matcher m = p.matcher(json);
        return m.find() ? Integer.parseInt(m.group(1)) : 0;
    }
}