package com.leonfang.whereru;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;

import com.baidu.mapapi.SDKInitializer;
import com.leonfang.whereru.service.LocationService;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import cn.bmob.newim.BmobIM;
import cn.smssdk.SMSSDK;

public class WhereRUApplication extends Application {

    private static WhereRUApplication INSTANCE;
    public static WhereRUApplication INSTANCE(){
        return INSTANCE;
    }
    private void setInstance(WhereRUApplication app) {
        setBmobIMApplication(app);
    }
    private static void setBmobIMApplication(WhereRUApplication a) {
        WhereRUApplication.INSTANCE = a;
    }
    public LocationService locationService;
    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
        setInstance(this);
        locationService = new LocationService(getApplicationContext());
        //只有主进程运行的时候才需要初始化
        if (getApplicationInfo().packageName.equals(getMyProcessName())){
            //im初始化
            BmobIM.init(this);
            //注册消息接收器
            BmobIM.registerDefaultMessageHandler(new MyIMMessageHandler(this));
            SMSSDK.initSDK(this, "1b72312635dea", "3b0364c8ffe9163e2a35f8389ccc4f4c");
            SDKInitializer.initialize(getApplicationContext());
        }

    }

    /**
     * 获取当前运行的进程名
     * @return
     */
    public static String getMyProcessName() {
        try {
            File file = new File("/proc/" + android.os.Process.myPid() + "/" + "cmdline");
            BufferedReader mBufferedReader = new BufferedReader(new FileReader(file));
            String processName = mBufferedReader.readLine().trim();
            mBufferedReader.close();
            return processName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
