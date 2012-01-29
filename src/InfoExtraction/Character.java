package InfoExtraction;

public class Character {
    private boolean  main = false;
    private boolean secondary = false;
    private double importance;
    private String name;    

    public Character (
        String name,
        double importance,
        boolean isMain,
        boolean isSecondary
    ) {
        if(isMain)
            this.main = true;
        else if (isSecondary)
            this.secondary = true;
        
        this.importance = importance;
        this.name = name.toLowerCase();
    }

    public double getImportance() { return importance; }
    
    public String getName() { return this.name; }

    public boolean isMain() { return this.main; }

    public boolean isSecondary() { return this.secondary; }
    
    public boolean isEpisodic() { return !this.main && !this.secondary; }

    @Override
    public String toString() { return this.name; }
}
