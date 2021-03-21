package com.reactlibrary;

import android.location.Location;

/**
 * Created by Ankur malik on 20,March,2021
 **/
public class LocationObserver {

    private LocationObserver(){}
    private static LocationObserver instance;
    private Observer observer;

    public static LocationObserver getInstance() {
        if (instance == null) {
            instance = new LocationObserver();
        }
        return instance;
    }

    public void setObserver(Observer obs) {
        observer = obs;
    }

    public void updateLocation(Location location) {
        if (observer != null)
        observer.onLocationUpdate(location);
    }

}
