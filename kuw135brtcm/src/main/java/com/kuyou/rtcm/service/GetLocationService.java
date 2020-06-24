package com.kuyou.rtcm.service;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.GpsStatus.NmeaListener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;
import java.util.Timer;

public class GetLocationService extends Service {
    private final int GET_POSITION = 1;
    private final int GET_POSITION_TIME_OUT = 2;
    protected final String TAG = "GetLocationService";
    private final long TIME_OUT = 300000L;
    private final String SHAREDPREF_FILE = "LocationService";
    private SharedPreferences pref;
    private final String ACTION_GETLOCATION = "com.BDMessage.sendLocationBroadCast";
    private final String ACTION_GETGPS_BD2_STATUS = "com.BDMessage.sendGpsBD2StatusBroadCast";
    private final String ACTION_GETGPS_STATUS = "com.BDMessage.sendGpsStatusBroadCast";
    private final String ACTION_GETBD2_STATUS = "com.BDMessage.sendBD2StatusBroadCast";
    private final String ACTION_GETNMEA_STATUS = "com.BDMessage.sendNmeaBroadCast";
    private boolean mAllowGetPosition = false;
    private Handler mHandler;
    private double mLatitude = -1.0D;
    private LocationManager mLocationManager;
    private Location mlocation;
    private double mLongitude = -1.0D;
    private ArrayList<String> mParts;
    private Timer mTimer;
    private String mphoneNumber = null;
    private boolean needCloseGPS = false;
    private PendingIntent paIntent;
    private ArrayList<PendingIntent> paIntents;
    final int max_checktimes = 20;
    final int min_checktimes = 5;
    private int gps_checktimes = 0;
    private int gprs_checktimes = 0;
    private int total_checktimes = 0;
    private Handler soshand = new soshandler();
    LocationBinder binder = new LocationBinder();
    private final LocationListener mGpsLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            saveLocation(location);
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    /**
     * Gps状态监听
     * satellite.getElevation(); //卫星仰角
     * satellite.getAzimuth();   //卫星方位角
     * satellite.getSnr();       //信噪比
     * satellite.getPrn();       //伪随机数，可以认为他就是卫星的编号
     * satellite.hasAlmanac();   //卫星历书
     * satellite.hasEphemeris();
     * satellite.usedInFix();
     */
    private GpsStatus.Listener gpsStautusListener = new GpsStatus.Listener() {
        @Override
        public void onGpsStatusChanged(int event) {
            Log.d(TAG, "BD2 enter onGpsStatusChanged");
            GpsStatus gpsStatus = mLocationManager.getGpsStatus(null);
            switch (event) {
                case GpsStatus.GPS_EVENT_FIRST_FIX:
                    Log.d(TAG, "BD2 is usable");
                    int i = gpsStatus.getTimeToFirstFix();
                    Log.d(TAG, "BD2 getTimeToFirstFix = " + i);
                    break;
                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                    // 得到所有收到的卫星的信息，包括 卫星的高度角、方位角、信噪比、和伪随机号（及卫星编号）
                    Log.d(TAG, "BD2 enter GPS_EVENT_SATELLITE_STATUS");
                    int index = 0;
                    int gpsIndex = 0;
                    int bd2Index = 0;
                    int satiliteNum = 0;
                    double[] starInfo_dSNR = new double[16];
                    double[] starInfo_dStarAzimuth = new double[16];
                    double[] starInfo_dStarElevation = new double[16];
                    long[] starInfo_lStarNum = new long[16];

                    int gpsSatiliteNum = 0;
                    double[] gpsStarInfo_dSNR = new double[16];
                    double[] gpsStarInfo_dStarAzimuth = new double[16];
                    double[] gpsStarInfo_dStarElevation = new double[16];
                    long[] gpsStarInfo_lStarNum = new long[16];

                    int bd2SatiliteNum = 0;
                    double[] bd2StarInfo_dSNR = new double[16];
                    double[] bd2StarInfo_dStarAzimuth = new double[16];
                    double[] bd2StarInfo_dStarElevation = new double[16];
                    long[] bd2StarInfo_lStarNum = new long[16];
                    Iterable<GpsSatellite> satellites = gpsStatus.getSatellites();
                    for (GpsSatellite satellite : satellites) {
                        // 包括 卫星的高度角、方位角、信噪比、和伪随机号（及卫星编号)
                        if (satellite.getPrn() >= 0 && satellite.getSnr() > 1 && index < 16) {
                            Log.d(TAG, "satellite = " + satellite);
                            starInfo_dSNR[index] = satellite.getSnr(); //信噪比
                            starInfo_dStarAzimuth[index] = satellite.getAzimuth();//卫星方位角
                            starInfo_dStarElevation[index] = satellite.getElevation();//卫星仰角
                            starInfo_lStarNum[index] = satellite.getPrn(); //伪随机数，可以认为他就是卫星的编号
                            index++;
                            satiliteNum++;
                        }
                        if (satellite.getPrn() >= 160 && satellite.getSnr() > 1 && bd2Index < 16) {
                            Log.d(TAG, "satellite = " + satellite);
                            bd2StarInfo_dSNR[bd2Index] = satellite.getSnr(); //信噪比
                            bd2StarInfo_dStarAzimuth[bd2Index] = satellite.getAzimuth();//卫星方位角
                            bd2StarInfo_dStarElevation[bd2Index] = satellite.getElevation();//卫星仰角
                            bd2StarInfo_lStarNum[bd2Index] = satellite.getPrn() - 160; //伪随机数，可以认为他就是卫星的编号
                            bd2Index++;
                            bd2SatiliteNum++;
                        }
                        if (satellite.getPrn() < 160 && satellite.getSnr() > 1 && gpsIndex < 16) {
                            Log.d(TAG, "satellite = " + satellite);
                            gpsStarInfo_dSNR[gpsIndex] = satellite.getSnr(); //信噪比
                            gpsStarInfo_dStarAzimuth[gpsIndex] = satellite.getAzimuth();//卫星方位角
                            gpsStarInfo_dStarElevation[gpsIndex] = satellite.getElevation();//卫星仰角
                            gpsStarInfo_lStarNum[gpsIndex] = satellite.getPrn(); //伪随机数，可以认为他就是卫星的编号
                            gpsIndex++;
                            gpsSatiliteNum++;
                        }
                    }
                    sendGpsAndBD2StatusBroadCastReceiver(starInfo_dSNR, starInfo_dStarAzimuth, starInfo_dStarElevation, starInfo_lStarNum, satiliteNum);
                    sendGpsStatusBroadCastReceiver(gpsStarInfo_dSNR, gpsStarInfo_dStarAzimuth, gpsStarInfo_dStarElevation, gpsStarInfo_lStarNum, gpsSatiliteNum);
                    sendBD2StatusBroadCastReceiver(bd2StarInfo_dSNR, bd2StarInfo_dStarAzimuth, bd2StarInfo_dStarElevation, bd2StarInfo_lStarNum, bd2SatiliteNum);
                    break;
                case GpsStatus.GPS_EVENT_STARTED:
                    Log.d(TAG, "BD2 enter GPS_EVENT_STARTED");
                    break;
                case GpsStatus.GPS_EVENT_STOPPED:
                    Log.d(TAG, "BD2 enter GPS_EVENT_STOPPED");
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * <p>
     * action : GGA数据监听<br/>
     * author: wuguoxian <br/>
     * date: 20200514 <br/>
     */
    NmeaListener mNmeaListener = new NmeaListener() {
        @Override
        public void onNmeaReceived(long timestamp, String nmea) {
            //此处以GPGGA为例
            //$GPGGA,232427.000,3751.1956,N,11231.1494,E,1,6,1.20,824.4,M,-23.0,M,,*7E
            Log.d(TAG, "enter onNmeaReceived -->nmea = " + nmea);
            if (nmea.contains("GGA")) {
                //GPGGA中altitude是MSL altitude(平均海平面)
                sendNmeaBroadCastReceiver(timestamp, nmea);
            } else {
                //不处理
            }
        }
    };

    private void sendNmeaBroadCastReceiver(long timestamp, String nmea) {
        Intent intent = new Intent(ACTION_GETNMEA_STATUS);
        Bundle bundle = new Bundle();
        bundle.putLong("TimeStamp", timestamp);
        bundle.putString("NMEA", nmea);
        intent.putExtras(bundle);
        sendBroadcast(intent);
    }

    private void sendGpsAndBD2StatusBroadCastReceiver(double[] dSNR, double[] dStarAzimuth, double[] dStarElevation, long[] StarNum, int satiliteNum) {
        Intent intent = new Intent(ACTION_GETGPS_BD2_STATUS);
        Bundle bundle = new Bundle();
        bundle.putDoubleArray("SNRArray", dSNR);
        bundle.putDoubleArray("AzimuthArray", dStarAzimuth);
        bundle.putDoubleArray("ElevationArray", dStarElevation);
        bundle.putLongArray("StarNumArray", StarNum);
        bundle.putInt("SatiliteNum", satiliteNum);
        intent.putExtras(bundle);
        sendBroadcast(intent);
    }

    private void sendGpsStatusBroadCastReceiver(double[] dSNR, double[] dStarAzimuth, double[] dStarElevation, long[] StarNum, int satiliteNum) {
        Intent intent = new Intent(ACTION_GETGPS_STATUS);
        Bundle bundle = new Bundle();
        bundle.putDoubleArray("SNRArray", dSNR);
        bundle.putDoubleArray("AzimuthArray", dStarAzimuth);
        bundle.putDoubleArray("ElevationArray", dStarElevation);
        bundle.putLongArray("StarNumArray", StarNum);
        bundle.putInt("SatiliteNum", satiliteNum);
        intent.putExtras(bundle);
        sendBroadcast(intent);
    }

    private void sendBD2StatusBroadCastReceiver(double[] dSNR, double[] dStarAzimuth, double[] dStarElevation, long[] StarNum, int satiliteNum) {
        Intent intent = new Intent(ACTION_GETBD2_STATUS);
        Bundle bundle = new Bundle();
        bundle.putDoubleArray("SNRArray", dSNR);
        bundle.putDoubleArray("AzimuthArray", dStarAzimuth);
        bundle.putDoubleArray("ElevationArray", dStarElevation);
        bundle.putLongArray("StarNumArray", StarNum);
        bundle.putInt("SatiliteNum", satiliteNum);
        intent.putExtras(bundle);
        sendBroadcast(intent);
    }

    private boolean checkLocationSettings() {
        return mLocationManager.isProviderEnabled("gps");
    }

    private void getGPSLocation() {
        mlocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 0.0F, mGpsLocationListener);
        mLocationManager.addGpsStatusListener(gpsStautusListener);
        mLocationManager.addNmeaListener(mNmeaListener);
        if (null != mlocation) {
            Log.d("lvyanbing", "tty3_mlocation gps is not null  ");
        } else {
            Log.d("lvyanbing", "tty3_mlocation gps  null  ");
        }
    }

    private void openLocationSource() {
        //Settings.Secure.setLocationProviderEnabled(getContentResolver(), "gps", true);
        Toast.makeText(GetLocationService.this, "位置信息开关未打开，请先打开位置信息开关！", Toast.LENGTH_SHORT).show();
    }

    private void saveLocation(Location location) {
        if (location != null) {
            mlocation = location;
            mLatitude = mlocation.getLatitude();
            mLongitude = mlocation.getLongitude();
            sendLocationBroadCast(location);
        }
    }

    private void getLocation() {
    }

    private void sendLocationBroadCast(Location location) {
        mLatitude = location.getLatitude();
        mLongitude = location.getLongitude();
        double latitude = mLatitude;
        double longitude = mLongitude;
        double height = location.getAltitude();
        long gpsTime = location.getTime();
        float speed = location.getSpeed();
        float angle = location.getBearing();

        int timeOffset = TimeZone.getDefault().getOffset(gpsTime);
        long gpsTime_0 = gpsTime - timeOffset;
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(gpsTime));
        String time_0 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(gpsTime_0));
        Bundle bundle = new Bundle();
        Intent intent = new Intent(ACTION_GETLOCATION);
        bundle.putDouble("Latitude", latitude);
        bundle.putDouble("Longitude", longitude);
        bundle.putDouble("Height", height);
        bundle.putFloat("Speed", speed);
        bundle.putFloat("Angle", angle);
        bundle.putString("Time", time);
        bundle.putString("Time_0", time_0);
        bundle.putLong("GPSTIME", gpsTime);
        intent.putExtras(bundle);
        GetLocationService.this.sendBroadcast(intent);
    }

