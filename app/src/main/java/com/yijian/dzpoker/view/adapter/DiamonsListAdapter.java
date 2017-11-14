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
import com.yijian.dzpoker.view.data.DiamondStoreGoods;

/**
 * Created by koyabr on 10/22/15.
 */
public class DiamonsListAdapter extends BaseListAdapter<DiamondStoreGoods, DiamonsListAdapter.ViewHolder> {

    private OnRecordSelectListener mListener;


    public DiamonsListAdapter(Context context, OnRecordSelectListener listener) {
        super(context);
        mListener=listener;
    }

    //接口供外部调用
    public interface OnRecordSelectListener{
        void onRecordSelected(DiamondStoreGoods diamondStoreGoods);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_diamonds_store_list, parent, false);

        return new ViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final DiamondStoreGoods diamondStoreGoods = mData.get(position);
        if (diamondStoreGoods == null) return;

        holder.mDiamons.setText(diamondStoreGoods.diamonds+"颗钻石");
        holder.mCostRMB.setText("￥"+ diamondStoreGoods.costrmb+".00");
        if (diamondStoreGoods.pic!=null && !diamondStoreGoods.pic.equals("")) {
            Picasso.with(mContext)
                    .load(diamondStoreGoods.pic)
                    .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .resize(80, 80)
                    .error(R.drawable.default_club_head)
                    .transform(new CircleTransform())
                    .into(holder.mDiamondHead);
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mListener.onRecordSelected(diamondStoreGoods);      }
        });

    }

    static class ViewHolder extends RecyclerView.ViewHolder  {

        public View mView;
        public TextView mDiamons,mCostRMB;
        public ImageView mDiamondHead;



        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mDiamondHead=(ImageView)itemView.findViewById(R.id.iv_diamonds_head);
            mDiamons=(TextView) itemView.findViewById(R.id.tv_diamonds);
            mCostRMB=(TextView) itemView.findViewById(R.id.tv_cost_rmb);
        }
    }




}