package com.yijian.dzpoker.activity.club;

import android.content.Intent;
import android.media.Image;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.yijian.dzpoker.R;
import com.yijian.dzpoker.activity.base.BaseBackActivity;
import com.yijian.dzpoker.util.DzApplication;
import com.yijian.dzpoker.util.ToastUtil;
import com.yijian.dzpoker.view.CircleTransform;
import com.yijian.dzpoker.view.adapter.ClubListHorizontalAdapter;
import com.yijian.dzpoker.view.data.ClubInfo;
import com.yijian.dzpoker.view.data.User;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;
import okhttp3.Response;

public class ClubUserInfoActivity extends BaseBackActivity {
    private RecyclerView rv_club_user_list;
    private TextView tv_added_club,tv_user_name,tv_user_location,tv_user_personltip;
    private ImageView iv_user_head;
    private User mUser=new User();
    private LinearLayoutManager mLayoutManager ;
    private ClubListHorizontalAdapter mAdapter;
    private List<ClubInfo> mClubList=new ArrayList<ClubInfo>();
    private  final static int MESAGE_GETUSERCLUB_OK=0x1001;//获取俱乐部信息

    private Handler handler = new Handler() {

        // 该方法运行在主线程中
        // 接收到handler发送的消息，对UI进行操作
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what){
                case MESAGE_GETUSERCLUB_OK:
                    mAdapter.setData(mClubList);
                    tv_added_club.setText("对方加入的俱乐部("+mClubList.size() +")");

            }
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.activity_club_user_info;
    }

    @Override
    protected void initViews() {
        super.initViews();
        rv_club_user_list=(RecyclerView)findViewById(R.id.rv_club_user_list);
        tv_added_club=(TextView)findViewById(R.id.tv_added_club);
        tv_user_name=(TextView)findViewById(R.id.tv_user_name);
        tv_user_location=(TextView)findViewById(R.id.tv_user_location);
        tv_user_personltip=(TextView)findViewById(R.id.tv_user_personltip);
        iv_user_head=(ImageView)findViewById(R.id.iv_user_head);
        //创建默认的线性LayoutManager
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rv_club_user_list.setLayoutManager(mLayoutManager);
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        rv_club_user_list.setHasFixedSize(true);

        //创建并设置Adapter
        mAdapter = new ClubListHorizontalAdapter(this);
        rv_club_user_list.setAdapter(mAdapter);
//        tv_back.setOnClickListener(this);
        //TODO:qipu

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUser=(User)getIntent().getSerializableExtra("user");
        initData();

    }

    public void initData(){
        tv_user_name.setText(mUser.nickName);
        if (mUser.province!=null && !mUser.province.equals("null")) {
            tv_user_location.setText(mUser.province + mUser.city);
        }
        if (mUser.province!=null && !mUser.province.equals("null")) {
        tv_user_personltip.setText(mUser.personalTip);
        }else{
            tv_user_personltip.setText("暂无简介");
        }
        if (mUser.userHeadPic!=null) {
            Picasso.with(getApplicationContext())
                    .load(mUser.userHeadPic)
                    .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .resize(100, 100)
                    .error(R.drawable.default_male_head)
                    .transform(new CircleTransform())
                    .into(iv_user_head);
        }

        Thread thread=new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try{
                    //拼装url字符串
                    JSONObject jsonObj = new JSONObject();
                    jsonObj.put("userid",mUser.userId);
                    String strURL=remoteURL+"func=getmyclub&param="+jsonObj.toString();

                    Request request = new Request.Builder().url(strURL).build();
                    Response response = DzApplication.getHttpClient().newCall(request).execute();
                    String result=response.body().string();
                    mClubList=new ArrayList<ClubInfo>();
                    JSONArray jsonClubList=new JSONArray(result);
                    for(int i=0;i<jsonClubList.length();i++){
                        ClubInfo clubInfo=new ClubInfo();
                        JSONObject jsonObject=new JSONObject(jsonClubList.get(i).toString());
                        clubInfo.clubID=jsonObject.getInt("clubid");
                        clubInfo.clubName=jsonObject.getString("name");
                        if (jsonObject.getString("headpic")!=null && !jsonObject.getString("headpic").equals("null"))
                        {clubInfo.clubHeadPic=jsonObject.getString("headpic");}
                        mClubList.add(clubInfo);

                    }
                    handler.sendEmptyMessage(MESAGE_GETUSERCLUB_OK);
                }catch (Exception e){
                    ToastUtil.showToastInScreenCenter(ClubUserInfoActivity.this,"取俱乐部列表俱乐部失败，请稍后重试!");
                }
            }
        });
        thread.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_back:
                finish();
        }
    }

}
