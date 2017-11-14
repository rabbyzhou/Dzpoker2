package com.yijian.dzpoker.view.adapter;

import android.content.Context;
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
import com.yijian.dzpoker.view.data.UserLevel;

/**
 * Created by c_huangl on 0008, 11/08/2017.
 */

public class UserLevelAdapter extends BaseListAdapter<UserLevel, UserLevelAdapter.ViewHolder> {

    private OnRecordSelectListener mListener;
    private Context mContext;


    public UserLevelAdapter(Context context, OnRecordSelectListener listener) {
        super(context);
        mContext = context;
        mListener=listener;
    }

    //接口供外部调用
    public interface OnRecordSelectListener{
        void onRecordSelected(UserLevel userLevel);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_userlevelitem, parent, false);

        return new ViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final UserLevel data = mData.get(position);
        if (data == null) return;

        int level = data.level;
        int durationDays = data.durationDay;
        int maxClubs = data.maxClubs;
        int maxGameTables = data.maxGameTables;
        int giveCoins = data.giveCoins;

        String desc = mContext.getResources().getString(R.string.leveldesc,durationDays,maxClubs,maxGameTables,giveCoins);

        if (data.pic != null && !data.pic.isEmpty()){
            Picasso.with(mContext)
                    .load(data.pic)
                    .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    //.resize(40, 40)
                    .into(holder.mLevelPic);
        }

        holder.mLevelName.setText(data.levelName);
        holder.mLevelDesc.setText(desc);

        //普通会员, 不需要钻石购买
        if ( level == 1 && giveCoins == 0 ){
            holder.mDiamondIcon.setVisibility(View.INVISIBLE);
            holder.mCosts.setVisibility(View.INVISIBLE);
        } else {
            holder.mDiamondIcon.setVisibility(View.VISIBLE);
            holder.mCosts.setText(giveCoins + "");
            holder.mItemView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    mListener.onRecordSelected(data);
                }
            });

        }


    }

    static class ViewHolder extends RecyclerView.ViewHolder  {

        public ImageView mLevelPic;
        public TextView  mLevelName;
        public TextView mLevelDesc;
        public ImageView mDiamondIcon;
        public TextView mCosts;
        public View mItemView;

        public ViewHolder(View itemView) {
            super(itemView);
            mItemView = itemView;
            mLevelPic=(ImageView)itemView.findViewById(R.id.level_icon);
            mLevelName=(TextView) itemView.findViewById(R.id.level_name);
            mLevelDesc=(TextView) itemView.findViewById(R.id.level_desc);
            mDiamondIcon=(ImageView)itemView.findViewById(R.id.diamond_icon);
            mCosts=(TextView) itemView.findViewById(R.id.diamond_cost);
        }
    }
}