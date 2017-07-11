package com.leonfang.whereru.data.source.remote;

import com.leonfang.whereru.data.source.local.NewFriend;

import java.io.File;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobGeoPoint;

/**
 * Created by LeonFang on 2017/3/30.
 */

public class User extends BmobUser {

    private String nickname;
    private BmobFile avatar;
    private BmobGeoPoint geoPoint;
    private String location;
    private String mPassword;
    private String locationUpdateTime;

    public String getLocationUpdateTime() {
        return locationUpdateTime;
    }

    public void setLocationUpdateTime(String locationUpdateTime) {
        this.locationUpdateTime = locationUpdateTime;
    }

    public String getmPassword() {
        return mPassword;
    }

    public void setmPassword(String mPassword) {
        this.mPassword = mPassword;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public BmobGeoPoint getGeoPoint() {
        return geoPoint;
    }

    public void setGeoPoint(BmobGeoPoint geoPoint) {
        this.geoPoint = geoPoint;
    }

    public User(){}

    public User(NewFriend friend){
        setObjectId(friend.getUid());
        setUsername(friend.getName());
        setAvatar(new BmobFile(new File(friend.getAvatar())));
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public BmobFile getAvatar() {
        if(avatar==null){
            return new BmobFile();
        }
        return avatar;
    }

    public void setAvatar(BmobFile avatar) {
        this.avatar = avatar;
    }

    @Override
    public String toString() {
        return "User{" +
                "nickname='" + nickname + '\'' +
                ", avatar=" + avatar +
                ", geoPoint=" + geoPoint +
                ", location='" + location + '\'' +
                ", mPassword='" + mPassword + '\'' +
                ", locationUpdateTime='" + locationUpdateTime + '\'' +
                '}';
    }
}
