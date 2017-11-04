package com.yijian.dzpoker.util;

import android.app.Activity;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by rabby on 2017/8/9.
 */

public class ToastUtil {
     /**
     * 显示toast
     * @param ctx
     * @param msg
     */


    public static void showToastInScreenCenter(final Activity ctx,final String msg)
     {
        // 判断是在子线程，还是主线程
        if("main".equals(Thread.currentThread().getName()))
        {
            Toast toast = Toast.makeText(ctx,msg, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }else{
            // 子线程
            ctx.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast toast = Toast.makeText(ctx,msg, Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            });
        }

    }
 }