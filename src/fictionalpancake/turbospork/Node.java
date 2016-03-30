package fictionalpancake.turbospork;

import java.util.Map;

public class Node {
    private int x;
    private int y;
    private int owner;
    private int generationTime;
    private int unitCap;
    private double unitSpeed;
    private int lastUnits = 0;
    private long lastUnitCheck;

    public int getY() {
        return y;
    }

    public int getUnitCap() {
        return unitCap;
    }

    public double getUnitSpeed() {
        return unitSpeed;
    }

    public int getX() {
        return x;
    }

    public int getOwner() {
        return owner;
    }

    public int getGenerationTime() {
        return generationTime;
    }

    public int getUnits() {
        synchronized(this) {
            while (lastUnitCheck + getGenerationTime() <= System.currentTimeMillis()) {
                lastUnitCheck += getGenerationTime();
                lastUnits++;
            }
            return lastUnits;
        }
    }

    public Node(Map map) {
        x = (int) ((long) map.get("x"));
        y = (int) ((long) map.get("y"));
        owner = (int) ((long) map.get("owner"));
        generationTime = (int) ((long) map.get("generationTime"));
        unitCap = (int) ((long) map.get("unitCap"));
        unitSpeed = (double) map.get("unitSpeed");
        lastUnitCheck = System.currentTimeMillis();
    }
}
