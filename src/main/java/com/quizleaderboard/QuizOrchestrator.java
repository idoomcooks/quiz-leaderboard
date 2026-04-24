package com.quizleaderboard;

import java.util.*;

public class QuizOrchestrator {

    private static final int TOTAL_POLLS = 10;
    private static final long POLL_DELAY_MS = 5000;

    private final String regNo;
    private final ApiClient apiClient;
    private final ScoreAggregator aggregator;

    public QuizOrchestrator(String regNo) {
        this.regNo = regNo;
        this.apiClient = new ApiClient();
        this.aggregator = new ScoreAggregator();
    }

    public void run() {
        Logger.banner("QUIZ LEADERBOARD SYSTEM");
        Logger.info("Registration Number: " + regNo);
        Logger.info("Starting poll sequence — " + TOTAL_POLLS + " polls with " + (POLL_DELAY_MS / 1000) + "s delay");
        Logger.divider();

        collectAllPolls();

        List<Participant> leaderboard = aggregator.buildLeaderboard();
        long totalScore = leaderboard.stream().mapToLong(Participant::getTotalScore).sum();

        Logger.divider();
        Logger.banner("LEADERBOARD");
        printLeaderboard(leaderboard, totalScore);

        Logger.divider();
        Logger.info("Submitting leaderboard...");
        SubmitResponse response = apiClient.submitLeaderboard(regNo, leaderboard);
        Logger.divider();
        Logger.banner("SUBMISSION RESULT");
        Logger.result(response);
    }

    private void collectAllPolls() {
        for (int poll = 0; poll < TOTAL_POLLS; poll++) {
            Logger.info("Fetching poll " + poll + "/" + (TOTAL_POLLS - 1) + "...");

            PollResponse response = apiClient.fetchPoll(regNo, poll);

            if (response != null && response.getEvents() != null) {
                int newEvents = aggregator.ingest(response.getEvents());
                Logger.info("  → " + response.getEvents().size() + " events received, "
                        + newEvents + " new (deduplicated)");
            } else {
                Logger.warn("  → No data returned for poll " + poll);
            }

            if (poll < TOTAL_POLLS - 1) {
                sleep(POLL_DELAY_MS);
            }
        }
    }

    private void printLeaderboard(List<Participant> leaderboard, long totalScore) {
        System.out.printf("%-5s %-20s %s%n", "Rank", "Participant", "Total Score");
        System.out.println("-".repeat(40));
        int rank = 1;
        for (Participant p : leaderboard) {
            System.out.printf("%-5d %-20s %d%n", rank++, p.getName(), p.getTotalScore());
        }
        System.out.println("-".repeat(40));
        System.out.printf("%-26s %d%n", "Combined Total:", totalScore);
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}