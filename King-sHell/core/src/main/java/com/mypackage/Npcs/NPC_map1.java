package com.mypackage.Npcs;

import com.mypackage.GamePanel;
import com.mypackage.Player;

/**
 * NPC de la map1 donnant des indices sur la quête.
 */
public class NPC_map1 extends NPC {

    /**
     * Constructeur de NPC_map1.
     * @param gp le panneau de jeu
     * @param player le joueur
     */
    public NPC_map1(GamePanel gp, Player player) {
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
        dialogues.add("Oh te voilà");
        dialogues.add("ton but retrouver mon alter-ego en haut");
        dialogues.add("il te guidera vers le dieu de l'enfer");
        dialogues.add("HADES");
        dialogues.add("sois prudent");
    }
}
