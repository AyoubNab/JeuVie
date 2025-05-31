package Entite.Heritage;

import Entite.Entite;
import Monde.Monde;
import Monde.Block;
import Element.Element;
import Element.Aliment.Aliment;
import Element.Aliment.Heritage.Baie;
import Element.Aliment.Heritage.Viande;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class HumainTest {

    private Monde monde;
    private Humain humain;
    private Baie baie;
    private Viande viande;
    private Block currentBlock;

    @BeforeEach
    void setUp() {
        monde = new Monde(3); // 3x3 world, humain at (1,1)
        humain = new Humain(1, 1, 1, 1, monde);
        baie = new Baie(); // nom="Baie", desc="...", valeurNutritionelle=5
        viande = new Viande(); // nom="Viande", desc="...", valeurNutritionelle=25
        currentBlock = humain.getBlockActuelle();
    }

    @Test
    void constructor_initializesInventaire() {
        assertNotNull(humain.getInventaire(), "Inventaire should be initialized.");
        assertTrue(humain.getInventaire().isEmpty(), "Inventaire should be empty initially.");
    }

    @Test
    void ramasserObjet_addsElementToInventaire() {
        humain.ramasserObjet(baie);
        assertTrue(humain.getInventaire().contains(baie), "Inventaire should contain the picked up Baie.");
        assertEquals(1, humain.getInventaire().size());

        humain.ramasserObjet(viande);
        assertTrue(humain.getInventaire().contains(viande), "Inventaire should contain the picked up Viande.");
        assertEquals(2, humain.getInventaire().size());
    }

    @Test
    void ramasserObjet_nullElement_doesNotAddToInventaire_doesNotThrow() {
        assertDoesNotThrow(() -> humain.ramasserObjet(null));
        assertTrue(humain.getInventaire().isEmpty(), "Inventaire should remain empty.");
    }

    @Test
    void laisserObjet_removesElementFromInventaire() {
        humain.ramasserObjet(baie);
        humain.ramasserObjet(viande);

        humain.laisserObjet(baie);
        assertFalse(humain.getInventaire().contains(baie), "Inventaire should not contain the dropped Baie.");
        assertTrue(humain.getInventaire().contains(viande), "Inventaire should still contain Viande.");
        assertEquals(1, humain.getInventaire().size());
    }

    @Test
    void laisserObjet_nonExistentElement_doesNotChangeInventaire_doesNotThrow() {
        humain.ramasserObjet(viande);
        assertDoesNotThrow(() -> humain.laisserObjet(baie)); // Baie was never picked up
        assertEquals(1, humain.getInventaire().size());
        assertTrue(humain.getInventaire().contains(viande));
    }

    @Test
    void manger_alimentInInventaire_increasesFaim_consumesAliment() {
        humain.ramasserObjet(baie);
        int initialFaim = 50;
        // Need a way to set faim for Humain (or Entite)
        // Using reflection as in EntiteTest
        setPrivateField(humain, "faim", initialFaim);

        boolean result = humain.manger(baie, 3); // Consume 3 out of 5

        assertTrue(result, "Manger should return true.");
        // The faim increase logic in Entite.manger is "this.faim += quantite"
        // but 'quantite' parameter in Entite.manger is the 'valeurNutritionelle' of the Aliment,
        // not the 'quantite' passed to Humain.manger.
        // Humain.manger calls super.manger(aliment, quantite)
        // This seems like a slight confusion in parameter naming or intent.
        // Let's re-check Entite.manger(Aliment a, int quantite)
        // It uses 'quantite' for faim increase: this.faim += quantite.
        // And Aliment.consomer(quantite) uses 'quantite' as amount to consume.
        // Humain.manger(Aliment aliment, int quantite) calls aliment.consomer(quantite)
        // and then super.manger(aliment, quantite)
        // This means 'quantite' in Humain.manger is used for BOTH how much to eat from the item
        // AND how much to increase faim. This is usually what's intended.
        // Baie has 5. We eat 3. Faim increases by 3. Baie has 2 left.
        assertEquals(initialFaim + 3, humain.getFaim());
        assertEquals(5 - 3, baie.getValeurNutritionelle());
        assertFalse(baie.estTotalementConsome());
        assertTrue(humain.getInventaire().contains(baie), "Baie should still be in inventory.");
    }

    @Test
    void manger_alimentInInventaire_fullyConsumed_removedFromInventaire() {
        humain.ramasserObjet(baie);
        setPrivateField(humain, "faim", 50);

        boolean result = humain.manger(baie, 5); // Consume all 5

        assertTrue(result);
        assertEquals(50 + 5, humain.getFaim());
        assertEquals(0, baie.getValeurNutritionelle());
        assertTrue(baie.estTotalementConsome());
        assertFalse(humain.getInventaire().contains(baie), "Fully consumed Baie should be removed from inventory.");
    }

    @Test
    void manger_alimentNotInInventaire_returnsFalse() {
        setPrivateField(humain, "faim", 50);
        boolean result = humain.manger(baie, 3); // Baie is not in inventory

        assertFalse(result);
        assertEquals(50, humain.getFaim(), "Faim should not change.");
        assertEquals(5, baie.getValeurNutritionelle(), "Baie value should not change.");
    }

    @Test
    void manger_attemptToEatMoreThanAvailableInAliment_returnsFalse() {
        humain.ramasserObjet(baie); // Baie has 5
        setPrivateField(humain, "faim", 50);

        // Humain.manger calls aliment.consomer() first. If that fails, it returns false.
        boolean result = humain.manger(baie, 10); // Try to eat 10 from a Baie of 5

        assertFalse(result, "Manger should return false if trying to eat more than available.");
        assertEquals(50, humain.getFaim(), "Faim should not change.");
        assertEquals(5, baie.getValeurNutritionelle(), "Baie value should not change if consomer fails.");
        assertTrue(humain.getInventaire().contains(baie), "Baie should still be in inventory.");
    }


    @Test
    void ramasserAlimentSurBlock_alimentExistsOnBlock_pickedUp() {
        currentBlock.ajouterElement(baie); // Baie is on the current block
        assertTrue(currentBlock.getElements().contains(baie));

        boolean result = humain.ramasserAlimentSurBlock(baie);

        assertTrue(result, "Should return true when aliment is picked up.");
        assertTrue(humain.getInventaire().contains(baie), "Humain inventory should have the baie.");
        assertFalse(currentBlock.getElements().contains(baie), "Block should no longer have the baie.");
    }

    @Test
    void ramasserAlimentSurBlock_alimentNotOnBlock_returnsFalse() {
        // Baie is NOT on currentBlock
        assertFalse(currentBlock.getElements().contains(baie));

        boolean result = humain.ramasserAlimentSurBlock(baie);

        assertFalse(result, "Should return false as aliment is not on block.");
        assertFalse(humain.getInventaire().contains(baie), "Humain inventory should not have the baie.");
    }

    @Test
    void ramasserAlimentSurBlock_nullAliment_returnsFalse() {
        boolean result = humain.ramasserAlimentSurBlock(null);
        assertFalse(result, "Should return false if trying to pick up null.");
    }

    // Helper to set private fields in Entite (like faim) for testing setup
    private void setPrivateField(Entite instance, String fieldName, int value) {
        try {
            java.lang.reflect.Field field = Entite.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.setInt(instance, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("Failed to set private field '" + fieldName + "': " + e.getMessage());
        }
    }
}
