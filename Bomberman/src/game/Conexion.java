/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

import game.server.ComObject;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.JOptionPane;

/**
 *
 * @author Sergio
 */
public class Conexion extends Thread {

    private Vector usuarios;
    private final static int PORT = 8000;
    private final static String HOST = "localhost";
    private Bomberman bomberman;
    private ObjectInputStream entrada;
    private ObjectOutputStream salida;
    private Socket socket;
    private boolean connected;

    Conexion(Bomberman bomberman) {
        this.usuarios = new Vector();
        this.connected = false;
        this.bomberman = bomberman;
        try {
            socket = new Socket(HOST, PORT);
            salida = new ObjectOutputStream(socket.getOutputStream());
            entrada = new ObjectInputStream(socket.getInputStream());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.out.println("start(0)");
    }

    @Override
    public void run() {
        System.out.println("start()");
        try {
            bomberman.pedirUsuario();
            System.out.println("start(2)");
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
        }
    }

    private void agregarUsuario(String user) {
        usuarios.add(user);
    }

    private void removerUsuario(String user) {
        usuarios.add(user);
    }

    private void cerrarConexion() {
        try {
            entrada.close();
            salida.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void enviarPeticion(ComObject cobj) {
        try {
            salida.writeObject(cobj);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void processAction(ComObject cobj) {
        switch (cobj.getCode()) {
            case 201: // connected
                llenarListaUsuarios(cobj.getObjects());
                break;
            case 301: // broadcast add user
                agregarUsuario(cobj.getTag());
                break;
            case 302: // broadcast remove user
                removerUsuario(cobj.getTag());
                break;
            case 402: // userexist
                bomberman.pedirUsuario();
                break;
            case 202: // connected
                connected = true;
                String mapa[] = (String[]) cobj.getObjects().get(0);
                bomberman.newGame(mapa);
                break;
        }
    }
}
