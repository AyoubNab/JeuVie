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

    private int age = 0;
    private int soif = 100;
    private int faim = 100;
    private int energie = 100;
    private boolean vivant = true;

    public int getAge() {return age;}
    public int getSoif() {return soif;}
    public int getFaim() {return faim;}
    public int getEnergie() {return energie;}



    public void prochainTick(){
        if (!vivant ) return;
        //chaque tick il gagne de l'energie, et il perd soif et faim
        energie += (faimTick + soifTick) / 2;
        soif -= soifTick;
        if (soif < 0) vivant = false;

        faim -= faimTick;
        if (faim < 0) vivant = false;
    }


    public boolean manger(Aliment a, int quantite){
        boolean consomme = a.consomer(quantite);
        if (consomme) {
            this.faim += quantite;
            if (this.faim > 100) this.faim = 100;
        }
        return consomme;
    }

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