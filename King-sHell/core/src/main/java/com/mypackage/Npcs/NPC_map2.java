package com.mypackage.Npcs;

import com.mypackage.GamePanel;
import com.mypackage.Player;

/**
 * NPC de la map2 remerciant le joueur pour la quête.
 */
public class NPC_map2 extends NPC {

    /**
     * Constructeur de NPC_map2.
     * @param gp le panneau de jeu
     * @param player le joueur
     */
    public NPC_map2(GamePanel gp, Player player) {
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
        dialogues.add("Mon alter-ego m'a dit de t'attendre ici");
        dialogues.add("Je suis désolé");
        dialogues.add("que ce soit à toi de nous sauver ");
        dialogues.add("de cet enfer");
        dialogues.add("merci");
        dialogues.add("infiniement");
    }
}
