package com.leonfang.whereru;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.leonfang.whereru.util.Constants;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by LeonFang on 2017/4/11.
 */

public class PickPhotoDialog extends Dialog {
    public PickPhotoDialog(@NonNull Context context) {
        super(context, R.style.dialog_pick_photo);
        setOwnerActivity((Activity) context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getAttributes().height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().getAttributes().gravity = Gravity.BOTTOM;
        setCanceledOnTouchOutside(true);
        init();
    }

    private void init() {
        setContentView(R.layout.dialog_pick_photo);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_take_photo, R.id.btn_pick_photo, R.id.btn_cancel})
    public void onViewClicked(View view) {
        dismiss();
        switch (view.getId()) {
            case R.id.btn_take_photo:
                Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                getOwnerActivity().startActivityForResult(takeIntent, Constants.REQUESTCODE_TAKE);
                break;
            case R.id.btn_pick_photo:
                Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
                pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                getOwnerActivity().startActivityForResult(pickIntent, Constants.REQUESTCODE_PICK);
                break;
        }
    }
}
