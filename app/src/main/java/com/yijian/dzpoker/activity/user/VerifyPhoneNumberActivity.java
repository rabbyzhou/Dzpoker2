package com.yijian.dzpoker.activity.user;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yijian.dzpoker.R;
import com.yijian.dzpoker.activity.base.BaseToolbarActivity;
import com.yijian.dzpoker.util.DzApplication;
import com.yijian.dzpoker.util.ToastUtil;
import com.yijian.dzpoker.util.Util;
import com.yijian.dzpoker.util.WeakRefHandler;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;
import okhttp3.Request;
import okhttp3.Response;

public class VerifyPhoneNumberActivity extends BaseToolbarActivity implements View.OnClickListener {

    private int opType;//从前一界面来的操作类型，1=注册，2：找回密码 3：修改密码
    static final int VERIFY_CODE_WAIT_SECONDS = 60; // 验证码等待60秒
    static final int MSG_VERIFY_CODE_WAIT = 1;
    private final WeakHandler mHandler = new WeakHandler(this);
    private Timer mTimer = new Timer();
    private TextView btnAskVerifyCode;
    private EditText edtPhoneNumber;
    private EditText edtVerifyCode;
    private String sVerifyCode;
    private EditText password;
    private EditText nickName;
    private LinearLayout backLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone_number);
        setToolbarTitle("注册");
        getSupportActionBar().hide();
