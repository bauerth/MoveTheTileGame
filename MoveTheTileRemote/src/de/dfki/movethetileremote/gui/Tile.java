package de.dfki.movethetileremote.gui;

import java.io.File;
import java.io.IOException;

import de.dfki.movethetileremote.Constants;
import de.dfki.movethetileremote.MoveDirection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class Tile {

	public int id;

	public MoveDirection moveDir;

	public Point posOnBoard;

	public String name;

	public Bitmap tileImg;
	public Drawable tileShadow;

	public String type = "";

	public Tile(Point pos, String name, int id, MoveDirection dir, String type) {
		this.name = name;

		posOnBoard = pos;

		this.id = id;

		moveDir = dir;

		this.type = type;

//		try {
//			if (Constants.ChosenOps == 0) {
//				tileImg = BitmapFactory.decodeStream(Constants.assetManager
//						.open("Portrait" + File.separator + "portrait_" + id
//								+ ".gif"));
//				tileShadow = Drawable.createFromStream(
//						Constants.assetManager.open("Portrait" + File.separator
//								+ "portrait_" + id + ".gif"), "portrait_" + id
//								+ ".gif");
//			} else {
//				tileImg = BitmapFactory.decodeStream(Constants.assetManager
//						.open("Circle" + File.separator + "circle_" + id
//								+ ".gif"));
//				tileShadow = Drawable.createFromStream(
//						Constants.assetManager.open("Circle" + File.separator
//								+ "circle_" + id + ".gif"), "circle_" + id
//								+ ".gif");
//
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}
	
	public Tile(Point pos, String name, int id, MoveDirection dir, String type, Bitmap bmp) {
		this.name = name;

		posOnBoard = pos;

		this.id = id;

		moveDir = dir;

		this.type = type;

		tileImg = Bitmap.createBitmap(bmp);
		tileShadow = new BitmapDrawable(bmp);
	}

	@Override
	public String toString() {
		return id + ";" + name + ";" + posOnBoard.x + ";" + posOnBoard.y/*
																		 * + ";"
																		 * +
																		 * moveDir
																		 * .
																		 * toString
																		 * ()
																		 */
				+ ";" + type;
	}

	public void setImage(Bitmap bitmap) {
		tileImg = Bitmap.createBitmap(bitmap);
		tileShadow = new BitmapDrawable(bitmap);
	}

}
