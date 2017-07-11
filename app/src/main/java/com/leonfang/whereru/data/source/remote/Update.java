package com.leonfang.whereru.data.source.remote;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by LeonFang on 2017/4/21.
 */

public class Update extends BmobObject {
    private BmobFile app;
    private int versionCode;
    private String updateInfo;

    public BmobFile getApp() {
        return app;
    }

    public void setApp(BmobFile app) {
        this.app = app;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getUpdateInfo() {
        return updateInfo;
    }

    public void setUpdateInfo(String updateInfo) {
        this.updateInfo = updateInfo;
    }

    @Override
    public String toString() {
        return "Update{" +
                ", app=" + app +
                ", versionCode=" + versionCode +
                ", updateInfo='" + updateInfo + '\'' +
                '}';
    }
}
