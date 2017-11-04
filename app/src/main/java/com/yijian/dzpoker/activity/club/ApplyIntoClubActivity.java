package com.yijian.dzpoker.activity.club;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.yijian.dzpoker.R;
import com.yijian.dzpoker.util.DzApplication;

public class ApplyIntoClubActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText et_applyinfo;
    private Button btn_send;
    private String strLoginName;
    private int userId;
    private DzApplication application;
    private int clubId;
    private  final static int MESAGE_GETCLUBLIST_OK=0x1001;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_into_club);
        application=(DzApplication)getApplication();
        strLoginName=application.getLoginName();
        userId=application.getUserId();
        Intent intent = this.getIntent();
        clubId=intent.getIntExtra("clubid",0);
        initViews();


    }

    private void initViews(){
         et_applyinfo=(EditText)findViewById(R.id.et_applyinfo);
         btn_send=(Button)findViewById(R.id.btn_send);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_send:
                break;
        }
    }
}
