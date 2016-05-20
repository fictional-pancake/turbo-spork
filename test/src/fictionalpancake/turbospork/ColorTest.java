package fictionalpancake.turbospork;

import fictionalpancake.turbospork.paint.Color;
import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

public class ColorTest {
    @Test
    public void testRGB() {
        Random rand = new Random();
        int r = rand.nextInt(256);
        int g = rand.nextInt(256);
        int b = rand.nextInt(256);
        Color c = new Color(r, g, b);
        Assert.assertEquals(c.getRed(), r);
        Assert.assertEquals(c.getGreen(), g);
        Assert.assertEquals(c.getBlue(), b);
    }

    @Test
    public void testRGBA() {
        Random rand = new Random();
        int r = rand.nextInt(256);
        int g = rand.nextInt(256);
        int b = rand.nextInt(256);
        int a = rand.nextInt(256);
        Color c = new Color(r, g, b, a);
        Assert.assertEquals(c.getRed(), r);
        Assert.assertEquals(c.getGreen(), g);
        Assert.assertEquals(c.getBlue(), b);
        Assert.assertEquals(c.getAlpha(), a);
    }

    @Test
    public void testNumber() {
        checkMatch(0xFFFFFF, 0xFF, 0xFF, 0xFF);
        checkMatch(0x666420, 0x66, 0x64, 0x20);
        checkMatch(0, 0, 0, 0);
    }

    private void checkMatch(int color, int r, int g, int b) {
        Color c = new Color(color);
        Assert.assertEquals(c.getRed(), r);
        Assert.assertEquals(c.getGreen(), g);
        Assert.assertEquals(c.getBlue(), b);
    }
}