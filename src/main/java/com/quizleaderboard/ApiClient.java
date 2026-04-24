package com.quizleaderboard;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ApiClient {

    private static final String BASE_URL = "https://devapigw.vidalhealthtpa.com/srm-quiz-task";
    private static final String POLL_ENDPOINT = "/quiz/messages";
    private static final String SUBMIT_ENDPOINT = "/quiz/submit";

    public PollResponse fetchPoll(String regNo, int pollIndex) {
    try {
        String url = BASE_URL + POLL_ENDPOINT + "?regNo=" + encode(regNo) + "&poll=" + pollIndex;
        String json = get(url);
        System.out.println("[DEBUG] Poll " + pollIndex + ": " + json);
        return PollResponse.parse(json);
        } catch (Exception e) {
            Logger.warn("Poll " + pollIndex + " failed: " + e.getMessage());
            return null;
        }
    }

    public SubmitResponse submitLeaderboard(String regNo, List<Participant> leaderboard) {
    try {
        String body = buildSubmitBody(regNo, leaderboard);
        System.out.println("[DEBUG] Submit body: " + body);        // ADD THIS
        String response = post(BASE_URL + SUBMIT_ENDPOINT, body);
        System.out.println("[DEBUG] Submit response: " + response); // ADD THIS
        return SubmitResponse.parse(response);
    } catch (Exception e) {
        Logger.warn("Submission failed: " + e.getMessage());
        return null;
    }
}

    private String get(String urlStr) throws Exception {
        URL url = URI.create(urlStr).toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(10000);
        return readResponse(conn);
    }

    private String post(String urlStr, String body) throws Exception {
        URL url = URI.create(urlStr).toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        conn.setDoOutput(true);
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(10000);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(body.getBytes(StandardCharsets.UTF_8));
        }

        return readResponse(conn);
    }

    private String readResponse(HttpURLConnection conn) throws Exception {
        int status = conn.getResponseCode();
        InputStream stream = (status >= 200 && status < 300)
                ? conn.getInputStream()
                : conn.getErrorStream();

        if (stream == null) return "{}";

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
            return sb.toString();
        }
    }

    private String buildSubmitBody(String regNo, List<Participant> leaderboard) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"regNo\":\"").append(regNo).append("\",\"leaderboard\":[");
        for (int i = 0; i < leaderboard.size(); i++) {
            Participant p = leaderboard.get(i);
            sb.append("{\"participant\":\"").append(p.getName())
              .append("\",\"totalScore\":").append(p.getTotalScore()).append("}");
            if (i < leaderboard.size() - 1) sb.append(",");
        }
        sb.append("]}");
        return sb.toString();
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}