package com.leonfang.whereru.main;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.leonfang.whereru.PickPhotoDialog;
import com.leonfang.whereru.R;
import com.leonfang.whereru.WhereRUApplication;
import com.leonfang.whereru.addfriend.AddFriendActivity;
import com.leonfang.whereru.base.BaseActivity;
import com.leonfang.whereru.contact.ContactFragment;
import com.leonfang.whereru.conversation.ConversationFragment;
import com.leonfang.whereru.data.source.remote.User;
import com.leonfang.whereru.event.RefreshEvent;
import com.leonfang.whereru.login.LoginActivity;
import com.leonfang.whereru.login.LoginContract;
import com.leonfang.whereru.map.MapFragment;
import com.leonfang.whereru.model.UserModel;
import com.leonfang.whereru.service.WhereRUService;
import com.leonfang.whereru.util.CircleImageViewTarget;
import com.leonfang.whereru.util.FileUtil;
import com.leonfang.whereru.util.Logger;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import cn.bmob.newim.BmobIM;
import cn.bmob.newim.listener.ConnectListener;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;

public class MainActivity extends BaseActivity implements MainContract.View, PopupMenu.OnMenuItemClickListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fragment_container)
    FrameLayout fragmentContainer;
    @BindView(R.id.rb_content_fragment_contact)
    RadioButton rbContentFragmentContact;
    @BindView(R.id.rb_content_fragment_map)
    RadioButton rbContentFragmentMap;
    @BindView(R.id.rg_content_fragment)
    RadioGroup rgContentFragment;
    @BindView(R.id.navigation)
    NavigationView navigation;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.activity_main)
    LinearLayout activityMain;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private ImageView userIcon;
    private TextView usernameView;
    private MainPresenter mainPresenter;
    private int currentTabIndex;
    private Intent service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainPresenter = new MainPresenter(this);
        mainPresenter.getUserInfo();
        mainPresenter.initBmobIm();
        mainPresenter.getUpdateInfo(false);
        service = new Intent(this, WhereRUService.class);
        startService(service);
        getPersimmions();
    }

    @Override
    protected void initView() {
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, 0, 0);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        initFragmentContainer();
        userIcon = (ImageView) navigation.getHeaderView(0).findViewById(R.id.user_icon);
        usernameView = (TextView) navigation.getHeaderView(0).findViewById(R.id.username);
        userIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PickPhotoDialog(MainActivity.this).show();
            }
        });
        setAvatarView(userIcon);
        navigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.drawer_logout:
                        stopService(service);
                        mainPresenter.logout();
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        finish();
                        break;
                    case R.id.check_update:
                        showProgressDialog(null);
                        mainPresenter.getUpdateInfo(true);
                }
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        actionBarDrawerToggle.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.more:
                showPopupMenu();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showPopupMenu() {
        PopupMenu popupMenu = new PopupMenu(this, findViewById(R.id.more));
        popupMenu.getMenuInflater().inflate(R.menu.popup_menu_more, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(this);
        //使用反射，强制显示菜单图标
        try {
            Field field = popupMenu.getClass().getDeclaredField("mPopup");
            field.setAccessible(true);
            MenuPopupHelper mHelper = (MenuPopupHelper) field.get(popupMenu);
            mHelper.setForceShowIcon(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        popupMenu.show();
    }

    List<Integer> radioButtonIDs = Arrays.asList(R.id.rb_content_fragment_conversation, R.id.rb_content_fragment_contact, R.id
            .rb_content_fragment_map);

    private void initFragmentContainer() {
        fragments.add(new ConversationFragment());
        fragments.add(new ContactFragment());
        fragments.add(new MapFragment());
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, fragments.get(0))
                .add(R.id.fragment_container, fragments.get(1))
                .add(R.id.fragment_container, fragments.get(2))
                .hide(fragments.get(0))
                .hide(fragments.get(1))
                .hide(fragments.get(2))
                .show(fragments.get(0)).commit();

        rgContentFragment.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                onTabIndex(radioButtonIDs.indexOf(i));
            }
        });
        rgContentFragment.check(radioButtonIDs.get(0));

    }

    private void onTabIndex(int index) {
        if (currentTabIndex != index) {
            FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
            trx.hide(fragments.get(currentTabIndex));
            if (!fragments.get(index).isAdded()) {
                trx.add(R.id.fragment_container, fragments.get(index));
            }
            trx.show(fragments.get(index)).commit();
        }
        currentTabIndex = index;
    }


    private ArrayList<Fragment> fragments = new ArrayList<>();

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        moveTaskToBack(false);
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void showUserInfo(User user) {
        usernameView.setText(user.getNickname());
        Glide.with(this).load(user.getAvatar().getUrl()).asBitmap().into(new CircleImageViewTarget(userIcon));
    }

    @Override
    public void updateUserInfoFail(String string) {
        Logger.e(getClass(), string);
    }

    @Override
    public void connectStatusChanged(String s) {
        Logger.e(getClass(), s);
    }

    @Override
    public void showUpdateDialog(String s, boolean needUpdate) {
        dismissProgressDialog();
        if (needUpdate) {
            new MaterialDialog.Builder(this).content(s).title("新版本信息").positiveText("更新").negativeText("取消")
                    .callback(new MaterialDialog.Callback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            mainPresenter.downloadUpdate(MainActivity.this);
                        }

                        @Override
                        public void onNegative(MaterialDialog dialog) {
                            dialog.dismiss();
                        }
                    }).build().show();
        } else {
            toast(s);
        }
    }

    @Override
    protected void setAvatar() {
        super.setAvatar();
        mainPresenter.updateCurrentUserAvatar(FileUtil.getAvatarFile(getFilename()));
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_friends:
                startActivity(new Intent(this, AddFriendActivity.class));
                break;
        }
        return true;
    }


    private final int SDK_PERMISSION_REQUEST = 127;
    private String permissionInfo;

    private void getPersimmions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> permissions = new ArrayList<String>();
            /***
             * 定位权限为必须权限，用户如果禁止，则每次进入都会申请
             */
            // 定位精确位置
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
            // 读写权限
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.RECORD_AUDIO);
            }
            /*
             * 电话状态权限非必要权限(建议授予)只会申请一次，用户同意或者禁止，只会弹一次
			 */
            // 读取电话状态权限
            if (addPermission(permissions, Manifest.permission.READ_PHONE_STATE)) {
                permissionInfo += "Manifest.permission.READ_PHONE_STATE Deny \n";
            }
            if (permissions.size() > 0) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), SDK_PERMISSION_REQUEST);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean addPermission(ArrayList<String> permissionsList, String permission) {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) { // 如果应用没有获得对应权限,则添加到列表中,准备批量申请
            if (shouldShowRequestPermissionRationale(permission)) {
                return true;
            } else {
                permissionsList.add(permission);
                return false;
            }

        } else {
            return true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mainPresenter.initBmobIm();
    }
}
