package com.yijian.dzpoker.activity.club;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Toast;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.yijian.dzpoker.R;
import com.yijian.dzpoker.activity.LoginActivity;
import com.yijian.dzpoker.activity.MainActivity;
import com.yijian.dzpoker.activity.WellcomeActivity;
import com.yijian.dzpoker.activity.base.BaseBackActivity;
import com.yijian.dzpoker.ui.ItemRemoveRecyclerView;
import com.yijian.dzpoker.util.DzApplication;
import com.yijian.dzpoker.util.ToastUtil;
import com.yijian.dzpoker.view.CircleTransform;
import com.yijian.dzpoker.view.OnItemClickListener;
import com.yijian.dzpoker.view.adapter.RemoveItemdapter;
import com.yijian.dzpoker.view.data.User;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;

import okhttp3.Request;
import okhttp3.Response;

public class ClubMemberManageActivity extends BaseBackActivity {
    private ItemRemoveRecyclerView rv_club_user_manage;
    private ArrayList<User> mUserList=new ArrayList<User>();
    private RemoveItemdapter mAdapter;
    private int mManagerUserId,mClubId;
    private final int MESAGE_DELETE_USER_OK=0x1001;


    private Handler handler = new Handler() {

        // 该方法运行在主线程中
        // 接收到handler发送的消息，对UI进行操作
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what){
                case MESAGE_DELETE_USER_OK:
                    int position=msg.arg1;
                    mAdapter.removeItem(position);
                    break;



            }


        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserList=(ArrayList) getIntent().getSerializableExtra("userlist");
        mClubId=getIntent().getIntExtra("clubid",0);
        mManagerUserId=getIntent().getIntExtra("manageruserid",0);
        mAdapter = new RemoveItemdapter(this, mUserList);
        rv_club_user_manage.setLayoutManager(new LinearLayoutManager(this));
        rv_club_user_manage.setHasFixedSize(true);
        rv_club_user_manage.addItemDecoration(new DividerItemDecoration(
                this, DividerItemDecoration.VERTICAL));
        rv_club_user_manage.setAdapter(mAdapter);
        rv_club_user_manage.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //Toast.makeText(ClubMemberManageActivity.this, "** " + mUserList.get(position).nickName + " **", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDeleteClick(final int position) {
                //这里先请求删除数据
                Thread thread=new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        try{

                            JSONObject jsonParam= new JSONObject();
                    /*Int manageruserid : 管理员用户id
                        Int clubid : 俱乐部id
                        Int userid ： 被移除用户id
                    */
                            jsonParam.put("manageruserid",mManagerUserId);
                            jsonParam.put("clubid",mClubId);
                            jsonParam.put("userid", mUserList.get(position).userId);
                            String strURL=getString(R.string.url_remote)+"func=removefromclub&param="+jsonParam.toString();
                            URL url = new URL(strURL);
                            Request request = new Request.Builder().url(strURL).build();
                            Response response = DzApplication.getHttpClient().newCall(request).execute();
                            String result=response.body().string();
                            JSONObject jsonResult=new JSONObject(result);
                            if (jsonResult.getInt("ret")==0){
                                Message message = Message.obtain();
                                message.arg1 = position;
                                message.what = MESAGE_DELETE_USER_OK;
                                handler.sendMessage(message);

                            }else{
                                ToastUtil.showToastInScreenCenter(ClubMemberManageActivity.this,"删除用户出错，错误信息为："+jsonResult.getInt("msg"));
                            }
                        }catch (Exception e){
                            ToastUtil.showToastInScreenCenter(ClubMemberManageActivity.this,"删除用户出错，请稍后重试!");
                        }
                    }
                });
                thread.start();


            }
        });

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_club_member_manage;
    }

    @Override
    protected void initViews() {
        super.initViews();
        rv_club_user_manage=(ItemRemoveRecyclerView)findViewById(R.id.rv_club_user_manange);
        tv_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_back:

                finish();
                break;

        }

    }
}
