package Entite.Heritage;

import Element.Element;
import Entite.Entite;

import java.util.List;

public class Humain extends Entite {

    List<Element> Inventaire;


    public Humain(int faimTick, int soifTick, int positionX, int positionY) {
        super(faimTick, soifTick, positionX, positionY);
    }
}
