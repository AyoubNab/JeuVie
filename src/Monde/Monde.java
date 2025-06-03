package Monde;

import Entite.Entite; // Added import for Entite
import java.util.ArrayList;
import java.util.HashMap; // Added import for HashMap
import java.util.List; // Added import for List
import java.util.Map; // Added import for Map
import java.util.Collections; // Added import for Collections

public class Monde {
    private int taille;
    public ArrayList<ArrayList<Block>> blocks;
    private Map<String, Entite> entitesMap; // To store entities by their ID

    public Monde(int taille) {
        this.taille = taille;
        this.entitesMap = new HashMap<>();
        this.blocks = new ArrayList<>();

        //initialisation des block
        for(int i = 0; i < taille; i++){
            ArrayList<Block> ligneBlocks = new ArrayList<>();
            for(int j = 0; j < taille; j++){
                //si c'est une partie du bord
                if((j == 0 || j == taille - 1) || (i == 0 || i == taille - 1)) {
                    ligneBlocks.add(new Block(false,false,false));
                }
                else ligneBlocks.add(new Block());
            }
            this.blocks.add(ligneBlocks); // Add the row of blocks to the main list
        }
        System.out.println("Monde créé avec taille " + taille + "x" + taille);
    }

    public int getTaille() {
        return taille;
    }

    public ArrayList<ArrayList<Block>> getBlocks() {
        return blocks;
    }

    public void ajouterEntite(Entite entite) {
        if (entite != null && !entitesMap.containsKey(entite.getId())) {
            // Assuming Entite has getPositionX() and getPositionY()
            if (entite.getPositionX() >= 0 && entite.getPositionX() < taille &&
                entite.getPositionY() >= 0 && entite.getPositionY() < taille) {
                entitesMap.put(entite.getId(), entite);
                // Add entity to the block it's on
                Block block = blocks.get(entite.getPositionY()).get(entite.getPositionX());
                if (block != null) {
                    block.ajouterEntite(entite);
                }
                System.out.println("Entite " + entite.getId() + " ajoutée au monde en (" + entite.getPositionX() + "," + entite.getPositionY() + ")");
            } else {
                System.out.println("Erreur: Tentative d'ajout de " + entite.getId() + " en dehors des limites du monde.");
            }
        } else if (entite == null) {
            System.out.println("Erreur: Tentative d'ajout d'une entité null.");
        } else {
            System.out.println("Erreur: Une entité avec l'ID " + entite.getId() + " existe déjà.");
        }
    }

    public Entite getEntiteById(String entityId) {
        return entitesMap.get(entityId);
    }

    public List<Entite> getAllEntities() {
        return new ArrayList<>(entitesMap.values());
    }

    public void faireTour() {
        System.out.println("\n--- Début du tour pour le Monde ---");
        List<Entite> entitesSnapshot = new ArrayList<>(entitesMap.values());
        Collections.shuffle(entitesSnapshot); // Randomize order of action

        for (Entite entite : entitesSnapshot) {
             entite.prochainTick(); // Process passive effects
             if (entite.estVivant()) { // Use estVivant() for clarity and consistency
                System.out.println("Monde.faireTour(): Calling action for " + entite.getId() + " (" + entite.getType() + ")");
                entite.action();
            } else {
                System.out.println("Monde.faireTour(): Entity " + entite.getId() + " is not vivant, skipping action.");
            }
        }
        System.out.println("--- Fin du tour pour le Monde ---\n");
    }
}
