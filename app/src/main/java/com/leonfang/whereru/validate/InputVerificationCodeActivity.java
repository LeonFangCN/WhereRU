package com.leonfang.whereru.validate;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.leonfang.whereru.R;
import com.leonfang.whereru.base.BaseActivityWithTitle;
import com.leonfang.whereru.util.Constants;
import com.leonfang.whereru.util.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * Created by LeonFang on 2017/5/5.
 */

public class InputVerificationCodeActivity extends BaseActivityWithTitle {

    @BindView(R.id.tv_send_code)
    TextView tvSendCode;
    @BindView(R.id.tv_tel)
    TextView tvTel;
    @BindView(R.id.tv_code)
    EditText tvCode;
    @BindView(R.id.tv_count)
    TextView tvCount;
    @BindView(R.id.btn_next)
    AppCompatButton btnNext;
    private boolean withoutCheck;
    private String account;
    private Intent intent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_code);
        SMSSDK.registerEventHandler(eventHandler);
    }

    @Override
    protected void initView() {
        withoutCheck = getIntent().getBooleanExtra(Constants.WITHOUT_CHECK, false);
        account = getIntent().getStringExtra(Constants.ACCOUNT);
        tvTel.setText(account);
        tvCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnNext.setEnabled(s.length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        if (withoutCheck) {
            tvSendCode.setText("以下号码为诚信号码，无需验证短信：");
            tvCode.setText("");
            tvCode.setHint("您的手机号码已验证成功");
            tvCode.setEnabled(false);
            btnNext.setEnabled(true);
            tvCount.setVisibility(View.GONE);
        } else {
           new CountDownTimer(60 * 1000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    tvCount.setText(String.format("接收短信大约需要%s秒钟", 60 * 1000 - millisUntilFinished));
                }

                @Override
                public void onFinish() {

                }
            };
        }

    }

    @OnClick(R.id.btn_next)
    public void onViewClicked() {
        intent = getIntent().setClass(this, modifyPasswordActivity.class);
        if (withoutCheck) {
            startActivity(intent);
        } else {
            SMSSDK.submitVerificationCode("+86", account, tvCode.getText().toString());
            showProgressDialog(null);
        }
    }

    EventHandler eventHandler = new EventHandler() {
        @Override
        public void afterEvent(int result, int event, Object data) {
            if (result == SMSSDK.RESULT_COMPLETE) {
                Logger.e(getClass(), "RESULT_COMPLETE" + data.toString());
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    startActivity(intent);
                    Logger.e(getClass(), "提交验证码成功" + data.toString());
                    //提交验证码成功
                }
                dismissProgressDialog();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterAllEventHandler();
    }
}
