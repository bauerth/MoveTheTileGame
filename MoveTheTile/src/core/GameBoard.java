package core;

import gui.Tile;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Random;

import communicator.ServerCommunicator;

public class GameBoard extends Observable /* implements ActionListener */{

	/**
	 * containing the tiles on the board
	 */
	private ArrayList<Tile> tilesOnBoard;

	/**
	 * properties of the game board
	 */
	public int width, height, tilesNumX, tilesNumY;

	/**
	 * free place on the board
	 */
	public Point freePos;

	/**
	 * list containing all tiles that can be moved
	 */
	private ArrayList<Tile> movableTiles = new ArrayList<Tile>();

	/**
	 * last tile that was moved
	 */
	private Tile lastTile = null;

	/**
	 * Reference to the server communicator to send messages to the clients
	 */
	private ServerCommunicator serverCommmunicator;
	
	private boolean solved = true;
	
	private boolean init  = false;

	/**
	 * Creates a new game board of given width and number of tiles.
	 * 
	 * @param width
	 * @param height
	 * @param tilesNumX
	 * @param tilesNumY
	 */
	public GameBoard(int width, int height, int tilesNumX, int tilesNumY) {
		this.tilesOnBoard = new ArrayList<Tile>(tilesNumX * tilesNumY);
		this.width = width;
		this.height = height;
		this.tilesNumX = tilesNumX;
		this.tilesNumY = tilesNumY;
	}

	/**
	 * Creates a new game, by reseting all fields.
	 */
	public void newGame() {
		lastTile = null;
		freePos = null;
		Constants.initialFreePos = null;
		tilesOnBoard = new ArrayList<Tile>(tilesNumX * tilesNumY);
		movableTiles = new ArrayList<Tile>();
		Constants.nameToId = new HashMap<String, Tile>();
		Constants.solvingSteps = new ArrayList<Tile>();

		setChanged();
		notifyObservers(null);

		initBoard();
	}

	/**
	 * Initializes the board, i.e. does random moves and records them to solve
	 * the game afterwards automatically.
	 */
	public void initBoard() {
		init = true;
		ArrayList<Point> pointsOnBoard = new ArrayList<Point>();
		for (int y = 0; y < tilesNumY; y++) {
			for (int x = 0; x < tilesNumX; x++) {
				pointsOnBoard.add(new Point(x, y));
			}
		}

		// Random r = new Random();
		//
		// int tileW = width / tilesNumX;
		// int tileH = height / tilesNumY;
		//
		// int size = pointsOnBoard.size();
		//
		// // Add the tile as solution
		// if (Constants.ChosenOpt == 0) {
		// for (int i = 0; i < size - 1; i++) {
		// Tile t = new Tile(tileW, tileH, pointsOnBoard.remove(0), "tile"
		// + i, null, i, "portrait_" + i + ".gif", this);
		// tilesOnBoard.add(t);
		// }
		// } else {
		// for (int i = 0; i < size - 1; i++) {
		// Tile t = new Tile(tileW, tileH, pointsOnBoard.remove(0), "tile"
		// + i, null, i, "circle_" + i + ".gif", this);
		// tilesOnBoard.add(t);
		// }
		// }

		// Constants.tiles = new ArrayList<BufferedImage>();

		BufferedImage tmp = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = tmp.createGraphics();
		g.drawImage(Constants.Chosen_Img, 0, 0, width, height, null);
		g.dispose();
		Constants.Chosen_Img = tmp;

		int imgW = Constants.Chosen_Img.getWidth();
		int imgH = Constants.Chosen_Img.getHeight();

		int tileW = imgW / tilesNumX;
		int tileH = imgH / tilesNumY;

		for (int y = 0; y < tilesNumY; y++) {
			for (int x = 0; x < tilesNumX; x++) {
				if (y == tilesNumY - 1 && x == tilesNumX - 1) {
					break;
				}
				BufferedImage subImage = Constants.Chosen_Img.getSubimage(x
						* tileW, y * tileH, tileW - 10, tileH - 10);
				// Constants.tiles.add(subImage);
				Tile t = new Tile(tileW, tileH, pointsOnBoard.remove(0),
						subImage, this, y * tilesNumX + x);
				tilesOnBoard.add(t);
				
				Constants.tileIDToPos.put(t.id, new Point(t.posOnBoard));
			}
		}

		Random r = new Random();

		freePos = pointsOnBoard.remove(0);

		updateBoard();

		setChanged();
		notifyObservers(tilesOnBoard);

		// do randomly create start positions and record the steps
		int Low = 10;
		int High = 20;
		int numSteps = r.nextInt(High - Low) + Low;

		for (int i = 0; i <= numSteps; i++) {
			int randIndex = r.nextInt(movableTiles.size());
			if (lastTile == null || lastTile != movableTiles.get(randIndex)) {
				moveTile(movableTiles.get(randIndex));
			} else {
				movableTiles.remove(randIndex);
				if (movableTiles.size() > 1) {
					randIndex = r.nextInt(movableTiles.size());
					moveTile(movableTiles.get(randIndex));
				} else {
					randIndex = 0;
					moveTile(movableTiles.get(randIndex));
				}
			}

		}

		Constants.initialFreePos = freePos;

		for (Tile t : tilesOnBoard) {
			Constants.nameToId.put(t.name, t);
		}

		init = false;
		solved = false;
		updateBoard();
	}

	public ArrayList<Tile> getTilesOnBoard() {
		return tilesOnBoard;
	}

