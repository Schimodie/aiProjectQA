package InfoExtraction;

import AnswerFinding.QueryResult;
import QuestionParser.Lemmatizer;
import QuestionParser.Question;
import java.util.ArrayList;
import java.util.Arrays;

public class Action {
    private String text;
    private String[] characters;
    private String[] places;
    private String[] verbs;
    
    public Action(String chars, String places, String verbs, String text) {
        text = text.toLowerCase();
        text = Lemmatizer.lemmatize(text);
        
        String[] trimedParts = text.split("\n");
        
        this.characters = chars.split(", ");
        this.places = places.split(", ");
        this.verbs = verbs.split(", ");
        
        for (int i = 0; i < this.characters.length; ++i)
            this.characters[i] = this.characters[i].toLowerCase();
        
        for (int i = 0; i < this.places.length; ++i)
            this.places[i] = this.places[i].toLowerCase();
        
        for (int i = 0; i < this.verbs.length; ++i)
            this.verbs[i] = this.verbs[i].toLowerCase();
        
        for (int i = 0; i < trimedParts.length; ++i)
            trimedParts[i] = trimedParts[i].trim();
        
        this.text = this.join("", trimedParts);
        this.text = text
            .replace("’", " ")
            .replace("‘", " ")
            .replace("'", "")
            .replace("\"", "")
            .replace("\r", "")
            .trim();
    }

    public String[] getCharacters() { return this.characters; }

    public String[] getPlaces() { return this.places; }

    public String[] getVerbs() { return this.verbs; }

    public String getText() { return this.text; }
    
    private String join(String glue, String... strs) {
        int len = strs.length;
        StringBuilder strb = new StringBuilder();
        
        if (len == 0)
            return null;
        
        strb.append(strs[0]);
        
        for (int x = 1; x < len; ++x)
            strb.append(glue).append(strs[x]);
        
        return strb.toString();
    }
    
    private int computeScore(ArrayList<String> keywords, ArrayList<String> container) {
        int maxScore = Integer.MIN_VALUE;
        int score;
        int beginIndex;
        int endIndex;
        String after;
        String before;
        String pattern = "[,;:\\s]";
        
        if (container.isEmpty()) {
            return 0;
        }
        
        for (String str : container) {
            score = 0;
            
            for (String key : keywords) {
                beginIndex = str.indexOf(key);
                
                if (beginIndex != -1) {
                    endIndex = beginIndex + key.length() + 1;
                    after = beginIndex + key.length() < str.length() 
                            ? str.substring(beginIndex + key.length(), endIndex)
                            : "";
                    before = beginIndex > 0 
                             ? str.substring(beginIndex - 1, beginIndex)
                             : "";
                    
                    if (
                        ("".equals(before) || before.matches(pattern)) && 
                        ("".equals(after) || after.matches(pattern))
                    ) {
                        ++score;
                    }
                }
            }
            
            if (score > maxScore) {
                maxScore = score;
            }
        }
        
        return maxScore;
    }
    
    public QueryResult query(Question q) {
        int score = 0;
        ArrayList<String> _verbs = q.getVerbs(true);
        ArrayList<String> _mo = q.getMainObjects();
        ArrayList<String> _kyewords = q.getKeywords();
        ArrayList<String> _text = new ArrayList<String> (
            Arrays.asList(new String[] { this.text })
        );
        
        for (String vb : this.verbs) {
            if (this.contains(_verbs, vb)) {
                ++score;
            }
        }
        
        for (String ch : this.characters) {
            if (this.contains(_mo, ch)) {
                ++score;
            }
        }
        
        for (String place : this.places) {
            if (this.contains(_mo, place)) {
                ++score;
            }
        }
        
        score += this.computeScore(_verbs, _text);
        score += this.computeScore(_mo, _text);
        score += this.computeScore(_kyewords, _text);
        
        return new QueryResult(score, this.text, this);
    }
    
    private boolean contains(ArrayList<String> words, String word) {
        for (String w : words) {
            if (w.equalsIgnoreCase(word)) {
                return true;
            }
        }
        
        return false;
    }
}
