import Monde.Monde;
import Entite.Entite;
import Entite.Heritage.Humain;
import Entite.Heritage.Animaux;
import Monde.Block; // Added import for Monde.Block
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

        // Simulate a few turns to let hunger affect via action() if not eating by API
        System.out.println("\n--- Simulating a 1 world turn (entities might act on their own) ---");
        world.faireTour();
        System.out.println("Status humain1 after 1 turn: " + humain1.toString() + " Inv: " + humain1.getInventaire().size());
        System.out.println("Status humain2 after 1 turn: " + humain2.toString() + " Inv: " + humain2.getInventaire().size());
        System.out.println("--- World turn simulation complete ---");

        // 4. Instantiate ApiHandler
        ApiHandler apiHandler = new ApiHandler();

        // 5. TODO: Setup HTTP server (e.g., SparkJava) here
        // System.out.println("\n// TODO: Setup HTTP server (e.g., SparkJava) here - Conceptual routes would be setup here.");

        // 6. Add example calls to ApiHandler methods for new actions
        System.out.println("\n--- Simulating API Calls for Eat and Pickup ---");

        String humain1Id = humain1.getId();
        String humain2Id = humain2.getId();

        // Test Eat for humain1 (has Carotte in inventory)
        humain1.setFaim(20); // Make hungry just before API call
        System.out.println("\nHumain1 (" + humain1Id + ") faim set to 20. Current faim: " + humain1.getFaim() + ", Inventory: " + humain1.getInventaire());
        String eatCarotteCommand = "**Action:** Eat\n**FoodItemName:** Carotte\n**Quantity:** 1";
        String eatCarotteResponse = apiHandler.executeEntityCommand(humain1Id, eatCarotteCommand, world);
        System.out.println("API - Eat Carotte response for " + humain1Id + ": " + eatCarotteResponse);
        System.out.println("Humain1 status after eating Carotte: " + humain1.toString() + " Inventory: " + humain1.getInventaire());

        // Test Pickup for humain2 (Baie is on the block)
        humain2.setFaim(20); // Make hungry just before API call, though not directly relevant for pickup
        System.out.println("\nHumain2 (" + humain2Id + ") faim set to 20. Current faim: " + humain2.getFaim() + ", Inventory: " + humain2.getInventaire());

        // Place Baie on humain2's current block RIGHT BEFORE pickup attempt
        Element.Aliment.Heritage.Baie baieToPickup = new Element.Aliment.Heritage.Baie();
        if (humain2.getBlockActuelle() != null) {
            humain2.getBlockActuelle().ajouterElement(baieToPickup);
            System.out.println("Placed " + baieToPickup.getNom() + " on " + humain2Id + "'s current block (" + humain2.getPositionX() + "," + humain2.getPositionY() + ") for pickup test.");
        } else {
            System.out.println("Cannot place " + baieToPickup.getNom() + " for " + humain2Id + " as its current block is null (cannot test pickup).");
        }

        // Verify Baie is on block before pickup
        if(humain2.getBlockActuelle()!=null) System.out.println("Elements on " + humain2Id + "'s block before pickup: " + humain2.getBlockActuelle().getElements());
        String pickupBaieCommand = "**Action:** Pickup\n**ItemName:** Baie";
        String pickupBaieResponse = apiHandler.executeEntityCommand(humain2Id, pickupBaieCommand, world);
        System.out.println("API - Pickup Baie response for " + humain2Id + ": " + pickupBaieResponse);
        System.out.println("Humain2 status after picking up Baie: " + humain2.toString() + " Inventory: " + humain2.getInventaire());
        if(humain2.getBlockActuelle()!=null) System.out.println("Elements on " + humain2Id + "'s block after pickup: " + humain2.getBlockActuelle().getElements());

        // Test Eat for humain2 (should now have Baie in inventory)
        String eatBaieCommand = "**Action:** Eat\n**FoodItemName:** Baie\n**Quantity:** 1";
        String eatBaieResponse = apiHandler.executeEntityCommand(humain2Id, eatBaieCommand, world);
        System.out.println("API - Eat Baie response for " + humain2Id + ": " + eatBaieResponse);
        System.out.println("Humain2 status after eating Baie: " + humain2.toString() + " Inventory: " + humain2.getInventaire());

        // Test Eat for non-existent item
        String eatNonExistentCommand = "**Action:** Eat\n**FoodItemName:** Steak\n**Quantity:** 1";
        String eatNonExistentResponse = apiHandler.executeEntityCommand(humain1Id, eatNonExistentCommand, world);
        System.out.println("API - Eat Steak (non-existent) response for " + humain1Id + ": " + eatNonExistentResponse);

        // Test Pickup for non-existent item
        String pickupNonExistentCommand = "**Action:** Pickup\n**ItemName:** Diamond";
        String pickupNonExistentResponse = apiHandler.executeEntityCommand(humain2Id, pickupNonExistentCommand, world);
        System.out.println("API - Pickup Diamond (non-existent) response for " + humain2Id + ": " + pickupNonExistentResponse);


        System.out.println("\n--- Simulation Complete ---");
    }
}
