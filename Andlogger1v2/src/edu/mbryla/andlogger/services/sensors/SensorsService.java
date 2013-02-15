package edu.mbryla.andlogger.services.sensors;

import java.util.ArrayList;
import java.util.List;

import edu.mbryla.andlogger.database.DbAdapter;
import edu.mbryla.andlogger.settings.AppProperties;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

/** Service responsible for collecting data from services.
 * 
 * @author mbryla
 * @version 1.0
 */
public class SensorsService extends Service {
	private final static String LOG_TAG = "Sensors";	
		
	private DbAdapter dbAdapter;
	private SensorManager sManager;			
	private List<SensorInfo> sensors;	
	private SensorInfo getSensorInfoWithType(int type) {
		for(SensorInfo si : sensors)
			if(si.getType() == type)
				return si;
		return null;		
	}
	
	private Handler handler;
		
	// runnable responsible for inserting logs with appropriate sampling rate		
	private Runnable logReadings = new Runnable() {
		@Override
		public void run() {
			for(SensorInfo si : sensors) {
				if(si.isRegistered()) {
					if(si.getLatestReading() != null)
						dbAdapter.insertGenericLog(si.getDatabaseTag(), si.getLatestReading());
					else
						Log.w(LOG_TAG, "registered sensor requested null reading insertion - aborting | sensor: " + sManager.getDefaultSensor(si.getType()).getName());
				}
			}							
				
			handler.postDelayed(this, (Long)AppProperties.timingProperties.get("collect-sensors-rate-ms"));
		}
	};
	
	@Override
	public void onCreate() {		
		dbAdapter = new DbAdapter(this);
		dbAdapter.openWritable();
		
		sManager = (SensorManager)getSystemService(SENSOR_SERVICE);		
		sensors = new ArrayList<SensorInfo>();
		
		handler = new Handler();
		
		// handler posts logReadings runnable delayed in order to give sensors time to broadcast first events
		handler.postDelayed(logReadings, (Long)AppProperties.timingProperties.get("collect-sensors-rate-ms"));
		
		Log.d(LOG_TAG, "service created");
	}
	
	@Override
	public void onDestroy() {				
		handler.removeCallbacks(logReadings);				
		
		for(SensorInfo si : sensors)
			if(si.isRegistered())
				sManager.unregisterListener(sel, sManager.getDefaultSensor(si.getType()));
		
		if(dbAdapter.isOpenWritable())
			dbAdapter.close();
		
		Log.d(LOG_TAG, "service destroyed");
	}		
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if(dbAdapter.isOpenWritable()) {
			int sensor = intent.getIntExtra("sensor", 0);			
			SensorInfo si = getSensorInfoWithType(sensor);
			
			boolean create = intent.getBooleanExtra("create", false);
								
			switch(sensor) {						
			case Sensor.TYPE_ACCELEROMETER:		
				si = getSensorInfoWithType(Sensor.TYPE_ACCELEROMETER);
				
				if(create) {																										
					if(si == null) {
						sManager.registerListener(sel, sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
						sensors.add(new SensorInfo(Sensor.TYPE_ACCELEROMETER, "Accelerometer"));					
					} else if(!si.isRegistered()) {
						sManager.registerListener(sel, sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
						si.setRegistered(true);		
					}						
					Log.d(LOG_TAG, "sensor accelerometer scanning requested");
				} else {
					if(si != null) {
						sManager.unregisterListener(sel, sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
						si.setRegistered(false);
					}
				}
				
				break;
				
			case Sensor.TYPE_GYROSCOPE:				
				if(create) {																										
					if(si == null) {
						sManager.registerListener(sel, sManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_NORMAL);
						sensors.add(new SensorInfo(Sensor.TYPE_GYROSCOPE, "Gyroscope"));					
					} else if(!si.isRegistered()) {
						sManager.registerListener(sel, sManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_NORMAL);
						si.setRegistered(true);		
					}				
					Log.d(LOG_TAG, "sensor gyroscope scanning requested");
				} else {
					if(si != null) {
						sManager.unregisterListener(sel, sManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE));
						si.setRegistered(false);
					}
				}
				
				break;
			
			case Sensor.TYPE_LIGHT:		
				si = getSensorInfoWithType(Sensor.TYPE_LIGHT);
				
				if(create) {																										
					if(si == null) {
						sManager.registerListener(sel, sManager.getDefaultSensor(Sensor.TYPE_LIGHT), SensorManager.SENSOR_DELAY_NORMAL);
						sensors.add(new SensorInfo(Sensor.TYPE_LIGHT, "Light"));					
					} else if(!si.isRegistered()) {
						sManager.registerListener(sel, sManager.getDefaultSensor(Sensor.TYPE_LIGHT), SensorManager.SENSOR_DELAY_NORMAL);
						si.setRegistered(true);		
					}						
					Log.d(LOG_TAG, "sensor light scanning requested");
				} else {
					if(si != null) {
						sManager.unregisterListener(sel, sManager.getDefaultSensor(Sensor.TYPE_LIGHT));
						si.setRegistered(false);
					}
				}
				
				break;
			
			case Sensor.TYPE_PROXIMITY:		
				si = getSensorInfoWithType(Sensor.TYPE_PROXIMITY);
				
				if(create) {																										
					if(si == null) {
						sManager.registerListener(sel, sManager.getDefaultSensor(Sensor.TYPE_PROXIMITY), SensorManager.SENSOR_DELAY_NORMAL);
						sensors.add(new SensorInfo(Sensor.TYPE_PROXIMITY, "Proximity"));					
					} else if(!si.isRegistered()) {
						sManager.registerListener(sel, sManager.getDefaultSensor(Sensor.TYPE_PROXIMITY), SensorManager.SENSOR_DELAY_NORMAL);
						si.setRegistered(true);		
					}					
					Log.d(LOG_TAG, "sensor proximity scanning requested");
				} else {
					if(si != null) {
						sManager.unregisterListener(sel, sManager.getDefaultSensor(Sensor.TYPE_PROXIMITY));
						si.setRegistered(false);
					}
				}
				
				break;
				
			default:
				Log.w(LOG_TAG, "unsupported sensor scanning requested!");
				break;
			}					
		}
			
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {		
		return null;
	}
			
	private SensorEventListener sel = new SensorEventListener() {

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {			
		}

		@Override
		public void onSensorChanged(SensorEvent event) {
			int sType = event.sensor.getType();
			for(SensorInfo si : sensors) {
				if(si.getType() == sType && si.isRegistered()) {
					StringBuilder sb = new StringBuilder();					
					
					switch(sType) {
					case Sensor.TYPE_ACCELEROMETER:
						sb.append(event.values[0]).append(',').append(event.values[1]).append(',').append(event.values[2]);
						break;
					case Sensor.TYPE_GYROSCOPE:
						sb.append(event.values[0]).append(',').append(event.values[1]).append(',').append(event.values[2]);
						break;
					case Sensor.TYPE_LIGHT:
						sb.append(event.values[0]);
						break;
					case Sensor.TYPE_PROXIMITY:
						Log.d(LOG_TAG, "" + event.values[0]);
						sb.append(event.values[0]);
						break;
					}
										
					si.updateLatestReading(sb.toString());
				}
			}
									
		}
		
	};	
}