package com.mypackage;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import com.mypackage.Enemies.SuperEnemy;

/**
 * Classe représentant le joueur, gérant mouvements, attaques, capacités et collisions.
 */
public class Player extends Entity {

    private int energy;
    private final GamePanel gamePanel;
    private final KeyHandler keyHandler;

    private static int MOVE_SPEED = 6;
    private static final int ANIM_SPEED = 10;

    private int damage;
    private final int max_Energy = 5;

    private Capacity capacity;

    private BufferedImage up_left, up_right, left1, left2, right1, right2, down_left, down_right;
    private BufferedImage[] leftAttack = new BufferedImage[4];
    private BufferedImage[] rightAttack = new BufferedImage[4];

    private boolean attacking = false;
    private int attackCounter = 0;
    private int attackFrame = 0;
    private boolean attackPressedLast = false;

    private Map<String, BufferedImage> dirImages = new HashMap<>();
    private String lastHorizontal = "right";

    /**
     * Constructeur du joueur.
     * @param gamePanel le panneau de jeu
     * @param keyHandler le gestionnaire de touches
     * @param health santé initiale
     * @param energy énergie initiale
     * @param x position X initiale
     * @param y position Y initiale
     */
    protected Player(GamePanel gamePanel, KeyHandler keyHandler, int health, int energy, float x, float y) {
        super(x, y, gamePanel.tileSize, gamePanel.tileSize, health, 0);
        this.gamePanel = gamePanel;
        this.keyHandler = keyHandler;
        this.energy = energy;
        this.capacity = new Capacity(1000, 1);
        setDefaultValues();
        getPlayerImage();
    }

    private void setDefaultValues() {
        this.energy = max_Energy;
        this.setMaxHealth(10);
        this.damage = 10;
        this.positionX = 100;
        this.positionY = 100;
        this.velocityX = 0;
        this.velocityY = 0;
        direction = "right";
    }

    /**
     * Met à jour l'état du joueur : mouvements, attaques, capacités, gravité.
     */
    @Override
    public void update() {
        if (keyHandler == null || gamePanel == null) return;

        boolean movingHoriz = false;

        // ===== SAUT =====
        if (keyHandler.upPressed && grounded) {
            velocityY = -5;
            grounded = false;
        }

        // ===== DEPLACEMENT HORIZONTAL =====
        if (keyHandler.leftPressed) {
            direction = "left";
            lastHorizontal = "left";
            moveX(-MOVE_SPEED);
            movingHoriz = true;
        }

        if (keyHandler.rightPressed) {
            direction = "right";
            lastHorizontal = "right";
            moveX(MOVE_SPEED);
            movingHoriz = true;
        }

        // ===== ATTAQUE =====
        if (keyHandler.attackPressed && !attackPressedLast && !attacking) {
            attacking = true;
            attackCounter = 0;
            attackFrame = 0;
        }
        attackPressedLast = keyHandler.attackPressed;

        // ===== ANIMATION =====
        if (movingHoriz) {
            spriteCounter++;
            if (spriteCounter > ANIM_SPEED) {
                spriteNum = (spriteNum == 1) ? 2 : 1;
                spriteCounter = 0;
            }
        } else {
            spriteCounter = 0;
            spriteNum = 1;
        }

        if (keyHandler.doubleJumpPressed) capacity.doubleJump(this);
        if (keyHandler.dashPressed) capacity.dash(this, "right".equals(lastHorizontal));
        if (keyHandler.healPressed) capacity.heal(this);
        if (keyHandler.boostPressed) capacity.boost(this);

        // ===== ATTAQUE LOGIQUE =====
        handleAttack();

        if (getHealth() <= 0) {
            System.out.println("tu es mort !. Je te laisse en vie uniquement car le jeu est très mal équilibré et Hades presque impossible à battre sinon.");
        }

        // ===== GRAVITE =====
        applyGravity();
    }

    /**
     * Vérifie si le joueur a assez d'énergie.
     * @param amount quantité requise
     * @return true si assez d'énergie
     */
    public boolean hasEnergy(int amount) {
        if (this.energy > 0) {
            return true;
        }
        return false;
    }

    /**
     * Consomme de l'énergie.
     * @param amount quantité à consommer
     */
    public void consumeEnergy(int amount) {
        if (this.energy >= amount) {
            this.energy -= amount;
        }
    }

    /**
     * Définit la position du joueur.
     * @param x position X
     * @param y position Y
     */
    public void setPosition(float x, float y) {
        this.positionX = x;
        this.positionY = y;
    }

    private void moveX(float dx) {
        positionX += dx;
        if (gamePanel.getCurrentMap().isSolid((int) positionX, (int) positionY, width, height)) {
            positionX -= dx;
        }
    }

    private void moveY(float dy) {
        positionY += dy;
        if (gamePanel.getCurrentMap().isSolid((int) positionX, (int) positionY, width, height)) {
            positionY -= dy;
            velocityY = 0;
        }
    }

