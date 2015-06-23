package com.stoianovici.horatiu.kontrol.activities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Paint;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.stoianovici.horatiu.kontrol.AdapterServerList;
import com.stoianovici.horatiu.kontrol.MyFonts;
import com.stoianovici.horatiu.kontrol.R;
import com.stoianovici.horatiu.kontrol.database.SavedServers;
import com.stoianovici.horatiu.kontrol.tcpcommunications.NetworkDevice;
import com.stoianovici.horatiu.kontrol.tcpcommunications.TCPSecurity;
import com.stoianovici.horatiu.kontrol.tcpcommunications.ServerInfo;
import com.stoianovici.horatiu.kontrol.utils.BluetoothService;
import com.stoianovici.horatiu.kontrol.utils.Constants;
import com.todddavies.components.progressbar.ProgressWheel;

public class ActivityConnectToServer extends Activity {
	private AdapterServerList listAdapter;
	private ListView listView;
	private TextView availableServersTextView;
	//	private RelativeLayout loadingPanel;
	private ProgressWheel progressWheel;
	private boolean isSpinning;
	private Context context;
	private HashSet<String> knownServers;
	private SavedServers dbHandler;
	private BluetoothAdapter mBtAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_connect_to_server);

		context = this;

		MyFonts.Initialize(context);
		knownServers = new HashSet<String>();
		listAdapter = new AdapterServerList(this);
		listView = (ListView)findViewById(R.id.listViewServers);
		listView.setAdapter(listAdapter);

		listView.setOnItemClickListener(new OnItemClickListener() {
			/**
			 * When clicking on an item from the list of servers, a dialog appears for connecting to that server
			 */
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
									long id) {
				//try to connect to the selected server
				final ServerInfo server = listAdapter.getItem(position);
				tryConnectingToServer(server);
			}
		});

		availableServersTextView = (TextView)findViewById(R.id.textViewAvailableServers);
		availableServersTextView.setTypeface(MyFonts.normalFont);
		availableServersTextView.setPaintFlags(availableServersTextView.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);

		progressWheel = (ProgressWheel) findViewById(R.id.pw_spinner);
		progressWheel.setSpinSpeed(9);

		progressWheel.setOnClickListener(new OnClickListener() {
			/**
			 * Refresh the list of servers when clicking on the spinner
			 */
			@Override
			public void onClick(View v) {
				if(!isSpinning){
					startSearchingForServers(context);
				}
			}
		});

		dbHandler = new SavedServers(this);

		// Register for broadcasts when a device is discovered
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		this.registerReceiver(mReceiver, filter);

		// Register for broadcasts when discovery has finished
		filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		this.registerReceiver(mReceiver, filter);

		// Get the local Bluetooth adapter
		mBtAdapter = BluetoothAdapter.getDefaultAdapter();

		startSearchingForServers(context);
	}

	InetAddress getBroadcastAddress() throws IOException {
		WifiManager wifi = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
		DhcpInfo dhcp = wifi.getDhcpInfo();
		// handle null somehow

		int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
		byte[] quads = new byte[4];
		for (int k = 0; k < 4; k++)
			quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
		return InetAddress.getByAddress(quads);
	}

    /**
     * Establish connection with other divice
     *
     * @param data   An {@link Intent} with {@link DeviceListActivity#EXTRA_DEVICE_ADDRESS} extra.
     * @param secure Socket Security type - Secure (true) , Insecure (false)
     */
    private void connectBluetoothDevice(ServerInfo server) {
        // Get the device MAC address
        String address = server.getAddressString();
        // Get the BluetoothDevice object
        BluetoothDevice device = mBtAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        BluetoothService.getInstance(this, mHandler).connect(device, true);
    }

	/**
	 * Scans for servers in the background and updates the listViewAdapter whenever it finds a new server
	 * @param context
	 */
	private void startSearchingForServers(final Context context){
		if(mBtAdapter != null) {
			// If we're already discovering, stop it
			if (mBtAdapter.isDiscovering()) {
				mBtAdapter.cancelDiscovery();
			}

			// Request discover from BluetoothAdapter
			mBtAdapter.startDiscovery();
//
//            // Get a set of currently paired devices
//            Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
//
//            // If there are paired devices, add each one to the ArrayAdapter
//            if (pairedDevices.size() > 0) {
//                for (BluetoothDevice device : pairedDevices) {
//                    ServerInfo server = new ServerInfo(device.getName(), NetworkDevice.Bluetooth, device.getAddress());
//                    listAdapter.add(server);
//                    listAdapter.notifyDataSetChanged();
//                }
//            }
		}



		new AsyncTask<Void, ServerInfo, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				knownServers.clear();

				//first send a broadcast message to alert all servers that you are looking for them
				WifiManager wifiMgr = (WifiManager) getSystemService(WIFI_SERVICE);
				WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
				int ip = wifiInfo.getIpAddress();
				String ipAddress = Formatter.formatIpAddress(ip);
				try {
					DatagramSocket s = new DatagramSocket(4673);
					s.setBroadcast(true);

					String message = ipAddress;

					DatagramPacket packet = new DatagramPacket(message.getBytes(), message.length(), InetAddress.getByName("255.255.255.255"), 4673);
					s.send(packet);
					s.close();
				}
				catch(Exception e){
					e.printStackTrace();
				}

				//after this the servers will wait for 1-2 seconds and send back their details to you
				ServerSocket welcomeSocket = null;
				try {
					welcomeSocket = new ServerSocket(4673);
					welcomeSocket.setSoTimeout(10000);
					while (true) {
						Socket connectionSocket = welcomeSocket.accept();
						BufferedReader inFromClient =
								new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
						String receivedData = inFromClient.readLine();
						ServerInfo info = new ServerInfo(receivedData);
						info.setAddressString(connectionSocket.getInetAddress().getHostAddress());

						if(!knownServers.contains(info.getId())){
							publishProgress(info);
							knownServers.add(info.getId());
						}
						connectionSocket.close();
					}
				}
				catch(Exception e){
					e.printStackTrace();
					if(welcomeSocket != null){
						try {
							welcomeSocket.close();
						}
						catch(Exception ex){
							//nothing
						}
					}
				}
				return null;
			}

			@Override
			protected  void onProgressUpdate(ServerInfo... values) {
				listAdapter.add(values[0]);
				listAdapter.notifyDataSetChanged();
				isSpinning = true;
			};

			@Override
			protected void onPostExecute(Void result) {
				//loadingPanel.setVisibility(View.GONE);
				progressWheel.stopSpinning();
				isSpinning = false;
			};

			@Override
			protected void onPreExecute() {
				//loadingPanel.setVisibility(View.VISIBLE);
				progressWheel.spin();
				isSpinning = true;
				listAdapter.clear();
				listAdapter.notifyDataSetChanged();
			};
		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

    private ServerInfo serverToConnectTo;
	/**
	 * Tries to connect to the server with the specified password, updating the listView as well and opening a new activity if successful
	 * @param server
	 * @param password
	 */
	private void tryConnectingToServer(final ServerInfo server) {
        // Cancel discovery because it's costly and we're about to connect
        if(mBtAdapter != null) {
            mBtAdapter.cancelDiscovery();
        }

        if(server.getDevice() == NetworkDevice.Bluetooth){
            serverToConnectTo = server;
            BluetoothService.getInstance(this, mHandler).connect(mBtAdapter.getRemoteDevice(server.getAddressString()), true);
            return;
        }

		new AsyncTask<Void, Void, Void>(){

			@Override
			protected void onPreExecute() {
				server.setIsConnecting(true);
				listAdapter.notifyDataSetChanged();
			};

			@Override
			protected Void doInBackground(Void... params) {
				try{
					if(TCPSecurity.AttemptLogin(server, context)){
						server.setConnectedMessage("Connected");

						//opening main
						Intent i = new Intent(context, ActivityMain.class);
						startActivity(i);
					}
					else{
						server.setConnectedMessage("Not authorized");
						dbHandler.deleteServer(server.getId());
					}
				}
				catch(Exception e){
					server.setConnectedMessage("Could not connect");
					Log.e("Kontrol", e.getMessage());
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				server.setIsConnecting(false);
				listAdapter.notifyDataSetChanged();
			};
		}.execute();
	}

	/**
	 * The BroadcastReceiver that listens for discovered devices and changes the title when
	 * discovery is finished
	 */
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			// When discovery finds a device
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				// Get the BluetoothDevice object from the Intent
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				// If it's already paired, skip it, because it's been listed already
				if (true){ //device.getBondState() != BluetoothDevice.BOND_BONDED) {
					ServerInfo server = new ServerInfo(device.getName(), NetworkDevice.Bluetooth, device.getAddress());
					listAdapter.add(server);
					listAdapter.notifyDataSetChanged();
				}
				// When discovery is finished, change the Activity title
			} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
				//TODO: stop spinning icon
			}
		}
	};

    /**
     * The Handler that gets information back from the BluetoothService
     */
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:
                            serverToConnectTo.setIsConnecting(false);
                            serverToConnectTo.setConnectedMessage("Connected");
                            BluetoothService.IS_ACTIVE = true;
                            listAdapter.notifyDataSetChanged();
                            //opening main
                            Intent i = new Intent(context, ActivityMain.class);
                            startActivity(i);
                            break;
                        case BluetoothService.STATE_CONNECTING:
                            serverToConnectTo.setIsConnecting(true);
                            listAdapter.notifyDataSetChanged();
                            break;
                        case BluetoothService.STATE_LISTEN:
                        case BluetoothService.STATE_NONE:
                            serverToConnectTo.setIsConnecting(false);
                            serverToConnectTo.setConnectedMessage("Connection failed");
                            listAdapter.notifyDataSetChanged();
                            break;
                    }
                    break;
            }
        }
    };

}
