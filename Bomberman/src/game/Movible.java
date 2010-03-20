/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package game;

import java.awt.Point;

/**
 *
 * @author Sergio
 */
public interface Movible {
    boolean checkMove(Point newDir, Cosa[][][] mapa);
    boolean startMove(Point pt);
    void move(Point newDir, Cosa[][][] map);
    void finishMove(Point pt);
}
