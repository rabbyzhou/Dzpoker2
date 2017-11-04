package com.yijian.dzpoker.activity.user;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.yijian.dzpoker.R;
import com.yijian.dzpoker.activity.base.BaseBackActivity;

public class AboutActivity extends BaseBackActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tv_back.setOnClickListener(this);

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_about;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_back:
                finish();
                break;
        }

    }
}
