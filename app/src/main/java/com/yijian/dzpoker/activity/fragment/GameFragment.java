package com.yijian.dzpoker.activity.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TabWidget;

import com.yijian.dzpoker.R;

import com.yijian.dzpoker.activity.MainActivity;
import com.yijian.dzpoker.activity.game.fragment.FindGameFragment;
import com.yijian.dzpoker.activity.game.fragment.QuickGameFragment;
import com.yijian.dzpoker.constant.Constant;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rabby on 2017/8/10.
 */

public class GameFragment extends BaseFragment implements View.OnClickListener,TabHost.OnTabChangeListener {
    ViewPager mViewPager;
    ViewPagerFragmentAdapter mViewPagerFragmentAdapter;
    FragmentManager mFragmentManager;
    List<Fragment> mFragmentList = new ArrayList<Fragment>();
    TabLayout toolbar_tab;

    private TabHost tabHost;
    private TabWidget tabWidget;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View gameLayout = inflater.inflate(R.layout.fragment_game_layout,
                container, false);

        tabHost =(TabHost) gameLayout.findViewById(R.id.tabhost);
        tabHost.setup();
        tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("tab1").setContent(R.id.widget_layout_red));
        tabHost.addTab(tabHost.newTabSpec("tab2").setIndicator("tab2").setContent(R.id.widget_layout_yellow));

        tabHost.setCurrentTab(0);
        tabHost.setOnTabChangedListener(this);

        return gameLayout;
    }


    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        MainActivity.currFragTag = Constant.FRAGMENT_FLAG_GAME;

    }

    public void initFragmetList() {
                 Fragment quickgame = new QuickGameFragment() ;
                 Fragment findgame = new FindGameFragment();
                 mFragmentList.add(quickgame);
                 mFragmentList.add(findgame);

    }

    public void initViewPager() {
       // mViewPager.addOnPageChangeListener(new ViewPagetOnPagerChangedLisenter());
        mViewPager.setAdapter(mViewPagerFragmentAdapter);
        mViewPager.setCurrentItem(0);

    }

    public class ViewPagerFragmentAdapter extends FragmentPagerAdapter {

        private List<Fragment> mList = new ArrayList<Fragment>();
        public ViewPagerFragmentAdapter(FragmentManager fm , List<Fragment> list) {
                     super(fm);
                     this.mList = list;
                 }

        @Override
        public Fragment getItem(int position) {
                    return mList.get(position);
        }

        @Override
        public int getCount() {
            return mList != null ? mList.size() : 0;
        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onTabChanged(String tabId) {

    }
}
