package fictionalpancake.turbospork;

import java.util.List;
import java.util.Map;

public class UnitGroup {
    private Node source;
    private Node dest;
    private int duration;
    private long start;
    private int units;

    private double xVel;
    private double yVel;

    public UnitGroup(Map map, GameHandler gameHandler) {
        List<Node> nodes = gameHandler.getNodes();
        source = nodes.get((int) ((long) map.get("source")));
        dest = nodes.get((int) ((long) map.get("dest")));
        duration = (int) ((long) map.get("duration"));
        units = (int) ((long) map.get("size"));
        start = System.currentTimeMillis();

        xVel = ((double)(dest.getX()-source.getX()))/duration;
        yVel = ((double)(dest.getY()-source.getY()))/duration;
    }

    public double getX() {
        long time = System.currentTimeMillis() - start;
        if(time >= duration) {
            return dest.getX();
        }
        else {
            return source.getX()+xVel*time;
        }
    }

    public double getY() {
        long time = System.currentTimeMillis() - start;
        if(time >= duration) {
            return dest.getY();
        }
        else {
            return source.getY()+yVel*time;
        }
    }

    public Node getDest() {
        return dest;
    }

    public int getDuration() {
        return duration;
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

    public void takeUnits(int i) {
        units -= i;
    }
}
