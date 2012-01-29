package AnswerFinding;

import InfoExtraction.Action;
import QuestionParser.AnswerType;
import QuestionParser.FocusType;
import QuestionParser.Multiplicity;
import QuestionParser.Question;
import java.util.ArrayList;

public class Answer
{
    private class Pair<T1, T2> {
        public T1 first;
        public T2 second;
    }
    
    private NovelInfo novelInfo;
    
    public Answer(NovelInfo ni) { this.novelInfo = ni; }
    
    public String answer(Question q) {
        AnswerType at = q.getAnswerType();
        Multiplicity mult = q.getMulitplicity();
        ArrayList<String> dates = q.getDates();
        ArrayList<String> keywords = q.getKeywords();
        ArrayList<String> mo = q.getMainObjects();
        
        switch (at) {
            case DEFINITION: {
                if (q.containsFocusType(FocusType.NOVEL)) {
                    return 
                        "This is the summary of the novel:\n" +
                        this.novelInfo.getSummary();
                }
                
                break;
            }
            case EVENT:
                break;
            case LOCATION: {
                if (q.containsFocusType(FocusType.NOVEL)) {
                    int index;
                    int max;
                    ArrayList<String> locs = new ArrayList<String>();
                    ArrayList<Integer> freq = new ArrayList<Integer>();
                    
                    for (Action act : this.novelInfo.getActions()) {
                        for (String loc : act.getPlaces()) {
                            if (!locs.contains(loc)) {
                                locs.add(loc);
                                freq.add(0);
                            }
                            else {
                                index = locs.indexOf(loc);
                                
                                freq.set(index, freq.get(index) + 1);
                                
                                break;
                            }
                        }
                    }
                    
                    if (freq.isEmpty())
                        break;
                    
                    index = 0;
                    max = -1;
                    
                    for (int i = 0; i < freq.size(); ++i) {
                        if (freq.get(i) > max) {
                            max = freq.get(i);
                            index = i;
                        }
                    }
                    
                    return "The novel takes place in " + locs.get(index);
                }
                else  {
                    
                }
                
                break;
            }
            case MODALITY: {
                if (q.containsFocusType(FocusType.NOVEL)) {
                    if (this.contains(keywords, "begin")) {
                        return 
                            "The novel begins as follows:\n" + 
                            this.novelInfo.getBeginningOfSummary();
                    }
                    else if (this.contains(keywords, "end")) {
                        return
                            "The novel ends as follows:\n" +
                            this.novelInfo.getEndOfSummary();
                    }
                }
                
                break;
            }
            case OTHER:
                break;
            case PERSON:
                break;
            case TIME:
                break;
        }
        
        return "Answer not found!";
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
