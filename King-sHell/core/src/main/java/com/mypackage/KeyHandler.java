package com.mypackage;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Gestionnaire des événements clavier et souris pour le jeu.
 */
public class KeyHandler implements KeyListener, MouseListener {

    protected boolean upPressed, downPressed, leftPressed, rightPressed, boostPressed;
    protected boolean attackPressed;
    protected boolean healPressed = false;
    protected boolean interactPressed = false;
    protected boolean doubleJumpPressed = false;
    protected boolean dashPressed = false;

    /**
     * Méthode appelée lorsqu'une touche est tapée (non utilisée).
     * @param e événement clavier
     */
    @Override
    public void keyTyped(KeyEvent e) {
    }

    /**
     * Méthode appelée lorsqu'une touche est pressée.
     * @param e événement clavier
     */
    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        //mvt
        if (code == KeyEvent.VK_SPACE) {
            upPressed = true;
        }
        if (code == KeyEvent.VK_Q) {
            leftPressed = true;
        }
        if (code == KeyEvent.VK_S) {
            downPressed = true;
        }
        if (code == KeyEvent.VK_D) {
            rightPressed = true;
        }
        //actions
        if (code == KeyEvent.VK_F) {
            interactPressed = true;
        }
        if (code == KeyEvent.VK_E) {
            boostPressed = true;
        }
        if (code == KeyEvent.VK_A) {
            healPressed = true;
        }
        if (code == KeyEvent.VK_Z) {
            doubleJumpPressed = true;
        }
    }

    /**
     * Méthode appelée lorsqu'une touche est relâchée.
     * @param e événement clavier
     */
    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_SPACE) {
            upPressed = false;
        }
        if (code == KeyEvent.VK_Q) {
            leftPressed = false;
        }
        if (code == KeyEvent.VK_S) {
            downPressed = false;
        }
        if (code == KeyEvent.VK_D) {
            rightPressed = false;
        }
        if (code == KeyEvent.VK_F) {
            interactPressed = false;
        }
        if (code == KeyEvent.VK_E) {
            boostPressed = false;
        }
        if (code == KeyEvent.VK_A) {
            healPressed = false;
        }
        if (code == KeyEvent.VK_Z) {
            doubleJumpPressed = false;
        }
    }

    /**
     * Retourne si la touche d'interaction est pressée.
     * @return true si pressée
     */
    public boolean getInteractPressed() {
        return this.interactPressed;
    }

    /**
     * Méthode appelée lorsqu'un bouton souris est pressé.
     * @param e événement souris
     */
    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON3) {
            dashPressed = true;
        }
        if(e.getButton() == MouseEvent.BUTTON1) {
            attackPressed = true;
        }
    }

    /**
     * Méthode appelée lorsqu'un bouton souris est relâché.
     * @param e événement souris
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON3) {
            dashPressed = false;
        }
        if (e.getButton() == MouseEvent.BUTTON1) {
            attackPressed = false;
        }
    }

    /**
     * Méthode appelée lorsqu'un clic souris est effectué (non utilisée).
     * @param e événement souris
     */
    @Override
    public void mouseClicked(MouseEvent e) {
    }

    /**
     * Méthode appelée lorsque la souris entre dans la zone (non utilisée).
     * @param e événement souris
     */
    @Override
    public void mouseEntered(MouseEvent e) {
    }

    /**
     * Méthode appelée lorsque la souris sort de la zone (non utilisée).
     * @param e événement souris
     */
    @Override
    public void mouseExited(MouseEvent e) {
    }
}