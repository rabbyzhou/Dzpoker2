package com.yijian.dzpoker.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.yijian.dzpoker.R;
import com.yijian.dzpoker.activity.base.BaseToolbarActivity;
import com.yijian.dzpoker.activity.user.RegisterActivity;
import com.yijian.dzpoker.activity.user.VerifyPhoneNumberActivity;
import com.yijian.dzpoker.util.DzApplication;
import com.yijian.dzpoker.util.ToastUtil;
import com.yijian.dzpoker.util.Util;
import com.yijian.dzpoker.view.data.User;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends BaseToolbarActivity implements View.OnClickListener {
    private  EditText edUserName;
    private  EditText edUserPWD;

    private static final boolean DEBUG = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        TextView tvNewUser=(TextView)findViewById(R.id.tvNewUser);
        tvNewUser.setOnClickListener(this);
        TextView tvForgetPWD=(TextView)findViewById(R.id.tvForgetPWD);
        tvForgetPWD.setOnClickListener(this);
        Button btnLogin=(Button)findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);
        edUserName=(EditText)findViewById(R.id.edUserName);
        edUserPWD=(EditText)findViewById(R.id.edPWD);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogin:
                if (DEBUG) {
                    Intent intent3 = new Intent();
                    intent3.setClass(LoginActivity.this, MainFragmentActivity.class);
                    startActivity(intent3);
                    ToastUtil.showToastInScreenCenter(LoginActivity.this, "登录成功！");
                    finish();
                    return;
                }

                if (edUserName.getText().toString().equals(""))
                {
                    ToastUtil.showToastInScreenCenter(LoginActivity.this,"手机号码不为空!");

                    return;
                }
                if (edUserName.getText().toString().length()!=11)
                {
                    ToastUtil.showToastInScreenCenter(LoginActivity.this,"手机号码不对!");
                    return;
                }
                if (edUserPWD.getText().toString().equals(""))
                {
                    ToastUtil.showToastInScreenCenter(LoginActivity.this,"密码不为空!");

                    return;
                }
                if (edUserPWD.getText().toString().length()<6||edUserPWD.getText().toString().length()>20)
                {
                    ToastUtil.showToastInScreenCenter(LoginActivity.this,"密码长度为6-20个字符!");

                    return;
                }

                Thread thread=new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        try{
                            //拼装url字符串
                            JSONObject jsonObj = new JSONObject();
                            jsonObj.put("loginname",edUserName.getText().toString());
                            jsonObj.put("psw",edUserPWD.getText().toString());

                            String strURL=getString(R.string.url_remote);
                            strURL+="func=login&param="+jsonObj.toString();

                            URL url = new URL(strURL);
                            Request request = new Request.Builder().url(strURL).build();
                            Response response = DzApplication.getHttpClient().newCall(request).execute();
                            String result=response.body().string();
                            JSONObject jsonObject=new JSONObject(result);
                            if (jsonObject.getInt("ret")==0){
                                //登录成功取用户信息
                                int userId=Integer.parseInt(jsonObject.getString("userid"));
                                JSONObject jsonParam= new JSONObject();
                                jsonParam.put("userid",userId);
                                strURL=getString(R.string.url_remote)+"func=getuserinfo&param="+jsonParam.toString();
                                url = new URL(strURL);
                                request = new Request.Builder().url(strURL).build();
                                response = DzApplication.getHttpClient().newCall(request).execute();
                                result=response.body().string();
                                JSONObject jsonResult=new JSONObject((new JSONArray(result)).get(0).toString());

                                /*[{"id":2,"nickname":"无名","loginname":"13801865121","password":"666666","mobilephone":"13801865121","email":null,"sex":"男",
                                "province":null,"city":null,"personaltip":null,"headpic":null,"goldcoin":1000,"diamond":100,"type":0,
                                "levelid":1,"levelenddate":"2017-08-10T00:00:00","lastlogintime":"2017-08-30T11:25:59","lastlogingpsx":null,
                                "lastlogingpsy":null,"lastloginip":null,"registertime":"2017-08-02T15:50:23","id1":1,"level":1,"levelname":"黄金会员",
                                "levelabstract":"黄金","durationdays":100,"costdiamonds":100,"maxclubs":5}]*/

                                final User user=new User();
                                user.userId=jsonResult.getInt("id");
                                user.nickName=jsonResult.getString("nickname");
                                if (!jsonResult.getString("personaltip").equals("null")){
                                    user.personalTip=jsonResult.getString("personaltip");
                                }else {
                                    user.personalTip="暂无签名";
                                }
                                user.province=jsonResult.getString("province");
                                user.city=jsonResult.getString("city");
                                user.goldcoin=jsonResult.getInt("goldcoin");
                                user.diamond=jsonResult.getInt("diamond");
                                user.userLoginName=jsonResult.getString("loginname");
                                if (jsonResult.getString("headpic")!=null && !jsonResult.getString("headpic").equals("null")) {
                                    user.userHeadPic = jsonResult.getString("headpic");
                                }
                                user.levelid=jsonResult.getInt("level");
                                user.levelname=jsonResult.getString("levelname");
                                user.password=jsonResult.getString("password");

                                //取到用户信息之后，利用ID登录IM
                                JMessageClient.login(user.userId+"", getString(R.string.unite_password), new BasicCallback() {
                                    @Override
                                    public void gotResult(int i, String s) {
                                        if (i==0){
                                            ((DzApplication)getApplication()).setUser(user);
                                            //存储登录成功的用户名到本地文件
                                            SharedPreferences settings = getSharedPreferences("depoker", 0);
                                            SharedPreferences.Editor editor = settings.edit();
                                            editor.putString("username",user.userLoginName);
                                            editor.putInt("userid", user.userId);
                                            DzApplication myApp=(DzApplication)getApplication();
                                            myApp.setLoginName(user.userLoginName);
                                            myApp.setUserId(user.userId);
                                            // Commit the edits!
                                            editor.commit();
                                            //跳转到主界面


                                            Intent intent = new Intent();
                                            intent.setClass(LoginActivity.this, MainFragmentActivity.class);
                                            startActivity(intent);
                                            ToastUtil.showToastInScreenCenter(LoginActivity.this,"登录成功！");
                                            finish();
                                        }else{
                                            ToastUtil.showToastInScreenCenter(LoginActivity.this,"注册到IM失败，错误内容："+s);
                                        }
                                    }
                                });



                            }else if (jsonObject.getInt("ret")==1){
                                ToastUtil.showToastInScreenCenter(LoginActivity.this,"登录失败，错误原因：用户不存在");
                            }else if (jsonObject.getInt("ret")==2){
                                ToastUtil.showToastInScreenCenter(LoginActivity.this,"登录失败，错误原因：密码错误");
                            }



                        }catch (Exception e){
                            ToastUtil.showToastInScreenCenter(LoginActivity.this,"登录异常，请稍后重试!");
                        }

                    }
                });
                thread.start();


                break;
            case R.id.tvNewUser:
                //跳转到获取验证码界面，带入参数：业务类型（注册，找回密码，修改密码），手机号
                Intent intent = new Intent();
                intent.putExtra("opType",1 );
                intent.putExtra("phonenumber", edUserName.getText().toString());
                intent.setClass(LoginActivity.this, VerifyPhoneNumberActivity.class);
                startActivity(intent);
                break;
            case R.id.tvForgetPWD:
                Intent intent1 = new Intent();
                intent1.putExtra("opType",2 );
                intent1.putExtra("phonenumber", edUserName.getText().toString());
                intent1.setClass(LoginActivity.this, VerifyPhoneNumberActivity.class);
                startActivity(intent1);
                break;
        }
    }



}
