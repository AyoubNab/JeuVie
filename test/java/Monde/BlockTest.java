package Monde;

import Entite.Entite;
import Entite.Heritage.Humain; // Using Humain as a concrete Entite
import Element.Element;
import Element.Aliment.Heritage.Baie; // Using Baie as a concrete Element

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

public class BlockTest {

    private Block block;
    private Entite testEntite;
    private Element testElement;
    private Monde mockMonde; // Required for Humain constructor

    @BeforeEach
    void setUp() {
        block = new Block();
        // Humain needs a Monde, faimTick, soifTick, posX, posY
        // Create a minimal Monde for this purpose.
        // Let's assume Monde has a constructor that takes size.
        // If Monde's constructor is more complex, this might need adjustment.
        // For now, we are testing Block, so a null Monde might be acceptable if Humain handles it.
        // However, Entite constructor accesses monde.getBlocks() so it cannot be null.
        mockMonde = new Monde(1); // Minimal monde of size 1x1
        testEntite = new Humain(1, 1, 0, 0, mockMonde); // Position 0,0 in a 1x1 world
        testElement = new Baie(); // Baie is an Element
    }

    @Test
    void constructor_initializesEntitesList() {
        assertNotNull(block.entites, "Entites list should be initialized by constructor.");
        assertTrue(block.entites.isEmpty(), "Entites list should be empty initially.");
    }

    @Test
    void constructor_initializesElementsList() {
        assertNotNull(block.getElements(), "Elements list should be initialized by constructor.");
        assertTrue(block.getElements().isEmpty(), "Elements list should be empty initially.");
    }

    @Test
    void ajouterEntite_addsEntiteToBlock() {
        block.ajouterEntite(testEntite);
        assertTrue(block.entites.contains(testEntite), "Block should contain the added entite.");
        assertEquals(1, block.entites.size(), "Entites list size should be 1 after adding one entite.");
    }

    @Test
    void enleverEntite_removesEntiteFromBlock() {
        block.ajouterEntite(testEntite);
        block.enleverEntite(testEntite);
        assertFalse(block.entites.contains(testEntite), "Block should not contain the removed entite.");
        assertTrue(block.entites.isEmpty(), "Entites list should be empty after removing the only entite.");
    }

    @Test
    void ajouterEntite_whenNotTraversable_doesNotAddEntite() {
        // This test requires a way to make a block not traversable and then test.
        // The current ajouterEntite checks "if(estTraverseable) entites.add(e);"
        // So we need a constructor or setter for estTraverseable.
        // Block blockNonTraversable = new Block(false, true, false);
        // Let's assume the default block IS traversable so the other tests pass.
        // If we want to test this, we need to modify Block or use a specific constructor.
        // For now, this aspect is implicitly tested by ajouterEntite successfully adding.
        // A dedicated test would be:
        Block blockNonTraversable = new Block(false, true, true); // Assuming this constructor sets estTraversable
        blockNonTraversable.ajouterEntite(testEntite);
        assertFalse(blockNonTraversable.entites.contains(testEntite), "Non-traversable block should not add entite.");
    }


    @Test
    void ajouterElement_addsElementToBlock() {
        block.ajouterElement(testElement);
        assertTrue(block.getElements().contains(testElement), "Block should contain the added element.");
        assertEquals(1, block.getElements().size(), "Elements list size should be 1 after adding one element.");
    }

    @Test
    void enleverElement_removesElementFromBlock() {
        block.ajouterElement(testElement);
        block.enleverElement(testElement);
        assertFalse(block.getElements().contains(testElement), "Block should not contain the removed element.");
        assertTrue(block.getElements().isEmpty(), "Elements list should be empty after removing the only element.");
    }

    @Test
    void enleverEntite_nonExistentEntite_doesNotThrowError() {
        Entite otherEntite = new Humain(1,1,0,0, mockMonde);
        assertDoesNotThrow(() -> block.enleverEntite(otherEntite), "Removing non-existent entite should not throw.");
    }

    @Test
    void enleverElement_nonExistentElement_doesNotThrowError() {
        Element otherElement = new Baie();
        assertDoesNotThrow(() -> block.enleverElement(otherElement), "Removing non-existent element should not throw.");
    }
}
