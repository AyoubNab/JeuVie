package Entite.Heritage;

import Element.Element;
import Element.Aliment.Aliment;
import Entite.Entite;
import Monde.Monde;
import Monde.Block;
import Monde.MurBlock; // Added import for MurBlock
import src.Metier;
import Element.Arbre;
import Element.Materiau.Bois;

import java.util.ArrayList;
import java.util.List;

public class Humain extends Entite {

    List<Element> Inventaire;
    private Metier metier; // Added metier field

    public Humain(String id, int faimTick, int soifTick, int positionX, int positionY, Monde monde) {
        super(id, "Humain", faimTick, soifTick, positionX, positionY, monde);
        this.Inventaire = new ArrayList<>();
        this.metier = Metier.AUCUN; // Default profession
    }

    // Overloaded constructor to set metier
    public Humain(String id, int faimTick, int soifTick, int positionX, int positionY, Monde monde, Metier metier) {
        super(id, "Humain", faimTick, soifTick, positionX, positionY, monde);
        this.Inventaire = new ArrayList<>();
        this.metier = metier;
    }

    // Default constructor example (if needed, though ID should be unique)
    // public Humain(int faimTick, int soifTick, int positionX, int positionY, Monde monde) {
    //     super("humain_default_id", "Humain", faimTick, soifTick, positionX, positionY, monde);
    //     this.Inventaire = new ArrayList<>();
    // }

    @Override
    public void action() {
        String actionLog = "Humain " + getId() + " (Faim:" + getFaim() + ", Energie:" + getEnergie() + ")";

        if (getFaim() < 30) { // Hunger threshold
            // System.out.println("DEBUG: " + getId() + " is hungry.");
            String eatResult = chercherEtMangerNourriture();
            actionLog += "\n  " + eatResult;
            // If eating was successful (or an attempt was made that should be the whole turn's action)
            if (!eatResult.equals("Could not find anything to eat. Faim is " + getFaim() + ".")) {
                 // System.out.println(actionLog); // Uncomment for detailed turn log
                return;
            }
        } else {
            actionLog += " is not hungry enough to prioritize eating.";
        }

        // Add Bûcheron specific logic after hunger check
        if (this.metier == Metier.BUCHERON && getEnergie() > 20) { // Example energy threshold for work
            int currentWoodCount = 0;
            for (Element item : Inventaire) {
                if (item instanceof Bois) { // Use direct class name due to import
                    currentWoodCount++;
                }
            }
            boolean needsWood = currentWoodCount < 10; // Example: cut trees if less than 10 wood

            if (needsWood) {
                actionLog += "\n  (Bûcheron) Décide de chercher du bois.";
                String resultatCoupe = this.couperArbreLePlusProche();
                actionLog += "\n  " + resultatCoupe;
                if (resultatCoupe.contains("a coupé un Arbre et obtenu")) {
                    // System.out.println(actionLog); // Log the full action sequence
                    return; // Successfully cut a tree, turn's main action is done.
                }
            } else {
                actionLog += "\n  (Bûcheron) A assez de bois pour le moment (" + currentWoodCount + ").";
            }
        }

        // Add Ouvrier specific logic:
        if (this.metier == Metier.OUVRIER && getEnergie() > 30) { // Example energy threshold for building
            final int COUT_BOIS_MUR_ACTION = 2; // Match the cost in construireMur
            int currentWoodCountOuvrier = 0;
            for (Element item : Inventaire) {
                if (item instanceof Bois) {
                    currentWoodCountOuvrier++;
                }
            }

            if (currentWoodCountOuvrier >= COUT_BOIS_MUR_ACTION) {
                if (Math.random() < 0.3) { // 30% chance to try to build
                    actionLog += "\n  (Ouvrier) Décide d'essayer de construire un mur.";

                    String[] directions = {"nord", "sud", "est", "ouest"};
                    java.util.Random random = new java.util.Random();
                    String chosenDirection = directions[random.nextInt(directions.length)];

                    actionLog += "\n  (Ouvrier) Tente de construire en direction: " + chosenDirection + ".";
                    String resultatConstruction = this.construireMur(chosenDirection);
                    actionLog += "\n  " + resultatConstruction;

                    if (resultatConstruction.contains("a construit un Mur")) {
                        // System.out.println(actionLog);
                        return; // Successfully built, turn's main action is done.
                    }
                } else {
                    actionLog += "\n  (Ouvrier) A les matériaux pour construire mais choisit de ne pas le faire ce tour.";
                }
            } else {
                actionLog += "\n  (Ouvrier) Veut construire mais n'a pas assez de bois (" + currentWoodCountOuvrier + "/" + COUT_BOIS_MUR_ACTION + ").";
            }
        }


        // If not critically hungry/didn't eat/didn't cut tree/didn't build, perform other actions
        if (getEnergie() > 10) {
            if (Math.random() > 0.5) { // 50% chance to move
                int dx = (int) (Math.random() * 3) - 1;
                int dy = (int) (Math.random() * 3) - 1;
                if (dx != 0 || dy != 0) {
                    int oldX = getPositionX();
                    int oldY = getPositionY();
                    deplacer(dx, dy);
                    actionLog += "\n  Moved from (" + oldX + "," + oldY + ") to (" + getPositionX() + "," + getPositionY() + ").";
                } else {
                    actionLog += "\n  Decided to move but stayed in place.";
                }
            } else { // 50% chance to interact
                 String interactResult = interagir();
                 actionLog += "\n  " + interactResult;
            }
        } else {
            actionLog += "\n  Has low energy (" + getEnergie() + ") and rests.";
        }
        // System.out.println(actionLog); // Uncomment for detailed turn log
    }

