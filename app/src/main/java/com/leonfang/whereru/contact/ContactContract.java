package com.leonfang.whereru.contact;

import com.leonfang.whereru.base.BasePresenter;
import com.leonfang.whereru.base.BaseView;
import com.leonfang.whereru.data.source.remote.Friend;

import java.util.List;

/**
 * Created by LeonFang on 2017/4/12.
 */

interface ContactContract{
    interface View extends BaseView{
        void queryFriendsSuccess(List<Friend> list);
        void queryFriendsFail(String s);
    }
    interface Presenter extends BasePresenter{
        void queryFriends();
    }
}
