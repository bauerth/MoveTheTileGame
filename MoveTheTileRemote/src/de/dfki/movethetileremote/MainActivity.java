package de.dfki.movethetileremote;

import de.dfki.movethetileremote.communication.ClientCommunicator;
import de.dfki.movethetileremote.gui.GameActivity;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.text.format.Formatter;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {

	/**
	 * Edit text in the main activity
	 */
	private EditText editTextIP;
	
	/**
	 * new game button that is at first invisible.
	 */
	private Button newGameBtn;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		this.editTextIP = (EditText) findViewById(R.id.HostIP);
		this.newGameBtn = (Button) findViewById(R.id.newGameButton);

		Display display = getWindowManager().getDefaultDisplay();
		Constants.SCREEN_H = display.getHeight();
		Constants.SCREEN_W = display.getWidth();

		WifiManager wim = (WifiManager) getSystemService(WIFI_SERVICE);
//		List<WifiConfiguration> l = wim.getConfiguredNetworks();
		Constants.OWN_IP = Formatter.formatIpAddress(wim.getConnectionInfo()
				.getIpAddress());
	}

	/**
	 * Starts the game by opening the game activity
	 * @param sfNormal
	 */
	public void onClickStart(View sfNormal) {
		Constants.HOST_IP = editTextIP.getText().toString();
		System.out.println("Host IP address: " + Constants.HOST_IP);
		Intent i = new Intent(MainActivity.this, GameActivity.class);
		startActivityForResult(i, Constants.Game_Done);
	}

//	@Override
//	public void onBackPressed() {
//		// close the connection when finishing the application
//		ClientCommunicator.getInstance(this, null).disconnect();
//
//		super.onBackPressed();
//	}

//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		// Making the new game button visible when the game activity comes back
//		if (requestCode == Constants.Game_Done) {
//			newGameBtn.setVisibility(View.VISIBLE);
//		}
//
//		super.onActivityResult(requestCode, resultCode, data);
//	}

	public void onClickNewGame(View sfNormal) {
		//Send a new game message to the server
//		ClientCommunicator.getInstance(this, null).sendNewGameMessage();
	}
}
