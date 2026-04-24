package com.quizleaderboard;

public class Main {
    public static void main(String[] args) {
        String regNo = args.length > 0 ? args[0] : "RA2411003011762";

        QuizOrchestrator orchestrator = new QuizOrchestrator(regNo);
        orchestrator.run();
    }
}