package com.quizleaderboard;

import java.util.regex.*;

public class SubmitResponse {

    private String regNo;
    private int totalPollsMade;
    private long submittedTotal;
    private int attemptCount;

    public static SubmitResponse parse(String json) {
        SubmitResponse r = new SubmitResponse();
        r.regNo = extractString(json, "regNo");
        r.totalPollsMade = (int) extractLong(json, "totalPollsMade");
        r.submittedTotal = extractLong(json, "submittedTotal");
        r.attemptCount = (int) extractLong(json, "attemptCount");
        return r;
    }

    public String getRegNo() { return regNo; }
    public int getTotalPollsMade() { return totalPollsMade; }
    public long getSubmittedTotal() { return submittedTotal; }
    public int getAttemptCount() { return attemptCount; }

    private static long extractLong(String json, String key) {
        Pattern p = Pattern.compile("\"" + key + "\"\\s*:\\s*(-?\\d+)");
        Matcher m = p.matcher(json);
        return m.find() ? Long.parseLong(m.group(1)) : 0L;
    }

    private static String extractString(String json, String key) {
        Pattern p = Pattern.compile("\"" + key + "\"\\s*:\\s*\"([^\"]+)\"");
        Matcher m = p.matcher(json);
        return m.find() ? m.group(1) : "";
    }
}