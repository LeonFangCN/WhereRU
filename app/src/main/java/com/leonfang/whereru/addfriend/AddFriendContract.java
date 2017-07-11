package com.leonfang.whereru.addfriend;

import com.leonfang.whereru.base.BasePresenter;
import com.leonfang.whereru.base.BaseView;
import com.leonfang.whereru.data.source.remote.User;

import java.util.List;

/**
 * Created by LeonFang on 2017/4/14.
 */

interface AddFriendContract {
    interface View extends BaseView{
        void showSearchResult(User user, boolean isFriend);
        void queryUsersError(String s);
        void showProgressDialog();
    }
    interface Presenter extends BasePresenter{
        void queryUsers(String account);
    }
}
