package com.puc.parte_electronico.location;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by jose on 6/5/14.
 */
public class LocationResolver {
    private static LocationResolver sResolver;
    private static Lock sLock;

    public static void startLocator(Context context) {
        if (sResolver == null) {
            sResolver = new LocationResolver(context);
            sLock = new ReentrantLock();
        }
        sResolver.startLocationUpdate();
    }

    public static Location getLocation() {
        if (sResolver != null) {
            return sResolver.mLocation;
        } else {
            throw new RuntimeException("Locator not started!");
        }
    }

    public static long getTimeOfLastLocationUpdate() {
        if (sResolver != null) {
            return sResolver.mTimeOfLastLocationUpdate;
        } else {
            throw new RuntimeException("Locator not started!");
        }
    }

    private LocationManager mLocationManager;
    private Location mLocation;
    private long mTimeOfLastLocationUpdate;
    private LocatorTask mTask;

    private LocationResolver(Context context) {
        mLocationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
    }

    public void startLocationUpdate() {
        sLock.lock();
        if (mTask == null) {
            mTask = new LocatorTask(mLocationManager);
            mTask.start();
        }
        sLock.unlock();
    }

    private class LocatorTask extends Thread implements LocationListener {
        private LocationManager mLocationManager;

        public LocatorTask(LocationManager locationManager) {
            mLocationManager = locationManager;
        }

        @Override
        public void run() {
            Looper.prepare();
            mLocationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, this, Looper.myLooper());
            Looper.loop();
        }

        @Override
        public void onLocationChanged(Location location) {
            sLock.lock();
            mLocation = location;
            mTask = null;
            mTimeOfLastLocationUpdate = System.currentTimeMillis();
            sLock.unlock();

            Looper.myLooper().quitSafely();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }
}
