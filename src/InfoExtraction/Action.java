package InfoExtraction;

import QuestionParser.Lemmatizer;

public class Action {
    private String text;
    private String[] characters;
    private String[] places;
    private String[] verbs;
    
    public Action(String chars, String places, String verbs, String text) {
        text = text.toLowerCase();
        text = Lemmatizer.lemmatize(text);
        
        String[] trimedParts = text.split("\n");
        
        this.characters = chars.split(", ");
        this.places = places.split(", ");
        this.verbs = verbs.split(", ");
        
        for (int i = 0; i < this.characters.length; ++i)
            this.characters[i] = this.characters[i].toLowerCase();
        
        for (int i = 0; i < this.places.length; ++i)
            this.places[i] = this.places[i].toLowerCase();
        
        for (int i = 0; i < this.verbs.length; ++i)
            this.verbs[i] = this.verbs[i].toLowerCase();
        
        for (int i = 0; i < trimedParts.length; ++i)
            trimedParts[i] = trimedParts[i].trim();
        
        this.text = this.join("", trimedParts);
        this.text = text
            .replace("’", " ")
            .replace("‘", " ")
            .replace("'", "")
            .replace("\"", "")
            .replace("\r", "")
            .trim();
    }

    public String[] getCharacters() { return this.characters; }

    public String[] getPlaces() { return this.places; }

    public String[] getVerbs() { return this.verbs; }

    public String getText() { return this.text; }
    
    private String join(String glue, String... strs) {
        int len = strs.length;
        StringBuilder strb = new StringBuilder();
        
        if (len == 0)
            return null;
        
        strb.append(strs[0]);
        
        for (int x = 1; x < len; ++x)
            strb.append(glue).append(strs[x]);
        
        return strb.toString();
    }
}