    protected void applyGravity() {
        velocityY += gamePanel.gravity;
        float dy = Math.min(Math.abs(velocityY), 4) * Math.signum(velocityY);

        positionY += dy;

        if (gamePanel.getCurrentMap().isGround((int) positionX, (int) positionY, width, height)
         || gamePanel.getCurrentMap().isSolid((int) positionX, (int) positionY, width, height)) {
            positionY -= dy;
            velocityY = 0;
            grounded = true;
        } else {
            grounded = false;
        }
    }

    private void handleAttack() {
        if (!attacking) return;

        attackCounter++;
        if (attackCounter % 10 == 0) {
            attackFrame = (attackFrame + 1) % 4;
        }

        if (attackCounter > 60) {
            attacking = false;
            for (SuperEnemy m : gamePanel.getMonsters()) {
                System.err.println("Checking attack on monster at (" + m.positionX + ", " + m.positionY + ")");
                float dx = positionX - m.positionX;
                float distance = Math.abs(dx);           
                if (distance < 40 ) {
                    System.err.println("Attacking monster! by " + damage + " damage.");
                    m.takeDamage(damage);
                    this.energy = Math.min(this.energy + 1, max_Energy);
                    return;
                }
            }
        }
    }

    /**
     * Dessine le joueur.
     * @param g2 Graphics2D pour le rendu
     * @param offsetX décalage X de la caméra
     * @param offsetY décalage Y de la caméra
     */
    @Override
    public void draw(Graphics2D g2, int offsetX, int offsetY) {
        BufferedImage image;

        if (attacking) {
            image = "left".equals(lastHorizontal)
                    ? leftAttack[attackFrame]
                    : rightAttack[attackFrame];
        } else {
            String key = ("left".equals(direction) || "right".equals(direction))
                    ? direction + "_" + spriteNum
                    : direction + "_" + lastHorizontal;
            image = dirImages.get(key);
        }

        if (image != null) {
            g2.drawImage(image, (int) (positionX - offsetX), (int) (positionY - offsetY), width, height, null);
        }
    }

    /**
     * Charge les images du joueur.
     */
    public void getPlayerImage() {
        try {
            up_left = load("/Entity/player/Slime_jump_left.png");
            up_right = load("/Entity/player/Slime_jump_right.png");
            left1 = load("/Entity/player/Slime_left1.png");
            left2 = load("/Entity/player/Slime_left2.png");
            right1 = load("/Entity/player/Slime_right1.png");
            right2 = load("/Entity/player/Slime_right2.png");
            down_left = up_left;
            down_right = up_right;

            for (int i = 0; i < 4; i++) {
                leftAttack[i] = load("/Entity/player/Slime_attack_left" + i + ".png");
                rightAttack[i] = load("/Entity/player/Slime_attack_right" + i + ".png");
            }

            dirImages.put("left_1", left1);
            dirImages.put("left_2", left2);
            dirImages.put("right_1", right1);
            dirImages.put("right_2", right2);
            dirImages.put("up_left", up_left);
            dirImages.put("up_right", up_right);
            dirImages.put("down_left", down_left);
            dirImages.put("down_right", down_right);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private BufferedImage load(String path) throws Exception {
        return ImageIO.read(getClass().getResourceAsStream(path));
    }

    /**
     * Retourne la santé du joueur.
     * @return santé actuelle
     */
    @Override
    public int getHealth() {
        return super.getHealth();
    }

    /**
     * Retourne l'énergie du joueur.
     * @return énergie actuelle
     */
    public int getEnergy() {
        return energy;
    }

    /**
     * Retourne le panneau de jeu.
     * @return GamePanel
     */
    public GamePanel getGamePanel() {
        return gamePanel;
    }

    /**
     * Retourne les dégâts du joueur.
     * @return dégâts
     */
    public int getDamage() {
        return damage;
    }

    /**
     * Retourne la vitesse de mouvement.
     * @return vitesse
     */
    public int getSpeed() {
        return MOVE_SPEED;
    }

    /**
     * Retourne la position X.
     * @return position X
     */
    public int getX() {
        return (int) positionX;
    }

    /**
     * Retourne la position Y.
     * @return position Y
     */
    public int getY() {
        return (int) positionY;
    }

    /**
     * Définit les dégâts.
     * @param damage nouveaux dégâts
     */
    public void setDamage(int damage) {
        this.damage = damage;
    }

    /**
     * Définit la vitesse.
     * @param speed nouvelle vitesse
     */
    public void setSpeed(int speed) {
        MOVE_SPEED = speed;
    }

    /**
     * Définit l'énergie.
     * @param energy nouvelle énergie
     */
    public void setEnergy(int energy) {
        this.energy = energy;
    }

    /**
     * Retourne la dernière direction horizontale.
     * @return dernière direction
     */
    public String getLastHorizontal() {
        return lastHorizontal;
    }

    /**
     * Définit la dernière direction horizontale.
     * @param lastHorizontal nouvelle direction
     */
    public void setLastHorizontal(String lastHorizontal) {
        this.lastHorizontal = lastHorizontal;
    }
}
