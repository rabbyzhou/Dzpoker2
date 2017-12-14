package com.yijian.dzpoker.activity.game;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yijian.dzpoker.R;
import com.yijian.dzpoker.activity.base.BaseToolbarActivity;
import com.yijian.dzpoker.activity.game.fragment.FragmentGameSet1;
import com.yijian.dzpoker.activity.game.fragment.FragmentGameSet2;
import com.yijian.dzpoker.activity.game.fragment.FragmentGameSet3;
import com.yijian.dzpoker.activity.game.fragment.FragmentGameSet4;
import com.yijian.dzpoker.activity.game.fragment.FragmentGameSet5;
import com.yijian.dzpoker.activity.game.fragment.FragmentGameSet6;
import com.yijian.dzpoker.constant.Constant;
import com.yijian.dzpoker.view.NoScrollViewPager;

import java.util.ArrayList;
import java.util.List;

public class GameSetActivity extends BaseToolbarActivity implements FragmentGameSet1.OnFragmentInteractionListener,FragmentGameSet2.OnFragmentInteractionListener,FragmentGameSet3.OnFragmentInteractionListener,FragmentGameSet4.OnFragmentInteractionListener,FragmentGameSet5.OnFragmentInteractionListener,FragmentGameSet6.OnFragmentInteractionListener{
    private TabLayout tabLayout,tabLayout2;
    private String[] titles1 = {"德州扑克", "奥马哈"};
    private String[] titles2 = {"普通牌局", "SNG", "MTT"};
    private NoScrollViewPager viewPager;
    private List<Fragment> list;
    private MyAdapter adapter;
    private TextView tvBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_set);
        tabLayout = (TabLayout) findViewById(R.id.tab_game);
        tabLayout2 = (TabLayout) findViewById(R.id.tab_game2);

        tvBack = (TextView)findViewById(R.id.tv_back);
        setToolbarTitle("创建牌局");
        String backText = getIntent().getStringExtra(Constant.INTENT_KEY_BACKTEXT);
        if ( backText != null && !backText.isEmpty()){
            tvBack.setText(backText);
        }
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        for (int i = 0; i < titles1.length; i++) {
            View v = LayoutInflater.from(this).inflate(R.layout.tab_gameset1, null);
            // v.setBackgroundResource(R.drawable.tab_background);
            TextView textView = (TextView) v.findViewById(R.id.tv_title);
            textView.setText(titles1[i]);
            //添加一行，设置颜色
            TabLayout.Tab tab =tabLayout.newTab().setCustomView(v);
            tabLayout.addTab(tab);

        }
        for (int i = 0; i < titles2.length; i++) {
            View v = LayoutInflater.from(this).inflate(R.layout.tab_gameset1, null);
            // v.setBackgroundResource(R.drawable.tab_background);
            TextView textView = (TextView) v.findViewById(R.id.tv_title);
            textView.setText(titles2[i]);
            //添加一行，设置颜色
            TabLayout.Tab tab =tabLayout2.newTab().setCustomView(v);
            tabLayout2.addTab(tab);

        }

        LinearLayout linearLayout = (LinearLayout) tabLayout.getChildAt(0);
        linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE|LinearLayout.SHOW_DIVIDER_END);
        linearLayout.setDividerDrawable(ContextCompat.getDrawable(this,
                R.drawable.layout_divide_vertical)); //设置分割线的样式
//        linearLayout.setDividerPadding(dip2px(0)); //设置分割线间隔

        LinearLayout linearLayout2 = (LinearLayout) tabLayout2.getChildAt(0);
        linearLayout2.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        linearLayout2.setDividerDrawable(ContextCompat.getDrawable(this,
                R.drawable.layout_divide_vertical)); //设置分割线的样式
//        linearLayout2.setDividerPadding(dip2px(10)); //设置分割线间隔


        //此处无效
//        for (int i = 1; i < tabLayout.getTabCount(); i++) {
//            View tabView = tabLayout.getChildAt(i);
//            if (tabView != null) {
//                tabView.setClickable(false);
//            }
//        }
//        for (int i = 1; i < tabLayout2.getTabCount(); i++) {
//            View tabView = tabLayout2.getChildAt(i);
//            if (tabView != null) {
//                tabView.setClickable(false);
//            }
//        }

        viewPager=(NoScrollViewPager)findViewById(R.id.view_pager);
        viewPager.setNoScroll(true);
        list = new ArrayList<>();
        list.add(new FragmentGameSet1());
        list.add(new FragmentGameSet2());
        list.add(new FragmentGameSet3());
        list.add(new FragmentGameSet4());
        list.add(new FragmentGameSet5());
        list.add(new FragmentGameSet6());


        //ViewPager的适配器
        adapter = new MyAdapter(getSupportFragmentManager(), this);
        viewPager.setAdapter(adapter);

        viewPager.setCurrentItem(0);
        //这里要设置监听事件，以切换fragment
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tabLayout.getSelectedTabPosition()==0){
                    switch (tabLayout2.getSelectedTabPosition()){
                        case 0:
                            //viewPager.setCurrentItem(0);
                            break;
                        case 1:
                           // viewPager.setCurrentItem(1);
                            break;
                        case 2:
                            //viewPager.setCurrentItem(2);
                            break;
                    }
                }else if (tabLayout.getSelectedTabPosition()==1){
                    switch (tabLayout2.getSelectedTabPosition()){
                        case 0:
                           // viewPager.setCurrentItem(3);
                            break;
                        case 1:
                           // viewPager.setCurrentItem(4);
                            break;
                        case 2:
                            //viewPager.setCurrentItem(5);
                            break;
                    }
                }


            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        tabLayout2.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tabLayout.getSelectedTabPosition()==0){
                    switch (tabLayout2.getSelectedTabPosition()){
                        case 0:
                            viewPager.setCurrentItem(0);
                            break;
                        case 1:
                           // viewPager.setCurrentItem(1);
                            break;
                        case 2:
                            //viewPager.setCurrentItem(2);
                            break;
                    }
                }else if (tabLayout.getSelectedTabPosition()==1){
                    switch (tabLayout2.getSelectedTabPosition()){
                        case 0:
                            //viewPager.setCurrentItem(3);
                            break;
                        case 1:
                           // viewPager.setCurrentItem(4);
                            break;
                        case 2:
                           // viewPager.setCurrentItem(5);
                           // viewPager.setCurrentItem(5);
                            break;
                    }
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    class MyAdapter extends FragmentPagerAdapter {

        private Context context;

        public MyAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.context = context;
        }

        @Override
        public Fragment getItem(int position) {
            return list.get(position);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        /**
         * 自定义方法，提供自定义Tab
         *
         * @param position 位置
         * @return 返回Tab的View
         */

    }


    //像素单位转换
    public int dip2px(int dip) {
        float density = getResources().getDisplayMetrics().density;
        return (int) (dip * density + 0.5);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ){
            setResult(RESULT_OK);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
