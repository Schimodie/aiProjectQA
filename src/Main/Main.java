package Main;

import AnswerFinding.Answer;
import AnswerFinding.NovelInfo;
import AnswerFinding.QueryResult;
import QuestionParser.MainKnowledge;
import QuestionParser.Parser;
import QuestionParser.Question;
import java.util.ArrayList;

/**
 *
 * @author Radu
 */
public class Main {
    public static void main(String[] args) {
        
        NovelInfo ni = new NovelInfo("output.xml", "rezumat.txt", "situations.xml");
        Answer a = new Answer(ni);
        String question = "Who is Marley?";
        Question q = Parser.parse(question);
        ArrayList<QueryResult> qrs = new ArrayList<QueryResult>();
        
        System.out.println(a.answer(q));

//        try
//        {
//            Server.Run(a); 
//        } catch (IOException ex) {
//            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
//        }

        
//Testing time parsing
/*       String situation = "At the request of Mr. Gatsby we are going to play for you Mr Vladimir Tostoff's latest work which attracted"
           + "so much attention at Carnegie Hall last May.";*/
//        String situation = "It is an old time-table now, disintegrating at its folds and headed \"This schedule in effect July 5th, 1922.\"";
//        String situation = "I remembered of course that the World's Series had been fixed in 1919 but if I had "
/*                + "thought of it at all I would have thought of it as a thing that merely HAPPENED, the end of some inevitable chain.";
        ArrayList<String> dates = MainKnowledge.getDates(situation) ;

        if(dates != null)
        {
            for(String date : dates)
            {
                System.out.println("d: " + date);
            }
        }*/
        
        
        /*//Search 1 example
//        String data = MainKnowledge.searchWiki("Myrtle Wilson", null) ;
//        System.out.println("out: " + data);

        //Search 2 example
        ArrayList<String> keywords = new ArrayList<String>();
        keywords.add("publisher");
        //better not include short words like "is" or other small words.
        data = MainKnowledge.searchWiki("Great Gatsby", keywords);
        System.out.println("out: " + data);*/
    }
}


