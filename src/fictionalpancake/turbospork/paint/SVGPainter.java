package fictionalpancake.turbospork.paint;

public class SVGPainter implements IPainter {
    private int width = 1000;
    private int height = 1000;
    private String tr;

    public SVGPainter() {
        tr = "<?xml version=\"1.0\"?>\n";
        tr += "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"" + width + "\" height=\"" + height + "\">\n";
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void drawCircle(PaintStyle style, int x, int y, int radius) {
        tr += "<circle cx=\"" + x + "\" cy=\"" + y + "\" r=\"" + radius + "\" style=\"" + convertStyle(style, false) + "\" />\n";
    }

    private String convertStyle(PaintStyle style, boolean text) {
        String tr = "";
        if (style.fill) {
            tr += "fill:" + convertColor(style.color) + ";";
        } else {
            tr += "fill:none;";
            tr += "stroke:" + convertColor(style.color) + ";";
            tr += "stroke-width:" + style.strokeWidth+";";
        }
        if (text) {
            tr += "font-family:sans-serif;";
            tr += "font-size:" + style.textSize + "px;";
            tr += "text-anchor:" + (style.alignX == PaintStyle.Align.LEFT ? "start" : (style.alignX == PaintStyle.Align.RIGHT ? "end" : "middle"))+";";
        }
        return tr;
    }

    private String convertColor(Color color) {
        return "rgba(" + color.getRed() + "," + color.getGreen() + "," + color.getBlue() + "," + ((double) color.getAlpha()) / 255 + ")";
    }

    @Override
    public void drawText(PaintStyle style, String text, int x, int y) {
        int ay = y;
        if (style.alignY == PaintStyle.Align.TOP) {
            ay += style.textSize;
        } else if (style.alignY == PaintStyle.Align.CENTER) {
            ay += style.textSize / 2;
        }
        tr += "<text x=\"" + x + "\" y=\"" + ay + "\" style=\"" + convertStyle(style, true) + "\">" + text.replace("\"", "\\\"") + "</text>\n";
    }

    @Override
    public Point getMousePos() {
        return null;
    }

    public String getResult() {
        return tr + "</svg>";
    }
}
