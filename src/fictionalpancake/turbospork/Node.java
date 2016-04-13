package fictionalpancake.turbospork;

import java.util.List;
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

    public int getGenerationTime(GameHandler gh) {
        int tr = generationTime;
        if (gh != null) {
            List<UnitGroup> groups = gh.getUnitGroups();
            if (groups != null) {
                for (UnitGroup group : groups) {
                    if (group.getDest() == this && group.isComplete() && group.getUnits() > 0 && group.getOwner() != getOwner()) {
                        tr *= 2;
                        break;
                    }
                }
            }
        }
        return tr;
    }

    public int getGenerationTime() {
        return getGenerationTime(null);
    }

    public int getUnits(GameHandler gh) {
        if (getOwner() == -1) {
            lastUnitCheck = System.currentTimeMillis();
            return 0;
        }
        synchronized (this) {
            while (lastUnitCheck + getGenerationTime(gh) <= System.currentTimeMillis()) {
                lastUnitCheck += getGenerationTime(gh);
                if (lastUnits < getUnitCap()) {
                    lastUnits++;
                }
            }
            return lastUnits;
        }
    }

    public Node(Map map) {
        x = (int) ((long) map.get("x"));
        y = (int) ((long) map.get("y"));
        setOwner((int) ((long) map.get("owner")));
        generationTime = (int) ((long) map.get("generationTime"));
        unitCap = (int) ((long) map.get("unitCap"));
        unitSpeed = (double) map.get("unitSpeed");
        setUnits(0);
    }

    public void addUnits(int units) {
        lastUnits += units;
    }

    public void updateProp(String key, String value) {
        switch (key) {
            case "owner":
                owner = Integer.parseInt(value);
                break;
            default:
                System.err.println("Unknown property: " + key);
        }
    }

    public int takeUnits(int i, GameHandler gameHandler) {
        int cu = getUnits(gameHandler);
        if (i > cu) {
            lastUnits -= cu;
            return cu;
        } else {
            lastUnits -= i;
            return i;
        }
    }

    public void setOwner(int owner) {
        this.owner = owner;
    }

    public void setUnits(int units) {
        lastUnits = units;
        lastUnitCheck = System.currentTimeMillis();
    }
}
