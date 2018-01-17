package com.yijian.dzpoker.activity.game;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.yijian.dzpoker.R;
import com.yijian.dzpoker.activity.base.BaseBackActivity;
import com.yijian.dzpoker.baselib.debug.Logger;
import com.yijian.dzpoker.baselib.http.RetrofitApiGenerator;
import com.yijian.dzpoker.constant.Constant;
import com.yijian.dzpoker.http.getgametype.GetGameTypeApi;
import com.yijian.dzpoker.http.getgametype.GetGameTypeCons;
import com.yijian.dzpoker.util.ToastUtil;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by c_huangl on 0015, 11/15/2017.
 */

public class GameAddActivity extends BaseBackActivity {
    private EditText et_game_id;
    private Button btn_confirm;

    private static final String TAG = "GameAddActivity";


    @Override
    protected int getLayoutId() {
        return R.layout.activity_game_add;
    }

    private final static String TITLE = "加入牌局";

    @Override
    protected void initViews() {
        super.initViews();

        et_game_id = (EditText) findViewById(R.id.et_game_id);
        btn_confirm = (Button) findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(this);


        String title = getIntent().getStringExtra(Constant.INTENT_KEY_TITLE);
        setToolbarTitle(TITLE);
        //TODO:qipu
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_back:
                finish();
                break;
            case R.id.btn_confirm:
                if (et_game_id.getText().toString().equals("")) {
                    ToastUtil.showToastInScreenCenter(GameAddActivity.this, "请输入牌局桌号！");
                }
                getGameType(et_game_id.getText().toString());
                break;

//                //106.14.221.253:11820
//                Intent intent = new Intent();
//                intent.putExtra("operation", 2);//1表示创建牌局，2表示加入牌局
//                intent.putExtra("gameid", et_game_id.getText().toString());
//                intent.putExtra("ip", "106.14.221.253");
//                intent.putExtra("port", 11820);
//                intent.setClass(GameAddActivity.this, GameActivity.class);
//                startActivity(intent);
//                finish();
//                break;
        }
    }

    private void getGameType(String shareCode) {
        try {
            GetGameTypeApi getClubInfoApi = RetrofitApiGenerator.createRequestApi(GetGameTypeApi.class);
            JSONObject param = new JSONObject();
            param.put(GetGameTypeCons.PARAM_KEY_SHARE_CODE, shareCode);

            Call<ResponseBody> getGameTypeCall = getClubInfoApi.getResponse(GetGameTypeCons.FUNC_NAME, param.toString());
            getGameTypeCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                    Logger.i(TAG, "getGameTypeCall response : " + response.body().toString());
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        int gameType = jsonObject.optInt("gametype");
                        int matchType = jsonObject.optInt("matchtype");
                        int gameId = jsonObject.optInt("gameid");
                        String ip = jsonObject.optString("ip");
                        int port = jsonObject.optInt("port");
                        if (matchType == -1) {
                            Intent intent = new Intent();
                            intent.putExtra("operation", 2);//1表示创建牌局，2表示加入牌局
                            intent.putExtra("gameid", gameId);
                            intent.putExtra("ip", ip);
                            intent.putExtra("port", port);
                            intent.setClass(GameAddActivity.this, GameActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            ToastUtil.showToastInScreenCenter(GameAddActivity.this, "当前不支持比赛");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
