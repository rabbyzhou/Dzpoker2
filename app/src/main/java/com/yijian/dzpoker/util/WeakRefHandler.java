package com.yijian.dzpoker.util;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * Created by koyabr on 8/24/15.
 */
public abstract class WeakRefHandler<T> extends Handler {

    private WeakReference<T> mRef;

    public WeakRefHandler(T ref){
        mRef = new WeakReference<T>(ref);
    }

    @Override
    public void handleMessage(Message msg) {
        T ref = mRef.get();
        if(ref != null){
            handleMessage(ref, msg);
        }
    }

    protected abstract void handleMessage(T ref, Message msg);
}
