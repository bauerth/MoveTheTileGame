package gui;

import java.awt.Point;
import java.awt.image.BufferedImage;
import core.Constants;
import core.GameBoard;
import core.MoveDirection;

public class Tile {

	public int id;

	public MoveDirection moveDir;

	public Point posOnBoard;

	private int width, height;

	private GameBoard gb;

	public String name;

	public String type;

	public BufferedImage img;

	public Point size;

	public Point bounds;

	/**
	 * Creates a new tile with the given parameters.
	 * 
	 * @param w
	 *            - width of the tile.
	 * @param h
	 *            - height of the tile.
	 * @param pos
	 *            - position of the tile on board.
	 * @param subImage
	 *            - image of the tile.
	 * @param gameBoard
	 *            - game board on which the tile is moved.
	 * @param id
	 *            - id of the tile.
	 */
	public Tile(int w, int h, Point pos, BufferedImage subImage,
			GameBoard gameBoard, int id) {
		super();
		this.size = new Point(w, h);
		this.bounds = new Point(Constants.startX + pos.x * w, Constants.startY
				+ pos.y * h);
		img = subImage;

		this.width = w;
		this.height = h;
		gb = gameBoard;

		type = "image";

		posOnBoard = pos;

		this.id = id;
		name = "tile" + id;
	}

	/**
	 * Slides the tile to given destination point by setting the bounds
	 * coordinates to the new coordinates computed with the destination point.
	 * 
	 * @param destination
	 */
	public void slide(final Point destination) {
		final Point oldPos = new Point(posOnBoard);
		if (moveDir == MoveDirection.RIGHT) {
			// Tile.this.setBounds(destination.x * width + Constants.startX,
			// Tile.this.getBounds().y, width,
			// height);
			this.bounds = new Point(destination.x * width
					+ Constants.startX, this.bounds.y);
			this.posOnBoard = destination;
			// Tile.this.repaint();
			gb.callbackTileMoved(oldPos, this);
		} else if (moveDir == MoveDirection.LEFT) {
			// Tile.this.setBounds(destination.x * width + Constants.startX,
			// Tile.this.getBounds().y, width,
			// height);
			this.bounds = new Point(destination.x * width
					+ Constants.startX, this.bounds.y);
			this.posOnBoard = destination;
			// Tile.this.repaint();
			gb.callbackTileMoved(oldPos, this);
		} else if (moveDir == MoveDirection.UP) {
			// Tile.this.setBounds(Tile.this.getBounds().x, destination.y *
			// height + Constants.startY, width,
			// height);
			this.bounds = new Point(this.bounds.x, destination.y
					* height + Constants.startY);
			this.posOnBoard = destination;
			// Tile.this.repaint();
			gb.callbackTileMoved(oldPos, this);
		} else if (moveDir == MoveDirection.DOWN) {
			// Tile.this.setBounds(Tile.this.getBounds().x, destination.y *
			// height + Constants.startY, width,
			// height);
			this.bounds = new Point(this.bounds.x, destination.y
					* height + Constants.startY);
			this.posOnBoard = destination;
			// Tile.this.repaint();
			gb.callbackTileMoved(oldPos, this);
		}
	}

	/**
	 * Returns the move direction of this tile.
	 * @return
	 */
	public String getMoveDirection() {
		if (moveDir == MoveDirection.DOWN) {
			return "DOWN";
		} else if (moveDir == MoveDirection.RIGHT) {
			return "RIGHT";
		} else if (moveDir == MoveDirection.LEFT) {
			return "LEFT";
		} else if (moveDir == MoveDirection.UP) {
			return "UP";
		} else {
			return "NONE";
		}
	}
}
