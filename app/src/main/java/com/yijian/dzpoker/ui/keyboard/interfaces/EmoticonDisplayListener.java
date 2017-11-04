package com.yijian.dzpoker.ui.keyboard.interfaces;

import android.view.ViewGroup;

import com.yijian.dzpoker.ui.keyboard.adpater.EmoticonsAdapter;

public interface EmoticonDisplayListener<T> {

    void onBindView(int position, ViewGroup parent, EmoticonsAdapter.ViewHolder viewHolder, T t, boolean isDelBtn);
}
