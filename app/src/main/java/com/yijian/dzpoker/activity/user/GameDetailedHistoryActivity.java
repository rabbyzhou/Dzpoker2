package com.yijian.dzpoker.activity.user;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.yijian.dzpoker.R;
import com.yijian.dzpoker.activity.base.BaseBackActivity;
import com.yijian.dzpoker.activity.base.BaseToolbarActivity;
import com.yijian.dzpoker.util.DzApplication;
import com.yijian.dzpoker.util.ToastUtil;
import com.yijian.dzpoker.util.Util;
import com.yijian.dzpoker.utils.DialogCreator;
import com.yijian.dzpoker.view.CircleTransform;
import com.yijian.dzpoker.view.adapter.GameTableUserAdapter;
import com.yijian.dzpoker.view.data.GameTableRecord;
import com.yijian.dzpoker.view.data.GameTableUserInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;

import okhttp3.Request;
import okhttp3.Response;

import static com.yijian.dzpoker.constant.Constant.INTENT_KEY_BACKTEXT;

/**
 * Created by c_huangl on 0011, 11/11/2017.
 */

public class GameDetailedHistoryActivity extends BaseBackActivity
        implements View.OnClickListener {


    private final static String TITLE = "战绩详情";

    TextView mBackText;
    TextView mTitle;
    ImageView mReviewEnter;

    TextView mTotalTimeDesc;
    TextView mBlindSize;
    TextView mType;
    TextView mClubName;
    TextView mAllHands;
    TextView mAllJoin;
    TextView mMaxPOT;
    TextView mRoomName;
    TextView mRoomOwner;
    ImageView mSharkHead;
    ImageView mBigFishHead;
    ImageView mRichHead;
    ImageView mWorkerModelHead;
    TextView mSharkName;
    TextView mBigFishName;
    TextView mRichName;
    TextView mWorkerModelName;
    TextView mSurancepot;
    TextView mJackPot;
    RecyclerView mUsersRView;

    private int mTableId;
    private GameTableRecord mGameTableRecord;
    private String[] mTopFourPic = new String[4];
    private String[] mTopFourName = new String[4];
    private GameTableUserAdapter mAdapter;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTableId = getIntent().getIntExtra("gametableid", -1);
        initViews();
        setToolbarTitle("战绩详情");
        showToolbarRightView(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLookBackPage();
            }
        });
