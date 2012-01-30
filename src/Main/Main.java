/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;

import AnswerFinding.Answer;
import AnswerFinding.NovelInfo;
import AnswerFinding.QueryResult;
import QuestionParser.Lemmatizer;
import QuestionParser.MainKnowledge;
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
    
    
    public static void main(String[] args) {
        NovelInfo ni = new NovelInfo("output.xml", "rezumat.txt", "situations.xml");
        Answer a = new Answer(ni);
        String question = "What does Ebenezer Scrooge do in the novel?";
        Question q = Parser.parse(question);
        ArrayList<QueryResult> qrs = new ArrayList<QueryResult>();
        
        System.out.println(a.answer(q));




        
        //Testing time parsing
//        String situation = "At the request of Mr. Gatsby we are going to play for you Mr Vladimir Tostoff's latest work which attracted"
//+"so much attention at Carnegie Hall last May.";
//        String situation = "It is an old time-table now, disintegrating at its folds and headed \"This schedule in effect July 5th, 1922.\"";
//        String situation = "I remembered of course that the World's Series had been fixed in 1919 but if I had "
//                + "thought of it at all I would have thought of it as a thing that merely HAPPENED, the end of some inevitable chain.";
//        ArrayList<String> dates = MainKnowledge.getDates(situation) ;
//        if(dates != null)
//        {
//            for(String date : dates)
//            {
//                System.out.println("d: " + date);
//            }
//        }
        
        
        /*//Search 1 example
//        String data = MainKnowledge.searchWiki("Myrtle Wilson", null) ;
//        System.out.println("out: " + data);

        //Search 2 example
        ArrayList<String> keywords = new ArrayList<String>();
        keywords.add("publisher");
        //better not include short words like "is" or other small words.
        data = MainKnowledge.searchWiki("Great Gatsby", keywords);
        System.out.println("out: " + data);*/

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
    }
}


