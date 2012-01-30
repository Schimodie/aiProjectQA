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
    
    
//    public static String answer(String questionText)
//    {
//        Question question = Parser.parse(questionText);
//        
//        return "Hello World";
//    }
//    
//    public static void loadKnowledge(String path)
//    {
//        //TODO
//    }
    
    public static String searchWiki(String inputString, ArrayList<String> keywords) {
//        StringBuffer inputSb = new StringBuffer(inputString);
        
        HashMap<String, String> data = new HashMap<String, String>();
        
        String keywordsPattern = null;
        if(keywords != null && keywords.size() > 0)
        {
            keywordsPattern = keywords.get(0).replaceAll("\\s+", "\\\\s+");
            for(int i=1; i<keywords.size(); i++)
                keywordsPattern += "|" +keywords.get(i).replaceAll("\\s+", "\\\\s+");
            keywordsPattern = "(?i)^\\s*"+keywordsPattern + ".*";
        }

        String parsedText = inputString.trim().replaceAll("\\s+", "%20");
        if (parsedText.length() == 0) {
            return "";
        }

        String text = "";
                
        try
        {
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

            text = sb.toString();

        }
        catch(Exception ex)
        {
            return "";
        }
                
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
        
        String tagRegex = "</?\\w+(\\s+\\w(\\w|:)*\\s*=\\s*('[^']*'|\"[^\"]*\"))*\\s*/?>?";
        text = text.replaceAll("(?s)<a\\s+href=(\"|')#cite.*?</\\s*a>", " ");
        text = text.replaceAll("(?s)<span\\s+class=(\"|')editsection.*?</\\s*span>", " ");
        text = text.replaceAll("(?s)<div\\s+class=(\"|')dablink.*?</\\s*div>", " ");
        text = text.replaceAll("(?s)<div\\s+class=(\"|')rellink.*?</\\s*div>", " ");
        //text = text.replaceAll("(?s)<div\\s+class=(\"|')thumb.*?</\\s*div>", " ");
        text = text.replaceAll("(&#160;|&nbsp;)", " ");
        text = text.replaceAll("(?s)<div\\s+class=(\"|')reflist.*$","");
        
        //substract infograph
        p = Pattern.compile("<table\\s+class=\"infobox.*?</table>",Pattern.DOTALL);
        m = p.matcher(text);
        String infoBox = "";
        int infoBoxEnd = 0;
        if(m.find())
        {
            infoBox = m.group();
            infoBoxEnd = m.end();
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
            text = text.substring(infoBoxEnd);
//                text = text.replace(infoBox, " ");
        
        //End infograph
        
        
        //removing table of content
//        String firstPart= "";
//        p = Pattern.compile("(?s)<table\\s+id=(\"|')toc(\"|').*?</\\s*table>");
//        m = p.matcher(text);
//        if(m.find())
//        {
//            firstPart = text.substring(0, m.start());
//            text = text.replace(m.group(), " ");
//        }

        //removing table of contents
        text = text.replaceAll("(?s)<table\\s+id=(\"|')toc(\"|').*?</\\s*table>", " ");

        
        if(!keywordsPattern.equals(""))
        {
            for(String key : data.keySet())
            {
                if(key.matches(keywordsPattern))
                    return data.get(key);
            }
            return "";
        }
            
        
        
//        if(!firstPart.equals(""))
//        {
//            //then it's a good wiki page
//            firstPart = firstPart.replaceAll(tagRegex, " ").replaceAll("<!--[^>]+>", " ").replaceAll("\\s+", " ");
//            for(String word in inputString)
//        }

        ArrayList<String> paragraphs = new ArrayList<String>();
        
        p = Pattern.compile("(?s)<p>(.*?)</p>");
        m = p.matcher(text);
        
        end =0;
        while(m.find(end))
        {
            paragraphs.add(m.group(1).replaceAll(tagRegex, " ").replaceAll("<!--[^>]+>", " ").replaceAll("\\s+", " "));
            end = m.end();
        }
        
        if(paragraphs.size() == 0)
            return "";
        
        
        String output = "";
        
        String startText = paragraphs.get(0);
        String pattern = inputString;
        if(inputString.contains(" "))
            pattern = "(" + inputString.replaceAll("\\s+","|") + ")";
        p = Pattern.compile("(?i)^\\s*" + pattern);
        m = p.matcher(startText) ;
        if(m.find())
        {
            //paragraph about person. then it's ok
            output = startText;
            int index = 1;
            while(output.length() < 500 && paragraphs.size() >= index)
            {
                output+=paragraphs.get(index);
                index++;
            }
        }
        else
        {
            //search in list
//            System.out.println("zz: " + "(?i)(?s)<li>(\\s*" + inputString.replaceAll("\\s+", "\\s+") + ".*?)</li>");
            p = Pattern.compile("(?i)(?s)<li>(\\s*"+inputString.replaceAll("\\s+", "\\\\s+") +".*?)</li>");
            m= p.matcher(text);
            if(m.find())
            {
                output = m.group(1);
            }
            else {
                //TODO full scan.
            }

        }
        //TODO search by keywords.
        
        
        
        //check first paragraph
        //collect until > 500 words if short only first paragraph
        
        return output;
        
        
//        text = text.replaceAll(tagRegex," ").replaceAll("<!--[^>]+>", " ").replaceAll("\\s+", " "); 
//        text = text.replaceAll("<!--[^>]+>", " ").replaceAll("\\s+", " ");
        
//        return text;
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
