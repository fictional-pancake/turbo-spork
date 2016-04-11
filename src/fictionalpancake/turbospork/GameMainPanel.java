package fictionalpancake.turbospork;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.*;

class GameMainPanel extends JPanel implements MouseMotionListener, MouseListener {
    private GameHandler gameHandler;
    private int xOffset;
    private int yOffset;
    private double scale;
    private int mouseY;
    private int mouseX;
    private Node selectedNode;

    public GameMainPanel(GameHandler gameHandler) {
        this.gameHandler = gameHandler;
        addMouseMotionListener(this);
        addMouseListener(this);
    }

    @Override
    public void paint(Graphics graphics) {
        super.paint(graphics);
        Graphics2D g = ((Graphics2D) graphics);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Stroke defaultStroke = g.getStroke();
        Stroke outlineStroke = new BasicStroke(convertSize(GameConstants.OUTLINE_SIZE));
        checkScaling();
        java.util.List<Node> nodes = gameHandler.getNodes();
        java.util.List<UnitGroup> groups = gameHandler.getUnitGroups();
        if (nodes != null && groups != null) {
            Node node;
            int d = convertSize(GameConstants.NODE_RADIUS * 2);
            for (int i = 0; i < nodes.size(); i++) {
                node = nodes.get(i);
                g.setColor(TurboSpork.getColorForOwner(node.getOwner()));
                g.fillOval(convertX(node.getX() - GameConstants.NODE_RADIUS), convertY(node.getY() - GameConstants.NODE_RADIUS), d, d);
                drawUnitGroup(g, i, node.getOwner(), node.getUnits(gameHandler), node.getX(), node.getY());
            }
            g.setStroke(outlineStroke);
            Node underMouse = getNodeUnderMouse();
            if (underMouse != null) {
                g.setColor(Color.darkGray);
                g.drawOval(convertX(underMouse.getX() - GameConstants.NODE_RADIUS), convertY(underMouse.getY() - GameConstants.NODE_RADIUS), d, d);
            }
            if(selectedNode != null) {
                g.setColor(Color.pink);
                g.drawOval(convertX(selectedNode.getX() - GameConstants.NODE_RADIUS), convertY(selectedNode.getY() - GameConstants.NODE_RADIUS), d, d);
            }
            g.setStroke(defaultStroke);
            for(UnitGroup group : groups) {
                Node src = group.getSource();
                drawUnitGroup(g, gameHandler.indexOf(src), group.getOwner(), group.getUnits(), group.getX(), group.getY());
            }
        } else {
            String lastWinner = gameHandler.getLastWinner();
            if (lastWinner != null) {
                g.setColor(Color.gray);
                g.setFont(new Font("SansSerif", Font.ITALIC, convertSize(GameConstants.WIN_TEXT_SIZE)));
                drawStringCenter(g, lastWinner + " wins!", convertX(GameConstants.FIELD_SIZE / 2), convertY(GameConstants.FIELD_SIZE / 2));
            }
        }
    }

    private Node getNodeUnderMouse() {
        java.util.List<Node> nodes = gameHandler.getNodes();
        Node tr = null;
        if(nodes != null) {
            for (Node node : nodes) {
                if (isMouseOverNode(node)) {
                    tr = node;
                }
            }
        }
        return tr;
    }

    private void drawUnitGroup(Graphics g, int seed, int owner, int numUnits, double x, double y) {
        Random rand = new Random(seed);
        double angle;
        double distance;
        int unitX;
        int unitY;
        int diameter = convertSize(GameConstants.UNIT_RADIUS * 2);
        Color color = TurboSpork.getColorForOwner(owner).darker();
        for (int j = 0; j < numUnits; j++) {
            // generate a random angle and distance for the node to be at
            angle = rand.nextDouble() * 2 * Math.PI;
            // sqrt is magic, makes things more evenly spaced
            distance = Math.sqrt(rand.nextDouble()) * GameConstants.UNIT_MAX_DISTANCE * GameConstants.NODE_RADIUS;
            // convert those to X and Y coordinates
            unitX = convertX(distance * Math.cos(angle) + x);
            unitY = convertY(distance * Math.sin(angle) + y);
            // draw the unit
            g.setColor(color);
            g.fillOval(unitX, unitY, diameter, diameter);
            g.setColor(Color.black);
            g.drawOval(unitX, unitY, diameter, diameter);
        }
    }

    private int convertX(double x) {
        return (int) (xOffset + x * scale);
    }

    private int convertY(double y) {
        return (int) (yOffset + y * scale);
    }

    private int convertSize(double s) {
        return (int) (s * scale);
    }

    public void checkScaling() {
        int minDimension;
        if (getWidth() < getHeight()) {
            xOffset = 0;
            yOffset = (getHeight() - getWidth()) / 2;
            minDimension = getWidth();
        } else {
            xOffset = (getWidth() - getHeight()) / 2;
            yOffset = 0;
            minDimension = getHeight();
        }
        scale = ((double) minDimension) / GameConstants.FIELD_SIZE;
    }

    private void drawStringCenter(Graphics g, String string, int centerX, int centerY) {
        FontMetrics metrics = g.getFontMetrics();
        g.drawString(string, centerX - metrics.stringWidth(string) / 2, centerY /*- metrics.getHeight() / 2*/);
    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    private boolean isMouseOverNode(Node node) {
        return TurboSpork.distance(mouseX, mouseY, convertX(node.getX()), convertY(node.getY())) <= convertSize(GameConstants.NODE_RADIUS);
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        Node underMouse = getNodeUnderMouse();
        if(selectedNode == null) {
            if (underMouse != null && underMouse.getOwner() == gameHandler.getPosition()) {
                selectedNode = underMouse;
            }
        }
        else if(selectedNode == underMouse) {
            selectedNode = null;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if(selectedNode != null) {
            Node underMouse = getNodeUnderMouse();
            if(underMouse != null && underMouse != selectedNode) {
                gameHandler.attack(underMouse, selectedNode);
                selectedNode = null;
            }
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}