package Entite;

import Element.Aliment.Aliment;
import Monde.*;

public abstract class Entite {
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

    public int getPositionX() { return positionX; } // Added getter
    public int getPositionY() { return positionY; } // Added getter

    public int getAge() {return age;}
    public int getSoif() {return soif;}
    public int getFaim() {return faim;}
    public int getEnergie() {return energie;}
    public int getSante() {return sante;} // Added getter for sante

    public Block getBlockActuelle() { return blockActuelle; }


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

    public Entite(int faimTick, int soifTick, int positionX, int positionY, Monde monde) {
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
        
        positionX = finalPosX;
        positionY = finalPosY;
        
        blockActuelle = monde.getBlocks().get(positionY).get(positionX);
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
}