package com.quizleaderboard;

public class Logger {

    public static void info(String msg) {
        System.out.println("[INFO]  " + msg);
    }

    public static void warn(String msg) {
        System.out.println("[WARN]  " + msg);
    }

    public static void divider() {
        System.out.println("─".repeat(50));
    }

    public static void banner(String title) {
        System.out.println();
        System.out.println(">>> " + title);
    }

    public static void result(SubmitResponse r) {
    if (r == null) {
        warn("No response received from server.");
        return;
    }
    System.out.println("RegNo          : " + r.getRegNo());
    System.out.println("Submitted Total: " + r.getSubmittedTotal());
    System.out.println("Total Polls Made: " + r.getTotalPollsMade());
    System.out.println("Attempt Count  : " + r.getAttemptCount());
}
}