package InfoExtraction;

import QuestionParser.Lemmatizer;

public class Relation {
    private Object entity1;
    private Object entity2;
    private String bond;
    private String dualBond;

    public Relation(Object entity1, Object entity2, String bond, String dualBond) {
        this.entity1 = entity1;
        this.entity2 = entity2;
        this.bond = Lemmatizer.lemmatize(bond.toLowerCase());
        this.dualBond = Lemmatizer.lemmatize(dualBond.toLowerCase());
    }
    
    public Relation(Object entity1, Object entity2, String bond) {
        this.entity1 = entity1;
        this.entity2 = entity2;
        this.bond = Lemmatizer.lemmatize(bond.toLowerCase());
    }

    public String getBond() { return this.bond; }

    public String getDualBond() { return this.dualBond; }

    public Object getEntity1() { return this.entity1; }

    public Object getEntity2() { return this.entity2; }

    public void setDualBond(String dualBond) { this.dualBond = dualBond; }
}
