package InfoExtraction;

import java.io.File;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class InfoExtractionParser {
    private ArrayList<Character> characters;
    private ArrayList<Genre> genres;
    private ArrayList<Place> places;
    private ArrayList<Relation> relations;
    private ArrayList<Action> actions;
    private File file;
    private Document doc;

    public InfoExtractionParser(String path) {
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
    
    private <T> T find(ArrayList<T> collection, String name) {
        for (T item : collection)
            if (item.toString().toLowerCase().contains(name.toLowerCase()))
                return item;
        
        return null;
    }

    public ArrayList<Action> getActions() { return this.actions; }

    public ArrayList<Character> getCharacters() { return this.characters; }

    public ArrayList<Genre> getGenres() { return this.genres; }

    public ArrayList<Place> getPlaces() { return this.places; }

    public ArrayList<Relation> getRelations() { return this.relations; }
    
    public void parse() {
        try {
            Element el;
            NodeList _characters = doc.getElementsByTagName("character");
            NodeList _genres = doc.getElementsByTagName("genre");
            NodeList _places = doc.getElementsByTagName("location");
            NodeList _relations = doc.getElementsByTagName("relationship");
            NodeList _actions = doc.getElementsByTagName("action");
            Object entity1;
            Object entity2;
            
            this.actions =
                new ArrayList<Action>(_actions.getLength());
            this.characters = 
                new ArrayList<Character>(_characters.getLength());
            this.genres = 
                new ArrayList<Genre>(_genres.getLength());
            this.places = 
                new ArrayList<Place>(_places.getLength());
            this.relations =
                new ArrayList<Relation>(_relations.getLength());
            
            for (int i = 0; i < _actions.getLength(); ++i) {
                el = (Element) _actions.item(i);
                
                this.actions.add(new Action (
                    el.getAttribute("characters"),
                    el.getAttribute("locations"),
                    el.getAttribute("verbs"),
                    el.getTextContent()
                ));
            }
            
            for (int i = 0; i < _characters.getLength(); ++i) {
                el = (Element) _characters.item(i);
                
                this.characters.add(new Character (
                    el.getTextContent(),
                    Double.parseDouble(el.getAttribute("importance")),
                    "yes".equals(el.getAttribute("main"))
                        ? true
                        : false,
                    "yes".equals(el.getAttribute("secondary"))
                        ? true
                        : false
                ));
            }
            
            for (int i = 0; i < _genres.getLength(); ++i) {
                el = (Element) _genres.item(i);
                
                this.genres.add(new Genre (
                    el.getAttribute("name"),
                    Double.parseDouble(el.getAttribute("percentage"))
                ));
            }
            
            for (int i = 0; i < _places.getLength(); ++i)
                this.places.add(new Place (
                    _places.item(i).getTextContent()
                ));
            
            for (int i = 0; i < _relations.getLength(); ++i) {
                el = (Element) _relations.item(i);
                
                entity1 =
                    this.getCharFullName(el.getAttribute("entity1"))
                        .toLowerCase();
                entity2 =
                    this.getCharFullName(el.getAttribute("entity2"))
                        .toLowerCase();
                
                this.relations.add(new Relation (
                    entity1,
                    entity2,
                    el.getAttribute("link")
                ));
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    
    private String toName(String str) {
        String aux = "";
        String[] strs = str.split(" ");
        
        for (String string : strs) {
            aux += 
                string.substring(0, 1).toUpperCase() + 
                string.substring(1) + " ";
        }
        
        return aux.trim();
    }
    
    private String getCharFullName(String someName) {
        for (InfoExtraction.Character chr : this.characters) {
            if (this.toName(chr.getName()).contains(this.toName(someName))) {
                return chr.getName();
            }
        }
        
        return "";
    }
}
