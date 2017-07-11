package com.leonfang.whereru.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.leonfang.whereru.Config;
import com.leonfang.whereru.WhereRUApplication;
import com.leonfang.whereru.event.RefreshLocationEvent;
import com.leonfang.whereru.model.UserModel;
import com.leonfang.whereru.util.Logger;

import org.greenrobot.eventbus.EventBus;

import cn.bmob.v3.datatype.BmobGeoPoint;

/**
 * Created by LeonFang on 2017/4/17.
 */

public class WhereRUService extends Service {

    private static final int GRAY_SERVICE_ID = 10001;
    private LocationService locationService;
    private BmobGeoPoint recentGeoPoint;
    private BmobGeoPoint currentGeoPoint;
    private double distanceInKilometers;
    private boolean isFirstLocate;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        isFirstLocate = true;
        locationService = ((WhereRUApplication) getApplication()).locationService;
        locationService.registerListener(mListener);
        locationService.start();
        recentGeoPoint = new BmobGeoPoint();
        currentGeoPoint = new BmobGeoPoint();
        Logger.e(getClass(), "onCreate()");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }


    private int scanSpan;
    private LocationClientOption option;
    private BDLocationListener mListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                StringBuffer sb = new StringBuffer(256);
                sb.append("time : ");
                /**
                 * 时间也可以使用systemClock.elapsedRealtime()方法 获取的是自从开机以来，每次回调的时间；
                 * location.getTime() 是指服务端出本次结果的时间，如果位置不发生变化，则时间不变
                 */
                sb.append(location.getTime());
//                sb.append("\nlocType : ");// 定位类型
//                sb.append(location.getLocType());
//                sb.append("\nlocType description : ");// *****对应的定位类型说明*****
//                sb.append(location.getLocTypeDescription());
//                sb.append("\nlatitude : ");// 纬度
//                sb.append(location.getLatitude());
//                sb.append("\nlontitude : ");// 经度
//                sb.append(location.getLongitude());
//                sb.append("\nradius : ");// 半径
//                sb.append(location.getRadius());
//                sb.append("\nCountry : ");// 国家名称
//                sb.append(location.getCountry());
//                sb.append("\ncitycode : ");// 城市编码
//                sb.append(location.getCityCode());
//                sb.append("\ncity : ");// 城市
//                sb.append(location.getCity());
//                sb.append("\nDistrict : ");// 区
//                sb.append(location.getDistrict());
//                sb.append("\nStreet : ");// 街道
//                sb.append(location.getStreet());
                sb.append("\naddr : ");// 地址信息
                sb.append(location.getAddrStr());
                sb.append("\nUserIndoorState: ");// *****返回用户室内外判断结果*****
                sb.append(location.getUserIndoorState());
                sb.append("\nDirection(not all devices have value): ");
                sb.append(location.getDirection());// 方向
                sb.append("\nlocationdescribe: ");
                sb.append(location.getLocationDescribe());// 位置语义化信息
                sb.append("\nPoi: ");// POI信息
                if (location.getPoiList() != null && !location.getPoiList().isEmpty()) {
                    for (int i = 0; i < location.getPoiList().size(); i++) {
                        Poi poi = (Poi) location.getPoiList().get(i);
                        sb.append(poi.getName() + ";");
                    }
                }
                if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                    sb.append("\nspeed : ");
                    sb.append(location.getSpeed());// 速度 单位：km/h
                    sb.append("\nsatellite : ");
                    sb.append(location.getSatelliteNumber());// 卫星数目
                    sb.append("\nheight : ");
                    sb.append(location.getAltitude());// 海拔高度 单位：米
                    sb.append("\ngps status : ");
                    sb.append(location.getGpsAccuracyStatus());// *****gps质量判断*****
                    sb.append("\ndescribe : ");
                    sb.append("gps定位成功");
                } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                    // 运营商信息
                    if (location.hasAltitude()) {// *****如果有海拔高度*****
                        sb.append("\nheight : ");
                        sb.append(location.getAltitude());// 单位：米
                    }
                    sb.append("\noperationers : ");// 运营商信息
                    sb.append(location.getOperators());
                    sb.append("\ndescribe : ");
                    sb.append("网络定位成功");
                } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                    sb.append("\ndescribe : ");
                    sb.append("离线定位成功，离线定位结果也是有效的");
                } else if (location.getLocType() == BDLocation.TypeServerError) {
                    sb.append("\ndescribe : ");
                    sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
                } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                    sb.append("\ndescribe : ");
                    sb.append("网络不同导致定位失败，请检查网络是否通畅");
                } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                    sb.append("\ndescribe : ");
                    sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
                }
                option = locationService.getOption();
                currentGeoPoint.setLatitude(location.getLatitude());
                currentGeoPoint.setLongitude(location.getLongitude());
                if (recentGeoPoint.getLatitude() == 0 && recentGeoPoint.getLongitude() == 0) {
                    distanceInKilometers = 0;
                } else {
                    distanceInKilometers = currentGeoPoint.distanceInKilometersTo(recentGeoPoint);
                }
                if (distanceInKilometers > 0.03 || isFirstLocate) {
                    EventBus.getDefault().post(new RefreshLocationEvent(location));
                    uploadGeoPoint(currentGeoPoint, location.getAddrStr() + "\n" + location.getLocationDescribe(), location.getTime());
                    scanSpan = Config.SCAN_SPAN;
                    isFirstLocate = false;
                } else {
                    if (scanSpan < 10 * 60 * 1000) {
                        scanSpan = option.getScanSpan() * 2;
                    }
                }
                option.setScanSpan(scanSpan);
                locationService.setLocationOption(option);
                recentGeoPoint.setLatitude(location.getLatitude());
                recentGeoPoint.setLongitude(location.getLongitude());
                logMsg(sb.toString());
            }
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }
    };

    private void uploadGeoPoint(BmobGeoPoint currentGeoPoint, String location, String time) {
        UserModel.getInstance().uploadGeoPoint(currentGeoPoint, location, time);
    }

    private void logMsg(final String s) {
        Log.e("service", s);
        Log.e("service", distanceInKilometers + "");
        Log.e("service", scanSpan + "");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        locationService.stop();
        locationService.unregisterListener(mListener);
    }

}
