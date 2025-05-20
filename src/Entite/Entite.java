package Entite;

import Element.Aliment.Aliment;
import Monde.Monde;

public abstract class Entite {
    private int positionX;
    private int positionY;
    private Monde monde;

    //ticks
    private int faimTick;
    private int soifTick;

    private int age = 0;
    private int soif = 100;
    private int faim = 100;
    private int energie = 100;

    public int getAge() {return age;}
    public int getSoif() {return soif;}
    public int getFaim() {return faim;}
    public int getEnergie() {return energie;}


    public boolean estEnVie(){
        if (soif > 0 && faim > 0) return true;
        return false;
    }

    public void prochainTick(){
        if (!estEnVie()) return;
        //chaque tick il gagne de l'energie, et il perd soif et faim
        energie += (faimTick + soifTick) / 2;
        soif -= soifTick;
        faim -= faimTick;
    }



    public boolean manger(Aliment a, int quantite){
        return a.consomer(quantite);
    }

    public Entite(int faimTick, int soifTick, int positionX, int positionY, Monde monde) {
        this.monde = monde;
        int tailleMonde = monde.getTaille();
        //si la position n'est pas dans le monde alors la position sera 1,1
        if((positionX == 0 || positionX == tailleMonde - 1) || (positionX < 0 || positionX > tailleMonde)) positionX = 1;
        if((positionY == 0 || positionX == tailleMonde - 1) || (positionY < 0 || positionX > tailleMonde)) positionY = 1;

    }
}