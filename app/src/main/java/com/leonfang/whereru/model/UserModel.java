package com.leonfang.whereru.model;

import android.content.Context;
import android.text.TextUtils;

import com.leonfang.whereru.data.source.remote.Friend;
import com.leonfang.whereru.data.source.remote.User;
import com.leonfang.whereru.model.i.QueryUserListener;
import com.leonfang.whereru.model.i.UpdateCacheListener;
import com.leonfang.whereru.util.FileUtil;
import com.leonfang.whereru.util.Logger;

import java.io.File;
import java.util.List;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetListener;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * @author :smile
 * @project:UserModel
 * @date :2016-01-22-18:09
 */
public class UserModel extends BaseModel {

    private static UserModel ourInstance = new UserModel();

    public static UserModel getInstance() {
        return ourInstance;
    }

    private UserModel() {
    }

    /**
     * 登录
     *
     * @param username
     * @param password
     * @param listener
     */
    public void login(String username, String password, final LogInListener listener) {
        if (TextUtils.isEmpty(username)) {
            listener.internalDone(new BmobException(CODE_NULL, "请填写用户名"));
            return;
        }
        if (TextUtils.isEmpty(password)) {
            listener.internalDone(new BmobException(CODE_NULL, "请填写密码"));
            return;
        }
        final User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.login(getContext(), new SaveListener() {
            @Override
            public void onSuccess() {
                listener.done(getCurrentUser(), null);
            }

            @Override
            public void onFailure(int i, String s) {
                listener.done(user, new BmobException(i, s));
            }
        });
    }

    /**
     * 退出登录
     */
    public void logout() {
        BmobUser.logOut(getContext());
    }

    public User getCurrentUser() {
        return BmobUser.getCurrentUser(getContext(), User.class);
    }

    /**
     * @param username
     * @param password
     * @param listener
     */
    public void register(String username, String password, String nickname, File avatar, final LogInListener listener) {
        if (TextUtils.isEmpty(username)) {
            listener.internalDone(new BmobException(CODE_NULL, "请填写用户名"));
            return;
        }
        if (TextUtils.isEmpty(password)) {
            listener.internalDone(new BmobException(CODE_NULL, "请填写密码"));
            return;
        }
        if (TextUtils.isEmpty(nickname)) {
            listener.internalDone(new BmobException(CODE_NULL, "请填写昵称"));
            return;
        }
        if (!avatar.exists()) {
            listener.internalDone(new BmobException(CODE_NULL, "请设置头像"));
            return;
        }
        final User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setNickname(nickname);
        user.setMobilePhoneNumber(username);
        user.setMobilePhoneNumberVerified(true);
        user.setmPassword(password);
        final BmobFile bmobFile = new BmobFile(avatar);
        bmobFile.upload(getContext(), new UploadFileListener() {
            @Override
            public void onSuccess() {
                user.setAvatar(bmobFile);
                user.signUp(getContext(), new SaveListener() {
                    @Override
                    public void onSuccess() {
                        listener.done(null, null);
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        listener.done(null, new BmobException(i, s));
                    }
                });
            }

            @Override
            public void onFailure(int i, String s) {

            }
        });
    }

    /**
     * 查询用户
     *
     * @param username
     * @param limit
     * @param listener
     */
    public void queryUsers(String username, int limit, final FindListener<User> listener) {
        BmobQuery<User> query = new BmobQuery<>();
        query.addWhereEqualTo("username", username);
        query.setLimit(limit);
        query.order("-createdAt");
        query.findObjects(getContext(), new FindListener<User>() {
            @Override
            public void onSuccess(List<User> list) {
                if (list != null && list.size() > 0) {
                    listener.onSuccess(list);
                } else {
                    listener.onError(CODE_NULL, "查无此人");
                }
            }

            @Override
            public void onError(int i, String s) {
                listener.onError(i, s);
            }
        });
    }

    /**
     * 查询用户信息
     *
     * @param objectId
     * @param listener
     */
    public void queryUserInfo(String objectId, final QueryUserListener listener) {
        BmobQuery<User> query = new BmobQuery<>();
        query.addWhereEqualTo("objectId", objectId);
        query.findObjects(getContext(), new FindListener<User>() {
            @Override
            public void onSuccess(List<User> list) {
                if (list != null && list.size() > 0) {
                    listener.internalDone(list.get(0), null);
                } else {
                    listener.internalDone(new BmobException(000, "查无此人"));
                }
            }

            @Override
            public void onError(int i, String s) {
                listener.internalDone(new BmobException(i, s));
            }
        });
    }

