package com.leonfang.whereru.map;

import com.leonfang.whereru.data.source.remote.Friend;
import com.leonfang.whereru.data.source.remote.User;
import com.leonfang.whereru.model.UserModel;

import java.util.List;

import cn.bmob.v3.listener.FindListener;

/**
 * Created by LeonFang on 2017/4/17.
 */

public class MapPresenter implements MapContract.Presenter {
    private MapContract.View view;
    private UserModel userModel;

    public MapPresenter(MapContract.View view) {
        this.view = view;
        this.userModel = UserModel.getInstance();
    }

    @Override
    public void queryFriends() {
        userModel.queryFriends(new FindListener<Friend>() {
            @Override
            public void onSuccess(List<Friend> list) {
                view.queryFriendsSuccess(list);
            }

            @Override
            public void onError(int i, String s) {
                view.queryFriendsFail(s);
            }
        });
    }

    @Override
    public void start() {

    }
}
