package edu.mbryla.andlogger.services.location;

import java.sql.Timestamp;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import edu.mbryla.andlogger.database.dao.LocationLogDAO;
import edu.mbryla.andlogger.database.models.LocationLog;

public class LocationService extends Service implements LocationListener {
    private final static String LOG_TAG = "Location";

    private LocationLogDAO logDao;
    private LocationManager lManager;
    private Location latestBestLocation;
    private Location latestNetworkLocation;
    private Location latestGPSLocation = null;

    public LocationService() {
        super();
    }

    Timer locationTimeoutTimer = null;

    TimerTask timerTask = new TimerTask() {
        public void run() {
            Log.d(LOG_TAG, "timeout reached");

            if (latestBestLocation != null) {
                LocationLog log = new LocationLog();
                log.setTimestamp(new Timestamp(System.currentTimeMillis()));
                log.setLatitude(latestBestLocation.getLatitude());
                log.setLongitude(latestBestLocation.getLongitude());
                if (latestBestLocation.hasAccuracy()) {
                    log.setAccuracy(latestBestLocation.getAccuracy());
                }
                if (latestBestLocation.hasAltitude()) {
                    log.setAltitude(latestBestLocation.getAltitude());
                }
                if (latestBestLocation.hasSpeed()) {
                    log.setSpeed(latestBestLocation.getSpeed());
                }
                logDao.save(log);
            }

            LocationService.this.stopSelf();
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        latestBestLocation = null;
        Log.d(LOG_TAG, "service created");
        Log.d(LOG_TAG, "scan requested");

        logDao = new LocationLogDAO(this);

        lManager = (LocationManager)getSystemService(LOCATION_SERVICE);

        boolean isNetworkEnabled = lManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if(!isNetworkEnabled)
            Log.w(LOG_TAG, "network disabled - location accuracy may be affected");
        else {
            lManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
            latestNetworkLocation = lManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }

        boolean isGPSEnabled = lManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(!isGPSEnabled)
            Log.w(LOG_TAG, "GPS disabled - location accuracy may be affected");
        else {
            lManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            latestGPSLocation = lManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }

        /**
         * Poczatkowe ustalenie najlepszej lokalizacji, jesli nic nie wyciagnal z cache to bedzie nullem, coz...
         */
        if(latestGPSLocation == null){
            if(latestNetworkLocation != null)
                latestBestLocation = latestNetworkLocation;
        } else {
            if(latestNetworkLocation == null)
                latestBestLocation = latestGPSLocation;
            else{
                if(isBetterLocation(latestNetworkLocation, latestGPSLocation))
                    latestBestLocation = latestNetworkLocation;
                else latestBestLocation = latestGPSLocation;
            }
        }

        locationTimeoutTimer = new Timer("locationtimer", true);
        locationTimeoutTimer.schedule(timerTask, 30000);
    }

    @Override
    public void onDestroy() {
        if(lManager != null)
            lManager.removeUpdates(this);

        Log.d(LOG_TAG, "service destroyed");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location == null) return;

        if(latestBestLocation == null)
            latestBestLocation = location;
        else if(isBetterLocation(location, latestBestLocation)){
            latestBestLocation = location;
        }
    }

    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    private static final int ONE_MINUTE = 1000 * 60 * 1;

    /** Determines whether one Location reading is better than the current Location fix
     * @param location  The new Location that you want to evaluate
     * @param currentBestLocation  The current Location fix, to which you want to compare the new one
     */
    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > ONE_MINUTE;
        boolean isSignificantlyOlder = timeDelta < -ONE_MINUTE;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

}
