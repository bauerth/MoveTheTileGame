package de.dfki.movethetileremote.communication;

import java.io.IOException;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import de.dfki.movethetileremote.Constants;
import de.dfki.movethetileremote.communication.Network.GameBoardResponse;
import de.dfki.movethetileremote.communication.Network.MoveRequest;
import de.dfki.movethetileremote.communication.Network.RegisterUserRequest;
import de.dfki.movethetileremote.communication.Network.StringMessage;
import de.dfki.movethetileremote.gui.Tile;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.widget.Toast;

public class ClientCommunicator {

	/**
	 * Constants TCP and UDP port.
	 */
	private final int TCP_PORT = 54555;

	/**
	 * instance of this class, to use it as singleton
	 */
	public static ClientCommunicator mClientCommunicator;

	/**
	 * Client instance to send and receive messages.
	 */
	private Client client;

	/**
	 * handler to send callback messages to the game activity
	 */
	private Handler interactionHandler;

	/**
	 * Context of the calling activity
	 */
	private Context ctx;
	

	/**
	 * Creates a new instance of this communicator class.
	 * 
	 * @param ctx
	 *            - context of the calling activity
	 * @param handler
	 *            - handler to pass messages to an activity
	 */
	public ClientCommunicator(Context ctx, Handler handler) {
		this.ctx = ctx;
		this.interactionHandler = handler;

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();

		StrictMode.setThreadPolicy(policy);

		init();
	}

	/**
	 * Creates a instance of this class as singleton.
	 * 
	 * @param _context
	 * @return
	 */
	public static ClientCommunicator getInstance(Context _context,
			Handler handler) {
		synchronized (ClientCommunicator.class) {
			if (mClientCommunicator == null) {
				mClientCommunicator = new ClientCommunicator(_context, handler);
			}
		}
		return mClientCommunicator;
	}

	/**
	 * 
	 */
	private void init() {
		this.client = new Client(8192, 131072);
		this.client.start();

		// For consistency, the classes to be sent over the network are
		// registered by the same method for both the client and server.
		Network.register(client);

		// Add listener for response messages
		client.addListener(new Listener() {

			@Override
			public void connected(Connection conn) {
				// When connected send a register request
				System.out.println("Connection message received: "
						+ conn.toString());
				sendRegisterRequest(Constants.OWN_IP, Constants.SCREEN_W,
						Constants.SCREEN_H);
			}

			@Override
			public void disconnected(Connection conn) {
				// nothing to do when disconnecting
				System.out.println("Disconnect message received: "
						+ conn.toString());
			}

			@Override
			public void received(Connection conn, Object obj) {
				if (obj instanceof MoveRequest) {
					// Getting a move request if another player has just moved a
					// tile
					MoveRequest mr = (MoveRequest) obj;
					final Bundle b = new Bundle();
					b.putString(Constants.TILETOMOVE, mr.tileName);
					b.putBoolean(Constants.MOVE_TILE, true);
					Message msg = new Message();
					msg.setData(b);
					interactionHandler.sendMessage(msg);
				} else if (obj instanceof GameBoardResponse) {
					// After a register request was successfully sent, the
					// server responses with a game board response containing
					// all necessary information about the game state to build
					// the game board
					System.out.println("game board response received");
					GameBoardResponse gbr = (GameBoardResponse) obj;

					//reset all global variables
					Constants.nameToTile.clear();
					Constants.stringToBmp.clear();
					Constants.currentBlank = null;
					Constants.currentDragged = null;
					Constants.Blank_Bmp = null;
					Constants.TileSize = null;
					
					Constants.NumPuzzleTiles = gbr.numPuzzleTiles;
					Constants.TilesNumX = gbr.numTilesX;
					Constants.TilesNumY = gbr.numTilesY;
					
					for (TileInformation ti : gbr.tiles) {
						if (ti.type.equalsIgnoreCase(Constants.IMAGE)) {
							Bitmap bmp = BitmapFactory.decodeByteArray(
									ti.imageAsByteArray, 0,
									ti.imageAsByteArray.length);

							if (Constants.TileSize == null) {
								Constants.TileSize = new Point(
										bmp.getWidth() + 4, bmp.getHeight() + 4);
							}

							Constants.nameToTile.put(ti.name,
									new Tile(new Point(ti.posOnBoard.x,
											ti.posOnBoard.y), ti.name, ti.id,
											ti.moveDir, ti.type, bmp));

							Constants.stringToBmp.put(ti.name, bmp);

						} else {
							Constants.Blank_Bmp = Bitmap.createBitmap(ti.imgW,
									ti.imgH, Config.RGB_565);

							Constants.nameToTile.put(ti.name,
									new Tile(new Point(ti.posOnBoard.x,
											ti.posOnBoard.y), ti.name, ti.id,
											ti.moveDir, ti.type,
											Constants.Blank_Bmp));

							Constants.stringToBmp.put(ti.name,
									Constants.Blank_Bmp);
						}
					}

					final Bundle b = new Bundle();
					b.putBoolean(Constants.NEW_GAME, true);
					Message msg = new Message();
					msg.setData(b);
					interactionHandler.sendMessage(msg);
				} else if (obj instanceof StringMessage) {
					StringMessage sm = (StringMessage) obj;
					if (sm.message.equalsIgnoreCase("solve")) {
						final Bundle b = new Bundle();
						b.putBoolean(Constants.GAME_END, true);
						Message msg = new Message();
						msg.setData(b);
						interactionHandler.sendMessage(msg);
					}
				}
			}

		});

		// start client
		try {
			System.out.println("try to connect");
			//set timeout to zero to prevent from auto disconnect
			client.setTimeout(0);
			client.connect(5000, Constants.HOST_IP, TCP_PORT);
			client.setTimeout(0);
		} catch (IOException e) {
			e.printStackTrace();
			Toast.makeText(ctx, "Keine Verbindung möglich", Toast.LENGTH_SHORT)
					.show();
		}
	}

	/**
	 * Sends a move message to the server
	 * 
	 * @param tile
	 */
	public void sendMoveRequset(Tile tile) {
		MoveRequest mr = new MoveRequest(tile.id, tile.name, tile.moveDir,
				new TilePoint(tile.posOnBoard.x, tile.posOnBoard.y));
		client.sendTCP(mr);
	}

	/**
	 * Sends a solve message to the server
	 */
	public void sendSolveMessage() {
		StringMessage sm = new StringMessage("solve");
		client.sendTCP(sm);
	}

	/**
	 * Sends a new game message to the server.
	 */
	public void sendNewGameMessage() {
		StringMessage sm = new StringMessage("new_game");
		client.sendTCP(sm);
	}

	/**
	 * Sends a register user request to the server.
	 * @param myIP - IP of the connecting device
	 * @param screenW - screen width of the device
	 * @param screenH - screen height of the device
	 */
	public void sendRegisterRequest(String myIP, int screenW, int screenH) {
		RegisterUserRequest rur = new RegisterUserRequest(myIP, screenW,
				screenH);
		client.sendTCP(rur);
	}

	/**
	 * Closes the connection to the server.
	 */
	public void disconnect() {
		System.out.println("closing the connection");
		client.stop();
		client.close();
		ClientCommunicator.mClientCommunicator = null;
	}

}
