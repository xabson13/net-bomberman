package game;

import java.awt.*;

public abstract class Indestruible extends Cosa {

    public Indestruible(Point point, String path, int x, int y, int sizex, int sizey) {
        super(point, path, x, y, sizex, sizey);
    }

    @Override
    public boolean explode() {
        return false;
    }
}