    protected void finishSelf() {
        stopSelf();
    }

    public IBinder onBind(Intent paramIntent) {
        return this.binder;
    }

    public boolean onUnbind(Intent paramIntent) {
        return super.onUnbind(paramIntent);
    }

    public void onCreate() {
        super.onCreate();
        Log.v(TAG, "tty3_GetLocationService enter onCreate");
        mLocationManager = ((LocationManager) getSystemService("location"));
        if (!checkLocationSettings()) {
            openLocationSource();
            needCloseGPS = true;
            pref = getSharedPreferences(SHAREDPREF_FILE, MODE_PRIVATE);
            Editor edt = pref.edit();
            edt.putString("wind.sos.close_gps", "1");
            edt.commit();
        } else {
            needCloseGPS = false;
            pref = getSharedPreferences(SHAREDPREF_FILE, MODE_PRIVATE);
            Editor edt = pref.edit();
            edt.putString("wind.sos.close_gps", "0");
            edt.commit();
        }
    }

    public void onDestroy() {
        Log.v(TAG, "tty3_GetLocationService enter onDestroy");
        gps_checktimes = 0;
        gprs_checktimes = 0;
        mLatitude = -1.0D;
        mLongitude = -1.0D;
        gps_checktimes = 0;
        gprs_checktimes = 0;
        total_checktimes = 0;
        mlocation = null;
        mLocationManager.removeUpdates(mGpsLocationListener);
        mLocationManager.removeGpsStatusListener(gpsStautusListener);
        mLocationManager.removeNmeaListener(mNmeaListener);
        if (needCloseGPS) {
            // Settings.Secure.setLocationProviderEnabled(getContentResolver(), "gps", false);
        }
        super.onDestroy();
    }

