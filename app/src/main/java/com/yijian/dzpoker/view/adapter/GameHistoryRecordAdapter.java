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
import com.yijian.dzpoker.util.Util;
import com.yijian.dzpoker.view.CircleTransform;
import com.yijian.dzpoker.view.data.HistoryGameTableInfo;

/**
 * Created by c_huangl on 0008, 11/08/2017.
 */

public class GameHistoryRecordAdapter extends BaseListAdapter<HistoryGameTableInfo, GameHistoryRecordAdapter.ViewHolder> {

    private final static String TAG = "GameHistoryRecordAdapter";
    private OnRecordSelectListener mListener;
    private Context mContext;



    public GameHistoryRecordAdapter(Context context, GameHistoryRecordAdapter.OnRecordSelectListener listener) {
        super(context);
        mContext = context;
        mListener=listener;
    }

    //接口供外部调用
    public interface OnRecordSelectListener{
        void onRecordSelected(HistoryGameTableInfo data);
    }

    @Override
    public GameHistoryRecordAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_record_history, parent, false);

        return new GameHistoryRecordAdapter.ViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(GameHistoryRecordAdapter.ViewHolder holder, int position) {
        final HistoryGameTableInfo data = mData.get(position);

        if (data == null) return;

        if (data.createuserheadpic != null && !data.createuserheadpic.isEmpty()){
            Picasso.with(mContext)
                    .load(data.createuserheadpic)
                    .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    //.resize(100, 100)
                    .transform(new CircleTransform())
                    .into(holder.mUserHead);
        }

        holder.mDate.setText(Util.getFormatDate(mContext, data.starttime));
        holder.mTime.setText(Util.getFormatTime(mContext, data.starttime));
        holder.mClubName.setText(data.gametablename);
        holder.mDurationTime.setText(data.durationmin + "");
        holder.mHands.setText(data.joinhands + "");
        holder.mWinChips.setText(data.userwinchips + "");

        holder.mItemView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mListener.onRecordSelected(data);
            }
        });

    }

    static class ViewHolder extends RecyclerView.ViewHolder  {

        public TextView mDate;
        public TextView mTime;
        public TextView mClubName;
        public TextView mDurationTime;
        public TextView mHands;
        public TextView mWinChips;
        public ImageView mUserHead;
        public View mItemView;

        public ViewHolder(View itemView) {
            super(itemView);
            mItemView = itemView;
            mDate=(TextView)itemView.findViewById(R.id.date);
            mTime=(TextView) itemView.findViewById(R.id.time);
            mClubName=(TextView) itemView.findViewById(R.id.club_name);
            mDurationTime=(TextView) itemView.findViewById(R.id.duration_time);
            mHands=(TextView) itemView.findViewById(R.id.hands);
            mWinChips=(TextView) itemView.findViewById(R.id.win_chips);
            mUserHead=(ImageView) itemView.findViewById(R.id.user_head);
        }
    }
}
