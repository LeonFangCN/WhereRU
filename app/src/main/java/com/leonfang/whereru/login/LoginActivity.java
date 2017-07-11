package com.leonfang.whereru.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.leonfang.whereru.main.MainActivity;
import com.leonfang.whereru.R;
import com.leonfang.whereru.base.BaseActivity;
import com.leonfang.whereru.register.RegisterActivity;
import com.leonfang.whereru.util.Constants;
import com.leonfang.whereru.validate.ValidateActivity;

import java.lang.reflect.Field;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.gui.RegisterPage;

public class LoginActivity extends BaseActivity implements LoginContract.View {

    @BindView(R.id.input_account)
    EditText inputAccount;
    @BindView(R.id.input_password)
    EditText inputPassword;
    @BindView(R.id.btn_login)
    AppCompatButton btnLogin;
    @BindView(R.id.link_signUp)
    TextView linkSignUp;
    @BindView(R.id.link_forget_pwd)
    TextView linkForgetPwd;
    private LoginPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        presenter = new LoginPresenter(this);
    }

    @Override
    protected void initView() {
        SpannableString spannableString = new SpannableString("还没账号？立即注册");
        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                showRegisterPage();
//                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            }
        }, 5, 9, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        linkSignUp.setText(spannableString);
        linkSignUp.setMovementMethod(LinkMovementMethod.getInstance());

        spannableString = new SpannableString("忘记密码");
        spannableString.setSpan(new ClickableSpan() {

            @Override
            public void onClick(View widget) {
                showSMSLoginPage();
            }
        }, 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        linkForgetPwd.setText(spannableString);
        linkForgetPwd.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void showRegisterPage() {
        RegisterPage registerPage = new RegisterPage();
        registerPage.setRegisterCallback(new EventHandler() {
            public void afterEvent(int event, int result, Object data) {
                if (result == SMSSDK.RESULT_COMPLETE) {
                    @SuppressWarnings("unchecked")
                    HashMap<String, Object> phoneMap = (HashMap<String, Object>) data;
                    String country = (String) phoneMap.get("country");
                    String phone = (String) phoneMap.get("phone");
                    Log.e("LoginActivity", country + ":" + phone);
                    Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                    intent.putExtra(Constants.ACCOUNT, phone);
                    startActivity(intent);
                }
            }
        });

        registerPage.show(this);
    }

    private void showSMSLoginPage() {
        startActivity(new Intent(this, ValidateActivity.class));
    }

    @OnClick({R.id.btn_login})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                presenter.login(inputAccount.getText().toString(), inputPassword.getText().toString());
                break;
        }
    }

    @Override
    public void loginSuccess() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public void loginFail(String s) {
        toast(s);
    }

}
