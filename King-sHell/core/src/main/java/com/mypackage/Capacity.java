package com.mypackage;

/**
 * Classe gérant les capacités spéciales du joueur avec cooldown et coût en énergie.
 */
class Capacity {
    private int cooldown;
    private int energyRequired;
    private long lastUseTime;

    /**
     * Constructeur de Capacity.
     * @param cooldown temps de recharge en millisecondes
     * @param energyRequired énergie requise
     */
    public Capacity(int cooldown, int energyRequired) {
        this.cooldown = cooldown;
        this.energyRequired = energyRequired;
        this.lastUseTime = 0;
    }

    /**
     * Vérifie si la capacité peut être utilisée.
     * @param p le joueur
     * @return true si utilisable
     */
    private boolean canUse(Player p) {
        long now = System.currentTimeMillis();
        return p.hasEnergy(energyRequired) && (now - lastUseTime) >= cooldown;
    }

    /**
     * Active la capacité en consommant l'énergie.
     * @param p le joueur
     */
    private void activate(Player p) {
        lastUseTime = System.currentTimeMillis();
        p.consumeEnergy(energyRequired);
    }

    /**
     * Effectue un double saut.
     * @param p le joueur
     */
    public void doubleJump(Player p) {
        if (canUse(p)) {
            activate(p);
            p.velocityY = -5;
            p.move(0, p.velocityY);
            p.grounded = false;
            System.out.println("Double saut");
        } else {
            System.out.println("Pas assez d'énergie ou cooldown actif");
        }
    }

    /**
     * Effectue un dash.
     * @param p le joueur
     * @param toRight direction du dash
     */
    public void dash(Player p, boolean toRight) {
        if (canUse(p)) {
            activate(p);
            float dashDistance = 90f;
            float newX = p.getPositionX() + (toRight ? dashDistance : -dashDistance);
            if (p.getGamePanel() != null && p.getGamePanel().getCurrentMap() != null && !p.getGamePanel().getCurrentMap().isSolid((int)newX, (int)p.getPositionY(), p.getWidth(), p.getHeight())) {
                p.move(toRight ? dashDistance : -dashDistance, 0);
                System.out.println("Dash " + (toRight ? "à droite" : "à gauche") + " !");
            } else {
                System.out.println("Dash bloqué !");
            }
        } else {
            System.out.println("Pas assez d'énergie ou cooldown actif");
        }

    }

    /**
     * Soigne le joueur.
     * @param p le joueur
     */
    public void heal(Player p) {
        if (canUse(p) && p.getHealth() < p.getMaxHealth()) {
            activate(p);
            int healAmount = 2;
            p.heal(healAmount);
            System.out.println("Soin de " + healAmount + " points de vie !");
        } else {
            System.out.println("Pas assez d'énergie ou cooldown actif");
        }            
    }

    /**
     * Augmente les stats du joueur.
     * @param p le joueur
     */
    public void boost(Player p){
        if (canUse(p) && p.getEnergy() >= 3) {
            p.setEnergy(p.getEnergy() - 3);
            activate(p);
            p.setDamage(p.getDamage() + 5);
            System.out.println("Dégâts augmentés, dmg = " + p.getDamage());
            p.setSpeed(p.getSpeed() + 2);
            System.out.println("Vitesse augmentée, speed = " + p.getSpeed());
        } else {
            System.out.println("Pas assez d'énergie ou cooldown actif");
        }            
    }
}