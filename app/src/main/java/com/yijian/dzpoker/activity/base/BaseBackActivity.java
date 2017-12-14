package com.yijian.dzpoker.activity.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.yijian.dzpoker.R;
import com.yijian.dzpoker.constant.Constant;
import com.yijian.dzpoker.util.DzApplication;


/**
 * 带有toolbar, back arrow的activity
 */
public abstract class BaseBackActivity extends BaseToolbarActivity implements View.OnClickListener{

    protected TextView tv_back,tv_title;
    public DzApplication application;
    protected String strLoginName;
    protected int userId;
    protected String remoteURL;

    protected abstract int getLayoutId();


    protected void initViews(){
        tv_back = (TextView) findViewById(R.id.tv_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_back.setOnClickListener(this);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        application=(DzApplication) getApplication();
        strLoginName=application.getLoginName();
        userId=application.getUserId();
        remoteURL=getString(R.string.url_remote);

        initViews();

        Intent intent = getIntent();
        String backText = intent.getStringExtra(Constant.INTENT_KEY_BACKTEXT);
        if ( backText != null && !backText.isEmpty() && tv_back != null){
             tv_back.setText(backText);
        }

    }


}