//        getOverflowMenu();

        new QueryDataTask().execute();
    }

    private void getOverflowMenu() {

        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if(menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_game_detail_history;
    }

    @Override
    public void initViews() {
        //标题栏
        mReviewEnter = (ImageView) findViewById(R.id.review_enter);
        mBackText = (TextView) findViewById(R.id.tv_back);
        mTitle = (TextView) findViewById(R.id.tv_title);

        String backText = getIntent().getStringExtra(INTENT_KEY_BACKTEXT);
        if (backText != null && !backText.isEmpty()) {
            mBackText.setText(backText);
        }

        mTitle.setText(TITLE);
		
        mBackText.setOnClickListener(this);
        mReviewEnter.setOnClickListener(this);

        mTotalTimeDesc = (TextView)findViewById(R.id.total_time_desc);
        mBlindSize = (TextView)findViewById(R.id.blind_size);
        mType = (TextView)findViewById(R.id.type);
        mClubName = (TextView)findViewById(R.id.club_name);
        mAllHands = (TextView)findViewById(R.id.all_hands);
        mAllJoin = (TextView)findViewById(R.id.all_join);
        mMaxPOT = (TextView)findViewById(R.id.max_pot);
        mRoomName = (TextView)findViewById(R.id.room_name);
        mRoomOwner = (TextView)findViewById(R.id.room_owner);
        mSharkHead = (ImageView) findViewById(R.id.shark_head);
        mBigFishHead = (ImageView) findViewById(R.id.big_fish_head);
        mRichHead = (ImageView) findViewById(R.id.rish_head);
        mWorkerModelHead = (ImageView) findViewById(R.id.worker_model_head);
        mSharkName = (TextView)findViewById(R.id.shark_name);
        mBigFishName = (TextView)findViewById(R.id.big_fish_name);
        mRichName = (TextView)findViewById(R.id.rich_name);
        mWorkerModelName = (TextView)findViewById(R.id.worker_model_name);
        mSurancepot = (TextView)findViewById(R.id.surancepot);
        mJackPot = (TextView)findViewById(R.id.jackpot);

        mUsersRView = (RecyclerView)findViewById(R.id.detail_list);
        mUsersRView.setLayoutManager(new GridLayoutManager(this,1));
        mUsersRView.setHasFixedSize(true);

        mUsersRView.addItemDecoration(new DividerItemDecoration(
                this, DividerItemDecoration.VERTICAL));

        mAdapter = new GameTableUserAdapter(this, new GameTableUserAdapter.OnRecordSelectListener() {
            @Override
            public void onRecordSelected(final GameTableUserInfo data) {

            }
        });
        DividerItemDecoration decoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        decoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.store_user_level_list_divide_drawable));
        mUsersRView.addItemDecoration(decoration);
        mUsersRView.setAdapter(mAdapter);



    }

    private void updateUI(){
        if (null == mGameTableRecord) {
            return;
        }

        //  = 需要解析
		String startDate = Util.getFormatDate(this,mGameTableRecord.starttime);
        String startTime = Util.getFormatTime(this,mGameTableRecord.starttime);
		String endDate = Util.getFormatDate(this,mGameTableRecord.endtime);
		String endTime = Util.getFormatTime(this,mGameTableRecord.endtime);
		String duration = Util.getTimeDuration(mGameTableRecord.starttime, mGameTableRecord.endtime);
		mTotalTimeDesc.setText(getResources().getString(
			R.string.game_detail_history_time_desc,startDate,startTime,endDate,endTime,duration));
        mBlindSize.setText(mGameTableRecord.sb + "/" + mGameTableRecord.bb);
        mType.setText(getString(R.string.game_detail_history_max_player, mGameTableRecord.maxplayers));
        mClubName.setText(mGameTableRecord.clubname);
        mAllHands.setText(mGameTableRecord.allrounds + "");
        mAllJoin.setText(mGameTableRecord.alltakein + "");
        mMaxPOT.setText(mGameTableRecord.maxpot + "");
        mRoomName.setText(mGameTableRecord.gametablename);
        mRoomOwner.setText(mGameTableRecord.createusernickname);

        if ( mTopFourPic[0] != null && !mTopFourPic[0].isEmpty()){
            Picasso.with(this)
                    .load(mTopFourPic[0])
                    .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    //.resize(100, 100)
					.transform(new CircleTransform())
                    .into(mSharkHead);
        }

        if ( mTopFourPic[1] != null && !mTopFourPic[1].isEmpty()){
            Picasso.with(this)
                    .load(mTopFourPic[1])
                    .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    //.resize(100, 100)
					.transform(new CircleTransform())
                    .into(mBigFishHead);
        }

        if ( mTopFourPic[2] != null && !mTopFourPic[2].isEmpty()){
            Picasso.with(this)
                    .load(mTopFourPic[2])
                    .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    //.resize(100, 100)
					.transform(new CircleTransform())
                    .into(mRichHead);
        }

        if ( mTopFourPic[3] != null && !mTopFourPic[3].isEmpty()){
            Picasso.with(this)
                    .load(mTopFourPic[3])
                    .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    //.resize(100, 100)
					.transform(new CircleTransform())
                    .into(mWorkerModelHead);
        }

        if ( mTopFourName[0] != null && !mTopFourName[0].isEmpty()){
            mSharkName.setText(mTopFourName[0] + "");
        }

        if ( mTopFourName[1] != null && !mTopFourName[1].isEmpty()){
            mBigFishName.setText(mTopFourName[1] + "");
        }

        if ( mTopFourName[2] != null && !mTopFourName[2].isEmpty()){
            mRichName.setText(mTopFourName[2] + "");
        }

        if ( mTopFourName[3] != null && !mTopFourName[3].isEmpty()){
            mWorkerModelName.setText(mTopFourName[3] + "");
        }

        mSurancepot.setText(mGameTableRecord.surancepot + "");
        mJackPot.setText(mGameTableRecord.jackpot + "");

        if ( mGameTableRecord.gametableuser != null){
            mAdapter.setData(mGameTableRecord.gametableuser);
        }
    }

    private void queryData(){
        try{

            JSONObject jsonParam= new JSONObject();
            jsonParam.put("gametableid",mTableId);
            String strURL=getString(R.string.url_remote)+"func=getgametablerecord&param="+jsonParam.toString();

            Request request = new Request.Builder().url(strURL).build();
            Response response = DzApplication.getHttpClient().newCall(request).execute();

            String result=response.body().string();
            JSONObject jsonObject=new JSONObject(result);

            mGameTableRecord = new GameTableRecord();


            /**
             public class GameTableRecord {

                 public String clubname;
                 public String gametablename;
                 public int bb;
                 public int sb;
                 public String endtime;
                 public String starttime;
                 public String createusernickname;
                 public int type;  //0 德州 1 奥马哈
                 public int maxplayers;
                 public int surancepot;
                 public int jackpot;
                 public int allrounds;
                 public int maxpot;
                 public int alltakein;
                 public List<GameTableUserInfo> gametableuser;

             }
             */
            mGameTableRecord.clubname=jsonObject.getString("clubname");
            mGameTableRecord.gametablename=jsonObject.getString("gametablename");
            mGameTableRecord.bb= jsonObject.getInt("bb");
            mGameTableRecord.sb=jsonObject.getInt("sb");
            mGameTableRecord.endtime=jsonObject.getString("endtime");
            mGameTableRecord.starttime=jsonObject.getString("starttime");
            mGameTableRecord.createusernickname=jsonObject.getString("createusernickname");
            mGameTableRecord.type=jsonObject.getInt("type");
            mGameTableRecord.maxplayers=jsonObject.getInt("maxplayers");
            mGameTableRecord.surancepot=jsonObject.getInt("surancepot");
            mGameTableRecord.jackpot=jsonObject.getInt("jackpot");
            mGameTableRecord.allrounds=jsonObject.getInt("allrounds");
            mGameTableRecord.maxpot=jsonObject.getInt("maxpot");
            mGameTableRecord.alltakein=jsonObject.getInt("alltakein");

            JSONArray array =new JSONArray(jsonObject.getString("gametableuser"));

            ArrayList<GameTableUserInfo> infoList = new ArrayList<GameTableUserInfo>();
            for(int i=0;i<array.length();i++) {

                JSONObject object =new JSONObject(array.get(i).toString());
                GameTableUserInfo info = new GameTableUserInfo();
                /**
                 public class GameTableUserInfo {

                     public int takeinchips;
                     public int winchips;
                     public String nickname;
                     public String headpic;
                     public int id;
                     public int allhands;

                 }
                 */
                info.takeinchips = object.getInt("takeinchips");
                info.winchips = object.getInt("winchips");
                info.nickname = object.getString("nickname");
                info.headpic = object.getString("headpic");
                info.id = object.getInt("id");
                info.allhands = object.getInt("allhands");

                if ( i <= 3) {
                    mTopFourPic[i] =info.headpic;
                    mTopFourName[i] = info.nickname;
                }

                infoList.add(info);
            }

            mGameTableRecord.gametableuser = infoList;

        } catch (Exception e){
            Log.d("GameRecordActivity", e.getMessage());
            ToastUtil.showToastInScreenCenter(this,"获取数据失败!");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_back:
                setResult(RESULT_OK);
                finish();
                break;
            case R.id.review_enter:
                goToLookBackPage();
                break;
        }
    }

    private void goToLookBackPage(){
        Intent intent = new Intent(this, GameRecordLookBack.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		intent.putExtra(INTENT_KEY_BACKTEXT, TITLE);
		intent.putExtra("gametableid", mTableId);
        startActivity(intent);
    }

    private class QueryDataTask extends AsyncTask<String, Integer, String> {

        private Dialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = DialogCreator.createLoadingDialog(GameDetailedHistoryActivity.this,
                    GameDetailedHistoryActivity.this.getResources().getString(R.string.jmui_loading));
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
            updateUI();
            super.onPostExecute(s);
        }
    }
}
