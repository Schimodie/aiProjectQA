package InfoExtraction;

public class Place {
    private String name;

    public Place(String name) { this.name = name.toLowerCase(); }

    public String getName() { return this.name; }

    @Override
    public String toString() { return this.name; }
}
