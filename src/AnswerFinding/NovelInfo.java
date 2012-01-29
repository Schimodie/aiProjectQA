package AnswerFinding;

import InfoExtraction.Action;
import InfoExtraction.Relation;
import InfoExtraction.Place;
import InfoExtraction.Genre;
import InfoExtraction.Character;
import InfoExtraction.InfoExtractionParser;
import Situations.Situation;
import Situations.SituationsParser;
import com.sun.org.apache.xalan.internal.lib.NodeInfo;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public final class NovelInfo {
    private ArrayList<Action> actions;
    private ArrayList<Character> characters;
    private ArrayList<Genre> genres;
    private ArrayList<Place> places;
    private ArrayList<Relation> relations;
    private ArrayList<Situation> situations;
    private String summary;
    private String beginningOfSummary = null;
    private String endOfSummary = null;

    public NovelInfo(String secModPath, String thirdModPath, String fourthModPath) {
        this.load2ndModInfo(secModPath);
        this.load3rdModInfo(thirdModPath);
        this.load4thModInfo(fourthModPath);
    }

    public String getBeginningOfSummary() {
        if (this.beginningOfSummary == null) {
            String[] phrases = this.summary.split("\\Q.\\E");
            
            if (phrases.length > 3) {
                this.beginningOfSummary = NovelInfo.join("", phrases[0], phrases[1], phrases[2]);
            }
            else {
                this.beginningOfSummary = NovelInfo.join("", phrases);
            }
            
            this.beginningOfSummary = this.beginningOfSummary.trim();
        }
            
        return this.beginningOfSummary;
    }

    public String getEndOfSummary() {
        if (this.endOfSummary == null) {
            int n;
            String[] phrases = this.summary.split("\\Q.\\E");
            
            n = phrases.length;
            
            if (n > 3) {
                this.endOfSummary = NovelInfo.join("", phrases[n - 3], phrases[n - 2], phrases[n - 1]);
            }
            else {
                this.endOfSummary = NovelInfo.join("", phrases);
            }
            
            this.endOfSummary = this.endOfSummary.trim();
        }
            
        return this.endOfSummary;
    }
    
    public String getSummary() { return this.summary; }

    public ArrayList<Action> getActions() { return this.actions; }

    public ArrayList<Character> getCharacters() { return this.characters; }

    public ArrayList<Genre> getGenres() { return this.genres; }

    public ArrayList<Place> getPlaces() { return this.places; }

    public ArrayList<Relation> getRelations() { return this.relations; }

    public ArrayList<Situation> getSituations() { return situations; }
    
    private void load2ndModInfo(String path) {
        InfoExtractionParser iep = new InfoExtractionParser(path);
        
        iep.parse();
        
        this.actions = iep.getActions();
        this.characters = iep.getCharacters();
        this.genres = iep.getGenres();
        this.places = iep.getPlaces();
        this.relations = iep.getRelations();
    }
    
    private void load3rdModInfo(String path) {
        String s;
        StringBuilder sb = new StringBuilder();
        BufferedReader br = null;
        
        try {
            br = new BufferedReader(new FileReader(new File(path)));
            
            while ((s = br.readLine()) != null) {
                sb.append(s);
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
        finally {
            if (br != null) {
                try {
                    br.close();
                }
                catch (IOException ioe) {
                    System.out.println(ioe.getMessage());
                }
            }
        }
        
        this.summary = sb.toString();
    }
    
    private void load4thModInfo(String path) {
        SituationsParser sp = new SituationsParser(path);
        
        sp.parse();
        
        this.situations = sp.getSituations();
    }
    
    private static String join(String glue, String... strs) {
        int len = strs.length;
        StringBuilder strb = new StringBuilder();
        
        if (len == 0)
            return null;
        
        strb.append(strs[0]);
        
        for (int x = 1; x < len; ++x)
            strb.append(glue).append(strs[x]);
        
        return strb.toString();
    }
}
