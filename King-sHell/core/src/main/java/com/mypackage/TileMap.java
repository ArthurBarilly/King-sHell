package com.mypackage;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

/**
 * Classe pour gérer les cartes de tuiles, incluant le chargement des tiles, collisions et objets.
 */
public class TileMap {
    private final int tileWidth;
    private final int tileHeight;
    private int mapCols;
    private int mapRows;
    private int[] gids;
    private BufferedImage[] tiles;

    private int spawnX = 100, spawnY = 100;
    private int exitX = -1, exitY = -1, exitW = 0, exitH = 0;

    private List<Rectangle> groundObjects = new ArrayList<>();
    private List<Rectangle> solidObjects = new ArrayList<>();
    private List<Rectangle> spawnObjects = new ArrayList<>();

    private int[][] collisionMap;

    private TileMap(int tileW, int tileH) {
        this.tileWidth = tileW;
        this.tileHeight = tileH;
    }

    /**
     * Charge une TileMap depuis les ressources spécifiées.
     * @param mapResourceBase le chemin de base des ressources de la map
     * @param tileW la largeur des tiles
     * @param tileH la hauteur des tiles
     * @return la TileMap chargée
     */
    public static TileMap load(String mapResourceBase, int tileW, int tileH) {
        TileMap tm = new TileMap(tileW, tileH);

        BufferedImage tileset = null;
        try (InputStream is = TileMap.class.getResourceAsStream(mapResourceBase + "/tileset.png")) {
            if (is != null) tileset = ImageIO.read(is);
        } catch (Exception ignored) {
            System.out.println("TileMap.load: failed to load tileset from " + mapResourceBase + "/tileset.png");
        }

        if (tileset == null) {
            try (InputStream is = TileMap.class.getResourceAsStream(mapResourceBase + "/tileset.png")) {
                if (is != null) tileset = ImageIO.read(is);
            } catch (Exception ignored) {}
        }
        List<Integer> gidList = new ArrayList<>();
        try (InputStream is = TileMap.class.getResourceAsStream(mapResourceBase + "/layer0.csv")) {
            if (is != null) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
                    String line;
                    int cols = -1, rows = 0;
                    while ((line = br.readLine()) != null) {
                        String[] parts = line.trim().split(",");
                        if (parts.length == 1 && parts[0].isEmpty()) continue;
                        if (cols == -1) cols = parts.length;
                        for (String p : parts) {
                            gidList.add(Integer.parseInt(p.trim()));
                        }
                        rows++;
                    }
                    if (cols > 0) {
                        tm.mapCols = cols;
                        tm.mapRows = rows;
                    }
                }
            }
        } catch (Exception ignored) { }

        try (InputStream is = TileMap.class.getResourceAsStream(mapResourceBase + "/meta.txt")) {
            if (is != null) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        line = line.trim();
                        if (line.startsWith("spawn=")) {
                            String[] parts = line.substring(6).split(",");
                            tm.spawnX = Integer.parseInt(parts[0].trim());
                            tm.spawnY = Integer.parseInt(parts[1].trim());
                        }
                        if (line.startsWith("exit=")) {
                            String[] parts = line.substring(5).split(",");
                            tm.exitX = Integer.parseInt(parts[0].trim());
                            tm.exitY = Integer.parseInt(parts[1].trim());
                            tm.exitW = Integer.parseInt(parts[2].trim());
                            tm.exitH = Integer.parseInt(parts[3].trim());
                        }
                    }
                }
            }
        } catch (Exception ignored) {}

        if (!gidList.isEmpty()) {
            tm.gids = gidList.stream().mapToInt(Integer::intValue).toArray();
        } else {
            tm.mapCols = 10; tm.mapRows = 8; tm.gids = new int[tm.mapCols * tm.mapRows];
        }
        try (InputStream tmxIs = TileMap.class.getResourceAsStream(mapResourceBase + "/maptuto.tmx")) {
            if (tmxIs != null) {
                StringBuilder sb = new StringBuilder();
                try (BufferedReader br = new BufferedReader(new InputStreamReader(tmxIs))) {
                    String l;
                    while ((l = br.readLine()) != null) sb.append(l).append('\n');
                }
                String xml = sb.toString();
                int dataStart = xml.indexOf("<data");
                int dataEnd = xml.indexOf("</data>");
                if (dataStart >= 0 && dataEnd > dataStart) {
                    int gt = xml.indexOf('>', dataStart);
                    if (gt > dataStart) {
                        String inner = xml.substring(gt + 1, dataEnd).trim();
                        String[] lines = inner.split("\\r?\\n");
                        List<Integer> tmxList = new ArrayList<>();
                        int tmxCols = -1, tmxRows = 0;
                        for (String line : lines) {
                            String[] parts = line.trim().split(",");
                            if (parts.length == 1 && parts[0].isEmpty()) continue;
                            if (tmxCols == -1) tmxCols = parts.length;
                            for (String p : parts) tmxList.add(Integer.parseInt(p.trim()));
                            tmxRows++;
                        }
                        if (!tmxList.isEmpty()) {
                            int[] tmxGids = tmxList.stream().mapToInt(Integer::intValue).toArray();
                            int maxCsv = Integer.MIN_VALUE; for (int v : tm.gids) maxCsv = Math.max(maxCsv, v);
                            int maxTmx = Integer.MIN_VALUE; for (int v : tmxGids) maxTmx = Math.max(maxTmx, v);
                            if (maxTmx > maxCsv) {
                                tm.gids = tmxGids;
                                tm.mapCols = (tmxCols > 0) ? tmxCols : tm.mapCols;
                                tm.mapRows = (tmxRows > 0) ? tmxRows : tm.mapRows;
                                System.out.println("TileMap.load: using TMX layer data instead of CSV (TMX appeared more complete)");
                            }
                        }
                    }
                }
            }
        } catch (Exception ignored) {}

        tm.collisionMap = new int[tm.mapRows][tm.mapCols];
        try (InputStream is = TileMap.class.getResourceAsStream(mapResourceBase + "/layer0.csv")) {
            if (is != null) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
                    String line;
                    int row = 0;
                    while ((line = br.readLine()) != null && row < tm.mapRows) {
                        String[] parts = line.trim().split(",");
                        for (int col = 0; col < parts.length && col < tm.mapCols; col++) {
                            tm.collisionMap[row][col] = Integer.parseInt(parts[col].trim());
                        }
                        row++;
                    }
                }
            }
        } catch (Exception ignored) {}

        try {
            System.out.println("TileMap.load: base=" + mapResourceBase + " cols=" + tm.mapCols + " rows=" + tm.mapRows + " gids=" + (tm.gids != null ? tm.gids.length : 0) + " tileset=" + (tileset != null));
        } catch (Exception ignored) {}

        if (tileset != null) {
            int cols = tileset.getWidth() / tileW;
            int rows = tileset.getHeight() / tileH;
            tm.tiles = new BufferedImage[cols * rows];
            int idx = 0;
            for (int y = 0; y < rows; y++) {
                for (int x = 0; x < cols; x++) {
                    tm.tiles[idx++] = tileset.getSubimage(x * tileW, y * tileH, tileW, tileH);
                }
            }
        } else {
            tm.tiles = new BufferedImage[1];
            BufferedImage bi = new BufferedImage(tileW, tileH, BufferedImage.TYPE_INT_ARGB);
            java.awt.Graphics2D g = bi.createGraphics();
            g.setColor(java.awt.Color.GRAY); g.fillRect(0,0,tileW,tileH);
            g.dispose();
            tm.tiles[0] = bi;
        }

        try (InputStream is = TileMap.class.getResourceAsStream(mapResourceBase + "/maptuto.tmx")) {
            if (is != null) {
                StringBuilder sb = new StringBuilder();
                try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
                    String l;
                    while ((l = br.readLine()) != null) sb.append(l).append('\n');
                }
                String xml = sb.toString();
                System.out.println("TileMap.load: TMX loaded for objects, length=" + xml.length());
                int objGroupStart = 0;
                while ((objGroupStart = xml.indexOf("<objectgroup", objGroupStart)) >= 0) {
                    int objGroupEnd = xml.indexOf("</objectgroup>", objGroupStart);
                    if (objGroupEnd < 0) break;
                    String objGroupXml = xml.substring(objGroupStart, objGroupEnd + 14);
                    System.out.println("TileMap.load: found objectgroup");
                    int objStart = 0;
                    while ((objStart = objGroupXml.indexOf("<object ", objStart)) >= 0) {
                        int objEnd = objGroupXml.indexOf("</object>", objStart) + 9;
                        if (objEnd <= objStart) break;
                        String objXml = objGroupXml.substring(objStart, objEnd);
                        int x = 0, y = 0, w = 0, h = 0;
                        try {
                            int xStart = objXml.indexOf("x=\"");
                            if (xStart >= 0) {
                                int xEnd = objXml.indexOf("\"", xStart + 3);
                                x = (int) Double.parseDouble(objXml.substring(xStart + 3, xEnd));
                            }
                            int yStart = objXml.indexOf("y=\"");
                            if (yStart >= 0) {
                                int yEnd = objXml.indexOf("\"", yStart + 3);
                                y = (int) Double.parseDouble(objXml.substring(yStart + 3, yEnd));
                            }
                            int wStart = objXml.indexOf("width=\"");
                            if (wStart >= 0) {
                                int wEnd = objXml.indexOf("\"", wStart + 7);
                                w = (int) Double.parseDouble(objXml.substring(wStart + 7, wEnd));
                            }
                            int hStart = objXml.indexOf("height=\"");
                            if (hStart >= 0) {
                                int hEnd = objXml.indexOf("\"", hStart + 8);
                                h = (int) Double.parseDouble(objXml.substring(hStart + 8, hEnd));
                            }
                        } catch (Exception e) {
                            System.out.println("TileMap.load: error parsing object coords: " + e.getMessage());
                        }

                        // Chercher les Booleens utiles
                        boolean isGround = objXml.contains("name=\"ground\"") && objXml.contains("value=\"true\"");
                        boolean isSolid  = objXml.contains("name=\"solid\"")  && objXml.contains("value=\"true\"");
                        boolean isSpawn  = objXml.contains("name=\"spawn\"")  && objXml.contains("value=\"true\"");
                        boolean nextLvl = objXml.contains("name=\"nextLvl\"")   && objXml.contains("value=\"true\"");
                        if (isGround) {
                            tm.groundObjects.add(new Rectangle(x, y, w, h));
                            System.out.println("TileMap.load: added ground at (" + x + "," + y + ") size " + w + "x" + h);
                        }
                        if (isSolid) {
                            tm.solidObjects.add(new Rectangle(x, y, w, h));
                            System.out.println("TileMap.load: added solid at (" + x + "," + y + ") size " + w + "x" + h);
                        }
                        if (isSpawn) {
                            tm.spawnObjects.add(new Rectangle(x, y, w, h));
                            System.out.println("TileMap.load: added spawn at (" + x + "," + y + ") size " + w + "x" + h);
                        }
                        if (nextLvl) {
                            tm.exitX = x; tm.exitY = y; tm.exitW = w; tm.exitH = h;
                            System.out.println("TileMap.load: set exit at (" + x + "," + y + ") size " + w + "x" + h);
                        }
                        objStart = objEnd;
                        
                    }
                    objGroupStart = objGroupEnd + 14;
                }
            } else {
                System.out.println("TileMap.load: TMX not found for objects at " + mapResourceBase + "/maptuto.tmx");
            }
        } catch (Exception e) {
            System.out.println("TileMap.load: error parsing TMX for objects: " + e.getMessage());
        }

        return tm;
    }

    public void draw(Graphics2D g2, int offsetX, int offsetY) {
        if (gids == null) return;
        int startX = Math.max(0, (int) Math.ceil(offsetX / (double) tileWidth));
        int startY = Math.max(0, (int) Math.ceil(offsetY / (double) tileHeight));
        int endX = Math.min(mapCols - 1, (int) Math.floor((offsetX + g2.getClipBounds().width) / (double) tileWidth));
        int endY = Math.min(mapRows - 1, (int) Math.floor((offsetY + g2.getClipBounds().height) / (double) tileHeight));

        for (int y = startY; y <= endY; y++) {
            for (int x = startX; x <= endX; x++) {
                int rawGid = gids[y * mapCols + x];
                int gid = rawGid & 0x1FFFFFFF;
                if (gid <= 0) continue;
                int idx = gid - 1;
                if (idx < 0 || idx >= tiles.length) {
                    System.err.println("TileMap.draw: GID " + gid + " at (" + x + "," + y + ") out of range (tile count=" + tiles.length + ")");
                    continue;
                }
                BufferedImage tileImg = tiles[idx];
                int px = x * tileWidth - offsetX;
                int py = y * tileHeight - offsetY;
                g2.drawImage(tileImg, px, py, null);
            }
        }
        /*
        // Debug pour les Hitbox
        g2.setColor(java.awt.Color.RED);
        for (Rectangle obj : solidObjects) {
            int px = obj.x - offsetX;
            int py = obj.y - offsetY;
            g2.drawRect(px, py, obj.width, obj.height);
        }
        g2.setColor(java.awt.Color.GREEN);
        for (Rectangle obj : groundObjects) {
            int px = obj.x - offsetX;
            int py = obj.y - offsetY;
            g2.drawRect(px, py, obj.width, obj.height);
        }
        */
    }

    /**
     * Retourne la position X du spawn par défaut.
     * @return la position X du spawn
     */
    public int getSpawnX() { 
        return spawnX; 
    }

    /**
     * Retourne la position Y du spawn par défaut.
     * @return la position Y du spawn
     */
    public int getSpawnY() { 
        return spawnY; 
    }

    public boolean isExitReached(float px, float py, int width, int height) {
        int x = (int) px, y = (int) py, w = width, h = height;
        int startTx = Math.max(0, x / tileWidth);
        int startTy = Math.max(0, y / tileHeight);
        int endTx = Math.min(mapCols - 1, (x + w - 1) / tileWidth);
        int endTy = Math.min(mapRows - 1, (y + h - 1) / tileHeight);
        for (int ty = startTy; ty <= endTy; ty++) {
            for (int tx = startTx; tx <= endTx; tx++) {
                if (collisionMap[ty][tx] == 3) {
                    return true;
                }
            }
        }
        if (exitX < 0) return false;
        int ex = exitX; int ey = exitY; int ew = exitW; int eh = exitH;
        return px < (ex+ew) && (px+width) > ex && py < (ey+eh) && (py+height) > ey;
    }

    /**
     * Vérifie si la zone spécifiée est sur un sol.
     * @param x position X
     * @param y position Y
     * @param w largeur
     * @param h hauteur
     * @return true si c'est du sol
     */
    public boolean isGround(int x, int y, int w, int h) {
        int playerBottom = y + h;
        int playerLeft = x;
        int playerRight = x + w;
        int startTx = Math.max(0, playerLeft / tileWidth);
        int endTx = Math.min(mapCols - 1, (playerRight - 1) / tileWidth);
        int ty = playerBottom / tileHeight;
        if (ty >= 0 && ty < mapRows) {
            for (int tx = startTx; tx <= endTx; tx++) {
                if (collisionMap[ty][tx] == 2) {
                    return true;
                }
            }
        }
        Rectangle playerRect = new Rectangle(x, y, w, h);
        for (Rectangle obj : groundObjects) {
            int groundTop = obj.y;
            int groundBottom = obj.y + obj.height;
            int groundLeft = obj.x;
            int groundRight = obj.x + obj.width;
            if (playerBottom >= groundTop && playerBottom <= groundBottom && playerRight > groundLeft && playerLeft < groundRight) {
                return true;
            }
        }
        if (playerBottom >= mapRows * tileHeight) {
            return true;
        }

        return false;
    }

    /**
     * Vérifie si la zone spécifiée intersecte un objet solide.
     * @param x position X
     * @param y position Y
     * @param w largeur
     * @param h hauteur
     * @return true si collision avec un solide
     */
    public boolean isSolid(int x, int y, int w, int h) {
        Rectangle playerRect = new Rectangle(x, y, w, h);
        for (Rectangle obj : solidObjects) {
            if (obj.intersects(playerRect)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Vérifie si la zone spécifiée intersecte une zone de spawn.
     * @param x position X
     * @param y position Y
     * @param w largeur
     * @param h hauteur
     * @return true si dans une zone de spawn
     */
    public boolean isSpawn(int x, int y, int w, int h) {
        Rectangle playerRect = new Rectangle(x, y, w, h);
        for (Rectangle obj : spawnObjects) {
            if (obj.intersects(playerRect)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retourne la largeur d'une tile.
     * @return la largeur des tiles
     */
    public int getTileWidth() { 
        return tileWidth; 
    }

    /**
     * Retourne la hauteur d'une tile.
     * @return la hauteur des tiles
     */
    public int getTileHeight() { 
        return tileHeight; 
    }

    /**
     * Retourne le nombre de colonnes de la map.
     * @return le nombre de colonnes
     */
    public int getMapCols() { 
        return mapCols; 
    }

    /**
     * Retourne le nombre de lignes de la map.
     * @return le nombre de lignes
     */
    public int getMapRows() { 
        return mapRows; 
    }

    /**
     * Retourne le rectangle du premier spawn trouvé.
     * @return le rectangle de spawn ou null
     */
    public Rectangle getSpawnRect() {
        if (!spawnObjects.isEmpty()) {
            return spawnObjects.get(0);
        }
        return null;
    }
}
