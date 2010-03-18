package game;

import game.server.ComObject;
import java.awt.*;
import java.awt.event.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import java.io.*;
import java.util.*;

public class Bomberman extends Entorno {

    boolean isKeyPressed[] = new boolean[4];
    public Jugador[] man = new Jugador[2];
    Point fourDir[] = {new Point(0, -1), new Point(0, 1), new Point(-1, 0), new Point(1, 0)};
    public Timer timer[] = new Timer[4];

    public Bomberman() {
        super();
        conexion = new Conexion(this);
    }

    public static void main(String[] args) {
        new Bomberman();
    }

    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getActionCommand().equals("About")) {
            JOptionPane.showMessageDialog(null, "CSC3100: Software Engineering\nProject: Bomberman\n(c) Wong Man Tik");
        }
        if (actionEvent.getActionCommand().equals("Reset Game")) {
            conexion.setListo();
        }
        if (actionEvent.getActionCommand().equals("Quit Game")) {
            System.exit(1);
        }
        if (actionEvent.getActionCommand().equals("Save Moves")) {
            conexion.empezarJuego();
        }
        if (actionEvent.getActionCommand().equals("Load  Moves")) {
        }
        if (actionEvent.getActionCommand().equals("New Game")) {
            //newGame();

            conexion.start();
        }
    }

    public void pedirUsuario() {
        ComObject cobj = new ComObject(100);
        String name = "sergio2";
        cobj.setTag(name);
        conexion.enviarPeticion(cobj);
    }

    public String getLoadFilePath() {
        FileDialog fd = new FileDialog(new Frame(), "File Select");
        fd.show();
        String name = fd.getFile();
        if (name == null) {
            return null;
        }
        fd.dispose();
        return fd.getDirectory() + name;
    }

    public String getSaveFilePath() {
        FileDialog fd = new FileDialog(new Frame(), "File Save", FileDialog.SAVE);
        fd.show();
        String name = fd.getFile();
        if (name == null) {
            return null;
        }
        fd.dispose();
        return fd.getDirectory() + name;
    }

    public void saveMove() {
        try {
            FileWriter fstream = new FileWriter(getSaveFilePath());
            BufferedWriter out = new BufferedWriter(fstream);
            out.write("\n");

            out.close();
        } catch (IOException e) {//Catch exception if any
            JOptionPane.showMessageDialog(null, "Cannot write to the specific file.");
        } catch (NullPointerException e) {
        }

    }

    public void loadMove() {
        try {
            FileInputStream fstream = new FileInputStream(getLoadFilePath());
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            while ((strLine = br.readLine()) != null) {
            }
            in.close();
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, "File not found.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "File not found.");
        } catch (InputMismatchException e) {
            JOptionPane.showMessageDialog(null, "File format mismatch!");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "File format mismatch!");
        } catch (NullPointerException e) {
        }
    }

    protected void newGame(String mapa[]) {

        map = new Cosa[width][height][5];

        try {
            /*InputStream fstream = this.getClass().getResourceAsStream("./map/1-1.map");
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));*/
            String strLine;
            int line = 0;
            int player = 0;
            for (line = 0; line < height; line++) {
                strLine = mapa[line];
                for (int i = 0; i < strLine.length(); i++) {
                    if (strLine.charAt(i) == 'X') {
                        map[i][line][0] = new Muro(new Point(i, line));
                    } else {
                        map[i][line][0] = new Fondo(new Point(i, line));
                    }
                    if (strLine.charAt(i) >= 'a' && strLine.charAt(i) <= 'z') {
                        player++;
                        man[player-1] = new Jugador(new Point(i, line), player, this);
                        man[player-1].setId(strLine.charAt(i));
                        map[i][line][4] = man[player-1];
                    } else if (strLine.charAt(i) == 'O') {
                        map[i][line][0] = new Obstaculo(new Point(i, line));
                    } else if (strLine.charAt(i) == 'F') {
                        map[i][line][1] = new Destruible(new Point(i, line), this);
                    }
                }
            }
            //in.close();
        } catch (Exception e) { //Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }

        repaint();
    }

    public boolean checkMove(Point newDir, Jugador player) {
        boolean permitido = false;
        int newX = (int) (player.getPoint().getX() + newDir.getX()), newY = (int) (player.getPoint().getY() + newDir.getY());

        if (newDir.equals(new Point(0, -1))) {
            if (!(player.getSmallSizeY() == -1 && (map[newX][newY][0] instanceof Indestruible || map[newX][newY][1] instanceof Indestruible)) && player.getSmallSizeX() == 0) {
                permitido = true;
            }
        } else if (newDir.equals(new Point(0, 1))) {
            if (!(player.getSmallSizeY() == 1 && (map[newX][newY][0] instanceof Indestruible || map[newX][newY][1] instanceof Indestruible)) && player.getSmallSizeX() == 0) {
                permitido = true;
            }
        } else if (newDir.equals(new Point(-1, 0))) {
            if (!(player.getSmallSizeX() == -1 && (map[newX][newY][0] instanceof Indestruible || map[newX][newY][1] instanceof Indestruible)) && player.getSmallSizeY() == 0) {
                permitido = true;
            }
        } else if (newDir.equals(new Point(1, 0))) {
            if (!(player.getSmallSizeX() == 1 && (map[newX][newY][0] instanceof Indestruible || map[newX][newY][1] instanceof Indestruible)) && player.getSmallSizeY() == 0) {
                permitido = true;
            }
        }

        return permitido;
    }

    private Jugador getPlayerById(char id){
        for( Jugador j : man ){
            if( j.getId() == id )
                return j;
        }
        return null;
    }

    public void move(Point newDir, Jugador j) {
        boolean permitido = true;
        j.move(newDir);
        permitido = checkMove(newDir, j);
        j.reverseDir(newDir);

        if (permitido) {
            if (j.move(newDir)) {
                map[(int) j.getPoint().getX()][(int) j.getPoint().getY()][4] = null;
                Point temp = new Point(newDir.x + j.getPoint().x, newDir.y + j.getPoint().y);
                map[(int) temp.getX()][(int) temp.getY()][4] = j;
                j.setPoint(temp);
            }
        }
        j.switchPic();
        repaint();
    }

    public void keyPressed(KeyEvent e) {

        if (e.getKeyCode() >= 37 && e.getKeyCode() <= 40 && timer[e.getKeyCode() - 37] == null) {
            isKeyPressed[e.getKeyCode() - 37] = true;
            ComObject cobj = new ComObject(103);
            cobj.addObject(e.getKeyCode());
            conexion.enviarPeticion(cobj);
            

        }

    }

    public void mover(int kcode, char movId) {
        Jugador j = getPlayerById(movId);
        if (kcode == 32 && map[(int) j.getPoint().getX()][(int) j.getPoint().getY()][1] == null) {
            map[(int) j.getPoint().getX()][(int) j.getPoint().getY()][1] = new Bomba(new Point(j.getPoint()), 3, this);
        }
        if (kcode >= 37 && kcode <= 40 && timer[kcode - 37] == null) {
            Point movept = new Point(0, 0);
            isKeyPressed[kcode - 37] = true;
            switch (kcode) {
                case KeyEvent.VK_UP:
                    movept = new Point(0, -1);
                    break;
                case KeyEvent.VK_DOWN:
                    movept = new Point(0, 1);
                    break;
                case KeyEvent.VK_LEFT:
                    movept = new Point(-1, 0);
                    break;
                case KeyEvent.VK_RIGHT:
                    movept = new Point(1, 0);
                    break;
            }

            /*for (int i = 0; i < isKeyPressed.length; i++) {
                if (timer[i] != null) {
                    timer[i].cancel();
                }
            }*/

            //timer[kcode - 37] = new Timer();
            (new LoadSteps(movept, j)).run();
            /*try {
                repaint();
                Thread.sleep(1);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            timer[kcode - 37].cancel();
            timer[kcode - 37] = null;*/
            
        }
    }

    class LoadSteps extends TimerTask {

        Point pt = new Point(0, 0);
        Jugador j = null;

        public LoadSteps(Point pt, Jugador j) {
            this.j = j;
            this.pt = pt;
        }

        public void run() {
            move(pt, j);
        }
    }

    public boolean anyKeyPressed() {
        for (int i = 0; i < isKeyPressed.length; i++) {
            if (isKeyPressed[i]) {
                return true;
            }
        }
        return false;
    }

    public void removeObject(Point p, int layer) {
        map[(int) p.getX()][(int) p.getY()][layer] = null;
        repaint();
    }

    public void explode(Point p) {
        int flameLen = ((Bomba) (map[(int) p.getX()][(int) p.getY()][1])).getFlameLen();
        map[(int) p.getX()][(int) p.getY()][1] = null;
        map[(int) p.getX()][(int) p.getY()][0] = new Fondo(new Point(p));
        map[(int) p.getX()][(int) p.getY()][1] = new Flama(new Point(p), 0, this);
        for (int k = 0; k < fourDir.length; k++) {
            for (int i = 1; i <= flameLen; i++) {
                boolean re = true;
                int flameType = 0;
                for (int j = 0; j < 5; j++) {
                    if (map[(int) (p.getX() + fourDir[k].getX() * i)][(int) (p.getY() + fourDir[k].getY() * i)][j] != null) {
                        re = re && map[(int) (p.getX() + fourDir[k].getX() * i)][(int) (p.getY() + fourDir[k].getY() * i)][j].explode();
                    }
                }
                if (!re) {
                    break;
                }

                if (fourDir[k].getY() == -1) {//UP
                    flameType = 5;
                    if (i == flameLen && !(map[(int) (p.getX() + fourDir[k].getX() * (1 + i))][(int) (p.getY() + fourDir[k].getY() * (1 + i))][1] instanceof Flama)) {
                        flameType = 6;
                    }
                }
                if (fourDir[k].getY() == 1) {//DOWN
                    flameType = 5;
                    if (i == flameLen && !(map[(int) (p.getX() + fourDir[k].getX() * (1 + i))][(int) (p.getY() + fourDir[k].getY() * (1 + i))][1] instanceof Flama)) {
                        flameType = 7;
                    }
                }
                if (fourDir[k].getX() == -1) {//LEFT
                    flameType = 2;
                    if (i == flameLen && !(map[(int) (p.getX() + fourDir[k].getX() * (1 + i))][(int) (p.getY() + fourDir[k].getY() * (1 + i))][1] instanceof Flama)) {
                        flameType = 3;
                    }
                }
                if (fourDir[k].getX() == 1) {//RIGT
                    flameType = 2;
                    if (i == flameLen && !(map[(int) (p.getX() + fourDir[k].getX() * (1 + i))][(int) (p.getY() + fourDir[k].getY() * (1 + i))][1] instanceof Flama)) {
                        flameType = 4;
                    }
                }
                if (map[(int) (p.getX() + fourDir[k].getX() * i)][(int) (p.getY() + fourDir[k].getY() * i)][1] == null || (map[(int) (p.getX() + fourDir[k].getX() * i)][(int) (p.getY() + fourDir[k].getY() * i)][1] instanceof Flama && ((Flama) map[(int) (p.getX() + fourDir[k].getX() * i)][(int) (p.getY() + fourDir[k].getY() * i)][1]).getType() != 0)) {
                    map[(int) (p.getX() + fourDir[k].getX() * i)][(int) (p.getY() + fourDir[k].getY() * i)][1] = new Flama(new Point((int) (p.getX() + fourDir[k].getX() * i), (int) (p.getY() + fourDir[k].getY() * i)), flameType, this);
                }
            }
        }
        repaint();
    }

    public void keyReleased(KeyEvent e) {
      /*  if (e.getKeyCode() >= 37 && e.getKeyCode() <= 40 && isKeyPressed[e.getKeyCode() - 37]) {
            timer[e.getKeyCode() - 37].cancel();
            timer[e.getKeyCode() - 37] = null;
            isKeyPressed[e.getKeyCode() - 37] = false;
        }
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                man.finishMove(new Point(0, -1));
                break;
            case KeyEvent.VK_DOWN:
                man.finishMove(new Point(0, 1));
                break;
            case KeyEvent.VK_LEFT:
                man.finishMove(new Point(-1, 0));
                break;
            case KeyEvent.VK_RIGHT:
                man.finishMove(new Point(1, 0));
                break;
        }
        repaint();*/
    }

    public void keyTyped(KeyEvent e) {
    }
}
