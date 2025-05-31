package Element.Aliment;

import Element.Aliment.Heritage.Baie; // Using Baie as a concrete Aliment
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AlimentTest {

    private Aliment baie;
    private String nom = "Baie";
    private String description = "Une petite baie nutritive.";
    private int initialValeurNutritionelle = 5;

    @BeforeEach
    void setUp() {
        // Constructor: public Baie() { super("Baie", "Une petite baie nutritive.", 5); }
        baie = new Baie();
    }

    @Test
    void constructor_initializesPropertiesCorrectly() {
        assertEquals(nom, baie.getNom());
        assertEquals(description, baie.getDescription());
        assertEquals(initialValeurNutritionelle, baie.getValeurNutritionelle());
        assertFalse(baie.estTotalementConsome(), "New aliment should not be consumed.");
        assertFalse(baie.estConsomer, "estConsomer flag should be false initially.");
    }

    @Test
    void consomer_reducesValeurNutritionelle_returnsTrue() {
        int quantiteConsomee = 3;
        boolean result = baie.consomer(quantiteConsomee);

        assertTrue(result, "Consomer should return true for valid quantity.");
        assertEquals(initialValeurNutritionelle - quantiteConsomee, baie.getValeurNutritionelle(),
                "Valeur nutritionelle should decrease by consumed quantity.");
        assertFalse(baie.estTotalementConsome(), "Aliment should not be fully consumed yet.");
        assertFalse(baie.estConsomer, "estConsomer flag should still be false.");
    }

    @Test
    void consomer_exactValeurNutritionelle_becomesFullyConsumed() {
        int quantiteConsomee = initialValeurNutritionelle;
        boolean result = baie.consomer(quantiteConsomee);

        assertTrue(result, "Consomer should return true.");
        assertEquals(0, baie.getValeurNutritionelle(), "Valeur nutritionelle should be 0.");
        assertTrue(baie.estTotalementConsome(), "Aliment should be fully consumed.");
        assertTrue(baie.estConsomer, "estConsomer flag should be true.");
    }

    @Test
    void consomer_moreThanValeurNutritionelle_returnsFalse_notConsumed() {
        int quantiteConsomee = initialValeurNutritionelle + 1;
        boolean result = baie.consomer(quantiteConsomee);

        assertFalse(result, "Consomer should return false if quantity is too high.");
        assertEquals(initialValeurNutritionelle, baie.getValeurNutritionelle(),
                "Valeur nutritionelle should not change if consumption fails.");
        assertFalse(baie.estTotalementConsome(), "Aliment should not be consumed.");
        assertFalse(baie.estConsomer, "estConsomer flag should remain false.");
    }

    @Test
    void consomer_multipleTimes_correctlyUpdatesAndFlagsWhenFullyConsumed() {
        baie.consomer(2);
        assertEquals(initialValeurNutritionelle - 2, baie.getValeurNutritionelle());
        assertFalse(baie.estTotalementConsome());
        assertFalse(baie.estConsomer);

        baie.consomer(3); // Total consumed = 5
        assertEquals(0, baie.getValeurNutritionelle());
        assertTrue(baie.estTotalementConsome());
        assertTrue(baie.estConsomer);
    }

    @Test
    void consomer_alreadyFullyConsumed_returnsFalse() {
        baie.consomer(initialValeurNutritionelle); // Fully consume it
        assertTrue(baie.estTotalementConsome());
        assertTrue(baie.estConsomer);
        assertEquals(0, baie.getValeurNutritionelle());

        boolean result = baie.consomer(1); // Try to consume again
        assertFalse(result, "Should not be able to consume an already fully consumed aliment.");
        assertEquals(0, baie.getValeurNutritionelle(), "Valeur nutritionelle should remain 0.");
         assertTrue(baie.estTotalementConsome());
        assertTrue(baie.estConsomer);
    }


    @Test
    void estTotalementConsome_reflectsEstConsomerFlag() {
        assertFalse(baie.estTotalementConsome());
        baie.estConsomer = true; // Directly manipulate for testing the getter
        assertTrue(baie.estTotalementConsome());
    }
}