    public int onStartCommand(Intent intent, int paramInt1, int paramInt2) {

        gps_checktimes = 0;
        gprs_checktimes = 0;
        mLatitude = -1.0D;
        mLongitude = -1.0D;
        gps_checktimes = 0;
        gprs_checktimes = 0;
        total_checktimes = 0;
        getGPSLocation();
        Log.v(TAG, "tty3_GetLocationService enter onStartCommand");
        return START_REDELIVER_INTENT;
    }

    final class soshandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Log.d("lvyanbing", "tty3_GetLocationService soshandler ");
            getLocation();
        }

    }

    public class LocationBinder extends Binder {
        public LocationBinder() {
        }

        public void GetLocationInfo() {
            gps_checktimes = 0;
            gprs_checktimes = 0;
            mLatitude = -1.0D;
            mLongitude = -1.0D;
            gps_checktimes = 0;
            gprs_checktimes = 0;
            total_checktimes = 0;
            getGPSLocation();
            Log.v(TAG, "tty3_GetLocationService enter getLocation");
        }

        public void stopLocation() {
            gps_checktimes = 0;
            gprs_checktimes = 0;
            mLatitude = -1.0D;
            mLongitude = -1.0D;
            gps_checktimes = 0;
            gprs_checktimes = 0;
            total_checktimes = 0;
            mLocationManager.removeUpdates(mGpsLocationListener);
            mLocationManager.removeGpsStatusListener(gpsStautusListener);
            Log.v(TAG, "tty3_GetLocationService enter stopLocation");
        }

        public String GetLocationInfoString() {
            if ((mLatitude != -1.0D) && (mLongitude != -1.0D)) {
                DecimalFormat df = new DecimalFormat("0.#######");
                return df.format(mLongitude) + "," + df.format(mLatitude);
            }
            return null;
        }
    }
}
