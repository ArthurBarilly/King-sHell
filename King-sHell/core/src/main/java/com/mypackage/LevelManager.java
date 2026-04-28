package com.mypackage;

import java.util.ArrayList;
import java.util.List;

/**
 * Gestionnaire des niveaux du jeu, permettant de charger et naviguer entre les cartes.
 */
public class LevelManager {

    private final List<String> maps = new ArrayList<>();
    private int current = -1;
    private TileMap currentMap;

    private final int tileWidth;
    private final int tileHeight;

    /**
     * Constructeur de LevelManager.
     * @param tileWidth largeur des tuiles
     * @param tileHeight hauteur des tuiles
     */
    public LevelManager(int tileWidth, int tileHeight) {
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
    }

    /**
     * Ajoute une carte à la liste des niveaux.
     * @param mapPath chemin de la carte
     */
    public void addMap(String mapPath) {
        maps.add(mapPath);
    }

    /**
     * Charge la première carte.
     * @return true si chargée avec succès
     */
    public boolean loadFirst() {
        if (maps.isEmpty()) return false;
        current = 0;
        currentMap = TileMap.load(maps.get(current), tileWidth, tileHeight);
        return currentMap != null;
    }

    /**
     * Vérifie s'il y a une carte suivante.
     * @return true s'il y a une suivante
     */
    public boolean hasNext() {
        return current + 1 < maps.size();
    }

    /**
     * Charge la carte suivante.
     * @return true si chargée avec succès
     */
    public boolean nextMap() {
        if (!hasNext()) return false;
        current++;
        currentMap = TileMap.load(maps.get(current), tileWidth, tileHeight);
        return currentMap != null;
    }

    /**
     * Retourne la carte actuelle.
     * @return carte actuelle
     */
    public TileMap getCurrentMap() {
        return currentMap;
    }

    /**
     * Retourne le chemin de la carte actuelle.
     * @return chemin de la carte
     */
    public String getMapPath() {
        return (current >= 0 && current < maps.size()) ? maps.get(current) : null;
    }
    
}
