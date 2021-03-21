// AnkurForgroundServiceLocationModule.java

package com.reactlibrary;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;

public class AnkurForgroundServiceLocationModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;

    public AnkurForgroundServiceLocationModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "AnkurForgroundServiceLocation";
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

    @ReactMethod
    public void open() {
//        Log.d("data---->",data.toString());
        LocationObserver.getInstance().setObserver(this);
      Intent intent =  new Intent(reactContext,  LocationForegroundService.class);
//        intent.putExtra("hbjm","ankur");
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
