package Viewer;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class M implements KeyListener {
    @Override
    public void keyTyped(KeyEvent e) {
        if (e.getKeyCode()==KeyEvent.VK_LEFT){
            System.out.println("Hello");

        }
    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
