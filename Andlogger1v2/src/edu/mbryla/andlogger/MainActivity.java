package edu.mbryla.andlogger;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import edu.mbryla.andlogger.preview.PreviewActivity;
import edu.mbryla.andlogger.services.bluetooth.BtService;
import edu.mbryla.andlogger.services.location.LocationService;
import edu.mbryla.andlogger.services.sensors.AccelerationService;
import edu.mbryla.andlogger.services.sensors.SensorsService;
import edu.mbryla.andlogger.services.wifi.WifiService;
import edu.mbryla.andlogger.settings.AppProperties;

/** Supplies the application with UI, handles the direct user requests, prompts user for appropriate permissions.
 * @author mbryla
 * @version 1.0
 */
public class MainActivity extends Activity {
    private final static String LOG_TAG = "Main";
    private SharedPreferences appStatePrefs;

    private BluetoothAdapter btAdapter;
    private Button collectorButtonBt;

    private SensorManager sManager;
    private List<Integer> registeredSensors;
    private Button collectorButtonAcc;
    private Button collectorButtonGyr;
    private Button collectorButtonLight;
    private Button collectorButtonProx;

    private WifiManager wManager;
    private Button collectorButtonWifi;

    private LocationManager lManager;
    private Button collectorButtonGPS;

    private Button debugResetButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Log.d(LOG_TAG, "onCreate()");

        mHandler = new Handler();
        appStatePrefs = getSharedPreferences("appstate", MODE_PRIVATE);

        wManager = (WifiManager)getSystemService(WIFI_SERVICE);
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        sManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        lManager = (LocationManager)getSystemService(LOCATION_SERVICE);

        registeredSensors = new ArrayList<Integer>();

