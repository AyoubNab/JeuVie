package Entite;

import Element.Aliment.Aliment;
import Monde.*;

public abstract class Entite {
    protected String id; // Added ID
    protected String type; // Added type

    private int positionX;
    private int positionY;
    private Monde monde;
    private Block blockActuelle;

    //ticks
    private int faimTick;
    private int soifTick;
    private int coutDeplacement = 1;
    private int coutAttaque = 10; // Added coutAttaque

    private int age = 0;
    private int soif = 100;
    private int faim = 100;
    private int energie = 100;
    private int sante = 100; // Added sante
    private boolean vivant = true;

    public int getPositionX() { return positionX; }
    public int getPositionY() { return positionY; }

    public String getId() { return id; } // Added getter for ID
    public String getType() { return type; } // Added getter for type

    public int getAge() {return age;}
    public int getSoif() {return soif;}
    public int getFaim() {return faim;}
    public void setFaim(int faim) { // Added setter for faim
        this.faim = Math.max(0, Math.min(100, faim)); // Clamp faim between 0 and 100
    }
    public int getEnergie() {return energie;}
    public void setEnergie(int energie) { // Added setter for energie
        this.energie = Math.max(0, Math.min(100, energie)); // Clamp energy between 0 and 100
    }
    public int getSante() {return sante;}
    public void setSante(int sante) { // Added setter for sante
        this.sante = Math.max(0, Math.min(100, sante)); // Clamp sante between 0 and 100
        if (this.sante <= 0 && this.vivant) {
            this.vivant = false;
            // System.out.println(this.id + " n'est plus vivant (santé à 0).");
        }
    }

    public boolean estVivant() { return this.vivant; } // Added getter for vivant status

    public Block getBlockActuelle() { return blockActuelle; } // Reverted to rely on import Monde.*

    public abstract void action(); // Added abstract action method

    public void prochainTick(){
        if (!vivant ) return;
        //chaque tick il gagne de l'energie, et il perd soif et faim
        energie -= 1; // Passive energy drain
        if (energie < 0) vivant = false;

        soif -= soifTick;
        if (soif < 0) vivant = false;

        faim -= faimTick;
        if (faim < 0) vivant = false;

        if (sante <= 0) vivant = false; // Added sante check
    }

    public void attaquer(Entite cible, int degats) {
        if (!this.vivant || !cible.vivant) return; // Cannot attack or be attacked if not vivant

        if (this.energie >= this.coutAttaque) {
            this.energie -= this.coutAttaque;
            cible.sante -= degats;
            // No need to clamp cible.sante to 0 here,
            // as prochainTick() will set vivant = false if sante <= 0.
            // However, if direct feedback is needed (e.g. "target defeated"), this could be expanded.
        }
        // Else, not enough energy to attack - could add feedback here too if desired.
    }

    public boolean manger(Aliment a, int quantite){
        boolean consomme = a.consomer(quantite);
        if (consomme) {
            // faim is private, so we need a way to modify it.
            // Let's assume there's a method to add faim or it's handled by subclasses.
            // The current Entite.manger() directly modifies faim.
            this.faim += quantite; // This line was already here and is fine.
            if (this.faim > 100) this.faim = 100;
        }
        return consomme;
    }

    // This is a new method, we need to make sure it's placed correctly.
    // It should be outside of other methods.
    // The previous SEARCH block was for prochainTick, this one is for manger.
    // This should be fine.

    public Entite(String id, String type, int faimTick, int soifTick, int positionX, int positionY, Monde monde) {
        this.id = id;
        this.type = type;
        this.faimTick = faimTick;
        this.soifTick = soifTick;
        this.monde = monde;

        int tailleMonde = monde.getTaille();

        int finalPosX = positionX;
        int finalPosY = positionY;

        if (tailleMonde <= 0) {
            this.positionX = 0;
            this.positionY = 0;
            this.blockActuelle = null; 
            return; 
        }

        if (tailleMonde == 1) {
            finalPosX = 0; 
            finalPosY = 0;
        } else {
            if((finalPosX == 0 || finalPosX == tailleMonde - 1) || (finalPosX < 0 || finalPosX >= tailleMonde)) {
                finalPosX = 1;
            }
            if((finalPosY == 0 || finalPosY == tailleMonde - 1) || (finalPosY < 0 || finalPosY >= tailleMonde)) {
                finalPosY = 1;
            }
        }
        
        this.positionX = finalPosX; // Correctly assign to field
        this.positionY = finalPosY; // Correctly assign to field
        
        blockActuelle = monde.getBlocks().get(this.positionY).get(this.positionX);
        if (blockActuelle != null) {
            blockActuelle.ajouterEntite(this); // Ensure entity is on the block's list
        }
    }

