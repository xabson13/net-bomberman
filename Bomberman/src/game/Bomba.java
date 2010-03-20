package game;

import java.awt.*;
import java.util.*;

public class Bomba extends Indestruible {

    int imageLen = 3;
    int flameLen;
    Timer timer, explodeTimer;
    Point fourDir[] = {new Point(0, -1), new Point(0, 1), new Point(-1, 0), new Point(1, 0)};
    Actualizable interfaz;
    Cosa[][][] map;

    public Bomba(Point point, int flameLen, Cosa[][][] map, Actualizable interfaz) {
        super(point, "./pic/bombs.gif", 0, 0, 32, 32);
        this.flameLen = flameLen;
        this.interfaz = interfaz;
        this.map = map;

        timer = new Timer();
        explodeTimer = new Timer();
        explodeTimer.schedule(new LoadExplode(), 3000);
        timer.schedule(new LoadSteps(), 0, 280);
    }

    public int getFlameLen() {
        return flameLen;
    }

    @Override
    public boolean explode() {
        Point p = getPoint();
        timer.cancel();
        explodeTimer.cancel();
        int flameLen = ((Bomba) (map[(int) p.getX()][(int) p.getY()][1])).getFlameLen();
        map[(int) p.getX()][(int) p.getY()][1] = null;
        map[(int) p.getX()][(int) p.getY()][0] = new Fondo(new Point(p));
        map[(int) p.getX()][(int) p.getY()][1] = new Flama(new Point(p), 0, map, interfaz);
        for (int k = 0; k < fourDir.length; k++) {
            for (int i = 1; i <= flameLen; i++) {
                boolean re = true;
                int flameType = 0;
                for (int j = 0; j < 5; j++) {
                    if (map[(int) (p.getX() + fourDir[k].getX() * i)][(int) (p.getY() + fourDir[k].getY() * i)][j] != null) {
                        re = re && map[(int) (p.getX() + fourDir[k].getX() * i)][(int) (p.getY() + fourDir[k].getY() * i)][j].explode();
                    }
                }
                if (!re) {
                    break;
                }

                if (fourDir[k].getY() == -1) {//UP
                    flameType = 5;
                    if (i == flameLen && !(map[(int) (p.getX() + fourDir[k].getX() * (1 + i))][(int) (p.getY() + fourDir[k].getY() * (1 + i))][1] instanceof Flama)) {
                        flameType = 6;
                    }
                }
                if (fourDir[k].getY() == 1) {//DOWN
                    flameType = 5;
                    if (i == flameLen && !(map[(int) (p.getX() + fourDir[k].getX() * (1 + i))][(int) (p.getY() + fourDir[k].getY() * (1 + i))][1] instanceof Flama)) {
                        flameType = 7;
                    }
                }
                if (fourDir[k].getX() == -1) {//LEFT
                    flameType = 2;
                    if (i == flameLen && !(map[(int) (p.getX() + fourDir[k].getX() * (1 + i))][(int) (p.getY() + fourDir[k].getY() * (1 + i))][1] instanceof Flama)) {
                        flameType = 3;
                    }
                }
                if (fourDir[k].getX() == 1) {//RIGT
                    flameType = 2;
                    if (i == flameLen && !(map[(int) (p.getX() + fourDir[k].getX() * (1 + i))][(int) (p.getY() + fourDir[k].getY() * (1 + i))][1] instanceof Flama)) {
                        flameType = 4;
                    }
                }
                if (map[(int) (p.getX() + fourDir[k].getX() * i)][(int) (p.getY() + fourDir[k].getY() * i)][1] == null || (map[(int) (p.getX() + fourDir[k].getX() * i)][(int) (p.getY() + fourDir[k].getY() * i)][1] instanceof Flama && ((Flama) map[(int) (p.getX() + fourDir[k].getX() * i)][(int) (p.getY() + fourDir[k].getY() * i)][1]).getType() != 0)) {
                    map[(int) (p.getX() + fourDir[k].getX() * i)][(int) (p.getY() + fourDir[k].getY() * i)][1] = new Flama(new Point((int) (p.getX() + fourDir[k].getX() * i), (int) (p.getY() + fourDir[k].getY() * i)), flameType, map, interfaz);
                }
            }
        }
        return true;
    }

    class LoadExplode extends TimerTask {

        public void run() {
            explode();
        }
    }

    class LoadSteps extends TimerTask {

        int picNo = 0;

        public void run() {
            picNo = ++picNo % imageLen;
            image = orgImage.getSubimage(0 + picNo * 32, 0, 32, 32);
            interfaz.refresh();
        }
    }
}
