package com.mypackage.Npcs;

import com.mypackage.GamePanel;
import com.mypackage.Player;

/**
 * NPC du tutoriel expliquant les contrôles et mécaniques.
 */
public class NPC_tutorial1 extends NPC {

    /**
     * Constructeur de NPC_tutorial1.
     * @param gp le panneau de jeu
     * @param player le joueur
     */
    public NPC_tutorial1(GamePanel gp, Player player) {
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
        dialogues.add("Bonjour digne sauveur");
        dialogues.add("Pour te mouvoir tu pourras utiliser les touches Q et D.");
        dialogues.add("tu pourras sauter avec la touche ESPACE.");        
        dialogues.add("ou alors attaquer avec clic gauche");
        dialogues.add("et comme tu viens de le voir parler avec F");
        dialogues.add("Passons aux choses complexes");
        dialogues.add("Double saut avec Z");
        dialogues.add("Ou encore te régénérer d'un coeur (max 5) avec A pour 1 d'énergie");
        dialogues.add("Cependant tu as une barre d'énergie limitée");
        dialogues.add("5 d'energie maximum et +1 par coups porté");
        dialogues.add("Grâce à 3 d'energie tu pourras booster ta vitesse et attaque");
        dialogues.add("ou encore dash avec clic droit");
    }
}
