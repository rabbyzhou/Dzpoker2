package com.yijian.dzpoker.activity.user;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yijian.dzpoker.R;
import com.yijian.dzpoker.activity.base.BaseToolbarActivity;
import com.yijian.dzpoker.constant.Constant;
import com.yijian.dzpoker.util.DzApplication;
import com.yijian.dzpoker.util.ToastUtil;

import org.json.JSONObject;

import java.net.URL;

import okhttp3.Request;
import okhttp3.Response;

public class ResetPWDActivity extends BaseToolbarActivity implements View.OnClickListener {
    private String strPhoneNumber;
    private EditText edpassword;
    private String mOldPassWord;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pwd);
        setToolbarTitle("设置密码");
        LinearLayout exitLayout = (LinearLayout)findViewById(R.id.exit) ;
        exitLayout.setOnClickListener(this);

        Intent intent = getIntent();
        String backText = intent.getStringExtra(Constant.INTENT_KEY_BACKTEXT);
        TextView textView = (TextView)findViewById(R.id.tv_exit);
        if ( backText != null && !backText.isEmpty()){
            textView.setText(backText);
        }

        strPhoneNumber = intent.getStringExtra("phonenumber");
        TextView tvPhonenumber=(TextView)findViewById(R.id.tv_phonenumber);
        tvPhonenumber.setText("手机号："+strPhoneNumber);
        edpassword=(EditText)findViewById(R.id.et_password) ;

        Button btnResetPWD=(Button)findViewById(R.id.btnResetPWD);
        btnResetPWD.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnResetPWD:

                if (edpassword.getText().toString().equals(""))
                {
                    ToastUtil.showToastInScreenCenter(ResetPWDActivity.this,"密码不为空！");
                    return;
                }
                if(edpassword.length()<6||edpassword.length()>12){
                    ToastUtil.showToastInScreenCenter(ResetPWDActivity.this,"密码长度为6-20位！");

                    return;
                }

                Thread thread=new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        try{
//                            String loginname :  注册用户名，默认11位手机号
//                            String nickname:   注册用户昵称
//                            String psw    :  密码
//                            Int type      ：注册类型 0手机 1微信 2QQ


                            //拼装url字符串
                            JSONObject jsonObj = new JSONObject();
                            jsonObj.put("loginname",strPhoneNumber);
                            jsonObj.put("psw",edpassword.getText().toString());

                            String strURL=getString(R.string.url_remote);
                            strURL+="func=resetpsw&param="+jsonObj.toString();

                            URL url = new URL(strURL);
                            Request request = new Request.Builder().url(strURL).build();
                            Response response = DzApplication.getHttpClient().newCall(request).execute();
                            String result=response.body().string();
                            JSONObject jsonObject=new JSONObject(result);
                            if (jsonObject.getInt("ret")==0){
                                ToastUtil.showToastInScreenCenter(ResetPWDActivity.this,"密码设置成功！");
                                finish();

                            }else {
                                ToastUtil.showToastInScreenCenter(ResetPWDActivity.this,jsonObject.getString("msg"));
                            }
                        }catch (Exception e){
                            ToastUtil.showToastInScreenCenter(ResetPWDActivity.this,"密码设置错误，请稍后重试！");

                        }

                    }
                });
                thread.start();
                break;

            case R.id.exit:
                finish();
        }
    }


}
