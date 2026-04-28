package com.mypackage;

/**
 * Gestionnaire vérifiant la condition de passage au niveau suivant en atteignant la sortie.
 */
public class NextLevelHandler implements LevelConditionHandler {

    /**
     * Vérifie si le joueur atteint la sortie et charge le niveau suivant.
     * @param gp le panneau de jeu
     * @return true si le niveau suivant est chargé
     */
    @Override
    public boolean handle(GamePanel gp) {

        if (gp == null) return false;

        TileMap map = gp.getLevelManager().getCurrentMap();
        Player player = gp.getPlayer();

        if (map == null || player == null) return false;

        if (map.isExitReached(
                player.getX(),
                player.getY(),
                player.getWidth(),
                player.getHeight()
        )) {
            if (gp.getLevelManager().nextMap()) {
                TileMap newMap = gp.getLevelManager().getCurrentMap();
                player.setPosition(newMap.getSpawnX(),newMap.getSpawnY());

                System.out.println("=== NEXT LEVEL ===");
                return true;
            }
        }

        return false;
    }
}
