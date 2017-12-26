package com.yijian.dzpoker.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yijian.dzpoker.R;
import com.yijian.dzpoker.entity.OfficialInfoBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by qipu.qp on 2017/12/26.
 */

public class OfficialInfoAdapter extends RecyclerView.Adapter {


    private List<OfficialInfoBean> data = new ArrayList<OfficialInfoBean>();
    private View.OnClickListener listener;

    public OfficialInfoAdapter(List<OfficialInfoBean> list, View.OnClickListener l) {
        if (null != list) {
            this.data.addAll(list);
        }
        this.listener = l;
    }

    public void updateData(List<OfficialInfoBean> list) {
        if (null != list) {
            if (null != data) {
                data.clear();
                data.addAll(list);
                notifyDataSetChanged();
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.club_official_info_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ViewHolder) holder).mainMsg.setText(data.get(position).getMainMsg());
        ((ViewHolder) holder).detailMsg.setText(data.get(position).getDetailMsg());
        ((ViewHolder) holder).rootLayout.setOnClickListener(listener);
    }

    @Override
    public int getItemCount() {
        return null != data ? data.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        public RelativeLayout rootLayout;
        public ImageView icon;
        public TextView mainMsg;
        public TextView detailMsg;

        public ViewHolder(View itemView) {
            super(itemView);
            rootLayout = (RelativeLayout) itemView.findViewById(R.id.club_official_info_item_root);
            icon = (ImageView) itemView.findViewById(R.id.official_info_list_icon);
            mainMsg = (TextView) itemView.findViewById(R.id.official_info_list_main_msg);
            detailMsg = (TextView) itemView.findViewById(R.id.official_info_list_detail_msg);
        }
    }
}
