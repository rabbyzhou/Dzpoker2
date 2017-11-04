package com.yijian.dzpoker.activity.club;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.yijian.dzpoker.R;
import com.yijian.dzpoker.activity.LoginActivity;
import com.yijian.dzpoker.activity.base.BaseBackActivity;
import com.yijian.dzpoker.activity.user.VerifyPhoneNumberActivity;
import com.yijian.dzpoker.ui.SwitchButton;
import com.yijian.dzpoker.util.DzApplication;
import com.yijian.dzpoker.util.ToastUtil;
import com.yijian.dzpoker.view.CircleTransform;
import com.yijian.dzpoker.view.adapter.ClubUserHeadAdapter;
import com.yijian.dzpoker.view.data.City;
import com.yijian.dzpoker.view.data.ClubInfo;
import com.yijian.dzpoker.view.data.Province;
import com.yijian.dzpoker.view.data.User;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;
import okhttp3.Response;

public class ClubInfoActivity extends BaseBackActivity {
    private TextView tv_disband_club,tv_club_name,tv_club_location,tv_club_creator_name,tv_club_leavel,tv_club_member,tv_create_time,tv_club_descript,tv_leave_club,tv_clear_chatmessage,tv_club_member_manage;
    private RecyclerView rv_club_user_list;
    private ImageView iv_club_head,iv_club_creator_head;
    private SwitchButton sb_message_notice;
    private ClubInfo mClubInfo;
    private LinearLayout layout_leave_club;

    private LinearLayoutManager mLayoutManager ;
    private ClubUserHeadAdapter mAdapter;
    private ArrayList<User> mUserList=new ArrayList<User>();
    private User mCreator=new User();
    private int mClubId;


    private  final static int MESAGE__OK=0x1001;//获取俱乐部信息
    private  final static int MESSAGE_GETCLUBINFO_OK=0x1002;//获取俱乐部信息
    private  final static int MESSAGE_GETCLUBCREATOR_OK=0x1003;//获取俱乐部创建者
    private  final static int MESSAGE_GETCLUBUSER_OK=0x1004;//获取俱乐部用户
    //private  final static int MESSAGE_GETCLUBUSER_OK=0x1005;



