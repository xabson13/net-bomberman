package game;

import java.awt.*;
import java.util.*;

public class Destruible extends Indestruible {

    int picSwitch = 0;
    int imageLen = 8;
    Timer timer;
    Bomberman bomberman;
    Cosa[][][] map;

    public Destruible(Point point) {
        super(point, "./pic/tile_stage1.bmp", 0, 32, 32, 32);
    }
    
    public Destruible(Point point, Bomberman bomberman) {
        super(point, "./pic/tile_stage1.bmp", 0, 32, 32, 32);
        this.bomberman = bomberman;
        map = bomberman.map;

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
                bomberman.repaint();
                return;
            }
            image = orgImage.getSubimage(0 + picNo * 32, 32, 32, 32);
            bomberman.repaint();
        }
    }
}
