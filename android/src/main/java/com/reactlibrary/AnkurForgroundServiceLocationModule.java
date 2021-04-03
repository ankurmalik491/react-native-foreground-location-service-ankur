


package com.reactlibrary;

import android.app.KeyguardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.view.WindowManager;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import static android.content.Context.POWER_SERVICE;

public class AnkurForgroundServiceLocationModule extends ReactContextBaseJavaModule implements Observer {

    private final ReactApplicationContext reactContext;
    private Location loc;
    LocationForegroundService l ;

    public AnkurForgroundServiceLocationModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            LocationForegroundService.LocalBinder binder = (LocationForegroundService.LocalBinder) service;
            l = binder.getService();
            //  mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            // mBound = false;
        }
    };


    @Override
    public String getName() {
        return "AnkurForgroundServiceLocation";
    }

    @ReactMethod
    public void open(ReadableMap optionsMap) {
        Log.d("data---->",optionsMap.getString("title").toString());
        ;
        LocationObserver.getInstance().setObserver(this);
        Intent intent =  new Intent(reactContext,  LocationForegroundService.class);
        intent.putExtra("title",optionsMap.getString("title"));
        intent.putExtra("icon",optionsMap.getString("icon"));
        intent.putExtra("color",optionsMap.getString("color"));
        reactContext.startService(intent);
        reactContext.bindService(intent,connection , Context.BIND_AUTO_CREATE);

    }

    @ReactMethod
    public void close() {
        if(l!=null)
            l.stopForegroundService();
    }

    @Override
    public void onLocationUpdate(Location location) {
        Log.d("<<<<<",""+location);
        if (getReactApplicationContext().hasActiveCatalystInstance()) {
            getReactApplicationContext()
                    .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                    .emit("locationFetchingBackground", ""+ location.getLatitude()+ ","+ location.getLongitude());
        }
    }
}
