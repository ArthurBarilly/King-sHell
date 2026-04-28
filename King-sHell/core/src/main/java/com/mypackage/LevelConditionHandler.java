package com.mypackage;

/**
 * Interface pour gérer les conditions de passage de niveau.
 */
public interface LevelConditionHandler {
    /**
     * Vérifie et gère la condition pour passer au niveau suivant.
     * @param gamePanel le panneau de jeu
     * @return true si la condition est remplie
     */
    boolean handle(GamePanel gamePanel);
}
