package com.mypackage.Npcs;

import com.mypackage.GamePanel;
import com.mypackage.Player;

/**
 * NPC du tutoriel avec dialogues spécifiques.
 */
public class NPC_tutorial extends NPC {

    /**
     * Constructeur de NPC_tutorial.
     * @param gp le panneau de jeu
     * @param player le joueur
     */
    public NPC_tutorial(GamePanel gp, Player player) {
        super(gp, player, 0, 0, 32, 64, 10, 5);
        loadImages();
        loadDialogues();
    }

    @Override
    protected void loadImages() {
        right = loadOrPlaceholder(
                "/Entity/npcs/npc_1right.png",
                java.awt.Color.GREEN);

        left = loadOrPlaceholder(
                "/Entity/npcs/npc_1left.png",
                java.awt.Color.MAGENTA);

        image = right;
    }

    private void loadDialogues() {
        dialogues.add("Bonjour aventurier !");
        dialogues.add("Accepete tu de nous libérer d'Hadess");
        dialogues.add("Je ne te laisse pas le choix");
        dialogues.add("Sinon toi aussi tu meurt de ce monstre");
        dialogues.add("Merci d'avance, ");
        dialogues.add("continue jusqu'à la prochaine zone ");
    }
}
