package Monde;

import java.util.ArrayList;

public class Monde {
    private int taille;
    public ArrayList<ArrayList<Block>> blocks;
    public Monde(int taille) {

        this.taille = taille;

        blocks = new ArrayList<>();
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
            blocks.add(ligneBlocks); // Add the row of blocks to the main list
        }
    }

    public int getTaille() {
        return taille;
    }

    public ArrayList<ArrayList<Block>> getBlocks() {
        return blocks;
    }
}
