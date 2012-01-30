package AnswerFinding;

public class QueryResult {
    private int score;
    private String result;
    private Object obj;

    public QueryResult(int score, String result, Object obj) {
        this.result = result;
        this.score = score;
        this.obj = obj;
    }

    public String getResult() { return this.result; }

    public int getScore() { return this.score; }

    public Object getObject() { return this.obj; }
}
