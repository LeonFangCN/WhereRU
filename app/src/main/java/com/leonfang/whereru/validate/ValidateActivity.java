package com.leonfang.whereru.validate;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.leonfang.whereru.R;
import com.leonfang.whereru.base.BaseActivityWithTitle;
import com.leonfang.whereru.data.source.remote.User;
import com.leonfang.whereru.util.Constants;
import com.leonfang.whereru.util.Logger;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bmob.v3.listener.FindListener;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * Created by LeonFang on 2017/5/4.
 */

public class ValidateActivity extends BaseActivityWithTitle implements ValidateContract.View {
    @BindView(R.id.tv_tel)
    EditText tvTel;
    @BindView(R.id.btn_next)
    AppCompatButton btnNext;
    private ValidatePresenter validatePresenter;
    private User user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validate);
        validatePresenter = new ValidatePresenter(this);
        SMSSDK.registerEventHandler(eh);
    }

    EventHandler eh = new EventHandler() {
        @Override
        public void afterEvent(final int event, final int result, final Object data) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (result == SMSSDK.RESULT_COMPLETE) {

                        if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {

                            Logger.e(getClass(),"提交验证码成功" + data.toString());

                            //提交验证码成功
                        } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                            Intent intent = new Intent(ValidateActivity.this, InputVerificationCodeActivity.class);
                            intent.putExtra(Constants.ACCOUNT, tvTel.getText().toString());
                            intent.putExtra(Constants.WITHOUT_CHECK,(boolean) data);
                            intent.putExtra(Constants.USER,user);
                            startActivity(intent);
                            dismissProgressDialog();
                        }
                    }
                }
            });

        }
    };

    @Override
    protected void initView() {
        btnNext.setEnabled(false);
        tvTel.addTextChangedListener(new TextWatcher() {
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
    }

    @OnClick(R.id.btn_next)
    public void onViewClicked() {
        showProgressDialog(null);
        validatePresenter.queryUser(tvTel.getText().toString(), new FindListener<User>() {
            @Override
            public void onSuccess(List<User> list) {
                user = list.get(0);
                dismissProgressDialog();
                showConfirmDialog();
            }

            @Override
            public void onError(int i, String s) {
                dismissProgressDialog();
                toast("账号不存在");
            }
        });
    }

    private void showConfirmDialog() {
        MaterialDialog dialog = new MaterialDialog.Builder(this).title("确认手机号码").content("我们将发送验证码短信到这个号码：\n" + tvTel.getText().toString())
                .positiveText("确定").negativeText("取消")
                .callback
                        (new MaterialDialog.Callback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                SMSSDK.getVerificationCode("+86", tvTel.getText().toString());
                                dialog.dismiss();
                                showProgressDialog(null);
                            }

                            @Override
                            public void onNegative(MaterialDialog dialog) {
                                dialog.dismiss();
                            }
                        }).build();
        dialog.show();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterAllEventHandler();
    }
}
