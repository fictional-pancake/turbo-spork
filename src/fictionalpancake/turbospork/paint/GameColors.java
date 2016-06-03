package fictionalpancake.turbospork.paint;

import java.util.Random;

public class GameColors {
    public static Color[] COLORS = {
            Color.GREEN,
            Color.BLUE,
            Color.RED,
            Color.MAGENTA,
            Color.CYAN,
            Color.YELLOW,
            Color.ORANGE
    };

    public static Color getColorForOwner(int owner) {
        if (owner == -3) {
            return Color.BLACK;
        } else if (owner < 0) {
            return Color.LIGHT_GRAY;
        } else if (owner < COLORS.length) {
            return COLORS[owner];
        } else {
            Random r = new Random(owner);
            return new Color(r.nextInt(256), r.nextInt(256), r.nextInt(256));
        }
    }
}
