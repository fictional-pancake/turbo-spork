package fictionalpancake.turbospork;

import java.util.Random;

public class MathHelper {
    public static double distance(int x1, int y1, int x2, int y2) {
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    public static int toInt(Object o) {
        if(o instanceof Integer) {
            return ((int) o);
        }
        else if(o instanceof Long) {
            return ((Long) o).intValue();
        }
        else {
            return Integer.parseInt(o.toString());
        }
    }
}
