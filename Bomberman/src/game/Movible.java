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
    boolean checkMove(Point newDir);
    boolean startMove(Point pt);
    void move(Point newDir);
    void finishMove(Point pt);
}
