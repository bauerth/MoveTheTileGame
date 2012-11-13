package de.dfki.movethetileremote.gui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import de.dfki.movethetileremote.Constants;
import de.dfki.movethetileremote.MoveDirection;
import de.dfki.movethetileremote.R;
import de.dfki.movethetileremote.communication.ClientCommunicator;
import android.app.Activity;
import android.graphics.Point;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class GameActivity extends Activity {

	/**
	 * counter needed to ensure, that every draggable tile called its drag ended
	 * method. Only after this is ensured, the game board is updated.
	 */
	private int callbackCounter = 0;

	/**
	 * list containing all draggable tiles on the game board
	 */
	private ArrayList<DraggableTile> draggableTiles;

	/**
	 * layout containing the game board.
	 */
	private RelativeLayout container;

	/**
	 * flag indicating if we can do a new game
	 */
	private boolean solved = false;

	/**
	 * list containing all tiles in sorted order compared by the position on
	 * board
	 */
	private ArrayList<Tile> sortedTiles;

	/**
	 * Handler used to treat the callback messages sent from the communicator.
	 */
	private final Handler interactionCallbackHandler = new Handler() {
		@Override
		public void handleMessage(final android.os.Message msg) {
			super.handleMessage(msg);
			final Bundle bundle = msg.getData();
			if (bundle != null) {
				if (bundle.getBoolean(Constants.MOVE_TILE)) {
					handleMoveMessage(bundle.getString(Constants.TILETOMOVE));
				} else if (bundle.getBoolean(Constants.GAME_END)) {
					// finish();
					// container.removeAllViews();
					// container.invalidate();
					// container.requestLayout();
//					container.setVisibility(View.GONE);
//					container.removeAllViews();
					solved = true;
					Toast.makeText(
							getApplicationContext(),
							"Das Spiel wurde gelöst. Sie können nun ein neues starten",
							Toast.LENGTH_SHORT).show();
				} else if (bundle.getBoolean(Constants.NEW_GAME)) {
					solved = false;
					container.setVisibility(View.VISIBLE);
					initializePuzzle(GameActivity.this);
				}
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game_activity);

		this.container = (RelativeLayout) findViewById(R.id.container);

		ClientCommunicator.getInstance(this, interactionCallbackHandler);
	}

	/**
	 * Initializes the puzzle game. Creates for each tile a draggable tile
	 * object and redraws the game board.
	 * 
	 * @param ga
	 *            - the activity itself
	 */
	private void initializePuzzle(final GameActivity ga) {
		Collection<Tile> tiles = Constants.nameToTile.values();

		sortedTiles = new ArrayList<Tile>();
		sortedTiles.addAll(tiles);

		Collections.sort(sortedTiles, new Comparator<Tile>() {

			@Override
			public int compare(Tile lhs, Tile rhs) {
				if (lhs.posOnBoard.y < rhs.posOnBoard.y) {
					return -1;
				} else if (lhs.posOnBoard.y > rhs.posOnBoard.y) {
					return 1;
				} else /* if (lhs.posOnBoard.y == rhs.posOnBoard.y) */{
					if (lhs.posOnBoard.x < rhs.posOnBoard.x) {
						return -1;
					} else if (lhs.posOnBoard.x > rhs.posOnBoard.x) {
						return 1;
					} else /* if (lhs.posOnBoard.x == rhs.posOnBoard.x) */{
						return 0;
					}
				}
			}
		});

		draggableTiles = new ArrayList<DraggableTile>();
		//
		// for (Tile t : tiles) {
		// if (t.posOnBoard.equals(0, 0)) {
		// DraggableTile dTile = (DraggableTile) findViewById(R.id.drag_tile_1);
		// dTile.getLayoutParams().width = Constants.TileSize.x;
		// dTile.getLayoutParams().height = Constants.TileSize.y;
		// dTile.setTile(new Tile(t.posOnBoard, t.name, t.id, t.moveDir,
		// t.type, t.tileImg));
		// dTile.setCallback(ga);
		// draggableTiles.add(dTile);
		// } else if (t.posOnBoard.equals(1, 0)) {
		// DraggableTile dTile = (DraggableTile) findViewById(R.id.drag_tile_2);
		// dTile.getLayoutParams().width = Constants.TileSize.x;
		// dTile.getLayoutParams().height = Constants.TileSize.y;
		// dTile.setTile(new Tile(t.posOnBoard, t.name, t.id, t.moveDir,
		// t.type, t.tileImg));
		// dTile.setCallback(ga);
		// draggableTiles.add(dTile);
		// } else if (t.posOnBoard.equals(2, 0)) {
		// DraggableTile dTile = (DraggableTile) findViewById(R.id.drag_tile_3);
		// dTile.getLayoutParams().width = Constants.TileSize.x;
		// dTile.getLayoutParams().height = Constants.TileSize.y;
		// dTile.setTile(new Tile(t.posOnBoard, t.name, t.id, t.moveDir,
		// t.type, t.tileImg));
		// dTile.setCallback(ga);
		// draggableTiles.add(dTile);
		// } else if (t.posOnBoard.equals(0, 1)) {
		// DraggableTile dTile = (DraggableTile) findViewById(R.id.drag_tile_4);
		// dTile.getLayoutParams().width = Constants.TileSize.x;
		// dTile.getLayoutParams().height = Constants.TileSize.y;
		// dTile.setTile(new Tile(t.posOnBoard, t.name, t.id, t.moveDir,
		// t.type, t.tileImg));
		// dTile.setCallback(ga);
		// draggableTiles.add(dTile);
		// } else if (t.posOnBoard.equals(1, 1)) {
		// DraggableTile dTile = (DraggableTile) findViewById(R.id.drag_tile_5);
		// dTile.getLayoutParams().width = Constants.TileSize.x;
		// dTile.getLayoutParams().height = Constants.TileSize.y;
		// dTile.setTile(new Tile(t.posOnBoard, t.name, t.id, t.moveDir,
		// t.type, t.tileImg));
		// dTile.setCallback(ga);
		// draggableTiles.add(dTile);
		// } else if (t.posOnBoard.equals(2, 1)) {
		// DraggableTile dTile = (DraggableTile) findViewById(R.id.drag_tile_6);
		// dTile.getLayoutParams().width = Constants.TileSize.x;
		// dTile.getLayoutParams().height = Constants.TileSize.y;
		// dTile.setTile(new Tile(t.posOnBoard, t.name, t.id, t.moveDir,
		// t.type, t.tileImg));
		// dTile.setCallback(ga);
		// draggableTiles.add(dTile);
		// } else if (t.posOnBoard.equals(0, 2)) {
		// DraggableTile dTile = (DraggableTile) findViewById(R.id.drag_tile_7);
		// dTile.getLayoutParams().width = Constants.TileSize.x;
		// dTile.getLayoutParams().height = Constants.TileSize.y;
		// dTile.setTile(new Tile(t.posOnBoard, t.name, t.id, t.moveDir,
		// t.type, t.tileImg));
		// dTile.setCallback(ga);
		// draggableTiles.add(dTile);
		// } else if (t.posOnBoard.equals(1, 2)) {
		// DraggableTile dTile = (DraggableTile) findViewById(R.id.drag_tile_8);
		// dTile.getLayoutParams().width = Constants.TileSize.x;
		// dTile.getLayoutParams().height = Constants.TileSize.y;
		// dTile.setTile(new Tile(t.posOnBoard, t.name, t.id, t.moveDir,
		// t.type, t.tileImg));
		// dTile.setCallback(ga);
		// draggableTiles.add(dTile);
		// } else if (t.posOnBoard.equals(2, 2)) {
		// DraggableTile dTile = (DraggableTile) findViewById(R.id.drag_tile_9);
		// dTile.getLayoutParams().width = Constants.TileSize.x;
		// dTile.getLayoutParams().height = Constants.TileSize.y;
		// dTile.setTile(new Tile(t.posOnBoard, t.name, t.id, t.moveDir,
		// t.type, t.tileImg));
		// dTile.setCallback(ga);
		// draggableTiles.add(dTile);
		// }
		// }

		// adding the first tile, that has to be declared in the layout file, to
		// make it visible (little bit hacky)
		Tile tile0 = sortedTiles.remove(0);
		DraggableTile dTile0 = (DraggableTile) findViewById(R.id.drag_tile_1);

		dTile0.getLayoutParams().width = Constants.TileSize.x;
		dTile0.getLayoutParams().height = Constants.TileSize.y;

		dTile0.setTile(new Tile(tile0.posOnBoard, tile0.name, tile0.id,
				tile0.moveDir, tile0.type, tile0.tileImg));
		dTile0.setCallback(ga);
		draggableTiles.add(dTile0);

		// adding the other draggable tiles dynamically
		int row = 2;
		int column = 1;
		int counter = 1;
		for (Tile t : sortedTiles) {
			// realized with layout inflater so build one layout and use it
			// every time
			DraggableTile dTile = (DraggableTile) LayoutInflater.from(this)
					.inflate(R.layout.draggable_tile_template, null);
			dTile.setId(counter);

			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
					Constants.TileSize.x, Constants.TileSize.y);

			if (row == 1) {
				if (column != 1) {
					lp.addRule(
							RelativeLayout.BELOW,
							draggableTiles
									.get(draggableTiles.size()
											- Constants.TilesNumX).getId());
//					System.out.println("*********** draggable tiles created: "
//							+ draggableTiles.size()
//							+ "\n"
//							+ "new tile: "
//							+ t.posOnBoard.x
//							+ ", "
//							+ t.posOnBoard.y
//							+ ", below "
//							+ draggableTiles
//									.get(draggableTiles.size()
//											- Constants.TilesNumX).getId());
				}
			} else {
				lp.addRule(RelativeLayout.RIGHT_OF,
						draggableTiles.get(draggableTiles.size() - 1).getId());
//				System.out
//						.println("*********** draggable tiles created: "
//								+ draggableTiles.size()
//								+ "\n"
//								+ "new tile: "
//								+ t.posOnBoard.x
//								+ ", "
//								+ t.posOnBoard.y
//								+ ", right to "
//								+ draggableTiles.get(draggableTiles.size() - 1)
//										.getId());
				if (column != 1) {
					lp.addRule(
							RelativeLayout.BELOW,
							draggableTiles
									.get(draggableTiles.size()
											- Constants.TilesNumX).getId());
					// System.out.println("*********** draggable tiles created: "
					// + draggableTiles.size()
					// + "\n"
					// + "new tile: "
					// + t.posOnBoard.x
					// + ", "
					// + t.posOnBoard.y
					// + ", below "
					// + draggableTiles
					// .get(draggableTiles.size()
					//							- Constants.TilesNumX).getId());
				}
			}

			dTile.setLayoutParams(lp);
			dTile.setTile(new Tile(t.posOnBoard, t.name, t.id, t.moveDir,
					t.type, t.tileImg));
			dTile.setCallback(ga);
			draggableTiles.add(dTile);

			// System.out.println("*********** draggable tiles created: "
			// + draggableTiles.size() + "\n" + "new tile: "
			// + t.posOnBoard.x + ", " + t.posOnBoard.y);

			container.addView(dTile);

			// setting the counters
			counter++;
			if (row == Constants.TilesNumX) {
				if (column < Constants.TilesNumY + 1) {
					column++;
					row = 1;
				}
			} else {
				row++;
			}

			// container.invalidate();
			// container.requestLayout();
		}

		for (DraggableTile dTile : draggableTiles) {
			if (dTile.tile.type.equalsIgnoreCase(Constants.BLANK)) {
				Constants.currentBlank = dTile;
			}
		}

		container.invalidate();
		container.requestLayout();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_solve:
			// send solve message to server
			Toast.makeText(this, "Das Spiel wird gelöst", Toast.LENGTH_SHORT).show();
			ClientCommunicator.getInstance(this, null).sendSolveMessage();
			container.setVisibility(View.GONE);
			// finish();
			return true;
		case R.id.menu_new:
			if (solved) {
				ClientCommunicator.getInstance(this, null).sendNewGameMessage();
			} else {
				Toast.makeText(this, "Das Spiel muss zuerst gelöst werden",
						Toast.LENGTH_SHORT).show();
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onBackPressed() {
		// close the connection when finishing the application
		ClientCommunicator.getInstance(this, null).disconnect();

		super.onBackPressed();
	}

	/**
	 * Handles the move message from the game server.
	 * 
	 * @param name
	 *            - name of the tile to be moved
	 */
	public void handleMoveMessage(String name) {
		DraggableTile tileToBeNewBlank = null;
		DraggableTile tileToBeNoMoreBlank = null;
		for (DraggableTile dTile : draggableTiles) {
			if (dTile.tile.name.equalsIgnoreCase(name)) {
				tileToBeNewBlank = dTile;
			}
			if (dTile.tile.type.equalsIgnoreCase(Constants.BLANK)) {
				tileToBeNoMoreBlank = dTile;
			}
		}

		if (tileToBeNewBlank != null
				&& tileToBeNoMoreBlank != null
				&& !tileToBeNewBlank.tile.type
						.equalsIgnoreCase(Constants.BLANK)) {
			Point newPoint = tileToBeNoMoreBlank.tile.posOnBoard;

			tileToBeNoMoreBlank.setTile(new Tile(
					tileToBeNewBlank.tile.posOnBoard,
					tileToBeNewBlank.tile.name, tileToBeNewBlank.tile.id,
					MoveDirection.NONE, tileToBeNewBlank.tile.type,
					tileToBeNewBlank.tile.tileImg));

			tileToBeNewBlank.setTile(new Tile(newPoint, "tile_blank", 9,
					MoveDirection.NONE, Constants.BLANK, Constants.Blank_Bmp));
			Constants.currentBlank = tileToBeNewBlank;

			tileToBeNewBlank.invalidate();
			tileToBeNoMoreBlank.invalidate();

			updateTiles();
		}
	}

	/**
	 * Method to handle the update process. Do the update just after all
	 * draggable tiles were updated --> callback counter!!!
	 */
	public void handleDragEnded() {
		callbackCounter++;
		if (callbackCounter == Constants.NumPuzzleTiles) {
			if (Constants.currentDragged != null) {
				for(DraggableTile dTile : draggableTiles) {
					if (dTile == Constants.currentDragged) {
						dTile.setTile(new Tile(dTile.tile.posOnBoard, "tile_blank",
								Constants.currentBlank.tile.id, MoveDirection.NONE,
								Constants.BLANK, Constants.Blank_Bmp));
						Constants.currentBlank = dTile;
						Constants.currentDragged = null;
					}
				}
			}
			System.out.println("drag ended");
			updateTiles();
			callbackCounter = 0;
			Constants.droppedOnBlank = false;
		}
	}

	/**
	 * Update the draggable tiles.
	 */
	private void updateTiles() {
		Point posToCompare = Constants.currentBlank.tile.posOnBoard;
		for (DraggableTile dTile : draggableTiles) {
			dTile.setOnLongClickListener(null);
			Tile t = dTile.tile;
			if (t.posOnBoard.y == posToCompare.y) {
				if (t.posOnBoard.x == posToCompare.x - 1) {
					t.moveDir = MoveDirection.RIGHT;
					dTile.setTile(t);
				} else if (t.posOnBoard.x == posToCompare.x + 1) {
					t.moveDir = MoveDirection.LEFT;
					dTile.setTile(t);
				}
			} else if (t.posOnBoard.x == posToCompare.x) {
				if (t.posOnBoard.y == posToCompare.y - 1) {
					t.moveDir = MoveDirection.DOWN;
					dTile.setTile(t);
				} else if (t.posOnBoard.y == posToCompare.y + 1) {
					t.moveDir = MoveDirection.UP;
					dTile.setTile(t);
				}
			}
		}
	}
}
