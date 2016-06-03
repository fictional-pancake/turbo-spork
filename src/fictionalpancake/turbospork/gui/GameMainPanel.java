package fictionalpancake.turbospork.gui;

import fictionalpancake.turbospork.GameHandler;
import fictionalpancake.turbospork.Node;
import fictionalpancake.turbospork.paint.GraphicsHandler;
import fictionalpancake.turbospork.paint.IPainter;
import fictionalpancake.turbospork.paint.PaintStyle;
import fictionalpancake.turbospork.paint.Point;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

class GameMainPanel extends JPanel implements MouseMotionListener, MouseListener, IPainter {
    private GameHandler gameHandler;
    private int mouseY;
    private int mouseX;
    private Node selectedNode;
    private GraphicsHandler graphicsHandler;
    private Graphics2D g;

    public GameMainPanel(GameHandler gameHandler) {
        this.gameHandler = gameHandler;
        this.graphicsHandler = new GraphicsHandler(gameHandler);
        addMouseMotionListener(this);
        addMouseListener(this);
    }

    @Override
    public void paint(Graphics graphics) {
        super.paint(graphics);
        g = (Graphics2D) graphics;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphicsHandler.paint(this);
    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }


    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        Node underMouse = graphicsHandler.getNodeUnderMouse(this);
        graphicsHandler.select(underMouse);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        Node underMouse = graphicsHandler.getNodeUnderMouse(this);
        graphicsHandler.attack(underMouse);
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void drawCircle(PaintStyle style, int x, int y, int radius) {
        applyStyle(style);
        if (style.fill) {
            g.fillOval(x - radius, y - radius, radius * 2, radius * 2);
        } else {
            g.drawOval(x - radius, y - radius, radius * 2, radius * 2);
        }
    }

    @Override
    public void drawText(PaintStyle style, String text, int x, int y) {
        applyStyle(style);
        FontMetrics metrics = g.getFontMetrics();
        int ax = x;
        int ay = y;
        if (style.alignX == PaintStyle.Align.CENTER) {
            ax -= metrics.stringWidth(text) / 2;
        } else if (style.alignX == PaintStyle.Align.RIGHT) {
            ax -= metrics.stringWidth(text);
        }
        if (style.alignY == PaintStyle.Align.TOP) {
            ay += metrics.getAscent();
        } else if (style.alignY == PaintStyle.Align.BOTTOM) {
            ay -= metrics.getDescent();
        } else if (style.alignY == PaintStyle.Align.CENTER) {
            ay -= metrics.getDescent() / 2;
        }
        g.drawString(text, ax, ay);
    }

    @Override
    public Point getMousePos() {
        return new Point(mouseX, mouseY);
    }

    @Override
    public void drawRectangle(PaintStyle style, int x, int y, int width, int height) {
        applyStyle(style);
        if(style.fill) {
            g.fillRect(x, y, width, height);
        }
        else {
            g.drawRect(x, y, width, height);
        }
    }

    public void applyStyle(PaintStyle style) {
        g.setColor(TurboSpork.convertColor(style.color));
        g.setStroke(new BasicStroke(style.strokeWidth));
        g.setFont(new Font("SansSerif", Font.PLAIN, style.textSize));
    }

    public void selectLast() {
        graphicsHandler.selectLast();
    }

    public void attackLast() {
        graphicsHandler.attackLast();
    }
}
