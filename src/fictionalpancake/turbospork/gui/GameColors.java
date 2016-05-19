package fictionalpancake.turbospork.gui;
import java.awt.*;
import java.util.Random;

public class GameColors {
    public static Color[] COLORS = {
            Color.green,
            Color.blue,
            Color.red,
            Color.yellow,
            Color.magenta,
            Color.cyan,
            Color.orange
    };

    public static Color getColorForOwner(int owner) {
        if (owner == -3) {
            return Color.black;
        } else if (owner < 0) {
            return Color.lightGray;
        } else if (owner < COLORS.length) {
            return COLORS[owner];
        } else {
            Random r = new Random(owner);
            return new Color(r.nextInt(256), r.nextInt(256), r.nextInt(256));
        }
    }
}
