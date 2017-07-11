package com.leonfang.whereru.addfriend;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.leonfang.whereru.R;
import com.leonfang.whereru.base.BaseActivity;
import com.leonfang.whereru.data.source.remote.User;
import com.leonfang.whereru.userinfo.UserInfoActivity;
import com.leonfang.whereru.util.Constants;
import com.leonfang.whereru.util.Logger;

import java.util.List;

import butterknife.BindView;

public class AddFriendActivity extends BaseActivity implements AddFriendContract.View {

    @BindView(R.id.txt_not_exists)
    TextView txtNotExists;
    @BindView(R.id.ll_search_user)
    LinearLayout llSearch;
    @BindView(R.id.txt_recent_name)
    TextView txtRecentName;
    private AddFriendPresenter addFriendPresenter;
    private ProgressDialog progressDialog;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        addFriendPresenter = new AddFriendPresenter(this);
    }

    @Override
    protected void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            searchView = new SearchView(this);
            searchView.setQueryHint("手机号");
            getSupportActionBar().setCustomView(searchView);
            getSupportActionBar().setDisplayShowCustomEnabled(true);
            //自动弹出输入法
            searchView.setIconifiedByDefault(true);
            searchView.setFocusable(true);
            searchView.setIconified(false);
            searchView.requestFocusFromTouch();
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    Logger.e("AddFriendActivity", query);
                    addFriendPresenter.queryUsers(query);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    llSearch.setVisibility(TextUtils.isEmpty(newText) ? View.GONE : View.VISIBLE);
                    txtRecentName.setText(newText);
                    txtNotExists.setVisibility(View.GONE);
                    return true;
                }
            });
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        llSearch.setVisibility(View.GONE);
        llSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFriendPresenter.queryUsers(txtRecentName.getText().toString());
                searchView.clearFocus();
            }
        });
    }

    @Override
    public void showSearchResult(User user, boolean isFriend) {
        progressDialog.dismiss();
        Intent intent = new Intent(AddFriendActivity.this, UserInfoActivity.class);
        intent.putExtra(Constants.USER, user);
        intent.putExtra(Constants.IS_FRIEND,isFriend);
        startActivity(intent);
    }

    @Override
    public void queryUsersError(String s) {
        txtNotExists.setVisibility(View.VISIBLE);
        llSearch.setVisibility(View.GONE);
        progressDialog.dismiss();
    }

    @Override
    public void showProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在查找联系人");
        progressDialog.show();
    }

}
