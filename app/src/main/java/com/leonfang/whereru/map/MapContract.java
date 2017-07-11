package com.leonfang.whereru.map;

import com.baidu.location.BDLocation;
import com.leonfang.whereru.base.BasePresenter;
import com.leonfang.whereru.base.BaseView;
import com.leonfang.whereru.data.source.remote.Friend;
import com.leonfang.whereru.data.source.remote.User;

import java.util.List;

/**
 * Created by LeonFang on 2017/4/17.
 */

public interface MapContract {
    interface View extends BaseView{
        void queryFriendsSuccess(List<Friend> list);
        void queryFriendsFail(String s);
        void locateCurrentUser(BDLocation location);
    }
    interface Presenter extends BasePresenter{
        void queryFriends();
    }
}
