package Entite.Heritage;

import Entite.Entite;
import Monde.Monde;

public class Animaux extends Entite {
    private String espece;

    public Animaux(String id, String espece, int faimTick, int soifTick, int positionX, int positionY, Monde monde) {
        super(id, "Animal:" + espece, faimTick, soifTick, positionX, positionY, monde); // Type includes espece
        this.espece = espece;
    }

    public String getEspece() {
        return espece;
    }

    @Override
    public void action() {
        System.out.println("Animaux.action() called for: " + getId() + " [" + getEspece() + "]");
        if (!estVivant()) {
            // System.out.println("Animaux " + getId() + " is not vivant. Action aborted.");
            return;
        }

        String moveOutcomeLog = "Animal " + getId() + " (" + getEspece() + ", Faim:" + getFaim() + ", Energie:" + getEnergie() + ")";

        // TODO: Implement more complex AI, e.g., hunger-driven search for food
        // if (getFaim() < 50) { /* try to find food */ }

        if (getEnergie() > 5) { // Only move if has some energy
            java.util.Random random = new java.util.Random();
            int direction = random.nextInt(5); // 0:N, 1:S, 2:E, 3:W, 4:Stay

            int oldX = getPositionX();
            int oldY = getPositionY();

            switch (direction) {
                case 0: // North
                    deplacer(0, -1);
                    moveOutcomeLog += " attempted North. ";
                    break;
                case 1: // South
                    deplacer(0, 1);
                    moveOutcomeLog += " attempted South. ";
                    break;
                case 2: // East
                    deplacer(1, 0);
                    moveOutcomeLog += " attempted East. ";
                    break;
                case 3: // West
                    deplacer(-1, 0);
                    moveOutcomeLog += " attempted West. ";
                    break;
                case 4: // Stay still
                    moveOutcomeLog += " decided to stay still.";
                    break;
            }


            if (getPositionX() != oldX || getPositionY() != oldY) {
                moveOutcomeLog += " New position: (" + getPositionX() + "," + getPositionY() + ").";
            } else {
                if (direction != 4) { // Tried to move but failed
                    moveOutcomeLog += " Movement failed or stayed in place. Position: (" + getPositionX() + "," + getPositionY() + ").";
                }
            }


        } else {
            moveOutcomeLog += " has low energy and rests.";
        }
        System.out.println(moveOutcomeLog); // Print the log
    }

    @Override
    public String interagir() {
        // Animal specific interaction
        return super.interagir() + " L'animal (" + espece + ") renifle l'air.";
    }

    @Override
    public String toString() {
        // Example of a more specific toString for Animaux
        return String.format("%s{id='%s', espece='%s', x=%d, y=%d, sante=%d, faim=%d, energie=%d}",
                             "Animaux", getId(), espece, getPositionX(), getPositionY(), getSante(), getFaim(), getEnergie());
    }
}
