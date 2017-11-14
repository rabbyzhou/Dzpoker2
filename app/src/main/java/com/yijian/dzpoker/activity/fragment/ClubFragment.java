package com.yijian.dzpoker.activity.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yijian.dzpoker.R;
import com.yijian.dzpoker.activity.MainActivity;
import com.yijian.dzpoker.activity.club.MyClubActivity;
import com.yijian.dzpoker.constant.Constant;

import static com.yijian.dzpoker.constant.Constant.INTENT_KEY_BACKTEXT;

/**
 * Created by rabby on 2017/8/10.
 */

public class ClubFragment extends BaseFragment implements View.OnClickListener {

    TextView tvMyclub;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View clubLayout = inflater.inflate(R.layout.fragment_club_layout,
                container, false);
        ImageView ivMyclub=(ImageView)clubLayout.findViewById(R.id.image_myclub);
        ivMyclub.setOnClickListener(this);
        tvMyclub=(TextView)clubLayout.findViewById(R.id.text_myclub);
        tvMyclub.setOnClickListener(this);
        return clubLayout;
    }


    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        MainActivity.currFragTag = Constant.FRAGMENT_FLAG_CLUB;

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_myclub:
                gotomyclub();
                break;
            case R.id.text_myclub:
                gotomyclub();
                break;


        }
    }

    private void gotomyclub(){
        //跳转到主界面
        Intent intent = new Intent();
        intent.setClass(getActivity(), MyClubActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(INTENT_KEY_BACKTEXT, tvMyclub.getText());
        startActivity(intent);

    }





}
