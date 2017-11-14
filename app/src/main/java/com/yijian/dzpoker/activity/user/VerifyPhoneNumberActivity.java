package com.yijian.dzpoker.activity.user;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.yijian.dzpoker.R;
import com.yijian.dzpoker.util.ToastUtil;
import com.yijian.dzpoker.util.Util;
import com.yijian.dzpoker.util.WeakRefHandler;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;
import com.soundcloud.android.crop.Crop;
import com.squareup.picasso.Picasso;

public class VerifyPhoneNumberActivity extends AppCompatActivity implements View.OnClickListener {

    private int opType;//从前一界面来的操作类型，1=注册，2：找回密码 3：修改密码
    static final int VERIFY_CODE_WAIT_SECONDS = 60; // 验证码等待60秒
    static final int MSG_VERIFY_CODE_WAIT = 1;
    private final WeakHandler mHandler = new WeakHandler(this);
    private Timer mTimer = new Timer();
    private Button btnAskVerifyCode;
    private EditText edtPhoneNumber;
    private EditText edtVerifyCode;
    private String sVerifyCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone_number);

        Intent intent = getIntent();
        opType  = intent.getIntExtra("opType",0);
        String strPhoneNumber = intent.getStringExtra("phonenumber");

        edtPhoneNumber=(EditText)findViewById(R.id.et_username);
        edtPhoneNumber.setText(strPhoneNumber);

        if(opType==3){
            edtPhoneNumber.setEnabled(false);
        }else{
            edtPhoneNumber.setEnabled(true);
        }

        btnAskVerifyCode=(Button)findViewById(R.id.btn_ask_verify_code);
        btnAskVerifyCode.setOnClickListener(this);

        edtVerifyCode=(EditText)findViewById(R.id.et_verify_code);
        ImageView ivExit=(ImageView)findViewById(R.id.exit);
        ivExit.setOnClickListener(this);

        Button btnNext=(Button)findViewById(R.id.btn_next);
        btnNext.setOnClickListener(this);

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
            case R.id.btn_ask_verify_code:
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

                break;
            case R.id.btn_next:
                if (mTimer!=null) {
                    mTimer.cancel();
                }
                //下一步，此处校验短信验证码是否相同,相同则根据opType进入不同的界面
                if(edtVerifyCode.getText().toString().equals("")){
                    ToastUtil.showToastInScreenCenter(VerifyPhoneNumberActivity.this,"请输入短信验证码！");
                }
                if(edtVerifyCode.getText().toString().equals(sVerifyCode)){
                    switch(opType){
                        case 1:
                            Intent intent = new Intent();
                            intent.putExtra("phonenumber", edtPhoneNumber.getText().toString());
                            intent.setClass(VerifyPhoneNumberActivity.this, RegisterActivity.class);
                            startActivity(intent);
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
                break;
            case R.id.exit:
                if (mTimer!=null) {
                    mTimer.cancel();
                }
                finish();


        }
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

    @Override
    protected void onResume() {
        if (mTimer!=null) {
            mTimer.cancel();
        }
        super.onResume();
    }
}
