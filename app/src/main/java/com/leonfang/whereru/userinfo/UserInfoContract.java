package com.leonfang.whereru.userinfo;

import android.os.Bundle;

import com.leonfang.whereru.base.BasePresenter;
import com.leonfang.whereru.base.BaseView;
import com.leonfang.whereru.data.source.remote.Friend;
import com.leonfang.whereru.data.source.remote.User;

import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.v3.listener.DeleteListener;

/**
 * Created by LeonFang on 2017/4/14.
 */

public interface UserInfoContract {
    interface View extends BaseView{
        void sendAddFriendMessageSuccess();
        void sendAddFriendMessageFail(String s);
    }
    interface Presenter extends BasePresenter{
        void sendAddFriendMessage(User user);
        void deleteFriend(Friend friend, DeleteListener deleteListener);
        Bundle startPrivateConversation(User user);
    }
}
