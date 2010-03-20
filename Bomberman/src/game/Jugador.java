package game;

import java.awt.*;
import java.util.*;

public class Jugador extends Indestruible implements Movible{

    int picSwitch = 0;
    Point curDir = new Point(0, 1);
    Timer timer;
    private char id;
    private Actualizable interfaz;

    public Jugador(Point point, int playerNo, char id) {
        super(point, "./pic/bomberman" + playerNo + ".gif", 0, 192, 32, 64);
        this.id = id;
    }
    
    public Jugador(Point point, int playerNo, Actualizable interfaz) {
        super(point, "./pic/bomberman" + playerNo + ".gif", 0, 192, 32, 64);
        this.interfaz = interfaz;
    }

    public boolean checkMove(Point newDir, Cosa[][][] mapa) {
        boolean permitido = false;
        int newX = (int) (getPoint().getX() + newDir.getX()), newY = (int) (getPoint().getY() + newDir.getY());
        startMove(newDir);
        if (newDir.equals(new Point(0, -1))) {
            if (!(getSmallSizeY() == -1 &&
                    (mapa[newX][newY][0] instanceof Indestruible ||
                     mapa[newX][newY][1] instanceof Indestruible ||
                     mapa[newX][newY][4] instanceof Indestruible)) &&
                    getSmallSizeX() == 0) {
                permitido = true;
            }
        } else if (newDir.equals(new Point(0, 1))) {
            if (!(getSmallSizeY() == 1 &&
                    (mapa[newX][newY][0] instanceof Indestruible ||
                     mapa[newX][newY][4] instanceof Indestruible ||
                     mapa[newX][newY][1] instanceof Indestruible)) &&
                     getSmallSizeX() == 0) {
                permitido = true;
            }
        } else if (newDir.equals(new Point(-1, 0))) {
            if (!(getSmallSizeX() == -1 &&
                    (mapa[newX][newY][0] instanceof Indestruible ||
                     mapa[newX][newY][4] instanceof Indestruible ||
                     mapa[newX][newY][1] instanceof Indestruible)) &&
                     getSmallSizeY() == 0) {
                permitido = true;
            }
        } else if (newDir.equals(new Point(1, 0))) {
            if (!(getSmallSizeX() == 1 &&
                    (mapa[newX][newY][0] instanceof Indestruible ||
                     mapa[newX][newY][4] instanceof Indestruible ||
                     mapa[newX][newY][1] instanceof Indestruible)) &&
                     getSmallSizeY() == 0) {
                permitido = true;
            }
        }
        reverseDir(newDir);

        return permitido;
    }

    public boolean startMove(Point pt) {
        curDir = new Point(pt);
        if (pt.equals(new Point(0, -1))) {
            image = orgImage.getSubimage(0 + Math.abs(picSwitch) * 32, 128, 32, 64);
        } else if (pt.equals(new Point(0, 1))) {
            image = orgImage.getSubimage(0 + Math.abs(picSwitch) * 32, 192, 32, 64);
        } else if (pt.equals(new Point(-1, 0))) {
            image = orgImage.getSubimage(0 + Math.abs(picSwitch) * 32, 0, 32, 64);
        } else if (pt.equals(new Point(1, 0))) {
            image = orgImage.getSubimage(0 + Math.abs(picSwitch) * 32, 64, 32, 64);
        }
        
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

    public void move(Point newDir, Cosa[][][] map){
        if (checkMove(newDir, map)) {
            if (startMove(newDir)) {
                map[(int) getPoint().getX()][(int) getPoint().getY()][4] = null;
                Point temp = new Point(newDir.x + getPoint().x, newDir.y + getPoint().y);
                map[(int) temp.getX()][(int) temp.getY()][4] = this;
                setPoint(temp);
            }
        }
    }

    public void ponerBomba(Cosa[][][] map){
        if (map[(int) getPoint().getX()][(int) getPoint().getY()][1] == null) {
            map[(int) getPoint().getX()][(int) getPoint().getY()][1] = new Bomba(new Point(getPoint()), 3, map, interfaz);
        }
    }

    @Override
    public boolean explode() {
        image = orgImage.getSubimage(96, 192, 32, 64);
        return true;
    }

    public char getId() {
        return id;
    }

    public void setId(char id) {
        this.id = id;
    }

    class LoadSteps extends TimerTask {

        int picNo = 0;

        public void run() {
        }
    }

    public void switchPic() {
        if (picSwitch++ % 3 == 0) {
            picSwitch = -2;
        }
    }

    public void finishMove(Point pt) {

        if (pt.equals(new Point(0, -1))) {
            image = orgImage.getSubimage(0, 128, 32, 64);
        } else if (pt.equals(new Point(0, 1))) {
            image = orgImage.getSubimage(0, 192, 32, 64);
        } else if (pt.equals(new Point(-1, 0))) {
            image = orgImage.getSubimage(0, 0, 32, 64);
        } else if (pt.equals(new Point(1, 0))) {
            image = orgImage.getSubimage(0, 64, 32, 64);
        }
    }
}
