package com.mypackage.Enemies;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.mypackage.GamePanel;
import com.mypackage.Player;

/**
 * Boss Hades avec deux phases, attaques physiques et sorts.
 */
public class Hades extends SuperEnemy {

    protected BufferedImage[] leftAttack = new BufferedImage[5];
    protected BufferedImage[] rightAttack = new BufferedImage[5];
    protected BufferedImage[] leftSpell = new BufferedImage[5];
    protected BufferedImage[] rightSpell = new BufferedImage[5];

    private boolean alive = true;
    private boolean trueForm = false;
    private boolean invincible = false;

    private boolean castingSpell = false;
    private boolean attacking = false;
    private boolean attackHitDone = false;

    private int attackCounter = 0;
    private int attackFrame = 0;
    private int spellCounter = 0;
    private int spellCooldown = 0;
    private int phaseTransitionCounter = 0;

    private static final float STOP_DISTANCE = 90f;
    private static final float DIRECTION_TOLERANCE = 10f;
    private boolean isMoving = false;

    /**
     * Constructeur de Hades.
     * @param gp le panneau de jeu
     * @param player le joueur
     */
    public Hades(GamePanel gp, Player player) {
        super(gp, player, 0, 0, 128, 256, 10, 50);
        loadImages();
    }

    /**
     * Met à jour l'état de Hades : agro, attaques, sorts, phases.
     */
    @Override
    public void update() {
        if (!alive || player == null) return;

        if (invincible) {
            phaseTransitionCounter++;
            if (phaseTransitionCounter > 40) {
                invincible = false;
                attacking = false;
                castingSpell = false;
                attackHitDone = false;
                attackCounter = 0;
                spellCounter = 0;
                attackFrame = 0;
                attackDelayCounter = 0;
            }
            return;
        }

        updateAgro();

        if (!attacking && !castingSpell) setAction();

        if (attacking) updateAttack();
        else if (castingSpell) updateSpell();
        else updateWalk();

        checkPhaseChange();
        checkDeath();
    }

    /**
     * Met à jour l'agro et le mouvement de Hades.
     */
    private void updateAgro() {
        float dx = player.positionX - positionX;
        float distance = Math.abs(dx);

        agro = distance < 250;

        if (dx > DIRECTION_TOLERANCE) directionEnemy = "right";
        else if (dx < -DIRECTION_TOLERANCE) directionEnemy = "left";

        if (spellCooldown > 0) spellCooldown--;

        if (agro && distance > STOP_DISTANCE && !attacking && !castingSpell) {
            float moveSpeed = speed * 0.4f;
            move(dx > 0 ? moveSpeed : -moveSpeed, 0);
            isMoving = true;
        } else {
            isMoving = false;
        }

        if (agro && distance <= STOP_DISTANCE && !attacking && !castingSpell) {
            attackDelayCounter++;
            if (attackDelayCounter >= ATTACK_DELAY) {
                chooseAction();
                attackDelayCounter = 0;
            }
        } else {
            attackDelayCounter = 0;
        }
    }

    /**
     * Choisit entre une attaque physique ou un sort.
     */
    private void chooseAction() {
        if (trueForm && spellCooldown == 0 && Math.random() < 0.4) {
            startSpell();
        } else {
            startAttack();
        }
    }

    /**
     * Démarre une attaque physique.
     */
    private void startAttack() {
        attacking = true;
        attackCounter = 0;
        attackFrame = 0;
        attackHitDone = false;
    }

    /**
     * Met à jour l'animation et les dégâts de l'attaque.
     */
    private void updateAttack() {
        attackCounter++;

        if (attackCounter % 8 == 0) attackFrame = (attackFrame + 1) % 5;

        if (!attackHitDone && attackCounter >= 20) {
            float dx = player.positionX - positionX;
            float distance = Math.abs(dx);

            if (distance <= STOP_DISTANCE) {
                player.takeDamage(damage);
            }

            attackHitDone = true;
        }

        if (attackCounter > 40) attacking = false;
    }

    /**
     * Démarre un sort.
     */
    private void startSpell() {
        castingSpell = true;
        spellCounter = 0;
        spellCooldown = 140;
        attackFrame = 0;
    }

    /**
     * Met à jour l'animation du sort.
     */
    private void updateSpell() {
        spellCounter++;

        if (spellCounter == 10) dealSpellDamage();

        if (spellCounter % 6 == 0) attackFrame = (attackFrame + 1) % 5;

        if (spellCounter > 35) castingSpell = false;
    }

    /**
     * Inflige les dégâts du sort au joueur et crée un hazard au sol.
     */
    private void dealSpellDamage() {
        float dx = player.positionX - positionX;
        float dy = player.positionY - positionY;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        if (distance <= 120) player.takeDamage(damage * 2);

        gamePanel.spawnGroundHazard(positionX, positionY, 120, 0.25f, damage);
    }

    /**
     * Met à jour l'animation de marche.
     */
    private void updateWalk() {
        if (isMoving) {
            walkCounter++;
            if (walkCounter % 10 == 0) walkFrame = (walkFrame + 1) % 2;
        } else {
            walkFrame = 0;
        }
    }

    /**
     * Dessine Hades à l'écran.
     * @param g2 contexte graphique
     * @param offsetX décalage X de la caméra
     * @param offsetY décalage Y de la caméra
     */
    @Override
    public void draw(Graphics2D g2, int offsetX, int offsetY) {
        if (!alive) return;

        BufferedImage image = null;

        if (castingSpell) {
            image = directionEnemy.equals("left") ? leftSpell[attackFrame] : rightSpell[attackFrame];
        } else if (attacking) {
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
     * Vérifie et gère le changement de phase.
     */
    private void checkPhaseChange() {
        if (!trueForm && getHealth() <= 0) {
            trueForm = true;
            invincible = true;
            phaseTransitionCounter = 0;

            setHealth(getMaxHealth());
            damage += 2;
            speed += 1;
        }
    }

    /**
     * Vérifie la mort définitive en phase 2.
     */
    private void checkDeath() {
        if (trueForm && getHealth() <= 0) alive = false;
    }

    /**
     * Charge les images de Hades.
     */
    @Override
    protected void loadImages() {
        right[0] = loadOrPlaceholder("/Entity/enemy/Hades_right.png", java.awt.Color.GREEN);
        right[1] = loadOrPlaceholder("/Entity/enemy/Hades_right1.png", java.awt.Color.GREEN);

        left[0] = loadOrPlaceholder("/Entity/enemy/Hades_left.png", java.awt.Color.MAGENTA);
        left[1] = loadOrPlaceholder("/Entity/enemy/Hades_left1.png", java.awt.Color.MAGENTA);

        for (int i = 0; i < 5; i++) {
            leftAttack[i] = loadOrPlaceholder("/Entity/enemy/Hades_left_attack_" + (i + 1) + ".png", java.awt.Color.BLUE);
            rightAttack[i] = loadOrPlaceholder("/Entity/enemy/Hades_right_attack_" + (i + 1) + ".png", java.awt.Color.BLUE);

            leftSpell[i] = loadOrPlaceholder("/Entity/enemy/Hades_left_spell" + (i + 1) + ".png", java.awt.Color.RED);
            rightSpell[i] = loadOrPlaceholder("/Entity/enemy/Hades_right_spell" + (i + 1) + ".png", java.awt.Color.RED);
        }
    }
}
