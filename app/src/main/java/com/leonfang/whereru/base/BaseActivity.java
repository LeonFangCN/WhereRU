package com.leonfang.whereru.base;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.leonfang.whereru.R;
import com.leonfang.whereru.util.CircleImageViewTarget;
import com.leonfang.whereru.util.Constants;
import com.leonfang.whereru.util.FileUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.ButterKnife;

/**
 * Created by LeonFang on 2017/3/30.
 */

public abstract class BaseActivity extends AppCompatActivity implements BaseView {

    public String getFilename() {
        return filename;
    }

    private String filename;
    private ProgressDialog progressDialog;

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
        initView();
    }

    protected abstract void initView();

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        ButterKnife.bind(this);
        initView();
    }

    public Bundle getBundle() {
        if (getIntent() != null && getIntent().hasExtra(getPackageName()))
            return getIntent().getBundleExtra(getPackageName());
        else
            return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Subscribe
    public void onEvent(Boolean empty) {

    }

    /**
     * 隐藏软键盘
     */
    public void hideSoftInputView() {
        InputMethodManager manager = ((InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE));
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    public void toast(String content) {
        Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case Constants.REQUESTCODE_PICK:// 直接从相册获取
            case Constants.REQUESTCODE_TAKE:// 调用相机拍照
                try {
                    startPhotoZoom(data.getData());
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                break;
            case Constants.REQUESTCODE_CUTTING:// 取得裁剪后的图片
                if (data != null) {
                    setAvatar();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {
        filename = SystemClock.uptimeMillis() + "";
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true")
                // aspectX aspectY 是宽高的比例
                .putExtra("aspectX", 1)
                .putExtra("aspectY", 1)
                // outputX outputY 是裁剪图片宽高
                .putExtra("outputX", 200)
                .putExtra("outputY", 200)
                .putExtra("scale", true)//黑边
                .putExtra("scaleUpIfNeeded", true)//黑边
                .putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(FileUtil.getAvatarFile(filename)))
                .putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())
                .putExtra("return-data", false);
        startActivityForResult(intent, Constants.REQUESTCODE_CUTTING);
    }

    protected void setAvatar() {
        Glide.with(this).load(FileUtil.getAvatarFile(filename)).asBitmap()
                .placeholder(R.drawable.personal_icon_default_avatar)
                .error(R.drawable.personal_icon_default_avatar)
                .centerCrop().into(new CircleImageViewTarget(avatarView));
    }

    ImageView avatarView;

    public void setAvatarView(ImageView view) {
        this.avatarView = view;
    }

    /**
     * 启动指定Activity
     *
     * @param target
     * @param bundle
     */
    public void startActivity(Class<? extends Activity> target, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(this, target);
        if (bundle != null)
            intent.putExtra(this.getPackageName(), bundle);
        startActivity(intent);
    }

    protected void showProgressDialog(String s) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(s);
        progressDialog.show();
    }

    protected void dismissProgressDialog() {
        if (progressDialog != null)
            progressDialog.dismiss();
    }
}
