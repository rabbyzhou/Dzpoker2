package com.yijian.dzpoker.activity.club;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.yijian.dzpoker.R;
import com.yijian.dzpoker.activity.base.BaseBackActivity;
import com.yijian.dzpoker.util.DzApplication;
import com.yijian.dzpoker.view.data.ClubInfo;

public class ClubChatActivity extends BaseBackActivity {
    private TextView tv_club_manage;
    private int mClubId;
   // private  ClubInfo mClubInfo;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_club_chat;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent=getIntent();
        mClubId=intent.getIntExtra("clubid",0);
        //mClubInfo=(ClubInfo) intent.getSerializableExtra("clubinfo");

    }

    @Override
    protected void initViews() {
        super.initViews();
        tv_club_manage = (TextView) findViewById(R.id.tv_club_manage);
        tv_club_manage.setOnClickListener(this);
        tv_title.setText(application.getClubInfo().clubName+"("+application.getClubInfo().clubMemberNumber+")");

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_back:
                setResult(1);
                finish();
            case R.id.tv_club_manage:

                Intent intent = new Intent();
                intent.putExtra("clubid",mClubId);
                //intent.putExtra("clubinfo",mClubInfo);
                intent.setClass(ClubChatActivity.this, ClubInfoActivity.class);
                startActivityForResult(intent,1);
                //startActivity(intent);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //这里需要判断返回的类型
        /*1.退出俱乐部，2：离开俱乐部 3：从上一界面返回（有可能改名，改头像什么的，或者删除成员，这里需要更新界面的值）*/
        switch(resultCode){
            case 1:
                tv_title.setText(application.getClubInfo().clubName+"("+application.getClubInfo().clubMemberNumber+")");
                setResult(1);//告诉上一界面，俱乐部有变化

            case 9:
                setResult(1);//解散或者离开俱乐部，需要重新加载数据
                finish();

        }

    }
}
