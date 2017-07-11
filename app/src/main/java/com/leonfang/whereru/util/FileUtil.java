package com.leonfang.whereru.util;

import android.graphics.Bitmap;
import android.os.Environment;

import com.leonfang.whereru.WhereRUApplication;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by LeonFang on 2017/4/10.
 */

public class FileUtil {
    public static void saveBitmap(Bitmap bitmap, String path, String bitName) throws IOException {
        File file = new File(path, bitName);
        if (file.exists()) {
            file.delete();
        }
        FileOutputStream out;
        try {
            out = new FileOutputStream(file);
            if (bitmap.compress(Bitmap.CompressFormat.PNG, 90, out)) {
                out.flush();
                out.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static File getAvatarFile(String filename) {
        File file = new File(getDiskCacheDir() , filename+".jpg");
        Logger.e(FileUtil.class,file.getAbsolutePath());
        return file;
    }

    private static String getDiskCacheDir() {
        String cachePath = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = WhereRUApplication.INSTANCE().getExternalCacheDir().getPath();
        } else {
            cachePath = WhereRUApplication.INSTANCE().getCacheDir().getPath();
        }
        return cachePath;
    }
}
