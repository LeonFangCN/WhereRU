package com.leonfang.whereru.userinfo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.leonfang.whereru.R;
import com.leonfang.whereru.base.BaseActivity;
import com.leonfang.whereru.chat.ChatActivity;
import com.leonfang.whereru.data.source.remote.Friend;
import com.leonfang.whereru.data.source.remote.User;
import com.leonfang.whereru.register.RegisterActivity;
import com.leonfang.whereru.util.CircleImageViewTarget;
import com.leonfang.whereru.util.Constants;
import com.leonfang.whereru.util.Logger;

import java.lang.reflect.Field;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bmob.v3.listener.DeleteListener;

/**
 * Created by LeonFang on 2017/4/14.
 */

public class UserInfoActivity extends BaseActivity implements UserInfoContract.View {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.iv_recent_avatar)
    ImageView ivRecentAvatar;
    @BindView(R.id.tv_recent_name)
    TextView tvRecentName;
    @BindView(R.id.tv_nickname)
    TextView tvNickname;
    @BindView(R.id.tv_tel)
    TextView tvTel;
    @BindView(R.id.btn_add_friend)
    AppCompatButton btnAddFriend;
    @BindView(R.id.cv_location)
    CardView cvLocation;
    @BindView(R.id.tv_location)
    TextView tvLocation;
    @BindView(R.id.btn_send_msg)
    Button btnSendMsg;
    private UserInfoPresenter userInfoPresenter;
    private User user;
    private boolean isFriend;
    private Friend friend;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activiy_user_info);
        userInfoPresenter = new UserInfoPresenter(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_info, menu);
        return isFriend && friend != null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_friend:
                showDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDialog() {
        MaterialDialog dialog = new MaterialDialog.Builder(UserInfoActivity.this).content("删除" + user.getNickname() + "？").positiveText("确定")
                .negativeText("取消").callback
                        (new MaterialDialog.Callback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                userInfoPresenter.deleteFriend(friend, new DeleteListener() {
                                    @Override
                                    public void onSuccess() {
                                        toast("删除成功");
                                    }

                                    @Override
                                    public void onFailure(int i, String s) {
                                        toast("删除失败，" + s);
                                    }
                                });
                                finish();
                            }

                            @Override
                            public void onNegative(MaterialDialog dialog) {
                                dialog.dismiss();
                            }
                        }).build();
        dialog.show();
    }

    @Override
    protected void initView() {
        Object object = getIntent().getExtras().get(Constants.USER);
        if (object instanceof Friend) {
            friend = ((Friend) object);
            user = friend.getFriendUser();

        } else {
            user = (User) object;
        }
        isFriend = (boolean) getIntent().getExtras().get(Constants.IS_FRIEND);
        btnAddFriend.setVisibility(isFriend ? View.GONE : View.VISIBLE);
        btnSendMsg.setVisibility(isFriend ? View.VISIBLE : View.GONE);
        cvLocation.setVisibility(isFriend ? View.VISIBLE : View.GONE);
        tvLocation.setText(user.getLocation() + "\n更新时间：" + user.getLocationUpdateTime());
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvRecentName.setText(user.getNickname());
        tvTel.setText(user.getUsername());
        if (null != user.getAvatar()) {
            Glide.with(this).load(user.getAvatar().getUrl()).asBitmap().placeholder(R.drawable.personal_icon_default_avatar).into(new
                    CircleImageViewTarget(ivRecentAvatar));
        }

    }

    @OnClick({R.id.btn_add_friend, R.id.btn_send_msg})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_add_friend:
                userInfoPresenter.sendAddFriendMessage(user);
                break;
            case R.id.btn_send_msg:
                Bundle bundle = userInfoPresenter.startPrivateConversation(user);
                startActivity(ChatActivity.class,bundle);
                break;
        }
        finish();
    }

    @Override
    public void sendAddFriendMessageSuccess() {
        toast("好友请求发送成功，等待验证");
    }

    @Override
    public void sendAddFriendMessageFail(String s) {
        toast(s);
    }
}
