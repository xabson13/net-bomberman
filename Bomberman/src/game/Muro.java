package game;

import java.awt.*;

public class Muro extends Indestruible {

    int picSwitch = 0;

    public Muro(Point point) {
        super(point, "/game/pic/tile_stage1.bmp", 128, 0, 32, 32);
    }
}
