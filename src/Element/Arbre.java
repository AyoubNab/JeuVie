package Element; // Assuming Element.java is directly in package Element

public class Arbre extends Element {
    private int boisDonne; // Amount of wood this tree gives
    private boolean estCoupe;
    private static final int DEFAULT_BOIS = 5; // Default wood if not specified

    public Arbre(String nom, String description, int boisDonne) {
        super(nom, description); // Element constructor is Element(String nom, String description)
        this.boisDonne = boisDonne;
        this.estCoupe = false;
    }

    // Overloaded constructor with default wood amount
    public Arbre() {
        super("Arbre", "Un arbre qui peut être coupé pour obtenir du bois.");
        this.boisDonne = DEFAULT_BOIS;
        this.estCoupe = false;
    }

    public boolean estCoupe() {
        return estCoupe;
    }

    public int etreCoupe() {
        if (!estCoupe) {
            this.estCoupe = true;
            // System.out.println(this.nom + " a été coupé, donne " + this.boisDonne + " bois.");
            // After being cut, the Arbre object still exists on the block but is marked as 'estCoupe'.
            // It won't give more wood. It could be removed from the block by another mechanism if desired.
            return this.boisDonne;
        }
        // System.out.println(this.nom + " est déjà coupé.");
        return 0; // Returns 0 if already cut
    }

    public int getBoisDonne() {
        return boisDonne;
    }

    @Override
    public String toString() {
        return super.getNom() + (estCoupe ? " (coupé)" : ""); // e.g., "Arbre (coupé)"
    }
}
