package com.reactlibrary;

import android.location.Location;

/**
 * Created by Ankur malik on 20,March,2021
 **/
public interface Observer {

    void onLocationUpdate(Location location);
}
