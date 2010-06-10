/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package game;

import game.server.Server;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;

/**
 *
 * @author sxceron
 */
public class Main extends JFrame implements ActionListener{

    private JButton servidor, cliente;

    public Main(){
        super("Net Bomberman v1.0");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 200);
        setLayout(new GridLayout(2,1));

        servidor = new JButton("Lanzar Servidor (puerto 8011)");
        servidor.addActionListener(this);
        cliente  = new JButton("Lanzar Cliente");
        cliente.addActionListener(this);

        add(servidor);
        add(cliente);

        setVisible(true);
    }

    public static void main(String args[]){
        new Main();
    }

    public void actionPerformed(ActionEvent ae) {
        String c = ae.getActionCommand();
        if( c.equals("Lanzar Servidor (puerto 8011)") ){
            new Thread(){
                @Override
                public void run(){
                    servidor.setEnabled(false);
                    Server server = new Server();
                    server.iniciar();
                }
            }.start();
        }
        if( c.equals("Lanzar Cliente") ){
            new Thread(){
                @Override
                public void run(){
                    new Bomberman();
                }
            }.start();
        }
    }

}