    // Generic deplacer method
    public void deplacer(int dx, int dy) {
        if (!vivant) return;

        int targetX = positionX + dx;
        int targetY = positionY + dy;

        // Boundary checks
        if (targetX < 0 || targetX >= monde.getTaille() || targetY < 0 || targetY >= monde.getTaille()) {
            System.out.println("DEBUGLOG: " + id + " cannot move out of bounds. Target: (" + targetX + "," + targetY + ")");
            return;
        }

        // Traversability check
        if (!monde.getBlocks().get(targetY).get(targetX).estTraverseable()) {
            System.out.println("DEBUGLOG: " + id + " cannot move to non-traversable block at (" + targetX + "," + targetY + ")");
            return;
        }

        // Energy check (assuming coutDeplacement is for one step)
        // For simplicity, let's assume moving 1 unit in X and 1 unit in Y costs twice.
        // A more sophisticated approach might use sqrt(dx^2 + dy^2) or Manhattan distance for cost.
        int cost = coutDeplacement * (Math.abs(dx) + Math.abs(dy));
        if (energie < cost) {
            System.out.println("DEBUGLOG: " + id + " does not have enough energy to move. Has " + energie + ", needs " + cost);
            return;
        }

        energie -= cost;

        // Update position and block
        if (blockActuelle != null) {
            blockActuelle.enleverEntite(this);
        }
        positionX = targetX;
        positionY = targetY;
        blockActuelle = monde.getBlocks().get(positionY).get(positionX);
        if (blockActuelle != null) {
            blockActuelle.ajouterEntite(this);
        }
        // System.out.println(id + " moved to (" + positionX + "," + positionY + ")");
    }

    public void deplacerHaut(){
        //s'il est pas vivant
        if(!vivant) return;

        //si on peux pas le traverser ou si
        if (positionY - 1 < 0) {
            return;
        }
        if (!monde.getBlocks().get(positionY - 1).get(positionX).estTraverseable()) {
            return;
        }
        energie -= coutDeplacement;
        positionY--;
        blockActuelle.enleverEntite(this);
        blockActuelle = monde.getBlocks().get(positionY).get(positionX);
        blockActuelle.ajouterEntite(this);
    }

    public void deplacerBas(){
        //s'il est pas vivant
        if(!vivant) return;

        //si on peux pas le traverser ou si
        if (positionY + 1 >= monde.getTaille()) {
            return;
        }
        if (!monde.getBlocks().get(positionY + 1).get(positionX).estTraverseable()) {
            return;
        }
        energie -= coutDeplacement;
        positionY++;
        blockActuelle.enleverEntite(this);
        blockActuelle = monde.getBlocks().get(positionY).get(positionX);
        blockActuelle.ajouterEntite(this);
    }

    public void deplacerGauche(){
        //s'il est pas vivant
        if(!vivant) return;

        //si on peux pas le traverser ou si
        if (positionX - 1 < 0) {
            return;
        }
        if (!monde.getBlocks().get(positionY).get(positionX - 1).estTraverseable()) {
            return;
        }
        energie -= coutDeplacement;
        positionX--;
        blockActuelle.enleverEntite(this);
        blockActuelle = monde.getBlocks().get(positionY).get(positionX);
        blockActuelle.ajouterEntite(this);
    }

    public void deplacerDroite(){
        //s'il est pas vivant
        if(!vivant) return;

        //si on peux pas le traverser ou si
        if (positionX + 1 >= monde.getTaille()) {
            return;
        }
        if (!monde.getBlocks().get(positionY).get(positionX + 1).estTraverseable()) {
            return;
        }
        energie -= coutDeplacement;
        positionX++;
        blockActuelle.enleverEntite(this);
        blockActuelle = monde.getBlocks().get(positionY).get(positionX);
        blockActuelle.ajouterEntite(this);
    }

    // Placeholder for interaction
    public String interagir() {
        if (!vivant) return this.id + " cannot interact.";
        // Basic interaction, subclasses can override for specific behaviors
        return this.id + " (" + this.type + ") interagit avec son environnement.";
    }
}