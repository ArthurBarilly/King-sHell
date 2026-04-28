package com.mypackage;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * Classe abstraite de base pour toutes les entités du jeu (joueur, ennemis, etc.).
 * Gère la position, la santé, les dégâts et les mouvements de base.
 */
public abstract class Entity {

    public float positionX;
    public float positionY;

    protected final int width;
    protected final int height;

    private int damage;
    private int health;
    private int maxHealth;

    public String direction;
    protected int spriteCounter = 0;
    protected int spriteNum = 1;
    protected GamePanel gamePanel;
    protected BufferedImage image;


    protected float velocityX;
    protected float velocityY;
    protected float gravity = 0.5f;

    protected boolean grounded;

    /**
     * Constructeur d'Entity.
     * @param positionX position X initiale
     * @param positionY position Y initiale
     * @param width largeur de l'entité
     * @param height hauteur de l'entité
     * @param health points de vie initiaux
     * @param damage dégâts infligés
     */
    protected Entity(float positionX, float positionY, int width, int height, int health, int damage) {
        this.positionX = positionX;
        this.positionY = positionY;
        this.width = width;
        this.height = height;
        this.health = health;
        this.damage = damage;
        this.maxHealth = health;
        this.velocityX = 0;
        this.velocityY = 0;
        this.grounded = false;
    }

    /**
     * Dessine l'entité à l'écran (à implémenter dans les sous-classes).
     * @param g2 contexte graphique
     * @param offsetX décalage X de la caméra
     * @param offsetY décalage Y de la caméra
     */
    abstract public void draw(Graphics2D g2, int offsetX, int offsetY);


    /**
     * Déplace l'entité.
     * @param deltaX déplacement en X
     * @param deltaY déplacement en Y
     */
    protected void move(float deltaX, float deltaY) {
        this.positionX += deltaX;
        this.positionY += deltaY;
    }

    /**
     * Applique la gravité à l'entité.
     */
    protected void applyGravity() {
        if (!grounded) {
            velocityY -= gravity;
            positionY += velocityY;
        }
    }

    /**
     * Inflige des dégâts à l'entité.
     * @param damage quantité de dégâts
     */
    public void takeDamage(int damage) {
        this.health -= damage;
        if (this.health < 0) this.health = 0; //fct de changement de phase?
    }

    /**
     * Soigne l'entité.
     * @param amount quantité de soin
     */
    protected void heal(int amount) {
        this.health += amount;
        if (this.health > this.maxHealth) this.health = this.maxHealth;
    }

    /**
     * Attaque une cible.
     * @param target entité cible
     */
    protected void attack(Entity target) {
        if (target != null) {
            target.takeDamage(this.damage);
        }
    }
    
    /**
     * Retourne la santé actuelle.
     * @return santé actuelle
     */
    protected int getHealth() {
        return this.health;
    }

    /**
     * Retourne la santé maximale.
     * @return santé maximale
     */
    public int getMaxHealth() {
        return this.maxHealth;
    }

    /**
     * Définit la santé maximale.
     * @param maxHealth nouvelle santé maximale
     */
    protected void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    /**
     * Retourne les dégâts.
     * @return dégâts
     */
    protected int getDamage() {
        return this.damage;
    }

    /**
     * Retourne la position X.
     * @return position X
     */
    protected float getPositionX() {
        return this.positionX;
    }

    /**
     * Retourne la position Y.
     * @return position Y
     */
    protected float getPositionY() {
        return this.positionY;
    }

    /**
     * Retourne la largeur.
     * @return largeur
     */
    protected int getWidth() {
        return this.width;
    }

    /**
     * Retourne la hauteur.
     * @return hauteur
     */
    protected int getHeight() {
        return this.height;
    }

    /**
     * Vérifie si l'entité est au sol.
     * @return true si au sol
     */
    protected boolean isGrounded() {
        return this.grounded;
    }


    /**
     * Définit les dégâts.
     * @param damage nouveaux dégâts
     */
    /**
     * Définit les dégâts.
     * @param damage nouveaux dégâts
     */
    protected void setDamage(int damage) {
        this.damage = damage;
    }

    /**
     * Définit la santé.
     * @param health nouvelle santé
     */
    protected void setHealth(int health) {
        this.health = health;
    }

    /**
     * Définit si l'entité est au sol.
     * @param grounded true si au sol
     */
    protected void setGrounded(boolean grounded) {
        this.grounded = grounded;
    }

    /**
     * Met à jour l'entité (à implémenter dans les sous-classes).
     */
    public abstract void update();

    /**
     * Charge une image depuis les ressources ou retourne un placeholder.
     * @param resourcePath chemin de la ressource
     * @param color couleur du placeholder
     * @return image chargée ou placeholder
     */
    protected BufferedImage loadOrPlaceholder(String resourcePath, java.awt.Color color) {
        try {
            try (java.io.InputStream is = getClass().getResourceAsStream(resourcePath)) {
                if (is != null) return javax.imageio.ImageIO.read(is);
            }

            String rel = resourcePath.startsWith("/") ? resourcePath.substring(1) : resourcePath;
            java.io.File f = new java.io.File("src/main/resources/" + rel);
            if (f.exists() && f.isFile()) {
                return javax.imageio.ImageIO.read(f);
            }

            System.err.println("Resource not found: " + resourcePath + " -> using placeholder");
            int w = Math.max(32, width > 0 ? width : 32);
            int h = Math.max(32, height > 0 ? height : 32);
            BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            java.awt.Graphics2D g = img.createGraphics();
            g.setColor(color); g.fillRect(0, 0, w, h);
            g.setColor(java.awt.Color.BLACK); g.drawRect(0, 0, w - 1, h - 1);
            g.dispose();
            return img;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}