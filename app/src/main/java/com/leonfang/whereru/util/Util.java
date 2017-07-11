package com.leonfang.whereru.util;

import android.util.TypedValue;

import com.leonfang.whereru.WhereRUApplication;

/**
 * @author smile
 * @project Util
 * @date 2016-03-01-14:55
 */
public class Util {
    public static boolean checkSdCard() {
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }

    public static int px2dp(int px) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, px, WhereRUApplication.INSTANCE().getResources().getDisplayMetrics());
    }
}