        initializeButtons();
        loadAppState();
        validateButtons();
    }

    private void initializeButtons() {
        debugResetButton = (Button) findViewById(R.id.debug_reset_button);
        debugResetButton.setOnClickListener(buttonsListener);

        collectorButtonWifi = (Button) findViewById(R.id.collector_button_wifi);
        collectorButtonWifi.setOnClickListener(buttonsListener);

        collectorButtonBt = (Button) findViewById(R.id.collector_button_bt);
        collectorButtonBt.setOnClickListener(buttonsListener);

        collectorButtonGPS = (Button) findViewById(R.id.collector_button_gps);
        collectorButtonGPS.setOnClickListener(buttonsListener);

        collectorButtonAcc = (Button) findViewById(R.id.collector_button_acc);
        collectorButtonAcc.setOnClickListener(buttonsListener);

        collectorButtonGyr = (Button) findViewById(R.id.collector_button_gyr);
        collectorButtonGyr.setOnClickListener(buttonsListener);

        collectorButtonLight = (Button) findViewById(R.id.collector_button_light);
        collectorButtonLight.setOnClickListener(buttonsListener);

        collectorButtonProx = (Button) findViewById(R.id.collector_button_prox);
        collectorButtonProx.setOnClickListener(buttonsListener);
    }

    /** Function loading app state from <code>SharedPreferences</code>
     *
     * @version 1.0
     */
    private void loadAppState() {
        if(appStatePrefs.getBoolean("collector-wifi-on", false))
            setCollectorButtonState(collectorButtonWifi, CollectorButtonState.OFF);
        else
            setCollectorButtonState(collectorButtonWifi, CollectorButtonState.ON);

        if(appStatePrefs.getBoolean("collector-bt-on", false))
            setCollectorButtonState(collectorButtonBt, CollectorButtonState.OFF);
        else
            setCollectorButtonState(collectorButtonBt, CollectorButtonState.ON);

        if(appStatePrefs.getBoolean("collector-gps-on", false))
            setCollectorButtonState(collectorButtonGPS, CollectorButtonState.OFF);
        else
            setCollectorButtonState(collectorButtonGPS, CollectorButtonState.ON);

        if(appStatePrefs.getBoolean("collector-acc-on", false)) {
            setCollectorButtonState(collectorButtonAcc, CollectorButtonState.OFF);
            registeredSensors.add(Sensor.TYPE_ACCELEROMETER);
        } else
            setCollectorButtonState(collectorButtonAcc, CollectorButtonState.ON);

        if(appStatePrefs.getBoolean("collector-gyr-on", false)) {
            setCollectorButtonState(collectorButtonGyr, CollectorButtonState.OFF);
            registeredSensors.add(Sensor.TYPE_GYROSCOPE);
        } else
            setCollectorButtonState(collectorButtonGyr, CollectorButtonState.ON);

        if(appStatePrefs.getBoolean("collector-light-on", false)) {
            setCollectorButtonState(collectorButtonLight, CollectorButtonState.OFF);
            registeredSensors.add(Sensor.TYPE_LIGHT);
        } else
            setCollectorButtonState(collectorButtonLight, CollectorButtonState.ON);

        if(appStatePrefs.getBoolean("collector-prox-on", false)) {
            setCollectorButtonState(collectorButtonProx, CollectorButtonState.OFF);
            registeredSensors.add(Sensor.TYPE_PROXIMITY);
        } else
            setCollectorButtonState(collectorButtonProx, CollectorButtonState.ON);
    }

    /** Function disabling buttons starting unsupported collectors.
     *
     * @version 1.0
     */
    private void validateButtons() {
        if(wManager == null)
            setCollectorButtonState(collectorButtonWifi, CollectorButtonState.DISABLED);

        if(btAdapter == null)
            setCollectorButtonState(collectorButtonBt, CollectorButtonState.DISABLED);

        if(lManager == null)
            setCollectorButtonState(collectorButtonGPS, CollectorButtonState.OFF);

        List<Sensor> sensors = sManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if(sensors.size() == 0)
            setCollectorButtonState(collectorButtonAcc, CollectorButtonState.DISABLED);

        sensors = sManager.getSensorList(Sensor.TYPE_GYROSCOPE);
        if(sensors.size() == 0)
            setCollectorButtonState(collectorButtonGyr, CollectorButtonState.DISABLED);

        sensors = sManager.getSensorList(Sensor.TYPE_LIGHT);
        if(sensors.size() == 0)
            setCollectorButtonState(collectorButtonLight, CollectorButtonState.DISABLED);

        sensors = sManager.getSensorList(Sensor.TYPE_PROXIMITY);
        if(sensors.size() == 0)
            setCollectorButtonState(collectorButtonProx, CollectorButtonState.DISABLED);
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "onDestroy()");
        super.onDestroy();
    }

    private OnClickListener buttonsListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()) {
            case R.id.debug_reset_button: // ================================================================| DEBUG_RESET
                changeAppState("collector-bt-on", false);
                changeAppState("collector-wifi-on", false);
                changeAppState("collector-gps-on", false);
                changeAppState("collector-acc-on", false);
                changeAppState("collector-gyr-on", false);
                changeAppState("collector-light-on", false);
                changeAppState("collector-prox-on", false);

                loadAppState();
                validateButtons();

                break;
            case R.id.collector_button_wifi: // ================================================================| WIFI

                if(((Button)v).getText().equals(getString(R.string.collector_on))) {
                    if(wManager != null) {
                        if(!wManager.isWifiEnabled()) {
                            Toast.makeText(v.getContext(), "Enabling WiFi...", Toast.LENGTH_SHORT).show();

                            IntentFilter wifiStateChanged = new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION);

                            broadcastReceiver.resetWifiState();
                            registerReceiver(broadcastReceiver, wifiStateChanged);

                            wManager.setWifiEnabled(true);
                        } else
                            startCollectorWifi();

                    } else
                        Toast.makeText(v.getContext(), "Your device does not support wifi!", Toast.LENGTH_SHORT).show();

                } else {
                    stopCollectorWifi();

                    if(wManager.isWifiEnabled()) {
                        Toast.makeText(v.getContext(), "Disabling WiFi...", Toast.LENGTH_SHORT).show();
                        wManager.setWifiEnabled(false);
                    }
                }

                break;

            case R.id.collector_button_bt: // ================================================================| BLUETOOTH

                if(((Button)v).getText().equals(getString(R.string.collector_on))) {
                    if(btAdapter != null) {
                        if(!btAdapter.isEnabled()) {
                            Intent btEnable = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            startActivityForResult(btEnable, REQUEST_ENABLE_BT);
                        } else
                            startCollectorBluetooth();
                    } else
                        Toast.makeText(v.getContext(), "Your device does not support bluetooth!", Toast.LENGTH_SHORT).show();

                } else {
                    stopCollectorBluetooth();

                    if(btAdapter.isEnabled())
                        btAdapter.disable();
                }

                break;

            case R.id.collector_button_gps: // ================================================================| GPS
                if(((Button)v).getText().equals(getString(R.string.collector_on))) {
                    boolean networkProviderEnabled = lManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                    boolean gpsProviderEnabled = lManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

                    if(!(networkProviderEnabled || gpsProviderEnabled)) {
                        Log.w(LOG_TAG, "no location providers available");
                    } else {
                        String disabledProvider = "";
                        if(!networkProviderEnabled) {
                            Log.w(LOG_TAG, "network provider disabled - location accuracy may be affected");
                            disabledProvider = "network";
                        } else if(!gpsProviderEnabled) {
                            Log.w(LOG_TAG, "gps provider disabled - location accuracy may be affected");
                            disabledProvider = "gps";
                        }

                        if(disabledProvider != "") {
                            AlertDialog.Builder ad = new AlertDialog.Builder(v.getContext());
                            ad.setTitle("Location Inaccuracy");
                            ad.setMessage("Disabled provider " + disabledProvider + " may result in location inaccuracy. Do you want to adjust the provider settings?");
                            final Context tmpContext = v.getContext();
                            ad.setPositiveButton("Settings", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    tmpContext.startActivity(intent);
                                }
                            });
                            ad.setNegativeButton("Cancel",  new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

                            ad.show();
                        }

                        startCollectorGPS();
                    }
                } else
                    stopCollectorGPS();

                break;

            case R.id.collector_button_acc: // ================================================================| ACCELEROMETER
                if(((Button)v).getText().equals(getString(R.string.collector_on))) {
//                    startCollectorSensor(Sensor.TYPE_ACCELEROMETER, (Button)v);
                    startAcceleratorService((Button) v);
                    changeAppState("collector-acc-on", true);
                } else {
//                    stopCollectorSensor(Sensor.TYPE_ACCELEROMETER, (Button)v);
                    stopAcceleratorService((Button) v);
                    changeAppState("collector-acc-on", false);
                }
                break;

            case R.id.collector_button_gyr: // ================================================================| GYROSCOPE
                if(((Button)v).getText().equals(getString(R.string.collector_on))) {
                    startCollectorSensor(Sensor.TYPE_GYROSCOPE, (Button)v);
                    changeAppState("collector-gyr-on", true);
                } else {
                    stopCollectorSensor(Sensor.TYPE_GYROSCOPE, (Button)v);
                    changeAppState("collector-gyr-on", false);
                }
                break;

            case R.id.collector_button_light: // ================================================================| LIGHT
                if(((Button)v).getText().equals(getString(R.string.collector_on))) {
                    startCollectorSensor(Sensor.TYPE_LIGHT, (Button)v);
                    changeAppState("collector-light-on", true);
                } else {
                    stopCollectorSensor(Sensor.TYPE_LIGHT, (Button)v);
                    changeAppState("collector-light-on", false);
                }
                break;

            case R.id.collector_button_prox: // ================================================================| PROXIMITY
                if(((Button)v).getText().equals(getString(R.string.collector_on))) {
                    startCollectorSensor(Sensor.TYPE_PROXIMITY, (Button)v);
                    changeAppState("collector-prox-on", true);
                } else {
                    stopCollectorSensor(Sensor.TYPE_PROXIMITY, (Button)v);
                    changeAppState("collector-prox-on", false);
                }
                break;
            }
        }
    };

    private Handler mHandler;

    private void startCollectorBluetooth() {
        setCollectorButtonState(collectorButtonBt, CollectorButtonState.OFF);
        changeAppState("collector-bt-on", true);

        mHandler.post(startServiceBt);
    }
    private void stopCollectorBluetooth() {
        setCollectorButtonState(collectorButtonBt, CollectorButtonState.ON);
        changeAppState("collector-bt-on", false);

        mHandler.removeCallbacks(startServiceBt);

        Intent intent = new Intent(this, BtService.class);
        stopService(intent);
    }
    private Runnable startServiceBt = new Runnable() {
        @Override
        public void run() {
            Intent intent = new Intent(getApplicationContext(), BtService.class);
            startService(intent);

            mHandler.postDelayed(startServiceBt, (Long)AppProperties.timingProperties.get("collect-bt-rate-ms"));
        }
    };

    private void startCollectorWifi() {
        setCollectorButtonState(collectorButtonWifi, CollectorButtonState.OFF);
        changeAppState("collector-wifi-on", true);

        mHandler.post(startServiceWifi);
    }
    private void stopCollectorWifi() {
        setCollectorButtonState(collectorButtonWifi, CollectorButtonState.ON);
        changeAppState("collector-wifi-on", false);

        mHandler.removeCallbacks(startServiceWifi);

        Intent intent = new Intent(this, WifiService.class);
        stopService(intent);
    }
    private Runnable startServiceWifi = new Runnable() {
        @Override
        public void run() {
            Intent intent = new Intent(getApplicationContext(), WifiService.class);
            startService(intent);

            mHandler.postDelayed(startServiceWifi, (Long)AppProperties.timingProperties.get("collect-bt-rate-ms"));
        }
    };

    private void startCollectorGPS() {
        setCollectorButtonState(collectorButtonGPS, CollectorButtonState.OFF);
        changeAppState("collector-gps-on", true);

        mHandler.post(startServiceGPS);
    }
    private void stopCollectorGPS() {
        setCollectorButtonState(collectorButtonGPS, CollectorButtonState.ON);
        changeAppState("collector-gps-on", false);

        mHandler.removeCallbacks(startServiceGPS);

        Intent intent = new Intent(this, LocationService.class);
        stopService(intent);
    }
    private Runnable startServiceGPS = new Runnable() {
        @Override
        public void run() {
            Intent intent = new Intent(getApplicationContext(), LocationService.class);
            startService(intent);

            mHandler.postDelayed(startServiceGPS, (Long)AppProperties.timingProperties.get("collect-location-rate-ms"));
        }
    };

    private void startAcceleratorService(Button button) {
        setCollectorButtonState(button, CollectorButtonState.OFF);

        Intent intent = new Intent(this, AccelerationService.class);
        startService(intent);
    }

    private void stopAcceleratorService(Button button) {
        setCollectorButtonState(button, CollectorButtonState.ON);

        Intent intent = new Intent(this, AccelerationService.class);
        stopService(intent);
    }

    /** Starts the <code>SensorsService</code> if necessary and starts the collection of a sensor data
     *
     * @param type sensor type
     * @param button button reference for color change
     * @version 1.0
     */
    private void startCollectorSensor(int type, Button button) {
        if(!registeredSensors.contains(type)) {
            setCollectorButtonState(button, CollectorButtonState.OFF);

            Intent intent = new Intent(this, SensorsService.class);
            intent.putExtra("create", true);
            intent.putExtra("sensor", type);

            registeredSensors.add(type);

            startService(intent);
        }
    }

    /** Stops the collection of a sensor data and destroys the <code>SensorsService</code> if necessary
     *
     * @param type
     * @param button
     * @version 1.0
     */
    private void stopCollectorSensor(int type, Button button) {
        if(registeredSensors.contains(type)) {
            setCollectorButtonState(button, CollectorButtonState.ON);

            Intent intent = new Intent(this, SensorsService.class);
            intent.putExtra("create", true);
            intent.putExtra("sensor", type);

            registeredSensors.remove(((Object)type));
            if(registeredSensors.isEmpty())
                stopService(intent);
            else
                startService(intent);
        }
    }

    private final static int REQUEST_ENABLE_BT = 1;

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
        case REQUEST_ENABLE_BT:
            if(resultCode == RESULT_OK)
                startCollectorBluetooth();
            else
                Toast.makeText(this, "Could not enable Bluetooth!", Toast.LENGTH_SHORT).show();
            break;
        }
    }

    private MyBroadcastReceiver broadcastReceiver = new MyBroadcastReceiver();
    private class MyBroadcastReceiver extends BroadcastReceiver {
        private int oldWifiState = WifiManager.WIFI_STATE_DISABLED;

        public void resetWifiState() {
            oldWifiState = WifiManager.WIFI_STATE_DISABLED;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
                int newWifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);

                switch(newWifiState) {
                case WifiManager.WIFI_STATE_ENABLING:
                    oldWifiState = WifiManager.WIFI_STATE_ENABLING;
                    break;
                case WifiManager.WIFI_STATE_ENABLED:
                    startCollectorWifi();
                    unregisterReceiver(broadcastReceiver);
                    break;
                case WifiManager.WIFI_STATE_DISABLED:
                    if(oldWifiState == WifiManager.WIFI_STATE_ENABLING)
                        Toast.makeText(getApplicationContext(), "Could not enable Wifi!",  Toast.LENGTH_SHORT).show();
                    break;
                }
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
        case R.id.main_menu_preview:
            Intent preview = new Intent(getApplicationContext(), PreviewActivity.class);
            startActivity(preview);
            return true;

        case R.id.main_menu_settings:
            // TODO app settings
            return true;

        default:
            return super.onOptionsItemSelected(item);
        }
    }

    private enum CollectorButtonState {ON, OFF, DISABLED};
    /** Changes the appearance (color & text) of the <code>button</code> depending on the chosen button <code>state</code>.
     * @param button button to be changed
     * @param state state to be introduced
     */
    private void setCollectorButtonState(Button button, CollectorButtonState state) {
        button.setWidth(65);
        button.setHeight(65);

        switch(state) {
        case ON:
            button.setText(getString(R.string.collector_on));
            button.setBackgroundColor(Color.rgb(253,223,224)); // light red
            break;
        case OFF:
            button.setText(getString(R.string.collector_off));
            button.setBackgroundColor(Color.rgb(215,241,133)); // light green
            break;
        case DISABLED:
            button.setText("X");
            button.setBackgroundColor(Color.rgb(221, 221, 221));
            button.setEnabled(false);
        }
    }

    private void changeAppState(String key, boolean value) {
        Editor editor = appStatePrefs.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }
}
