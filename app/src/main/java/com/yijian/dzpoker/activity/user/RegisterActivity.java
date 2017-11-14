package com.yijian.dzpoker.activity.user;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.yijian.dzpoker.R;
import com.yijian.dzpoker.util.DzApplication;
import com.yijian.dzpoker.util.ToastUtil;
import com.yijian.dzpoker.util.Util;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import okhttp3.Request;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private String strPhoneNumber;
    private EditText edpassword;
    private EditText edNickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Intent intent = getIntent();
        strPhoneNumber = intent.getStringExtra("phonenumber");
        TextView tvPhonenumber=(TextView)findViewById(R.id.tv_phonenumber);
        tvPhonenumber.setText("手机号："+strPhoneNumber);
        ImageView ivExit=(ImageView)findViewById(R.id.exit);
        ivExit.setOnClickListener(this);
        Button btnRegister=(Button)findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(this);

        edpassword=(EditText)findViewById(R.id.et_password);
        edNickname=(EditText)findViewById(R.id.et_nickname);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnRegister:



                if (edpassword.getText().toString().equals(""))
                {
                    ToastUtil.showToastInScreenCenter(RegisterActivity.this,"密码不为空！");
                    return;
                }
                if(edpassword.length()<6||edpassword.length()>12){
                    ToastUtil.showToastInScreenCenter(RegisterActivity.this,"密码长度为6-12位！");
                    return;
                }
                if (edNickname.getText().toString().equals(""))
                {
                    ToastUtil.showToastInScreenCenter(RegisterActivity.this,"昵称不为空！");
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
                            jsonObj.put("nickname",edNickname.getText().toString());
                            jsonObj.put("psw",edpassword.getText().toString());
                            jsonObj.put("type",0);


                            String strURL=getString(R.string.url_remote);
                            strURL+="func=register&param="+jsonObj.toString();

                            //此处提交后台会乱码，改用httpclient来做
                            URL url = new URL(strURL);
                            Request request = new Request.Builder().url(strURL).build();
                            Response response = DzApplication.getHttpClient().newCall(request).execute();
                            String result=response.body().string();
                            JSONObject jsonObject=new JSONObject(result);
                            if (jsonObject.getInt("ret")==0){
                                JMessageClient.register(jsonObject.getString("msg"), getString(R.string.unite_password), new BasicCallback() {
                                    @Override
                                    public void gotResult(int i, String s) {
                                        if (i==0){
                                            UserInfo userInfo=JMessageClient.getMyInfo();
                                            userInfo.setNickname(edNickname.getText().toString());
                                            JMessageClient.updateMyInfo(UserInfo.Field.nickname, JMessageClient.getMyInfo(), new BasicCallback() {
                                                @Override
                                                public void gotResult(int i, String s) {
                                                    if (i==0) {
                                                        ToastUtil.showToastInScreenCenter(RegisterActivity.this,"注册成功！");
                                                        finish();
                                                    }
                                                }
                                            });

                                        }else{
                                            ToastUtil.showToastInScreenCenter(RegisterActivity.this,"注册到IM失败，错误内容："+s);
                                        }
                                    }
                                });

                            }else {
                                ToastUtil.showToastInScreenCenter(RegisterActivity.this,jsonObject.getString("msg"));
                            }


                        }catch (Exception e){
                            ToastUtil.showToastInScreenCenter(RegisterActivity.this,"注册异常，请稍后重试！");

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
