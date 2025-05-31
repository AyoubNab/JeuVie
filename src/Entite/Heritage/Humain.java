package Entite.Heritage;

import Element.Element;
import Entite.Entite;
import Monde.Monde;

import java.util.ArrayList; // Added import
import java.util.List;

public class Humain extends Entite {

    List<Element> Inventaire;


    public Humain(int faimTick, int soifTick, int positionX, int positionY, Monde monde) {
        super(faimTick, soifTick, positionX, positionY, monde);
        this.Inventaire = new ArrayList<>(); // Initialized Inventaire
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
    public boolean manger(Element.Aliment.Aliment aliment, int quantite) {
        if (Inventaire.contains(aliment)) {
            boolean consomme = aliment.consomer(quantite);
            if (consomme) {
                // Assuming faim is a protected or package-private field in Entite, or there's a setter.
                // For now, let's assume direct access for simplicity if it's package-private or protected.
                // If faim is private with no setter, this part needs Entite class modification.
                // Let's check Entite.java: faim is private, but it's accessed by a getter getFaim().
                // We need a way to change faim. For now, I'll call the super method if the item is in inventory,
                // and then handle the inventory removal.
                // This is a bit of a workaround. Ideally, Entite.faim should be protected or have a setter.
                // However, the original manger method in Entite already handles the faim increase.

                super.manger(aliment, quantite); // This will handle faim increase and max faim check.

                if (aliment.estTotalementConsome()) {
                    this.Inventaire.remove(aliment);
                }
                return true;
            }
            return false; // Consuming failed (e.g., not enough valeurNutritionelle)
        }
        return false; // Aliment not in inventory
    }

    public boolean ramasserAlimentSurBlock(Element.Aliment.Aliment aliment) {
        if (aliment == null) return false;

        Monde.Block currentBlock = this.getBlockActuelle(); // Method from Entite.java
        if (currentBlock != null && currentBlock.getElements().contains(aliment)) {
            currentBlock.enleverElement(aliment); // Remove from block's list
            this.ramasserObjet(aliment); // Add to inventory (method in Humain.java)
            return true;
        }
        return false; // Aliment not found on current block or block is null
    }
}
