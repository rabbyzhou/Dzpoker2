package com.yijian.dzpoker.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.yijian.dzpoker.R;
import com.yijian.dzpoker.view.data.GoldCoinStore;

/**
 * Created by c_huangl on 0008, 11/08/2017.
 */

public class GoldCoinShopAdapter extends BaseListAdapter<GoldCoinStore, GoldCoinShopAdapter.ViewHolder> {

    private final static String TAG = "GoldCoinShopAdapter";
    private OnRecordSelectListener mListener;
    private Context mContext;


    public GoldCoinShopAdapter(Context context, GoldCoinShopAdapter.OnRecordSelectListener listener) {
        super(context);
        mContext = context;
        mListener=listener;
    }

    //接口供外部调用
    public interface OnRecordSelectListener{
        void onRecordSelected(GoldCoinStore data);
    }

    @Override
    public GoldCoinShopAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_goldcoinitem, parent, false);

        Log.d(TAG, "onCreateViewHolder: " + itemView);
        return new GoldCoinShopAdapter.ViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(GoldCoinShopAdapter.ViewHolder holder, int position) {
        final GoldCoinStore data = mData.get(position);

        Log.d(TAG, "onBindViewHolder: position = " + position);
        if (data == null) return;

        if (data.pic != null && !data.pic.isEmpty()){
            Picasso.with(mContext)
                    .load(data.pic)
                    .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    //.resize(20, 20)
                    .into(holder.mPic);
        }

        holder.mGoldCoin.setText(data.goldcoins + "");
        holder.mDiamondCost.setText(data.costdiamonds + "");

        holder.mItemView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mListener.onRecordSelected(data);
            }
        });

    }

    static class ViewHolder extends RecyclerView.ViewHolder  {

        public ImageView mPic;
        public TextView mGoldCoin;
        public TextView mDiamondCost;
        public View mItemView;

        public ViewHolder(View itemView) {
            super(itemView);
            mItemView = itemView;
            mPic=(ImageView)itemView.findViewById(R.id.product_pic);
            mGoldCoin=(TextView) itemView.findViewById(R.id.goldcoin);
            mDiamondCost=(TextView) itemView.findViewById(R.id.diamond_cost);
        }
    }
}
