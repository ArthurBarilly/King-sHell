package com.mypackage;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import com.mypackage.Enemies.SuperEnemy;
import com.mypackage.Npcs.NPC;

/**
 * Panneau principal du jeu, gérant la boucle de jeu, rendu et logique.
 */
public class GamePanel extends JPanel implements Runnable {

    protected final int tileSize;
    private final int screenWidth;
    private final int screenHeight;
    private static final int FPS = 60;

    private final KeyHandler keyHandler;
    private Thread gameThread;
    private final Player player;
    private final LevelManager levelManager;
    private TileMap currentMap;

    private final List<NPC> npcs = new ArrayList<>();
    private final List<SuperEnemy> monsters = new ArrayList<>();

    private final List<com.mypackage.objects.SuperObject> uiObjects = new ArrayList<>();

    protected final float gravity = 0.1f;
    private float cameraX = 0;
    private float cameraY = 0;

    private boolean levelChanging = false;

    /**
     * Constructeur du GamePanel, initialise les dimensions, joueur, maps et assets.
     */
    protected GamePanel() {
        final int originalTileSize = 16;
        final int scale = 2;
        this.tileSize = originalTileSize * scale;

        final int maxScreenCol = 16;
        final int maxScreenRow = 12;
        this.screenWidth = tileSize * maxScreenCol;
        this.screenHeight = tileSize * maxScreenRow;

        this.keyHandler = new KeyHandler();
        this.player = new Player(this, keyHandler, 6, 10, 100, 100);

        this.levelManager = new LevelManager(tileSize, tileSize);
        levelManager.addMap("/maps/mapTuto");
        levelManager.addMap("/maps/map1");
        levelManager.addMap("/maps/map2");

        levelManager.loadFirst();
        this.currentMap = levelManager.getCurrentMap();

        AssetSetter setter = new AssetSetter(this);
        setter.loadForCurrentMap();

        if (currentMap != null && currentMap.getSpawnX() >= 0 && currentMap.getSpawnY() >= 0) {
            player.setPosition(currentMap.getSpawnX(), currentMap.getSpawnY());
        }

        setupPanel();
    }

    private void setupPanel() {
        setPreferredSize(new Dimension(screenWidth, screenHeight));
        setBackground(Color.BLACK);
        setDoubleBuffered(true);
        addKeyListener(keyHandler);
        addMouseListener(keyHandler);
        setFocusable(true);
    }

