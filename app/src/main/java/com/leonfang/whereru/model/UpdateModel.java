package com.leonfang.whereru.model;

import com.leonfang.whereru.data.source.remote.Update;
import com.leonfang.whereru.util.Logger;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindStatisticsListener;
import cn.bmob.v3.listener.SQLQueryListener;

/**
 * Created by LeonFang on 2017/4/21.
 */

public class UpdateModel extends BaseModel {
    private static UpdateModel updateModel = new UpdateModel();

    public static UpdateModel getInstance() {
        return updateModel;
    }
    private UpdateModel() {
    }

    public void queryUpdateInfo(FindStatisticsListener listener){
        Logger.e(getClass(),"queryUpdateInfo");
        BmobQuery<Update> query=new BmobQuery<>();
        query.max(new String[]{"versionCode"});
        query.groupby(new String[]{"app","updateInfo","versionCode"});
        query.findStatistics(getContext(), Update.class, listener);
    }
}
