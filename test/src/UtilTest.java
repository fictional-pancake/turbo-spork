import fictionalpancake.turbospork.TurboSpork;
import org.junit.Assert;
import org.junit.Test;

public class UtilTest {
    @Test
    public void testDistance() {
        Assert.assertEquals(TurboSpork.distance(0, 0, 1, 0), 1, TestConstants.TOLERANCE);
        Assert.assertEquals(TurboSpork.distance(0, 0, 1, 1), Math.sqrt(2), TestConstants.TOLERANCE);
        Assert.assertEquals(TurboSpork.distance(5, 6, 5, 7), 1, TestConstants.TOLERANCE);
        Assert.assertEquals(TurboSpork.distance(9, 8, 12, 4), 5, TestConstants.TOLERANCE);
    }
}
