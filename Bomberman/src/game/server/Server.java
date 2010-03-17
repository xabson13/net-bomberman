package game.server;

/**
 *
 * @author Sergio
 */
import game.Jugador;
import java.awt.Point;
import java.net.*;
import java.io.*;
import java.util.*;

/**
 *
 * @author  Sergio Ceron Figueroa
 */
public class Server {

    public static final int PORT = 8000;
    private List<Conexion> clientes;
    private boolean started;

    public Server() {
        this.started = false;
        clientes = new ArrayList<Conexion>();
    }

    private void iniciar() {
        try {
            System.out.println("INFO: Iniciando Servidor ");
            ServerSocket socketServer = new ServerSocket(PORT);
            System.out.println("INFO: Servidor iniciado por el puerto " + PORT);
            while (true) {
                Socket socketClient = socketServer.accept();
                System.out.println("Nueva conexion " + socketClient.getInetAddress());
                Conexion conn = new Conexion(socketClient);
                clientes.add(conn);
                conn.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private synchronized void broadcastRespuesta(ComObject cobj) {
        for (int i = 0; i < clientes.size(); i++) {
            Conexion con = clientes.get(i);
            con.enviarRespuesta(cobj);
        }
    }

    private Vector<String> getUsuarios(Conexion c) {
        Vector<String> users = new Vector<String>();

        for (Conexion con : clientes) {
            if( con.getNombre() != null )
                if (!con.getNombre().equals(c.getNombre())) {
                    users.add(con.getNombre());
                }
        }
        return users;
    }

    private boolean isAllReady() {
        for (Conexion con : clientes) {
            if (!con.ready) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.iniciar();
    }

    class Conexion extends Thread {

        private Socket socket;
        private ObjectOutputStream salida;
        private ObjectInputStream entrada;
        private String nombre;
        private MapFactory mf;
        private boolean ready;
        private boolean connected;
        private Jugador player;

        Conexion(Socket socket) {
            this.socket = socket;
            this.ready = false;
            this.connected = false;
            mf = new MapFactory();
        }

        @Override
        public void run() {
            try {
                entrada = new ObjectInputStream(socket.getInputStream());
                salida = new ObjectOutputStream(socket.getOutputStream());

                while (true) {
                    ComObject cobj = (ComObject) entrada.readObject();
                    if (cobj != null) {
                        System.out.println("Request code: " + cobj.getCode());
                        processAction(cobj);
                    }
                }
            } catch (Exception e) {
                System.out.println("INFO: El usuario " + nombre + " se ha desconectado");
                ComObject cobj;
                if (nombre != null) {
                    cobj = new ComObject(302);
                    cobj.setTag(nombre);
                    broadcastRespuesta(cobj);
                }
                clientes.remove(this);
                e.printStackTrace();
            }
        }

        private void enviarRespuesta(ComObject cobj) {
            try {
                salida.writeObject(cobj);
            } catch (Exception err) {
                err.printStackTrace();
            }
        }

        private void processAction(ComObject cobj) {
            switch (cobj.getCode()) {
                case 100: // connect
                    connect(cobj.getTag());
                    break;
                case 101: // ready
                    ready();
                    break;
                case 102: // start
                    begin();
                    break;
                case 103: // move
                    move(cobj.getObjects());
                    break;
            }
        }

        private void connect(String user) {
            nombre = user;
            ComObject cobj;
            if (!existeUsuario(nombre)) {
                cobj = new ComObject(201); // connected
                cobj.addObject(getUsuarios(this));
                enviarRespuesta(cobj);
                cobj = new ComObject(301); // broadcast adduser
                cobj.setTag(nombre);
                broadcastRespuesta(cobj);
                connected = true;
            } else {
                cobj = new ComObject(402); // user exist
                enviarRespuesta(cobj);
                nombre = null;
                clientes.remove(this);
            }
        }

        private void ready() {
            if (connected) {
                ready = true;
                enviarRespuesta(new ComObject(200)); // ok
            } else {
                enviarRespuesta(new ComObject(403)); // not connected
            }
        }

        private void begin() {
            if (!connected) {
                enviarRespuesta(new ComObject(403)); // not connected
                return;
            }
            if (isAllReady()) {
                mf.generate(clientes);
                for( Conexion c : clientes ){
                    ComObject cobj = new ComObject(202); // mapa
                    cobj.addObject(mf.getMapa());
                    cobj.addObject(c.player.getId());
                    c.enviarRespuesta(cobj);
                }
            } else {
                enviarRespuesta(new ComObject(405)); // not all ready
            }
        }

        private void move(Vector data) {
            Point mov = (Point) data.get(0);
           /* if(mf.checkMove(mov, player))
                broadcastMensaje(nombre + ": " + text);*/
        }

        private boolean existeUsuario(String name) {
            for (Conexion c : clientes) {
                if( c.getNombre() != null )
                    if (c.getNombre().equalsIgnoreCase(name) && this != c) {
                        return true;
                    }
            }
            return false;
        }

        public String getNombre() {
            return nombre;
        }

        public void setJugador(Jugador player) {
            this.player = player;
        }
    }
}
