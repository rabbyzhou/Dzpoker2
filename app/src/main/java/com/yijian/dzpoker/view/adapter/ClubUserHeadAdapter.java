package com.yijian.dzpoker.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.yijian.dzpoker.R;
import com.yijian.dzpoker.activity.club.ClubInfoActivity;
import com.yijian.dzpoker.activity.club.ClubUserInfoActivity;
import com.yijian.dzpoker.view.CircleTransform;
import com.yijian.dzpoker.view.data.City;
import com.yijian.dzpoker.view.data.User;

/**
 * Created by koyabr on 10/22/15.
 */
public class ClubUserHeadAdapter extends BaseListAdapter<User, ClubUserHeadAdapter.ViewHolder> {




    public ClubUserHeadAdapter(Context context) {
        super(context);
    }

    //接口供外部调用
    public interface OnRecordSelectListener{
        void onRecordSelected(City city);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_club_user, parent, false);

        return new ViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final User user = mData.get(position);
        if (user == null) return;

        if (user.userHeadPic!=null && !user.userHeadPic.equals("")) {
            Picasso.with(mContext)
                    .load(user.userHeadPic)
                    .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .resize(100, 100)
                    .error(R.drawable.default_club_head)
                    .transform(new CircleTransform())
                    .into(holder.mUserHead);
        }
        holder.mView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
               //跳转到用户信息界面
                Intent intent = new Intent();
                intent.putExtra("user", user);
                intent.setClass(mContext, ClubUserInfoActivity.class);
                mContext.startActivity(intent);
                     }
        });

    }


    static class ViewHolder extends RecyclerView.ViewHolder  {

        public View mView;
        public ImageView mUserHead;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mUserHead=(ImageView) itemView.findViewById(R.id.iv_user_head);

        }
    }


}