/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package QuestionParser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import com.softcorporation.util.Logger;
import com.softcorporation.suggester.util.Constants;
import com.softcorporation.suggester.util.SpellCheckConfiguration;
import com.softcorporation.suggester.Suggestion;
import com.softcorporation.suggester.tools.SpellCheck;
import com.softcorporation.suggester.dictionary.BasicDictionary;
import com.softcorporation.suggester.BasicSuggester;
import java.awt.TexturePaint;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
/**
 *
 * @author Dinu
 */
public class MainKnowledge {
    
    static SpellCheckConfiguration configuration = null;
    static BasicSuggester suggester = null;
    static
    {
        //prepare spellchecker
        try
        {
            String language = "en";
            String dictName = "english.jar";

            String dictFileName = "file://./" + dictName;

            BasicDictionary dictionary = new BasicDictionary(dictFileName);
            configuration = new SpellCheckConfiguration("file://spellCheck.config");

            suggester = new BasicSuggester(configuration);
            suggester.attach(dictionary);
        }
        catch(Exception ex)
        {
            System.err.println("Error loading dictionary");
        }
    }
    
    
    public static String answer(String questionText)
    {
        Question question = Parser.parse(questionText);
        
        return "Hello World";
    }
    
    public static void loadKnowledge(String path)
    {
        //TODO
    }
    
    public static String getWiki(String inputString, HashMap<String, String> data) throws MalformedURLException, IOException  {
//        StringBuffer inputSb = new StringBuffer(inputString);
        
        String parsedText = inputString.trim().replaceAll("\\s+", "%20");
        if(parsedText.length() == 0)
            return "";

        URL wikiURL = new URL("http://www.google.com/search?btnI=I'm+Feeling+Lucky&q=" + parsedText + "%20site%3Aen.wikipedia.org");
        
        HttpURLConnection connection = (HttpURLConnection) wikiURL.openConnection();
        connection.addRequestProperty("User-Agent", "Mozilla/4.76");
        connection.setConnectTimeout(15000);
        connection.setReadTimeout(15000);
        connection.setInstanceFollowRedirects(true);
        connection.connect();

                
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream())); //oracle.openStream()
        
        String inputLine;

        StringBuilder sb = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            sb.append(inputLine);
//            System.out.println(inputLine);
        }
        
        String text = sb.toString();

        int start = 0;
        int end = 0;
        
//        Pattern p = Pattern.compile("<\\s*body[^>]+>",Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Pattern p = Pattern.compile("<!--\\s*bodycontent\\s*-->", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher m = p.matcher(text);
        while(m.find(start))
        {
            start = m.end();
        }
        
//        p = Pattern.compile("<\\s*/\\s*body\\s*>", Pattern.CASE_INSENSITIVE);
        p = Pattern.compile("<!--\\s*/bodycontent\\s*-->", Pattern.CASE_INSENSITIVE);
        m = p.matcher(text);
        if(m.find(start))
        {
            end = m.start();
        }
        
        text = text.substring(start, end);
        
        String tagRegex = "</?\\w+(\\s+\\w+\\s*=\\s*('[^']*'|\"[^\"]*\"))*\\s*/?>?";
        //remove table of content
        text = text.replaceAll("(?s)<table\\s+id=(\"|')toc(\"|').*?</\\s*table>", " ");
        text = text.replaceAll("(?s)<a\\s+href=(\"|')#cite.*?</\\s*a>", " ");
        text = text.replaceAll("(?s)<span\\s+class=(\"|')editsection.*?</\\s*span>", " ");
        text = text.replaceAll("(?s)<div\\s+class=(\"|')dablink.*?</\\s*div>", " ");
        text = text.replaceAll("(?s)<div\\s+class=(\"|')rellink.*?</\\s*div>", " ");
        //text = text.replaceAll("(?s)<div\\s+class=(\"|')thumb.*?</\\s*div>", " ");
        text = text.replaceAll("(&#160;|&nbsp;)", " ");
        text = text.replaceAll("(?s)<div\\s+class=(\"|')reflist.*$","");
        
        if(data != null)
        {
            //substract infograph
            p = Pattern.compile("<table\\s+class=\"infobox.*?</table>",Pattern.DOTALL);
            m = p.matcher(text);
            String infoBox = "";
            if(m.find())
            {
                infoBox = m.group();
                //Substract Data
//                p = Pattern.compile("<tr.*?>\\s*<t(d|h).*?>(.*?)</t(d|h)>\\s*<td.*?>(.*?)</td>\\s*</tr>");
                p = Pattern.compile("<tr.*?>(.*?)</tr>");
                m = p.matcher(infoBox);
                Pattern p2 = Pattern.compile("\\s*<t(d|h).*?>(.*?)</t(d|h)>\\s*<td.*?>(.*?)</td>\\s*");
                Matcher m2;
                int endRows = 0;
                String row = "";
                while(m.find(endRows))
                {
                    row = m.group(1);
                    m2 = p2.matcher(row);
                    if(m2.find())
                    {
                        String k = m2.group(2).replaceAll(tagRegex, "");
                        String v = m2.group(4).replaceAll(tagRegex, "");
                        data.put(k, v);
                    }
                    
                    endRows = m.end();
                }

            }
            if(infoBox.length() >0)
                text = text.replace(infoBox, " ");
        }
        
        text = text.replaceAll(tagRegex," ").replaceAll("<!--[^>]+>", " ").replaceAll("\\s+", " "); 
//        text = text.replaceAll("<!--[^>]+>", " ").replaceAll("\\s+", " ");
        
        return text;
    }
    
    public static String checkAndCorrect(String text)
    {
        if(suggester == null || configuration == null)
            return text;
        try {

            ArrayList suggestions = null;

            SpellCheck spellCheck = new SpellCheck(configuration);
            spellCheck.setSuggester(suggester);
            spellCheck.setSuggestionLimit(5);

            spellCheck.setText(text, Constants.DOC_TYPE_TEXT, "en");

            spellCheck.check();

            while (spellCheck.hasMisspelt()) {
                String misspeltWord = spellCheck.getMisspelt();

                suggestions = spellCheck.getSuggestions();

                if(suggestions.size() > 0)
                {
                    Suggestion suggestion = (Suggestion) suggestions.get(0);
                    spellCheck.change(suggestion.getWord());
                }

                spellCheck.checkNext();
            }
            text = spellCheck.getText();

        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        
        return text;
    }
    
    
    
    
    
    
    
}
