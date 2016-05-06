package fictionalpancake.turbospork.gui;

import java.awt.*;

public class RepaintThread implements Runnable {
    private Component target;

    @Override
    public void run() {
        while (true) {
            if (target.isShowing()) {
                target.repaint();
            }
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public RepaintThread(Component target) {
        this.target = target;
    }
}
