package com.leonfang.whereru.register;

import com.leonfang.whereru.data.source.remote.User;
import com.leonfang.whereru.event.FinishEvent;
import com.leonfang.whereru.model.BaseModel;
import com.leonfang.whereru.model.UserModel;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;

/**
 * Created by LeonFang on 2017/4/4.
 */

public class RegisterPresenter implements RegisterContract.Presenter {
    private final UserModel userModel;
    private final RegisterContract.View view;

    public RegisterPresenter(RegisterContract.View view) {
        this.userModel = UserModel.getInstance();
        this.view = view;
    }

    @Override
    public void register(String username, String password, String nickname, File avatar) {
        view.showProgressDialog(null);
        userModel.register(username, password, nickname , avatar, new LogInListener() {
            @Override
            public void done(Object o, BmobException e) {
                view.dismissProgressDialog();
                if (e == null) {
                    view.startMainActivity();
                    view.registerSuccess();
                } else {
                    view.registerFail(e.getMessage() + "(" + e.getErrorCode() + ")");
                }
            }
        });
    }

    @Override
    public void register(User user) {

    }

    @Override
    public void start() {

    }
}
