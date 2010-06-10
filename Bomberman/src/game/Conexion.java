/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

import game.server.ComObject;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Vector;
import javax.swing.JOptionPane;

/**
 *
 * @author Sergio
 */
public class Conexion extends Thread {

    private Vector usuarios;
    private final static int PORT = 8011;
    private final static String HOST = "localhost";
    private Bomberman bomberman;
    private ObjectInputStream entrada;
    private ObjectOutputStream salida;
    private Socket socket;
    private boolean connected;
    private char id;

    Conexion(Bomberman bomberman) throws IOException {
        this.usuarios = new Vector();
        this.connected = false;
        this.bomberman = bomberman;

        socket = new Socket(HOST, PORT);
        salida = new ObjectOutputStream(socket.getOutputStream());
        entrada = new ObjectInputStream(socket.getInputStream());
    }

    Conexion(Bomberman bomberman, String host, int port) throws IOException {
        this.usuarios = new Vector();
        this.connected = false;
        this.bomberman = bomberman;

        socket = new Socket(host, port);
        salida = new ObjectOutputStream(socket.getOutputStream());
        entrada = new ObjectInputStream(socket.getInputStream());
    }

    @Override
    public void run() {
        try {
            bomberman.pedirUsuario();
            while (true) {
                ComObject cobj = (ComObject) entrada.readObject();
                if (cobj != null) {
                    System.out.println("Response code: " + cobj.getCode());
                    processAction(cobj);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    

    public void setListo(){
        ComObject cobj = new ComObject(101);
        enviarPeticion(cobj);
    }

    public void empezarJuego(){
        ComObject cobj = new ComObject(102);
        enviarPeticion(cobj);
    }

    private void llenarListaUsuarios(Vector data) {
        Vector users = (Vector) data.get(0);
        for (Object user : users) {
            usuarios.add((String)user);
            bomberman.playersModel.addRow(new Object[]{"?", user, "?"});
        }
    }

    private void llenarListaStatus(Vector data) {
        bomberman.status.setText("Conectado y listo");
        Vector users = (Vector) data.get(0);

        for (Object user : users) {
            String nombre = user.toString().split(";")[0];
            String status = user.toString().split(";")[1];

            for( int r = 0; r < bomberman.playersModel.getRowCount(); r++)
                if( bomberman.playersModel.getValueAt(r, 1).equals(nombre) )
                    bomberman.playersModel.setValueAt(status, r, 2);

        }
    }

    private void agregarUsuario(String user) {
        usuarios.add(user);
        bomberman.playersModel.addRow(new Object[]{"?", user, "?"});
    }

    private void removerUsuario(String user) {
        int index = usuarios.indexOf(user);
        usuarios.remove(index);
        bomberman.playersModel.removeRow(index);
    }

    public void cerrarConexion() throws IOException {
        entrada.close();
        salida.close();
        socket.close();
    }

    public synchronized void enviarPeticion(ComObject cobj) {
        try {
            salida.writeObject(cobj);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void processAction(ComObject cobj) {
        char movId;
        switch (cobj.getCode()) {
            case 201: // connected
                llenarListaUsuarios(cobj.getObjects());
                bomberman.empezar.setEnabled(true);
                bomberman.status.setText("Conectado");
                setListo();
                break;
            case 301: // broadcast add user
                agregarUsuario(cobj.getTag());
                break;
            case 302: // broadcast remove user
                removerUsuario(cobj.getTag());
                break;
            case 307: // broadcast status
                llenarListaStatus(cobj.getObjects());
                break;
            case 402: // userexist
                bomberman.existeUsuario();
                break;
            case 303: // broadcast mapa
                connected = true;
                bomberman.status.setText("Jugando");
                String mapa[] = (String[]) cobj.getObjects().get(0);
                id = (Character) cobj.getObjects().get(1);
                bomberman.newGame(mapa);
                bomberman.frame.requestFocus();
                bomberman.empezar.setEnabled(false);
                break;
            case 304: // broadcast move
                movId = (Character) cobj.getObjects().get(1);
                bomberman.mover((Integer)cobj.getObjects().get(0), movId);
                break;
            case 305: // broadcast bomba
                movId = (Character) cobj.getObjects().get(1);
                bomberman.ponerBomba(movId);
                break;
            case -10: // juego empezado
                JOptionPane.showMessageDialog(bomberman, "Un juego ya esta en linea, por favor espere a que termine");
                break;
            case 306: // broadcast death
                movId = (Character) cobj.getObjects().get(0);
                if(movId == id){
                    try {
                        bomberman.status.setText("Desconectado");
                        int c = bomberman.playersModel.getRowCount();
                        for( int p = 0; p < c; p++ )
                            removerUsuario(bomberman.playersModel.getValueAt(0, 1).toString());
                        usuarios.clear();
                        bomberman.conectar.setEnabled(true);
                        bomberman.desconectar.setEnabled(false);
                        bomberman.field_host.setEnabled(true);
                        bomberman.field_port.setEnabled(true);
                        bomberman.paintOff();
                        //bomberman.setEnabled(false);
                        cerrarConexion();
                    } catch (IOException ex) {}
                    bomberman.setEnabled(false);
                }else{
                    bomberman.eliminarJugador(movId);
                }
                break;
        }
    }
}
