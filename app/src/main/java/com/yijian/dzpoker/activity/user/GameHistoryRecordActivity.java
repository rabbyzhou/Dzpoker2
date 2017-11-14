package com.yijian.dzpoker.activity.user;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.yijian.dzpoker.R;
import com.yijian.dzpoker.activity.base.BaseBackActivity;
import com.yijian.dzpoker.constant.Constant;
import com.yijian.dzpoker.util.DzApplication;
import com.yijian.dzpoker.util.ToastUtil;
import com.yijian.dzpoker.utils.DialogCreator;
import com.yijian.dzpoker.view.adapter.GameHistoryRecordAdapter;
import com.yijian.dzpoker.view.data.HistoryGameTableInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.Request;
import okhttp3.Response;
import static com.yijian.dzpoker.constant.Constant.INTENT_KEY_BACKTEXT;

/**
 * Created by c_huangl on 0011, 11/11/2017.
 */

public class GameHistoryRecordActivity extends BaseBackActivity
    implements View.OnClickListener{

    private final static String TITLE = "牌局信息";

    private RecyclerView mReView;
    private TextView mNoDataView;
    private ArrayList<HistoryGameTableInfo> mRecordList = new ArrayList<HistoryGameTableInfo>();

    private GameHistoryRecordAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_game_history;
    }

    @Override
    protected void initViews() {
        super.initViews();

        String backText = getIntent().getStringExtra(Constant.INTENT_KEY_BACKTEXT);
        if (backText != null && !backText.isEmpty()) tv_back.setText(backText);

        tv_back.setOnClickListener(this);
		tv_title.setText(TITLE);

        mReView = (RecyclerView) findViewById(R.id.rv_history);
        mNoDataView = (TextView) findViewById(R.id.tv_no_data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mReView.setLayoutManager(new GridLayoutManager(this,1));
        mReView.setHasFixedSize(true);

        mAdapter = new GameHistoryRecordAdapter(this, new GameHistoryRecordAdapter.OnRecordSelectListener() {
            @Override
            public void onRecordSelected(final HistoryGameTableInfo data) {
                
				//总手数为0, 不进入
				if ( data.joinhands == 0) return;
				
				//进入 战绩详情
                Intent intent = new Intent(GameHistoryRecordActivity.this,
                        GameDetailedHistoryActivity.class);
                intent.putExtra("gametableid", data.gametableid);
				intent.putExtra(INTENT_KEY_BACKTEXT, TITLE);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });

        mReView.setAdapter(mAdapter);

        new QueryDataTask().execute();
    }

    private class QueryDataTask extends AsyncTask<String, Integer, String> {

        private Dialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = DialogCreator.createLoadingDialog(GameHistoryRecordActivity.this,
                    GameHistoryRecordActivity.this.getResources().getString(R.string.jmui_loading));
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
                mReView.setVisibility(View.GONE);
            }else {
                mNoDataView.setVisibility(View.GONE);
                mReView.setVisibility(View.VISIBLE);
                mAdapter.setData(mRecordList);
            }

            super.onPostExecute(s);
        }
    }

    private void queryData(){
        try{

            JSONObject jsonParam= new JSONObject();
            jsonParam.put("userid",((DzApplication)getApplication()).getUserId());
            String strURL=getString(R.string.url_remote)+"func=getmyhistorygametable&param="+jsonParam.toString();

            Request request = new Request.Builder().url(strURL).build();
            Response response = DzApplication.getHttpClient().newCall(request).execute();
            String result=response.body().string();

            JSONObject jObject = new JSONObject(result);

            JSONArray list= jObject.getJSONArray("hisgametable");

            for(int i=0;i<list.length();i++) {

                JSONObject jsonObject=new JSONObject(list.get(i).toString());
                HistoryGameTableInfo history = new HistoryGameTableInfo();

                /**
                 public class HistoryGameTableInfo {

                 public int gametableid;
                 public String starttime;
                 public String gametablename;
                 public String clubname;
                 public int sb;
                 public int bb;
                 public int durationmin;
                 public int joinhands;
                 public int userwinchips;
                 public String createuserheadpic;

                 }             */
                history.gametableid = jsonObject.getInt("gametableid");
                history.starttime = jsonObject.getString("starttime");
                history.gametablename = jsonObject.getString("gametablename");
                history.clubname = jsonObject.getString("clubname");
                history.sb = jsonObject.getInt("sb");
                history.bb = jsonObject.getInt("bb");
                history.durationmin = jsonObject.getInt("durationmin");
                history.joinhands = jsonObject.getInt("joinhands");
                history.userwinchips = jsonObject.getInt("userwinchips");
                history.createuserheadpic = jsonObject.getString("createuserheadpic");

                mRecordList.add(history);
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
        }
    }
}
