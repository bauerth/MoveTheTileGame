package gui;

import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.JPanel;

import core.Constants;

public class GameGui extends JPanel implements Observer {

	/**
	 * generated ID
	 */
	private static final long serialVersionUID = -2126644414620511061L;

	/**
	 * containing all tiles on board
	 */
	private ArrayList<Tile> tilesOnBoard = new ArrayList<Tile>();

	/**
	 * Outer frame of the game window
	 */
	private JFrame gameFrame;

	/**
	 * game board frame, where the tiles are in
	 */
	private Rectangle gameBoardFrame;

	/**
	 * flag indicating if we have to init the game, so draw all tiles
	 */
	private boolean init = false;

	/**
	 * the tile to be removed or added
	 */
	private Tile tileToMove;

	/**
	 * flag indicating if the tile has to be deleted or not
	 */
	private boolean removed = false;

	/**
	 * Inits a new game gui.
	 * 
	 * @param w
	 *            - width of the outer frame
	 * @param h
	 *            - height of the outer frame
	 * @param gameW
	 *            - width of the game frame
	 * @param gameH
	 *            - height of the game frame
	 */
	public GameGui(int w, int h, int gameW, int gameH) {

		gameFrame = new JFrame();
		gameFrame.setSize(w, h);

		gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gameFrame.setUndecorated(true);

		if (Constants.switchScreen) {
			gameFrame.setLocation(Constants.GameTopLeft);
		}else if (Constants.isFullScreen) {
			GraphicsDevice gd = GraphicsEnvironment
					.getLocalGraphicsEnvironment().getDefaultScreenDevice();
			gameFrame.setExtendedState(Frame.MAXIMIZED_BOTH);
			gd.setFullScreenWindow(gameFrame);
		}

		// width = w;
		// height = h;

		gameBoardFrame = new Rectangle((w - gameW) / 2, (h - gameH) / 2, gameW,
				gameH);

		gameFrame.getContentPane().add(this);
		gameFrame.setVisible(true);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void update(Observable o, Object arg) {
		if (arg instanceof Tile) {
			if (!removed) {
				tileToMove = (Tile) arg;
				removed = true;
			} else {
				tileToMove = (Tile) arg;
				removed = false;
			}
			repaint();
		} else if (arg == null) {
			reset();
		} else if (arg instanceof ArrayList<?>) {
			tilesOnBoard = (ArrayList<Tile>) arg;
			init = true;
			repaint();
		}
	}

	/**
	 * Reset the game gui, by removing all tiles from the board.
	 */
	private void reset() {
		tilesOnBoard.clear();
		init = true;
		repaint();
	}

	/**
	 * Paint method, that draws the tiles on the board.
	 */
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		if (init) {
			g2d.clearRect(gameBoardFrame.x, gameBoardFrame.y,
					gameBoardFrame.width, gameBoardFrame.height);

			for (Tile t : tilesOnBoard) {
				g2d.drawImage(t.img, t.bounds.x, t.bounds.y, null);
			}
			// init = false;
		} else {
			if (tileToMove != null) {
				if (removed) {
					g2d.clearRect(tileToMove.bounds.x, tileToMove.bounds.y,
							tileToMove.size.x, tileToMove.size.y);
				} else {
					g2d.drawImage(tileToMove.img, tileToMove.bounds.x,
							tileToMove.bounds.y, null);
				}
				tileToMove = null;
			}
		}
	}

	/********* Methods for frame effect ********/
	// NOT USED ANYMORE

