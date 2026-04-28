package com.mypackage.objects;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.mypackage.GamePanel;

/**
 * Objet représentant la barre d'énergie du joueur.
 * Affiche les points d'énergie sous forme d'icônes.
 */
public  class OBJ_Energy extends SuperObject{

    GamePanel gp;
    BufferedImage start_image;
    private int coordY = screenY + 30;

    /**
     * Constructeur de OBJ_Energy.
     * @param gp le panneau de jeu
     */
    public OBJ_Energy(GamePanel gp){
        this.gp = gp;
        
        name = "Energy";
        try{
            BufferedImage original = ImageIO.read(getClass().getResourceAsStream("/objects/energy.png"));
            image = new BufferedImage(gp.getTileSize(), gp.getTileSize(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = image.createGraphics();
            g.drawImage(original, 0, 0, gp.getTileSize(), gp.getTileSize(), null);
            g.dispose();

            BufferedImage start_original = ImageIO.read(getClass().getResourceAsStream("/objects/energy_start.png"));
            start_image = new BufferedImage(gp.getTileSize(), gp.getTileSize(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = start_image.createGraphics();
            g2.drawImage(start_original, 0, 0, gp.getTileSize(), gp.getTileSize(), null);
            g2.dispose();

        }catch(IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Dessine la barre d'énergie.
     * @param g2 contexte graphique
     * @param gp panneau de jeu
     */
    @Override
    public void draw(Graphics2D g2, GamePanel gp) {
        int currentEnergy = gp.getPlayer().getEnergy();
        for (int i = currentEnergy; i > 0; i-=1) {
            g2.drawImage(image, (screenX + i * gp.getTileSize()), coordY, null);
 
        }
    g2.drawImage(start_image, screenX, coordY, null);
    }
}