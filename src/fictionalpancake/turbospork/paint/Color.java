package fictionalpancake.turbospork.paint;

public class Color {
    public static final Color BLUE = new Color(0x0000FF);
    public static final Color GREEN = new Color(0x00FF00);
    public static final Color RED = new Color(0xFF0000);
    public static final Color MAGENTA = new Color(0xFF00FF);
    public static final Color CYAN = new Color(0x00FFFF);
    public static final Color YELLOW = new Color(0xFFFF00);
    public static final Color ORANGE = new Color(0xFFC800);
    public static final Color LIGHT_GRAY = new Color(0xC0C0C0);
    public static final Color DARK_GRAY = new Color(0x404040);
    public static final Color PINK = new Color(0xFFAFAF);
    public static final Color GRAY = new Color(0x808080);
    public static final Color BLACK = new Color(0);


    private int red;
    private int green;
    private int blue;
    private int alpha;

    public Color(int red, int green, int blue, int alpha) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    public Color(int red, int green, int blue) {
        this(red, green, blue, 255);
    }

    public Color(int color) {
        this(color / 0x10000, (color / 0x100) % 0x100, color % 0x100);
    }

    public Color darker() {
        return new Color(((int) (red * 0.7)), ((int) (green * 0.7)), ((int) (blue * 0.7)), alpha);
    }

    public int getAlpha() {
        return alpha;
    }

    public int getBlue() {
        return blue;
    }

    public int getGreen() {
        return green;
    }

    public int getRed() {
        return red;
    }
}
