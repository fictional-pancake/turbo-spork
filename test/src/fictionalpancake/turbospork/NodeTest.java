package fictionalpancake.turbospork;

import fictionalpancake.turbospork.paint.GameColors;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class NodeTest {
    @Test
    public void testLoadFromMap() {
        long x = new Random().nextInt(100);
        long y = new Random().nextInt(100);
        long owner = new Random().nextInt(GameColors.COLORS.length);
        long generationTime = new Random().nextInt(10000);
        long unitCap = new Random().nextInt(1000);
        double unitSpeed = new Random().nextDouble();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("x", x);
        map.put("y", y);
        map.put("owner", owner);
        map.put("generationTime", generationTime);
        map.put("unitCap", unitCap);
        map.put("unitSpeed", unitSpeed);
        Node n = new Node(map);
        Assert.assertEquals(n.getX(), x);
        Assert.assertEquals(n.getY(), y);
        Assert.assertEquals(n.getOwner(), owner);
        Assert.assertEquals(n.getGenerationTime(), generationTime);
        Assert.assertEquals(n.getUnitCap(), unitCap);
        Assert.assertEquals(n.getUnitSpeed(), unitSpeed, TestConstants.TOLERANCE);
    }
}
