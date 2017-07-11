package com.leonfang.whereru.validate;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.leonfang.whereru.R;
import com.leonfang.whereru.base.BaseActivityWithTitle;
import com.leonfang.whereru.data.source.remote.User;
import com.leonfang.whereru.main.MainActivity;
import com.leonfang.whereru.util.Constants;
import com.leonfang.whereru.util.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by LeonFang on 2017/5/5.
 */

public class modifyPasswordActivity extends BaseActivityWithTitle implements ValidateContract.View {
    @BindView(R.id.et_pwd)
    EditText etPwd;
    @BindView(R.id.et_pwd2)
    EditText etPwd2;
    @BindView(R.id.btn_next)
    AppCompatButton btnNext;
    private ValidatePresenter validatePresenter;
    private User user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_pwd);
        ButterKnife.bind(this);
        validatePresenter = new ValidatePresenter(this);
        user = ((User) getIntent().getExtras().get(Constants.USER));
    }

    @Override
    protected void initView() {

    }

    @OnClick(R.id.btn_next)
    public void onViewClicked() {
        final String pwd = etPwd.getText().toString();
        String pwd2 = etPwd2.getText().toString();
        if (pwd.isEmpty() || pwd2.isEmpty()) {
            toast("密码不能为空");
        } else if (pwd.equals(pwd2)) {
            showProgressDialog(null);
            validatePresenter.modifyPassword(user, pwd, new UpdateListener() {
                @Override
                public void onSuccess() {
                    toast("修改密码成功");
                    Logger.e(getClass(),"修改密码成功");
                    dismissProgressDialog();
                    startActivity(new Intent(modifyPasswordActivity.this, MainActivity.class));
                }

                @Override
                public void onFailure(int i, String s) {
                    toast(s);
                    dismissProgressDialog();
                }
            });
        } else {
            toast("两次输入的密码必须相同");
        }
    }
}

