package com.yijian.dzpoker.ui;

import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yijian.dzpoker.R;

public class RemoverViewHolder extends RecyclerView.ViewHolder {
    public TextView mUserName;
    public TextView mDelete;
    public LinearLayout layout,layout_user;
    public ImageView mUserHeadPic;

    public RemoverViewHolder(View itemView) {
        super(itemView);
        mUserName = (TextView) itemView.findViewById(R.id.tv_user_name);
        mDelete = (TextView) itemView.findViewById(R.id.item_delete);
        layout = (LinearLayout) itemView.findViewById(R.id.item_layout);
        layout_user = (LinearLayout) itemView.findViewById(R.id.layout_user);
        mUserHeadPic=(ImageView)itemView.findViewById(R.id.iv_user_head);
    }
}
