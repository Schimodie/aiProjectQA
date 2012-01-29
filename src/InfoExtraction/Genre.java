package InfoExtraction;

public class Genre {
    private double certainty;
    private String name;

    public Genre(String name, double certainty) {
        this.name = name.toLowerCase();
        this.certainty = certainty;
    }

    public double getCertainty() { return this.certainty; }

    public String getName() { return this.name; }

    @Override
    public String toString() { return this.name; }
}
