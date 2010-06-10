package game;

import game.server.ComObject;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import javax.swing.JOptionPane;
import java.util.*;

public class Bomberman extends Entorno implements Actualizable{

    boolean isKeyPressed[] = new boolean[4];
    public Jugador[] players = new Jugador[2];
    Point fourDir[] = {new Point(0, -1), new Point(0, 1), new Point(-1, 0), new Point(1, 0)};
    public Timer timer[] = new Timer[4];

    public Bomberman() {
        super();
    }

    public static void main(String[] args) {
        new Bomberman();
    }

    public void actionPerformed(ActionEvent actionEvent) {
        String c = actionEvent.getActionCommand();

        if (c.equals("Conectar")){
            try {
                conexion = new Conexion(this, field_host.getText(), Integer.parseInt(field_port.getText()));
                conectar.setEnabled(false);
                desconectar.setEnabled(true);
                field_host.setEnabled(false);
                field_port.setEnabled(false);
                status.setText("Estado: Conectado, validando...");
                off = false;
                conexion.start();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error de conexion, el servidor no se encuentra disponible.");
            } catch (Exception ex) {

            }
        }

        if (c.equals("Desconectar")){
            try {
                conexion.cerrarConexion();
                conectar.setEnabled(true);
                desconectar.setEnabled(false);
                field_host.setEnabled(true);
                field_port.setEnabled(true);
                status.setText("Estado: Desconectado");
                empezar.setEnabled(false);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "No se puede terminar la conexion.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex);
            }
        }
        if (c.equals("Empezar")){
            conexion.empezarJuego();
            empezar.setEnabled(false);
        }
        if (c.equals("Quit Game")) {
            System.exit(1);
        }

    }

    public void pedirUsuario() {
        ComObject cobj = new ComObject(100);
        String name = JOptionPane.showInputDialog("Nombre: ");
        cobj.setTag(name);
        conexion.enviarPeticion(cobj);
    }

    public void existeUsuario() {
        JOptionPane.showMessageDialog(this, "El usuario ya existe, escoje otro nombre");
        ComObject cobj = new ComObject(100);
        String name = JOptionPane.showInputDialog("Nombre: ");
        cobj.setTag(name);
        conexion.enviarPeticion(cobj);
    }

    protected void newGame(String mapa[]) {

        map = new Cosa[width][height][5];
        java.util.List<Jugador> lplayer = new ArrayList<Jugador>();
        try {
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
                        Jugador nj = new Jugador(new Point(i, line), player, strLine.charAt(i), map, this);
                        nj.setId(strLine.charAt(i));
                        lplayer.add(nj);
                        //players[player-1] = new Jugador(new Point(i, line), player, strLine.charAt(i), map, this);
                        //players[player-1].setId(strLine.charAt(i));
                        map[i][line][4] = nj;
                    } else if (strLine.charAt(i) == 'O') {
                        map[i][line][0] = new Obstaculo(new Point(i, line));
                    } else if (strLine.charAt(i) == 'F') {
                        map[i][line][1] = new Destruible(new Point(i, line), map, this);
                    }
                }
            }
            players = lplayer.toArray(new Jugador[0]);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error: " + e.getMessage());
        }

        repaint();
    }

    private Jugador getPlayerById(char id){
        for( Jugador j : players ){
            if( j.getId() == id )
                return j;
        }
        return null;
    }

    public void ponerBomba(char movId){
            getPlayerById(movId).ponerBomba();
    }

    public void move(Point newDir, Jugador j) {
        j.move(newDir);
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

        if( e.getKeyCode() == 32 ){
            ComObject cobj = new ComObject(104);
            cobj.addObject(e.getKeyCode());
            conexion.enviarPeticion(cobj);
        }

    }

    public void eliminarJugador(char id){
        Jugador j = getPlayerById(id);
        j.eliminate();
    }

    public void mover(int kcode, char movId) {
        Jugador j = getPlayerById(movId);
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
            (new LoadSteps(movept, j)).run(); 
        }
    }

    public void refresh() {
        repaint();
    }

    public void notificar(Object[] params) {
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

    public void keyReleased(KeyEvent e) {
    }

    public void keyTyped(KeyEvent e) {
    }
}
