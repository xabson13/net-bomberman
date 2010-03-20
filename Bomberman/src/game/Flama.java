package game;

import java.awt.*;
import java.util.*;

public class Flama extends Cosa {

    int picSwitch = 0;
    int imageLen = 4;
    Timer timer;
    int picType;
    Actualizable interfaz;
    Cosa[][][] map;

    public Flama(Point point, int picType, Cosa[][][] map, Actualizable interfaz) {
        super(point, "./pic/flames.gif", 0, picType * 32, 32, 32);
        this.picType = picType;
        this.interfaz = interfaz;
        this.map = map;
        
        timer = new Timer();
        timer.schedule(new LoadSteps(), 0, 100);
    }

    public int getType() {
        return picType;
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
            image = orgImage.getSubimage(0 + picNo * 32, picType * 32, 32, 32);
            interfaz.refresh();
        }
    }
}
