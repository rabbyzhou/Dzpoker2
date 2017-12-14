package com.yijian.dzpoker.activity.user;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yijian.dzpoker.R;
import com.yijian.dzpoker.activity.base.BaseToolbarActivity;
import com.yijian.dzpoker.activity.fragment.GoldCoinBuyFragment;
import com.yijian.dzpoker.activity.fragment.UserLevelFragment;
import com.yijian.dzpoker.constant.Constant;
import com.yijian.dzpoker.util.DzApplication;
import com.yijian.dzpoker.util.ToastUtil;
import com.yijian.dzpoker.view.data.User;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;

import okhttp3.Request;
import okhttp3.Response;

import static com.yijian.dzpoker.constant.Constant.INTENT_KEY_BACKTEXT;

public class StoreActivity extends BaseToolbarActivity implements
        View.OnClickListener,UserLevelFragment.OnLevelDealDoneListener, GoldCoinBuyFragment.OnDealDoneListener {

    private static final String TAG = "StoreActivity";
    TextView mGoldCoinText ;
    TextView mDiamondText ;
    ImageView mDiamondPlusImage;
    ViewPager mShopVPager ;
    TabLayout mShoptab ;
    TextView mBackText;
    TextView mTitle;

    protected int userId;

    protected int mGoldcoin;
    public int mDiamond;
    public DzApplication application;

    private final static String TITLE = "商店" ;
    private ArrayList<String> mTitleList = new ArrayList<String>(){{
        add("游戏商店");
        add("VIP特权");
    }};

    private ArrayList<Fragment> mFragmentList = new ArrayList<Fragment>(){{
        add( GoldCoinBuyFragment.newInstance());
        add( UserLevelFragment.newInstance());
    }};

    private MyAdapter mAdapter;

    private static final int MESSAGE_UDPATE_USER = 0;

    private Handler mHander = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case MESSAGE_UDPATE_USER:
                    showAccountData();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_store);
        application=(DzApplication) getApplication();
        userId=application.getUserId();
        initViews();

        mAdapter = new MyAdapter(getSupportFragmentManager(), mTitleList, mFragmentList);
        mShopVPager.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        //绑定
        mShoptab.setupWithViewPager(mShopVPager);
        mShoptab.getTabAt(0).select();
        mShopVPager.setCurrentItem(0);

        showAccountData();
    }

    protected void initViews() {

        mGoldCoinText = (TextView)findViewById(R.id.store_goldcoin);
        mDiamondText = (TextView)findViewById(R.id.store_diamond);
        mDiamondPlusImage = (ImageView)findViewById(R.id.diamond_plus);
        mShopVPager = (ViewPager) findViewById(R.id.vp_shop);
        mShoptab = (TabLayout) findViewById(R.id.tab_shop);
//        mBackText = (TextView) findViewById(R.id.tv_back);
//        mTitle = (TextView) findViewById(R.id.tv_title);

        String backText = getIntent().getStringExtra(Constant.INTENT_KEY_BACKTEXT);
//        if (backText != null && !backText.isEmpty()) mBackText.setText(backText);

        String title = getIntent().getStringExtra(Constant.INTENT_KEY_TITLE);
        if (title == null || title.isEmpty()) title = TITLE;
        setToolbarTitle(title);
//        mTitle.setText(title);

//        mBackText.setOnClickListener(this);
        mDiamondPlusImage.setOnClickListener(this);
        mShopVPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void showAccountData(){
        mGoldcoin = application.getUser().goldcoin;
        mDiamond = application.getUser().diamond;

        mGoldCoinText.setText(mGoldcoin + "");
        mDiamondText.setText(mDiamond + "");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_back:
                setResult(RESULT_OK);
                finish();
                break;
            case R.id.diamond_plus:
                buyDiamond();
                break;
        }
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

    private void buyDiamond(){
        Intent intent = new Intent();
        intent.setClass(this, BuyDiamondActivity.class);
        intent.putExtra(INTENT_KEY_BACKTEXT, TITLE);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ( requestCode == 1){
            freshUserAccountInfo();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onLevelDealDone() {
        freshUserAccountInfo();
    }

    @Override
    public void onDealDone() {
        freshUserAccountInfo();
    }

    private void freshUserAccountInfo(){

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                try{
                    //获取用户基本数据
                    JSONObject jsonParam= new JSONObject();
                    jsonParam.put("userid",userId);
                    String strURL=getString(R.string.url_remote)+"func=getuserinfo&param="+jsonParam.toString();
                    URL url = new URL(strURL);
                    Request request = new Request.Builder().url(strURL).build();
                    Response response = DzApplication.getHttpClient().newCall(request).execute();
                    String result=response.body().string();
                    JSONObject jsonResult=new JSONObject((new JSONArray(result)).get(0).toString());

                    User user = application.getUser();
                    user.goldcoin = jsonResult.getInt("goldcoin");
                    user.diamond =jsonResult.getInt("diamond");
                    user.levelid =jsonResult.getInt("level");
                    user.levelname = jsonResult.getString("levelname");

                    mHander.sendEmptyMessage(MESSAGE_UDPATE_USER);

                }catch (Exception e){
                    ToastUtil.showToastInScreenCenter(StoreActivity.this,"更新用户数据失败!");
                }
            }
        });

        thread.start();
    }


    private class MyAdapter extends FragmentStatePagerAdapter {
        private ArrayList<String> titleList;
        private ArrayList<Fragment> fragmentList;

        public MyAdapter(FragmentManager fm, ArrayList<String> titleList, ArrayList<Fragment> fragmentList) {
            super(fm);
            this.titleList = titleList;
            this.fragmentList = fragmentList;
        }

        @Override
        public Fragment getItem(int position) {
            Log.d(TAG, "getItem: " + position);
            Fragment fragment = fragmentList.get(position);
            Log.d(TAG, "getFragment: " + fragment.toString());
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titleList.get(position);
        }

    }
}
