/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package QuestionParser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Dinu
 */
public class Lemmatizer {
    
//    class WordInfo {
//        char type;
//        String defaultWord;
//        
//        public WordInfo(char type, String defaultWord)
//        {
//            this.type = type;
//            this.defaultWord = defaultWord;
//        }
//    }
    
    static {
        loadKeywords();
    }
    
    static HashMap<String, Character> wordTypes;
    static HashMap<String, String> defaultWords;

    private static void loadKeywords() {
//        if (wordTypes != null) {
//            return;
//        }
//        wordTypes = new HashMap<String, Character>();
        
    wordTypes = new HashMap<String, Character>();
    defaultWords = new HashMap<String, String>();

        Scanner scanner = null;
        String last = "";
        char type = 'A';
        Pattern r = Pattern.compile("\"(\\w+)\"");
        Matcher m = null;
        int end = 0;
        String defaultWord = "";
        try {
            scanner = new Scanner(new FileInputStream("./lemmeEN.txt"));
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                m = r.matcher(line);

                end = 0;

                type = line.charAt(0);
                while (m.find(end) && !m.group(1).equals(last)) {
                    last = m.group(1);
                    if (!wordTypes.containsKey(m.group(1))) {
                        wordTypes.put(m.group(1), type);
                    }
                    if(end==0)
                    {
                        //first word
                        defaultWord = m.group(1);
                    }
                    else
                    {
                        if(!defaultWord.equals(m.group(1)) && !defaultWords.containsKey(m.group(1)))
                        {
                            defaultWords.put(m.group(1), defaultWord);
                        }
                    }
                        
                    end = m.end();
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Main.Main.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }
    }
    
    public static boolean containsType(String word)
    {
        return wordTypes.containsKey(word);
    }
    
    public static char getType(String word)
    {
        return wordTypes.get(word);
    }

//    public static boolean containsType(String word) {
//        return wordTypes.containsKey(word);
//    }
//
    public static String getDefaultWord(String word) {
        return defaultWords.get(word);
    }
    
    public static String[] lemmatize(String[] words) {

        String[] wordsCopy = words.clone();
        
        for(int i=0; i< wordsCopy.length; i++)
        {
            String word = wordsCopy[i];
            if(word.contains(" "))
            {
                String[] tmpWords = word.split("\\s+");
                for(String w : tmpWords)
                {
                    w = w.toLowerCase();
                    if (defaultWords.containsKey(w)) {
                        word = word.replace(w, defaultWords.get(w));
                    }
                }
                wordsCopy[i] = word;
            }
            else
            if(defaultWords.containsKey(word.toLowerCase()))
                wordsCopy[i] = defaultWords.get(word.toLowerCase());
        }
        return wordsCopy;
    }

    public static ArrayList lemmatize(ArrayList words) {
        ArrayList wordsCopy = (ArrayList) words.clone();

        for (int i = 0; i < wordsCopy.size(); i++) {
            //TODO separe
            String word = (String) wordsCopy.get(i);
            if (word.contains(" ")) {
                String[] tmpWords = word.split("\\s+");
                for (String w : tmpWords) {
                    w = w.toLowerCase();
                    if (defaultWords.containsKey(w)) {
                        word = word.replace(w, defaultWords.get(w));
                    }
                }
                wordsCopy.set(i,word);
            } 
            else
            if (defaultWords.containsKey(word)) {
                wordsCopy.set(i, defaultWords.get(word));
            }
        }
        return wordsCopy;
    }

    //changes to lower by default or not.
    public static String lemmatize(String text) {
//        StringBuilder outText = new StringBuilder(text);
        
        //TODO to lower case
        
        ArrayList<WordChange> changes = new ArrayList();
//        Pair<int,int> pair = new Pair<int,int>();
        Pattern p = Pattern.compile("(^|[^a-zA-Z])([a-zA-Z]+)([^a-zA-Z]|$)");
        Matcher m = p.matcher(text);
        
        String word = "";
        int end=0;
        while(m.find(end))
        {
            word = m.group(2).toLowerCase();
            if(defaultWords.containsKey(word))
                changes.add(new WordChange(word, m.start(2), m.end(2)));
            end = m.end(2);
        }
        
        if(changes.size() > 0)
        {
            int diff = 0;

            StringBuilder outText = new StringBuilder(text);
            for(WordChange wordChange : changes)
            {
                word = defaultWords.get(wordChange.word);
                outText.replace(wordChange.start-diff, wordChange.end-diff, word);
                diff +=  wordChange.word.length() -word.length();
            }
            return outText.toString();
        }
        else
        return text;
    }
}


class WordChange
{
    String word;
    int start;
    int end;
    WordChange(String word, int start, int end)
    {
        this.word = word;
        this.start = start;
        this.end = end;
    }
}