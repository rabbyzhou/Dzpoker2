package com.yijian.dzpoker.activity.user;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.yijian.dzpoker.R;
import com.yijian.dzpoker.activity.LoginActivity;
import com.yijian.dzpoker.activity.base.BaseBackActivity;
import com.yijian.dzpoker.ui.SwitchButton;

import static com.yijian.dzpoker.constant.Constant.INTENT_KEY_BACKTEXT;

public class SysConfigActivity extends BaseBackActivity {
    private SwitchButton sb_audio,sb_msg_sound,sb_msg_shake,sb_hide_club_info;
    private TextView tv_changePWD,tv_about,tv_logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setToolbarTitle("系统设置");

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_sys_config;
    }

    @Override
    protected void initViews() {
        super.initViews();
        sb_audio=(SwitchButton)findViewById(R.id.sb_audio);
        sb_msg_sound=(SwitchButton)findViewById(R.id.sb_msg_sound);
        sb_msg_shake=(SwitchButton)findViewById(R.id.sb_msg_shake);
        sb_hide_club_info=(SwitchButton)findViewById(R.id.sb_hide_club_info);
        tv_about=(TextView)findViewById(R.id.tv_about);
        tv_changePWD=(TextView)findViewById(R.id.tv_changePWD);
        tv_logout=(TextView)findViewById(R.id.tv_logout);
        tv_about.setOnClickListener(this);
        tv_changePWD.setOnClickListener(this);
        tv_logout.setOnClickListener(this);
//        tv_back.setOnClickListener(this);
        //TODO:qipu
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ){
            setResult(RESULT_OK);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.tv_back:
                setResult(RESULT_OK);
                finish();
                break;
            case R.id.tv_about:

                Intent intent = new Intent();
                intent.setClass(SysConfigActivity.this, AboutActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                intent.putExtra(INTENT_KEY_BACKTEXT, tv_title.getText());
                //TODO:qipu
                startActivity(intent);
                break;
            case R.id.tv_logout:
                //退出登录
                // 退回到主界面
                SharedPreferences settings = getSharedPreferences("depoker", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("username", "");
                editor.putInt("userid", 0);
                editor.commit();
                intent = new Intent(SysConfigActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;

            case R.id.tv_changePWD:
                //修改密码
                Intent intent1 = new Intent();
                intent1.putExtra("opType",3 );
                intent1.putExtra("phonenumber", application.getLoginName());
                intent1.setClass(SysConfigActivity.this, VerifyPhoneNumberActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                intent1.putExtra(INTENT_KEY_BACKTEXT, tv_title.getText());
                //TODO:qipu
                startActivity(intent1);
                break;
        }

    }
}
