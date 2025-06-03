package Element.Aliment.Heritage;

import Element.Aliment.Aliment;

public class Baie extends Aliment {
    public Baie() {
        super("Baie", "Une petite baie nutritive.", 5);
    }

    @Override
    public String toString() {
        return "Baie"; // Simple name for identification
    }
}
