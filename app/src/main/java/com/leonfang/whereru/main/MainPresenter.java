package com.leonfang.whereru.main;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.leonfang.whereru.WhereRUApplication;
import com.leonfang.whereru.data.source.remote.Update;
import com.leonfang.whereru.data.source.remote.User;
import com.leonfang.whereru.event.RefreshEvent;
import com.leonfang.whereru.model.UpdateModel;
import com.leonfang.whereru.model.UserModel;
import com.leonfang.whereru.util.DownloadUtil;
import com.leonfang.whereru.util.Logger;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.List;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.core.ConnectionStatus;
import cn.bmob.newim.listener.ConnectListener;
import cn.bmob.newim.listener.ConnectStatusChangeListener;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindStatisticsListener;
import cn.bmob.v3.listener.SQLQueryListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by LeonFang on 2017/4/12.
 */

public class MainPresenter implements MainContract.Presenter {
    private MainContract.View view;
    private UserModel userModel;
    private Update update;
    private ConnectStatusChangeListener connectStatusChangeListener= new ConnectStatusChangeListener() {
        @Override
        public void onChange(ConnectionStatus status) {
            view.connectStatusChanged("" + status.getMsg());
        }
    };

    public MainPresenter(MainContract.View view) {
        this.view = view;
        this.userModel = UserModel.getInstance();
    }

    @Override
    public void getUserInfo() {
        view.showUserInfo(userModel.getCurrentUser());
    }

    @Override
    public void logout() {
        userModel.logout();
    }

    @Override
    public void initBmobIm() {
        User user = BmobUser.getCurrentUser(WhereRUApplication.INSTANCE(), User.class);
        BmobIM.connect(user.getObjectId(), new ConnectListener() {
            @Override
            public void done(String uid, BmobException e) {
                if (e == null) {
                    Logger.i("MainPresenter", "connect success");
                    //服务器连接成功就发送一个更新事件，同步更新会话及主页的小红点
                    EventBus.getDefault().post(new RefreshEvent());
                } else {
                    Logger.e("MainPresenter", e.getErrorCode() + "/" + e.getMessage());
                }
            }
        });
        //监听连接状态，也可通过BmobIM.getInstance().getCurrentStatus()来获取当前的长连接状态
        BmobIM.getInstance().setOnConnectStatusChangeListener(connectStatusChangeListener);
    }

    @Override
    public void getUpdateInfo(final boolean fromUser) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = WhereRUApplication.INSTANCE().getPackageManager().getPackageInfo(WhereRUApplication.INSTANCE()
                    .getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageInfo != null) {
            final int versionCode = packageInfo.versionCode;
            UpdateModel.getInstance().queryUpdateInfo(new FindStatisticsListener() {
                @Override
                public void onSuccess(Object o) {
                    Logger.e(MainPresenter.class, o.toString());
                    List<Update> updateList = new Gson().fromJson(o.toString(), new TypeToken<List<Update>>() {
                    }.getType());
                    Logger.e(MainPresenter.class, updateList.toString() + "versionCode:" + versionCode);
                    update = updateList.get(0);
                    if (update.getVersionCode() > versionCode) {
                        view.showUpdateDialog(update.getUpdateInfo(),true);
                    }else if(fromUser){
                        view.showUpdateDialog("当前是最新版本",false);
                    }

                }

                @Override
                public void onFailure(int i, String s) {

                }
            });
        }
    }

    @Override
    public void downloadUpdate(Context context) {
        if(update!=null&&update.getApp()!=null){
            DownloadUtil downloadUtil = new DownloadUtil(context);
            downloadUtil.downloadAPK(update.getApp().getUrl(),update.getVersionCode());
        }
    }

    @Override
    public void updateUserInfo() {
        // TODO: 2017/4/12 更新用户信息
    }

    @Override
    public void updateCurrentUserAvatar(File avatar) {
        userModel.updateCurrentUserAvatar(avatar, new UpdateListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int i, String s) {
                view.updateUserInfoFail(s);
            }
        });
    }


    @Override
    public void start() {

    }
}
