package Element.Aliment;
import Element.Element;

public abstract class Aliment extends Element {
    private int valeurNutritionelle = 10;
    boolean estConsomer = false;


    public Aliment(String nom, String description){
        super(nom, description);
    }

    public Aliment(String nom, String description, int valeurNutritionelle) {
        super(nom, description);
        this.valeurNutritionelle = valeurNutritionelle;
    }

    public int getValeurNutritionelle() { return valeurNutritionelle; }

    public boolean consomer(int quantite) {
        if (quantite > valeurNutritionelle) return false;
        valeurNutritionelle -= quantite;
        if (valeurNutritionelle == 0) {
            estConsomer = true;
        }
        return true;
    }

    public boolean estTotalementConsome() {
        return estConsomer;
    }
}
