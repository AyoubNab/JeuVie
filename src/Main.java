import Monde.Monde;
import Entite.Entite;
import Entite.Heritage.Humain;
import Entite.Heritage.Animaux;
import Monde.Block; // Added import for Monde.Block
import src.Metier; // Added import for Metier
import Element.Arbre;
import Element.Materiau.Bois;
import Monde.MurBlock; // Added import for MurBlock
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // 1. Initialize Monde
        Monde world = new Monde(20);

        // 2. Create entities
        // Humain constructor: Humain(String id, int faimTick, int soifTick, int positionX, int positionY, Monde monde)
        // Animaux constructor: Animaux(String id, String espece, int faimTick, int soifTick, int positionX, int positionY, Monde monde)

        Humain humain1 = new Humain("humain1", 5, 5, 5, 5, world);
        Humain humain2 = new Humain("humain2", 8, 8, 8, 8, world); // Will try to pick up food
        // Tick rates for faim/soif are the first two numeric params after id/espece
        Animaux animal1 = new Animaux("animal1", "Loup", 7, 7, 2, 2, world); // Example tick rates
        Animaux animal2 = new Animaux("animal2", "Cerf", 4, 4, 15, 15, world); // Example tick rates

        // Set initial stats using setters
        humain1.setSante(100);
        humain1.setEnergie(100); // Use setter for energie
        humain2.setSante(100);   // Added for humain2
        humain2.setEnergie(100); // Added for humain2


        // 3. Add entities to the Monde
        world.ajouterEntite(humain1);
        world.ajouterEntite(humain2);
        world.ajouterEntite(animal1);
        world.ajouterEntite(animal2);

        // Add food for testing Humain's new action logic (Keep humain1 for now)
        Element.Aliment.Heritage.Carotte carotteInventory = new Element.Aliment.Heritage.Carotte();
        humain1.ramasserObjet(carotteInventory); // Put a carrot in humain1's inventory
        humain1.setFaim(70); // Set faim high initially to prevent auto-eating during world.faireTour()

        // Initial setup for humain2, Baie will be placed later
        humain2.setFaim(70); // Set faim high initially

        System.out.println("Entities in the world: " + world.getAllEntities().size());
        System.out.println("Initial status humain1 (faim set to 70): " + humain1.toString() + " Inventory: " + humain1.getInventaire().size());
        System.out.println("Initial status humain2 (faim set to 70): " + humain2.toString() + " Inventory: " + humain2.getInventaire().size());
        System.out.println("Initial status animal1: " + animal1.toString());
        System.out.println("Initial status animal2: " + animal2.toString());

        // Set Metier for humain1 to MEDECIN for healing tests
        humain1.setMetier(Metier.MEDECIN);
        humain1.setEnergie(100); // Ensure medic has energy
        humain1.getInventaire().clear(); // Clear inventory for cleaner test
        System.out.println("Humain1 (" + humain1.getId() + ") setup as MEDECIN. Status: " + humain1.toString());

        // Use humain2 as the target
        Humain humainCible = humain2; // Re-using humain2 for clarity as target
        world.ajouterEntite(humainCible); // Ensure target is in the world (already added, but good practice)

        System.out.println("\n--- Testing soignerEntiteProche for humain1 (MEDECIN) ---");

        // Test 1: Successful healing
        humainCible.setSante(50); // Injure the target
        System.out.println("\nHumain1 (Médecin) énergie: " + humain1.getEnergie());
        System.out.println("HumainCible ("+humainCible.getId()+") santé initiale: " + humainCible.getSante());
        String resultatSoin = humain1.soignerEntiteProche(humainCible.getId());
        System.out.println(resultatSoin);
        System.out.println("HumainCible santé après soin: " + humainCible.getSante());
        System.out.println("Humain1 énergie après soin: " + humain1.getEnergie());

        // Test 2: Target already at full health
        humainCible.setSante(100); // Heal to full
        System.out.println("\nHumainCible ("+humainCible.getId()+") remis en pleine santé: " + humainCible.getSante());
        resultatSoin = humain1.soignerEntiteProche(humainCible.getId());
        System.out.println(resultatSoin); // Should indicate no healing needed
        System.out.println("Humain1 énergie (devrait être inchangée): " + humain1.getEnergie());


        // Test 3: Medic not having enough energy
        humainCible.setSante(50); // Re-injure target
        humain1.setEnergie(10); // Medic low energy
        System.out.println("\nHumain1 ("+humain1.getId()+") énergie basse: " + humain1.getEnergie());
        System.out.println("HumainCible ("+humainCible.getId()+") santé: " + humainCible.getSante());
        resultatSoin = humain1.soignerEntiteProche(humainCible.getId());
        System.out.println(resultatSoin); // Should indicate not enough energy
        System.out.println("HumainCible santé (devrait être inchangée): " + humainCible.getSante());
        System.out.println("Humain1 énergie (devrait être inchangée): " + humain1.getEnergie());

        // Test 4: Healing non-existent target
        humain1.setEnergie(100); // Replenish energy for medic
        System.out.println("\nHumain1 ("+humain1.getId()+") énergie replenish: " + humain1.getEnergie());
        resultatSoin = humain1.soignerEntiteProche("cibleNonExistante");
        System.out.println(resultatSoin); // Should indicate target not found
        System.out.println("Humain1 énergie (devrait être inchangée car target not found): " + humain1.getEnergie());

        // Test 5: Healing a non-vivant target
        humainCible.setSante(0); // Target is now not vivant (setSante should handle this)
        System.out.println("\nHumainCible ("+humainCible.getId()+") santé mise à 0. Est Vivant: " + humainCible.estVivant());
        resultatSoin = humain1.soignerEntiteProche(humainCible.getId());
        System.out.println(resultatSoin); // Should indicate target not vivant
        System.out.println("Humain1 énergie (devrait être inchangée): " + humain1.getEnergie());


        // Commenting out previous API tests and autonomous behavior tests
        /*
        ApiHandler apiHandler = new ApiHandler();
        // ... (previous API tests for construireMur, etc. commented out) ...
        // ... (previous autonomous behavior tests commented out) ...
        */

        // System.out.println("\n--- Other API Call Simulations Commented Out ---");
        /*
        // ApiHandler apiHandler = new ApiHandler();
        String humain2Id = humain2.getId();

        // Test Eat for humain1 (has Carotte in inventory)
        // humain1.setFaim(20); // Make hungry just before API call
        // System.out.println("\nHumain1 (" + humain1Id + ") faim set to 20. Current faim: " + humain1.getFaim() + ", Inventory: " + humain1.getInventaire());
        // String eatCarotteCommand = "**Action:** Eat\n**FoodItemName:** Carotte\n**Quantity:** 1";
        // String eatCarotteResponse = apiHandler.executeEntityCommand(humain1Id, eatCarotteCommand, world);
        // System.out.println("API - Eat Carotte response for " + humain1Id + ": " + eatCarotteResponse);
        // System.out.println("Humain1 status after eating Carotte: " + humain1.toString() + " Inventory: " + humain1.getInventaire());

        // Test Pickup for humain2 (Baie is on the block)
        // humain2.setFaim(20); // Make hungry just before API call, though not directly relevant for pickup
        // System.out.println("\nHumain2 (" + humain2Id + ") faim set to 20. Current faim: " + humain2.getFaim() + ", Inventory: " + humain2.getInventaire());

        // Place Baie on humain2's current block RIGHT BEFORE pickup attempt
        // Element.Aliment.Heritage.Baie baieToPickup = new Element.Aliment.Heritage.Baie();
        // if (humain2.getBlockActuelle() != null) {
        //     humain2.getBlockActuelle().ajouterElement(baieToPickup);
        //     System.out.println("Placed " + baieToPickup.getNom() + " on " + humain2Id + "'s current block (" + humain2.getPositionX() + "," + humain2.getPositionY() + ") for pickup test.");
        // } else {
        //     System.out.println("Cannot place " + baieToPickup.getNom() + " for " + humain2Id + " as its current block is null (cannot test pickup).");
        // }

        // Verify Baie is on block before pickup
        // if(humain2.getBlockActuelle()!=null) System.out.println("Elements on " + humain2Id + "'s block before pickup: " + humain2.getBlockActuelle().getElements());
        // String pickupBaieCommand = "**Action:** Pickup\n**ItemName:** Baie";
        // String pickupBaieResponse = apiHandler.executeEntityCommand(humain2Id, pickupBaieCommand, world);
        // System.out.println("API - Pickup Baie response for " + humain2Id + ": " + pickupBaieResponse);
        // System.out.println("Humain2 status after picking up Baie: " + humain2.toString() + " Inventory: " + humain2.getInventaire());
        // if(humain2.getBlockActuelle()!=null) System.out.println("Elements on " + humain2Id + "'s block after pickup: " + humain2.getBlockActuelle().getElements());

        // Test Eat for humain2 (should now have Baie in inventory)
        // String eatBaieCommand = "**Action:** Eat\n**FoodItemName:** Baie\n**Quantity:** 1";
        // String eatBaieResponse = apiHandler.executeEntityCommand(humain2Id, eatBaieCommand, world);
        // System.out.println("API - Eat Baie response for " + humain2Id + ": " + eatBaieResponse);
        // System.out.println("Humain2 status after eating Baie: " + humain2.toString() + " Inventory: " + humain2.getInventaire());

        // Test Eat for non-existent item
        // String eatNonExistentCommand = "**Action:** Eat\n**FoodItemName:** Steak\n**Quantity:** 1";
        // String eatNonExistentResponse = apiHandler.executeEntityCommand(humain1Id, eatNonExistentCommand, world);
        // System.out.println("API - Eat Steak (non-existent) response for " + humain1Id + ": " + eatNonExistentResponse);

        // Test Pickup for non-existent item
        // String pickupNonExistentCommand = "**Action:** Pickup\n**ItemName:** Diamond";
        // String pickupNonExistentResponse = apiHandler.executeEntityCommand(humain2Id, pickupNonExistentCommand, world);
        // System.out.println("API - Pickup Diamond (non-existent) response for " + humain2Id + ": " + pickupNonExistentResponse);
        */

        System.out.println("\n--- Simulation Complete ---");
    }
}
