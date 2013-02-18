package edu.mbryla.andlogger.services.sensors;

import java.sql.Timestamp;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import edu.mbryla.andlogger.database.dao.AccelerationLogDAO;
import edu.mbryla.andlogger.database.models.AccelerationLog;
import edu.mbryla.andlogger.settings.AppProperties;


public class AccelerationService extends Service implements SensorEventListener {
    private final static String LOG_TAG = "Sensors";

    private SensorManager manager;
    private AccelerationLogDAO dao;
    private Handler handler = new Handler();

    private boolean newLog = false;
    private AccelerationLog log = null;

    private Runnable logger = new Runnable() {

        @Override
        public void run() {
            if (log != null && newLog) {
                Log.d(LOG_TAG, "Acceleration service log saved to the database.");
                dao.save(log);
                newLog = false;
            }
            handler.postDelayed(logger, (Long) AppProperties.timingProperties
                    .get("collect-sensors-rate-ms"));
        }
    };

    @Override
    public void onCreate() {
        Log.d(LOG_TAG, "Acceleration sensor service created.");

        dao = new AccelerationLogDAO(this);
        manager = (SensorManager) getSystemService(SENSOR_SERVICE);
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "Acceleration sensor service destroyed.");

        handler.removeCallbacks(logger);
        manager.unregisterListener(this,
                manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "Acceleration sensor service started.");

        manager.registerListener(this,
                manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        handler.postDelayed(logger, (Long) AppProperties.timingProperties
                .get("collect-sensors-rate-ms"));

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            log = new AccelerationLog();
            log.setTimestamp(new Timestamp(System.currentTimeMillis()));
            log.setX(event.values[0]);
            log.setY(event.values[1]);
            log.setZ(event.values[2]);
        }
        newLog = true;
    }

}
