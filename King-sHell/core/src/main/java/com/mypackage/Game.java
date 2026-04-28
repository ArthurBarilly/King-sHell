package com.mypackage;

import java.util.ArrayList;
import java.util.List;


/**
 * Classe principale gérant la logique du jeu et les entités.
 */
class Game {
    private List<Entity> entities = new ArrayList<>();
    private GamePanel gp;
    private KeyHandler keyHandler;

    /**
     * Initialise le jeu en créant les entités de base.
     */
    public void initializeGame() {
        entities.add(new Player(gp, keyHandler, 10, 5, 100, 100));
    }

    /**
     * Met à jour toutes les entités du jeu.
     * @param deltaTime temps écoulé depuis la dernière mise à jour
     */
    public void update(float deltaTime) {
        for (Entity entity : entities) {
            entity.update();
        }
    }
}