    private String chercherEtMangerNourriture() {
        // 1. Try to eat from inventory
        Aliment alimentAConsommer = null;
        // Iterate over a copy of the inventory if removal happens during iteration,
        // though current Humain.manger handles removal from this.Inventaire.
        for (Element item : new ArrayList<>(Inventaire)) {
            if (item instanceof Aliment) {
                Aliment currentAliment = (Aliment) item;
                if (currentAliment.getValeurNutritionelle() > 0) {
                    alimentAConsommer = currentAliment;
                    break;
                }
            }
        }

        if (alimentAConsommer != null) {
            int faimManquante = 100 - getFaim();
            int quantiteAConsommer = Math.min(faimManquante, alimentAConsommer.getValeurNutritionelle());
            quantiteAConsommer = Math.max(1, quantiteAConsommer);

            if (manger(alimentAConsommer, quantiteAConsommer)) {
                return "Ate " + alimentAConsommer.getNom() + " from inventory. Faim is now " + getFaim() + ".";
            }
        }

        // 2. If no food in inventory, try to pick up and eat from current block
        Block currentBlock = getBlockActuelle();
        if (currentBlock != null) {
            Aliment alimentSurBlock = null;
            // Iterate over a copy to avoid ConcurrentModificationException if ramasserAlimentSurBlock modifies elements list
            for (Element element : new ArrayList<>(currentBlock.getElements())) {
                if (element instanceof Aliment) {
                    Aliment currentBlockAliment = (Aliment) element;
                    if (currentBlockAliment.getValeurNutritionelle() > 0) {
                        alimentSurBlock = currentBlockAliment;
                        break;
                    }
                }
            }

            if (alimentSurBlock != null) {
                if (ramasserAlimentSurBlock(alimentSurBlock)) { // Picks it up
                    // Now it's in inventory, try to eat it
                    int faimManquante = 100 - getFaim();
                    int quantiteAConsommer = Math.min(faimManquante, alimentSurBlock.getValeurNutritionelle());
                    quantiteAConsommer = Math.max(1, quantiteAConsommer);

                    if (manger(alimentSurBlock, quantiteAConsommer)) {
                        return "Found and ate " + alimentSurBlock.getNom() + " from the ground. Faim is now " + getFaim() + ".";
                    } else {
                        return "Picked up " + alimentSurBlock.getNom() + " but could not eat it. Faim is " + getFaim() + ".";
                    }
                } else {
                    return "Found " + alimentSurBlock.getNom() + " on the ground but failed to pick it up.";
                }
            }
        }
        return "Could not find anything to eat. Faim is " + getFaim() + ".";
    }

    // Method to pick up an object
    public void ramasserObjet(Element element) {
        if (element != null) {
            this.Inventaire.add(element);
        }
    }

    // Method to drop an object
    public void laisserObjet(Element element) {
        this.Inventaire.remove(element);
    }

    // Method to get the inventory
    public List<Element> getInventaire() {
        return this.Inventaire;
    }

