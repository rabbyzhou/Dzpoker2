package com.yijian.dzpoker.activity.game;

import android.os.Bundle;
import android.view.View;

import com.yijian.dzpoker.R;
import com.yijian.dzpoker.activity.base.BaseBackActivity;
import com.yijian.dzpoker.constant.Constant;

/**
 * Created by c_huangl on 0015, 11/15/2017.
 */

public class GameAddActivity   extends BaseBackActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_game_add;
    }

    private final static String TITLE = "加入牌局";

    @Override
    protected void initViews() {
        super.initViews();

        String title = getIntent().getStringExtra(Constant.INTENT_KEY_TITLE);
        if (title == null || title.isEmpty()) title = TITLE;
        tv_title.setText(title);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_back:
                finish();
                break;
            case R.id.btn_confirm:
                break;
        }
    }

}
