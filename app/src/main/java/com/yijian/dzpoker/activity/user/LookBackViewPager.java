package com.yijian.dzpoker.activity.user;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by c_huangl on 0012, 11/12/2017.
 */

public class LookBackViewPager extends ViewPager {

    public LookBackViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LookBackViewPager(Context context) {
        super(context);
    }

    //是否可以滑动
    private boolean isCanScroll = true;

    //----------禁止左右滑动------------------
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (isCanScroll) {
            return super.onTouchEvent(ev);
        } else {
            return false;
        }

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        if (isCanScroll) {
            return super.onInterceptTouchEvent(arg0);
        } else {
            return false;
        }

    }
    //-------------------------------------------

    /**
     * 设置 是否可以滑动
     * @param isCanScroll
     */
    public void setScrollble(boolean isCanScroll) {
        this.isCanScroll = isCanScroll;
    }
}