    private Handler handler = new Handler() {

        // 该方法运行在主线程中
        // 接收到handler发送的消息，对UI进行操作
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what){
                case MESSAGE_GETCLUBINFO_OK:
                    if (mClubInfo.createuserid!=userId){
                        tv_disband_club.setVisibility(View.GONE);
                        tv_club_member_manage.setVisibility(View.GONE);
                        tv_club_member_manage.setOnClickListener(ClubInfoActivity.this);
                    }else{
                        layout_leave_club.setVisibility(View.GONE);
                        iv_club_head.setOnClickListener(ClubInfoActivity.this);
                    }
                    tv_club_name.setText(mClubInfo.clubName);
                    tv_club_location.setText(mClubInfo.location);
                    tv_club_leavel.setText(mClubInfo.clubLevelName);
                    if (!mClubInfo.clubabstract.equals("null")){
                        tv_club_descript.setText(mClubInfo.clubabstract);
                    }
                    tv_club_member.setText("会员  "+mClubInfo.clubMemberNumber+"/"+mClubInfo.maxCLubMemberNumber);
                    tv_create_time.setText("创建于"+mClubInfo.clubCreatetime);
                    sb_message_notice.setOn(mClubInfo.bGetClubNotice);

                    if (mClubInfo.clubHeadPic!=null && !mClubInfo.clubHeadPic.equals("") ){
                    Picasso.with(getApplicationContext())
                            .load(mClubInfo.clubHeadPic)
                            .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                            .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                            .resize(100, 100)
                            .error(R.drawable.default_club_head)
                            .transform(new CircleTransform())
                            .into(iv_club_head);
                    }
                    break;
                case MESSAGE_GETCLUBCREATOR_OK:
                    tv_club_creator_name.setText(mCreator.nickName);
                    if (mCreator.userHeadPic!=null && !mCreator.userHeadPic.equals("")) {
                        Picasso.with(getApplicationContext())
                                .load(mCreator.userHeadPic)
                                .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                                .resize(100, 100)
                                .error(R.drawable.default_male_head)
                                .transform(new CircleTransform())
                                .into(iv_club_creator_head);
                    }
                    break;
                case MESSAGE_GETCLUBUSER_OK:
                    mAdapter.setData(mUserList);
                    break;

            }


        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.activity_club_info;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent=getIntent();
        mClubId=intent.getIntExtra("clubid",0);
        //clubInfo=(ClubInfo)intent.getSerializableExtra("clubinfo") ;
        initData();


    }

    private void initData(){
        initViewData();

   }

    private void initViewData()
    {
        //调用线程，到后台去请求俱乐部成员信息，以完成相关数据的初始化
        Thread thread=new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                boolean bRet;
                bRet=getClubInfo();
                if(!bRet) return;
                bRet=getClubCreator();
                if(!bRet) return;
                bRet=getClubUser();
                if(!bRet) return;


//                try{
//
//                    //先根据clubid获得俱乐部的相关信息
//                    /*[{"id":8,"createuserid":2,"createtime":"2017-08-25T14:44:19",
//                    "province":"宁夏","city":"石嘴山市",
//                    "clublevelid":1,"clublevelenddate":"2050-08-10T00:00:00",
//                    "clubstate":0,"clubabstract":null,"headpic":"http://120.24.243.2:85/uploadfile/1503643456_file.jpg",
//                    "name":"俱乐部","id1":1,"level":1,"levelabstract":"初级俱乐部","maxmembers":20,"maxmanagers":5,
//                    "durationdays":36500,"costdiamonds":0,"clubcuruser":1,"clubid":8,"msgnotify":1,"clubid1":8}]*/
//                    JSONObject jsonParam= new JSONObject();
//                    jsonParam.put("userid",userId);
//                    jsonParam.put("clubid",mClubId);
//
//                    String strURL=remoteURL+"func=getmyclubinfo&param="+jsonParam.toString();
//                    URL url = new URL(strURL);
//                    Request request = new Request.Builder().url(strURL).build();
//                    Response response = DzApplication.getHttpClient().newCall(request).execute();
//                    String result=response.body().string();
//                    JSONObject jsonResult=new JSONObject((new JSONArray(result)).get(0).toString());
//
//                    mClubInfo=new ClubInfo();
//                    mClubInfo.clubID=jsonResult.getInt("clubid");
//                    mClubInfo.clubName=jsonResult.getString("name");
//                    if (jsonResult.getString("headpic")!=null&& !jsonResult.getString("headpic").equals("null")){
//                    mClubInfo.clubHeadPic=jsonResult.getString("headpic");}
//                    mClubInfo.maxCLubMemberNumber=jsonResult.getInt("maxmembers");
//                    mClubInfo.clubMemberNumber=jsonResult.getInt("clubcuruser");
//                    mClubInfo.location=jsonResult.getString("province")+jsonResult.getString("city");
//                    mClubInfo.clubLevelName=jsonResult.getString("levelabstract");
//                    mClubInfo.createuserid=jsonResult.getInt("createuserid");
//                    mClubInfo.clubabstract=jsonResult.getString("clubabstract");
//                    mClubInfo.clubCreatetime=jsonResult.getString("createtime");
//                    mClubInfo.levelpic=jsonResult.getString("levelpic");
//                    int iGetClubNotice=jsonResult.getInt("msgnotify");
//                    if (iGetClubNotice==1){
//                        mClubInfo.bGetClubNotice=true;
//                    }else{
//                        mClubInfo.bGetClubNotice=false;
//                    }
//
//                    handler.sendEmptyMessage(MESAGE_GETCLUBINFO_OK);
//
//
//                    // 根据用户ID取数据
//                    jsonParam= new JSONObject();
//                    jsonParam.put("userid",mClubInfo.createuserid);
//                    strURL=remoteURL+"func=getuserinfo&param="+jsonParam.toString();
//                    url = new URL(strURL);
//                    request = new Request.Builder().url(strURL).build();
//                    response = DzApplication.getHttpClient().newCall(request).execute();
//                    result=response.body().string();
//                    jsonResult=new JSONObject((new JSONArray(result)).get(0).toString());
//
//                    /*[{"id":2,"nickname":"无名","loginname":"13801865121","password":"666666","mobilephone":"13801865121","email":null,"sex":"男",
//                    "province":null,"city":null,"personaltip":null,"headpic":null,"goldcoin":1000,"diamond":100,"type":0,
//                    "levelid":1,"levelenddate":"2017-08-10T00:00:00","lastlogintime":"2017-08-30T11:25:59","lastlogingpsx":null,
//                    "lastlogingpsy":null,"lastloginip":null,"registertime":"2017-08-02T15:50:23","id1":1,"level":1,"levelname":"黄金会员",
//                    "levelabstract":"黄金","durationdays":100,"costdiamonds":100,"maxclubs":5}]*/
//
//                    mCreator=new User();
//                    mCreator.userId=jsonResult.getInt("id");
//                    mCreator.nickName=jsonResult.getString("nickname");
//                    mCreator.personalTip=jsonResult.getString("personaltip");
//                    mCreator.province=jsonResult.getString("province");
//                    mCreator.city=jsonResult.getString("city");
//                    if (jsonResult.getString("headpic")!=null && !jsonResult.getString("headpic").equals("null")) {
//                        mCreator.userHeadPic = jsonResult.getString("headpic");
//                    }
//
//                    handler.sendEmptyMessage(MESAGE_GETCLUBCREATOR_OK);
//
//                    //拼装url字符串
//                    JSONObject jsonObj = new JSONObject();
//                    jsonObj.put("clubid",mClubId);
//
//                    strURL=remoteURL+"func=getclubuser&param="+jsonObj.toString();
//                    url = new URL(strURL);
//
//                    request = new Request.Builder().url(strURL).build();
//                    response = DzApplication.getHttpClient().newCall(request).execute();
//                    result=response.body().string();
//
//                    JSONArray jsonUserList = new JSONArray(result);
//
//                    for (int i = 0; i < jsonUserList.length(); i++) {
//                        JSONObject json=new JSONObject(jsonUserList.getString(i));
//                        User user = new User();
//                        user.userId=json.getInt("id");
//                        user.nickName=json.getString("nickname");
//                        user.personalTip=json.getString("personaltip");
//                        user.province=json.getString("province");
//                        user.city=json.getString("city");
//
//                        if (json.getString("headpic")!=null&& !json.getString("headpic").equals("null")){
//                            user.userHeadPic=json.getString("headpic");}
////                        user.province=json.getString("province");
////                        user.city=json.getString("city");
////                        if (json.getString("personaltip")!=null){
////                            user.personalTip=json.getString("personaltip");}
//
//                        mUserList.add(user);
//                    }
//                    //获取数据后，通知主线程，更新界面
//                    handler.sendEmptyMessage(MESAGE_GETCLUBUSER_OK);
//                }catch (Exception e){
//                    ToastUtil.showToastInScreenCenter(ClubInfoActivity.this,"从后台取俱乐部用户数据报错，请稍后重试!");
//                }
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
            jsonParam.put("clubid",mClubId);

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
            mClubInfo.levelpic=jsonResult.getString("levelpic");
            mClubInfo.province=jsonResult.getString("province");
            mClubInfo.city=jsonResult.getString("city");
            int iGetClubNotice=jsonResult.getInt("msgnotify");
            if (iGetClubNotice==1){
                mClubInfo.bGetClubNotice=true;
            }else{
                mClubInfo.bGetClubNotice=false;
            }

            handler.sendEmptyMessage(MESSAGE_GETCLUBINFO_OK);
            return true;

        }catch (Exception e){
            ToastUtil.showToastInScreenCenter(ClubInfoActivity.this,"从后台取俱乐部用户数据报错，请稍后重试!");
            return false;
        }
    }

    private boolean getClubCreator()
    {
        try{

            // 根据用户ID取数据
            JSONObject jsonParam= new JSONObject();
            jsonParam.put("userid",mClubInfo.createuserid);
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

            mCreator=new User();
            mCreator.userId=jsonResult.getInt("id");
            mCreator.nickName=jsonResult.getString("nickname");
            mCreator.personalTip=jsonResult.getString("personaltip");
            mCreator.province=jsonResult.getString("province");
            mCreator.city=jsonResult.getString("city");
            if (jsonResult.getString("headpic")!=null && !jsonResult.getString("headpic").equals("null")) {
                mCreator.userHeadPic = jsonResult.getString("headpic");

            }

            handler.sendEmptyMessage(MESSAGE_GETCLUBCREATOR_OK);
            return true;


        }catch (Exception e){
            ToastUtil.showToastInScreenCenter(ClubInfoActivity.this,"从后台取俱乐部用户数据报错，请稍后重试!");
            return false;
        }

    }

    private boolean getClubUser()
    {
        try{


            //拼装url字符串
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("clubid",mClubId);

            String strURL=remoteURL+"func=getclubuser&param="+jsonParam.toString();
            URL url = new URL(strURL);

            Request request = new Request.Builder().url(strURL).build();
            Response response = DzApplication.getHttpClient().newCall(request).execute();
            String result=response.body().string();

            JSONArray jsonUserList = new JSONArray(result);
            mUserList=new ArrayList<User>();

            for (int i = 0; i < jsonUserList.length(); i++) {
                JSONObject json=new JSONObject(jsonUserList.getString(i));
                User user = new User();
                user.userId=json.getInt("id");
                user.nickName=json.getString("nickname");
                user.personalTip=json.getString("personaltip");
                user.province=json.getString("province");
                user.city=json.getString("city");

                if (json.getString("headpic")!=null&& !json.getString("headpic").equals("null")){
                    user.userHeadPic=json.getString("headpic");}
//                        user.province=json.getString("province");
//                        user.city=json.getString("city");
//                        if (json.getString("personaltip")!=null){
//                            user.personalTip=json.getString("personaltip");}

                mUserList.add(user);
            }
            //获取数据后，通知主线程，更新界面
            handler.sendEmptyMessage(MESSAGE_GETCLUBUSER_OK);
            return true;
        }catch (Exception e){
            ToastUtil.showToastInScreenCenter(ClubInfoActivity.this,"从后台取俱乐部用户数据报错，请稍后重试!");
            return false;
        }

    }


    @Override
    protected void initViews() {
        super.initViews();
        tv_disband_club=(TextView)findViewById(R.id.tv_disband_club) ;
        iv_club_head=(ImageView) findViewById(R.id.iv_club_head) ;
        tv_club_name=(TextView)findViewById(R.id.tv_club_name) ;
        tv_club_location=(TextView)findViewById(R.id.tv_club_location) ;
        iv_club_creator_head=(ImageView) findViewById(R.id.iv_club_creator_head) ;
        tv_club_creator_name=(TextView)findViewById(R.id.tv_club_creator_name) ;
        tv_club_leavel=(TextView)findViewById(R.id.tv_club_leavel) ;
        tv_club_member=(TextView)findViewById(R.id.tv_club_member) ;
        rv_club_user_list=(RecyclerView) findViewById(R.id.rv_club_user_list) ;
        tv_create_time=(TextView)findViewById(R.id.tv_create_time) ;
        tv_club_descript=(TextView)findViewById(R.id.tv_club_descript) ;
        sb_message_notice=(SwitchButton) findViewById(R.id.sb_message_notice) ;
        tv_clear_chatmessage=(TextView)findViewById(R.id.tv_clear_chatmessage) ;
        tv_leave_club=(TextView)findViewById(R.id.tv_leave_club) ;
        layout_leave_club=(LinearLayout)findViewById(R.id.layout_leave_club);
        tv_club_member_manage=(TextView)findViewById(R.id.tv_club_member_manage) ;

        tv_disband_club.setOnClickListener(this);
        addSwitchButtonListeners();
        tv_clear_chatmessage.setOnClickListener(this);
        tv_leave_club.setOnClickListener(this);
        iv_club_creator_head.setOnClickListener(this);
        tv_club_member_manage.setOnClickListener(this);
        tv_club_leavel.setOnClickListener(this);



        //创建默认的线性LayoutManager
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rv_club_user_list.setLayoutManager(mLayoutManager);
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        rv_club_user_list.setHasFixedSize(true);

        //创建并设置Adapter
        mAdapter = new ClubUserHeadAdapter(this);
        rv_club_user_list.setAdapter(mAdapter);



    }

    private void addSwitchButtonListeners() {
        try {
            sb_message_notice.setOnSwitchListner(new SwitchButton.SwitchChangedListner() {
                @Override
                public void switchChanged(Integer viewId, boolean isOn) {
                    SetSwitchButton(isOn);
//                    if(isOn) {
//
//                        //打开接受消标志，保存到配置文件，以及application
//
//                       //clubInfo.bInClub=true;
//
//                    } else {
//                        //打开接受消标志，保存到配置文件，以及application
//                        //clubInfo.bInClub=false;
//                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void SetSwitchButton(final boolean flag){
        Thread thread=new Thread(new Runnable()
        {
            @Override
            public void run()
            {
            try{
                //更新服务器上保存的状态值

                JSONObject jsonParam= new JSONObject();
                jsonParam.put("userid",userId);
                jsonParam.put("clubid",mClubId);
                jsonParam.put("isnotify",flag);

                String strURL=remoteURL+"func=modifymsgnotify&param="+jsonParam.toString();
                URL url = new URL(strURL);
                Request request = new Request.Builder().url(strURL).build();
                Response response = DzApplication.getHttpClient().newCall(request).execute();
                String result=response.body().string();
                JSONObject jsonResult=new JSONObject(result);
                int ret=jsonResult.getInt("ret");
                if (ret!=0){
                    sb_message_notice.setOn(!flag);
                    ToastUtil.showToastInScreenCenter(ClubInfoActivity.this,"更新服务器数据出错，请稍后重试！");
                }
            }catch (Exception e){
                ToastUtil.showToastInScreenCenter(ClubInfoActivity.this,"更新服务器数据出错，请稍后重试!");
            }
            }
        });
        thread.start();
    }



    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.tv_club_member_manage://会员管理

                Intent intentClubMemberManage = new Intent();
                intentClubMemberManage.putExtra("userlist",mUserList);
                intentClubMemberManage.putExtra("manageruserid",userId);
                intentClubMemberManage.putExtra("clubid",mClubId);

                intentClubMemberManage.setClass(ClubInfoActivity.this, ClubMemberManageActivity.class);
                //此处要等待会员管理结果，所以返回的时候要重刷界面，更改会员列表
                startActivityForResult(intentClubMemberManage,1);
                break;
            case R.id.tv_disband_club://解散俱乐部
                //这里需要确认，是否解散俱乐部

                Dialog dialog = new AlertDialog.Builder(this)
                                    .setIcon(android.R.drawable.btn_star)
                                    .setTitle("确认").setMessage("确定解散俱乐部?")
                                    .setPositiveButton("确定",new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                         // TODO Auto-generated method stub
                                            Thread thread=new Thread(new Runnable()
                                            {
                                                @Override
                                                public void run()
                                                {
                                                   CloseClub();
                                                }
                                            });
                                            thread.start();
                                        }
                                    }).
                                    setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                          // TODO Auto-generated method stub

                                        }
                                    })
                                    .create();
                                    dialog.show();

                break;
            case R.id.tv_leave_club://离开俱乐部
                //这里需要确认，是否离开俱乐部
                Dialog dialog1 = new AlertDialog.Builder(this)
                        .setIcon(android.R.drawable.btn_star)
                        .setTitle("确认").setMessage("离开俱乐部后将不再参与俱乐部的活动以及接收俱乐部信息，确认离开?")
                        .setPositiveButton("确定",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                Thread thread=new Thread(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        LeaveClub();
                                    }
                                });
                                thread.start();
                            }
                        }).
                                setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // TODO Auto-generated method stub

                                    }
                                })
                        .create();
                dialog1.show();
                break;
            case R.id.tv_club_leavel://俱乐部等级
                Intent intentClubLevel = new Intent();
                intentClubLevel.setClass(ClubInfoActivity.this, BuyClubLevelActivity.class);
                //此处要等待会员管理结果，所以返回的时候要重刷界面，更改会员列表
                startActivityForResult(intentClubLevel,3);
                break;
            case R.id.iv_club_head://俱乐部信息管理
                Intent intentCLubInfoManage=new Intent();
                intentCLubInfoManage.putExtra("clubinfo",mClubInfo);
                intentCLubInfoManage.setClass(ClubInfoActivity.this, ClubInfoManageActivity.class);
                //此处要等待会员管理结果，所以返回的时候要重刷界面，更改会员列表
                startActivityForResult(intentCLubInfoManage,2);

                break;
            case R.id.iv_club_creator_head:
                //点击创建者头像，进入查看创建者信息界面
                //跳转到获取验证码界面，带入参数：业务类型（注册，找回密码，修改密码），手机号
                Intent intent = new Intent();
                intent.putExtra("user", mCreator);
                intent.setClass(ClubInfoActivity.this, ClubUserInfoActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_back:
                setResult(1);
                finish();
                break;
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
         //本界面，更改俱乐部等级，修改俱乐部信息，会员管理三个界面都必须返回，有返回值的话，到服务器去取俱乐部信息以及会员信息，更新界面
        if (requestCode==1 ){
            //此处是从会员管理界面回来的，仅仅要刷新会员头像数据
            Thread thread=new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    boolean bRet;
                    bRet=getClubInfo();
                    if(!bRet) return;
                    bRet=getClubUser();
                    if(!bRet) return;
                }
            });
            thread.start();

        }
        if (requestCode==2){
            //此处是从俱乐部信息来的 ,此处，直接返回reslutcode=0，保存返回，resultcode=1
            Thread thread=new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    boolean bRet;
                    bRet=getClubInfo();
                }
            });
            thread.start();


        }
        if (requestCode==3 ){
            //此处是从俱乐部等级信息来的 ,此处，直接返回reslutcode=0，保存返回，resultcode=1
            Thread thread=new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    boolean bRet;
                    bRet=getClubInfo();
                }
            });
            thread.start();

        }
    }

    private void CloseClub(){
        try{

            //拼装url字符串
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("clubid",mClubId);
            jsonParam.put("userid",application.getUserId());


            String strURL=remoteURL+"func=closeclub&param="+jsonParam.toString();
            URL url = new URL(strURL);

            Request request = new Request.Builder().url(strURL).build();
            Response response = DzApplication.getHttpClient().newCall(request).execute();
            String result=response.body().string();

            JSONObject jsonResult=new JSONObject(result);
            if (jsonResult.getInt("ret")==0){
                setResult(9);
                finish();

            }else{
                ToastUtil.showToastInScreenCenter(ClubInfoActivity.this,"解散俱乐部失败，错误内容为："+jsonResult.getString("msg"));
            }


        }catch (Exception e){
            ToastUtil.showToastInScreenCenter(ClubInfoActivity.this,"解散俱乐部失败，请稍后重试!");

        }

    }

    private void LeaveClub(){
        try{

            //拼装url字符串
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("clubid",mClubId);
            jsonParam.put("userid",application.getUserId());


            String strURL=remoteURL+"func=leaveclub&param="+jsonParam.toString();
            URL url = new URL(strURL);

            Request request = new Request.Builder().url(strURL).build();
            Response response = DzApplication.getHttpClient().newCall(request).execute();
            String result=response.body().string();

            JSONObject jsonResult=new JSONObject(result);
            if (jsonResult.getInt("ret")==0){
                setResult(9);
                finish();

            }else{
                ToastUtil.showToastInScreenCenter(ClubInfoActivity.this,"离开俱乐部失败，错误内容为："+jsonResult.getString("msg"));
            }


        }catch (Exception e){
            ToastUtil.showToastInScreenCenter(ClubInfoActivity.this,"离开俱乐部失败，请稍后重试!");

        }

    }
}