    /**
     * 更新用户资料和会话资料
     *
     * @param event
     * @param listener
     */
    public void updateUserInfo(MessageEvent event, final UpdateCacheListener listener) {
        final BmobIMConversation conversation = event.getConversation();
        final BmobIMUserInfo info = event.getFromUserInfo();
        final BmobIMMessage msg = event.getMessage();
        String username = info.getName();
        String title = conversation.getConversationTitle();
        Logger.i(this.getClass(), "" + username + "," + title);
        //sdk内部，将新会话的会话标题用objectId表示，因此需要比对用户名和会话标题--单聊，后续会根据会话类型进行判断
        if (!username.equals(title)) {
            UserModel.getInstance().queryUserInfo(info.getUserId(), new QueryUserListener() {
                @Override
                public void done(User s, BmobException e) {
                    if (e == null) {
                        String name = s.getUsername();
                        String avatar = s.getAvatar().getUrl();
                        Logger.i(this.getClass(), "query success：" + name + "," + avatar);
                        conversation.setConversationIcon(avatar);
                        conversation.setConversationTitle(name);
                        info.setName(name);
                        info.setAvatar(avatar);
                        //更新用户资料
                        BmobIM.getInstance().updateUserInfo(info);
                        //更新会话资料-如果消息是暂态消息，则不更新会话资料
                        if (!msg.isTransient()) {
                            BmobIM.getInstance().updateConversation(conversation);
                        }
                    } else {
                        Logger.e(this.getClass(), e.toString());
                    }
                    listener.done(null);
                }
            });
        } else {
            listener.internalDone(null);
        }
    }

    /**
     * 同意添加好友：1、发送同意添加的请求，2、添加对方到自己的好友列表中
     */
    public void agreeAddFriend(final User friend, final SaveListener listener) {
        final User user = BmobUser.getCurrentUser(getContext(), User.class);
        BmobQuery<Friend> query = new BmobQuery<>();
        query.addWhereEqualTo("user", user).addWhereEqualTo("friendUser", friend).findObjects(getContext(), new FindListener<Friend>() {
            @Override
            public void onSuccess(List<Friend> list) {
                if (list.size() == 0) {
                    Friend f = new Friend();
                    f.setUser(user);
                    f.setFriendUser(friend);
                    f.save(getContext(), listener);
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });
        Logger.e("UserModel,agreeAddFriend", "user.getUsername()" + user.getObjectId());
        Logger.e("UserModel,agreeAddFriend", "friend.getUsername()" + friend.getObjectId());
    }


    /**
     * 查询好友
     *
     * @param listener
     */
    public void queryFriends(final FindListener<Friend> listener) {
        BmobQuery<Friend> query = new BmobQuery<>();
        User user = BmobUser.getCurrentUser(getContext(), User.class);
        query.addWhereEqualTo("user", user);
        query.include("friendUser");
        query.order("-updatedAt");
        query.findObjects(getContext(), new FindListener<Friend>() {
            @Override
            public void onSuccess(List<Friend> list) {
                if (list != null && list.size() > 0) {
                    listener.onSuccess(list);
                } else {
                    listener.onError(0, "暂无联系人");
                }
            }

            @Override
            public void onError(int i, String s) {
                listener.onError(i, s);
            }
        });
    }

    /**
     * 删除好友
     *
     * @param f
     * @param listener
     */
    public void deleteFriend(Friend f, DeleteListener listener) {
        Friend friend = new Friend();
        friend.delete(getContext(), f.getObjectId(), listener);
    }

    public void updateCurrentUserAvatar(File avatar, final UpdateListener updateListener) {
        final User currentUser = getCurrentUser();

        final BmobFile bmobFile = new BmobFile(avatar);
        bmobFile.upload(getContext(), new UploadFileListener() {
            @Override
            public void onSuccess() {
                currentUser.setAvatar(bmobFile);
                currentUser.update(getContext(), updateListener);
            }

            @Override
            public void onFailure(int i, String s) {

            }
        });
    }

    public void uploadGeoPoint(BmobGeoPoint currentGeoPoint, String location,String time) {
        User user = BmobUser.getCurrentUser(getContext(), User.class);
        user.setGeoPoint(currentGeoPoint);
        user.setLocation(location);
        user.setLocationUpdateTime(time);
        user.update(getContext(), new UpdateListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int i, String s) {

            }
        });
    }

    public void modifyPassword(final User user, final String password, final UpdateListener updateListener) {
        user.setPassword(user.getmPassword());
        user.login(getContext(), new SaveListener() {
            @Override
            public void onSuccess() {
                Logger.e(getClass(),"modifyPassword-"+password);
                user.setPassword(password);
                user.setmPassword(password);
                Logger.e(getClass(),"modifyPassword:"+user.toString());
                user.update(getContext(),updateListener);
                updateListener.onSuccess();
            }

            @Override
            public void onFailure(int i, String s) {
                updateListener.onFailure(i,s);
                Logger.e(getClass(),s);
            }
        });
    }
}
