package game;

import java.awt.*;

public class Fondo extends Cosa {

    int picSwitch = 0;

    public Fondo(Point point) {
        super(point, "/game/pic/tile_stage1.bmp", 64, 0, 32, 32);
    }
}
