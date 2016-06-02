package fictionalpancake.turbospork.paint;

public interface IPainter {

    int getWidth();

    int getHeight();

    void drawCircle(PaintStyle style, int x, int y, int radius);

    void drawText(PaintStyle style, String text, int x, int y);

    Point getMousePos();

    void drawRectangle(PaintStyle style, int x, int y, int width, int height);
}
