package com.leonfang.whereru.contact;

import com.leonfang.whereru.data.source.remote.Friend;
import com.leonfang.whereru.model.UserModel;

import java.util.List;

import cn.bmob.v3.listener.FindListener;

/**
 * Created by LeonFang on 2017/4/12.
 */

public class ContactPresenter implements ContactContract.Presenter {
    private ContactContract.View view;
    private UserModel userModel;

    public ContactPresenter(ContactContract.View view) {
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