    @Override
    public boolean manger(Aliment aliment, int quantite) {
        if (!Inventaire.contains(aliment)) {
            // System.out.println("DEBUG: " + getId() + " attempted to eat " + (aliment != null ? aliment.getNom() : "null") + " but not in inventory.");
            return false;
        }

        // Entite.manger (super.manger) calls aliment.consomer(quantite) and updates faim.
        boolean success = super.manger(aliment, quantite);

        if (success) {
            // System.out.println("DEBUG: " + getId() + " successfully called super.manger for " + aliment.getNom());
            if (aliment.estTotalementConsome()) {
                Inventaire.remove(aliment); // Remove from inventory if fully consumed
                // System.out.println("DEBUG: Removed consumed " + aliment.getNom() + " from inventory.");
            }
        } else {
            // System.out.println("DEBUG: " + getId() + " failed super.manger for " + aliment.getNom() + " (e.g. not enough nutritional value for quantity)");
        }
        return success;
    }

    public boolean ramasserAlimentSurBlock(Aliment aliment) {
        if (aliment == null) return false;
        Block currentBlock = this.getBlockActuelle();
        if (currentBlock != null && currentBlock.getElements().contains(aliment)) {
            // Ensure we remove the specific instance from the block
            boolean removed = currentBlock.getElements().remove(aliment); // Relies on Element.equals or instance equality
            if(removed){
                this.ramasserObjet(aliment);
                // System.out.println("DEBUG: Picked up " + aliment.getNom() + " from block " + currentBlock);
                return true;
            } else {
                // System.out.println("DEBUG: Failed to remove " + aliment.getNom() + " from block's element list, though it was reported as contained.");
                return false;
            }
        }
        // System.out.println("DEBUG: Failed to pick up " + aliment.getNom() + " from block " + currentBlock + " or aliment not found on block.");
        return false;
    }

    @Override
    public String interagir() {
        // Humain specific interaction
        return super.interagir() + " L'humain examine attentivement son environnement.";
    }

    public Metier getMetier() {
        return metier;
    }

    public void setMetier(Metier metier) {
        this.metier = metier;
        // System.out.println("DEBUG: Humain " + getId() + " metier set to: " + metier); // For debugging
    }

    @Override
    public String toString() {
        // Basic toString, can be expanded. Relies on Entite's getters.
        return String.format("Humain{id='%s', type='%s', x=%d, y=%d, sante=%d, faim=%d, energie=%d, metier=%s, inventaireSize=%d}",
                getId(), getType(), getPositionX(), getPositionY(), getSante(), getFaim(), getEnergie(), metier, (Inventaire != null ? Inventaire.size() : 0));
    }

    public String mangerItemEspecific(String itemName, int quantity) {
        if (itemName == null || itemName.isEmpty() || quantity <= 0) {
            return "Invalid item name or quantity for eating.";
        }

        Aliment alimentToEat = null;
        for (Element item : Inventaire) { // Inventaire is List<Element>
            if (item instanceof Aliment) {
                Aliment currentAliment = (Aliment) item;
                // Using toString() as a proxy for item name matching.
                // Assumes Aliment subclasses override toString() to return simple name (e.g., "Carotte").
                if (currentAliment.toString().toLowerCase().contains(itemName.toLowerCase())) {
                    alimentToEat = currentAliment;
                    break;
                }
            }
        }

        if (alimentToEat != null) {
            boolean eaten = manger(alimentToEat, quantity); // This calls the overridden manger in Humain
            if (eaten) {
                return "Successfully ate " + quantity + " of " + alimentToEat.getNom() + ". Faim: " + getFaim();
            } else {
                return "Failed to eat " + alimentToEat.getNom() + " (e.g., not enough, or not consumable).";
            }
        } else {
            return "Could not find " + itemName + " in inventory.";
        }
    }

