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
import com.yijian.dzpoker.view.CircleTransform;
import com.yijian.dzpoker.view.data.ClubLevel;
import com.yijian.dzpoker.view.data.DiamondStoreGoods;

/**
 * Created by koyabr on 10/22/15.
 */
public class ClubLevelListAdapter extends BaseListAdapter<ClubLevel, ClubLevelListAdapter.ViewHolder> {

    private OnRecordSelectListener mListener;


    public ClubLevelListAdapter(Context context, OnRecordSelectListener listener) {
        super(context);
        mListener=listener;
    }

    //接口供外部调用
    public interface OnRecordSelectListener{
        void onRecordSelected(ClubLevel clubLevel);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_club_level_list, parent, false);

        return new ViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final ClubLevel clubLevel = mData.get(position);
        if (clubLevel == null) return;

        if (clubLevel.levelpic!=null && !clubLevel.levelpic.equals("")) {
            Picasso.with(mContext)
                    .load(clubLevel.levelpic)
                    .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .resize(100, 100)
                    .error(R.drawable.default_club_level)
                    .transform(new CircleTransform())
                    .into(holder.mClubLevelHead);
        }

        holder.mclubLevelName.setText(clubLevel.levelabstract);
        holder.mClubLevelInfo.setText(String.format(mContext.getString(R.string.club_level),clubLevel.maxmembers+"",clubLevel.maxmanagers+"",clubLevel.durationdays+""));
        holder.mClubLevelDiamonds.setText(clubLevel.costdiamonds+"");


        holder.mView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mListener.onRecordSelected(clubLevel);      }
        });

    }

    static class ViewHolder extends RecyclerView.ViewHolder  {

        public View mView;
        public TextView mclubLevelName,mClubLevelInfo,mClubLevelDiamonds;
        public ImageView mClubLevelHead;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mClubLevelHead=(ImageView)itemView.findViewById(R.id.iv_club_level_head);
            mclubLevelName=(TextView) itemView.findViewById(R.id.tv_club_level_name);
            mClubLevelInfo=(TextView) itemView.findViewById(R.id.tv_club_level_info);
            mClubLevelDiamonds=(TextView) itemView.findViewById(R.id.tv_diamonds);
        }
    }




}