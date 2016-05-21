package fictionalpancake.turbospork.paint;

import fictionalpancake.turbospork.*;

import java.util.List;
import java.util.Random;

public class GraphicsHandler {

    private GameHandler gameHandler;
    private Node selectedNode;
    private Node lastSelected;
    private Node lastAttacked;
    private long lastPaint;
    private int xOffset;
    private int yOffset;
    private double scale;

    public GraphicsHandler(GameHandler gameHandler) {
        this.gameHandler = gameHandler;
    }

    public void paint(IPainter g) {
        checkScaling(g);
        java.util.List<Node> nodes = gameHandler.getNodes();
        java.util.List<UnitGroup> groups = gameHandler.getUnitGroups();
        if (nodes != null && groups != null) {
            Node node;
            int r = convertSize(GameConstants.NODE_RADIUS);
            for (int i = 0; i < nodes.size(); i++) {
                node = nodes.get(i);
                PaintStyle style = new PaintStyle();
                style.color = GameColors.getColorForOwner(node.getOwner());
                g.drawCircle(style, convertX(node.getX()), convertY(node.getY()), r);
                drawNodeUnits(g, node);
            }
            Node underMouse = getNodeUnderMouse(g);
            if (underMouse != null) {
                PaintStyle style = new PaintStyle();
                style.color = Color.DARK_GRAY;
                style.strokeWidth = convertSize(GameConstants.OUTLINE_SIZE);
                style.fill = false;
                g.drawCircle(style, convertX(underMouse.getX()), convertY(underMouse.getY()), r);
            }
            if (selectedNode != null) {
                PaintStyle style = new PaintStyle();
                style.color = Color.PINK;
                style.strokeWidth = convertSize(GameConstants.OUTLINE_SIZE);
                style.fill = false;
                g.drawCircle(style, convertX(selectedNode.getX()), convertY(selectedNode.getY()), r);
            }
            for (int i = 0; i < groups.size(); i++) {
                UnitGroup group = groups.get(i);
                drawUnitGroup(g, group);
            }
        } else {
            String lastWinner = gameHandler.getLastWinner();
            if (lastWinner != null) {
                PaintStyle style = new PaintStyle();
                style.color = Color.GRAY;
                style.textSize = convertSize(GameConstants.WIN_TEXT_SIZE);
                style.alignX = PaintStyle.Align.CENTER;
                style.alignY = PaintStyle.Align.CENTER;
                g.drawText(style, lastWinner + " wins!", convertX(GameConstants.FIELD_SIZE / 2), convertY(GameConstants.FIELD_SIZE / 2));
            }
        }
        if (gameHandler.isSpectating()) {
            PaintStyle style = new PaintStyle();
            style.color = Color.RED;
            style.textSize = convertSize(GameConstants.SPECTATING_TEXT_SIZE);
            style.alignY = PaintStyle.Align.BOTTOM;
            g.drawText(style, "Spectating", convertX(0), convertY(100));
        }
        // draw game start message
        if (gameHandler.hasGameData() && !gameHandler.isInProgress()) {
            PaintStyle style = new PaintStyle();
            style.color = Color.RED;
            style.textSize = convertSize(GameConstants.GAMEREADY_TEXT_SIZE);
            style.alignX = PaintStyle.Align.CENTER;
            style.alignY = PaintStyle.Align.CENTER;
            g.drawText(style, "Game starting soon", convertX(GameConstants.FIELD_SIZE / 2), convertY(GameConstants.FIELD_SIZE / 2));
        }
        PaintStyle style = new PaintStyle();
        style.color = Color.RED;
        style.textSize = convertSize(GameConstants.SMALL_TEXT_SIZE);
        double fps = 1000 / ((double) (System.currentTimeMillis() - lastPaint));
        String fpsText = ((int) fps) + " fps";
        g.drawText(style, fpsText, convertX(0), convertY(0));
        lastPaint = System.currentTimeMillis();
    }

    private void drawNodeUnits(IPainter g, Node node) {
        for (int owner : node.getUnitOwners()) {
            int seed = getGroupSeed(node, owner);
            drawUnitGroup(g, seed, seed, 1, owner, node.getUnits(owner), node.getX(), node.getY());
        }
    }

    private void drawUnitGroup(IPainter g, UnitGroup group) {
        int owner = group.getOwner();
        drawUnitGroup(g, getGroupSeed(group.getSource(), owner), getGroupSeed(group.getDest(), owner), group.getProgress(), owner, group.getUnits(), group.getX(), group.getY());
    }

    public void checkScaling(IPainter g) {
        int minDimension;
        if (g.getWidth() < g.getHeight()) {
            xOffset = 0;
            yOffset = (g.getHeight() - g.getWidth()) / 2;
            minDimension = g.getWidth();
        } else {
            xOffset = (g.getWidth() - g.getHeight()) / 2;
            yOffset = 0;
            minDimension = g.getHeight();
        }
        scale = ((double) minDimension) / GameConstants.FIELD_SIZE;
    }


    private void drawUnitGroup(IPainter g, int seed1, int seed2, double progress, int owner, int numUnits, double x, double y) {
        Random rand1 = new Random(seed1);
        Random rand2 = new Random(seed2);

        int radius = convertSize(GameConstants.UNIT_RADIUS);
        Color color = GameColors.getColorForOwner(owner).darker();
        PaintStyle fillStyle = new PaintStyle();
        fillStyle.color = color;
        PaintStyle outlineStyle = new PaintStyle();
        outlineStyle.color = Color.BLACK;
        outlineStyle.fill = false;
        for (int j = 0; j < numUnits; j++) {
            Point coords1 = getRandomUnitCoords(rand1, x, y);
            Point coords2 = getRandomUnitCoords(rand2, x, y);

            int unitX = (int) (coords1.getX() + (coords2.getX() - coords1.getX()) * progress);
            int unitY = (int) (coords1.getY() + (coords2.getY() - coords1.getY()) * progress);

            // draw the unit
            g.drawCircle(fillStyle, unitX, unitY, radius);
            g.drawCircle(outlineStyle, unitX, unitY, radius);
        }
    }

    private int getGroupSeed(Node node, int owner) {
        return gameHandler.indexOf(node) * (owner + 1);
    }


    public void select(Node node) {
        if (selectedNode == null && node != null && gameHandler.isInProgress()) {
            int pos = gameHandler.getPosition();
            if ((pos != -1 && pos == node.getOwner()) || node.getUnits(pos) > 0) {
                selectedNode = node;
                lastSelected = node;
            }
        } else if (selectedNode != null && selectedNode == node) {
            selectedNode = null;
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

    private int convertX(double x) {
        return (int) (xOffset + x * scale);
    }

    private int convertY(double y) {
        return (int) (yOffset + y * scale);
    }

    private int convertSize(double s) {
        return (int) (s * scale);
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

    public boolean isMouseOverNode(Node node, Point mouse) {
        if(mouse == null) {
            return false;
        }
        return MathHelper.distance(mouse.getX(), mouse.getY(), convertX(node.getX()), convertY(node.getY())) <= convertSize(GameConstants.NODE_RADIUS);
    }

    public boolean isMouseOverNode(Node node, IPainter graphics) {
        return isMouseOverNode(node, graphics.getMousePos());
    }

    public Node getNodeUnderMouse(IPainter g) {
        List<Node> nodes = gameHandler.getNodes();
        Node tr = null;
        if (nodes != null) {
            for (Node node : nodes) {
                if (isMouseOverNode(node, g)) {
                    tr = node;
                }
            }
        }
        return tr;
    }
}
