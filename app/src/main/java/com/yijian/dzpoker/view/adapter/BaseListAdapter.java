package com.yijian.dzpoker.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by koyabr on 8/19/15.
 */
public abstract class BaseListAdapter<T, VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> {

    protected Context mContext;
    protected List<T> mData;

    protected BaseListAdapter(Context context) {
        mContext = context;
        mData = new ArrayList<>();
    }

    public void setData(List<T> data) {
        mData = data;
        notifyDataSetChanged();
    }

    public void addLast(T data) {
        if (mData != null) {
            mData.add(data);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}
