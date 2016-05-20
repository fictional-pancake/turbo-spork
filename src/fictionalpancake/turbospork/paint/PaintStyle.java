package fictionalpancake.turbospork.paint;

public class PaintStyle {
    public Color color = Color.BLACK;
    public float strokeWidth = 1;
    public int textSize = 12;
    public Align alignX = Align.LEFT;
    public Align alignY = Align.TOP;
    public boolean fill = true;

    public enum Align {
        LEFT,
        CENTER,
        RIGHT,
        TOP,
        BOTTOM
    }
}