    public String ramasserItemEspecific(String itemName) {
        if (itemName == null || itemName.isEmpty()) {
            return "Invalid item name for ramasser.";
        }

        Block currentBlock = getBlockActuelle();
        if (currentBlock == null) {
            return "Cannot ramasser item, current block is null.";
        }

        Element elementToRamasser = null;
        // Iterate over a copy to avoid ConcurrentModificationException if Block.enleverElement modifies the list
        for (Element element : new ArrayList<>(currentBlock.getElements())) {
            // Using toString() as a proxy for item name matching.
            if (element.toString().toLowerCase().contains(itemName.toLowerCase())) {
                elementToRamasser = element;
                break;
            }
        }

        if (elementToRamasser != null) {
            if (elementToRamasser instanceof Aliment) {
                boolean pickedUp = ramasserAlimentSurBlock((Aliment) elementToRamasser);
                if (pickedUp) {
                    return "Successfully picked up " + ((Aliment)elementToRamasser).getNom() + " from the ground.";
                } else {
                    return "Failed to pick up " + ((Aliment)elementToRamasser).getNom() + " (already picked up or error).";
                }
            } else {
                // Generic element pickup
                // Ensure this logic is safe: currentBlock.getElements().remove() then this.ramasserObjet()
                boolean removedFromBlock = currentBlock.getElements().remove(elementToRamasser);
                if (removedFromBlock) {
                    this.ramasserObjet(elementToRamasser); // add to inventory
                    return "Successfully picked up generic item " + elementToRamasser.getNom() + " from the ground.";
                } else {
                     return "Failed to pick up generic item " + elementToRamasser.getNom() + " from block.";
                }
            }
        } else {
            return "Could not find " + itemName + " on the current block.";
        }
    }

    public String couperArbreLePlusProche() {
        if (this.getMetier() != Metier.BUCHERON && this.getMetier() != Metier.AUCUN) {
             // For now, let's restrict it unless they are Bucheron or testing (AUCUN)
             // return getId() + " n'est pas Bûcheron et ne peut pas couper d'arbre.";
        }
        if (this.getEnergie() < 10) { // Energy cost for attempting to cut
            return getId() + " n'a pas assez d'énergie pour couper un arbre.";
        }
        this.setEnergie(this.getEnergie() - 10); // Consume energy

        Block blockActuel = getBlockActuelle();
        if (blockActuel == null) {
            return getId() + " n'est sur aucun block, ne peut pas chercher d'arbres.";
        }

        Arbre arbreACouper = null;
        // Search on current block first
        // Iterate over a copy in case etreCoupe() or other logic modifies the list indirectly
        for (Element el : new ArrayList<>(blockActuel.getElements())) {
            if (el instanceof Arbre) {
                Arbre tempArbre = (Arbre) el;
                if (!tempArbre.estCoupe()) {
                    arbreACouper = tempArbre;
                    break;
                }
            }
        }

        // TODO: Extend search to adjacent blocks if desired

        if (arbreACouper != null) {
            int boisObtenu = arbreACouper.etreCoupe(); // Marks tree as cut and returns wood
            if (boisObtenu > 0) {
                for (int i = 0; i < boisObtenu; i++) {
                    this.Inventaire.add(new Bois()); // Add 'boisObtenu' units of Bois to inventory
                }
                return getId() + " a coupé un " + arbreACouper.getNom() + " et obtenu " + boisObtenu + " bois. Inventaire: " + this.Inventaire.size();
            } else {
                return getId() + " a trouvé un " + arbreACouper.getNom() + ", mais il était déjà coupé ou ne donne pas de bois.";
            }
        } else {
            return getId() + " n'a trouvé aucun arbre à couper à proximité.";
        }
    }

