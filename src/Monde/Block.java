package Monde;

import Element.Element; // Added import
import Entite.Entite;

import java.util.ArrayList;
import java.util.List; // Added import

public class Block {
    int tailleBlock = 20;
    public ArrayList<Entite> entites;
    private List<Element> elements; // Added elements list


    boolean estTraverseable = true; //es ce qu'on peux passer le block, ex si c'est un arbre non et si c'est une riviere non plus
    boolean estConstructible = true; //es ce qu'on peux contruire dans le block
    boolean estDestructible = false; //es ce qu'on peux detruire le block

    public Block(){
        this.entites = new ArrayList<>();
        this.elements = new ArrayList<>(); // Initialize elements
    }
    public Block(boolean estTraverseable, boolean estConstructible, boolean estDestructible){
        this.entites = new ArrayList<>();
        this.elements = new ArrayList<>(); // Initialize elements
        this.estTraverseable = estTraverseable;
        this.estConstructible = estConstructible;
        this.estDestructible = estDestructible;
    }

    public void ajouterEntite(Entite e){
        if(estTraverseable) entites.add(e);
    }

    public void enleverEntite(Entite e){
        entites.remove(e);
    }

    public boolean estTraverseable() {
        return estTraverseable;
    }

    // Methods for managing elements
    public void ajouterElement(Element element) {
        if (element != null) {
            this.elements.add(element);
        }
    }

    public void enleverElement(Element element) {
        this.elements.remove(element);
    }

    public List<Element> getElements() {
        return this.elements;
    }
}
