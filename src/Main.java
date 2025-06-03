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
        humain1.setFaim(20);

        // Commenting out humain2's specific food-on-block setup to avoid Monde.Block error
        // Element.Aliment.Heritage.Baie baieOnBlock = new Element.Aliment.Heritage.Baie();
        // Monde.Block blockHumain2 = humain2.getBlockActuelle(); // This line causes compilation error
        // if (blockHumain2 != null) {
        //     blockHumain2.ajouterElement(baieOnBlock);
        //     System.out.println("Added " + baieOnBlock.getNom() + " to block: " + blockHumain2.toString() + " at (" + humain2.getPositionX() + "," + humain2.getPositionY() + ")");
        // } else {
        //     System.out.println("Could not place Baie for humain2 as its current block is null.");
        // }
        humain2.setFaim(20); // Still make humain2 hungry, it just won't find food on block

        System.out.println("Entities in the world: " + world.getAllEntities().size());
        System.out.println("Initial status humain1 (faim set to 20): " + humain1.toString() + " Inventory: " + humain1.getInventaire().size());
        // System.out.println("Initial status humain2 (faim set to 20): " + humain2.toString() + " (Block elements: " + (blockHumain2 != null ? blockHumain2.getElements().size() : "N/A") + ")");
        System.out.println("Initial status humain2 (faim set to 20): " + humain2.toString());
        System.out.println("Initial status animal1: " + animal1.toString());
        System.out.println("Initial status animal2: " + animal2.toString() + " (This is the one we'll focus on for movement test)");

        // Simulate a few turns for the world (entities perform actions)
        System.out.println("\n--- Simulating a few world turns (focus on animal2 movement) ---");
        int turnsForAnimalTest = 5;
        for (int i = 0; i < turnsForAnimalTest; i++) {
            System.out.println("--- World Turn " + (i + 1) + " ---");
            // Print animal2's position BEFORE action
            System.out.println("  animal2 Pre-Action: " + animal2.toString());
            world.faireTour(); // This will call action() for all entities including animal2
            // Print animal2's position AFTER action
            System.out.println("  animal2 Post-Action: " + animal2.toString());

            // Keep other printouts for context if needed, or comment them out for cleaner log
            Entite h1 = world.getEntiteById("humain1");
            if (h1 != null) {
                 System.out.println("  Status humain1: " + h1.toString() + " Inv: " + ((Humain)h1).getInventaire().size());
            }
        }
        System.out.println("--- World turns simulation complete ---");

        // 4. Instantiate ApiHandler (Comment out API calls for now to simplify)
        // ApiHandler apiHandler = new ApiHandler();

        // 5. TODO: Setup HTTP server (e.g., SparkJava) here
        // System.out.println("\n// TODO: Setup HTTP server (e.g., SparkJava) here - Conceptual routes would be setup here.");

        // 6. Add example calls to ApiHandler methods
        System.out.println("\n--- API Call Simulations Commented Out for This Test ---");
        /*
        String entity1Id = humain1.getId();
        String statusJson = apiHandler.getEntityStatus(entity1Id, world);
        System.out.println("API - Status for " + entity1Id + ": " + statusJson);

        String entity2Id = animal1.getId();
        String statusJson2 = apiHandler.getEntityStatus(entity2Id, world);
        System.out.println("API - Status for " + entity2Id + ": " + statusJson2);

        // ... (rest of API calls commented out) ...
        */

        System.out.println("\n--- Simulation Complete ---");
    }
}
