/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package QuestionParser;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Dinu
 */
public class Parser {

    static 
    {
        phrasalVerbs = new HashMap<String, ArrayList>();
        try {
            loadPhrasalVerbs();
        } catch (IOException ex) {
            Logger.getLogger(Parser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


//persoane, numere, masuri, locatii, timp, organizatii, obiecte
    static final String[]wordnet =
    {
        "(scorer|director|President|spouses|wife|writer|actor|artist\\s|singer|historian|thinker|author|mortal|immortal|member.?|partner.?|goddess|representatives|disciple)",
        "(position.?|age|percentage|percent|number.?)",
        "(area|height|mass|masses|amplitude|duration|depth|speed|length|quantity|size|density)",
        "((region|district|Capital|island|Peak|territory|province|continent|state|street|avenue|boulevard|county|republic)(s?))|((Cit|countr|count)(y|ies))",
        "(year(s?)|centur(y|ies)|period(s?)|date(s?)|time|hour)",
        
//        "(organization|Organizers|agents|University|group|company|companies|entity|entities|Manufacturer|institution.?|prison.?|Order.?|Party|CIA|Agency|UNESCO|Eesti|Solidarity|Ethers|led|NATO|European Union|EU)",
//        "(a|an)?\\s?(?>instrument|product|gun.?|machine|middle|unit|concrete-wood|equipment|toy.?|compass|compasses|armor.?|equipment|computer|device|Selena|stole)"
    };
    
//    static final String[][] q_types = {
//        {"What's the procedure", "What is the procedure", "What are the stages", "What are the Community's procedures",
//            "What  measures", "What are the Community's procedures for","Under what circumstances", "In what circumstances"},
//        {"Why", "By what reason", "For what reason", "What is the motive", "What is  the main reason", "On what ground"},
//        {"What is the purpose", "What's the purpose", "To what purpose", "On what purpose", "For what purpose",
//            "What's the aim", "/What is the aim", "What's the objective", "What is the objective", "What are the goals",
//            "What are the objectives", "What are the Community's objectives", "What are the aims", "What is the scope"},
//        {"What means\\s+[A-Z]", "What is (a|an)", "What are the", "Who (is|was)\\s+[A-Z]", "What does the term",
//            "What is meant by", "What(is|was|are)\\s+(?!(?>a|an)\\s)", "What is the meaning", "What is the definition"},
//        {"What types", "Who were", "Whom", "Through what", "Name (?!(?>a|an)\\s)"}
//    };
    
//    static final String[][] a_types = {{"(Why|What|Name|With who|What is called)", // .*?+wordnet[0],
//        "What is (his|her) name", "Who", "Whom", "Whos", "With who"},
//            {"Approximately how many","Of how many", "How many", "How much", "Of how much", "(What|Who is)\\s"+ wordnet[1]},
//            {"How (much|manny) ", "(What is the).*?"+wordnet[2]},
//            {"What (state|city)", "From where", "Where", "(On what|What|On which|Name).*?"+wordnet[3]},
//            {"When", "(In what|From what|At what|After how (much|many)) "+wordnet[4]},
//            {"Who produced", "Who made", "(What was the|What is the|At|From what|What was|What is).*?(?<!of\\s)"+wordnet[5]},
//            {"What ", "What (give|gived|gives)", "With what(he|she|it) ", "(What|Name|What is the name of the|For what|At what).*?"+wordnet[6]}
//    };

    static final String[][] a_types = {{"(Why|What|Name|With who|What is called)", // .*?+wordnet[0],
            "What is (his|her) name", "Who", "Whom", "Whos", "With who"},
        {"What (state|city)", "From where", "Where", "(On what|What|On which|Name).*?" + wordnet[3]},
        {"When", "(In what|From what|At what|After how (much|many)) " + wordnet[4]},
        {"Who produced", "Who made", "(What was the|What is the|At|From what|What was|What is).*?(?<!of\\s)"},
        {"What ", "What (give|gived|gives)", "With what(he|she|it) ", "(What|Name|What is the name of the|For what|At what).*?"},

        {"Approximately how many", "Of how many", "Of how much", "(What|Who is)\\s" + wordnet[1],
        "How (much|manny) ", "(What is the) " + wordnet[2]}
    };

    
//    static String[] a_typesTitle = {"PERSON", "COUNT", "MEASURE", "LOCATION", "TIME", "ORGANIZATION", "OBJECT"};
    static String[] a_typesTitle = {"PERSON", "LOCATION", "TIME", "ORGANIZATION", "OBJECT", "QUANTITY"};
    
//    static String[] q_typesTitle = {"PROCEDURE", "RESON", "PURPOSE", "DEFINITION", "LIST"};
    
    public static Question parse(String question)
    {
        
        Question outputQuestion = new Question(question);
        
        question = question.replaceFirst("in\\s+the\\s+novel", ""); 

//
//        Match match = new Match();
//        Pattern r = Pattern.compile(a_types[0][0]);
//
        boolean found = false;
//        for(int i=0; i<a_types.length; i++)
//        {
//            for(String pattern : a_types[i])
//            {
//                if(Pattern.matches(pattern, question))
//                {
//                    outputQuestion.setAnswerType(a_typesTitle[i]);
//                    found = true;
//                    break;
//                }
//            }
//            if(found)
//                break;
//        }
//
//        found = false;
//        for(int i=0; i<q_types.length; i++)
//        {
//            for(String pattern : q_types[i])
//            {
//                if(Pattern.matches(pattern, question))
//                {
//                    outputQuestion.setQuestionType(q_typesTitle[i]);
//                    found = true;
//                    break;
//                }
//            }
//            if(found)
//                break;
//        }
//
//        findKeywords(outputQuestion);
        
        
        String[] patterns = {"^What (is|are) ", "^What ", "^Who (is|are) ", "^Where ", "^When (does|did|do)?", "^What (does|did) (\\w+ \\w+) do "};
        String[] answerType = {"Definition", "Object", "Person", "Location", "Time", "Occupation"};
        
        
        
//        for(int i=0; i<patterns.length; i++)
//        {
//            Pattern p = Pattern.compile(patterns[i]);
//            Matcher m = p.matcher(question);
//            if(m.find())
//            {
//                outputQuestion.setAnswerType(answerType[i]);
//                
//                break;
//            }
//        }


        
        Pattern novelP = Pattern.compile("(^|\\s*)(the\\s+)?novel");
        Pattern personP = Pattern.compile("(^|\\s*)(the)?\\s*(([A-Z]\\w+\\s*)+)");
//        Pattern mainCharP = Pattern.compile("");
        
        String additionalP = "\\s*(((the\\s+)?novel)|((the)?\\s*(([A-Z]\\w+\\s*)+)))?";
        
        Pattern p = Pattern.compile("^(W|w)hat\\s+(is|are)\\s+" + additionalP);
        Matcher m = p.matcher(question);
        if (m.find()) {
            outputQuestion.setAnswerType(AnswerType.DEFINITION);
            if (m.group(2) != null) {
                setMultiplicity(outputQuestion,m.group(2));
            }
            
            int start = 3;
            if(m.groupCount() >= start && m.group(start) != null)
            {
                if (m.group(start+1) != null) {
                    outputQuestion.addFocusType(FocusType.NOVEL);
//                    question = question.replace(m.group(start + 1), "");
                } else 
                if (m.group(start+3) != null) {
//                    if (m.group(start + 3) != null) {
                    outputQuestion.addFocusType(FocusType.OBJECT);
//                    } else {
//                        outputQuestion.addFocusType(FocusType.PERSON);
//                    }

                    outputQuestion.addMainObject(m.group(start + 5));
//                    question = question.replace(m2.group(0), "");
                }
            }
            
            
//            Matcher m2 = novelP.matcher(question);
//            if(m2.find(m.end()))
//            {
//                outputQuestion.addFocusType(FocusType.NOVEL);
//                question = question.replace(m2.group(0), "");
//            }
//            else
//            {
//                m2 = personP.matcher(question);
//                if (m2.find(m.end())) {
//                    if(m2.group(1) != null)
//                        outputQuestion.addFocusType(FocusType.OBJECT);
//                    else
//                        outputQuestion.addFocusType(FocusType.PERSON);
//                    outputQuestion.addMainObject(m2.group(2)); 
//                    question = question.replace(m2.group(0), "");
//                }
//            }

            
            
            question = question.replace(m.group(0), ""); 
            
//            m2 = mainCharP.matcher(question);
//            if (m2.find(m.end())) {
//                outputQuestion.setFocus("main character");
//            }
        }
        else
        {
            p = Pattern.compile("^(W|w)hat\\s+(\\w+)\\s+(is|are)\\s+" + additionalP);
            m = p.matcher(question);
            if (m.find()) {
                outputQuestion.setAnswerType(AnswerType.DEFINITION);

                if (m.group(3) != null) {
                    setMultiplicity(outputQuestion, m.group(3));
                }


                int start = 4;
                if (m.groupCount() >= start && m.group(start) != null) {
                    if (m.group(start + 1) != null) {
                        outputQuestion.addFocusType(FocusType.NOVEL);
//                    question = question.replace(m.group(start + 1), "");
                    } else if (m.group(start + 3) != null) {
//                    if (m.group(start + 3) != null) {
                        outputQuestion.addFocusType(FocusType.OBJECT);
//                    } else {
//                        outputQuestion.addFocusType(FocusType.PERSON);
//                    }

                        outputQuestion.addMainObject(m.group(start + 5));
//                    question = question.replace(m2.group(0), "");
                    }
                }
                //retaining word
                question = m.group(1) + " " + question.replace(m.group(0), "");
            }
            else
            {
                p = Pattern.compile("^(W|w)hat\\s+((does|did|do)|(\\w+))(\\W|$)" + additionalP );
                m = p.matcher(question);
                if (m.find()) {
//                    outputQuestion.setAnswerType(AnswerType.EXPLANATION);
                    if (m.group(3) != null) {
                        outputQuestion.setAnswerType(AnswerType.DEFINITION);

                        setMultiplicity(outputQuestion, m.group(3));
//                        if (m.group(1).toUpperCase().equals("IS")) {
//                            outputQuestion.setMulitplicity(Multiplicity.MULTIPLE);
//                        } else if (m.group(1).toUpperCase().equals("ARE")) {
//                            outputQuestion.setMulitplicity(Multiplicity.SINGLE);
//                        }
                    }
                    else
                        outputQuestion.setAnswerType(AnswerType.EVENT);
                     //TODO maybe change to situation   
//TOOD change spaces to \\s


                    int start = 6;
                    if (m.groupCount() >= start && m.group(start) != null) {
                        if (m.group(start + 1) != null) {
                            outputQuestion.addFocusType(FocusType.NOVEL);
                        } else if (m.group(start + 3) != null) {
//                    if (m.group(start + 3) != null) {
//                            outputQuestion.addFocusType(FocusType.OBJECT);
//                    } else {
//                        outputQuestion.addFocusType(FocusType.PERSON);
//                    }

//                    if (outputQuestion.getAnswerType() == AnswerType.DEFINITION) { //What does ???
//                            outputQuestion.addFocusType(FocusType.PERSON);
//                    } else {
                        outputQuestion.addFocusType(FocusType.OBJECT);
//                    }

                            outputQuestion.addMainObject(m.group(start + 5));
                        }
                    }

                    question = question.replace(m.group(0), "");
                    if(m.group(4) != null)
                        question = m.group(4) + " " + question;
                    else
                    {
                        if(m.group(3) != null && ((question.trim().length() > 2 &&question.trim().startsWith("do ")) || 
                                (question.trim().length() == 2 && question.trim().startsWith("do"))))
                        {
                            //What does he do
                            outputQuestion.addFocusType(FocusType.OCCUPATION);
                            question = question.replaceFirst("^\\s*do", "");
                        }
                    }
                }
            }
        }

        p = Pattern.compile("^(W|w)ho\\s+((is|are)|(\\w+))(\\W|$)+" + additionalP);
        m = p.matcher(question);
        if (m.find()) {
            outputQuestion.setAnswerType(AnswerType.PERSON);
            if (m.group(3) != null) {
                setMultiplicity(outputQuestion, m.group(3));
            }
            
//            FocusType defaultFocus = FocusType.PERSON;
//            if(m.group(3))
                
            int start = 6;
            if (m.groupCount() >= start && m.group(start) != null) {
                if (m.group(start + 1) != null) {
                    outputQuestion.addFocusType(FocusType.NOVEL);
                } else if (m.group(start + 3) != null) {
                    //Becouse we find name 
                    if(m.group(3) != null) //other verb //TODO check this
//                    {
                        outputQuestion.setAnswerType(AnswerType.DEFINITION);
//                        outputQuestion.addFocusType(FocusType.PERSON);
//                    }
//                    else
                        outputQuestion.addFocusType(FocusType.PERSON);
//                    if (m.group(start + 3) != null) {
//                    outputQuestion.addFocusType(FocusType.OBJECT);
//                    } else {
//                        outputQuestion.addFocusType(FocusType.PERSON);
//                    }

                    outputQuestion.addMainObject(m.group(start + 5));
                }
            }
            
            question = question.replace(m.group(0), "");
            if (m.group(4) != null) {
                question = m.group(4) + " " + question;
            }
        }

//        System.out.println("^(W|w)here\\s+((is|are|does|did|do)|(\\w+))\\s+" + additionalP);
        p = Pattern.compile("^(W|w)here\\s+((is|are|does|did|do)|(\\w+))\\s+" + additionalP);
        m = p.matcher(question);
        if (m.find()) {
            outputQuestion.setAnswerType(AnswerType.LOCATION);
            if (m.group(3) != null) {
                setMultiplicity(outputQuestion, m.group(3));
            }
            
            
            int start = 5;
            if (m.groupCount() >= start && m.group(start) != null) {
                if (m.group(start + 1) != null) {
                    outputQuestion.addFocusType(FocusType.NOVEL);
                } else if (m.group(start + 3) != null) {
                    
//                    if (m.group(start + 3) != null) {
                    outputQuestion.addFocusType(FocusType.OBJECT);
//                    } else {
//                        outputQuestion.addFocusType(FocusType.PERSON);
//                    }

                    
                    outputQuestion.addMainObject(m.group(start + 5));
                }
            }

            question = question.replace(m.group(0), "");
        }
        
        p = Pattern.compile("^(W|w)hen\\s+((does|did|do|is|are)|(\\w+))\\s+" + additionalP);
        m = p.matcher(question);
        if (m.find()) {
            outputQuestion.setAnswerType(AnswerType.TIME);
            if(m.group(3) != null)
            {
                setMultiplicity(outputQuestion, m.group(3));
            }
            
            
            int start = 5;
            if (m.groupCount() >= start && m.group(start) != null) {
                if (m.group(start + 1) != null) {
                    outputQuestion.addFocusType(FocusType.NOVEL);
                } else if (m.group(start + 3) != null) {
//                    if (m.group(start + 3) != null) {
                        outputQuestion.addFocusType(FocusType.OBJECT);
//                    } else {
//                        outputQuestion.addFocusType(FocusType.PERSON);
//                    }
                    outputQuestion.addMainObject(m.group(start + 5));
                }
            }

            question = question.replace(m.group(0), "");
        }
        
        p = Pattern.compile("^(H|h)ow\\s+((does|did|do|is|are)|(\\w+))\\s+" + additionalP);
        m = p.matcher(question);
        if (m.find()) {
            outputQuestion.setAnswerType(AnswerType.MODALITY);

            //TODO add word to ...

            if (m.group(3) != null) {
                setMultiplicity(outputQuestion, m.group(3));
            }


            int start = 5;
            if (m.groupCount() >= start && m.group(start) != null) {
                if (m.group(start + 1) != null) {
                    outputQuestion.addFocusType(FocusType.NOVEL);
                } else if (m.group(start + 3) != null) {
//                    if (m.group(start + 3) != null) {
                    outputQuestion.addFocusType(FocusType.OBJECT);
//                    } else {
//                        outputQuestion.addFocusType(FocusType.PERSON);
//                    }

                    outputQuestion.addMainObject(m.group(start + 5));
                }
            }

            question = question.replace(m.group(0), "");
        }
        
        
        String output = parseTime(outputQuestion, question);
        if (output != null) {
            question = output;
        }

//        String text = question;
        Pattern r = Pattern.compile("([\"\'](\\w)+[\"\'])|(\\s*(([A-Z]\\w+\\s*)+))");
        m = r.matcher(question);
        int end = 0;
        while (m.find(end)) {
            if(m.group(2) != null)
            {
                outputQuestion.addMainObject(m.group(2).trim());  //TODO changed
                question = question.replace(m.group(2), "");
            }
            else
            {
                outputQuestion.addMainObject(m.group(4).trim());
                question = question.replace(m.group(4), "");
            }
            end = m.end();
        }
       
        
        if((outputQuestion.getAnswerType() == AnswerType.DEFINITION || outputQuestion.getAnswerType() == AnswerType.PERSON) && 
                ((question.trim().length() > 2 && question.trim().startsWith("to ")) || (question.trim().length() == 2 && question.trim().startsWith("to"))))
        {
//            System.out.println("z: " + ((outputQuestion.getAnswerType() == AnswerType.DEFINITION || outputQuestion.getAnswerType() == AnswerType.PERSON) &&
//                    question.trim().length() >= 3 && question.trim().startsWith("to ")));
            outputQuestion.addFocusType(FocusType.RELATION);
//            question = question.replace("\\s*to", "");
            question = question.replaceFirst("^\\s*to", "");
        }
        
        if(question.contains("relationship")|| question.contains("relation"))
        {
            outputQuestion.addFocusType(FocusType.RELATION);
            question = question.replaceAll("(relationship|relation)s?", "");
        }
        
        if (question.contains("main character")) {
            outputQuestion.addFocusType(FocusType.MAIN_CHARACTER);
            question = question.replaceAll("main\\s+characters?", "");
        }
        
        Pattern ansP = Pattern.compile("((takes?|took)\\s+place|occur(s|ed)?|happen(s|ed)?)");
        Matcher mAns = ansP.matcher(question);
        
//        if((outputQuestion.getAnswerType() == AnswerType.LOCATION || outputQuestion.getAnswerType() == AnswerType.TIME)
//                && mAns.find())
        if (mAns.find())
        {
            question = question.replace(mAns.group(), "");
        }
        
        question = question.trim();
        
        if(!outputQuestion.containsMainObjects() && question.trim().startsWith("it"))
        {
            outputQuestion.addFocusType(FocusType.NOVEL);
            question = question.substring(2);
        }
       

        setKeywords(outputQuestion, question);

        return outputQuestion;
    }
    
    
    public static void setMultiplicity(Question outputQuestion, String verb)
    {
        verb = verb.toLowerCase();
        if (verb.equals("is") || verb.equals("does") ) {
            outputQuestion.setMulitplicity(Multiplicity.SINGLE);
        } else  { //if (verb.equals("are"))
            outputQuestion.setMulitplicity(Multiplicity.MULTIPLE);
        }

    }
    public static void setKeywords(Question outputQuestion, String question)
    {
        ///(January|February|March|April|May|June|July|August|September|October|November|December) \d\d, \d\d\d\d/i';
        //(jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec)
        
        
        //Filtering
        question = question.replaceAll("[\\?,.!:]", " ");

        question = question.trim().replaceAll("(^|\\W)(the|&|or|and)(\\W|$)", " ");
        

        
        
        if(question.equals(""))
        {
            outputQuestion.setKeywords(new String[0]);
            return;
        }
        
//        String[] words = question.split("\\s+");
        String[] words = Lemmatizer.lemmatize(question.split("\\s+"));
        int[] join = new int[words.length];
        boolean[] remove = new boolean[words.length];
        Arrays.fill(join, -1);
        Arrays.fill(remove, false);
        
//        int counter = 0;
        int joined = 0;
        int removed = 0;
        
//        for(String word : words)
        String word, nextWord;
        for(int i=0; i< words.length-1; i++)
        {
            word = words[i];
           if(Lemmatizer.containsType(word) && Lemmatizer.getType(word) == 'J') //Adjective
           {
               //and next is noun or unknown join
               
               if(i+1<words.length ) 
               {
                   nextWord = words[i+1];
                   if (!Lemmatizer.containsType(nextWord) || Lemmatizer.getType(nextWord) == 'N')
                   {
                       joined++;
//                       counter++;
//                       join[i] = counter;
                       join[i+1] = i;
                       i++;
                   }
               }
           }

           
           if (phrasalVerbs.containsKey(word))
           {
               nextWord = words[i + 1];
               if(phrasalVerbs.get(word).contains(nextWord))
               {
                   joined++;
                   join[i+1]=i;
                   i++;
               }
           }
               
           
           
            if (words[i].toUpperCase().equals("TO")) //Adjective
            {

                nextWord = words[i + 1];
                if (!Lemmatizer.containsType(nextWord) || Lemmatizer.getType(nextWord) == 'V') {
                    removed++;
                    remove[i] = true;
                }
            }
        }
        
        
        
        if(joined + removed > 0)
        {
            String[] outputWords = new String[words.length-joined - removed];
            int counter = 0;
//            int n = words.length;
            for(int i=0; i< words.length; i++)
            {
                if(remove[i])
                {
                    counter++;
                    continue;
                }
                
                if(join[i] < 0)
                {
                    outputWords[i - counter] = words[i];
                }
                else
                {
                    outputWords[join[i]-counter] += " " + words[i];
                    counter++;
                }
            }
            words = outputWords;
        }
        
        
//        words = filterWords(words);
        outputQuestion.setKeywords(words);
    }
    
    public static String parseTime(Question outputQuestion, String text)
    {
        text = " " + text + " "; // temporary fix for problem
        //TODO parse for all questions.

//            String year = "(^|\\D)\\d{4}[^\\d]";
//            String month = "\\d\\d[^0-9]";
//            String day = "\\d\\d[^0-9]";
        String longMonth = "(January|February|March|April|May|June|July|August|September|October|November|December)";
        String day = "(((twenty|thirty[- ]+)?(first|second|third|fourth|fifth|sixth|seventh|eighth|ninth|tenth))|"
                + "(eleventh|twelfth|thirteenth|fourteenth|fifthteenth|sixteenth|seventeenth|eighteenth|nineteenth))";
//                    String twoDigitMult = "([^|\\D]\\d{2}[^\\d])+";
//                    String or = "|";
//                    String sep = "[-,/ ]*";
//        String x = "";
//                            "(" + twoDigitMult + "(th)?(\\s+of)?" + sep + ")?" + longMonth + //28th of December
//                            "(" + sep + twoDigitMult + ")?" + "(" + sep + year + ")?" + //28 2003
//                            "(" + sep + twoDigitMult + ")?" + or + //30
//                            "(" + twoDigitMult + sep + ")?" + year + "(" + sep + twoDigitMult + ")?"; //date,month,year or year,month,date
//                    System.out.println("string: (" + x + ")");

        //in on

        String x = "(?i)(((^|\\W)(in|on|year|month))?"
                + "(([-,/ ]|^)\\d{4}([-,/ ]+\\d{1,2}){2}" + "|"
                + "(([-,/ ]|^)+\\d{1,2}){0,2}[-,/ ]\\d{4}" + "|"
                + "([-,/ ]|^)+(\\d{1,2}(th|rd|st)?|" + day + ")?(\\s+of)?([-,/ ]|^)*" + longMonth + "([-,/ ]+\\d{1,2}(th|rd|st)?)?" + "([-,/ ]+\\d{4})?" + "))(\\D|$)";
                
//                + "(([-,/ ]|^)+\\d{1,2}(th|rd|st)?)?(\\s+of)?" + longMonth + "([-,/ ]+\\d{1,2}(th|rd|st)?)?" + "([-,/ ]+\\d{4})?" + "))(\\D|$)";

//                    System.out.println("string: (" + x + ")");

        boolean found = false;
        Pattern dateP = Pattern.compile(x);
        Matcher dateM = dateP.matcher(text);
        while (dateM.find()) {
            outputQuestion.addDate(dateM.group(5).trim());
            text = text.replace(dateM.group().trim(), "").trim();
            found = true;
        }
        
        if(found)
        {
            if(outputQuestion.getAnswerType() == AnswerType.DEFINITION)
                outputQuestion.setAnswerType(AnswerType.EVENT);
            return text;
        }
        return null;
    }
    
//    public void parseDates(Question outputQuestion, String question)
//    {
//        String year = "[-,/ ]+\\d\\d\\d\\d";
//        String month = "[-,/ ]+\\d\\d[^0-9]";
//        String day = "[-,/ ]+\\d\\d[^0-9]";
//        String longMonth = "[-,/ ]+(January|February|March|April|May|June|July|August|September|October|November|December)";
//        String twoDigitMult = "([-,/ ]+\\d\\d[^0-9])*";
//        String or = "|";
//        String x = twoDigitMult +"(th)?(\\s+of)?" + longMonth + twoDigitMult + year +"?" + twoDigitMult + or + twoDigitMult + year + twoDigitMult;
//        Pattern p = Pattern.compile("(" + x + ")");
//        Matcher m = p.matcher(question);
//        if (m.find()) {
//            outputQuestion.addDate(m.group(0));
//            question = question.replace(m.group(0), "");
//        }
//    }
    
    
    static HashMap<String, ArrayList> phrasalVerbs;

    static void loadPhrasalVerbs() throws IOException
    {
        String prevVerb = null; //valoare care retine verbul de pe linia precedenta din fisier
        String rest = new String(); //String care retine prepozitiile/adverbele care alcatuiesc PV-urile impreuna cu verbul
        ArrayList<String> restList = new ArrayList<String>(); //lista care retine toate prep./adverbele care alcatuiesc PV-uri cu verbul
        //cerem un verb pentru a testa functionalitatea programului

        try {
            //deschidem fisierul
            FileInputStream fstream = new FileInputStream("./phrasal_verbs.txt");
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            //citim fisierul linie cu linie
            while ((strLine = br.readLine()) != null) {
                //despartim liniile fisierelor in string-uri
                String[] result = strLine.trim().split("\\s+");
                //daca suntem la primul verb din fisier, il setam ca prevVerb
                if (prevVerb == null) {
                    prevVerb = result[0];
                }
                //daca suntem la acelasi verb ca in linia precedenta, adaugam la restList prep./adverbul de dupa verb
                if (result[0].equals(prevVerb)) {
                    rest = result[1];
                    for (int x = 2; x < result.length; x++) {
                        rest = rest + " " + result[x].toLowerCase();
                    }
                    restList.add(rest);
                }
                //daca avem un verb diferit fata de cel de la linia precedenta,
                //golim restList, setam prevVerb ca verbul curent si adaugam
                //prep./adverbul de dupa acesta in restList
                else 
                {
                    phrasalVerbs.put(prevVerb.toLowerCase(), restList);
                    restList = new ArrayList<String>();
                    prevVerb = result[0];
                    rest = result[1];
                    for (int x = 2; x < result.length; x++) {
                        rest = rest + " " + result[x].toLowerCase();
                    }
                    restList.add(rest);
                }
                //adaugam in HashMap lista prep./adverbelor pentru verbul curent
                
                rest = "";
            }
            if(restList.size() > 0)
                phrasalVerbs.put(prevVerb.toLowerCase(), restList);
            
            in.close();
        } catch (Exception e) {
            //Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
    }
    
}


