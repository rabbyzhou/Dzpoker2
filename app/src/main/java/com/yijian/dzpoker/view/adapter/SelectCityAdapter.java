package com.yijian.dzpoker.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.yijian.dzpoker.R;
import com.yijian.dzpoker.activity.club.SelectCityActivity;
import com.yijian.dzpoker.view.data.City;
import com.yijian.dzpoker.view.data.Province;

/**
 * Created by koyabr on 10/22/15.
 */
public class SelectCityAdapter extends BaseListAdapter<City, SelectCityAdapter.ViewHolder> {

    private OnRecordSelectListener mListener;


    public SelectCityAdapter(Context context, OnRecordSelectListener listener) {
        super(context);
        mListener=listener;
    }

    //接口供外部调用
    public interface OnRecordSelectListener{
        void onRecordSelected(City city);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_city, parent, false);

        return new ViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final City city = mData.get(position);
        if (city == null) return;

        holder.mCityName.setText(city.cityName);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mListener.onRecordSelected(city);      }
        });

    }


    static class ViewHolder extends RecyclerView.ViewHolder  {

        public View mView;
        public TextView mCityName;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mCityName=(TextView) itemView.findViewById(R.id.tv_city);

        }
    }

}