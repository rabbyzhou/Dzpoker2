package com.yijian.dzpoker.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.yijian.dzpoker.activity.club.SelectCityActivity;
import com.yijian.dzpoker.view.data.Province;
import com.yijian.dzpoker.R;

import java.util.Date;

/**
 * Created by koyabr on 10/22/15.
 */
public class SelectProvinceAdapter extends BaseListAdapter<Province, SelectProvinceAdapter.ViewHolder> {

    private SelectProvinceAdapter.OnRecordSelectListener mListener;


    public SelectProvinceAdapter(Context context, SelectProvinceAdapter.OnRecordSelectListener listener) {
        super(context);
        mListener=listener;
    }

    //接口供外部调用
    public interface OnRecordSelectListener{
        void onRecordSelected(Province province);
    }

    @Override
    public SelectProvinceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_province, parent, false);

        return new SelectProvinceAdapter.ViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(SelectProvinceAdapter.ViewHolder holder, int position) {
        final Province province = mData.get(position);
        if (province == null) return;

        holder.mProvinceName.setText(province.provinceName);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mListener.onRecordSelected(province);
            }
        });

    }


    static class ViewHolder extends RecyclerView.ViewHolder  {

        public View mView;
        public TextView mProvinceName;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mProvinceName=(TextView) itemView.findViewById(R.id.tv_province);

        }
    }


}