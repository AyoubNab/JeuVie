package Entite;

import Entite.Heritage.Humain;
import Monde.Monde;
import Monde.Block;
import Element.Aliment.Heritage.Baie; // For manger test, though HumainTest will be more specific
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EntiteTest {

    private Monde monde;
    private Humain entite; // Using Humain as a concrete Entite
    private Humain cibleEntite;
    private final int initialFaimSoifEnergieSante = 100;
    private final int faimTick = 1;
    private final int soifTick = 1;
    private final int coutDeplacement = 1;
    private final int coutAttaque = 10;


    @BeforeEach
    void setUp() {
        // Create a 3x3 world. Borders are non-traversable. Playable area is 1x1 at (1,1)
        monde = new Monde(3);
        // Place entite at (1,1) which should be traversable
        entite = new Humain(faimTick, soifTick, 1, 1, monde);
        cibleEntite = new Humain(faimTick, soifTick, 1, 1, monde); // Another entity for attack tests
    }

    @Test
    void prochainTick_decreasesFaimSoifEnergie() {
        entite.prochainTick();
        assertEquals(initialFaimSoifEnergieSante - faimTick, entite.getFaim());
        assertEquals(initialFaimSoifEnergieSante - soifTick, entite.getSoif());
        assertEquals(initialFaimSoifEnergieSante - 1, entite.getEnergie()); // Passive energy drain is 1
        assertEquals(initialFaimSoifEnergieSante, entite.getSante()); // Sante should not change by default
        assertTrue(entite.estVivant());
    }

    private void setPrivateField(Entite instance, String fieldName, int value) {
        try {
            java.lang.reflect.Field field = Entite.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.setInt(instance, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("Failed to set private field '" + fieldName + "': " + e.getMessage());
        }
    }

    @Test
    void prochainTick_vivantBecomesFalse_whenFaimIsZero() {
        setPrivateField(entite, "faim", 1); // About to become 0
        entite.prochainTick(); // faim becomes 0
        assertFalse(entite.estVivant());
    }

    @Test
    void prochainTick_vivantBecomesFalse_whenSoifIsZero() {
        setPrivateField(entite, "soif", 1); // About to become 0
        entite.prochainTick(); // soif becomes 0
        assertFalse(entite.estVivant());
    }

    @Test
    void prochainTick_vivantBecomesFalse_whenEnergieIsZero() {
        setPrivateField(entite, "energie", 1); // About to become 0
        entite.prochainTick(); // energie becomes 0
        assertFalse(entite.estVivant());
    }

    @Test
    void prochainTick_vivantBecomesFalse_whenSanteIsZero() {
        setPrivateField(entite, "sante", 1); // Sante is 1
        entite.prochainTick(); // Sante remains 1, vivant is true
        assertTrue(entite.estVivant());
        setPrivateField(entite, "sante", 0); // Sante becomes 0
        entite.prochainTick(); // Now vivant should be false
        assertFalse(entite.estVivant());
    }

    // Helper to get Entite's positionX (assuming no public getter for direct position)
    // This requires reflection or a getter in Entite.java. For now, let's assume we test via block.
    // For proper testing of deplacer, Entite needs getPositionX/Y or test via getBlockActuelle changes.
    // Let's add getPositionX/Y to Entite for testability. (Will require modifying Entite.java later)
    // For now, I'll test by checking the block the entity is on.

    @Test
    void deplacerHaut_changesPosition_decreasesEnergie_updatesBlock() {
        Block initialBlock = entite.getBlockActuelle();
        assertNotNull(initialBlock, "Initial block should not be null.");
        assertTrue(initialBlock.entites.contains(entite), "Initial block should contain entity.");

        entite.deplacerHaut(); // Move from (1,1) to (1,0) - this is a border in 3x3!
                               // Monde(3) creates (0,0) (0,1) (0,2) | (1,0) (1,1) (1,2) | (2,0) (2,1) (2,2)
                               // Borders are non-traversable. (1,0) is a border.
                               // So deplacerHaut from (1,1) should fail.

        // Let's adjust the setup for movement tests: Monde(5), entite at (2,2)
        // So (2,1) is traversable.
        monde = new Monde(5);
        entite = new Humain(faimTick, soifTick, 2, 2, monde);
        initialBlock = entite.getBlockActuelle(); // block at (2,2)

        int expectedEnergie = entite.getEnergie() - coutDeplacement;
        entite.deplacerHaut(); // Move from (2,2) to (2,1)

        Block newBlock = entite.getBlockActuelle();
        assertNotNull(newBlock, "New block should not be null.");
        assertNotEquals(initialBlock, newBlock, "Entity should be on a new block.");
        //assertEquals(2, entite.getPositionX()); // Requires getter
        //assertEquals(1, entite.getPositionY()); // Requires getter
        assertEquals(expectedEnergie, entite.getEnergie());
        assertFalse(initialBlock.entites.contains(entite), "Initial block should not contain entity after move.");
        assertTrue(newBlock.entites.contains(entite), "New block should contain entity.");
    }

    // Similar tests for deplacerBas, deplacerGauche, deplacerDroite needed.
    // Test movement to borders and out of bounds.

    @Test
    void deplacer_cannotMoveToNonTraversableBorder() {
        // Entite is at (1,1) in a 3x3 Monde. (1,0) is a border block (non-traversable).
        Block initialBlock = entite.getBlockActuelle();
        int initialEnergie = entite.getEnergie();

        entite.deplacerHaut(); // Attempt to move from (1,1) to (1,0)

        assertEquals(initialBlock, entite.getBlockActuelle(), "Entity should remain on the same block.");
        assertEquals(initialEnergie, entite.getEnergie(), "Energie should not change if movement failed.");
        assertTrue(initialBlock.entites.contains(entite), "Entity should still be in the initial block.");
    }

    @Test
    void attaquer_reducesTargetSante_reducesAttackerEnergie() {
        int initialTargetSante = cibleEntite.getSante();
        int initialAttackerEnergie = entite.getEnergie();
        int degats = 10;

        entite.attaquer(cibleEntite, degats);

        assertEquals(initialTargetSante - degats, cibleEntite.getSante());
        assertEquals(initialAttackerEnergie - coutAttaque, entite.getEnergie());
    }

    @Test
    void attaquer_notEnoughEnergie_attackFails() {
        setPrivateField(entite, "energie", coutAttaque - 1); // Not enough energy
        int initialTargetSante = cibleEntite.getSante();
        int initialAttackerEnergie = entite.getEnergie();

        entite.attaquer(cibleEntite, 10);

        assertEquals(initialTargetSante, cibleEntite.getSante(), "Target sante should not change.");
        assertEquals(initialAttackerEnergie, entite.getEnergie(), "Attacker energie should not change.");
    }

    @Test
    void attaquer_targetSanteToZero_targetBecomesNotVivantAfterTick() {
        int degatsToKill = cibleEntite.getSante();
        entite.attaquer(cibleEntite, degatsToKill);

        assertEquals(0, cibleEntite.getSante());
        assertTrue(cibleEntite.estVivant(), "Target should be vivant until its next tick.");
        cibleEntite.prochainTick();
        assertFalse(cibleEntite.estVivant(), "Target should not be vivant after tick if sante is 0.");
    }

    @Test
    void attaquer_attackerNotVivant_attackFails() {
        setPrivateField(entite, "vivant", false);
        int initialTargetSante = cibleEntite.getSante();
        int initialAttackerEnergie = entite.getEnergie();

        entite.attaquer(cibleEntite, 10);

        assertEquals(initialTargetSante, cibleEntite.getSante());
        assertEquals(initialAttackerEnergie, entite.getEnergie()); // Energie is not spent
    }

    @Test
    void attaquer_targetNotVivant_attackFails() {
        setPrivateField(cibleEntite, "vivant", false);
        int initialTargetSante = cibleEntite.getSante(); // Will be whatever health it had when it died
        int initialAttackerEnergie = entite.getEnergie();

        entite.attaquer(cibleEntite, 10);

        assertEquals(initialTargetSante, cibleEntite.getSante());
        // Attacker might still spend energy if the check for target.vivant is after energy cost.
        // Current Entite.attaquer: if (!this.vivant || !cible.vivant) return;
        // So energy should not be spent.
        assertEquals(initialAttackerEnergie, entite.getEnergie());
    }

    // estVivant() method test
    @Test
    void estVivant_returnsCorrectStatus() {
        assertTrue(entite.estVivant());
        setPrivateField(entite, "vivant", false);
        assertFalse(entite.estVivant());
    }

    // Need to add getPositionX() and getPositionY() to Entite for better movement testing.
    // For now, this provides a good base.
}
