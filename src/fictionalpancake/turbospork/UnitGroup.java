package fictionalpancake.turbospork;

import java.util.List;
import java.util.Map;

public class UnitGroup {
    private Node source;
    private Node dest;
    private int duration;
    private long start;
    private int units;
    private int owner;
    private int id;

    private double xVel;
    private double yVel;

    public UnitGroup(Map map, GameHandler gameHandler) {
        List<Node> nodes = gameHandler.getNodes();
        source = nodes.get(MathHelper.toInt(map.get("source")));
        dest = nodes.get(MathHelper.toInt(map.get("dest")));
        duration = MathHelper.toInt(map.get("duration"));
        units = MathHelper.toInt(map.get("size"));
        owner = MathHelper.toInt(map.get("owner"));
        id = MathHelper.toInt(map.get("id"));
        start = System.currentTimeMillis();

        xVel = ((double) (dest.getX() - source.getX())) / duration;
        yVel = ((double) (dest.getY() - source.getY())) / duration;
    }

    public double getX() {
        long time = System.currentTimeMillis() - start;
        if (time >= duration) {
            return dest.getX();
        } else {
            return source.getX() + xVel * time;
        }
    }

    public double getY() {
        long time = System.currentTimeMillis() - start;
        if (time >= duration) {
            return dest.getY();
        } else {
            return source.getY() + yVel * time;
        }
    }

    public Node getDest() {
        return dest;
    }

    public Node getSource() {
        return source;
    }

    public int getUnits() {
        return units;
    }

    public boolean isComplete() {
        long time = System.currentTimeMillis() - start;
        return time >= duration;
    }

    public int takeUnits(int i) {
        if (i > units) {
            int tr = units;
            units = 0;
            return tr;
        } else {
            units -= i;
            return i;
        }
    }

    public int getOwner() {
        return owner;
    }

    public double getProgress() {
        return Math.min(1, (System.currentTimeMillis() - start) / ((double) duration));
    }

    public int getID() {
        return id;
    }

    public void setUnits(int i) {
        units = i;
    }
}
