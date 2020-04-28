package de.phxlxp_mxyxr.arrive;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.telephony.SmsManager;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.ArrayList;

import static de.phxlxp_mxyxr.arrive.Globals.CHANNEL_ID;

public class LocationService extends Service {
    private final LocationServiceBinder binder = new LocationServiceBinder();
    private LocationListener locationlistener;
    private LocationManager locationmanager;
    private PowerManager powermanager;
    private PowerManager.WakeLock wakelock;

    private final int LOCATION_INTERVAL=5000;
    private final int LOCATION_DISTANCE=0;

    private class LocationListener implements android.location.LocationListener{
        private Location lastlocation=null;

        public LocationListener(String provider)
        {
            lastlocation=new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            lastlocation=location;
            if(checkDistance(lastlocation)){
                finish();
            }
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

    private void initializeLocationManager() {
        if(locationmanager==null){
            locationmanager=(LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    public void startTracking() {
        initializeLocationManager();
        locationlistener=new LocationListener(LocationManager.GPS_PROVIDER);
        locationmanager.requestLocationUpdates(LocationManager.GPS_PROVIDER,LOCATION_INTERVAL,LOCATION_DISTANCE,locationlistener);
    }

    public class LocationServiceBinder extends Binder {
        public LocationService getService() {
            return LocationService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        powermanager=(PowerManager)getSystemService(Context.POWER_SERVICE);
        wakelock=powermanager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"arrived:locationservice");
        wakelock.acquire();

        Intent notificaitonintent=new Intent(this,JourneyActivity.class);
        PendingIntent pendingintent=PendingIntent.getActivity(this,0,notificaitonintent,0);

        Notification notification=new NotificationCompat.Builder(this,CHANNEL_ID)
                .setContentTitle(getString(R.string.journey_has_started))
                .setContentText(getString(R.string.messages_will_be_sent))
                .setSmallIcon(R.drawable.notification_icon)
                .setContentIntent(pendingintent)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .build();

        startForeground(1,notification);
        startTracking();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
        if(locationmanager!=null){
            this.locationmanager.removeUpdates(locationlistener);
            this.locationmanager=null;
        }
        wakelock.release();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    //checks if location is in the radius of the destination
    public boolean checkDistance(Location location){
        Globals global=(Globals)getApplication();
        double latitude1=global.get_latlngDestination().latitude;
        double latitude2=location.getLatitude();
        double longtitude1=global.get_latlngDestination().longitude;
        double longtitude2=location.getLongitude();
        float[] result = new float[1];
        Location.distanceBetween(latitude1,longtitude1,latitude2,longtitude2,result);
        return result[0]<=global.get_radius();
    }

    //sends messages to all selected contacts
    public void sendMessages(){
        Globals global=(Globals)getApplication();
        SmsManager smsmanager=SmsManager.getDefault();
        for(int index=0;index<global.get_arraylistSelectedContacts().size();index++){
            smsmanager.sendTextMessage(global.get_arraylistSelectedContacts().get(index).getContact_number(), null, global.get_selectedTemplate(), null, null);
        }
    }

    //sends sms and starts "FinishActivity"
    public void finish(){
        sendMessages();
        Intent intent=new Intent(this,FinishActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
