package com.yijian.dzpoker.activity.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.yijian.dzpoker.R;

import com.yijian.dzpoker.activity.MainActivity;
import com.yijian.dzpoker.constant.Constant;

/**
 * Created by rabby on 2017/8/10.
 */

public class MainFragment extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mainLayout = inflater.inflate(R.layout.fragment_main_layout,
                container, false);
        return mainLayout;
    }


    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        MainActivity.currFragTag = Constant.FRAGMENT_FLAG_MAIN;

    }
}
