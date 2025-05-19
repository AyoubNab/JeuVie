package Entite;

import Element.Aliment.Aliment;

public abstract class Entite {
    private int positionX;
    private int positionY;

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

    public void avancerHaut(){

    }

    public boolean manger(Aliment a, int quantite){
        return a.consomer(quantite);
    }

    public Entite(int faimTick, int soifTick, int positionX, int positionY) {

    }
}