package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class StopperThread extends Thread implements KeyListener {
    private boolean stopped = false;

    public StopperThread() {

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    public void keyPressed(KeyEvent e) {

        int key = e.getKeyCode();


        System.out.println(KeyEvent.getKeyText(key));
        System.out.println("wæææææ");
//        if (key == KeyEvent.getExtendedKeyCodeForChar()) {
//            dx = -1;
//        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    public boolean isStopped() {
        return stopped;
    }
}
