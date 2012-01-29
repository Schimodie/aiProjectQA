package AnswerFinding;

import Situations.Situation;

public class QueryResult {
    private int score;
    private String result;
    private Situation situation;

    public QueryResult(int score, String result, Situation situation) {
        this.result = result;
        this.score = score;
        this.situation = situation;
    }

    public String getResult() { return this.result; }

    public int getScore() { return this.score; }

    public Situation getSituation() { return this.situation; }
}
