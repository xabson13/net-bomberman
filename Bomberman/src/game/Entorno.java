package game;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

abstract class Entorno extends JPanel implements ActionListener, KeyListener {

    protected Cosa[][][] map = new Cosa[21][21][5]; //0 - WALL&BG&OBSTACLE 1 - BREAKABLE&ITEM 2 - ENEMY 3,4-PLAYER
    private JLabel status;
    private JFrame frame;
    private JMenuBar menuBar;
    private JMenu gameButton, helpButton;
    protected JMenuItem newGameButton, resetButton, saveButton, loadButton, quitButton, aboutButton;
    protected int picwidth = 32;
    protected int picheight = 32;
    protected int width = 15;
    protected int height = 11;
    protected Conexion conexion;

    public Entorno() {
        menuBar = new JMenuBar();
        gameButton = new JMenu("Game");
        helpButton = new JMenu("Help");
        newGameButton = new JMenuItem("New Game");
        resetButton = new JMenuItem("Reset Game");
        saveButton = new JMenuItem("Save Moves");
        loadButton = new JMenuItem("Load  Moves");
        quitButton = new JMenuItem("Quit Game");
        aboutButton = new JMenuItem("About");
        gameButton.add(newGameButton);
        gameButton.add(resetButton);
        gameButton.add(saveButton);
        gameButton.add(loadButton);
        gameButton.add(quitButton);
        helpButton.add(aboutButton);
        menuBar.add(gameButton);
        //menuBar.add(helpButton);
        newGameButton.addActionListener(this);
        resetButton.addActionListener(this);
        saveButton.addActionListener(this);
        loadButton.addActionListener(this);
        quitButton.addActionListener(this);
        aboutButton.addActionListener(this);

        /*resetButton.setEnabled(false);
        saveButton.setEnabled(false);*/
        loadButton.setEnabled(false);


        frame = new JFrame("Net Bomberman");
        status = new JLabel("ESCOM");
        frame.setJMenuBar(menuBar);
        //Set layout
        frame.setLayout(new FlowLayout());
        frame.add(this);
        frame.add(status);
        //frame.add(new JLabel("( Use the ARROW KEYS to control the ball! )", 0));
        frame.setSize(width * picwidth + 20, height * picheight + 90);
        frame.setDefaultCloseOperation(3);
        frame.setVisible(true);
        //Center the window
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

    @Override
    public void paint(Graphics g) {

        super.paint(g);
        Image offScreenImage = createImage(picwidth * width, picheight * height);
        Graphics offScreen = offScreenImage.getGraphics();

        for (int k = 0; k < 5; k++) {
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    if (map[j][i][k] != null) {
                        if (map[j][i][k] instanceof Jugador) {
                            //System.out.println(map[j][i][k].getPoint().x + " " + map[j][i][k].getPoint().y);
                            offScreen.drawImage(map[j][i][k].getImage(), (int) (j * picwidth + map[j][i][k].getSmallSizeX() / Cosa.MAXSMALLSIZE * picwidth), (int) ((i - 1) * picheight + 10 + map[j][i][k].getSmallSizeY() / Cosa.MAXSMALLSIZE * picheight), this);
                        } else {
                            offScreen.drawImage(map[j][i][k].getImage(), (int) (j * picwidth + map[j][i][k].getSmallSizeX() / Cosa.MAXSMALLSIZE * picwidth), (int) (i * picheight + map[j][i][k].getSmallSizeY() / Cosa.MAXSMALLSIZE * picheight), this);
                        }
                    }

                }
            }
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
