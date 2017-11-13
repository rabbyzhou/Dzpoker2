package com.yijian.dzpoker.activity;

import android.content.Context;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yijian.dzpoker.R;
import com.yijian.dzpoker.activity.fragment.ClubInfoFragment;
import com.yijian.dzpoker.activity.game.fragment.FindGameFragment;
import com.yijian.dzpoker.activity.fragment.HomeFragment;
import com.yijian.dzpoker.activity.fragment.PlayGameFragment;
import com.yijian.dzpoker.activity.game.fragment.QuickGameFragment;
import com.yijian.dzpoker.activity.fragment.UserFragment;

import java.util.ArrayList;
import java.util.List;

public class MainFragmentActivity extends FragmentActivity implements HomeFragment.OnFragmentInteractionListener,ClubInfoFragment.OnFragmentInteractionListener,PlayGameFragment.OnFragmentInteractionListener,UserFragment.OnFragmentInteractionListener,QuickGameFragment.OnFragmentInteractionListener,FindGameFragment.OnFragmentInteractionListener  {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    private List<Fragment> list;
    private MyAdapter adapter;
    private String[] titles = {"首页", "游戏", "俱乐部", "我"};
    private int images[] = {R.drawable.message_unselected, R.drawable.message_unselected,R.drawable.message_unselected, R.drawable.message_unselected};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_fragment);

        viewPager = (ViewPager) findViewById(R.id.vp_main);
        tabLayout = (TabLayout) findViewById(R.id.tab_main);
        //页面，数据源
        list = new ArrayList<>();
        list.add(new HomeFragment());
        list.add(new PlayGameFragment());
        list.add(new ClubInfoFragment());
        list.add(new UserFragment());

        //ViewPager的适配器
        adapter = new MyAdapter(getSupportFragmentManager(), this);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                TextView titleView = (TextView) MainFragmentActivity.this.findViewById(R.id.title);
                titleView.setText(titles[position]);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        //绑定
        tabLayout.setupWithViewPager(viewPager);
        //设置自定义视图
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            tab.setCustomView(adapter.getTabView(i));

        }

        tabLayout.getTabAt(0).select();
        TextView titleView = (TextView) MainFragmentActivity.this.findViewById(R.id.title);
        titleView.setText(titles[0]);

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
        public View getTabView(int position) {
            View v = LayoutInflater.from(context).inflate(R.layout.tab_custom, null);
            TextView textView = (TextView) v.findViewById(R.id.tv_title);
            ImageView imageView = (ImageView) v.findViewById(R.id.iv_icon);
            textView.setText(titles[position]);
            imageView.setImageResource(images[position]);
            //添加一行，设置颜色
            textView.setTextColor(tabLayout.getTabTextColors());//
            return v;
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }


}
