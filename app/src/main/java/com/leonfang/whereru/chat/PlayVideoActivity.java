package com.leonfang.whereru.chat;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.MediaController;
import android.widget.VideoView;

import com.leonfang.whereru.R;
import com.leonfang.whereru.util.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by LeonFang on 2017/6/15.
 */

public class PlayVideoActivity extends Activity {

    @BindView(R.id.video_view)
    VideoView mVideoView;
    private String path;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        path = getIntent().getStringExtra(Constants.FILE_PATH);
        setContentView(R.layout.activity_play_video);
        ButterKnife.bind(this);
        initView();
    }

    protected void initView() {
        mVideoView.setVideoPath(path);
        mVideoView.setMediaController(new MediaController(this));
        mVideoView.requestFocus();
        mVideoView.start();
    }

}
