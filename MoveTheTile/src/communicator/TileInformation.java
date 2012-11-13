package communicator;

import core.MoveDirection;


public class TileInformation {

	/**
	 * ID of the tile
	 */
	public int id;

	/**
	 * the direction in which this tile can be moved
	 */
	public MoveDirection moveDir;

	/**
	 * current position on the game board
	 */
	public TilePoint posOnBoard;

	/**
	 * name of the tile, build out of a string 'tile' plus the id
	 */
	public String name;
	
	/**
	 * image represented by an byte array
	 */
	public byte[] imageAsByteArray;
	
	/**
	 * type of the tile
	 */
	public String type;
	
	/**
	 * width of the tile image
	 */
	public int imgW;
	
	/**
	 * height of the tile image
	 */
	public int imgH;
	
	public TileInformation() {}
	
	public TileInformation(int ID, MoveDirection moveDir, TilePoint posOnBoard, String name, byte[] img, String type, int imgW, int imgH) {
		this.id = ID;
		this.name = name;
		this.posOnBoard = posOnBoard;
		this.moveDir = moveDir;
		this.imageAsByteArray = img;
		this.type = type;
		this.imgW = imgW;
		this.imgH = imgH;
	}
}
