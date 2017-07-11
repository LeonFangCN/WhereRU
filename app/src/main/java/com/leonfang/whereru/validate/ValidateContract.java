package com.leonfang.whereru.validate;

import com.leonfang.whereru.base.BasePresenter;
import com.leonfang.whereru.base.BaseView;
import com.leonfang.whereru.data.source.remote.User;

import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by LeonFang on 2017/5/4.
 */

interface ValidateContract {
    interface View extends BaseView{

    }
    interface Presenter extends BasePresenter{
        void queryUser(String account,FindListener<User> listener);
        void modifyPassword(User user,String password,UpdateListener updateListener);
        void login(String username, String password,LogInListener logInListener);
    }
}
