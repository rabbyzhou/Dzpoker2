package com.yijian.dzpoker.activity.user;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yijian.dzpoker.R;
import com.yijian.dzpoker.constant.Constant;
import com.yijian.dzpoker.util.DzApplication;
import com.yijian.dzpoker.util.ToastUtil;
import com.yijian.dzpoker.utils.DialogCreator;
import com.yijian.dzpoker.view.OpenRingProgressView;
import com.yijian.dzpoker.view.data.GameRecord;

import org.json.JSONObject;

import okhttp3.Request;
import okhttp3.Response;

import static com.yijian.dzpoker.constant.Constant.INTENT_KEY_BACKTEXT;

/**
 * Created by c_huangl on 0011, 11/11/2017.
 */

public class GamesRecordActivity extends Activity
    implements View.OnClickListener {

    private String title = "战绩";

    TextView mWinChipsText;
    TextView mBackText;
    TextView mTitle;

    TextView mAllGamesText;
    TextView mAllRoundsText;
    TextView mSNG;
    TextView mMT_SNG;
    TextView mMTT;
    TextView mRoundslinkText;
    TextView mBrandslinkText;
    LinearLayout mRoundsLinkLayout;
    LinearLayout mBrandsLinkLayout;
    OpenRingProgressView mRingProgress ;

    GameRecord mGameRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game_record);
        initViews();

        new QueryDataTask().execute();
    }

    private void initViews() {

        //标题栏
        mWinChipsText = (TextView) findViewById(R.id.tv_win_chips);
        mBackText = (TextView) findViewById(R.id.tv_back);
        mTitle = (TextView) findViewById(R.id.tv_title);

        String backText = getIntent().getStringExtra(INTENT_KEY_BACKTEXT);
        if (backText != null && !backText.isEmpty()) mBackText.setText(backText);

        String titleRet = getIntent().getStringExtra(Constant.INTENT_KEY_TITLE);
        if (titleRet == null || titleRet.isEmpty()) title = titleRet;
        mTitle.setText(title);

        mBackText.setOnClickListener(this);

        //战绩总记录
        mAllGamesText = (TextView) findViewById(R.id.all_games);
        mAllRoundsText = (TextView) findViewById(R.id.all_rounds);
        mSNG = (TextView) findViewById(R.id.all_sng);
        mMT_SNG = (TextView) findViewById(R.id.all_mt_sng);
        mMTT = (TextView) findViewById(R.id.all_mtt);
        mRoundslinkText = (TextView) findViewById(R.id.rounds_link_desc);
        mBrandslinkText = (TextView) findViewById(R.id.brands_link_desc);

        mRoundsLinkLayout = (LinearLayout)findViewById(R.id.lv_rounds_link);
        mBrandsLinkLayout = (LinearLayout)findViewById(R.id.lv_brands_link);

        mRingProgress = (OpenRingProgressView)findViewById(R.id.open_ring_progress);



        mRoundsLinkLayout.setOnClickListener(this);
        mBrandsLinkLayout.setOnClickListener(this);

    }

    private void updateUI() {

        mWinChipsText.setText(mGameRecord.allwinchips + "");
        mAllGamesText.setText(mGameRecord.allgames + "");
        mAllRoundsText.setText(mGameRecord.allrounds + "");

        int[] percents = new int[]{mGameRecord.allplaypercent,mGameRecord.allwinpercent};
        int[] colors = new int[]{R.color.game_play_precent_progress, R.color.game_win_precent_progress};
        int[] texts = new int[]{R.string.all_play_precent, R.string.all_win_precent};
        int[] textColors = new int[]{R.color.game_play_precent_text, R.color.game_win_precent_text};
        mRingProgress.setProgress(percents, colors, texts, textColors);

        int allsng = mGameRecord.allsng;
        //int allmt_sng = mGameRecord.allmtsng;
        int allmtt = mGameRecord.allmtt;

        mSNG.setText(getString(R.string.rounds_subtype_gams,allsng));
        mMT_SNG.setText(getString(R.string.rounds_subtype_gams,0));
        mMTT.setText(getString(R.string.rounds_subtype_gams,allmtt));
        mRoundslinkText.setText(getString(R.string.rounds_link_desc, mGameRecord.allgames));
    }

    private void queryData(){
        try{

            JSONObject jsonParam= new JSONObject();
            jsonParam.put("userid",((DzApplication)getApplication()).getUserId());
            String strURL=getString(R.string.url_remote)+"func=getmygamerecord&param="+jsonParam.toString();

            Request request = new Request.Builder().url(strURL).build();
            Response response = DzApplication.getHttpClient().newCall(request).execute();

            String result=response.body().string();
            JSONObject jsonObject=new JSONObject(result);

            mGameRecord = new GameRecord();

            /**
             * public class GameRecord {

                 public int allgames;
                 public int allrounds;
                 public int allwinchips;
                 public int allplaypercent;
                 public int allwinpercent;
                 public int allmatchwinchips;
                 public int allsng;
                 public int allmtt;

             }
             */
            mGameRecord.allgames=jsonObject.getInt("allgames");
            mGameRecord.allrounds=jsonObject.getInt("allrounds");
            mGameRecord.allwinchips= jsonObject.getInt("allwinchips");
            mGameRecord.allplaypercent=jsonObject.getInt("allplaypercent");
            mGameRecord.allwinpercent=jsonObject.getInt("allwinpercent");
            mGameRecord.allmatchwinchips=jsonObject.getInt("allmatchwinchips");
            mGameRecord.allsng=jsonObject.getInt("allsng");
            mGameRecord.allmtt=jsonObject.getInt("allmtt");

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
            case R.id.lv_rounds_link:
                goToRoundPage();
                break;
            case R.id.lv_brands_link:
                goToBrandPage();
                break;
        }
    }

    private void goToRoundPage(){
        Intent intent = new Intent(this, GameHistoryRecordActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		intent.putExtra(INTENT_KEY_BACKTEXT, title);
        startActivity(intent);
    }

    private void goToBrandPage(){

    }

    private class QueryDataTask extends AsyncTask<String, Integer, String> {

        private Dialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = DialogCreator.createLoadingDialog(GamesRecordActivity.this,GamesRecordActivity.this.getResources().getString(R.string.jmui_loading));
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
