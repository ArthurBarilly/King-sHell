package com.mypackage.Enemies;

import com.mypackage.GamePanel;
import com.mypackage.Player;

/**
 * Ennemi Gromp, un ennemi de base avec animations de marche et d'attaque.
 */
public class Gromp extends SuperEnemy {

    /**
     * Constructeur de Gromp.
     * @param gp le panneau de jeu
     * @param player le joueur
     */
    public Gromp(GamePanel gp, Player player) {
        super(gp, player, 0, 0, 32, 32, 10, 30);
        loadImages();
    }

    /**
     * Charge les images du Gromp.
     */
    @Override
    protected void loadImages() {
        right[0] = loadOrPlaceholder(
                "/Entity/enemy/gromp_right0.png",
                java.awt.Color.GREEN);
        right[1] = loadOrPlaceholder(
                "/Entity/enemy/gromp_right1.png",
                java.awt.Color.GREEN);

        left[0] = loadOrPlaceholder(
                "/Entity/enemy/gromp_left0.png",
                java.awt.Color.MAGENTA);
        left[1] = loadOrPlaceholder(
                "/Entity/enemy/gromp_left1.png",
                java.awt.Color.MAGENTA);

        leftAttack[0] = loadOrPlaceholder(
                "/Entity/enemy/gromp_left_attack0.png",
                java.awt.Color.BLUE);
        leftAttack[1] = loadOrPlaceholder(
                "/Entity/enemy/gromp_left_attack1.png",
                java.awt.Color.BLUE);

        rightAttack[0] = loadOrPlaceholder(
                "/Entity/enemy/gromp_right_attack0.png",
                java.awt.Color.BLUE);
        rightAttack[1] = loadOrPlaceholder(
                "/Entity/enemy/gromp_right_attack1.png",
                java.awt.Color.BLUE);
    }

}
