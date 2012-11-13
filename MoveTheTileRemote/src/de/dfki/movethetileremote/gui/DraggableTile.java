package de.dfki.movethetileremote.gui;

import de.dfki.movethetileremote.Constants;
import de.dfki.movethetileremote.MoveDirection;
import de.dfki.movethetileremote.communication.ClientCommunicator;
import android.content.ClipData;
import android.content.Context;
import android.drm.DrmStore.Action;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.DropBoxManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;

public class DraggableTile extends View {

	private Paint mPaint;
	private Paint mBitmapPaint;
	private Paint mGlow;
	public Tile tile;

	private boolean mDragInProgress;
	private boolean mHovering;
	private boolean mAcceptsDrag;

	private static final int NUM_GLOW_STEPS = 10;
	private static final int GREEN_STEP = 0x0000FF00 / NUM_GLOW_STEPS;
	private static final int WHITE_STEP = 0x00FFFFFF / NUM_GLOW_STEPS;
	private static final int ALPHA_STEP = 0xFF000000 / NUM_GLOW_STEPS;

	private GameActivity game;

	public DraggableTile(Context context, AttributeSet attrs) {
		super(context, attrs);

		setFocusable(true);
		setClickable(true);

		mBitmapPaint = new Paint(Paint.DITHER_FLAG);

		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setStrokeWidth(6);
		mPaint.setColor(Color.DKGRAY);

		mGlow = new Paint();
		mGlow.setAntiAlias(true);
		mGlow.setStrokeWidth(3);
		mGlow.setStyle(Paint.Style.STROKE);

	}

	public void setTile(Tile t) {
		this.tile = null;
		this.tile = t;

		if (tile.moveDir != MoveDirection.NONE) {
			setOnLongClickListener(new View.OnLongClickListener() {
				public boolean onLongClick(View v) {
					View.DragShadowBuilder myShadow = new MyDragShadowBuilder(v);
					ClipData data = ClipData.newPlainText("Tile",
							tile.toString());
					Constants.currentDragged = DraggableTile.this;
					v.startDrag(data, myShadow, (Object) tile.type, 0);
					return true;
				}
			});
		}
		invalidate();
	}

	public void setCallback(GameActivity game) {
		this.game = game;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (tile != null) {
			int w = tile.tileImg.getWidth() + 10;
			int h = tile.tileImg.getHeight() + 10;
			canvas.drawRect(0, 0, w, h, mPaint);
			canvas.drawBitmap(tile.tileImg, 5, 5, mBitmapPaint);

			// if we're in the middle of a drag, light up as a potential target
			if (mDragInProgress && this == Constants.currentBlank) {
				for (int i = NUM_GLOW_STEPS; i > 0; i--) {
					int color = (mHovering) ? WHITE_STEP : GREEN_STEP;
					color = i * (color | ALPHA_STEP);
					mGlow.setColor(color);
					canvas.drawRect(0, 0, w, h, mGlow);
				}
			}
		}
	}

	/**
	 * Drag and drop
	 */
	@Override
	public boolean onDragEvent(DragEvent event) {
		boolean result = false;
		switch (event.getAction()) {
		case DragEvent.ACTION_DRAG_STARTED: {
			// cache whether we accept the drag to return for LOCATION events
			mDragInProgress = true;
			mAcceptsDrag = result = true;

			// Redraw in the new visual state if we are a potential drop target
			if (this == Constants.currentBlank) {
				invalidate();
			}
		}
			break;
		case DragEvent.ACTION_DRAG_ENDED: {
			if (Constants.droppedOnBlank) {
				if (this == Constants.currentDragged) {
					invalidate();
					this.setOnLongClickListener(null);
					this.setTile(new Tile(this.tile.posOnBoard, "tile_blank",
							Constants.currentBlank.tile.id, MoveDirection.NONE,
							Constants.BLANK, Constants.Blank_Bmp));
					Constants.currentBlank = this;
					System.out.println("set new blank****");
					Constants.currentDragged = null;
				} else {
					invalidate();
				}
				game.handleDragEnded();
			} else {
				invalidate();
			}

			mDragInProgress = false;
			mHovering = false;
		}
			break;
		case DragEvent.ACTION_DRAG_LOCATION: {
			// we returned true to DRAG_STARTED, so return true here
			result = mAcceptsDrag;
		}
			break;

		case DragEvent.ACTION_DROP: {
			processDrop(event);
			result = true;
		}
			break;

		case DragEvent.ACTION_DRAG_ENTERED: {
			mHovering = true;
			invalidate();
		}
			break;

		case DragEvent.ACTION_DRAG_EXITED: {
			mHovering = false;
			invalidate();
		}
			break;

		default:
			result = mAcceptsDrag;
			break;
		}

		return result;

	}

	private void processDrop(DragEvent event) {
		final ClipData data = event.getClipData();
		final int N = data.getItemCount();
		for (int i = 0; i < N; i++) {
			ClipData.Item item = data.getItemAt(i);
			String text = item.coerceToText(getContext()).toString();
			if (this.tile.type.equalsIgnoreCase(Constants.BLANK)) {
				String[] tileArgs = text.split(";");
				this.setTile(new Tile(this.tile.posOnBoard, tileArgs[1],
						Integer.parseInt(tileArgs[0]), MoveDirection.NONE,
						tileArgs[4], Constants.stringToBmp.get(tileArgs[1])));
				Constants.droppedOnBlank = true;
				// Communicator.getInstance(null,
				// null).sendMoveTile(tileArgs[1]);
				ClientCommunicator.getInstance(getContext(), null)
						.sendMoveRequset(tile);
			} else {
				Constants.droppedOnBlank = false;
			}
		}
	}

	private class MyDragShadowBuilder extends View.DragShadowBuilder {

		// The drag shadow image, defined as a drawable thing
		private Drawable shadow;
		private int width, height;

		// Defines the constructor for myDragShadowBuilder
		public MyDragShadowBuilder(View v) {

			// Stores the View parameter passed to myDragShadowBuilder.
			super(v);

			// Creates a draggable image that will fill the Canvas provided by
			// the system.
			shadow = tile.tileShadow;
		}

		// Defines a callback that sends the drag shadow dimensions and touch
		// point back to the
		// system.
		@Override
		public void onProvideShadowMetrics(Point size, Point touch) {

			// Sets the width of the shadow to half the width of the original
			// View
			width = tile.tileImg.getWidth();// getView().getWidth() / 2;

			// Sets the height of the shadow to half the height of the original
			// View
			height = tile.tileImg.getHeight();// getView().getHeight() / 2;

			// The drag shadow is a ColorDrawable. This sets its dimensions to
			// be the same as the
			// Canvas that the system will provide. As a result, the drag shadow
			// will fill the
			// Canvas.
			shadow.setBounds(0, 0, width, height);

			// Sets the size parameter's width and height values. These get back
			// to the system
			// through the size parameter.
			size.set(width, height);

			// Sets the touch point's position to be in the middle of the drag
			// shadow
			touch.set(width / 2, height / 2);
		}

		// Defines a callback that draws the drag shadow in a Canvas that the
		// system constructs
		// from the dimensions passed in onProvideShadowMetrics().
		@Override
		public void onDrawShadow(Canvas canvas) {

			// Draws the ColorDrawable in the Canvas passed in from the system.
			shadow.draw(canvas);
		}
	}
}
