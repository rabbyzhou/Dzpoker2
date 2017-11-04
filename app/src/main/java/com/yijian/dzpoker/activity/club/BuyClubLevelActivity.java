package com.yijian.dzpoker.activity.club;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
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
import com.yijian.dzpoker.activity.user.BuyDiamondActivity;
import com.yijian.dzpoker.util.DzApplication;
import com.yijian.dzpoker.util.ToastUtil;
import com.yijian.dzpoker.view.CircleTransform;
import com.yijian.dzpoker.view.adapter.ClubLevelListAdapter;
import com.yijian.dzpoker.view.adapter.DiamonsListAdapter;
import com.yijian.dzpoker.view.data.ClubInfo;
import com.yijian.dzpoker.view.data.ClubLevel;
import com.yijian.dzpoker.view.data.DiamondStoreGoods;
import com.yijian.dzpoker.view.data.User;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;
import okhttp3.Response;

public class BuyClubLevelActivity extends BaseBackActivity {

    private RecyclerView rv_club_level_list;
    private TextView tv_current_user_diamond,tv_current_club_level;
    private ImageView iv_club_level_head;
    private ClubLevelListAdapter mAdapter;
    private List<ClubLevel> mClubLevelList=new ArrayList<ClubLevel>();
    private ClubInfo mClubInfo=new ClubInfo();//这里只记录照片途径，还有俱乐部等级

    public final static int REQUEST_BUY_DIAMONDS = 4;



    private final int MESSAGE_GET_CLUBLEVELINFO_OK=0x1001;
    private final int MESSAGE_BUY_CLUB_LEVEL_OK=0x1002;
    private final int MESSAGE_GETCLUBINFO_OK=0x1003;
    private final int MESSAGE_GET_USERINFO_OK=0x1004;




