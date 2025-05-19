package Monde;

public class Block {
    int tailleBlock = 20;

    boolean estTraverseable = true; //es ce qu'on peux passer le block, ex si c'est un arbre non et si c'est une riviere non plus
    boolean estConstructible = true; //es ce qu'on peux contruire dans le block
    boolean estDestructible = false; //es ce qu'on peux detruire le block

    public Block(){}
    public Block(boolean estTraverseable, boolean estConstructible, boolean estDestructible){
        this.estTraverseable = estTraverseable;
        this.estConstructible = estConstructible;
        this.estDestructible = estDestructible;
    }
}
