package Element.Aliment;
import Element.Element;

public abstract class Aliment extends Element {
    private int valeurNutritionelle = 10;
    boolean estConsomer = false;


    public Aliment(){
    }

    public Aliment(int valeurNutritionelle) {
        this.valeurNutritionelle = valeurNutritionelle;
    }

    public int getValeurNutritionelle() { return valeurNutritionelle; }

    public boolean consomer(int quantite) {
        if (quantite > valeurNutritionelle) return false;
        valeurNutritionelle -= quantite;
        return true;
    }
}
