package game;

import java.awt.*;
import java.util.*;

public class Flama extends Cosa {

    int picSwitch = 0;
    int imageLen = 4;
    Timer timer;
    int picType;
    Bomberman bomberman;

    public Flama(Point point, int picType, Bomberman bomberman) {
        super(point, "./pic/flames.gif", 0, picType * 32, 32, 32);
        this.picType = picType;
        this.bomberman = bomberman;
        timer = new Timer();
        timer.schedule(new LoadSteps(), 0, 100);
    }

    public int getType() {
        return picType;
    }

    class LoadSteps extends TimerTask {

        int picNo = 0;

        public void run() {
            picNo++;
            if (picNo == imageLen) {
                timer.cancel();
                bomberman.removeObject(getPoint(), 1);
                return;
            }
            image = orgImage.getSubimage(0 + picNo * 32, picType * 32, 32, 32);
            bomberman.repaint();
        }
    }
}
