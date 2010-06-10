package game;

import java.awt.*;

public class Obstaculo extends Indestruible {

    int picSwitch = 0;

    public Obstaculo(Point point) {
        super(point, "/game/pic/tile_stage1.bmp", 0, 0, 32, 32);
    }
}
