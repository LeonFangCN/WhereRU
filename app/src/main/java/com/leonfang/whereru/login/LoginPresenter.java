package com.leonfang.whereru.login;

import com.leonfang.whereru.data.source.remote.User;
import com.leonfang.whereru.model.UserModel;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;

/**
 * Created by LeonFang on 2017/4/4.
 */

public class LoginPresenter implements LoginContract.Presenter{
    private LoginContract.View view;
    public LoginPresenter(LoginContract.View view) {
        this.view=view;
    }

    @Override
    public void login(String username, String password) {
        UserModel.getInstance().login(username, password, new LogInListener() {
            @Override
            public void done(Object o, BmobException e) {
                if (e == null) {
                    User user =(User)o;
                    BmobIM.getInstance().updateUserInfo(new BmobIMUserInfo(user.getObjectId(), user.getUsername(), user.getAvatar().getUrl()));
                    view.loginSuccess();
                } else {
                    view.loginFail(e.getMessage() + "(" + e.getErrorCode() + ")");
                }
            }
        });
    }

    @Override
    public void login(User user) {

    }

    @Override
    public void start() {

    }
}
