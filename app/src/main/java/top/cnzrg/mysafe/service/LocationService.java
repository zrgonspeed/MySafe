package top.cnzrg.mysafe.service;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.util.Log;

/**
 * FileName: LocationService
 * Author: ZRG
 * Date: 2019/5/6 1:36
 */
public class LocationService extends Service {

    private String tag = "LocationServiceTag";

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // 获取手机的经纬度坐标

        // 1.获取位置管理者对象
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);

        // 2.以最优的方式获取经纬度坐标()
        Criteria criteria = new Criteria();
        criteria.setCostAllowed(true);  // 允许花费
        criteria.setAccuracy(Criteria.ACCURACY_FINE); // 指定获取经纬度的精确度

        // 获取最优的提供者
        String bestProvider = lm.getBestProvider(criteria, true);

        // 3.在一定时间间隔，移动一定距离后获取经纬度
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i(tag, "无位置权限");
            return;
        }

        lm.requestLocationUpdates(bestProvider, 0, 0, new MyLocationListener());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            Log.i(tag, "onLocationChanged");

            // 经度
            double longitude = location.getLongitude();

            // 纬度
            double latitude = location.getLatitude();

            // 4.发送短信
            Log.i(tag, "发送坐标短信");
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage("5554", null, "longitude = " + longitude + ", latitude = " + latitude, null, null);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // gps状态发生切换的事件监听
            Log.i(tag, "gps状态发生切换");
        }

        @Override
        public void onProviderEnabled(String provider) {
            // gps开启的事件监听
            Log.i(tag, "gps开启的事件监听");
        }

        @Override
        public void onProviderDisabled(String provider) {
            // gps关闭的事件监听
            Log.i(tag, "gps关闭的事件监听");
        }
    }
}
