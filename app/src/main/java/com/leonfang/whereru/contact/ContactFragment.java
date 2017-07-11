package com.leonfang.whereru.contact;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.leonfang.whereru.R;
import com.leonfang.whereru.base.BaseFragment;
import com.leonfang.whereru.base.IMutlipleItem;
import com.leonfang.whereru.base.OnRecyclerViewListener;
import com.leonfang.whereru.chat.ChatActivity;
import com.leonfang.whereru.data.source.remote.Friend;
import com.leonfang.whereru.data.source.remote.User;
import com.leonfang.whereru.event.RefreshEvent;
import com.leonfang.whereru.newfriend.NewFriendActivity;
import com.leonfang.whereru.userinfo.UserInfoActivity;
import com.leonfang.whereru.util.Constants;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.BindView;
import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMUserInfo;

/**
 * Created by LeonFang on 2017/4/4.
 */

public class ContactFragment extends BaseFragment implements ContactContract.View {

    @BindView(R.id.contact_recycler_view)
    RecyclerView contactRecyclerView;
    private ContactAdapter adapter;
    private LinearLayoutManager layoutManager;
    @BindView(R.id.sw_refresh)
    SwipeRefreshLayout swipeRefreshLayout;
    private ContactPresenter contactPresenter;

    @Override
    protected void initView() {

    }

    @Override
    protected int getContentLayoutRes() {
        return R.layout.fragment_contact;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        contactPresenter = new ContactPresenter(this);
        IMutlipleItem<Friend> mutlipleItem = new IMutlipleItem<Friend>() {
            @Override
            public int getItemViewType(int postion, Friend friend) {
                if (postion == 0) {
                    return ContactAdapter.TYPE_NEW_FRIEND;
                } else {
                    return ContactAdapter.TYPE_ITEM;
                }
            }

            @Override
            public int getItemLayoutId(int viewtype) {
                if (viewtype == ContactAdapter.TYPE_NEW_FRIEND) {
                    return R.layout.header_new_friend;
                } else {
                    return R.layout.item_contact;
                }
            }

            @Override
            public int getItemCount(List<Friend> list) {
                return list.size() + 1;
            }
        };
        adapter = new ContactAdapter(getActivity(), mutlipleItem, null);
        contactRecyclerView.setAdapter(adapter);
        layoutManager = new LinearLayoutManager(getActivity());
        contactRecyclerView.setLayoutManager(layoutManager);
        swipeRefreshLayout.setEnabled(true);
        contactPresenter.queryFriends();
        setListener();
    }

    private void setListener() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                contactPresenter.queryFriends();
            }
        });
        adapter.setOnRecyclerViewListener(new OnRecyclerViewListener() {
            @Override
            public void onItemClick(int position) {
                if (position == 0) {//跳转到新朋友页面
                    startActivity(NewFriendActivity.class, null);
                } else {
                    Intent intent = new Intent(getActivity(), UserInfoActivity.class);
                    Friend friend = adapter.getItem(position);
                    intent.putExtra(Constants.USER, friend);
                    intent.putExtra(Constants.IS_FRIEND,true);
                    startActivity(intent);
                }
            }

            @Override
            public boolean onItemLongClick(final int position) {
                // TODO: 2017/4/12 删除好友
                return true;
            }
        });
    }

    @Override
    public void queryFriendsSuccess(List<Friend> list) {
        adapter.bindDatas(list);
        adapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void queryFriendsFail(String s) {
        adapter.bindDatas(null);
        adapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        swipeRefreshLayout.setRefreshing(true);
        contactPresenter.queryFriends();
    }

    /**
     * 注册自定义消息接收事件
     *
     * @param event
     */
    @Subscribe
    public void onEventMainThread(RefreshEvent event) {
        swipeRefreshLayout.setRefreshing(true);
        contactPresenter.queryFriends();
        //重新刷新列表
//        adapter.notifyDataSetChanged();
    }
}
