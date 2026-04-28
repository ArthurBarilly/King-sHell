package com.mypackage.objects;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.mypackage.GamePanel;

/**
 * Classe de base abstraite pour tous les objets du jeu.
 * Gère l'affichage et les propriétés communes des objets.
 */
public class SuperObject {
    
    protected BufferedImage image;
    protected String name;
    protected int screenX = 0;
    protected int screenY = 0;

    /**
     * Dessine l'objet à l'écran.
     * @param g2 contexte graphique
     * @param gp panneau de jeu
     */
    public void draw(Graphics2D g2, GamePanel gp){
        g2.drawImage(image, screenX, screenY, null);

    }
}