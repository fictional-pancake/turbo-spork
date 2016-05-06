package fictionalpancake.turbospork;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Node {
    private int x;
    private int y;
    private int owner;
    private int generationTime;
    private int unitCap;
    private double unitSpeed;
    private HashMap<Integer, Integer> units;
    private long lastUnitCheck;

    public Node(Map map) {
        x = MathHelper.toInt(map.get("x"));
        y = MathHelper.toInt(map.get("y"));
        setOwner(MathHelper.toInt(map.get("owner")));
        generationTime = MathHelper.toInt(map.get("generationTime"));
        unitCap = MathHelper.toInt(map.get("unitCap"));
        unitSpeed = (double) map.get("unitSpeed");
        units = new HashMap<>();
        lastUnitCheck = System.currentTimeMillis();
    }

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

    public void setOwner(int owner) {
        this.owner = owner;
        lastUnitCheck = System.currentTimeMillis();
    }

    public int getGenerationTime() {
        int tr = generationTime;
        for(int owner : units.keySet()) {
            if(units.get(owner) > 0 && owner != getOwner()) {
                tr *= 2;
                break;
            }
        }
        return tr;
    }

    public int getUnits(int owner) {
        synchronized (units) {
            int tr = 0;
            if(units.containsKey(owner)) {
                tr = units.get(owner);
            }
            if (owner == getOwner()) {
                while (lastUnitCheck + getGenerationTime() <= System.currentTimeMillis()) {
                    lastUnitCheck += getGenerationTime();
                    if (tr < getUnitCap()) {
                        tr++;
                    }
                }
            }
            units.put(owner, tr);
            return tr;
        }
    }

    public void addUnits(int owner, int units) {
        int c = 0;
        if(this.units.containsKey(owner)) {
            c = this.units.get(owner);
        }
        this.units.put(owner, c + units);
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

    public int takeUnits(int owner, int i) {
        int cu = getUnits(owner);
        int tr;
        if (i > cu) {
            tr = cu;
        } else {
            tr = i;
        }
        addUnits(owner, -i);
        return tr;
    }

    public void setUnits(int owner, int units) {
        this.units.put(owner, units);
        if (owner == this.owner) {
            lastUnitCheck = System.currentTimeMillis();
        }
    }

    public List<Integer> getUnitOwners() {
        List<Integer> tr = new ArrayList<>();
        for (int owner : units.keySet()) {
            if (getUnits(owner) > 0) {
                tr.add(owner);
            }
        }
        return tr;
    }

    public void sync(Map curNodeData) {
        int owner = MathHelper.toInt(curNodeData.get("owner"));
        if (getOwner() != owner) {
            setOwner(owner);
        }
        synchronized (units) {
            units.clear();
            Map unitMap = (Map) curNodeData.get("units");
            for (Object i : unitMap.keySet()) {
                units.put(MathHelper.toInt(i), MathHelper.toInt(unitMap.get(i)));
            }
        }
    }
}
