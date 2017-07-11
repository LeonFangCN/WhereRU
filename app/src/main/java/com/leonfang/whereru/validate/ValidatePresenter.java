package com.leonfang.whereru.validate;

import com.leonfang.whereru.data.source.remote.User;
import com.leonfang.whereru.model.UserModel;

import java.util.List;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by LeonFang on 2017/5/4.
 */

public class ValidatePresenter implements ValidateContract.Presenter{
    private UserModel userModel;
    private ValidateContract.View view;

    public ValidatePresenter(ValidateContract.View view) {
        this.view = view;
        userModel=UserModel.getInstance();
    }
    @Override
    public void queryUser(String account,FindListener<User> listener){
        userModel.queryUsers(account,1,listener);
    }

    @Override
    public void modifyPassword(User user, String password, UpdateListener updateListener) {
        userModel.modifyPassword(user,password,updateListener);
    }

    @Override
    public void login(String username, String password,LogInListener logInListener) {
        UserModel.getInstance().login(username, password,logInListener);
    }
    @Override
    public void start() {

    }

}
