package communicator;

import java.util.ArrayList;


import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

import core.MoveDirection;


/**
 * This class is a convenient place to keep things common to both the client and server.
 */
public class Network {

	

	// This registers objects that are going to be sent over the network.
    static public void register (EndPoint endPoint) {
            Kryo kryo = endPoint.getKryo();
            kryo.register(RegisterUserRequest.class);
            kryo.register(TilePoint.class);
            kryo.register(MoveDirection.class);
            kryo.register(MoveRequest.class);
            kryo.register(ArrayList.class);
            kryo.register(byte[].class);
            kryo.register(TileInformation.class);
            kryo.register(GameBoardResponse.class);
            kryo.register(StringMessage.class);
    }

    
    /**
     * Register User request containing necessary information about the client that want to register at the server.
     * @author Christian
     *
     */
    static public class RegisterUserRequest {

    	private String IP;
    	private int w, h;
    	
    	public RegisterUserRequest() {}
    	
    	public RegisterUserRequest(String IP, int w, int h) {
    		this.IP = IP;
    		this.w = w;
    		this.h = h;
    	}

    	public String getIP() {
    		return IP;
    	}

    	public void setIP(String iP) {
    		IP = iP;
    	}

    	public int getW() {
    		return w;
    	}

    	public void setW(int w) {
    		this.w = w;
    	}

    	public int getH() {
    		return h;
    	}

    	public void setH(int h) {
    		this.h = h;
    	}	
    }
    
    /**
     * Move request sent to both sides (client and server) to move a tile.
     * @author Christian
     *
     */
    static public class MoveRequest {

    	/**
    	 * ID of the tile to be moved
    	 */
    	public int tileID;
    	
    	/**
    	 * name of the tile to be moved
    	 */
    	public String tileName;
    	
    	/**
    	 * Direction to which the tile has to be moved.
    	 */
    	public MoveDirection moveDir;
    	
    	/**
    	 * Tile position on game board.
    	 */
    	public TilePoint currentTilePos;
    	
    	public MoveRequest() {}
    	
    	public MoveRequest(int ID, String name, MoveDirection dir, TilePoint pos) {
    		this.tileID = ID;
    		this.tileName = name;
    		this.moveDir = dir;
    		this.currentTilePos = pos;
    	}
    }
    
    /**
     * Game response containing all information about the gameboard.
     * @author Christian
     *
     */
    static public class GameBoardResponse {

    	/**
    	 * number of tiles on board
    	 */
    	public int numPuzzleTiles;
    	
    	/**
    	 * number of tiles in x and y direction
    	 */
    	public int numTilesX, numTilesY;
    	
    	/**
    	 * list containing information for each tile on board to build the game
    	 */
    	public ArrayList<TileInformation> tiles;
    	
    	public GameBoardResponse() {}
    	
    	public GameBoardResponse(int numPuzzleTiles, int numX, int numY, ArrayList<TileInformation> tiles) {
    		this.numPuzzleTiles = numPuzzleTiles;
    		this.numTilesX = numX;
    		this.numTilesY = numY;
    		this.tiles = tiles;
    	}
    }
    
    /**
     * String message can be used to send messages, that only uses one identifier, like 'solve' or 'new game'
     * @author Christian
     *
     */
    static public class StringMessage {
    	public String message;
    	
    	public StringMessage() {}
    	
    	public StringMessage(String msg) {
    		this.message = msg;
    	}
    }
}
