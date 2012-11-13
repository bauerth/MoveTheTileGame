package core;

import gui.Tile;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

import communicator.Network.RegisterUserRequest;

public class Constants {

	/**
	 * pre-defined numbers for tiles in x and y direction
	 */
	public static int TilesNumX = 3;
	public static int TilesNumY = 3;

	/**
	 * mapping from action name of tile to tile
	 */
	public static HashMap<String, Tile> nameToId = new HashMap<String, Tile>();

	/**
	 * start coordinates for the tiles to be drawn, i.e. the (x,y) coordinate of
	 * the upper left point of the real game board
	 */
	public static int startX;
	public static int startY;
	
	/**
	 * initial free position
	 */
	public static Point initialFreePos;

	/**
	 * List containing the steps, that are needed to solve the game.
	 */
	public static ArrayList<Tile> solvingSteps = new ArrayList<Tile>();

	/**
	 * Fields to store the settings made in the first window.
	 */
	// flag indicating if the window should be full screen
	public static boolean isFullScreen = true;
	// the size of the screen
	public static int SCREEN_W, SCREEN_H;
	// path to the chosen image
	public static String pathToSourceImage;
	// the chosen image
	public static BufferedImage Chosen_Img;
	// list containing all tile images
//	public static ArrayList<BufferedImage> tiles;
	// flag indicationg if the game should be displayed on a second screen
	public static boolean switchScreen = false;

	/**
	 * Hash map mapping the connection id to the registered user
	 */
	public static HashMap<Integer, RegisterUserRequest> registeredUsers = new HashMap<Integer, RegisterUserRequest>();
	
	
	/**
	 * Hashmap containing the correct positions for each tile
	 */
	public static HashMap<Integer, Point> tileIDToPos = new HashMap<Integer, Point>();
	
	/**
	 * Point where to draw the game board window, if we switch to another monitor
	 */
	public static Point GameTopLeft;
}