    private Handler handler = new Handler() {

        // 该方法运行在主线程中
        // 接收到handler发送的消息，对UI进行操作
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what){
                case MESSAGE_GET_CLUBLEVELINFO_OK:
                    mAdapter.setData(mClubLevelList);
                    break;
                case MESSAGE_BUY_CLUB_LEVEL_OK:


                    break;
                case MESSAGE_GETCLUBINFO_OK:
                    //更新界面的俱乐部等级，俱乐部等级图片
                    if (mClubInfo.levelpic!=null && !mClubInfo.levelpic.equals("")){
                        Picasso.with(getApplicationContext())
                                .load(mClubInfo.levelpic)
                                .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                                .resize(100, 100)
                                .error(R.drawable.default_club_level)
                                .transform(new CircleTransform())
                                .into(iv_club_level_head);
                    }
                    tv_current_club_level.setText(mClubInfo.clubLevelName);
                    break;
                case MESSAGE_GET_USERINFO_OK:
                    tv_current_user_diamond.setText(application.getUser().diamond+"");
                    break;
            }


        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new ClubLevelListAdapter(this, new ClubLevelListAdapter.OnRecordSelectListener() {
            @Override
            public void onRecordSelected(final ClubLevel clubLevel) {
                //这里点击升级的俱乐部之后，必须判断钻石数目是否够，不够的话，直接到购买钻石界面
                if (clubLevel.costdiamonds>application.getUser().diamond){
                    //跳转到购买钻石界面
                    Intent intentBuyDiamons=new Intent();
                    intentBuyDiamons.setClass(BuyClubLevelActivity.this, BuyDiamondActivity.class);
                    startActivityForResult(intentBuyDiamons,  REQUEST_BUY_DIAMONDS);
                }else{

                        Thread thread=new Thread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                try{
                                    /* public class BuyMyClubLevelParam

                                        {


                                    返回
                                    public class BuyMyClubLevelRetParam

                                        {

                                            public int ret; //0成功 1没有足够金币 2其他失败

                                            public string msg; //其他失败信息

                                        }

                                    */
                                    JSONObject jsonParam= new JSONObject();
                                    jsonParam.put("userid",application.getUserId());
                                    jsonParam.put("clubid",application.getClubInfo().clubID);
                                    jsonParam.put("clublevelid",clubLevel.id);
                                    String strURL=getString(R.string.url_remote)+"func=buymyclublevel&param="+jsonParam.toString();
                                    URL url = new URL(strURL);
                                    Request request = new Request.Builder().url(strURL).build();
                                    Response response = DzApplication.getHttpClient().newCall(request).execute();
                                    String result=response.body().string();

                                    JSONObject jsonResult=new JSONObject(result);
                                    if (jsonResult.getInt("ret")==0){
                                        //取俱乐部信息更新俱乐部数据
                                        getClubInfo();
                                        getUserInfo();
                                        ToastUtil.showToastInScreenCenter(BuyClubLevelActivity.this,"购买成功!");

                                    }else{
                                        ToastUtil.showToastInScreenCenter(BuyClubLevelActivity.this,"提升俱乐部等级出错，请稍后重试!");
                                    }




                                }catch (Exception e){
                                    ToastUtil.showToastInScreenCenter(BuyClubLevelActivity.this,"提升俱乐部等级出错，请稍后重试!");
                                }
                            }
                        });
                        thread.start();

                }



            }
        });

        final ClubInfo clubInfo=application.getClubInfo();
        if (clubInfo.levelpic!=null && !clubInfo.levelpic.equals("")){
            Picasso.with(getApplicationContext())
                    .load(clubInfo.levelpic)
                    .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .resize(100, 100)
                    .error(R.drawable.default_club_level)
                    .transform(new CircleTransform())
                    .into(iv_club_level_head);
        }
        tv_current_club_level.setText(clubInfo.clubLevelName);
        tv_current_user_diamond.setText(application.getUser().diamond+"");

        rv_club_level_list.setLayoutManager(new LinearLayoutManager(this));
        rv_club_level_list.setHasFixedSize(true);
        rv_club_level_list.addItemDecoration(new DividerItemDecoration(
                this, DividerItemDecoration.VERTICAL));
        rv_club_level_list.setAdapter(mAdapter);
        //从服务器取购买俱乐部等级的列表
        Thread thread=new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try{
                    JSONObject jsonParam= new JSONObject();
                    String strURL=getString(R.string.url_remote)+"func=getclublevel&param="+jsonParam.toString();
                    URL url = new URL(strURL);
                    Request request = new Request.Builder().url(strURL).build();
                    Response response = DzApplication.getHttpClient().newCall(request).execute();
                    String result=response.body().string();

                    JSONArray jsonArray=new JSONArray(result);
                    mClubLevelList=new ArrayList<ClubLevel>();
                    for (int i=0;i<jsonArray.length();i++){
                        ClubLevel clubLevel=new ClubLevel();
                        JSONObject jsonObject=new JSONObject(jsonArray.get(i).toString());
                        clubLevel.id=jsonObject.getInt("id");
                        clubLevel.level=jsonObject.getInt("level");
                        //clubLevel.levelpic=jsonObject.getString("levelpic");
                        clubLevel.costdiamonds=jsonObject.getInt("costdiamonds");
                        clubLevel.levelabstract=jsonObject.getString("levelabstract");
                        clubLevel.maxmembers=jsonObject.getInt("maxmembers");
                        clubLevel.maxmanagers=jsonObject.getInt("maxmanagers");
                        clubLevel.durationdays=jsonObject.getInt("durationdays");

                        if (jsonObject.getString("levelpic")!=null && !jsonObject.getString("levelpic").equals("null")){
                            clubLevel.levelpic=jsonObject.getString("levelpic");
                        }
                        mClubLevelList.add(clubLevel);
                    }

                    handler.sendEmptyMessage(MESSAGE_GET_CLUBLEVELINFO_OK);

                }catch (Exception e){
                    ToastUtil.showToastInScreenCenter(BuyClubLevelActivity.this,"从服务取俱乐部等级数据出错，请稍后重试!");
                }
            }
        });
        thread.start();

    }

    private boolean getClubInfo()
    {
        try{
            //先根据clubid获得俱乐部的相关信息
                    /*[{"id":8,"createuserid":2,"createtime":"2017-08-25T14:44:19",
                    "province":"宁夏","city":"石嘴山市",
                    "clublevelid":1,"clublevelenddate":"2050-08-10T00:00:00",
                    "clubstate":0,"clubabstract":null,"headpic":"http://120.24.243.2:85/uploadfile/1503643456_file.jpg",
                    "name":"俱乐部","id1":1,"level":1,"levelabstract":"初级俱乐部","maxmembers":20,"maxmanagers":5,
                    "durationdays":36500,"costdiamonds":0,"clubcuruser":1,"clubid":8,"msgnotify":1,"clubid1":8}]*/
            JSONObject jsonParam= new JSONObject();
            jsonParam.put("userid",userId);
            jsonParam.put("clubid",application.getUserId());

            String strURL=remoteURL+"func=getmyclubinfo&param="+jsonParam.toString();
            URL url = new URL(strURL);
            Request request = new Request.Builder().url(strURL).build();
            Response response = DzApplication.getHttpClient().newCall(request).execute();
            String result=response.body().string();
            JSONObject jsonResult=new JSONObject((new JSONArray(result)).get(0).toString());

            mClubInfo=new ClubInfo();
            mClubInfo.clubID=jsonResult.getInt("clubid");
            mClubInfo.clubName=jsonResult.getString("name");
            if (jsonResult.getString("headpic")!=null&& !jsonResult.getString("headpic").equals("null")){
                mClubInfo.clubHeadPic=jsonResult.getString("headpic");}
            mClubInfo.maxCLubMemberNumber=jsonResult.getInt("maxmembers");
            mClubInfo.clubMemberNumber=jsonResult.getInt("clubcuruser");
            mClubInfo.location=jsonResult.getString("province")+jsonResult.getString("city");
            mClubInfo.clubLevelName=jsonResult.getString("levelabstract");
            mClubInfo.createuserid=jsonResult.getInt("createuserid");
            mClubInfo.clubabstract=jsonResult.getString("clubabstract");
            mClubInfo.clubCreatetime=jsonResult.getString("createtime");
            if (jsonResult.getString("levelpic")!=null&& !jsonResult.getString("levelpic").equals("null")){
                mClubInfo.levelpic=jsonResult.getString("headpic");}
            mClubInfo.province=jsonResult.getString("province");
            mClubInfo.city=jsonResult.getString("city");
            if(jsonResult.get("msgnotify")!=null){
            int iGetClubNotice=jsonResult.getInt("msgnotify");
                if (iGetClubNotice==1){
                    mClubInfo.bGetClubNotice=true;
                }else{
                    mClubInfo.bGetClubNotice=false;
            }
            }

            handler.sendEmptyMessage(MESSAGE_GETCLUBINFO_OK);
            return true;

        }catch (Exception e){
            ToastUtil.showToastInScreenCenter(BuyClubLevelActivity.this,"从后台取俱乐部用户数据报错，请稍后重试!");
            return false;
        }
    }

    private boolean getUserInfo()
    {
        try{

            // 根据用户ID取数据
            JSONObject jsonParam= new JSONObject();
            jsonParam.put("userid",application.getUserId());
            String strURL=remoteURL+"func=getuserinfo&param="+jsonParam.toString();
            URL url = new URL(strURL);
            Request request = new Request.Builder().url(strURL).build();
            Response response = DzApplication.getHttpClient().newCall(request).execute();
            String result=response.body().string();
            JSONObject jsonResult=new JSONObject((new JSONArray(result)).get(0).toString());

                    /*[{"id":2,"nickname":"无名","loginname":"13801865121","password":"666666","mobilephone":"13801865121","email":null,"sex":"男",
                    "province":null,"city":null,"personaltip":null,"headpic":null,"goldcoin":1000,"diamond":100,"type":0,
                    "levelid":1,"levelenddate":"2017-08-10T00:00:00","lastlogintime":"2017-08-30T11:25:59","lastlogingpsx":null,
                    "lastlogingpsy":null,"lastloginip":null,"registertime":"2017-08-02T15:50:23","id1":1,"level":1,"levelname":"黄金会员",
                    "levelabstract":"黄金","durationdays":100,"costdiamonds":100,"maxclubs":5}]*/
            application.getUser().diamond=jsonResult.getInt("diamond");

//            mCreator=new User();
//            mCreator.userId=jsonResult.getInt("id");
//            mCreator.nickName=jsonResult.getString("nickname");
//            mCreator.personalTip=jsonResult.getString("personaltip");
//            mCreator.province=jsonResult.getString("province");
//            mCreator.city=jsonResult.getString("city");
//            if (jsonResult.getString("headpic")!=null && !jsonResult.getString("headpic").equals("null")) {
//                mCreator.userHeadPic = jsonResult.getString("headpic");
//
//            }

            handler.sendEmptyMessage(MESSAGE_GET_USERINFO_OK);
            return true;


        }catch (Exception e){
            ToastUtil.showToastInScreenCenter(BuyClubLevelActivity.this,"从后台取俱乐部用户数据报错，请稍后重试!");
            return false;
        }

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_buy_club_level;
    }

    @Override
    protected void initViews() {
        super.initViews();
        rv_club_level_list=(RecyclerView)findViewById(R.id.rv_club_level_list);
        iv_club_level_head=(ImageView) findViewById(R.id.iv_club_level_head);
        tv_current_user_diamond=(TextView)findViewById(R.id.tv_current_user_diamond);
        tv_current_club_level=(TextView)findViewById(R.id.tv_current_club_level);
        tv_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_back:
                finish();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //刷新界面，更改用户的现有钻石数
        if (requestCode==REQUEST_BUY_DIAMONDS){
            Thread thread=new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    boolean bRet;
                    bRet=getUserInfo();
                    if(!bRet) return;
                }
            });
            thread.start();
        }
    }
}
