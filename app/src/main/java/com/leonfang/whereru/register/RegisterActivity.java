package com.leonfang.whereru.register;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.leonfang.whereru.main.MainActivity;
import com.leonfang.whereru.PickPhotoDialog;
import com.leonfang.whereru.R;
import com.leonfang.whereru.base.BaseActivity;
import com.leonfang.whereru.util.Constants;
import com.leonfang.whereru.util.FileUtil;

import butterknife.BindView;
import butterknife.OnClick;

public class RegisterActivity extends BaseActivity implements RegisterContract.View {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.input_account)
    EditText inputAccount;
    @BindView(R.id.input_password)
    EditText inputPassword;
    @BindView(R.id.btn_confirm)
    AppCompatButton btnConfirm;
    @BindView(R.id.input_nickname)
    EditText inputNickname;
    @BindView(R.id.img_avatar)
    ImageView imgAvatar;
    private RegisterContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        presenter = new RegisterPresenter(this);
    }

    @Override
    protected void initView() {
        setAvatarView(imgAvatar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
        inputNickname.requestFocus();
        String account = getIntent().getStringExtra(Constants.ACCOUNT);
        inputAccount.setText(account);
    }

    private void showDialog() {
        MaterialDialog dialog = new MaterialDialog.Builder(RegisterActivity.this).content("放弃注册？").positiveText("确定").negativeText("取消").callback
                (new MaterialDialog.Callback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        finish();
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        dialog.dismiss();
                    }
                }).build();
        dialog.show();
    }
    @OnClick({R.id.btn_confirm, R.id.img_avatar})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_confirm:
                final String account = inputAccount.getText().toString();
                final String password = inputPassword.getText().toString();
                final String nickname = inputNickname.getText().toString();
                presenter.register(account, password, nickname, FileUtil.getAvatarFile(getFilename()));
                break;
            case R.id.img_avatar:
                new PickPhotoDialog(this).show();
                break;
        }
    }

    @Override
    public void startMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    public void registerSuccess() {
        finish();
    }

    @Override
    public void registerFail(String s) {
        toast(s);
    }

    private ProgressDialog progressDialog;

    @Override
    public void showProgressDialog(String msg) {
        progressDialog = new ProgressDialog(this);
        if (!TextUtils.isEmpty(msg)) {
            progressDialog.setMessage(msg);
        }
        progressDialog.show();
    }

    @Override
    public void dismissProgressDialog() {
        progressDialog.dismiss();
    }

    @Override
    public void onBackPressed() {
        showDialog();
    }

}
