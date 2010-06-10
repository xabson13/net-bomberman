package game;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author  Sergio Ceron Figueroa
 */
abstract class Entorno extends JPanel implements ActionListener, KeyListener {

    protected Cosa[][][] map = new Cosa[21][21][5]; //0 - WALL&BG&OBSTACLE 1 - BREAKABLE&ITEM 2 - ENEMY 3,4-PLAYER
    protected JLabel status;
    public JFrame frame;
    private JMenuBar menuBar;
    private JMenu gameButton, helpButton;
    protected JMenuItem newGameButton, resetButton, saveButton, loadButton, quitButton;
    protected TextField field_host, field_port;
    protected JButton conectar, desconectar, empezar;
    protected JTable playersTable;
    protected JPanel topPanel;
    public DefaultTableModel playersModel;
    private JScrollPane playersScroll;
    protected int picwidth = 32;
    protected int picheight = 32;
    protected int width = 15;
    protected int height = 11;
    protected Conexion conexion;
    protected   boolean off = false;
    public Entorno() {
        menuBar = new JMenuBar();
        gameButton = new JMenu("Game");
        helpButton = new JMenu("Help");
        newGameButton = new JMenuItem("New Game");
        resetButton = new JMenuItem("Reset Game");
        saveButton = new JMenuItem("Save Moves");
        loadButton = new JMenuItem("Load  Moves");
        quitButton = new JMenuItem("Quit Game");

        gameButton.add(quitButton);
        menuBar.add(gameButton);

        topPanel   = new JPanel(new FlowLayout(FlowLayout.LEFT));
        field_host = new TextField(20); field_host.setText("localhost");
        field_port = new TextField(5);  field_port.setText("8011");
        conectar   = new JButton("Conectar");
        desconectar= new JButton("Desconectar"); desconectar.setEnabled(false);
        empezar    = new JButton("Empezar");     empezar.setEnabled(false);

        topPanel.add(new JLabel("Servidor:"));
        topPanel.add(field_host);
        topPanel.add(new JLabel("Puerto:"));
        topPanel.add(field_port);
        topPanel.add(conectar);
        topPanel.add(desconectar);
        topPanel.add(empezar);

        newGameButton.addActionListener(this);
        resetButton.addActionListener(this);
        saveButton.addActionListener(this);
        loadButton.addActionListener(this);
        quitButton.addActionListener(this);
        conectar.addActionListener(this);
        desconectar.addActionListener(this);
        empezar.addActionListener(this);

        loadButton.setEnabled(false);

        playersModel = new DefaultTableModel(new Object[][]{},
                new Object[]{"ID", "Nombre", "Status"});
        playersTable = new JTable(playersModel);
        playersScroll = new JScrollPane(playersTable);
        playersScroll.setPreferredSize(new Dimension(250,300));

        frame = new JFrame("Net Bomberman");
        status = new JLabel("Estado: Desconectado");
        frame.setJMenuBar(menuBar);
        frame.setLayout(new BorderLayout());

        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(this, BorderLayout.CENTER);
        frame.add(playersScroll, BorderLayout.EAST);
        frame.add(status, BorderLayout.SOUTH);

        frame.setSize(width * picwidth + 250, height * picheight + 100);
        frame.setDefaultCloseOperation(3);
        frame.setVisible(true);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = frame.getSize();
        if (frameSize.height > screenSize.height) {
            frameSize.height = screenSize.height;
        }
        if (frameSize.width > screenSize.width) {
            frameSize.width = screenSize.width;
        }
        frame.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
        setBackground(Color.white);
        frame.addKeyListener(this);
        frame.requestFocus();
    }

    public void paintOff(){
        off = true;
        repaint();
    }

    @Override
    public void paint(Graphics g) {

        super.paint(g);

        Image offScreenImage = createImage(picwidth * width, picheight * height);
        Graphics offScreen = offScreenImage.getGraphics();

        if( !off ){
            for (int k = 0; k < 5; k++) {
                for (int i = 0; i < height; i++) {
                    for (int j = 0; j < width; j++) {
                        if (map[j][i][k] != null) {
                            if (map[j][i][k] instanceof Jugador) {
                                offScreen.drawImage(map[j][i][k].getImage(), (int) (j * picwidth + map[j][i][k].getSmallSizeX() / Cosa.MAXSMALLSIZE * picwidth), (int) ((i - 1) * picheight + 10 + map[j][i][k].getSmallSizeY() / Cosa.MAXSMALLSIZE * picheight), this);
                            } else {
                                offScreen.drawImage(map[j][i][k].getImage(), (int) (j * picwidth + map[j][i][k].getSmallSizeX() / Cosa.MAXSMALLSIZE * picwidth), (int) (i * picheight + map[j][i][k].getSmallSizeY() / Cosa.MAXSMALLSIZE * picheight), this);
                            }
                        }

                    }
                }
            }
        }else{
            offScreen.setColor(Color.black);
            offScreen.fillRect(0, 0, picwidth * width, picheight * height);
            offScreen.setColor(Color.white);
            offScreen.drawString("DEATH", (picwidth * width)/2-10, (picheight * height)/2);
            //off = false;
            //g.drawImage(offScreenImage, 0, 0, this);
        }
        g.drawImage(offScreenImage, 0, 0, this);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(picwidth * width, picheight * height);
    }

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(picwidth * width, picheight * height);
    }

    @Override
    public Dimension getMaximumSize() {
        return new Dimension(picwidth * width, picheight * height);
    }
}
