package game;

import java.awt.*;
import java.util.*;

public class Destruible extends Indestruible {

    int picSwitch = 0;
    int imageLen = 8;
    Timer timer;
    Bomberman bomberman;

    public Destruible(Point point) {
        super(point, "./pic/tile_stage1.bmp", 0, 32, 32, 32);
    }
    
    public Destruible(Point point, Bomberman bomberman) {
        super(point, "./pic/tile_stage1.bmp", 0, 32, 32, 32);
        this.bomberman = bomberman;

    }

    @Override
    public boolean explode() {
        timer = new Timer();
        timer.schedule(new LoadSteps(), 0, 20);
        return false;
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
            image = orgImage.getSubimage(0 + picNo * 32, 32, 32, 32);
            bomberman.repaint();
        }
    }
}
