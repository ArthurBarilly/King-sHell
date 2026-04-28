package com.mypackage;

import com.mypackage.Enemies.Gromp;
import com.mypackage.Enemies.Hades;
import com.mypackage.Npcs.NPC_map1;
import com.mypackage.Npcs.NPC_map2;
import com.mypackage.Npcs.NPC_tutorial;
import com.mypackage.Npcs.NPC_tutorial1;
import com.mypackage.objects.OBJ_Energy;
import com.mypackage.objects.OBJ_Heart;

/**
 * Classe responsable du chargement des assets (NPCs, monstres, objets) pour chaque map.
 */
public class AssetSetter {

    private final GamePanel gp;

    /**
     * Constructeur d'AssetSetter.
     * @param gp le panneau de jeu
     */
    public AssetSetter(GamePanel gp) {
        this.gp = gp;
    }

    /**
     * Charge les assets pour la map actuelle.
     */
    public void loadForCurrentMap() {

        if (gp.getCurrentMap() == null) return;

        gp.getNpcs().clear();
        gp.getMonsters().clear();

        String mapPath = gp.getLevelManager().getMapPath();

        if ("/maps/mapTuto".equals(mapPath)) {
            loadMapTuto();
        }
        else if ("/maps/map1".equals(mapPath)) {
            loadMap1();
        }
        else if ("/maps/map2".equals(mapPath)) {
            loadMap2();
        }
        else {
            System.out.println("⚠ Map inconnue : " + mapPath);
        }
    }

    /**
     * Charge les assets pour la map tutoriel.
     */
    private void loadMapTuto() {
        NPC_tutorial npc1 = new NPC_tutorial(gp, gp.getPlayer());
        npc1.positionX = 620;
        npc1.positionY = 120;
        gp.getNpcs().add(npc1);

        NPC_tutorial1 npc2 = new NPC_tutorial1(gp, gp.getPlayer());
        npc2.positionX = 50;
        npc2.positionY = 180;
        gp.getNpcs().add(npc2);

        Gromp gromp = new Gromp(gp, gp.getPlayer());
        gromp.positionX = 300;
        gromp.positionY = 220;
        gp.getMonsters().add(gromp);

        gp.getUiObjects().add(new OBJ_Heart(gp));
        gp.getUiObjects().add(new OBJ_Energy(gp));

        System.out.println("mapTuto générée");
    }

    /**
     * Charge les assets pour la map 1.
     */
    private void loadMap1() {
        NPC_map1 npc3 = new NPC_map1(gp, gp.getPlayer());
        npc3.positionX = 130;
        npc3.positionY = 570;
        gp.getNpcs().add(npc3);

        NPC_map2 npc4 = new NPC_map2(gp, gp.getPlayer());
        npc4.positionX = 890;
        npc4.positionY = 65;
        gp.getNpcs().add(npc4);

        Gromp gromp1 = new Gromp(gp, gp.getPlayer());
        gromp1.positionX = 500;
        gromp1.positionY = 255;
        gp.getMonsters().add(gromp1);

        Gromp gromp2 = new Gromp(gp, gp.getPlayer());
        gromp2.positionX = 190;
        gromp2.positionY = 335;
        gp.getMonsters().add(gromp2);

        gp.getUiObjects().add(new OBJ_Heart(gp));

        System.out.println("map 1générée");
    }

    /**
     * Charge les assets pour la map 2.
     */
    private void loadMap2() {
        Hades hades = new Hades(gp, gp.getPlayer());
        hades.positionX = 200;
        hades.positionY = 80;
        gp.getMonsters().add(hades);
        System.out.println("map 2 générée");
    }
}
