package com.leonfang.whereru.util;

import android.util.Log;

import com.leonfang.whereru.Config;

/**
 * Created by LeonFang on 2017/3/30.
 */

public class Logger {
    /**
     * 是否开启debug
     */
    public static boolean isDebug= Config.DEBUG;



    public static void e(Class<?> clazz,String msg){
        if(isDebug){
            Log.e(clazz.getSimpleName(), msg+"");
        }
    }

    public static void i(Class<?> clazz,String msg){
        if(isDebug){
            Log.i(clazz.getSimpleName(), msg+"");
        }
    }

    public static void w(Class<?> clazz,String msg) {
        if (isDebug) {
            Log.w(clazz.getSimpleName(), msg + "");
        }
    }

    public static void e(String string,String msg){
        if(isDebug){
            Log.e(string, msg+"");
        }
    }

    public static void i(String string,String msg){
        if(isDebug){
            Log.i(string, msg+"");
        }
    }

    public static void w(String string,String msg) {
        if (isDebug) {
            Log.w(string, msg + "");
        }
    }
}