    public String construireMur(String direction) {
        // Check metier - for now, let's assume OUVRIER or AUCUN (for testing) can attempt
        if (this.getMetier() != Metier.OUVRIER && this.getMetier() != Metier.AUCUN) {
            // return getId() + " n'est pas Ouvrier, ne peut pas construire.";
            // For now, allow for testing, profession check can be stricter later.
        }

        final int COUT_BOIS_MUR = 2; // Cost 2 wood to build a wall
        int boisEnInventaire = 0;
        for (Element item : Inventaire) {
            if (item instanceof Bois) {
                boisEnInventaire++;
            }
        }

        if (boisEnInventaire < COUT_BOIS_MUR) {
            return getId() + " n'a pas assez de bois pour construire un mur (besoin de " + COUT_BOIS_MUR + ", a " + boisEnInventaire + ").";
        }

        if (this.getEnergie() < 15) { // Energy cost for attempting to build
            return getId() + " n'a pas assez d'énergie pour construire un mur.";
        }
        // Deduct energy later if successful

        int dx = 0;
        int dy = 0;
        switch (direction.toLowerCase()) {
            case "nord": dy = -1; break;
            case "sud":  dy = 1;  break;
            case "est":  dx = 1;  break;
            case "ouest":dx = -1; break;
            default:
                return "Direction de construction invalide: " + direction + ". Utilisez Nord, Sud, Est, ou Ouest.";
        }

        int targetX = getPositionX() + dx;
        int targetY = getPositionY() + dy;
        Monde monde = getMonde();

        if (monde == null) return "Erreur: Le monde n'est pas accessible pour " + getId();

        // Boundary checks for target coordinates
        if (targetX < 0 || targetX >= monde.getTaille() || targetY < 0 || targetY >= monde.getTaille()) {
            return "Impossible de construire un mur hors des limites du monde à (" + targetX + "," + targetY + ").";
        }

        Block blockCible = monde.getBlocks().get(targetY).get(targetX);

        if (!blockCible.estTraverseable()) {
            return "Impossible de construire un mur à (" + targetX + "," + targetY + "), le block n'est pas traversable/libre.";
        }
        if (blockCible instanceof MurBlock) {
             return "Il y a déjà un mur à (" + targetX + "," + targetY + ").";
        }
        // Check if there are entities on the target block
        if (!blockCible.entites.isEmpty()) {
            return "Impossible de construire un mur à (" + targetX + "," + targetY + "), le block est occupé par une entité.";
        }


        // Consume wood
        int boisConsomme = 0;
        List<Element> itemsToRemove = new ArrayList<>();
        for (Element item : Inventaire) {
            if (item instanceof Bois) {
                itemsToRemove.add(item);
                boisConsomme++;
                if (boisConsomme == COUT_BOIS_MUR) break;
            }
        }
        Inventaire.removeAll(itemsToRemove);

        this.setEnergie(this.getEnergie() - 15); // Consume energy

        // Replace block
        MurBlock newMur = new MurBlock();
        monde.getBlocks().get(targetY).set(targetX, newMur);
        // The new MurBlock is automatically on the block list of Monde, no specific add to block needed
        // If Block needs to know its own coordinates, that would be a change to Block/MurBlock constructors

        return getId() + " a construit un Mur en (" + targetX + "," + targetY + "). Bois restant: " + (boisEnInventaire - COUT_BOIS_MUR);
    }

    public String soignerEntiteProche(String targetId) {
        // Check metier - for now, let's assume MEDECIN or AUCUN (for testing) can attempt
        if (this.getMetier() != Metier.MEDECIN && this.getMetier() != Metier.AUCUN) {
            // return getId() + " n'est pas Médecin, ne peut pas soigner.";
            // For now, allow for testing.
        }

        final int COUT_ENERGIE_SOIN = 20;
        final int POINTS_SOIN = 25;

        if (this.getEnergie() < COUT_ENERGIE_SOIN) {
            return getId() + " n'a pas assez d'énergie pour soigner (besoin de " + COUT_ENERGIE_SOIN + ", a " + getEnergie() + ").";
        }

        if (targetId == null || targetId.isEmpty()) {
            return "ID de la cible invalide pour le soin.";
        }

        Monde monde = getMonde();
        if (monde == null) {
            return "Erreur: Le monde n'est pas accessible pour " + getId();
        }

        Entite targetEntity = monde.getEntiteById(targetId);

        if (targetEntity == null) {
            return "Impossible de soigner: entité cible '" + targetId + "' non trouvée.";
        }

        if (!targetEntity.estVivant()) {
            return "Impossible de soigner: " + targetId + " n'est plus vivant(e).";
        }

        if (targetEntity.getSante() >= 100) {
            return targetId + " est déjà en pleine santé (" + targetEntity.getSante() + "). Soin non nécessaire.";
        }

        // All checks passed, perform healing
        this.setEnergie(this.getEnergie() - COUT_ENERGIE_SOIN);

        int oldSanteTarget = targetEntity.getSante();
        targetEntity.setSante(oldSanteTarget + POINTS_SOIN); // setSante in Entite handles clamping to 100

        return getId() + " a soigné " + targetId + ". Santé de " + targetId + " augmentée de " + (targetEntity.getSante() - oldSanteTarget) + " à " + targetEntity.getSante() + ". Energie restante pour " + getId() + ": " + getEnergie() + ".";
    }
}
