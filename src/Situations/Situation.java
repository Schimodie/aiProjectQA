package Situations;

import AnswerFinding.QueryResult;
import QuestionParser.Lemmatizer;
import QuestionParser.Question;
import java.util.ArrayList;
import java.util.Arrays;

public final class Situation {
    private String name;
    private String paragraph;
    private ArrayList<String> characters = new ArrayList<String>();
    private ArrayList<String> places = new ArrayList<String>();
    private ArrayList<String> events = new ArrayList<String>();
    private ArrayList<String> objects = new ArrayList<String>();
    private ArrayList<String> keywords = new ArrayList<String>();
    private String[] phrases;

    public Situation(String name, String paragraph) {
        paragraph = paragraph
            .replaceFirst("\\Q<![CDATA[\\E", "")
            .replaceFirst("\\Q]]>\\E", "");
        paragraph = paragraph.toLowerCase();
        paragraph = Lemmatizer.lemmatize(paragraph);
        
        String[] trimedParts = paragraph.split("\n");
        
        for (int i = 0; i < trimedParts.length; ++i) {
            trimedParts[i] = trimedParts[i].trim();
        }
        
        this.name = Lemmatizer.lemmatize(name.toLowerCase());
        this.paragraph = this.join("", trimedParts);
        this.paragraph = 
            this.paragraph.toLowerCase()
                .replace("'", "")
                .replace("\"", "")
                .replace("‘", "")
                .replace("’", "");
        
        this.phrases = this.paragraph.split("[\\Q.!?\\E]");
        
        for (int i = 0; i < this.phrases.length; ++i) {
            this.phrases[i] = this.phrases[i].trim();
        }
    }
    
    public void addCharacter(String character) {
        this.characters.add(character.toLowerCase());
    }
    
    public void addPlace(String place) { this.places.add(place.toLowerCase()); }
    
    public void addEvent(String event) { this.events.add(event.toLowerCase()); }
    
    public void addObject(String object) {
        this.objects.add(object.toLowerCase());
    }
    
    public void addKeyword(String keyword) {
        this.keywords.add(keyword.toLowerCase());
    }

    public String getName() { return this.name; }
    
    public String getParagraph() { return this.paragraph; }
    
    public ArrayList<String> getCharacters() { return this.characters; }

    public ArrayList<String> getEvents() { return this.events; }

    public ArrayList<String> getKeywords() { return this.keywords; }

    public ArrayList<String> getObjects() { return this.objects; }

    public ArrayList<String> getPlaces() { return this.places; }
    
    private String join(String glue, String... strs) {
        int len = strs.length;
        StringBuilder strb = new StringBuilder();
        
        if (len == 0) {
            return null;
        }
        
        strb.append(strs[0]);
        
        for (int x = 1; x < len; ++x) {
            strb.append(glue).append(strs[x]);
        }
        
        return strb.toString();
    }

    public ArrayList<QueryResult> query(Question q) {
        int baseScore = 0;
        int score;
        String mystr;
        ArrayList<QueryResult> qrs = new ArrayList<QueryResult>();
        ArrayList<String> _keywords = q.getKeywords();
        
        for (String str : q.getMainObjects()) {
            mystr = str.toLowerCase();
            
            if (
                this.characters.contains(mystr) ||
                this.events.contains(mystr) ||
                this.keywords.contains(mystr) ||
                this.objects.contains(mystr) ||
                this.places.contains(mystr) ||
                this.name.contains(mystr)
            ) {
                ++baseScore;
            }
        }
        
        baseScore += this.max (
            this.computeScore(_keywords, this.characters),
            this.computeScore(_keywords, this.events),
            this.computeScore(_keywords, this.keywords),
            this.computeScore(_keywords, this.objects),
            this.computeScore(_keywords, this.places)
        );
        
        baseScore += this.computeScore(_keywords, new ArrayList<String> (
           Arrays.asList(new String[] { this.name }) 
        ));
        
        for (String phrase : this.phrases) {
            score = baseScore;
                
            for (String str : q.getMainObjects()) {
                mystr = str.toLowerCase();
                
                if (phrase.contains(mystr)) {
                    ++score;
                    
                    score += this.computeScore (
                        _keywords,
                        new ArrayList<String> (
                            Arrays.asList(new String[] { phrase })
                        )
                    );
                }
            }
            
            if (score != baseScore) {
                qrs.add(new QueryResult(score, phrase, this));
            }
        }
        
        return qrs;
    }
    
    private int computeScore(ArrayList<String> keywords, ArrayList<String> container) {
        int maxScore = Integer.MIN_VALUE;
        int score;
        int beginIndex;
        int endIndex;
        String after;
        String before;
        String pattern = "[\\Q.,;:'?!\\E\"\\s]";
        
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
    
    private int max(int... array) {
        if (array.length == 0) {
            return 0;
        }
        
        if (array.length == 1) {
            return array[0];
        }
        
        if (array.length == 2) {
            return this.max_(array[0], array[2]);
        }
        
        int max = this.max_(array[0], array[1]);
        
        for (int i = 2; i < array.length; ++i) {
            max = this.max_(max, array[i]);
        }
        
        return max;
    }
    
    private int max_(int i, int j) { return i > j ? i : j; }
    
    @Override
    public String toString() {
        StringBuilder strb = new StringBuilder();
        
        strb.append("Name: ").append(this.name)
            .append("\nCharacters:");
        
        for (String str : this.characters) {
            strb.append("\n  ").append(str);
        }
        
        strb.append("\nObjects:");
        
        for (String str : this.objects) {
            strb.append("\n  ").append(str);
        }
        
        strb.append("\nEvents:");
        
        for (String str : this.events) {
            strb.append("\n  ").append(str);
        }
        
        strb.append("\nKeywords:");
        
        for (String str : this.keywords) {
            strb.append("\n  ").append(str);
        }
        
        strb.append("\nPlaces:");
        
        for (String str : this.places) {
            strb.append("\n  ").append(str);
        }
        
        strb.append("\nParagraph:\n  ").append(this.paragraph);
        
        return strb.toString();
    }
}
