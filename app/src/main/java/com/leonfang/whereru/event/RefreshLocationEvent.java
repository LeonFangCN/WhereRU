package com.leonfang.whereru.event;

import com.baidu.location.BDLocation;

import cn.bmob.v3.datatype.BmobGeoPoint;

/**
 * Created by Administrator on 2016/4/28.
 */
public class RefreshLocationEvent {
    private BDLocation location;

    public BDLocation getLocation() {
        return location;
    }

    public void setLocation(BDLocation location) {
        this.location = location;
    }

    public RefreshLocationEvent(BDLocation location) {
        this.location = location;
    }
}
