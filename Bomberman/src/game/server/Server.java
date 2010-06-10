package game.server;

/**
 *
 * @author Sergio
 */
import game.Actualizable;
import game.Jugador;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.net.*;
import java.io.*;
import java.util.*;

/**
 *
 * @author  Sergio Ceron Figueroa
 */
public class Server {

    public static final int PORT = 8011;
    private List<Conexion> clientes;
    private boolean started;

    public Server() {
        this.started = false;
        clientes = new ArrayList<Conexion>();
    }

    public void iniciar() {
        try {
            System.out.println("INFO: Iniciando Servidor ");
            ServerSocket socketServer = new ServerSocket(PORT);
            System.out.println("INFO: Servidor iniciado por el puerto " + PORT);
            while (true) {
                Socket socketClient = socketServer.accept();
                //System.out.println("Nueva conexion " + socketClient.getInetAddress());
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
            if (con.getNombre() != null) {
                if (!con.getNombre().equals(c.getNombre())) {
                    users.add(con.getNombre());
                }
            }
        }
        return users;
    }

    private Vector<String> getStatus() {
        Vector<String> users = new Vector<String>();

        for (Conexion con : clientes) {
            if (con.getNombre() != null) {
                users.add(con.getNombre() + ";" + con.ready + ";" );
            }
        }
        return users;
    }

    private Conexion getConexionById(Character id){
        for( Conexion c : clientes ){
            if( c.player.getId() == id )
                return c;
        }
        return null;
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

    class Conexion extends Thread implements Actualizable{

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
            mf = new MapFactory(this);
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

        public void cerrarConexion() {
            try {
                clientes.remove(this);
                entrada.close();
                salida.close();
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void enviarRespuesta(ComObject cobj) {
            try {
                System.out.println("Response code: " + cobj.getCode());
                salida.writeObject(cobj);
                salida.flush();
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
                case 104: // bomba
                    ponerBomba(cobj.getObjects());
                    break;
            }
        }

        private void connect(String user) {
            if( getUsuarios(this).size() < 2 )
                started = false;
            nombre = user;
            ComObject cobj;
            if (!existeUsuario(nombre) && !started) {
                cobj = new ComObject(201); // connected
                cobj.addObject(getUsuarios(this));
                enviarRespuesta(cobj);
                cobj = new ComObject(301); // broadcast adduser
                cobj.setTag(nombre);
                broadcastRespuesta(cobj);
                connected = true;
            } else if(existeUsuario(nombre)){
                cobj = new ComObject(402); // user exist
                enviarRespuesta(cobj);
                nombre = null;
                clientes.remove(this);
            }else{
                cobj = new ComObject(-10); // juego empezado
                enviarRespuesta(cobj);
                nombre = null;
                clientes.remove(this);
            }
        }

        private void ready() {
            if (connected) {
                ready = true;
                enviarRespuesta(new ComObject(200)); // ok
                ComObject cobj;
                cobj = new ComObject(307); // status && ids
                cobj.addObject(getStatus());
                broadcastRespuesta(cobj);
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
                mf.generar(clientes);
                for (Conexion c : clientes) {
                    ComObject cobj = new ComObject(303); // broascast mapa
                    cobj.addObject(mf.getMapaAsArray());
                    cobj.addObject(c.player.getId());
                    c.enviarRespuesta(cobj);
                }
                started = true;
            } else {
                enviarRespuesta(new ComObject(405)); // not all ready
            }
        }

        private void move(Vector data) {
            Integer kcode = (Integer) data.get(0);
            if (started && connected) {
                broadcastMove(kcode);

                Point movept = new Point(0, 0);
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
                (new LoadSteps(movept, player)).run();
            }
        }

        private void broadcastMove(int kcode) {
            ComObject cobj = new ComObject(304); // broadcast move
            cobj.addObject(kcode);
            cobj.addObject(player.getId());
            System.out.println("pid+" + player.getId());
            broadcastRespuesta(cobj);
        }

        private void ponerBomba(Vector data) {
            Integer kcode = (Integer) data.get(0);
            if (started && connected) {
                broadcastBomba(kcode);
                player.ponerBomba();
            }
        }

        private void broadcastBomba(int kcode) {
            ComObject cobj = new ComObject(305); // broadcast bomba
            cobj.addObject(kcode);
            cobj.addObject(player.getId());
            broadcastRespuesta(cobj);
        }

        private boolean existeUsuario(String name) {
            for (Conexion c : clientes) {
                if (c.getNombre() != null) {
                    if (c.getNombre().equalsIgnoreCase(name) && this != c) {
                        return true;
                    }
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

        public void refresh() {
        }

        public void notificar(Object[] params) {
            Integer code = (Integer) params[0];
            Character id = (Character) params[1];

            if( code == 1 ){
                Conexion c = getConexionById(id);
                ComObject cobj = new ComObject(306); // broadcast death
                cobj.addObject(id);
                broadcastRespuesta(cobj);
                //c.cerrarConexion();
            }
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
            j.move(pt);
        }
    }
}
