package Entite.Heritage;

import Element.Element;
import Element.Aliment.Aliment; // Added import
import Entite.Entite;
import Monde.Monde;
import Monde.Block; // Added import

import java.util.ArrayList;
import java.util.List;

public class Humain extends Entite {

    List<Element> Inventaire;

    // Constructor updated to include id and type (hardcoded as "Humain")
    public Humain(String id, int faimTick, int soifTick, int positionX, int positionY, Monde monde) {
        super(id, "Humain", faimTick, soifTick, positionX, positionY, monde);
        this.Inventaire = new ArrayList<>();
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

        // If not critically hungry or didn't find/eat food, perform other actions
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
}
