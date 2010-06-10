/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package game.server;

import game.Actualizable;
import game.Cosa;
import game.Destruible;
import game.Fondo;
import game.Indestruible;
import game.Jugador;
import game.Muro;
import game.Obstaculo;
import game.server.Server.Conexion;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 *
 * @author Sergio
 */
public class MapFactory {

    private final static int MAPWIDTH = 15;
    private final static int MAPHEIGHT = 11;
    private Cosa map[][][];
    private String mapa[];
    private Actualizable updatable;

    public MapFactory(Actualizable updatable) {
        mapa = new String[MAPHEIGHT];
        this.updatable = updatable;
    }

    public boolean checkMove(Point newDir, Jugador man) {
        boolean needRe = false;
        int newX = (int) (man.getPoint().getX() + newDir.getX()), newY = (int) (man.getPoint().getY() + newDir.getY());
        man.startMove(newDir);
        if (newDir.equals(new Point(0, -1))) {
            if (!(man.getSmallSizeY() == -1 && (map[newX][newY][0] instanceof Indestruible || map[newX][newY][1] instanceof Indestruible)) && man.getSmallSizeX() == 0) {
                needRe = true;
            }
        } else if (newDir.equals(new Point(0, 1))) {
            if (!(man.getSmallSizeY() == 1 && (map[newX][newY][0] instanceof Indestruible || map[newX][newY][1] instanceof Indestruible)) && man.getSmallSizeX() == 0) {
                needRe = true;
            }
        } else if (newDir.equals(new Point(-1, 0))) {
            if (!(man.getSmallSizeX() == -1 && (map[newX][newY][0] instanceof Indestruible || map[newX][newY][1] instanceof Indestruible)) && man.getSmallSizeY() == 0) {
                needRe = true;
            }
        } else if (newDir.equals(new Point(1, 0))) {
            if (!(man.getSmallSizeX() == 1 && (map[newX][newY][0] instanceof Indestruible || map[newX][newY][1] instanceof Indestruible)) && man.getSmallSizeY() == 0) {
                needRe = true;
            }
        }
        man.reverseDir(newDir);
        return needRe;
    }

    public void generar(List<Conexion> threadPlayers) {
        map = new Cosa[MAPWIDTH][MAPHEIGHT][5];
        try {
            InputStream fstream = this.getClass().getResourceAsStream("../map/"+threadPlayers.size()+".map");
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            int line = 0;
            int player = 0;
            while ((strLine = br.readLine()) != null) {
                System.out.println(strLine);
                mapa[line] = strLine;
                for (int i = 0; i < strLine.length(); i++) {
                    if (strLine.charAt(i) == 'X') {
                        map[i][line][0] = new Muro(new Point(i, line));
                    } else {
                        map[i][line][0] = new Fondo(new Point(i, line));
                    }
                    if (strLine.charAt(i) >= 'a' && strLine.charAt(i) <= 'z') {
                        player++;
                        Jugador man = new Jugador(new Point(i, line), player, strLine.charAt(i), map, updatable);
                        map[i][line][4] = man;
                        threadPlayers.get(player - 1).setJugador(man);
                    } else if (strLine.charAt(i) == 'O') {
                        map[i][line][0] = new Obstaculo(new Point(i, line));
                    } else if (strLine.charAt(i) == 'F') {
                        map[i][line][1] = new Destruible(new Point(i, line), map, updatable);
                    }
                }
                line++;
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Mapa Error: " + e.getMessage());
        }
    }

    public String[] getMapaAsArray() {
        return mapa;
    }

    public Cosa[][][] getMapa() {
        return map;
    }
}
