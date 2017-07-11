package com.leonfang.whereru.map;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.BitmapDecoder;
import com.leonfang.whereru.R;
import com.leonfang.whereru.base.BaseFragment;
import com.leonfang.whereru.data.source.remote.Friend;
import com.leonfang.whereru.data.source.remote.User;
import com.leonfang.whereru.event.RefreshLocationEvent;
import com.leonfang.whereru.service.WhereRUService;
import com.leonfang.whereru.util.Constants;
import com.leonfang.whereru.util.GlideCircleTransform;
import com.leonfang.whereru.util.Logger;
import com.leonfang.whereru.util.Util;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by LeonFang on 2017/4/4.
 */

public class MapFragment extends BaseFragment implements MapContract.View {
    @BindView(R.id.map_view)
    MapView mapView;
    @BindView(R.id.iv_refresh)
    ImageView ivRefresh;
    @BindView(R.id.iv_locate)
    ImageView ivLocate;
    private MapPresenter mapPresenter;
    private BaiduMap mBaiduMap;
    private BDLocation bdLocation;
    private RotateAnimation rotateAnimation;
    private Intent service;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Logger.e(getClass(),"onViewCreated");
        if (savedInstanceState != null) {
            bdLocation = (BDLocation) savedInstanceState.getParcelable(Constants.LOCATION);
            Logger.e(getClass(),"onViewCreated,getbdLocation");
        }
        initView();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Logger.e(getClass(),"onSaveInstanceState");
        outState.putParcelable(Constants.LOCATION, bdLocation);
    }

    @Override
    protected void initView() {
        mBaiduMap = mapView.getMap();
        mapPresenter = new MapPresenter(this);
        mapPresenter.queryFriends();
        rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        service = new Intent(getActivity(), WhereRUService.class);
    }

    @OnClick({R.id.iv_refresh, R.id.iv_locate})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_locate:
                if (bdLocation != null) {
                    locateCurrentUser(bdLocation);
                }
                break;
            case R.id.iv_refresh:
                mapPresenter.queryFriends();
                ivRefresh.setEnabled(false);
                rotateAnimation.setDuration(300);
                rotateAnimation.setRepeatCount(Integer.MAX_VALUE);
                ivRefresh.startAnimation(rotateAnimation);
                break;
        }
        getActivity().startService(service);

    }

    @Override
    protected int getContentLayoutRes() {
        return R.layout.fragment_map;
    }

    @Override
    public void queryFriendsSuccess(List<Friend> list) {
        mBaiduMap.clear();
        setMakerPool(list);
        Logger.e(getClass(), list.toString());
        ivRefresh.setEnabled(true);
        rotateAnimation.cancel();
    }

    @Override
    public void queryFriendsFail(String s) {
        ivRefresh.setEnabled(true);
        rotateAnimation.cancel();
    }

    @Subscribe
    public void onEventMainThread(RefreshLocationEvent event) {
        bdLocation = event.getLocation();
        locateCurrentUser(bdLocation);
    }

    @Override
    public void locateCurrentUser(final BDLocation location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLng(latLng);
        mBaiduMap.animateMapStatus(mapStatusUpdate);
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        // 构造定位数据
        MyLocationData locData = new MyLocationData.Builder()
                // 此处设置开发者获取到的方向信息，顺时针0-360
                .latitude(latLng.latitude)
                .longitude(latLng.longitude).build();
        // 设置定位数据
        mBaiduMap.setMyLocationData(locData);
        mBaiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChange(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {
//                mBaiduMap.clear();
//                setMakerPool();
            }
        });
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                return false;
            }
        });
    }

    private void setMakerPool(final List<Friend> list) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Logger.e("MapFragment", "Thread.start");
                for (Friend friend : list) {
                    Bitmap myBitmap = null;
                    User friendUser = friend.getFriendUser();
                    int imgSize = Util.px2dp(150);
                    try {
                        myBitmap = Glide.with(getContext())
                                .load(friendUser.getAvatar().getUrl())
                                .asBitmap()
                                .error(R.drawable.personal_icon_default_avatar)
                                .transform(new GlideCircleTransform(getContext(), 2, ContextCompat.getColor(getContext(), R.color.colorPrimary)))
                                .into(imgSize, imgSize)
                                .get();
                        if (myBitmap == null) {
                            Glide.with(getContext()).load(R.drawable.personal_icon_default_avatar).asBitmap().into(imgSize, imgSize).get();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    if (myBitmap == null) {
                        continue;
                    }
                    BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory.fromBitmap(myBitmap);
                    LatLng latLng = new LatLng(friendUser.getGeoPoint().getLatitude(), friendUser.getGeoPoint().getLongitude());
                    Bundle bundle = new Bundle();
                    MarkerOptions markerOptions = new MarkerOptions().position(latLng).icon(mCurrentMarker).period(6)
                            .extraInfo(bundle).title(friendUser.getNickname());
                    mBaiduMap.addOverlay(markerOptions);
                }
            }
        }).start();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mapView != null) {
            mapView.onDestroy();
        }
        mBaiduMap.setMyLocationEnabled(false);
    }


}
