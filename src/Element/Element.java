package Element;

public abstract class Element {
    protected String nom;
    protected String description;

    public Element(String nom, String description) {
        this.nom = nom;
        this.description = description;
    }

    public String getNom() {
        return nom;
    }

    public String getDescription() {
        return description;
    }

    // It's common for abstract classes to have abstract methods
    // that subclasses must implement, but for now, we'll keep it simple.
    // For example: public abstract void utiliser();
}
