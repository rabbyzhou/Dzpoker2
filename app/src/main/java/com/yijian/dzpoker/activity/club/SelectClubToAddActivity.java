package com.yijian.dzpoker.activity.club;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.yijian.dzpoker.R;
import com.yijian.dzpoker.activity.base.BaseToolbarActivity;
import com.yijian.dzpoker.constant.Constant;
import com.yijian.dzpoker.util.DzApplication;
import com.yijian.dzpoker.util.ToastUtil;
import com.yijian.dzpoker.view.adapter.SearchClubAdapter;
import com.yijian.dzpoker.view.data.ClubInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;
import okhttp3.Response;

public class SelectClubToAddActivity extends BaseToolbarActivity {

    private List<ClubInfo> mClubInfoList=new ArrayList<ClubInfo>();
    private String strLoginName;
    private int userId;
    private RecyclerView rv_club_list;
    private LinearLayoutManager mLayoutManager ;
    private SearchClubAdapter mAdapter;
    private TextView exitView;

    public  final static int MESAGE_GETCLUBLIST_OK=0x1001;


    private Handler handler = new Handler() {

        // 该方法运行在主线程中
        // 接收到handler发送的消息，对UI进行操作
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            if (msg.what == MESAGE_GETCLUBLIST_OK) {
                mAdapter.setData(mClubInfoList);
            }

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_club_to_add);
        initViews();
        setToolbarTitle("搜索结果");
        SharedPreferences settings = getSharedPreferences("depoker", 0);
        strLoginName=settings.getString("username","");
        userId=settings.getInt("userid",0);
        Intent intent = this.getIntent();
        searchClub(intent.getStringExtra("condition"),intent.getIntExtra("serachtype",0));
    }

    private void initViews(){
        rv_club_list=(RecyclerView)findViewById(R.id.rv_club_list);
        //创建默认的线性LayoutManager
        mLayoutManager = new LinearLayoutManager(this);
        rv_club_list.setLayoutManager(mLayoutManager);
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        rv_club_list.setHasFixedSize(true);
        rv_club_list.addItemDecoration(new DividerItemDecoration(
                this, DividerItemDecoration.VERTICAL));

        exitView = (TextView)findViewById(R.id.tv_exit);
        exitView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent intent = getIntent();
        String backText = intent.getStringExtra(Constant.INTENT_KEY_BACKTEXT);
        if ( backText != null && !backText.isEmpty()){
            exitView.setText(backText);
        }
        mAdapter = new SearchClubAdapter(this,exitView.getText().toString()) ;
        rv_club_list.setAdapter(mAdapter);

    }

    //通用的搜索俱乐部列表方法  1：搜索框搜索 2：热门城市搜索  3：省份搜索
    private void searchClub(final String condition,final int serachType){
        //将输入的参数传递到列表显示activity


        /**
         * searchclubbyhotcity  param :city
         * searchclubbyprovince param province
         * searchclubbynameorid param clubnameorid
         */


        Thread thread=new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try{
                    //获取热门城市列表
                    //拼装url字符串
                    JSONObject jsonObj = new JSONObject();
                    jsonObj.put("userid",userId);
                    String funcName="";
                    switch (serachType){
                        case 1:
                            jsonObj.put("clubnameorid",condition);
                            funcName="searchclubbynameorid";
                            break;
                        case 2:
                            jsonObj.put("city",condition);
                            funcName="searchclubbyhotcity";
                            break;
                        case 3:
                            jsonObj.put("province",condition);
                            funcName="searchclubbyprovince";
                            break;

                    }
                    mClubInfoList=new ArrayList<ClubInfo>();

                    String strURL=getString(R.string.url_remote);
                    strURL+="func="+funcName+"&param="+jsonObj.toString();
                    URL url = new URL(strURL);
                    Request request = new Request.Builder().url(strURL).build();
                    Response response = DzApplication.getHttpClient().newCall(request).execute();
                    String result=response.body().string();
                    JSONArray jsonClubList=new JSONArray(result);
                    for(int i=0;i<jsonClubList.length();i++){
                        /*Int id
                         String clubname
                         String headpic
                         String clubprovince
                         String clubcity
                         String clubabstract
                         int maxpeople
                         int curpeople
                         int createuserid
                         string nickname //创建用户昵称
                         string headpic1 //创建用户头像
                         string useridclub //查询用户是否在此club中  true/false

*/
                        ClubInfo clubInfo=new ClubInfo();
                        JSONObject jsonObject=new JSONObject(jsonClubList.get(i).toString());
                        clubInfo.clubID=jsonObject.getInt("id");
                        clubInfo.clubName=jsonObject.getString("name");
                        clubInfo.clubHeadPic=jsonObject.getString("headpic");
                        clubInfo.maxCLubMemberNumber=jsonObject.getInt("maxpeople");
                        clubInfo.clubMemberNumber=jsonObject.getInt("curpeople");
                        clubInfo.createuserid=jsonObject.getInt("createuserid");
                        clubInfo.nickname=jsonObject.getString("nickname");
                        clubInfo.headpic1=jsonObject.getString("headpic");
                        clubInfo.location=jsonObject.getString("city");
                        //clubInfo.clubLevelName=jsonObject.getString("levelabstract");
                        if (jsonObject.getString("userinclub").equals("true"))
                        {
                            clubInfo.bInClub=true;
                        }else{
                            clubInfo.bInClub=false;
                        }
                        mClubInfoList.add(clubInfo);
                    }


                    //获取数据后，将俱乐部列表数据作为对象传入到下一activity，进行数据初始化
                    handler.sendEmptyMessage(MESAGE_GETCLUBLIST_OK);



                }catch (Exception e){
                    ToastUtil.showToastInScreenCenter(SelectClubToAddActivity.this,"从后台取数据报错，请稍后重试!");
                }

            }
        });
        thread.start();


    }


}
