package com.mypackage;

import javax.swing.JFrame;
//si probleme de hitbox dans TileMap enlever de commentaires les contour de hitbox
// Joueur qui ne meurt pas normal
//Projet fait par Barilly Arthut, num etudiant 22405057
/**
 * Classe principale lançant le jeu dans une fenêtre JFrame.
 */
public class MainGame {
    /**
     * Point d'entrée du programme.
     * @param args arguments de ligne de commande
     */
    public static void main(String[] args) {

        JFrame window = new JFrame("Ceci est un nom");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setTitle("King'sHell");

        GamePanel gamePanel = new GamePanel();
        window.add(gamePanel);
        window.pack();

        window.setLocationRelativeTo(null);
        window.setVisible(true);

        window.requestFocus();
        gamePanel.requestFocusInWindow();

        gamePanel.startGameThread();

    }
}