	/**
	 * Updates the board after each move of a tile.
	 */
	private void updateBoard() {
		movableTiles.clear();
		boolean solvedTmp = !init;
		for (Tile t : tilesOnBoard) {
			
			if (!solved && t.posOnBoard.distance(Constants.tileIDToPos.get(t.id)) != 0) {
				solvedTmp = false;
			}

			double d = Point.distance(t.posOnBoard.x, t.posOnBoard.y,
					freePos.x, freePos.y);
			if (d == 1) {
				if (t.posOnBoard.x == freePos.x) {
					if (t.posOnBoard.y == freePos.y - 1) {
						t.moveDir = MoveDirection.DOWN;
						System.out.println("Tile on pos: " + t.posOnBoard.x
								+ ", " + t.posOnBoard.y + ", can move "
								+ t.moveDir);
						movableTiles.add(t);
					} else if (t.posOnBoard.y == freePos.y + 1) {
						t.moveDir = MoveDirection.UP;
						System.out.println("Tile on pos: " + t.posOnBoard.x
								+ ", " + t.posOnBoard.y + ", can move "
								+ t.moveDir);
						movableTiles.add(t);
					}
				} else if (t.posOnBoard.y == freePos.y) {
					if (t.posOnBoard.x == freePos.x - 1) {
						t.moveDir = MoveDirection.RIGHT;
						System.out.println("Tile on pos: " + t.posOnBoard.x
								+ ", " + t.posOnBoard.y + ", can move "
								+ t.moveDir);
						movableTiles.add(t);
					} else if (t.posOnBoard.x == freePos.x + 1) {
						t.moveDir = MoveDirection.LEFT;
						System.out.println("Tile on pos: " + t.posOnBoard.x
								+ ", " + t.posOnBoard.y + ", can move "
								+ t.moveDir);
						movableTiles.add(t);
					}

				}
			} else {
				t.moveDir = MoveDirection.NONE;
			}
		}
		
		if (solvedTmp) {
			solved = true;
			Constants.solvingSteps.clear();
			serverCommmunicator.sendSolvedToAll();
		}
	}

	/**
	 * Callback method called from a tile, when it was moved.
	 * 
	 * @param p
	 *            - the new free position
	 * @param t
	 *            - the tile that was moved
	 */
	public void callbackTileMoved(Point p, Tile t) {
		setChanged();
		notifyObservers(t);
		freePos = new Point(p);
		System.out.println("free position at: " + p.x + ", " + p.y);
		updateBoard();
		System.out.println("new step");
	}

	// @Override
	// public void actionPerformed(ActionEvent e) {
	// // if (Constants.nameToId.containsKey(e.getActionCommand())) {
	// // Tile t = Constants.nameToId.get(e.getActionCommand());
	// // if (t.moveDir != MoveDirection.NONE) {
	// // setChanged();
	// // notifyObservers(t);
	// // t.slide(freePos);
	// // // updateBoard();
	// // }
	// // }
	// }

	/**
	 * Method called from from clients through the server communicator to move a
	 * tile.
	 * 
	 * @param name
	 *            - name of the tile to be moved
	 */
	public void moveTile(String name) {
		Tile t = Constants.nameToId.get(name);
		if (t.moveDir != MoveDirection.NONE) {
			setChanged();
			notifyObservers(t);
			isCorrectStep(t);
			t.slide(freePos);
		}
	}

	private void isCorrectStep(Tile t) {
		Tile nextStep = Constants.solvingSteps.get(Constants.solvingSteps
				.size() - 1);
		if (nextStep.id == t.id && nextStep.moveDir == t.moveDir
				&& nextStep.posOnBoard.x == t.posOnBoard.x
				&& nextStep.posOnBoard.y == t.posOnBoard.y) {
			Constants.solvingSteps.remove(Constants.solvingSteps.size()-1);
			if (Constants.solvingSteps.isEmpty()) {
				//game was solved
				solved = true;
				serverCommmunicator.sendSolvedToAll();
			}
		} else {
			Constants.solvingSteps.add(t);
		}
	}

	/**
	 * Solves the game.
	 */
	public void solve() {
		for (int i = Constants.solvingSteps.size() - 1; i >= 0; i--) {
			Tile t = Constants.solvingSteps.get(i);
			// t.moveDir = s.direction;

			System.out.println("solve: " + i + ", from: " + t.posOnBoard.x
					+ ", " + t.posOnBoard.y + ", to: " + t.getMoveDirection());

			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			setChanged();
			notifyObservers(t);
			t.slide(freePos);
		}
		Constants.solvingSteps.clear();
		solved = true;
	}

	/**
	 * Moves the given tile.
	 * 
	 * @param t
	 *            - tile to be moved
	 */
	public void moveTile(Tile t) {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (t.moveDir != MoveDirection.NONE) {
			setChanged();
			notifyObservers(t);
			lastTile = t;
			t.slide(freePos);
			Constants.solvingSteps.add(t);
			// updateBoard();
		}
	}

	public void setCommunicator(ServerCommunicator sc) {
		this.serverCommmunicator = sc;
	}

	// private MoveDirection getOppsiteDirection(MoveDirection moveDir) {
	// if (moveDir == MoveDirection.DOWN) {
	// return MoveDirection.UP;
	// } else if (moveDir == MoveDirection.RIGHT) {
	// return MoveDirection.LEFT;
	// } else if (moveDir == MoveDirection.LEFT) {
	// return MoveDirection.RIGHT;
	// } else if (moveDir == MoveDirection.UP) {
	// return MoveDirection.DOWN;
	// } else {
	// return MoveDirection.NONE;
	// }
	// }

}
