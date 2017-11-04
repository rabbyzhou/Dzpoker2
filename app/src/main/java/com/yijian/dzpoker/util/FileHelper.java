package com.yijian.dzpoker.util;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * Created by koyabr on 8/21/15.
 */
public class FileHelper {

    static final String TAG = FileHelper.class.getSimpleName();

    static final String CACHE_DIR = "cache";
    static final String TEMP_DIR = "temp";

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    public static File getCacheDir(Context context){
        return getWritableDir(context, CACHE_DIR);
    }

    public static File getTempFile(Context context, String filename){
        return getWritableFile(context, TEMP_DIR, filename);
    }

    private static File getWritableDir(Context context, String dirName) {
        File dir;
        // 优先使用外部存储
        if(isExternalStorageWritable()) {
            dir = new File(context.getExternalFilesDir(null), dirName);
            if (dir.exists() || dir.mkdirs()){
                return dir;
            }
        }

        // 外部存储不可用，使用内部存储
        dir = new File(context.getFilesDir(), dirName);
        if (dir.exists() || dir.mkdirs()){
            return dir;
        }

        return null;


    }

    private static File getWritableFile(Context context, String dirName, String filename) {
        File dir;
        // 优先使用外部存储
        if(isExternalStorageWritable()) {
            dir = new File(context.getExternalFilesDir(null), dirName);
            if (dir.exists() || dir.mkdirs()){
                return new File(dir, filename);
            }
        }

        // 外部存储不可用，使用内部存储
        dir = new File(context.getFilesDir(), dirName);
        if (dir.exists() || dir.mkdirs()){
            return new File(dir, filename);
        }


        return null;


    }
}
