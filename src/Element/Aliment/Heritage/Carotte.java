package Element.Aliment.Heritage;

import Element.Aliment.Aliment;

public class Carotte extends Aliment {
    public Carotte() {
        super("Carotte", "Une carotte fraiche", 4);
    }

    @Override
    public String toString() {
        return "Carotte"; // Simple name for identification
    }
}
