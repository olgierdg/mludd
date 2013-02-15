package edu.mbryla.andlogger.preview;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ListView;
import edu.mbryla.andlogger.R;
import edu.mbryla.andlogger.database.DbAdapter;
import edu.mbryla.andlogger.database.models.GenericLog;
import edu.mbryla.andlogger.settings.AppProperties;

/** Activity previewing the latest logs
 * 
 * @author mbryla
 * @version 1.0
 */
public class PreviewActivity extends Activity {
	private final static String DEBUG_TAG = "Preview";			
	
	private ListView lvPreview;	
	private DbAdapter dbAdapter;
	private Handler handler;
	
	private Cursor logs;	
	private List<GenericLog> currentlyDisplayed;
	private GenericLogArrayAdapter currentlyDisplayedAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.preview);
		
		Log.d(DEBUG_TAG, "PV :: starting preview");
		
		lvPreview = (ListView) findViewById(R.id.lvPreview);		
		dbAdapter = new DbAdapter(getApplicationContext());
		dbAdapter.openReadable();		
		handler = new Handler();
						
		currentlyDisplayed = new ArrayList<GenericLog>();
		currentlyDisplayedAdapter = new GenericLogArrayAdapter(this, currentlyDisplayed);
		lvPreview.setAdapter(currentlyDisplayedAdapter);
		
		updateLogs();
		updateCurrentlyDisplayed();
		
		handler.postDelayed(updateTask, (Long)AppProperties.previewProperties.get("preview-rate-ms"));
	}
	
	private Runnable updateTask = new Runnable() {
		@Override
		public void run() {						
			Log.d(DEBUG_TAG, "TIM :: updating log list");
			updateLogs();
			updateCurrentlyDisplayed();
			
			handler.postDelayed(this, (Long)AppProperties.previewProperties.get("preview-rate-ms"));
		}
	};
	
	@Override
	protected void onDestroy() {
		Log.d(DEBUG_TAG, "PV :: ending preview");
		
		handler.removeCallbacks(updateTask);
    	if(dbAdapter != null)
    		dbAdapter.close();                
    	
    	super.onDestroy();
	}
	
	
	@SuppressWarnings("deprecation")
	private void updateLogs() {
		logs = dbAdapter.getLastGenericLogs((Integer)AppProperties.previewProperties.get("preview-logs-limit"));
		if(logs != null)
			startManagingCursor(logs);
	}
	
	private void updateCurrentlyDisplayed() {
		if(logs != null && logs.moveToLast()) {
			do {
				long id = logs.getLong(DbAdapter.COL_NUM_ID);
				long lastId = currentlyDisplayed.size() > 0 ? currentlyDisplayed.get(0).getId() : -1;
						
				if(id > lastId) {
					String timestamp = logs.getString(DbAdapter.COL_NUM_TIMESTAMP);
					String tag = logs.getString(DbAdapter.COL_NUM_TAG);
					String data = logs.getString(DbAdapter.COL_NUM_DATA);
				
					if(currentlyDisplayed.size() == (Integer)AppProperties.previewProperties.get("preview-logs-limit"))
						currentlyDisplayed.remove(currentlyDisplayed.size() - 1);
				
					currentlyDisplayed.add(0, new GenericLog(id, timestamp, tag, data));
				}
			} while(logs.moveToPrevious());
			currentlyDisplayedAdapter.notifyDataSetChanged();
		}
	}
}
