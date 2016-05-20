package fictionalpancake.turbospork.gui;

import fictionalpancake.turbospork.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Random;

class GameMainPanel extends JPanel implements MouseMotionListener, MouseListener {
    private GameHandler gameHandler;
    private int xOffset;
    private int yOffset;
    private double scale;
    private int mouseY;
    private int mouseX;
    private Node selectedNode;
    private Node lastSelected;
    private Node lastAttacked;
    private long lastPaint;

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
                g.setColor(GameColors.getColorForOwner(node.getOwner()));
                g.fillOval(convertX(node.getX() - GameConstants.NODE_RADIUS), convertY(node.getY() - GameConstants.NODE_RADIUS), d, d);
                // draw yo moms house
                if (node.getOwner() == -3) {
                    Color purple = new Color(0x420666);
                    for (int circle = GameConstants.HOUSE_NUM_CIRCLES - 1; circle >= 0; circle--) {
                        g.setColor((circle % 2 == 0) ? Color.blue : purple);
                        g.fillOval(convertX(node.getX() - (double) GameConstants.NODE_RADIUS / GameConstants.HOUSE_NUM_CIRCLES * circle),
                                convertY(node.getY() - (double) GameConstants.NODE_RADIUS / GameConstants.HOUSE_NUM_CIRCLES * circle),
                                (d / GameConstants.HOUSE_NUM_CIRCLES) * circle, (d / GameConstants.HOUSE_NUM_CIRCLES) * circle);
                    }
                } else {
                    drawNodeUnits(g, node);
                }
            }
            g.setStroke(outlineStroke);
            Node underMouse = getNodeUnderMouse();
            if (underMouse != null) {
                g.setColor(Color.darkGray);
                g.drawOval(convertX(underMouse.getX() - GameConstants.NODE_RADIUS), convertY(underMouse.getY() - GameConstants.NODE_RADIUS), d, d);
            }
            if (selectedNode != null) {
                g.setColor(Color.pink);
                g.drawOval(convertX(selectedNode.getX() - GameConstants.NODE_RADIUS), convertY(selectedNode.getY() - GameConstants.NODE_RADIUS), d, d);
            }
            g.setStroke(defaultStroke);
            for (int i = 0; i < groups.size(); i++) {
                UnitGroup group = groups.get(i);
                drawUnitGroup(g, group);
            }
        } else {
            String lastWinner = gameHandler.getLastWinner();
            if (lastWinner != null) {
                g.setColor(Color.gray);
                g.setFont(new Font("SansSerif", Font.ITALIC, convertSize(GameConstants.WIN_TEXT_SIZE)));
                drawStringCenter(g, lastWinner + " wins!", convertX(GameConstants.FIELD_SIZE / 2), convertY(GameConstants.FIELD_SIZE / 2));
            }
        }
        g.setColor(Color.red);
        if(gameHandler.isSpectating()) {
            g.setFont(new Font("SansSerif", Font.PLAIN, convertSize(GameConstants.SPECTATING_TEXT_SIZE)));
            g.drawString("Spectating", convertX(0), convertY(100)-g.getFontMetrics().getDescent());
        }
        g.setFont(new Font("SansSerif", Font.PLAIN, convertSize(GameConstants.SMALL_TEXT_SIZE)));
        double fps = 1000/((double)(System.currentTimeMillis()-lastPaint));
        String fpsText = ((int)fps)+" fps";
        g.drawString(fpsText, convertX(0), convertY(0)+g.getFontMetrics().getAscent());
        // draw game start message
        if (gameHandler.hasGameData() && !gameHandler.isInProgress()) {
            g.setColor(Color.red);
            g.setFont(new Font("SansSerif", Font.ITALIC, convertSize(GameConstants.GAMEREADY_TEXT_SIZE)));
            drawStringCenter(g, "Game starting soon", convertX(GameConstants.FIELD_SIZE / 2), convertY(GameConstants.FIELD_SIZE / 2));
        }
        lastPaint = System.currentTimeMillis();
    }

    private Node getNodeUnderMouse() {
        java.util.List<Node> nodes = gameHandler.getNodes();
        Node tr = null;
        if (nodes != null) {
            for (Node node : nodes) {
                if (isMouseOverNode(node)) {
                    tr = node;
                }
            }
        }
        return tr;
    }

    private void drawUnitGroup(Graphics g, int seed1, int seed2, double progress, int owner, int numUnits, double x, double y) {
        Random rand1 = new Random(seed1);
        Random rand2 = new Random(seed2);

        int diameter = convertSize(GameConstants.UNIT_RADIUS * 2);
        Color color = GameColors.getColorForOwner(owner).darker();
        for (int j = 0; j < numUnits; j++) {
            Point coords1 = getRandomUnitCoords(rand1, x, y);
            Point coords2 = getRandomUnitCoords(rand2, x, y);

            int unitX = (int) (coords1.getX() + (coords2.getX() - coords1.getX()) * progress);
            int unitY = (int) (coords1.getY() + (coords2.getY() - coords1.getY()) * progress);

            // draw the unit
            g.setColor(color);
            g.fillOval(unitX, unitY, diameter, diameter);
            g.setColor(Color.black);
            g.drawOval(unitX, unitY, diameter, diameter);
        }
    }

    private void drawNodeUnits(Graphics g, Node node) {
        for (int owner : node.getUnitOwners()) {
            int seed = getGroupSeed(node, owner);
            drawUnitGroup(g, seed, seed, 1, owner, node.getUnits(owner), node.getX(), node.getY());
        }
    }

    private void drawUnitGroup(Graphics g, UnitGroup group) {
        int owner = group.getOwner();
        drawUnitGroup(g, getGroupSeed(group.getSource(), owner), getGroupSeed(group.getDest(), owner), group.getProgress(), owner, group.getUnits(), group.getX(), group.getY());
    }

    private int getGroupSeed(Node node, int owner) {
        return gameHandler.indexOf(node) * (owner + 1);
    }

    private Point getRandomUnitCoords(Random rand, double x, double y) {
        // generate a random angle and distance for the node to be at
        double angle = rand.nextDouble() * 2 * Math.PI;
        // sqrt is magic, makes things more evenly spaced
        double distance = Math.sqrt(rand.nextDouble()) * GameConstants.UNIT_MAX_DISTANCE * GameConstants.NODE_RADIUS;
        // convert those to X and Y coordinates
        int unitX = convertX(distance * Math.cos(angle) + x);
        int unitY = convertY(distance * Math.sin(angle) + y);
        return new Point(unitX, unitY);
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
        return MathHelper.distance(mouseX, mouseY, convertX(node.getX()), convertY(node.getY())) <= convertSize(GameConstants.NODE_RADIUS);
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        Node underMouse = getNodeUnderMouse();
        if (selectedNode == null) {
            select(underMouse);
        } else if (selectedNode == underMouse) {
            selectedNode = null;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (selectedNode != null) {
            Node underMouse = getNodeUnderMouse();
            attack(underMouse);
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    public void select(Node node) {
        if (selectedNode == null && node != null && gameHandler.isInProgress()) {
            int pos = gameHandler.getPosition();
            if ((pos != -1 && pos == node.getOwner()) || node.getUnits(pos) > 0) {
                selectedNode = node;
                lastSelected = node;
            }
        }
    }

    public void attack(Node node) {
        if (selectedNode != null && node != null && node != selectedNode) {
            gameHandler.attack(node, selectedNode);
            selectedNode = null;
            lastAttacked = node;
        }
    }

    public void selectLast() {
        select(lastSelected);
    }

    public void attackLast() {
        attack(lastAttacked);
    }
}
