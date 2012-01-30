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
 * @author Radu
 */
public class Main {
    public static void main(String[] args) {
        NovelInfo ni = new NovelInfo("output.xml", "rezumat.txt", "situations.xml");
        Answer a = new Answer(ni);
        String question = "What does Ebenezer Scrooge do in the novel?";
        Question q = Parser.parse(question);
        ArrayList<QueryResult> qrs = new ArrayList<QueryResult>();
        
        System.out.println(a.answer(q));

        /*//Search 1 example
        String data = MainKnowledge.searchWiki("Myrtle Wilson", null) ;
        System.out.println("out: " + data);

        //Search 2 example
        ArrayList<String> keywords = new ArrayList<String>();
        keywords.add("publisher");
        //better not include short words like "is" or other small words.
        data = MainKnowledge.searchWiki("Great Gatsby", keywords);
        System.out.println("out: " + data);*/
    }
}


