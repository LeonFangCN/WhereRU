package com.leonfang.whereru.addfriend;

import com.leonfang.whereru.data.source.remote.Friend;
import com.leonfang.whereru.data.source.remote.User;
import com.leonfang.whereru.model.BaseModel;
import com.leonfang.whereru.model.UserModel;
import com.leonfang.whereru.userinfo.UserInfoContract;

import java.util.List;

import cn.bmob.v3.listener.FindListener;

/**
 * Created by LeonFang on 2017/4/14.
 */

public class AddFriendPresenter implements AddFriendContract.Presenter{
    private AddFriendContract.View view;
    private UserModel userModel;

    public AddFriendPresenter(AddFriendContract.View view) {
        this.view = view;
        this.userModel=UserModel.getInstance();
    }

    @Override
    public void queryUsers(String account) {
        view.showProgressDialog();
        userModel.queryUsers(account, BaseModel.DEFAULT_LIMIT, new FindListener<User>() {
            @Override
            public void onSuccess(List<User> list) {
                final User user = list.get(0);
                //查询的是用户自己
                if(userModel.getCurrentUser().getUsername().equals(user.getUsername())){
                    view.showSearchResult(user,true);
                    return;
                }
                //验证是否已经是好友
                userModel.queryFriends(new FindListener<Friend>() {
                    @Override
                    public void onSuccess(List<Friend> list) {

                        for (Friend friend : list) {
                            if (friend.getFriendUser().getUsername().equals(user.getUsername())){
                                view.showSearchResult(user,true);
                                return;
                            }
                        }

                        //可以被添加的用户
                        view.showSearchResult(user,false);
                    }
                    @Override
                    public void onError(int i, String s) {
                        //可以被添加的用户
                        view.showSearchResult(user,false);
                    }
                });

            }
            @Override
            public void onError(int i, String s) {
                view.queryUsersError(s);
            }
        });
    }

    @Override
    public void start() {

    }
}
