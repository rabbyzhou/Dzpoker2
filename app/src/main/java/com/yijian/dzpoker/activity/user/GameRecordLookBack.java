package com.yijian.dzpoker.activity.user;

import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yijian.dzpoker.R;
import com.yijian.dzpoker.activity.base.BaseBackActivity;
import com.yijian.dzpoker.constant.Constant;
import com.yijian.dzpoker.util.DzApplication;
import com.yijian.dzpoker.util.ToastUtil;
import com.yijian.dzpoker.util.Util;
import com.yijian.dzpoker.utils.DialogCreator;
import com.yijian.dzpoker.view.adapter.GameLookBackUserInfoAdapter;
import com.yijian.dzpoker.view.data.CardInfo;
import com.yijian.dzpoker.view.data.RoundInfo;
import com.yijian.dzpoker.view.data.RoundUserInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by c_huangl on 0012, 11/12/2017.
 */

public class GameRecordLookBack extends BaseBackActivity {

    private final static String TITLE = "手牌回顾";

    private TextView mNoDataView;
    private ImageView mCollectImage;
    private ImageView mFirstImage;
    private ImageView mFrontImage;
    private ImageView mNextImage;
    private ImageView mLastImage;
    private TextView mPageText;

    LookBackViewPager mLoopBackPager ;

    private int mGametableid;
    private ArrayList<RoundInfo> mRecordList = new ArrayList<RoundInfo>();
    //private ArrayList<Fragment> mFragmentList = new ArrayList<Fragment>();
    private ArrayList<View> mLoopBackViewList = new ArrayList<View>();
    private MyAdapter mAdapter;

