package com.yijian.dzpoker.activity.game;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.yijian.dzpoker.R;
import com.yijian.dzpoker.activity.base.BaseBackActivity;
import com.yijian.dzpoker.constant.Constant;
import com.yijian.dzpoker.util.ToastUtil;

/**
 * Created by c_huangl on 0015, 11/15/2017.
 */

public class GameAddActivity extends BaseBackActivity {
    private EditText et_game_id;
    private Button btn_confirm;


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
                //106.14.221.253:11820
                Intent intent = new Intent();
                intent.putExtra("operation", 2);//1表示创建牌局，2表示加入牌局
                intent.putExtra("gameid", et_game_id.getText().toString());
                intent.putExtra("ip", "106.14.221.253");
                intent.putExtra("port", 11820);
                intent.setClass(GameAddActivity.this, GameActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }

}
