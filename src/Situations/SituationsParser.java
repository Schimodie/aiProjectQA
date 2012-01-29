package Situations;

import java.io.File;
import java.util.ArrayList;
import org.w3c.dom.Document;
import javax.xml.parsers.*;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SituationsParser {
    private ArrayList<Situation> situations = new ArrayList<Situation>();
    private File file = null;
	private Document doc = null;
	
	public SituationsParser(String path) {
        try {
            this.file = new File(path);
            
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            
            this.doc = db.parse(file);
        }
        catch (Exception ex) {
            System.out.println(ex);
        }
    }
    
    public void parse() {
        Element element;
        Element element1;
        Node node;
        NodeList situationNodeList = this.doc.getElementsByTagName("situation");
        NodeList playerNodeList;
        NodeList objectNodeList;
        NodeList eventNodeList;
        NodeList keywordNodeList;
        NodeList placeNodeList;
        NodeList fstNm;
        Situation sit;
        
        for (int i = 0; i < situationNodeList.getLength(); i++) {
            node = situationNodeList.item(i);
            
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                element = (Element) node;
                playerNodeList = element.getElementsByTagName("player");
                objectNodeList = element.getElementsByTagName("object");
                eventNodeList = element.getElementsByTagName("event");
                keywordNodeList = element.getElementsByTagName("keyword");
                placeNodeList = element.getElementsByTagName("place");
                
                sit = new Situation (
                    element.getElementsByTagName("name")
                           .item(0)
                           .getTextContent().trim(),
                    element.getElementsByTagName("paragraph")
                           .item(0)
                           .getTextContent().trim()
                );
                
                for (int j = 0; j < playerNodeList.getLength(); j++) {
                    element1 = (Element) playerNodeList.item(j);
                    fstNm = element1.getChildNodes();
                    
                    for (int k = 0; k < fstNm.getLength(); k++)
                        sit.addCharacter(fstNm.item(k).getTextContent());
                }
                
                for (int j = 0; j < objectNodeList.getLength(); j++) {
                    element1 = (Element) objectNodeList.item(j);
                    fstNm = element1.getChildNodes();
                    
                    for (int k = 0; k < fstNm.getLength(); k++)
                        sit.addObject(fstNm.item(k).getTextContent());
                }
                
                for (int j = 0; j < eventNodeList.getLength(); j++) {
                    element1 = (Element) eventNodeList.item(j);
                    fstNm = element1.getChildNodes();
                    
                    for (int k = 0; k < fstNm.getLength(); k++)
                        sit.addEvent(fstNm.item(k).getTextContent());
                }
                
                for (int j = 0; j < placeNodeList.getLength(); j++) {
                    element1 = (Element) placeNodeList.item(j);
                    fstNm = element1.getChildNodes();
                    
                    for (int k = 0; k < fstNm.getLength(); k++)
                        sit.addPlace(fstNm.item(k).getTextContent());
                }
                
                for (int j = 0; j < keywordNodeList.getLength(); j++) {
                    element1 = (Element) keywordNodeList.item(j);
                    fstNm = element1.getChildNodes();
                    
                    for (int k = 0; k < fstNm.getLength(); k++)
                        sit.addKeyword(fstNm.item(k).getTextContent());
                }
                
                this.situations.add(sit);
            }
        }
    }

    public ArrayList<Situation> getSituations() { return situations; }
}
