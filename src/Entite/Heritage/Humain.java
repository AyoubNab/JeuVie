package Entite.Heritage;

import Element.Element;
import Entite.Entite;
import Monde.Monde;

import java.util.List;

public class Humain extends Entite {

    List<Element> Inventaire;


    public Humain(int faimTick, int soifTick, int positionX, int positionY, Monde monde) {
        super(faimTick, soifTick, positionX, positionY, monde);
    }
}
