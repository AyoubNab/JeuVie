package Monde; // Assuming Block.java is in package Monde

public class MurBlock extends Block {
    public MurBlock() {
        // Constructor for Block is: Block(boolean estTraversable, boolean estConstructible, boolean estDestructible)
        // Let's make MurBlock non-traversable, not constructible (it IS a construction),
        // and potentially destructible (or not, for simplicity now).
        super(false, false, true); // estTraversable=false, estConstructible=false, estDestructible=true (example)
    }

    @Override
    public String toString() {
        return "Mur"; // Simple representation
    }
}
