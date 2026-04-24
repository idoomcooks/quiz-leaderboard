# Quiz Leaderboard System

A Java solution for the SRM Internship Assignment — polls a quiz API across 10 rounds, deduplicates event data using `(roundId, participant)` as a composite key, aggregates scores, and submits a sorted leaderboard.

---

## Problem Summary

The validator API streams quiz events across 10 polls. The same event data can appear in multiple polls (simulating distributed system behavior). The program must:

1. Poll the API 10 times (poll indices `0–9`)
2. Wait 5 seconds between each poll
3. Deduplicate events using `roundId + participant` as the unique key
4. Aggregate scores per participant
5. Sort by total score (descending)
6. Submit the leaderboard exactly once

---

## Project Structure

```
quiz-leaderboard/
├── src/main/java/com/quizleaderboard/
│   ├── Main.java               # Entry point
│   ├── QuizOrchestrator.java   # Poll loop + submission flow
│   ├── ApiClient.java          # HTTP GET/POST (no external libs)
│   ├── ScoreAggregator.java    # Deduplication + score aggregation
│   ├── PollResponse.java       # Parses poll JSON response
│   ├── SubmitResponse.java     # Parses submission JSON response
│   ├── QuizEvent.java          # Event model
│   ├── Participant.java        # Leaderboard entry model
│   └── Logger.java             # Console output utility
├── Makefile
└── README.md
```

---

## How to Run

**Requirements:** Java 11+

### Using Make

```bash
# Compile and run with your registration number
make REG_NO=2024CS101

# Or step by step
make compile
make run REG_NO=2024CS101
```

### Using javac directly

```bash
mkdir -p out
find src/main/java -name "*.java" | xargs javac -d out
java -cp out com.quizleaderboard.Main 2024CS101
```

---

## Deduplication Logic

Each event is identified by a composite key:

```
key = roundId + "::" + participant
```

If the same key appears in a later poll, it is silently ignored. Only the first occurrence contributes to the score.

```java
String key = event.getRoundId() + "::" + event.getParticipant();
if (seenKeys.add(key)) {
    scoreMap.merge(event.getParticipant(), (long) event.getScore(), Long::sum);
}
```

---

## Design Decisions

- **Zero external dependencies** — only standard Java (`java.net`, `java.util`, `java.util.regex`)
- **Manual JSON parsing** — lightweight regex-based parsing avoids library bloat
- **Single submission** — the program submits exactly once after all 10 polls complete
- **Clear separation of concerns** — networking, parsing, aggregation, and orchestration are in dedicated classes

---

## Sample Output

```
>>> QUIZ LEADERBOARD SYSTEM
Registration Number: 2024CS101
Starting poll sequence — 10 polls with 5s delay
──────────────────────────────────────────────────
[INFO]  Fetching poll 0/9...
[INFO]    → 4 events received, 4 new (deduplicated)
...
──────────────────────────────────────────────────
>>> LEADERBOARD
Rank  Participant          Total Score
----------------------------------------
1     Alice                340
2     Bob                  280
3     Charlie              210
----------------------------------------
Combined Total:            830

>>> SUBMISSION RESULT
Status    : CORRECT ✓
Idempotent: true
Submitted : 830
Expected  : 830
Message   : Correct!
