package de.dfki.movethetileremote;

import java.util.HashMap;

import android.graphics.Bitmap;
import android.graphics.Point;
import de.dfki.movethetileremote.gui.DraggableTile;
import de.dfki.movethetileremote.gui.Tile;

public class Constants {

	/**
	 * constant to identify the new game message of the communication between activity and communicator
	 */
	public static final String NEW_GAME = "new_game";

	/**
	 * constants to identify the request code of the game activity returning
	 */
	public static final int Game_Done = 1;

	/**
	 * constant to identify the tile move message of the communication between activity and communicator
	 */
	public static final String MOVE_TILE = "move_tile";

	/**
	 * Tile to be moved in the own game, when a move request is coming from the server.
	 */
	public static final String TILETOMOVE = "tile_to_move";

	/**
	 * constant to identify the game end message of the communication between activity and communicator
	 */
	public static final String GAME_END = "game_end";

	/**
	 * IP address of the server
	 */
	public static String HOST_IP;

	/**
	 * mapping from name of tile to tile
	 */
	public static HashMap<String, Tile> nameToTile = new HashMap<String, Tile>();
	
	/**
	 * mapping from tile name to tile image
	 */
	public static HashMap<String, Bitmap> stringToBmp = new HashMap<String, Bitmap>();

	/**
	 * constants used for tile type
	 */
	public static String BLANK = "blank";
	public static String IMAGE = "image";
	
	/**
	 * global variable indicating if a tile was dropped on the free field (blank)
	 */
	public static boolean droppedOnBlank = false;

	/**
	 * global variable referencing to the tile, that is free
	 */
	public static DraggableTile currentBlank = null;
	
	/**
	 * global variable referencing to the tile, that is currently dragging
	 */
	public static DraggableTile currentDragged = null;

	/**
	 * height of the device's screen
	 */
	public static int SCREEN_H;

	/**
	 * width of the device's screen
	 */
	public static int SCREEN_W;

	/**
	 * number of the tiles in the game
	 */
	public static int NumPuzzleTiles;

	/**
	 * size of the tiles
	 */
	public static Point TileSize = null;

	/**
	 * the device's IP address
	 */
	public static String OWN_IP;

	/**
	 * global variable referencing to a bitmap that is just white, used for the blank tile
	 */
	public static Bitmap Blank_Bmp;

	/**
	 * global variable storing the number of tiles in x direction
	 */
	public static int TilesNumX;

	/**
	 * global variable storing the number of tiles in y direction
	 */
	public static int TilesNumY;
}
