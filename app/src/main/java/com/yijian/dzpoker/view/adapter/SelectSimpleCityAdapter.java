package com.yijian.dzpoker.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yijian.dzpoker.R;

import java.util.List;

/**
 * Created by rabby on 2017/8/20.
 */

public class SelectSimpleCityAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater layoutInflater;
    View[] itemViews;

    public SelectSimpleCityAdapter(Context context, List<String> mlistInfo) {
        // TODO Auto-generated constructor stub
        this.context=context;
        this.layoutInflater=LayoutInflater.from(context);
        itemViews = new View[mlistInfo.size()];
        for(int i=0;i<mlistInfo.size();i++){
            String province=mlistInfo.get(i);    //获取第i个对象
            //调用makeItemView，实例化一个Item
            itemViews[i]=makeItemView(province);
        }
    }

    public int getCount() {
        return itemViews.length;
    }

    public View getItem(int position) {
        return itemViews[position];
    }

    public long getItemId(int position) {
        return position;
    }

    //绘制Item的函数
    private View makeItemView(String province) {
       // 使用View的对象itemView与R.layout.item关联
        View itemView = layoutInflater.inflate(R.layout.item_city, null);

        // 通过findViewById()方法实例R.layout.item内各组件
        TextView tvCity = (TextView) itemView.findViewById(R.id.tv_city);
        tvCity.setText(province);    //填入相应的值


        return itemView;
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            return itemViews[position];
        return convertView;
    }
}
