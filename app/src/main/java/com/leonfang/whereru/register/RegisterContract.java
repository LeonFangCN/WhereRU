package com.leonfang.whereru.register;

import com.leonfang.whereru.base.BasePresenter;
import com.leonfang.whereru.base.BaseView;
import com.leonfang.whereru.data.source.remote.User;

import java.io.File;

/**
 * Created by LeonFang on 2017/3/30.
 */

public interface RegisterContract {
    interface View extends BaseView{
        void startMainActivity();
        void registerSuccess();
        void registerFail(String s);
        void showProgressDialog(String msg);
        void dismissProgressDialog();
    }
    interface Presenter extends BasePresenter{
        void register(String username, String password,String nickname,File avatar);
        void register(User user);
    }
}
