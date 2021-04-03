package com.reactlibrary;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.content.res.Resources;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

/**
 * Created by Ankur malik on 20,March,2021
 **/
public class LocationForegroundService extends Service {

    private static final String TAG_FOREGROUND_SERVICE = "FOREGROUND_SERVICE";
    FusedLocationProviderClient client ;
    LocationRequest request = new LocationRequest();
    private LocationCallback locationCallback;
    private final IBinder binder = new LocalBinder();


    public class LocalBinder extends Binder {
        LocationForegroundService getService() {
            // Return this instance of LocalService so clients can call public methods
            return LocationForegroundService.this;
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String title = intent.getStringExtra("title");
            String icon = intent.getStringExtra("icon");
//            String color = intent.getStringExtra("color");
            startForegroundService(title,icon);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void requestLocationUpdates() {

        request.setInterval(5000);
        request.setFastestInterval(5000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        int permission = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        );
        if (permission == PackageManager.PERMISSION_GRANTED) { // Request location updates and when an update is
            // received, store the location in Firebase
            client = LocationServices.getFusedLocationProviderClient(this);
            locationCallback=new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    Location location = locationResult.getLastLocation();
                    if (location != null) {
                        LocationObserver.getInstance().updateLocation(location);
                        Log.d("Location Service", "location update $location");
                    }
                }

            };
            client.requestLocationUpdates(request, locationCallback, null);


        }
    }

    /* Used to build and start foreground service. */
    private void startForegroundService(String title, String icon) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel("background_location_service", "Background location service channel",title,icon);
        } else {

            // Create notification default intent.
            Intent intent = new Intent();
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

            // Create notification builder.
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

            // Make notification show big text.
            builder.setContentTitle("Fetching Location");

            builder.setSmallIcon(R.drawable.common_google_signin_btn_icon_dark);

            builder.setWhen(System.currentTimeMillis());

            // Make the notification max priority.
            builder.setPriority(NotificationCompat.PRIORITY_MAX);
            // Make head-up notification.
            builder.setFullScreenIntent(pendingIntent, true);



            // Build the notification.
            Notification notification = builder.build();

            // Start foreground service.
            startForeground(77, notification);
        }
        requestLocationUpdates();
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void createNotificationChannel(String channelId, String channelName,String title, String icon) {
        NotificationChannel chan = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

//        Intent dialogIntent = getApplicationContext().getPackageManager().getLaunchIntentForPackage(getApplication().getPackageName());
//        PendingIntent notifyPendingIntent = PendingIntent.getActivity(
//                this, 0, dialogIntent, PendingIntent.FLAG_UPDATE_CURRENT
//        );
        Intent fullScreenIntent = getApplicationContext().getPackageManager().getLaunchIntentForPackage(getApplication().getPackageName());
        PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(this, 0,
                fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId);
        if(title!=null){
            notificationBuilder.setContentTitle(title);
        }
        else{
            notificationBuilder.setContentTitle(getApplicationName(this));
        }
        int smallIconResId;
        if(icon!=null){
            smallIconResId = getResources().getIdentifier(icon, "drawable", getApplicationContext().getPackageName());
        }
        else{
            smallIconResId = getResources().getIdentifier("ic_notification", "drawable", getApplicationContext().getPackageName());
        }
        notificationBuilder.setSmallIcon(smallIconResId);


//        int resourceId ;
//        if (color != null) {
//            ContextCompat.getColor(this, color)
//            resourceId = getResources().getColor()
//            notificationBuilder.setColor(Color.parseColor(color));
//        }

        Notification notification = notificationBuilder
                .setOngoing(true)
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setCategory(Notification.CATEGORY_SERVICE)

                .setContentIntent(fullScreenPendingIntent)
//                .setFullScreenIntent(fullScreenPendingIntent, true)
//                .setTimeoutAfter((1000))
                .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, notificationBuilder.build());
        startForeground(1, notification);
    }

    public static String getApplicationName(Context context) {
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        int stringId = applicationInfo.labelRes;
        return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString()+" is trying to fetch your location." : context.getString(stringId)+" is trying to fetch your location.";
    }


    public void stopForegroundService() {
        Log.d(TAG_FOREGROUND_SERVICE, "Stop foreground service.");

        // Stop foreground service and remove the notification.
        client.removeLocationUpdates(locationCallback);
        stopForeground(true);

        // Stop the foreground service.
        stopSelf();

    }
}