    private int mCurrentPage;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_game_lookback;
    }

    @Override
    protected void initViews() {
        super.initViews();

        String backText = getIntent().getStringExtra(Constant.INTENT_KEY_BACKTEXT);
//        if (backText != null && !backText.isEmpty()) tv_back.setText(backText);
//
//        tv_back.setOnClickListener(this);
//        tv_title.setText(TITLE);
        //TODO:qipu

        mNoDataView = (TextView) findViewById(R.id.tv_no_data);

        mLoopBackPager =(LookBackViewPager) findViewById(R.id.vp_lookback);
        mLoopBackPager.setScrollble(false);

        //mAdapter = new MyAdapter(getSupportFragmentManager(),mFragmentList);
        mAdapter = new MyAdapter();
        mLoopBackPager.setAdapter(mAdapter);

        mCollectImage = (ImageView) findViewById(R.id.collect);
        mFirstImage = (ImageView) findViewById(R.id.firstpage);
        mFirstImage.setRotation(180);
        mFrontImage = (ImageView) findViewById(R.id.frontpage);
        mNextImage = (ImageView) findViewById(R.id.nextpage);
        mLastImage = (ImageView) findViewById(R.id.lastpage);
        mPageText = (TextView) findViewById(R.id.page_text);

        mCollectImage.setOnClickListener(this);
        mFirstImage.setOnClickListener(this);
        mFrontImage.setOnClickListener(this);
        mNextImage.setOnClickListener(this);
        mLastImage.setOnClickListener(this);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGametableid = getIntent().getIntExtra("gametableid",-1);

        new QueryDataTask().execute();
    }


    private void updateUI(){

        for(int i = 0; i < mRecordList.size(); i++){
            View view = getLayoutInflater().inflate(R.layout.item_lookback_list,null);
            mLoopBackViewList.add(view);
        }
        mAdapter.notifyDataSetChanged();
        firstPage();
    }

    private class QueryDataTask extends AsyncTask<String, Integer, String> {

        private Dialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = DialogCreator.createLoadingDialog(GameRecordLookBack.this,
                    GameRecordLookBack.this.getResources().getString(R.string.jmui_loading));
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            queryData();
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if ( dialog.isShowing()) dialog.cancel();

            if ( mRecordList == null || mRecordList.isEmpty()){
                mNoDataView.setVisibility(View.VISIBLE);
                mLoopBackPager.setVisibility(View.GONE);
            }else {
                mNoDataView.setVisibility(View.GONE);
                mLoopBackPager.setVisibility(View.VISIBLE);
                updateUI();
            }

            super.onPostExecute(s);
        }
    }

    private void queryData(){
        try{

            JSONObject jsonParam= new JSONObject();
            jsonParam.put("gametableid",mGametableid);
            jsonParam.put("userid",((DzApplication)getApplication()).getUserId());

            String strURL=getString(R.string.url_remote)+"func=getroundreview&param="+jsonParam.toString();

            Request request = new Request.Builder().url(strURL).build();
            Response response = DzApplication.getHttpClient().newCall(request).execute();
            String result=response.body().string();

            JSONObject temoObject = new JSONObject(result);

            JSONArray list= temoObject.getJSONArray("roundinfo");

            for(int i=0;i<list.length();i++) {

                JSONObject jsonObject=new JSONObject(list.get(i).toString());
                RoundInfo roundInfo = new RoundInfo();
                /**
                 public class RoundInfo {

                     public String starttime;
                     public int pot;
                     public int jackpot;
                     public int surancepot;
                     public CardInfo[] cards;
                     public List<RoundUserInfo> rounduserinfo;
                     public boolean iscollect;
                 }
                 */

                roundInfo.starttime = jsonObject.getString("starttime");
                roundInfo.pot = jsonObject.getInt("pot");
                roundInfo.jackpot = jsonObject.getInt("jackpot");
                roundInfo.surancepot = jsonObject.getInt("surancepot");
                roundInfo.iscollect = jsonObject.getBoolean("iscollect");

                    //解析手牌
                    JSONArray cardInfos = jsonObject.getJSONArray("cards");

                    CardInfo[] cardInfoList = new CardInfo[5];

                    for(int j=0;j<cardInfos.length();j++) {

                        CardInfo cardInfo = new CardInfo();
                        if (cardInfos.get(j) != null && !cardInfos.get(j).toString().equals("null")){
                            JSONObject cardObject =new JSONObject(cardInfos.get(j).toString());

                            /**
                             public class CardInfo {
                             public int suit;
                             public int member;
                             public String name;
                             }**/
                            cardInfo.suit = cardObject.getInt("suit");
                            cardInfo.member = cardObject.getInt("member");
                            cardInfo.name = cardObject.getString("name");
                        }
                        cardInfoList[j] = cardInfo;
                    }

                roundInfo.cards = cardInfoList;

                    //解析游戏玩家列表
                    JSONArray roundUserListArray = jsonObject.getJSONArray("rounduserinfo");

                    List<RoundUserInfo> roundUserList = new ArrayList<RoundUserInfo>();

                    for(int j=0;j<roundUserListArray.length();j++) {

                        RoundUserInfo roundUserInfo = new RoundUserInfo();

                        if (roundUserListArray.get(j) != null && !roundUserListArray.get(j).toString().equals("null")){
                            JSONObject cardObject =new JSONObject(roundUserListArray.get(j).toString());

                            /**
                             public class RoundUserInfo {
                             public int userid;
                             public String nickname;
                             public String headpic;
                             public CardInfo[] card;
                             public String lastaction;
                             public int winchips;
                             public String wincard;
                             public int win27chips;
                             public int winsurance;
                             public boolean isshowcard;
                             }
                             }**/
                            roundUserInfo.userid = cardObject.getInt("userid");
                            roundUserInfo.nickname = cardObject.getString("nickname");
                            roundUserInfo.headpic = cardObject.getString("headpic");

                            //解析手牌
                            JSONArray subCardInfos = cardObject.getJSONArray("card");

                            CardInfo[] subCardInfoList = new CardInfo[5];

                            for(int k=0;k<subCardInfos.length();k++) {

                                CardInfo cardInfo = new CardInfo();
                                if(subCardInfos.get(k) != null && !subCardInfos.get(j).toString().equals("null")){
                                    JSONObject subCardObject =new JSONObject(subCardInfos.get(k).toString());
                                    /**
                                     public class CardInfo {
                                     public int suit;
                                     public int member;
                                     public String name;
                                     }**/
                                    cardInfo.suit = subCardObject.getInt("suit");
                                    cardInfo.member = subCardObject.getInt("member");
                                    cardInfo.name = subCardObject.getString("name");
                                }
                                subCardInfoList[k] = cardInfo;
                            }


                            roundUserInfo.card = subCardInfoList;

                            roundUserInfo.lastaction = cardObject.getString("lastaction");
                            roundUserInfo.winchips = cardObject.getInt("winchips");
                            roundUserInfo.wincard = cardObject.getString("wincard");
                            roundUserInfo.win27chips = cardObject.getInt("win27chips");
                            roundUserInfo.winsurance = cardObject.getInt("winsurance");
                            roundUserInfo.isshowcard = cardObject.getBoolean("isshowcard");
                        }

                        roundUserList.add(roundUserInfo);
                    }
                roundInfo.rounduserinfo = roundUserList;
                mRecordList.add(roundInfo);
            }
        } catch (Exception e){
            Log.d("GameRecordActivity", e.getMessage());
            ToastUtil.showToastInScreenCenter(this,"获取数据失败!");
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.tv_back:
                finish();
                return ;
            case R.id.collect:
                return ;
            case R.id.firstpage:
                firstPage();
                return ;
            case R.id.frontpage:
                prePage();
                return ;
            case R.id.nextpage:
                nextPage();
                return ;
            case R.id.lastpage:
                lastPage();
                return ;
        }
    }

    private int getTotalPages(){
        return mRecordList.size();
    }

    private void nextPage(){
        if( getTotalPages()>=1 && mCurrentPage + 1 < getTotalPages()){
            mLoopBackPager.setCurrentItem(mCurrentPage+1);
            mCurrentPage = mCurrentPage+1;
            mLoopBackPager.setCurrentItem(mCurrentPage);
            updatePageText();
        }
    }

    private void prePage(){
        if( getTotalPages()>=1 && mCurrentPage - 1 >=0){
            mLoopBackPager.setCurrentItem(mCurrentPage-1);
            mCurrentPage = mCurrentPage-1;
            mLoopBackPager.setCurrentItem(mCurrentPage);
            updatePageText();
        }
    }

    private void firstPage(){
        if( getTotalPages()>=1 ){
            mLoopBackPager.setCurrentItem(0);
            mCurrentPage = 0;
            mLoopBackPager.setCurrentItem(mCurrentPage);
            updatePageText();
        }
    }

    private void lastPage(){
        if( getTotalPages()>=1 ){
            mLoopBackPager.setCurrentItem(getTotalPages()-1);
            mCurrentPage = getTotalPages()-1;
            mLoopBackPager.setCurrentItem(mCurrentPage);
            updatePageText();
        }
    }

    private void updatePageText(){
        mPageText.setText( (mCurrentPage + 1)+ "/" + getTotalPages());
    }

    private class MyAdapter extends PagerAdapter {

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public int getCount() {
            return mLoopBackViewList.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position,
                                Object object) {
            container.removeView(mLoopBackViewList.get(position));

        }

        @Override
        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return ""  ;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            View containerView = mLoopBackViewList.get(position);

            if ( containerView instanceof ViewGroup){
                containerView = fillView((ViewGroup)containerView, position);

                container.addView(containerView);
                return containerView;
            }
            return null;
        }

        private View fillView(ViewGroup view, int position){

            RoundInfo roundInfo = mRecordList.get(position);

            TextView timeView = (TextView)view.findViewById(R.id.start_time);
            timeView.setText(Util.getFormatDateTime(GameRecordLookBack.this, roundInfo.starttime).toString());

            TextView poxView = (TextView)view.findViewById(R.id.pox);
            poxView.setText(roundInfo.pot + "");

            RecyclerView mRVLookBack = (RecyclerView)view.findViewById(R.id.rv_lookback);
            mRVLookBack.setLayoutManager(new GridLayoutManager(GameRecordLookBack.this,1));

            mRVLookBack.setHasFixedSize(true);
            mRVLookBack.addItemDecoration(new DividerItemDecoration(
                    GameRecordLookBack.this, DividerItemDecoration.VERTICAL));

            GameLookBackUserInfoAdapter adapter = new GameLookBackUserInfoAdapter(GameRecordLookBack.this, roundInfo.cards);
            mRVLookBack.setAdapter(adapter);
            adapter.setData(roundInfo.rounduserinfo);

            return view;
        }
    }
}
