package com.quizleaderboard;

public class Participant {

    private final String name;
    private final long totalScore;

    public Participant(String name, long totalScore) {
        this.name = name;
        this.totalScore = totalScore;
    }

    public String getName() { return name; }
    public long getTotalScore() { return totalScore; }
}