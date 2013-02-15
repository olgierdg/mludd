package edu.mbryla.andlogger.services.bluetooth;

import java.util.ArrayList;

import edu.mbryla.andlogger.database.DbAdapter;

import android.app.IntentService;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

/** Service performing a Bluetooth scan in a separate thread.
 * 
 * @author mbryla
 * @version 1.0
 */
public class BtService extends IntentService {
	private final static String LOG_TAG = "Bluetooth";
	private final static String DATABASE_TAG = "Bluetooth";	
	
	public BtService() {
		super("Bluetooth Collector");
	}	
	
	private DbAdapter dbAdapter;
	private BluetoothAdapter btAdapter;			

	/** Upon getting a writable-db access and enabled BT adapter BtService registers
	 *  broadcast receiver in order to listen to BT discovery actions. <code>doneScanning</code>
	 *  flag forces the service to wait until the <code>ACTION_DISCOVERY_FINISHED</code> is
	 *  received by the broadcast receiver.	 
	 */
	private boolean doneScanning = true;	
	
	/** Informs the <code>onDestroy()</code> function about the necessity to unregister 
	 *  the broadcast receiver.	 
	 */
	private boolean receiverRegistered = false;
	
	@Override
	protected void onHandleIntent(Intent intent) {		
		Log.d(LOG_TAG, "scan requested");
		dbAdapter = new DbAdapter(this);		
		
		dbAdapter.openWritable();
		if(dbAdapter != null) {
			btAdapter = BluetoothAdapter.getDefaultAdapter();
				
			if(btAdapter.isEnabled()) {
				doneScanning = false;	
				
				IntentFilter filter = new IntentFilter();
				filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
				filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
				filter.addAction(BluetoothDevice.ACTION_FOUND);
										
				receiverRegistered = true;
				registerReceiver(mReceiver, filter);			
				btAdapter.startDiscovery();										
			} else {
				Log.e(LOG_TAG, "bluetooth disabled at scan request!");
				dbAdapter.insertGenericLog(DATABASE_TAG, "ERR :: bluetooth disabled!");
			}
		} else
			Log.e(LOG_TAG, "can't get writable database!");		
		
		while(!doneScanning);
	}
		
	@Override
	public void onCreate() {		
		super.onCreate();
		Log.d(LOG_TAG, "service created");		
	}
	
	@Override
	public void onDestroy() {	
		if(receiverRegistered)
			unregisterReceiver(mReceiver);
		
		if(dbAdapter.isOpenWritable())
			dbAdapter.close();
		
		Log.d(LOG_TAG, "service destroyed");
		super.onDestroy();
	}
		
	private ArrayList<BluetoothDevice> activeDevices;
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {				
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();						
			
			if(action.equals(BluetoothDevice.ACTION_FOUND)) {
				Log.d(LOG_TAG, "device found");
				
				if(activeDevices != null) {
					BluetoothClass btClass = intent.getParcelableExtra(BluetoothDevice.EXTRA_CLASS);
					Log.d(LOG_TAG, btClass.toString());
					
					BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);					
					if(!activeDevices.contains(device))
						activeDevices.add(device);					
				}
			} else if(action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {				
				Log.d(LOG_TAG, "scan finished");
				
				if(activeDevices.size() == 0)
					dbAdapter.insertGenericLog(DATABASE_TAG, "no bluetooth devices found");
				else {										
					StringBuilder sb = new StringBuilder();
					sb.append(activeDevices.size()).append(" device(s) found");
					
					for(BluetoothDevice device : activeDevices)
						sb.append(" | ").append(device.getName()).append(',').append(device.getAddress());
					
					dbAdapter.insertGenericLog(DATABASE_TAG, sb.toString());
				}								
				doneScanning = true;				
			} else if(action.equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)) {
				Log.d(LOG_TAG, "scan started");				
				activeDevices = new ArrayList<BluetoothDevice>();				
			}
		}
	};
}
