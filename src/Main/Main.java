/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;

import AnswerFinding.Answer;
import AnswerFinding.NovelInfo;
import AnswerFinding.QueryResult;
import QuestionParser.Lemmatizer;
import QuestionParser.Parser;
import QuestionParser.Question;
import Situations.Situation;
import Situations.SituationsParser;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 *
 * @author Dinu
 */
public class Main {
    /**
     * @param args the command line arguments
     */
    
    
    public static void main(String[] args){
        /// TODO code application logic here
//        Parser parser = new Parser();
//        Question question = parser.Parse("What's your name?");
//
//        System.out.println("Question Type: " + question.getQuestionType());
//        System.out.println("Answer Type: " + question.getAnswerType());


//        System.out.println("out: " + Pattern.compile("What is?").matcher("What ").matches());
        
        
        
        /*String[] questions = TestingData.getMainQuestions(1);
        for(String question2 : questions)
        {
            Question parsedQuestion = Parser.parse(question2);
            System.out.println("Out: " + parsedQuestion);
        }*/
        
        int score = Integer.MIN_VALUE;
        NovelInfo ni = new NovelInfo("output.xml", "rezumat.txt", "situations.xml");
        Answer a = new Answer(ni);
        String question = "Who shows Scrooge two starved children?";
        Question q = Parser.parse(question);
        ArrayList<QueryResult> qrs = new ArrayList<QueryResult>();
        
        System.out.println(a.answer(q));
        
        /*SituationsParser accKeywords = new SituationsParser("situations.xml"); //A Christmas Carol
        
        accKeywords.parse();
        
        for (Situation sit : accKeywords.getSituations()) {
            //System.out.println(sit.toString() + "\n");
            
            qrs.addAll(sit.query(q));
        }
        
        Collections.sort(qrs, new Comparator<QueryResult>() {
            @Override
            public int compare(QueryResult o1, QueryResult o2) {
                return o2.getScore() - o1.getScore();
            }
        });
        
        for (QueryResult qr : qrs)
            System.out.println(qr.getScore() + " " + qr.getResult());*/
        
        //System.out.println((new File(".")).getAbsolutePath());
    }
}


