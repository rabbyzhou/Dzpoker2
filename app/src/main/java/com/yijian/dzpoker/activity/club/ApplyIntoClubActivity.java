package com.yijian.dzpoker.activity.club;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.yijian.dzpoker.R;
import com.yijian.dzpoker.activity.base.BaseBackActivity;
import com.yijian.dzpoker.baselib.http.httpapi.HttpRequestBaseApi;
import com.yijian.dzpoker.baselib.debug.Logger;
import com.yijian.dzpoker.http.RequestJoinClubConstants;
import com.yijian.dzpoker.baselib.http.RetrofitApiGenerator;
import com.yijian.dzpoker.constant.Constant;
import com.yijian.dzpoker.util.DzApplication;
import com.yijian.dzpoker.util.ToastUtil;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApplyIntoClubActivity extends BaseBackActivity implements View.OnClickListener {
    private EditText et_applyinfo;
    private Button btn_send;
    private String strLoginName;
    private int userId;
    private DzApplication application;
    private int clubId;
    private final static int MESAGE_GETCLUBLIST_OK = 0x1001;
    private TextView exitText;

    private static final String TAG = "ApplyIntoClubActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        application = (DzApplication) getApplication();
        strLoginName = application.getLoginName();
        userId = application.getUserId();
        Intent intent = this.getIntent();
        clubId = intent.getIntExtra("clubid", 0);
        setToolbarTitle("俱乐部验证");

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_apply_into_club;
    }

    @Override
    public void initViews() {
        et_applyinfo = (EditText) findViewById(R.id.et_applyinfo);
        btn_send = (Button) findViewById(R.id.btn_send);
        btn_send.setOnClickListener(this);
        exitText = (TextView) findViewById(R.id.tv_exit);
        exitText.setOnClickListener(this);

        Intent intent = getIntent();
        String backText = intent.getStringExtra(Constant.INTENT_KEY_BACKTEXT);
        if (backText != null && !backText.isEmpty()) {
            exitText.setText(backText);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send:
                if (checkRequestMsg()) {
                    sendRequest();
                }
                break;
            case R.id.tv_exit:
                finish();
                break;
        }
    }

    private boolean checkRequestMsg() {
        if (TextUtils.isEmpty(et_applyinfo.getText().toString())) {
            ToastUtil.showToastInScreenCenter(ApplyIntoClubActivity.this, "申请信息不能为空！");
            return false;
        }
        return true;
    }

    private void sendRequest() {
        HttpRequestBaseApi httpRequestBaseApi = RetrofitApiGenerator.createRequestApi(HttpRequestBaseApi.class);

        try {
            JSONObject params = new JSONObject();
            params.put(RequestJoinClubConstants.PARAM_KEY_USER_ID, userId);
            params.put(RequestJoinClubConstants.PARAM_KEY_CLUB_ID, clubId);
            params.put(RequestJoinClubConstants.PARAM_KEY_REQUEST_MSG, et_applyinfo.getText());
            Logger.i(TAG, "response=" + params.toString());
            Call<ResponseBody> call = httpRequestBaseApi.getResponse(RequestJoinClubConstants.FUNC_NAME, params.toString());

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        Logger.i(TAG, "response=" + response.body().string());
                        finish();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    ToastUtil.showToastInScreenCenter(ApplyIntoClubActivity.this, "发送请求失败，请尝试重新添加！");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
