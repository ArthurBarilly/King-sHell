package com.mypackage.Enemies;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.mypackage.Entity;
import com.mypackage.GamePanel;
import com.mypackage.Player;

/**
 * Classe de base abstraite pour tous les ennemis du jeu.
 * Gère les comportements communs comme l'agro, les attaques et les animations.
 */
public abstract class SuperEnemy extends Entity {

    protected BufferedImage[] right = new BufferedImage[2];
    protected BufferedImage[] left = new BufferedImage[2];
    protected final GamePanel gamePanel;
    protected Player player;
    private boolean alive = true;
    protected int attackDelayCounter = 0;
    protected static final int ATTACK_DELAY = 30;
    protected int speed;


    protected int actionLockCounter = 0;
    protected String directionEnemy;

    protected boolean agro = false;

    protected BufferedImage[] leftAttack = new BufferedImage[2];
    protected BufferedImage[] rightAttack = new BufferedImage[2];
    protected boolean attacking = false;
    protected int attackCounter = 0;
    protected int attackFrame = 0;
    protected int damage = 1;

    protected int walkCounter = 0;
    protected int walkFrame = 0;

    /**
     * Constructeur de SuperEnemy.
     * @param gp le panneau de jeu
     * @param player le joueur
     * @param x position X initiale
     * @param y position Y initiale
     * @param width largeur de l'ennemi
     * @param height hauteur de l'ennemi
     * @param speed vitesse de déplacement
     * @param health points de vie
     */
    public SuperEnemy(GamePanel gp, Player player, int x, int y, int width, int height, int speed, int health) {
        super(x, y, width, height, health, 0);
        this.speed = speed;
        this.gamePanel = gp;
        this.player = player;
        setDefaultValues();
    }

    protected void setDefaultValues() {
        this.positionX = 100;
        this.positionY = 100;
        direction = "right";
        directionEnemy = "left";
    }

    /**
     * Met à jour l'état de l'ennemi : animations, agro, attaques.
     */
    @Override
    public void update() {
        if (!alive) return;

        setAction();

        if (attacking) {
            attackCounter++;
            if (attackCounter % 10 == 0) {
                attackFrame = (attackFrame + 1) % 2;
            }
            if (attackCounter > 100) {
                attacking = false;
                float dx = positionX - player.positionX;
                boolean inFront = (directionEnemy.equals("left") && dx > 0 && dx < 50) || (directionEnemy.equals("right") && dx < 0 && dx > -50);
                if (inFront) {
                    player.takeDamage(damage);
                }
            }
        } else {
            walkCounter++;
            if (walkCounter % 10 == 0) {
                walkFrame = (walkFrame + 1) % 2;
            }
        }

        if (player != null) {
            float dx = positionX - player.positionX;
            float dy = positionY - player.positionY;
            float distance = (float) Math.sqrt(dx * dx + dy * dy);

            if (distance < 200) {
                agro = true;
            } else {
                agro = false;
            }

            if (agro && distance < 80 && !attacking) {
                attackDelayCounter++;

            if (attackDelayCounter >= ATTACK_DELAY) {
                attack(player);
                attackDelayCounter = 0;
            }
            } else {
            attackDelayCounter = 0;
            }
        }
        isAlive();
    }

    /**
     * Définit l'action de l'ennemi : mouvement aléatoire ou poursuite du joueur.
     */
    protected void setAction() {
        actionLockCounter++;

        if (actionLockCounter == 60) {
            if (!agro) {
                int random = (int) (Math.random() * 100) + 1;

                if (random < 50) {
                    directionEnemy = "left";
                    move(-10, 0);
                } else {
                    directionEnemy = "right";
                    move(10, 0);
                }
            
            } else if (agro && player != null) {
                float dx = positionX - player.positionX;
                float dy = positionY - player.positionY;
                float distance = (float) Math.sqrt(dx * dx + dy * dy);
                if (distance < 50) {
                    attack(player);
                }
            }
            actionLockCounter = 0;
        }
    }
    /**
     * Vérifie si l'ennemi est encore en vie.
     */
    protected void isAlive() {
        if (this.getHealth() <= 0) {
            alive = false;
        }
    }

    /**
     * Lance une attaque contre le joueur.
     * @param player le joueur cible
     */
    protected void attack(Player player) {
        if (!attacking) {
            attacking = true;
            attackCounter = 0;
            attackFrame = 0;
            agro = true;

            float dx = positionX - player.positionX;
            float dy = positionY - player.positionY;
            float distance = (float) Math.sqrt(dx * dx + dy * dy);
            boolean inAttackRange = distance < 40;
            boolean inFront = (directionEnemy.equals("left") && dx > 0) || (directionEnemy.equals("right") && dx < 0);
        }
    }

    /**
     * Dessine l'ennemi à l'écran.
     * @param g2 contexte graphique
     * @param offsetX décalage X de la caméra
     * @param offsetY décalage Y de la caméra
     */
    @Override
    public void draw(Graphics2D g2, int offsetX, int offsetY) {
        if (!alive) return;

        if (attacking) {
            image = directionEnemy.equals("left") ? leftAttack[attackFrame] : rightAttack[attackFrame];
        } else {
            image = directionEnemy.equals("left") ? left[walkFrame] : right[walkFrame];
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
    }

    /**
     * Déplace l'ennemi.
     * @param dx déplacement en X
     * @param dy déplacement en Y
     */
    @Override
    protected void move(float dx, float dy) {
        positionX += dx;
        positionY += dy;
    }

    /**
     * Charge les images de l'ennemi (à implémenter dans les sous-classes).
     */
    protected abstract void loadImages();
}
