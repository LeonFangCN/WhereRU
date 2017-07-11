package com.leonfang.whereru.login;

import com.leonfang.whereru.data.source.remote.User;
import com.leonfang.whereru.base.BasePresenter;
import com.leonfang.whereru.base.BaseView;

/**
 * Created by LeonFang on 2017/3/30.
 */

public interface LoginContract {
    interface View extends BaseView{
        void loginSuccess();
        void loginFail(String s);
    }
    interface Presenter extends BasePresenter{
        void login(String username,String password);
        void login(User user);
    }
}
