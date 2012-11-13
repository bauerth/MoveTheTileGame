package communicator;

import gui.Tile;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import communicator.Network.StringMessage;
import communicator.Network.*;

import core.Constants;
import core.GameBoard;
import core.MoveDirection;

public class ServerCommunicator {

	/**
	 * Constant TCP Port.
	 */
	private final int TCP_PORT = 54555;

	/**
	 * server object used to receive and send messages.
	 */
	private Server server;

	/**
	 * reference to the game board to pass changes.
	 */
	private GameBoard gameBoard;

	/**
	 * storing the last string message, to ignore two or more received 'solve'
	 * or 'new_game' messages.
	 */
	private String lastStringMessage = "";

	/**
	 * Creates a new instance of this class and initializes the server object.
	 * 
	 * @param gb
	 */
	public ServerCommunicator(GameBoard gb) {
		this.gameBoard = gb;

		init();
	}

	/**
	 * Initializes the server object according to some samples of kryonet.
	 */
	private void init() {
		server = new Server(131072, 131072);

		Network.register(server);

		server.addListener(new Listener() {

			@Override
			public void connected(Connection conn) {
				// Nothing to do when a client establishes a connection
				System.out.println("Connection message received");
			}

			@Override
			public void disconnected(Connection conn) {
				// When a client is disconnecting, delete it in the map
				System.out.println("Disconnect message received");
				Constants.registeredUsers.remove(conn.getID());
			}

			@Override
			public void received(Connection conn, Object obj) {
				if (obj instanceof MoveRequest) {
					// Passing the move to the game board and notify other
					// clients to update their game
					MoveRequest mr = (MoveRequest) obj;
					gameBoard.moveTile(mr.tileName);

					// server.sendToAllExceptTCP(conn.getID(), mr);
					Iterator<Entry<Integer, RegisterUserRequest>> it = Constants.registeredUsers
							.entrySet().iterator();
					while (it.hasNext()) {
						Entry<Integer, RegisterUserRequest> entry = it.next();
						if (entry.getKey() != conn.getID()) {
							server.sendToTCP(entry.getKey(), mr);
						}
					}
				} else if (obj instanceof RegisterUserRequest) {
					// Adding a user with the connection ID to the map.
					RegisterUserRequest rur = (RegisterUserRequest) obj;
					System.out.println("register user request: " + rur.getIP()
							+ ", " + rur.getW() + ", " + rur.getH());
					Constants.registeredUsers.put(conn.getID(), rur);

					if (Constants.solvingSteps.isEmpty()) {
						gameBoard.newGame();
					}
					sendGameBoardResponse(rur, conn.getID());

				} else if (obj instanceof StringMessage) {
					// Two kinds of string messages can be received: 'solve' and
					// 'new_game'
					StringMessage sm = (StringMessage) obj;
					System.out.println("String message received: " + sm.message
							+ ", lastStringMessage: " + lastStringMessage);
					if (sm.message.equalsIgnoreCase("solve")
							&& !lastStringMessage.equalsIgnoreCase("solve")) {
						// Solving the game and notify also the clients that the
						// game is over
						lastStringMessage = "solve";
						gameBoard.solve();

						// System.out.println("send solve message to everyone");
						// // server.sendToAllTCP(sm);
						// Iterator<Entry<Integer, RegisterUserRequest>> it =
						// Constants.registeredUsers
						// .entrySet().iterator();
						// while (it.hasNext()) {
						// Entry<Integer, RegisterUserRequest> entry = it
						// .next();
						// System.out.println("send solve msg to: " +
						// entry.getKey() + ", " + entry.getValue().getIP());
						// server.sendToTCP(entry.getKey(), sm);
						// }
					} else if (sm.message.equalsIgnoreCase("new_game")
							&& !lastStringMessage.equalsIgnoreCase("new_game")) {
						// creates a new game and notifies all registered
						// clients to build the game
						lastStringMessage = "new_game";
						gameBoard.newGame();

						Iterator<Entry<Integer, RegisterUserRequest>> it = Constants.registeredUsers
								.entrySet().iterator();
						while (it.hasNext()) {
							Entry<Integer, RegisterUserRequest> entry = it
									.next();
							sendGameBoardResponse(entry.getValue(),
									entry.getKey());
						}
					}
				}
			}

		});

		try {
			server.bind(TCP_PORT);
			server.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sends the game board response to the given user with the given connection
	 * ID.
	 * 
	 * @param rur
	 *            - registered user
	 * @param connID
	 *            - connection ID of the registered user
	 */
	public void sendGameBoardResponse(RegisterUserRequest rur, int connID) {
		ArrayList<Tile> tiles = gameBoard.getTilesOnBoard();

		float scaleW = (rur.getW() * 1.f) / Constants.SCREEN_W;
		float scaleH = (rur.getH() * 1.f) / Constants.SCREEN_H;

		float scale = (scaleW < scaleH) ? scaleW : scaleH;

		ArrayList<TileInformation> tileInformationList = new ArrayList<TileInformation>();

		int newW = 0;
		int newH = 0;

		for (Tile t : tiles) {
			newW = (int) (t.img.getWidth() * scale);
			newH = (int) (t.img.getHeight() * scale);
			// System.out.println("new size: " + newW + ", " + newH);
			BufferedImage tmp = new BufferedImage(newW, newH,
					BufferedImage.TYPE_3BYTE_BGR);
			Graphics2D g = tmp.createGraphics();
			g.drawImage(t.img, 0, 0, newW, newH, null);
			g.dispose();

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try {
				ImageIO.write(tmp, "jpg", baos);

				baos.flush();
				byte[] buffer = baos.toByteArray();

				// System.out.println("bytes used: " + buffer.length);

				tileInformationList.add(new TileInformation(t.id, t.moveDir,
						new TilePoint(t.posOnBoard.x, t.posOnBoard.y), t.name,
						buffer, t.type, newW, newH));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		tileInformationList.add(new TileInformation(tiles.size(),
				MoveDirection.NONE, new TilePoint(gameBoard.freePos.x,
						gameBoard.freePos.y), "tile" + tiles.size(), null,
				"blank", newW, newH));

		System.out.println("try to send game board information");

		server.sendToTCP(connID, new Network.GameBoardResponse(
				tileInformationList.size(), Constants.TilesNumX,
				Constants.TilesNumY, tileInformationList));
	}

	/**
	 * Sends a solved message to all clients
	 */
	public void sendSolvedToAll() {
		System.out.println("sending solve message to all clients...");
		Iterator<Entry<Integer, RegisterUserRequest>> it = Constants.registeredUsers
				.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Integer, RegisterUserRequest> entry = it.next();
			System.out.println("send solve msg to: " + entry.getKey() + ", "
					+ entry.getValue().getIP());
			server.sendToTCP(entry.getKey(), new StringMessage("solve"));
		}
	}
}
