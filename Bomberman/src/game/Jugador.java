package game;

import java.awt.*;
import java.util.*;

public class Jugador extends Cosa {

    int picSwitch = 0;
    Point curDir = new Point(0, 1);
    Timer timer;
    private char id;

    public Jugador(Point point, int playerNo, char id) {
        super(point, "./pic/bomberman" + playerNo + ".gif", 0, 192, 32, 64);
        this.id = id;
    }
    
    public Jugador(Point point, int playerNo, Bomberman bomberman) {
        super(point, "./pic/bomberman" + playerNo + ".gif", 0, 192, 32, 64);
    }

    @Override
    public boolean move(Point pt) {
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
        return super.move(pt);
    }

    @Override
    public boolean explode() {
        image = orgImage.getSubimage(96, 192, 32, 64);
        return true;
    }

    public char getId() {
        return id;
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
