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
import com.yijian.dzpoker.view.data.GameTableUserInfo;

/**
 * Created by c_huangl on 0008, 11/08/2017.
 */

public class GameTableUserAdapter extends BaseListAdapter<GameTableUserInfo, GameTableUserAdapter.ViewHolder> {

    private final static String TAG = "GameTableUserAdapter";
    private OnRecordSelectListener mListener;
    private Context mContext;



    public GameTableUserAdapter(Context context, GameTableUserAdapter.OnRecordSelectListener listener) {
        super(context);
        mContext = context;
        mListener=listener;
    }

    //接口供外部调用
    public interface OnRecordSelectListener{
        void onRecordSelected(GameTableUserInfo data);
    }

    @Override
    public GameTableUserAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_record_history_list, parent, false);

        return new GameTableUserAdapter.ViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(GameTableUserAdapter.ViewHolder holder, int position) {
        final GameTableUserInfo data = mData.get(position);

        if (data == null) return;

        holder.mIndexView.setText((position + 1) + " ");
        if (data.headpic != null && !data.headpic.isEmpty()){
            Picasso.with(mContext)
                    .load(data.headpic)
                    .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    //.resize(100, 100)
                    .transform(new CircleTransform())
                    .noPlaceholder()
                    .into(holder.mUserHead);
        }

        holder.mUserName.setText(data.nickname);
        holder.mTakeinchips.setText(data.takeinchips + "");
        holder.mAllhands.setText(data.allhands + "");
        holder.mWinchips.setText(data.winchips + "");

        holder.mItemView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            }
        });

    }

    static class ViewHolder extends RecyclerView.ViewHolder  {

        public ImageView mUserHead;
        public TextView mIndexView;
        public TextView mUserName;
        public TextView mTakeinchips;
        public TextView mAllhands;
        public TextView mWinchips;

        public View mItemView;

        public ViewHolder(View itemView) {
            super(itemView);
            mItemView = itemView;
            mIndexView=(TextView) itemView.findViewById(R.id.index);
            mUserHead=(ImageView) itemView.findViewById(R.id.ranking_headpic);
            mUserName=(TextView)itemView.findViewById(R.id.ranking_nickname);
            mTakeinchips=(TextView) itemView.findViewById(R.id.takeinchips);
            mAllhands=(TextView) itemView.findViewById(R.id.allhands);
            mWinchips=(TextView) itemView.findViewById(R.id.winchips);

        }
    }
}
