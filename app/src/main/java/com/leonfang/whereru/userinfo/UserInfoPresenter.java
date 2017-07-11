package com.leonfang.whereru.userinfo;

import android.os.Bundle;

import com.leonfang.whereru.WhereRUApplication;
import com.leonfang.whereru.data.source.remote.AddFriendMessage;
import com.leonfang.whereru.data.source.remote.Friend;
import com.leonfang.whereru.data.source.remote.User;
import com.leonfang.whereru.model.UserModel;
import com.leonfang.whereru.util.Constants;

import java.util.HashMap;
import java.util.Map;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.BmobIMClient;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DeleteListener;

/**
 * Created by LeonFang on 2017/4/14.
 */

public class UserInfoPresenter implements UserInfoContract.Presenter {
    UserInfoContract.View view;
    UserModel userModel;

    public UserInfoPresenter(UserInfoContract.View view) {
        this.view = view;
        this.userModel = UserModel.getInstance();
    }

    @Override
    public void sendAddFriendMessage(User user) {
        BmobIMUserInfo info = new BmobIMUserInfo(user.getObjectId(), user.getUsername(), user.getAvatar().getUrl());
        //启动一个会话，如果isTransient设置为true,则不会创建在本地会话表中创建记录，
        //设置isTransient设置为false,则会在本地数据库的会话列表中先创建（如果没有）与该用户的会话信息，且将用户信息存储到本地的用户表中
        BmobIMConversation c = BmobIM.getInstance().startPrivateConversation(info, true, null);
        //这个obtain方法才是真正创建一个管理消息发送的会话
        BmobIMConversation conversation = BmobIMConversation.obtain(BmobIMClient.getInstance(), c);
        AddFriendMessage msg = new AddFriendMessage();
        User currentUser = BmobUser.getCurrentUser(WhereRUApplication.INSTANCE(), User.class);
        msg.setContent("很高兴认识你，可以加个好友吗?");//给对方的一个留言信息
        Map<String, Object> map = new HashMap<>();
        map.put("name", currentUser.getUsername());//发送者姓名，这里只是举个例子，其实可以不需要传发送者的信息过去
        map.put("avatar", currentUser.getAvatar());//发送者的头像
        map.put("uid", currentUser.getObjectId());//发送者的uid
        msg.setExtraMap(map);
        conversation.sendMessage(msg, new MessageSendListener() {
            @Override
            public void done(BmobIMMessage msg, BmobException e) {
                if (e == null) {//发送成功
                    view.sendAddFriendMessageSuccess();
                } else {//发送失败
                    view.sendAddFriendMessageFail(e.getMessage());
                }
            }
        });
    }

    @Override
    public void deleteFriend(Friend friend, DeleteListener deleteListener) {
        userModel.deleteFriend(friend,deleteListener);
    }

    @Override
    public Bundle startPrivateConversation(User user) {
        BmobIMUserInfo info = new BmobIMUserInfo(user.getObjectId(), user.getNickname(), user.getAvatar().getUrl());
        //启动一个会话，实际上就是在本地数据库的会话列表中先创建（如果没有）与该用户的会话信息，且将用户信息存储到本地的用户表中
        BmobIMConversation c = BmobIM.getInstance().startPrivateConversation(info, null);
        Bundle bundle = new Bundle();
        bundle.putSerializable("c", c);
        bundle.putSerializable(Constants.USER, user);
        return  bundle;
    }


    @Override
    public void start() {

    }
}
