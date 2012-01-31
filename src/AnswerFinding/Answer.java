package AnswerFinding;

import InfoExtraction.Action;
import InfoExtraction.InfoExtractionParser;
import InfoExtraction.Place;
import InfoExtraction.Relation;
import QuestionParser.AnswerType;
import QuestionParser.FocusType;
import QuestionParser.Lemmatizer;
import QuestionParser.MainKnowledge;
import QuestionParser.Multiplicity;
import QuestionParser.Question;
import Situations.Situation;
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
        ArrayList<String> dates = q.getDates();
        ArrayList<String> keywords = q.getKeywords();
        ArrayList<String> mo = q.getMainObjects();
        
        switch (at) {
            case DEFINITION: {
                if (q.containsFocusType(FocusType.NOVEL)) {
                    return this.novelInfo.getSummary();
                }
                else if (q.containsFocusType(FocusType.RELATION)) {
                    ArrayList<Relation> rels = this.novelInfo.getRelations();
                    String e1;
                    String e2;
                    
                    for (Relation rel : rels) {
                        e1 = (String)rel.getEntity1();
                        e2 = (String)rel.getEntity2();
                        
                        if (
                            (this.getCharFullName(e1)
                                .equals(this.getCharFullName(mo.get(0))) &&
                            this.getCharFullName(e2)
                                .equals(this.getCharFullName(mo.get(1)))) ||
                            (this.getCharFullName(e1)
                                .equals(this.getCharFullName(mo.get(1))) &&
                            this.getCharFullName(e2)
                                .equals(this.getCharFullName(mo.get(0))))
                        ) {
                            if (rel.getBond().split(" ").length == 1) {
                                return
                                    this.toName(e1) + " is " +
                                    this.toName(e2) + "'s " +
                                    rel.getBond();
                            }
                            else {
                                return rel.getBond();
                            }
                        }
                    }
                }
                else if (q.containsFocusType(FocusType.PERSON)) {
                    String chr = "";
                    String data = "";
                    
                    for (String str : mo) {
                        chr = this.getCharFullName(str);
                        
                        if ("".equals(chr)) {
                            chr = this.toName(str);
                        }
                        
                        data += 
                            MainKnowledge.searchWiki(chr, null).trim() + "\n";
                    }
                    
                    if ("".equals(chr)) {
                        break;
                    }
                    
                    return data.trim();
                }
                else if (q.containsFocusType(FocusType.OCCUPATION)) {
                    int max = Integer.MIN_VALUE;
                    QueryResult qr;
                    QueryResult actQr = null;
                    
                    for (Action act : this.novelInfo.getActions()) {
                        qr = act.query(q);
                        
                        if (qr.getScore() > max) {
                            actQr = qr;                            
                            max = qr.getScore();
                        }
                    }
                    
                    if (actQr == null) {
                        break;
                    }
                    
                    return actQr.getResult();
                }
                else {
                    String chr = "";
                    String data = "";
                    
                    for (String str : mo) {
                        chr = this.getCharFullName(str);
                        
                        if ("".equals(chr)) {
                            chr = this.toName(str);
                        }
                        
                        data += 
                            MainKnowledge.searchWiki(chr, null).trim() + "\n";
                    }
                    
                    return data.trim();
                }
                
                break;
            }
            case EVENT: {
                int bIndex;
                int eIndex;
                String after;
                String before;
                String pattern = "[\\Q.,;:'?!\\E\"\\s]";
                String text;
                
                for (Action act : this.novelInfo.getActions()) {
                    text = act.getText();
                    
                    for (String date : dates) {
                        bIndex = text.indexOf(date.toLowerCase());

                        if (bIndex > -1) {
                            eIndex = bIndex + date.length() + 1;
                            after = eIndex <= text.length() 
                                    ? text.substring(eIndex -1 , eIndex)
                                    : "";
                            before = bIndex > 0
                                     ? text.substring(bIndex - 1, bIndex)
                                     : "";

                            if (
                                ("".equals(before) || before.matches(pattern)) && 
                                ("".equals(after) || after.matches(pattern))
                            ) {
                                return act.getText();
                            }
                        }
                    }   
                }
                
                break;
            }
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
                    
                    return this.toName(locs.get(index));
                }
                else  {
                    int max = Integer.MIN_VALUE;
                    QueryResult actQr = null;
                    QueryResult qr;
                    QueryResult sitQr = null;
                    String str;
                    ArrayList<QueryResult> qrs = new ArrayList<QueryResult>();
                    ArrayList<Place> places = this.novelInfo.getPlaces();
                    ArrayList<String> strs =
                        new ArrayList<String>(places.size());
                    
                    for (Place place : places) {
                        strs.add(place.getName());
                    }
                    
                    for (Action act : this.novelInfo.getActions()) {
                        qr = act.query(q);
                        
                        if (qr.getScore() > max) {
                            actQr = qr;                            
                            max = qr.getScore();
                        }
                    }
                    
                    max = Integer.MIN_VALUE;
                    
                    for (Situation sit : this.novelInfo.getSituations()) {
                        qrs = sit.query(q);
                        
                        for (QueryResult qr1 : qrs) {
                            if (qr1.getScore() > max) {
                                sitQr = qr1;
                                max = qr1.getScore();
                            }
                        }
                    }
                    
                    if (actQr == null) {
                        if (sitQr != null) {
                            str = this.findInString(strs, sitQr.getResult());
                            
                            if (!"".equals(str)) {
                                return this.toName(str);
                            }
                        }
                    }
                    else if (
                        sitQr == null ||
                        actQr.getScore() >= sitQr.getScore()
                    ) {
                        Action act = (Action)actQr.getObject();
                        
                        if (act.getPlaces().length > 0) {
                            str = act.getPlaces()[0];
                            
                            return this.toName(str);
                        }
                        else {
                            str = this.findInString(strs, actQr.getResult());

                            if (!"".equals(str)) {
                                return this.toName(str);
                            }
                        }
                    }
                    else {
                        str = this.findInString(strs, sitQr.getResult());
                            
                        if (!"".equals(str)) {
                            return this.toName(str);
                        }
                    }
                    
                }
                
                break;
            }
            case MODALITY: {
                if (q.containsFocusType(FocusType.NOVEL)) {
                    if (this.contains(keywords, "begin")) {
                        return this.novelInfo.getBeginningOfSummary();
                    }
                    else if (this.contains(keywords, "end")) {
                        return this.novelInfo.getEndOfSummary();
                    }
                }
                
                break;
            }
            case OTHER:
                break;
            case PERSON: {
                if (q.containsFocusType(FocusType.MAIN_CHARACTER)) {
                    String str = "";
                            
                    switch (q.getMulitplicity()) {
                        case SINGLE: {
                            return 
                                this.toName (
                                    this.novelInfo.getCharacters().get(0)
                                                  .getName()
                                );
                        }
                        case MULTIPLE: {
                            ArrayList<InfoExtraction.Character> chars =
                                this.novelInfo.getCharacters();
                            ArrayList<String> strChars = 
                                new ArrayList<String>(chars.size());
                            
                            for (InfoExtraction.Character chr : chars) {
                                if (!chr.isMain())
                                    continue;
                                
                                strChars.add(this.toName(chr.getName()));
                            }
                            
                            for (String string : strChars) {
                                str += 
                                    string.replace("\r", "").replace("\n", "") +
                                    "\n";
                            }
                            
                            return str.trim();
                        }
                    }
                }
                else {
                    int max = Integer.MIN_VALUE;
                    QueryResult actQr = null;
                    QueryResult qr;
                    QueryResult sitQr = null;
                    String str;
                    ArrayList<QueryResult> qrs = new ArrayList<QueryResult>();
                    ArrayList<InfoExtraction.Character> chars =
                        this.novelInfo.getCharacters();
                    ArrayList<String> strs =
                        new ArrayList<String>(chars.size());
                    ArrayList<Relation> rels = this.novelInfo.getRelations();
                    String e1;
                    String e2;
                    
                    for (String chr : mo)
                    {
                        for (Relation rel : rels) {
                            e1 = (String)rel.getEntity1();
                            e2 = (String)rel.getEntity2();

                            if (!"".equals(this.findInString (
                                    q.getVerbs(),
                                    rel.getBond()
                            ))) {
                                if (
                                    this.getCharFullName(e1)
                                        .equals(this.getCharFullName(chr))
                                ) {
                                    return this.getCharFullName(e2);
                                }
                                else if (
                                    this.getCharFullName(e2)
                                        .equals(this.getCharFullName(chr))
                                ) {
                                    return this.getCharFullName(e1);
                                }
                            }
                        }
                    }
                    
                    for (InfoExtraction.Character chr : chars) {
                        for (String string : mo) {
                            if (
                                !this.getCharFullName(string)
                                     .equalsIgnoreCase(chr.getName())
                            ) {
                                strs.add(chr.getName());
                            }
                        }
                    }
                    
                    for (Action act : this.novelInfo.getActions()) {
                        qr = act.query(q);
                        
                        if (qr.getScore() > max) {
                            actQr = qr;                            
                            max = qr.getScore();
                        }
                    }
                    
                    max = Integer.MIN_VALUE;
                    
                    for (Situation sit : this.novelInfo.getSituations()) {
                        qrs = sit.query(q);
                        
                        for (QueryResult qr1 : qrs) {
                            if (qr1.getScore() > max) {
                                sitQr = qr1;
                                max = qr1.getScore();
                            }
                        }
                    }
                    
                    if (actQr == null) {
                        if (sitQr != null) {
                            str = this.findInString(strs, sitQr.getResult());
                            
                            if (!"".equals(str)) {
                                return this.toName(str);
                            }
                        }
                    }
                    else if (
                        sitQr == null ||
                        actQr.getScore() >= sitQr.getScore()
                    ) {
                        Action act = (Action)actQr.getObject();
                        
                        if (act.getCharacters().length > 0) {
                            str = act.getCharacters()[0];
                            
                            return this.toName(str);
                        }
                        else {
                            str = this.findInString(strs, actQr.getResult());

                            if (!"".equals(str)) {
                                return this.toName(str);
                            }
                        }
                    }
                    else {
                        str = this.findInString(strs, sitQr.getResult());
                            
                        if (!"".equals(str)) {
                            return this.toName(str);
                        }
                    }
                }
                
                break;
            }
            case TIME: {
                if (q.containsFocusType(FocusType.NOVEL)) {
                    String pattern = "[1-2][0-9][0-9][0-9]";
                    ArrayList<String> summaryDates =
                        MainKnowledge.getDates(this.novelInfo.getSummary());
                    
                    for (String date : summaryDates) {
                        for (int i = 0; i < date.length() - 3; ++i) {
                            if (date.substring(i, i + 4).matches(pattern)) {
                                return date;
                            }
                        }
                    }
                }
                
                break;
            }
        }
        
        return "Answer not found!";
    }
    
    private String findInString(ArrayList<String> list, String str) {
        int bIndex;
        int eIndex;
        String after;
        String before;
        String pattern = "[\\Q.,;:'?!\\E\"\\s]";
        
        for (String item : list) {
            if ((bIndex = str.indexOf(item.toLowerCase())) != -1) {
                eIndex = bIndex + item.length() + 1;
                after = eIndex <= str.length() 
                        ? str.substring(eIndex -1 , eIndex)
                        : "";
                before = bIndex > 0
                         ? str.substring(bIndex - 1, bIndex)
                         : "";
                
                if (
                    ("".equals(before) || before.matches(pattern)) && 
                    ("".equals(after) || after.matches(pattern))
                ) {
                    return item;
                }
            }
        }
        
        return "";
    }
    
    private String toName(String str) {
        String aux = "";
        String[] strs = str.split(" ");
        
        if ("".equals(str)) {
            return "";
        }
        
        for (String string : strs) {
            if ("".equals(string)) {
                continue;
            }
            
            aux += 
                string.substring(0, 1).toUpperCase() + 
                string.substring(1) + " ";
        }
        
        return aux.trim();
    }
    
    private String getCharFullName(String someName) {
        for (InfoExtraction.Character chr : this.novelInfo.getCharacters()) {
            if (this.toName(chr.getName()).contains(this.toName(someName))) {
                return this.toName(chr.getName());
            }
        }
        
        return "";
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
