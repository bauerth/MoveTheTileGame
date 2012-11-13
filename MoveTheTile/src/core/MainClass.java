package core;

import gui.GameGui;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Observer;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import communicator.ServerCommunicator;

public class MainClass implements ActionListener {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new MainClass();
	}

	/**
	 * checkbox for choosing fullscreen or specialized screen resolution
	 */
	private JCheckBox cbFullScreen;

	/**
	 * checkbox to choose, that the game is displayed on a second screen, if any
	 * is available
	 */
	private JCheckBox cbSwitchScreen;

	/**
	 * Panel to show a miniature picture of the chosen image to play the puzzle
	 */
	private ImagePanel chosenImg;

	/**
	 * Text fields to set the screen dimension and the number of the grids in
	 * the puzzle
	 */
	private JTextField xdim, ydim, xGrid, yGrid;

	/**
	 * Panel that holds the text fields to enter the screen size
	 */
	private JPanel sizePanel;

	private GameBoard gameBoard;

	public MainClass() {
		JFrame settingsFrame = new JFrame("Game Settings");
		settingsFrame.setSize(400, 800);
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.setSize(480, 800);

		// check box
		cbFullScreen = new JCheckBox("Fullscreen");
		cbFullScreen.setSelected(Constants.isFullScreen);
		cbFullScreen.setActionCommand("cbFullScreen");
		cbFullScreen.addActionListener(this);
		cbFullScreen.setToolTipText("Unselect to declare custom screen size");

		cbSwitchScreen = new JCheckBox("Display game on second screen");
		cbSwitchScreen.setSelected(Constants.switchScreen);
		cbSwitchScreen.setActionCommand("cbSwitchScreen");
		cbSwitchScreen.addActionListener(this);
		cbSwitchScreen
				.setToolTipText("Select to display the game on a second screen if any is available");

		// screen size panel
		sizePanel = new JPanel();
		sizePanel.setLayout(new GridLayout(8, 1));
		JLabel label = new JLabel("Screen Size Settings");
		xdim = new JTextField();
		xdim.setSize(new Dimension(40, 40));
		xdim.setEditable(true);
		xdim.setToolTipText("screen width");

		ydim = new JTextField();
		ydim.setSize(40, 40);
		ydim.setEditable(true);
		ydim.setToolTipText("screen height");

		// puzzle grid size panel
		JLabel label2 = new JLabel("Puzzle Settings");
		xGrid = new JTextField();
		xGrid.setSize(160, 40);
		xGrid.setEditable(true);
		xGrid.setText(Constants.TilesNumX + "");

		yGrid = new JTextField();
		yGrid.setSize(160, 40);
		yGrid.setEditable(true);
		yGrid.setText(Constants.TilesNumY + "");

		sizePanel.add(label);
		sizePanel.add(cbFullScreen);
		sizePanel.add(cbSwitchScreen);
		sizePanel.add(xdim);
		sizePanel.add(ydim);
		sizePanel.add(label2);
		// sizePanel.add(xGrid);
		// sizePanel.add(yGrid);

		if (Constants.isFullScreen) {
			xdim.setEnabled(false);
			ydim.setEnabled(false);
		}

		// Buttons
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(3, 1));

		// file choose for image
		JButton chooseBtn = new JButton("Choose Image");
		chooseBtn.addActionListener(this);
		chooseBtn.setActionCommand("choose_image");

		// button to start the game
		JButton startBtn = new JButton("Start Game");
		startBtn.setActionCommand("start_game");
		startBtn.addActionListener(this);

		// restart the game
		JButton restartBtn = new JButton("Restart Game");
		restartBtn.setActionCommand("restart_game");
		restartBtn.addActionListener(this);

		// panel showing the chosen image
		try {
			chosenImg = new ImagePanel(ImageIO.read(getClass().getResource(
					"no.jpg")));
		} catch (IOException e) {
			e.printStackTrace();
		}

		// chosenImg.setSize(220, 220);

		// add the components
		sizePanel.add(chooseBtn);
		buttonPanel.add(startBtn);
		buttonPanel.add(restartBtn);

		panel.add(sizePanel, BorderLayout.NORTH);
		panel.add(chosenImg, BorderLayout.CENTER);
		panel.add(buttonPanel, BorderLayout.SOUTH);

		settingsFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		settingsFrame.getContentPane().add(panel);
		// settingsFrame.pack();
		settingsFrame.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equalsIgnoreCase("start_game")) {
			startGame();
		} else if (e.getActionCommand().equalsIgnoreCase("choose_image")) {
			chooseImage();
		} else if (e.getActionCommand().equalsIgnoreCase("cbFullScreen")) {
			Constants.isFullScreen = !Constants.isFullScreen;
			xdim.setEnabled(!Constants.isFullScreen);
			ydim.setEnabled(!Constants.isFullScreen);
		} else if (e.getActionCommand().equalsIgnoreCase("cbSwitchScreen")) {
			Constants.switchScreen = !Constants.switchScreen;
		} else if (e.getActionCommand().equalsIgnoreCase("restart_game")) {
			if (gameBoard != null) {
				gameBoard.solve();
				gameBoard.newGame();
			}
		}
	}

	/**
	 * Chooses the image with the help of a FileChooser.
	 */
	private void chooseImage() {
		JFileChooser fc = new JFileChooser();
		fc.setMultiSelectionEnabled(false);
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int state = fc.showOpenDialog(null);
		String path = null;
		if (state == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			String name = file.getName();
			if (name.contains(".jpg") || name.contains(".png")
					|| name.contains(".gif") || name.contains(".JPG")
					|| name.contains(".PNG") || name.contains(".GIF")) {
				path = file.getPath();
				Constants.pathToSourceImage = path;

				chosenImg.setImage(path);
			} else {

			}
			System.out.println("Image Source chosen:");
			System.out.println(file.getName());
			System.out.println(file.getPath());

		} else {
			System.out.println("Auswahl abgebrochen");
		}

	}

	/**
	 * Starts the game by creating the game board, the gui and the server
	 * communicator
	 */
	private void startGame() {
		int w, h, gameW, gameH;

		Constants.TilesNumX = Integer.parseInt(xGrid.getText());
		Constants.TilesNumY = Integer.parseInt(yGrid.getText());

		if (Constants.isFullScreen) {
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			if (Constants.switchScreen) {
				GraphicsEnvironment ge = GraphicsEnvironment
						.getLocalGraphicsEnvironment();
				GraphicsDevice[] gs = ge.getScreenDevices();
				Rectangle r = gs[gs.length - 1].getDefaultConfiguration()
						.getBounds();
				screenSize = new Dimension(r.width, r.height);
				Constants.GameTopLeft = new Point(r.x, r.y);
			}

			Constants.SCREEN_W = screenSize.width;
			Constants.SCREEN_H = screenSize.height;
			w = screenSize.width;
			h = screenSize.height;
			gameW = screenSize.width; // - 100;
			gameH = screenSize.height; // - 100;

		} else {
			w = Integer.parseInt(xdim.getText());
			h = Integer.parseInt(ydim.getText());
			Constants.SCREEN_W = w;
			Constants.SCREEN_H = h;
			gameW = w;
			gameH = h;
		}

		Observer gamObserver = new GameGui(w, h, gameW, gameH);

		Constants.startX = (w - gameW) / 2;
		Constants.startY = (h - gameH) / 2;

		gameBoard = new GameBoard(gameW, gameH, Constants.TilesNumX,
				Constants.TilesNumY);
		gameBoard.addObserver(gamObserver);

		gameBoard.initBoard();

		ServerCommunicator sc = new ServerCommunicator(gameBoard);

		gameBoard.setCommunicator(sc);
	}

	/**
	 * Class that shows a miniature of the chosen image.
	 * 
	 * @author Christian
	 * 
	 */
	public class ImagePanel extends JPanel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 5984232095948021084L;
		private BufferedImage image;

		public ImagePanel(BufferedImage img) {
			if (img != null) {
				image = img;// handle exception...
			}
		}

		public void setImage(String path) {
			try {
				BufferedImage tmp = ImageIO.read(new File(path));
				image = new BufferedImage(400, 400, BufferedImage.TYPE_INT_ARGB);
				Graphics2D g = image.createGraphics();
				g.drawImage(tmp, 0, 0, 400, 400, null);
				g.dispose();
				Constants.Chosen_Img = tmp;
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			repaint();
		}

		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.clearRect(0, 0, 400, 400);
			g.drawImage(image, 0, 0, null);
		}
	}
}
