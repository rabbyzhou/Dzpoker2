package com.yijian.dzpoker.activity.user;

import android.util.Log;
import android.view.View;

import com.yijian.dzpoker.activity.fragment.BottomMenuFragment;
import com.yijian.dzpoker.view.data.PayMenuItem;


public abstract class MenuItemOnClickListener implements View.OnClickListener{

    public MenuItemOnClickListener(BottomMenuFragment _bottomMenuFragment, PayMenuItem _menuItem) {
        this.bottomMenuFragment = _bottomMenuFragment;
        this.menuItem = _menuItem;
    }
    private final String TAG = "MenuItemOnClickListener";

    public BottomMenuFragment getBottomMenuFragment() {
        return bottomMenuFragment;
    }

    public void setBottomMenuFragment(BottomMenuFragment bottomMenuFragment) {
        this.bottomMenuFragment = bottomMenuFragment;
    }

    public PayMenuItem getMenuItem() {
        return menuItem;
    }

    public void setMenuItem(PayMenuItem menuItem) {
        this.menuItem = menuItem;
    }

    private BottomMenuFragment bottomMenuFragment;
    private PayMenuItem menuItem;

    @Override
    public void onClick(View v){

        Log.i(TAG, "onClick: ");

        /**
        if(bottomMenuFragment != null && bottomMenuFragment.isVisible()) {
            bottomMenuFragment.dismiss();
        }**/

        this.onClickMenuItem(v, this.menuItem);
    }
    public abstract void onClickMenuItem(View v, PayMenuItem menuItem);
}
