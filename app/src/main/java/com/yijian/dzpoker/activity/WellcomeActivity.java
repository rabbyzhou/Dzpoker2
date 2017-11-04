package com.yijian.dzpoker.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

import com.yijian.dzpoker.R;
import com.yijian.dzpoker.activity.club.ClubInfoActivity;
import com.yijian.dzpoker.activity.club.CreateClubActivity;
import com.yijian.dzpoker.util.DzApplication;
import com.yijian.dzpoker.util.ToastUtil;
import com.yijian.dzpoker.view.data.ClubInfo;
import com.yijian.dzpoker.view.data.User;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class WellcomeActivity extends AppCompatActivity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private String mLoginName;
    private  int mUserId;

    private static final boolean AUTO_HIDE = true;
    private DzApplication myApp;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_wellcome);

        //启动GPS服务，以后每秒取一次，如果取不到，根据网络或者基站信息来取

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);


        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {





        //根据本地文件里登录用户字符串来判断是否为用户登录状态
        SharedPreferences settings = getSharedPreferences("depoker", 0);
        mLoginName=settings.getString("username","");
        mUserId=settings.getInt("userid",0);

        if (mLoginName.equals("")) {
            //跳转到登录界面
            Intent intent = new Intent();
            intent.setClass(WellcomeActivity.this, LoginActivity.class);
            startActivity(intent);
        }else{


            //跳转到主界面
            //调用线程，到后台去请求俱乐部成员信息，以完成相关数据的初始化
            Thread thread=new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    try{

                        // 根据用户ID取数据数据，此处也是判断与后台网络是否通
                        JSONObject jsonParam= new JSONObject();
                        jsonParam.put("userid",mUserId);
                        String strURL=getString(R.string.url_remote)+"func=getuserinfo&param="+jsonParam.toString();
                        URL url = new URL(strURL);
                        Request request = new Request.Builder().url(strURL).build();
                        Response response = DzApplication.getHttpClient().newCall(request).execute();
                        String result=response.body().string();
                        JSONObject jsonResult=new JSONObject((new JSONArray(result)).get(0).toString());

                    /*[{"id":2,"nickname":"无名","loginname":"13801865121","password":"666666","mobilephone":"13801865121","email":null,"sex":"男",
                    "province":null,"city":null,"personaltip":null,"headpic":null,"goldcoin":1000,"diamond":100,"type":0,
                    "levelid":1,"levelenddate":"2017-08-10T00:00:00","lastlogintime":"2017-08-30T11:25:59","lastlogingpsx":null,
                    "lastlogingpsy":null,"lastloginip":null,"registertime":"2017-08-02T15:50:23","id1":1,"level":1,"levelname":"黄金会员",
                    "levelabstract":"黄金","durationdays":100,"costdiamonds":100,"maxclubs":5}]*/

                        User user=new User();
                        user.userId=jsonResult.getInt("id");
                        user.nickName=jsonResult.getString("nickname");
                        if (!jsonResult.getString("personaltip").equals("null")){
                            user.personalTip=jsonResult.getString("personaltip");
                        }else {
                            user.personalTip="暂无签名";
                        }
                        user.province=jsonResult.getString("province");
                        user.city=jsonResult.getString("city");
                        user.goldcoin=jsonResult.getInt("goldcoin");
                        user.diamond=jsonResult.getInt("diamond");
                        user.userLoginName=jsonResult.getString("loginname");
                        if (jsonResult.getString("headpic")!=null && !jsonResult.getString("headpic").equals("null")) {
                            user.userHeadPic = jsonResult.getString("headpic");
                        }
                        user.levelid=jsonResult.getInt("level");
                        user.levelname=jsonResult.getString("levelname");
                        user.password=jsonResult.getString("password");

                        ((DzApplication)getApplication()).setUser(user);

                        myApp=(DzApplication)getApplication();
                        myApp.setLoginName(mLoginName);
                        myApp.setUserId(mUserId);

                            //跳转到主界面
                            Intent intent = new Intent();
                            //intent.setClass(WellcomeActivity.this, MainActivity.class);
                            intent.setClass(WellcomeActivity.this, MainFragmentActivity.class);
                            startActivity(intent);


                    }catch (Exception e){
                        ToastUtil.showToastInScreenCenter(WellcomeActivity.this,"与后台通讯出错，请稍后重试!");
                    }

                }
            });
            thread.start();
        }



//        if (mVisible) {
//            hide();
//        } else {
//            show();
//        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}