	// public void paint(Graphics g) {
	// Graphics2D g2d = (Graphics2D) g;
	// g2d.clearRect(gameBoardFrame.x, gameBoardFrame.y,
	// gameBoardFrame.width, gameBoardFrame.height);
	// if (init) {
	// draw board
	// BufferedImage img = createClipImage(gameBoardFrame, g2d);
	// Graphics2D g2 = img.createGraphics();
	// // Fill the shape with a gradient
	// g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
	// RenderingHints.VALUE_ANTIALIAS_ON);
	// g2.setComposite(AlphaComposite.SrcAtop);
	// g2.setPaint(new GradientPaint(0, 0, clrHi, 0, height, clrLo));
	// g2.fill(gameBoardFrame);
	// // Apply the border glow effect
	// // paintBorderGlow(g2, 8);
	// paintBorderShadow(g2, 8);
	// g2.dispose();
	//
	// g2d.drawImage(img, 0, 0, null);
	//
	// init = false;
	// }
	// }

	// private static final Color clrHi = new Color(172, 171, 161);
	// private static final Color clrLo = new Color(80, 79, 73);

	// private BufferedImage createClipImage(Shape s, Graphics2D g) {
	// // Create a translucent intermediate image in which we can perform
	// // the soft clipping
	// GraphicsConfiguration gc = g.getDeviceConfiguration();
	// BufferedImage img = gc.createCompatibleImage(width, height,
	// Transparency.TRANSLUCENT);
	// Graphics2D g2 = img.createGraphics();
	//
	// // Clear the image so all pixels have zero alpha
	// g2.setComposite(AlphaComposite.Clear);
	// g2.fillRect(0, 0, width, height);
	//
	// // Render our clip shape into the image. Note that we enable
	// // antialiasing to achieve the soft clipping effect. Try
	// // commenting out the line that enables antialiasing, and
	// // you will see that you end up with the usual hard clipping.
	// g2.setComposite(AlphaComposite.Src);
	// g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
	// RenderingHints.VALUE_ANTIALIAS_ON);
	// g2.setColor(Color.WHITE);
	// g2.setStroke(new BasicStroke(15));
	// g2.draw(s);
	// g2.dispose();
	//
	// return img;
	// }
	//
	// private static Color getMixedColor(Color c1, float pct1, Color c2,
	// float pct2) {
	// float[] clr1 = c1.getComponents(null);
	// float[] clr2 = c2.getComponents(null);
	// for (int i = 0; i < clr1.length; i++) {
	// clr1[i] = (clr1[i] * pct1) + (clr2[i] * pct2);
	// }
	// return new Color(clr1[0], clr1[1], clr1[2], clr1[3]);
	// }
	//
	// private void paintBorderShadow(Graphics2D g2, int shadowWidth) {
	// g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
	// RenderingHints.VALUE_ANTIALIAS_ON);
	// int sw = shadowWidth * 2;
	// for (int i = sw; i >= 2; i -= 2) {
	// float pct = (float) (sw - i) / (sw - 1);
	// g2.setColor(getMixedColor(Color.LIGHT_GRAY, pct, Color.WHITE,
	// 1.0f - pct));
	// g2.setStroke(new BasicStroke(i));
	// g2.draw(gameBoardFrame);
	// }
	// }
	//
	// private static final Color clrGlowInnerHi = new Color(172, 171, 161,
	// 148);
	// private static final Color clrGlowInnerLo = clrLo;
	// private static final Color clrGlowOuterHi = new Color(80, 79, 73, 124);
	// private static final Color clrGlowOuterLo = clrLo;
	//
	// private void paintBorderGlow(Graphics2D g2, int glowWidth) {
	// int gw = glowWidth * 2;
	// for (int i = gw; i >= 2; i -= 2) {
	// float pct = (float) (gw - i) / (gw - 1);
	//
	// Color mixHi = getMixedColor(clrGlowInnerHi, pct, clrGlowOuterHi,
	// 1.0f - pct);
	// Color mixLo = getMixedColor(clrGlowInnerLo, pct, clrGlowOuterLo,
	// 1.0f - pct);
	// g2.setPaint(new GradientPaint(0.0f, height * 0.25f, mixHi, 0.0f,
	// height, mixLo));
	//
	// g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP,
	// pct));
	// g2.setStroke(new BasicStroke(i));
	// g2.draw(gameBoardFrame);
	// }
	// }

	/********************************************/
}
