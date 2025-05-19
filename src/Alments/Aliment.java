public abstract class Aliment {
    private int valeurNutritionelle = 10;

    public Aliment(){

    }

    public Aliment(int valeurNutritionelle) {
        this.valeurNutritionelle = valeurNutritionelle;
    }

    public int getValeurNutritionelle() { return valeurNutritionelle; }
}
