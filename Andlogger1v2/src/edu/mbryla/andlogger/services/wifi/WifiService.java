package edu.mbryla.andlogger.services.wifi;

import java.util.List;

import edu.mbryla.andlogger.database.DbAdapter;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

/** Service performing a Wifi scan in a separate thread.
 * 
 * @author mbryla
 * @version 1.0
 */
public class WifiService extends IntentService {
	private final static String LOG_TAG = "Wifi";
	private final static String DATABASE_TAG = "Wifi";
	
	public WifiService() {
		super("Wifi Collector");
	}
	
	private DbAdapter dbAdapter;
	private WifiManager wManager;
	
	/** Upon getting a writable-db access and enabled Wifi Manager WifiService registers
	 *  broadcast receiver in order to listen to Wifi scan end action. <code>doneScanning</code>
	 *  flag forces the service to wait until the <code>SCAN_RESULTS_AVAILABLE_ACTION</code> is
	 *  received by the broadcast receiver.	 
	 */
	private boolean doneScanning = true;
	
	/** Informs the <code>onDestroy()</code> function about the necessity to unregister 
	 *  the broadcast receiver.	 
	 */
	private boolean receiverRegistered;
	
	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d(LOG_TAG, "scan requested");
		dbAdapter = new DbAdapter(this);
		
		dbAdapter.openWritable();
		if(dbAdapter != null) {
			wManager = (WifiManager)getSystemService(WIFI_SERVICE);									
			
			if(wManager.isWifiEnabled()) {
				doneScanning = false;
				
				IntentFilter filter = new IntentFilter();
				filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
				
				receiverRegistered = true;
				registerReceiver(mReceiver, filter);						
				wManager.startScan();				
				
				Log.d(LOG_TAG, "scan started");
			} else {
				Log.e(LOG_TAG, "wifi disabled at scan request!");
				dbAdapter.insertGenericLog(DATABASE_TAG, "ERR :: wifi disabled!");
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
	
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			
			if(action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
				Log.d(LOG_TAG, "scan finished");
				
				List<ScanResult> scanResults = wManager.getScanResults();
				
				if(scanResults.size() == 0)
					dbAdapter.insertGenericLog(DATABASE_TAG, "no wifi devices found");
				else {
					StringBuilder sb = new StringBuilder();
					sb.append(scanResults.size()).append(" device(s) found");
				
					for(ScanResult sr : scanResults)
						sb.append(" | ").append(sr.SSID).append(',').append(sr.BSSID).append(',').append(sr.frequency).append(',').append(sr.level);
			
					dbAdapter.insertGenericLog(DATABASE_TAG, sb.toString());
				}
				doneScanning = true;
			}						
		}
	};
}