    /**
     * Boucle principale du jeu, gérant les updates et le rendu à 60 FPS.
     */
    @Override
    public void run() {
        double drawInterval = 1_000_000_000.0 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();

        while (gameThread != null) {
            long currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }
        }
    }

    /**
     * Démarre le thread de jeu.
     */
    protected void startGameThread() {
        if (gameThread == null) {
            gameThread = new Thread(this);
            gameThread.start();
        }
    }

    /**
     * Crée un hazard au sol qui inflige des dégâts périodiques au joueur dans un rayon.
     * @param x position X du centre
     * @param y position Y du centre
     * @param radius rayon d'effet
     * @param duration durée en secondes
     * @param damage dégâts par tick
     */
    public void spawnGroundHazard(float x, float y, int radius, float duration, int damage) {

    new Thread(() -> {
        long start = System.currentTimeMillis();

        while ((System.currentTimeMillis() - start) < duration * 1000) {

            float dx = player.getPositionX() - x;
            float dy = player.getPositionY() - y;
            float distance = (float) Math.sqrt(dx * dx + dy * dy);

            if (distance <= radius) {
                player.takeDamage(damage);
            }

            try {
                Thread.sleep(50);
            } catch (InterruptedException ignored) {}
        }
    }).start();
}


    /**
     * Met à jour l'état du jeu : joueur, caméra, changement de niveau, entités.
     */
    private void update() {
        player.update();

        if (currentMap != null) {

            boolean onExit = currentMap.isExitReached(
                    player.getPositionX(),
                    player.getPositionY(),
                    player.getWidth(),
                    player.getHeight()
            );

            if (onExit && !levelChanging && levelManager.hasNext()) {
                levelChanging = true;

                npcs.clear();
                monsters.clear();

                boolean ok = levelManager.nextMap();
                currentMap = levelManager.getCurrentMap();

                AssetSetter setter = new AssetSetter(this);
                setter.loadForCurrentMap();

                if (ok && currentMap != null) {
                    Rectangle spawn = currentMap.getSpawnRect();
                    if (spawn != null) {
                        player.setPosition(
                                spawn.x + spawn.width / 2,
                                spawn.y + spawn.height / 2
                        );
                    } else {
                        player.setPosition(
                                currentMap.getSpawnX(),
                                currentMap.getSpawnY()
                        );
                    }
                }
            }

            if (!onExit) {
                levelChanging = false;
            }

            int mapWidth = currentMap.getMapCols() * currentMap.getTileWidth();
            int mapHeight = currentMap.getMapRows() * currentMap.getTileHeight();

            cameraX = Math.max(
                    0,
                    Math.min(player.getPositionX() - screenWidth / 2, mapWidth - screenWidth)
            );

            cameraY = Math.max(
                    0,
                    Math.min(player.getPositionY() - screenHeight / 2, mapHeight - screenHeight)
            );
        }

        for (NPC n : npcs) {
            n.update();
        }

        for (SuperEnemy m : monsters) {
            m.update();
        }
    }

    /**
     * Dessine tous les éléments du jeu : map, joueur, entités, UI.
     * @param g Graphics pour le rendu
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        if (currentMap != null) {
            currentMap.draw(g2, (int) cameraX, (int) cameraY);
        }

        player.draw(g2, (int) cameraX, (int) cameraY);

        for (NPC n : npcs) {
            n.draw(g2, (int) cameraX, (int) cameraY);
        }

        for (SuperEnemy m : monsters) {
            m.draw(g2, (int) cameraX, (int) cameraY);
        }

        for (com.mypackage.objects.SuperObject obj : uiObjects) {
            obj.draw(g2, this);
        }

        g2.dispose();
    }

    /**
     * Retourne la map actuelle.
     * @return la TileMap actuelle
     */
    protected TileMap getCurrentMap() {
        return currentMap;
    }

    /**
     * Retourne la taille des tiles.
     * @return taille des tiles
     */
    public int getTileSize() {
        return tileSize;
    }

    /**
     * Retourne la hauteur de l'écran.
     * @return hauteur de l'écran
     */
    public int getScreenHeight() {
        return screenHeight;
    }

    /**
     * Retourne la largeur de l'écran.
     * @return largeur de l'écran
     */
    public int getScreenWidth() {
        return screenWidth;
    }

    /**
     * Retourne le joueur.
     * @return l'instance du joueur
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Retourne le gestionnaire de touches.
     * @return KeyHandler
     */
    public KeyHandler getKeyHandler() {
        return keyHandler;
    }

    /**
     * Retourne le gestionnaire de niveaux.
     * @return LevelManager
     */
    public LevelManager getLevelManager() {
        return levelManager;
    }

    /**
     * Retourne la liste des monstres.
     * @return liste des SuperEnemy
     */
    public List<SuperEnemy> getMonsters() {
        return monsters;
    }

    /**
     * Retourne la liste des NPCs.
     * @return liste des NPCs
     */
    public List<NPC> getNpcs() {
        return npcs;
    }

    /**
     * Retourne la liste des objets UI.
     * @return liste des objets UI
     */
    public List<com.mypackage.objects.SuperObject> getUiObjects() {
        return uiObjects;
    }

    /**
     * Retourne la position X de la caméra.
     * @return position X de la caméra
     */
    public float getCameraX() {
        return cameraX;
    }

    /**
     * Retourne la position Y de la caméra.
     * @return position Y de la caméra
     */
    public float getCameraY() {
        return cameraY;
    }

    /**
     * Retourne la gravité du jeu.
     * @return valeur de la gravité
     */
    public float getGravity() {
        return gravity;
    }

    /**
     * Retourne le chemin de la map actuelle.
     * @return chemin de la map
     */
    public String getMapPath() {
        return levelManager.getMapPath();
    }
}
