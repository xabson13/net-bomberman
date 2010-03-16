package game;

import java.awt.*;
import java.util.*;

public class Bomba extends Indestruible {

    int imageLen = 3;
    int flameLen;
    Timer timer, explodeTimer;
    Bomberman bomberman;

    public Bomba(Point point, int flameLen, Bomberman bomberman) {
        super(point, "./pic/bombs.gif", 0, 0, 32, 32);
        this.flameLen = flameLen;
        this.bomberman = bomberman;
        timer = new Timer();
        explodeTimer = new Timer();
        explodeTimer.schedule(new LoadExplode(), 3000);
        timer.schedule(new LoadSteps(), 0, 280);
    }

    public int getFlameLen() {
        return flameLen;
    }

    public boolean explode() {
        timer.cancel();
        explodeTimer.cancel();
        bomberman.explode(getPoint());
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
            bomberman.repaint();
        }
    }
}
