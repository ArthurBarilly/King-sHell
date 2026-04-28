package com.mypackage.objects;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.mypackage.GamePanel;

/**
 * Objet représentant la barre de vie du joueur.
 * Affiche les points de vie sous forme de cœurs pleins et demi-cœurs.
 */
public  class OBJ_Heart extends SuperObject{
    
    GamePanel gp;
    BufferedImage half_image;

    /**
     * Constructeur de OBJ_Heart.
     * @param gp le panneau de jeu
     */
    public OBJ_Heart(GamePanel gp){
        this.gp = gp;
        
        name = "Heart";
        try{
            BufferedImage original = ImageIO.read(getClass().getResourceAsStream("/objects/heart.png"));
            image = new BufferedImage(gp.getTileSize(), gp.getTileSize(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = image.createGraphics();
            g.drawImage(original, 0, 0, gp.getTileSize(), gp.getTileSize(), null);
            g.dispose();
            
            BufferedImage half_original = ImageIO.read(getClass().getResourceAsStream("/objects/half_heart.png"));
            half_image = new BufferedImage(gp.getTileSize(), gp.getTileSize(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = half_image.createGraphics();
            g2.drawImage(half_original, 0, 0, gp.getTileSize(), gp.getTileSize(), null);
            g2.dispose();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Dessine la barre de vie.
     * @param g2 contexte graphique
     * @param gp panneau de jeu
     */
    @Override
    public void draw(Graphics2D g2, GamePanel gp) {
        int currentHealth = gp.getPlayer().getHealth();
        for (int i = currentHealth; i > 0; i-=2) {
            if (i % 2 == 0){
                g2.drawImage(image, (screenX + i * gp.getTileSize())/2, screenY, null);
            }
            else {
                g2.drawImage(half_image, (screenX+ i * gp.getTileSize())/2, screenY, null);   
                i +=1;  
        } 
    }
    }
}