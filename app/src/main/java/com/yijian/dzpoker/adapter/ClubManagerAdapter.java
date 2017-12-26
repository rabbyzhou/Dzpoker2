package com.yijian.dzpoker.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yijian.dzpoker.R;
import com.yijian.dzpoker.entity.ClubManagerBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by qipu.qp on 2017/12/27.
 */

public class ClubManagerAdapter extends RecyclerView.Adapter {

    private List<ClubManagerBean> data = new ArrayList<ClubManagerBean>();

    public ClubManagerAdapter(List<ClubManagerBean> data) {
        this.data = data;
    }

    public void updateData(List<ClubManagerBean> list) {
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.club_manager_page_item, parent, false);
        return new ClubManagerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (null == data) {
            return;
        }
        ((ClubManagerViewHolder) holder).mainMsg.setText(data.get(position).getMainMsg());
        ((ClubManagerViewHolder) holder).detailMsg.setText(data.get(position).getDetailMsg());
        ((ClubManagerViewHolder) holder).rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO:reject
            }
        });
        ((ClubManagerViewHolder) holder).agreeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO:agree
            }
        });
    }

    @Override
    public int getItemCount() {
        return null != data ? data.size() : 0;
    }

    static class ClubManagerViewHolder extends RecyclerView.ViewHolder {

        public TextView mainMsg;
        public TextView detailMsg;
        public TextView rejectButton;
        public TextView agreeButton;

        public ClubManagerViewHolder(View itemView) {
            super(itemView);
            mainMsg = (TextView) itemView.findViewById(R.id.club_manager_item_main_msg);
            detailMsg = (TextView) itemView.findViewById(R.id.club_manager_item_detail_msg);
            rejectButton = (TextView) itemView.findViewById(R.id.club_manager_item_reject);
            agreeButton = (TextView) itemView.findViewById(R.id.club_manager_item_agree);
        }
    }
}
