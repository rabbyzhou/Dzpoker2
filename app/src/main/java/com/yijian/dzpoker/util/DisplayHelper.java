package com.yijian.dzpoker.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.WindowManager;

/**
 * Created by koyabr on 10/22/15.
 */
public class DisplayHelper {

    private Context context;

    private int SCREEN_WIDTH_PIXELS;
    private int SCREEN_HEIGHT_PIXELS;
    private float SCREEN_DENSITY;
    private int SCREEN_WIDTH_DP;
    private int SCREEN_HEIGHT_DP;

    public DisplayHelper(Context context) {
        if (context == null) {
            return;
        }
        this.context = context;
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        SCREEN_WIDTH_PIXELS = dm.widthPixels;
        SCREEN_HEIGHT_PIXELS = dm.heightPixels;
        SCREEN_DENSITY = dm.density;
        SCREEN_WIDTH_DP = (int) (SCREEN_WIDTH_PIXELS / dm.density);
        SCREEN_HEIGHT_DP = (int) (SCREEN_HEIGHT_PIXELS / dm.density);
    }

    public int dp2px(float dp) {
        final float scale = SCREEN_DENSITY;
        return (int) (dp * scale + 0.5f);
    }

    public int mm2px(int mm){
       return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, mm,
               context.getResources().getDisplayMetrics());
    }

    public int getSCREEN_WIDTH_PIXELS() {
        return SCREEN_WIDTH_PIXELS;
    }

    public int getSCREEN_HEIGHT_PIXELS() {
        return SCREEN_HEIGHT_PIXELS;
    }
}
