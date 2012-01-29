/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package QuestionParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 *
 * @author Dinu
 */
public class Question
{

    private Multiplicity multiplicity = Multiplicity.UNKNOWN;
    
//    private String questionType = "FACTOID";
    private AnswerType answerType = AnswerType.OTHER;
    private String originalText = "";
    
    private String[] keywords=null;
    private ArrayList<FocusType> focusTypes = new ArrayList<FocusType>();
    private ArrayList<String> mainObjects = new ArrayList<String>();

    private ArrayList<String> dates = new ArrayList<String>();
    
    public void addMainObject(String mainObject) {
        this.mainObjects.add(mainObject);
    }
    
    public ArrayList<String> getMainObjects()
    {
        return mainObjects;
    }
    
    public ArrayList<String> getDates() {
        return dates;
    }
    
    public boolean hasMainObjects()
    {
        return mainObjects.size() > 0;
    }
    
    public boolean hasDates()
    {
        return dates.size() > 0;
    }
    
    public void addFocusType(FocusType focus) {
        focusTypes.add(focus);
    }
    
    public boolean containsFocusType(FocusType focus)
    {
        return focusTypes.contains(focus);
    }
    
//    private String focus = "";

    public Question(String text)
    {
        originalText = text;
    }
        
    public AnswerType getAnswerType() {
        return answerType;
    }

    public String getOriginalText() {
        return originalText;
    }

//    public String getQuestionType() {
//        return questionType;
//    }

    public void setAnswerType(AnswerType answerType) {
        this.answerType = answerType;
    }

    public void setOriginalText(String originalText) {
        this.originalText = originalText;
    }

    public void addDate(String date)
    {
        dates.add(date);
    }
    
    
//    public void setQuestionType(String questionType) {
//        this.questionType = questionType;
//    }
//
//    public void setFocus(String focus) {
//        this.focus = focus;
//    }
    
//    public String getFocus() {
//        return focus;
//    }

    public ArrayList<String> getKeywords() {
        return new ArrayList<String>(Arrays.asList(this.keywords));
    }

//    public void setKeywords(ArrayList keywords) {
//        this.keywords = new String[keywords.size()];
//        keywords.toArray(this.keywords);
//    }
    public void setKeywords(String[] keywords) {
        this.keywords = keywords;
    }
    
    
    public void setMulitplicity(Multiplicity multiplicity)
    {
        this.multiplicity = multiplicity;
    }

    public Multiplicity getMulitplicity() {
        return this.multiplicity;
    }

    public ArrayList<String> getVerbs()
    {
        return getVerbs(true);
    }
    
    public ArrayList<String> getVerbs(boolean includePhrasalVerbs)
    {
        ArrayList<String> result = new ArrayList<String>();
        
        for(String word : keywords)
        {
            if(word.contains(" "))
            {
                if(includePhrasalVerbs)
                    result.add(word);
                else
                    result.add(word.split(" ")[0]);
                            
            }
            else
            if(Lemmatizer.containsType(word) && Lemmatizer.getType(word) == 'V')
            {
                result.add(word);
            }
            
        }
        
        return result;
    }
    
    public boolean containsMainObjects()
    {
        return mainObjects.size() > 0;
    }
   
    public boolean containsDates() {
        return dates.size() > 0;
    }
    
    @Override
    public String toString()
    {
        StringBuilder string = new StringBuilder();
        string.append("Original question: ");
        string.append(originalText);
        string.append("\n"); 
        
//        string.append("QuestionType: " + questionType + "\n");
        string.append("AnswerType: " + answerType + "\n");
        
        string.append("Focus: ");
        for (FocusType word : focusTypes) {
            string.append(word + ", ");
        }
        string.append("\n");
        string.append("Multiplicity: " + multiplicity.toString() + "\n");
        
        string.append("Main Objects: ");
        for (String word : mainObjects) {
            string.append(word + ", ");
        }
        string.append("\n");

        string.append("Dates: ");
        for (String word : dates) {
            string.append(word + ", ");
        }
        string.append("\n");
        
        string.append("Keywords: ");
        if(keywords != null)
        {
            for(String word : keywords)
            {
                string.append(word + ", ");
            }
        }
        string.append("\n");
        string.append("Verbs: ");
        ArrayList<String> verbs = getVerbs();
        if (verbs != null) {
            for (String word : verbs) {
                string.append(word + ", ");
            }
        }
        
        string.append("\n");
        return string.toString();
    }

    
}


