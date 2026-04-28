package com.mypackage.Npcs;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import com.mypackage.Entity;
import com.mypackage.GamePanel;
import com.mypackage.Player;

/**
 * Classe abstraite pour les NPCs, gérant mouvements, dialogues et interactions avec le joueur.
 */
public abstract class NPC extends Entity {

    protected BufferedImage right, left;
    protected final GamePanel gamePanel;
    protected Player player;

    protected int actionLockCounter = 0;
    protected String directionNPC;

    protected List<String> dialogues = new ArrayList<>();
    protected int dialogueIndex = 0;
    protected boolean talking = false;
    protected boolean interactPressedLast = false;

    /**
     * Constructeur d'un NPC.
     * @param gp le panneau de jeu
     * @param player le joueur
     * @param x position X
     * @param y position Y
     * @param width largeur
     * @param height hauteur
     * @param speed vitesse
     * @param maxLife vie maximale
     */
    public NPC(GamePanel gp, Player player,
               int x, int y, int width, int height, int speed, int maxLife) {
        super(x, y, width, height, speed, maxLife);
        this.gamePanel = gp;
        this.player = player;
        setDefaultValues();
    }

    protected void setDefaultValues() {
        this.positionX = 100;
        this.positionY = 100;
        direction = "right";
        directionNPC = "right";
    }

    /**
     * Met à jour l'état de l'NPC.
     */
    @Override
    public void update() {
        setAction();

        if (player != null) {
            float dx = positionX - player.positionX;
            float dy = positionY - player.positionY;
            float distance = (float) Math.sqrt(dx * dx + dy * dy);

            if (distance < 50
                    && gamePanel.getKeyHandler().getInteractPressed()
                    && !interactPressedLast) {
                talk();
            }else if (distance >= 50) {
                talking = false;
            }
        }

        interactPressedLast = gamePanel.getKeyHandler().getInteractPressed();
    }

    protected void setAction() {
        actionLockCounter++;

        if (actionLockCounter == 60) {
            if (!talking) {
                int random = (int) (Math.random() * 100) + 1;

                if (random < 50) {
                    directionNPC = "left";
                    move(-10, 0);
                } else {
                    directionNPC = "right";
                    move(10, 0);
                }
            
            }
            actionLockCounter = 0;
        }
    }

    protected void talk() {
        if (!talking) {
            talking = true;
            dialogueIndex = 0;
        } else {
            dialogueIndex++;
            if (dialogueIndex >= dialogues.size()) {
                talking = false;
            }
        }
    }

    /**
     * Dessine l'NPC et son dialogue si actif.
     * @param g2 Graphics2D pour le rendu
     * @param offsetX décalage X de la caméra
     * @param offsetY décalage Y de la caméra
     */
    @Override
    public void draw(Graphics2D g2, int offsetX, int offsetY) {

        if (directionNPC != null) {
            image = "left".equals(directionNPC) ? left : right;
        }

        if (image != null) {
            g2.drawImage(image,
                    (int) (positionX - offsetX),
                    (int) (positionY - offsetY),
                    width, height, null);
        } else {
            g2.setColor(java.awt.Color.RED);
            g2.fillRect(
                    (int) (positionX - offsetX),
                    (int) (positionY - offsetY),
                    Math.max(20, width),
                    Math.max(20, height));
        }

        if (talking && dialogueIndex < dialogues.size()) {
            g2.setFont(new Font("Arial", Font.PLAIN, 14));
            FontMetrics fm = g2.getFontMetrics();
            String text = dialogues.get(dialogueIndex);
            int textWidth = fm.stringWidth(text);
            int textHeight = fm.getHeight();
            int rectX = (int) (positionX - offsetX - 5);
            int rectY = (int) (positionY - offsetY - textHeight - 15);
            int rectWidth = textWidth + 10;
            int rectHeight = textHeight + 10;
            g2.setColor(new Color(0, 0, 0, 150));
            g2.fillRect(rectX, rectY, rectWidth, rectHeight);
            g2.setColor(Color.WHITE);
            g2.drawString(text, (int) (positionX - offsetX), (int) (positionY - offsetY - 10));
        }
    }

    protected void move(float dx, float dy) {
        positionX += dx;
        positionY += dy;
    }

    protected abstract void loadImages();
}
