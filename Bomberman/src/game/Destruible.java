package game;

import java.awt.*;
import java.util.*;

public class Destruible extends Indestruible {

    int picSwitch = 0;
    int imageLen = 8;
    Timer timer;
    Cosa[][][] map;
    private Actualizable interfaz;
    
    public Destruible(Point point, Cosa[][][] map, Actualizable interfaz) {
        super(point, "/game/pic/tile_stage1.bmp", 0, 32, 32, 32);
        this.interfaz = interfaz;
        this.map = map;

    }

    @Override
    public boolean explode() {
        timer = new Timer();
        timer.schedule(new LoadSteps(), 0, 20);
        return false;
    }

    class LoadSteps extends TimerTask {

        int picNo = 0;
        Point p = getPoint();
        public void run() {
            picNo++;
            if (picNo == imageLen) {
                timer.cancel();
                map[(int) p.getX()][(int) p.getY()][1] = null;
                interfaz.refresh();
                return;
            }
            image = orgImage.getSubimage(0 + picNo * 32, 32, 32, 32);
            interfaz.refresh();
        }
    }
}
