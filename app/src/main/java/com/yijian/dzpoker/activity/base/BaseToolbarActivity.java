package com.yijian.dzpoker.activity.base;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yijian.dzpoker.R;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by QIPU on 2017/12/14.
 */
public abstract class BaseToolbarActivity extends AppCompatActivity {

    LinearLayout rootLayout;
    public TextView toolbarTitle;
    public ImageView toolbarRightImageView;
    public TextView toolbarRightTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_base_toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | layoutParams.flags);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                // 因为EMUI3.1系统与这种沉浸式方案API有点冲突，会没有沉浸式效果。
                // 所以这里加了判断，EMUI3.1系统不清除FLAG_TRANSLUCENT_STATUS
                if (!isEMUI3_1()) {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                }
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.TRANSPARENT);
            }
        }
        initToolbar();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (null != toolbar) {
            toolbarTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
            toolbarRightTextView = (TextView) toolbar.findViewById(R.id.toolbar_right_tv);
            toolbarRightImageView = (ImageView) toolbar.findViewById(R.id.toolbar_right_iv);
            setSupportActionBar(toolbar);
            if (toolbarTitle != null) {
                getSupportActionBar().setDisplayShowTitleEnabled(false);
            }
        }
    }

    public void setToolbarTitle(String text) {
        toolbarTitle.setText(text);
    }

    public void showToolbarRightImageViewDefault(View.OnClickListener listener) {
        if (null != toolbarRightImageView) {
            toolbarRightImageView.setVisibility(View.VISIBLE);
            toolbarRightImageView.setOnClickListener(listener);
        }
    }

    public void hideToolbarRightImageView() {
        if (null != toolbarRightImageView) {
            toolbarRightImageView.setVisibility(View.GONE);
        }
    }

    public void showToolbarRightTextView(String content, View.OnClickListener listener) {
        if (null != toolbarRightTextView) {
            toolbarRightTextView.setText(content);
            toolbarRightTextView.setVisibility(View.VISIBLE);
            toolbarRightTextView.setOnClickListener(listener);
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        setContentView(View.inflate(this, layoutResID, null));
    }

    @Override
    public void setContentView(View view) {
        rootLayout = (LinearLayout) findViewById(R.id.root_layout);
        if (null != rootLayout) {
            rootLayout.addView(view, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            initToolbar();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public static boolean isEMUI3_1() {
        return "EmotionUI_3.1".equals(getEmuiVersion());
    }

    private static String getEmuiVersion(){
        Class<?> classType = null;
        try {
            classType = Class.forName("android.os.SystemProperties");
            Method getMethod = classType.getDeclaredMethod("get", String.class);
            return (String)getMethod.invoke(classType, "ro.build.version.emui");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }

}
