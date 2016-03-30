package fictionalpancake.turbospork;

import java.awt.*;

public class RepaintThread implements Runnable {
    private Component target;

    @Override
    public void run() {
        while(true) {
            target.repaint();
        }
    }

    public RepaintThread(Component target) {
        this.target = target;
    }
}