//        ActionBar actionBar = getSupportActionBar();
//        if (null != actionBar) {
//            actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#33000000")));
//            actionBar.setSplitBackgroundDrawable(new ColorDrawable(Color.parseColor("#33000000")));
//        }
        Intent intent = getIntent();
        initViews(intent);
    }

    private void initViews(Intent intent) {
        opType  = intent.getIntExtra("opType",0);
        String strPhoneNumber = intent.getStringExtra("phonenumber");

        edtPhoneNumber=(EditText)findViewById(R.id.register_page_phone);
        edtPhoneNumber.setText(strPhoneNumber);

        if(opType==3){
            edtPhoneNumber.setEnabled(false);
        }else{
            edtPhoneNumber.setEnabled(true);
        }

        btnAskVerifyCode=(TextView) findViewById(R.id.register_page_get_verify_code);
        btnAskVerifyCode.setOnClickListener(this);

        edtVerifyCode=(EditText)findViewById(R.id.register_page_verify_code);
        password = (EditText) findViewById(R.id.register_page_password);
        nickName = (EditText) findViewById(R.id.register_page_set_nick_name);
        ImageView ivExit=(ImageView)findViewById(R.id.exit);
        ivExit.setOnClickListener(this);

        Button btnNext=(Button)findViewById(R.id.register_page_complete);
        btnNext.setOnClickListener(this);
        backLayout = (LinearLayout) findViewById(R.id.register_page_back);
        backLayout.setOnClickListener(this);

        TextView tvTile=(TextView)findViewById(R.id.title);
        if (opType==1){
            tvTile.setText("注册");
        }else if(opType==2){
            tvTile.setText("找回密码");
        }else if(opType==3){
            tvTile.setText("修改密码");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_page_get_verify_code:
                requestVerifyCode();
                break;
            case R.id.register_page_complete:
                //下一步，此处校验短信验证码是否相同,相同则根据opType进入不同的界面
                doRegisterComplete();
                break;
            case R.id.exit:
                finish();
                break;
            case R.id.register_page_back:
                finish();
                break;
            default:
                break;
        }
    }

    private void requestVerifyCode() {
        if (edtPhoneNumber.getText().toString().equals(""))
         {
             ToastUtil.showToastInScreenCenter(VerifyPhoneNumberActivity.this,"手机号不为空！");
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
                    jsonObj.put("mobilephone",edtPhoneNumber.getText().toString());


                    String strURL=getString(R.string.url_remote);
                    strURL+="func=smsvertify&param="+jsonObj.toString();

                    URL url = new URL(strURL);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();// 利用HttpURLConnection对象,我们可以从网络中获取网页数据.
                    conn.setConnectTimeout(5 * 1000);   // 单位是毫秒，设置超时时间为5秒
                    conn.setRequestMethod("GET");       // HttpURLConnection是通过HTTP协议请求path路径的，所以需要设置请求方式,可以不设置，因为默认为GET
                    if (conn.getResponseCode() == 200) {// 判断请求码是否是200码，否则失败
                        InputStream is = conn.getInputStream(); // 获取输入流
                        byte[] data = Util.readStream(is);   // 把输入流转换成字符数组
                        String result = new String(data);        // 把字符数组转换成字符串
                        //这里返回json字符串
                        JSONObject jsonResult = new JSONObject(result);
                        int ret=jsonResult.getInt("result");
                        if (ret==0){
                            //按钮变灰，等待短信
                            sVerifyCode=jsonResult.getString("vertifymsg");
                            btnAskVerifyCode.setClickable(false);
                            mTimer = new Timer();
                            mTimer.schedule(new VerifyCodeTimerTask(), 0, 1000);
                        }else{
                            ToastUtil.showToastInScreenCenter(VerifyPhoneNumberActivity.this,"从服务器获取验证码失败，请稍后重试！");

                        }


                    }

                }catch (Exception e){
                    ToastUtil.showToastInScreenCenter(VerifyPhoneNumberActivity.this,"获取短信验证码异常，请稍后重试!");

                }

            }
        });
        thread.start();
        //请求短信验证码
    }

    private void doRegisterComplete() {
        if(checkEverythingPass()){
            switch(opType){
                case 1:
//                            Intent intent = new Intent();
//                            intent.putExtra("phonenumber", edtPhoneNumber.getText().toString());
//                            intent.setClass(VerifyPhoneNumberActivity.this, RegisterActivity.class);
//                            startActivity(intent);
                    doRegister();
                    finish();
                    break;
                case 2:
                    Intent intent1 = new Intent();
                    intent1.putExtra("phonenumber", edtPhoneNumber.getText().toString());
                    intent1.setClass(VerifyPhoneNumberActivity.this, ResetPWDActivity.class);
                    startActivity(intent1);
                    finish();
                    break;
                case 3:
                    Intent intent2 = new Intent();
                    intent2.putExtra("phonenumber", edtPhoneNumber.getText().toString());
                    intent2.setClass(VerifyPhoneNumberActivity.this, ResetPWDActivity.class);
                    startActivity(intent2);
                    finish();
                    break;
            }
        }
    }

    /**
     * 校验手机号，密码，验证码，昵称
     * @return
     */
    private boolean checkEverythingPass() {

        if (edtPhoneNumber.getText().toString().equals(""))
        {
            ToastUtil.showToastInScreenCenter(VerifyPhoneNumberActivity.this,"手机号不为空！");
            return false;
        }

        if(!edtVerifyCode.getText().toString().equals(sVerifyCode)){
            ToastUtil.showToastInScreenCenter(VerifyPhoneNumberActivity.this,"短信验证码错误！");
            return false;
        }

        if (password.getText().toString().equals(""))
        {
            ToastUtil.showToastInScreenCenter(this,"密码不为空！");
            return false;
        }
        if(password.length()<6||password.length()>12){
            ToastUtil.showToastInScreenCenter(this,"密码长度为6-12位！");
            return false;
        }
        if (nickName.getText().toString().equals(""))
        {
            ToastUtil.showToastInScreenCenter(this,"昵称不为空！");
            return false;
        }
        return true;
    }

    static class WeakHandler extends WeakRefHandler<VerifyPhoneNumberActivity> {

        public WeakHandler(VerifyPhoneNumberActivity ref) {
            super(ref);
        }

        @Override
        protected void handleMessage(VerifyPhoneNumberActivity ref, Message msg) {
            switch (msg.what) {
                case MSG_VERIFY_CODE_WAIT:
                    int seconds = msg.arg1;
                    if (seconds > 0) {
                        // 在按钮上显示剩余等待时间
                        ref.btnAskVerifyCode.setTextColor(Color.GRAY);
                        ref.btnAskVerifyCode.setText(String.format("(%d)秒后重新获取", seconds));
                        ref.btnAskVerifyCode.setClickable(false);
                    } else {
                        // 等待时间结束
                        ref.mTimer.cancel();

                        ref.btnAskVerifyCode.setTextColor(ref.getResources().getColor(R.color.theme_color));
                        ref.btnAskVerifyCode.setText("获取验证码");
                        ref.btnAskVerifyCode.setClickable(true);
                    }
                    break;
            }
        }
    }

    private void doRegister() {
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
                    jsonObj.put("loginname",edtPhoneNumber.getText().toString());
                    jsonObj.put("nickname",nickName.getText().toString());
                    jsonObj.put("psw",password.getText().toString());
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
                                    ToastUtil.showToastInScreenCenter(VerifyPhoneNumberActivity.this,"注册成功！");
//                                            UserInfo userInfo=JMessageClient.getMyInfo();
//                                            userInfo.setNickname(edNickname.getText().toString());
//                                            JMessageClient.updateMyInfo(UserInfo.Field.nickname, JMessageClient.getMyInfo(), new BasicCallback() {
//                                                @Override
//                                                public void gotResult(int i, String s) {
//                                                    if (i==0) {
//                                                        ToastUtil.showToastInScreenCenter(RegisterActivity.this,"注册成功！");
//                                                        finish();
//                                                    }
//                                                }
//                                            });

                                }else{
                                    ToastUtil.showToastInScreenCenter(VerifyPhoneNumberActivity.this,"注册到IM失败，错误内容："+s);
                                }
                            }
                        });

                    }else {
                        ToastUtil.showToastInScreenCenter(VerifyPhoneNumberActivity.this,jsonObject.getString("msg"));
                    }


                }catch (Exception e){
                    ToastUtil.showToastInScreenCenter(VerifyPhoneNumberActivity.this,"注册异常，请稍后重试！");

                }

            }
        });
        thread.start();
    }

    class VerifyCodeTimerTask extends TimerTask {

        private int mSecond;

        public VerifyCodeTimerTask() {
            mSecond = VERIFY_CODE_WAIT_SECONDS;
        }

        @Override
        public void run() {
            if (mSecond < 0) {
                return;
            }
            mHandler.sendMessage(mHandler.obtainMessage(MSG_VERIFY_CODE_WAIT, mSecond, 0));
            mSecond--;
        }
    }
}
