package com.yijian.dzpoker.activity.club;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yijian.dzpoker.R;
import com.yijian.dzpoker.constant.Constant;
import com.yijian.dzpoker.util.DzApplication;
import com.yijian.dzpoker.util.ToastUtil;
import com.yijian.dzpoker.view.adapter.ClubInfoAdapter;
import com.yijian.dzpoker.view.data.ClubInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;
import okhttp3.Response;

import static com.yijian.dzpoker.constant.Constant.INTENT_KEY_BACKTEXT;

public class MyClubActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout layout_clubList;
    private RelativeLayout layout_top;
    private TextView tv_more;
    private PopupWindow popupWindow;
    private String strLoginName;
    private int userId;

    private RecyclerView lv_club_list;
    private LinearLayoutManager mLayoutManager ;
    private ClubInfoAdapter mAdapter;
    private List<ClubInfo> mClubInfoList=new ArrayList<ClubInfo>();
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

    private void  initViews(){

        Intent intent = getIntent();
        String backText = intent.getStringExtra(Constant.INTENT_KEY_BACKTEXT);
        TextView exitText = (TextView)findViewById(R.id.tv_exit);
        if ( backText != null && !backText.isEmpty()){
            exitText.setText(backText);
        }
        LinearLayout exitLayput = (LinearLayout)findViewById(R.id.exit);
        exitLayput.setOnClickListener(this);

        layout_top=(RelativeLayout)findViewById(R.id.layout_top);
        tv_more=(TextView)findViewById(R.id.tv_more);
        tv_more.setOnClickListener(this);
        lv_club_list=(RecyclerView)findViewById(R.id.lv_club_list);

        //创建默认的线性LayoutManager
        mLayoutManager = new LinearLayoutManager(this);
        lv_club_list.setLayoutManager(mLayoutManager);
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        lv_club_list.setHasFixedSize(true);
        lv_club_list.addItemDecoration(new DividerItemDecoration(
                this, DividerItemDecoration.VERTICAL));
        mAdapter = new ClubInfoAdapter(this, new ClubInfoAdapter.OnRecordSelectListener() {
            @Override
            public void onRecordSelected(final ClubInfo clubInfo) {
                //记录clubId,并传到下一activity,这里只传递clubId,相关数据调用接口重新取
                Intent intent = new Intent();
                intent.putExtra("clubid",clubInfo.clubID);
                intent.putExtra(DzApplication.GROUP_ID,clubInfo.imgroupid);
                intent.putExtra(DzApplication.TARGET_ID,"");


                //intent.putExtra("clubinfo",clubInfo);
                DzApplication application=(DzApplication)getApplication();
                application.setClubInfo(clubInfo);
                intent.setClass(MyClubActivity.this, ChatActivity.class);
                startActivityForResult(intent,9);


            }
        });

        /* holder.mView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


            }
        });*/
        lv_club_list.setAdapter(mAdapter);
        //创建并设置Adapter

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_club);

        SharedPreferences settings = getSharedPreferences("depoker", 0);
        strLoginName=settings.getString("username","");
        userId=settings.getInt("userid",0);
        //从服务器获得俱乐部列表，利用handlemessage来更新主页的显示
        initViews();
        initList();

    }


   private void initList(){

       Thread thread=new Thread(new Runnable()
       {
           @Override
           public void run()
           {
               try{
                   //拼装url字符串
                   JSONObject jsonObj = new JSONObject();
                   jsonObj.put("userid",userId);

                   String strURL=getString(R.string.url_remote);
                   strURL+="func=getmyclub&param="+jsonObj.toString();

                   Request request = new Request.Builder().url(strURL).build();
                   Response response = DzApplication.getHttpClient().newCall(request).execute();
                   String result=response.body().string();
                   mClubInfoList=new ArrayList<ClubInfo>();
                   JSONArray jsonClubList=new JSONArray(result);
                   for(int i=0;i<jsonClubList.length();i++){
                       ClubInfo clubInfo=new ClubInfo();
                       JSONObject jsonObject=new JSONObject(jsonClubList.get(i).toString());
                       clubInfo.clubID=jsonObject.getInt("clubid");
                       clubInfo.clubName=jsonObject.getString("name");
                       clubInfo.clubHeadPic=jsonObject.getString("headpic");
                       clubInfo.maxCLubMemberNumber=jsonObject.getInt("maxmembers");
                       clubInfo.clubMemberNumber=jsonObject.getInt("clubcuruser");
                       clubInfo.location=jsonObject.getString("city");
                       clubInfo.clubLevelName=jsonObject.getString("levelabstract");
                       clubInfo.createuserid=jsonObject.getInt("createuserid");
                       clubInfo.clubabstract=jsonObject.getString("clubabstract");
                       clubInfo.clubCreatetime=jsonObject.getString("createtime");
                       clubInfo.province=jsonObject.getString("province");
                       clubInfo.city=jsonObject.getString("city");
                       if (!jsonObject.get("imgroupid").toString().equals("null")){
                           clubInfo.imgroupid = jsonObject.getLong("imgroupid");
                       }


                       int iGetClubNotice=jsonObject.getInt("msgnotify");
                       if (iGetClubNotice==1){
                           clubInfo.bGetClubNotice=true;
                       }else{
                           clubInfo.bGetClubNotice=false;
                       }
                       mClubInfoList.add(clubInfo);
                   }
                   handler.sendEmptyMessage(MESAGE_GETCLUBLIST_OK);
               }catch (Exception e){
                   ToastUtil.showToastInScreenCenter(MyClubActivity.this,"取俱乐部列表俱乐部失败，请稍后重试!");
               }

           }
       });
       thread.start();
   }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_more:
                showWindow(layout_top);
                break;
            case R.id.exit:
                finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ){
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void showWindow(View parent) {

        View contentView = getPopupWindowContentView();
        popupWindow = new PopupWindow(contentView,212,200
                , true);
        popupWindow.setFocusable(true);
        // 设置允许在外点击消失
        popupWindow.setOutsideTouchable(true);
        // 如果不设置PopupWindow的背景，有些版本就会出现一个问题：无论是点击外部区域还是Back键都无法dismiss弹框
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        // 设置好参数之后再show

        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int xOffset = parent.getWidth()  - contentView.getMeasuredWidth() ;
        popupWindow.showAsDropDown(parent,xOffset,20);    // 在mButton2的中间显示
    }

    private View getPopupWindowContentView() {
        // 一个自定义的布局，作为显示的内容
        int layoutId = R.layout.popupwindow_club;   // 布局ID
        View contentView = LayoutInflater.from(this).inflate(layoutId, null);
        View.OnClickListener menuItemOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.tv_createclub:
                        Intent intent = new Intent();
//                        intent.putExtra("opType",1 );
//                        intent.putExtra("phonenumber", edUserName.getText().toString());
                        intent.setClass(MyClubActivity.this, CreateClubActivity.class);
                        TextView titleView = (TextView)findViewById(R.id.title);
                        intent.putExtra(INTENT_KEY_BACKTEXT, titleView.getText());
                        startActivityForResult(intent,  1);
                        break;
                    case R.id.tv_addtoclub:
                        Intent intent1 = new Intent();
                        intent1.setClass(MyClubActivity.this, AddIntoClubActivity.class);
                        intent1.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        TextView titleView1 = (TextView)findViewById(R.id.title);
                        intent1.putExtra(INTENT_KEY_BACKTEXT, titleView1.getText());
                        startActivity(intent1);
                        break;
                }

                if (popupWindow != null) {
                    popupWindow.dismiss();
                }
            }
        };
        contentView.findViewById(R.id.tv_createclub).setOnClickListener(menuItemOnClickListener);
        contentView.findViewById(R.id.tv_addtoclub).setOnClickListener(menuItemOnClickListener);

        return contentView;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        initList();

    }
}
