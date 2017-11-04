package com.yijian.dzpoker.ui.keyboard.interfaces;

import android.view.View;
import android.view.ViewGroup;

import com.yijian.dzpoker.ui.keyboard.data.PageEntity;

public interface PageViewInstantiateListener<T extends PageEntity> {

    View instantiateItem(ViewGroup container, int position, T pageEntity);
}
