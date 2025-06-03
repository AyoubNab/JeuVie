package Element.Materiau; // New sub-package for materials

import Element.Element; // Element is in the parent package

public class Bois extends Element {
    // For now, one Bois object represents one unit of wood.
    // Quantity could be added later if needed, or managed by having multiple Bois objects.

    public Bois() {
        super("Bois", "Un morceau de bois, utile pour la construction ou comme combustible.");
    }

    @Override
    public String toString() {
        return super.getNom(); // "Bois"
    }
}
