package com.leonfang.whereru.main;

import android.content.Context;

import com.leonfang.whereru.base.BasePresenter;
import com.leonfang.whereru.base.BaseView;
import com.leonfang.whereru.data.source.remote.User;

import java.io.File;

/**
 * Created by LeonFang on 2017/4/12.
 */

public interface MainContract {
    interface View extends BaseView{
        void showUserInfo(User user);
        void updateUserInfoFail(String string);
        void connectStatusChanged(String s);
        void showUpdateDialog(String s,boolean needUpdate);
    }
    interface Presenter extends BasePresenter{
        void getUserInfo();
        void logout();
        void initBmobIm();
        void getUpdateInfo(boolean fromUser);
        void downloadUpdate(Context context);
        void updateUserInfo();

        /**
         * 更新当前用户头像
         * @param avatar 头像文件
         */
        void updateCurrentUserAvatar(File avatar);
    }
}
