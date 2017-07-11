package com.leonfang.whereru;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.leonfang.whereru.data.source.remote.User;
import com.leonfang.whereru.event.RefreshEvent;
import com.leonfang.whereru.service.WhereRUService;
import com.leonfang.whereru.util.Logger;

import org.greenrobot.eventbus.EventBus;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.listener.ConnectListener;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;

/**
 * Created by LeonFang on 2017/5/2.
 */

public class ConnectChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        switch (intent.getAction()) {
            case "android.net.conn.CONNECTIVITY_CHANGE":
                if (networkInfo != null && networkInfo.isAvailable()) {
                    User user = BmobUser.getCurrentUser(WhereRUApplication.INSTANCE(), User.class);
                    BmobIM.connect(user.getObjectId(), new ConnectListener() {
                        @Override
                        public void done(String uid, BmobException e) {
                            if (e == null) {
                                Logger.e("ConnectChangeReceiver", "connect success");
                                //服务器连接成功就发送一个更新事件，同步更新会话及主页的小红点
                                EventBus.getDefault().post(new RefreshEvent());
                            } else {
                                Logger.e("ConnectChangeReceiver", e.getErrorCode() + "/" + e.getMessage());
                            }
                        }
                    });
                }
                break;
            case "android.intent.action.USER_PRESENT":

                break;
            case "android.intent.action.BOOT_COMPLETED":
                context.startService(new Intent(context, WhereRUService.class));
                break;
        }
    }
}
