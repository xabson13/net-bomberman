package game;

import java.awt.*;
import java.awt.image.*;
import javax.imageio.*;

public abstract class Cosa {

    protected Point point;
    public static final double MAXSMALLSIZE = 3;
    protected int smallSizeX = 0, smallSizeY = 0;
    protected BufferedImage image;
    protected BufferedImage orgImage;

    public Cosa(Point point, String path, int x, int y, int sizex, int sizey) {
        this.point = point;
        try {
            orgImage = ImageIO.read(this.getClass().getResourceAsStream(path));
            image = orgImage.getSubimage(x, y, sizex, sizey);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean explode() {
        return true;
    }

    public boolean move(Point pt) {

        smallSizeY += pt.y;
        smallSizeX += pt.x;

        if (smallSizeX == (int) MAXSMALLSIZE / 2 + 1) {
            smallSizeX = (int) MAXSMALLSIZE / 2 * (-1);
            return true;
        } else if (smallSizeX == (int) MAXSMALLSIZE / 2 * (-1) - 1) {
            smallSizeX = (int) MAXSMALLSIZE / 2;
            return true;
        } else if (smallSizeY == (int) MAXSMALLSIZE / 2 + 1) {
            smallSizeY = (int) MAXSMALLSIZE / 2 * (-1);
            return true;
        } else if (smallSizeY == (int) MAXSMALLSIZE / 2 * (-1) - 1) {
            smallSizeY = (int) MAXSMALLSIZE / 2;
            return true;
        }
        return false;
    }

    public void reverseDir(Point pt) {
        smallSizeY -= pt.y;
        smallSizeX -= pt.x;
        if (smallSizeX == (int) MAXSMALLSIZE / 2 + 1) {
            smallSizeX = (int) MAXSMALLSIZE / 2 * (-1);
        } else if (smallSizeX == (int) MAXSMALLSIZE / 2 * (-1) - 1) {
            smallSizeX = (int) MAXSMALLSIZE / 2;
        } else if (smallSizeY == (int) MAXSMALLSIZE / 2 + 1) {
            smallSizeY = (int) MAXSMALLSIZE / 2 * (-1);
        } else if (smallSizeY == (int) MAXSMALLSIZE / 2 * (-1) - 1) {
            smallSizeY = (int) MAXSMALLSIZE / 2;
        }

    }

    public int getSmallSizeX() {
        return smallSizeX;
    }

    public int getSmallSizeY() {
        return smallSizeY;
    }

    public BufferedImage getImage() {
        return image;
    }

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }
}
