package com.yijian.dzpoker.baselib.image;

import android.content.Context;
import android.widget.ImageView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

/**
 * Created by QIPU on 2017/12/23.
 */

public class DzImageLoader {

    public static void loadImageFromUrl(Context context, String url, ImageView iv, int defaultDrawableId) {
        Picasso.with(context)
                .load(url)
                .placeholder(defaultDrawableId)
                .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .resize(100, 100)
                .error(defaultDrawableId)
                .into(iv);
    }
}
