package com.leonfang.whereru.util;

import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.widget.ImageView;

import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.leonfang.whereru.WhereRUApplication;

/**
 * Created by LeonFang on 2017/4/12.
 */

public class CircleImageViewTarget extends BitmapImageViewTarget {
    private ImageView imageView;

    public CircleImageViewTarget(ImageView view) {
        super(view);
        this.imageView = view;
    }

    @Override
    protected void setResource(Bitmap resource) {
        RoundedBitmapDrawable circularBitmapDrawable =
                RoundedBitmapDrawableFactory.create(WhereRUApplication.INSTANCE().getResources(), resource);
        circularBitmapDrawable.setCircular(true);
        if (imageView != null) {
            imageView.setImageDrawable(circularBitmapDrawable);
        }
    }
}
