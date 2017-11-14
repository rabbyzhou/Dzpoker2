package com.yijian.dzpoker.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.yijian.dzpoker.R;
import com.yijian.dzpoker.view.data.ApplyInfo;
import com.yijian.dzpoker.view.data.City;

/**
 * Created by koyabr on 10/22/15.
 */
public class ControlInApplyAdapter extends BaseListAdapter<ApplyInfo, ControlInApplyAdapter.ViewHolder> {

    private onButtonClick mListener;


    public ControlInApplyAdapter(Context context, onButtonClick listener) {
        super(context);
        mListener=listener;
    }

    //接口供外部调用
    public interface onButtonClick{
        void onButtonClick(ApplyInfo applyInfo);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_apply_list, parent, false);

        return new ViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final ApplyInfo applyInfo = mData.get(position);
        if (applyInfo == null) return;

        holder.mApplyTime.setText("");
        holder.mHouseName.setText(applyInfo.tablename);
        holder.mPlayer.setText(applyInfo.usernickname);
        holder.mApplyinfo.setText(String.format(mContext.getString(R.string.apply_info),applyInfo.requesttakeinchips+"",applyInfo.requestpermittakeinchips+""));

        // 0 拒绝 1同意 2等待通过
        if (applyInfo.ispermit==1){
            holder.mRefuse.setVisibility(View.GONE);
            holder.mApply.setText("已同意");

        }else if (applyInfo.ispermit==0){
            holder.mApply.setVisibility(View.GONE);
            holder.mRefuse.setText("已拒绝");

        }else if (applyInfo.ispermit==2){
            holder.mRefuse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    applyInfo.ispermit=0;
                    mListener.onButtonClick(applyInfo);
                }
            });
            holder.mApply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    applyInfo.ispermit=1;
                    mListener.onButtonClick(applyInfo);
                }
            });

        }else if (applyInfo.ispermit==1){
            holder.mApply.setVisibility(View.GONE);
            holder.mRefuse.setText("已拒绝");

        }
    }


    static class ViewHolder extends RecyclerView.ViewHolder  {

        public View mView;

        public TextView mHouseName,mApplyTime,mPlayer,mApplyinfo;
        public Button mRefuse,mApply;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mHouseName=(TextView) itemView.findViewById(R.id.tv_house_name);
            mApplyTime=(TextView) itemView.findViewById(R.id.tv_apply_time);
            mPlayer=(TextView) itemView.findViewById(R.id.tv_player);
            mApplyinfo=(TextView) itemView.findViewById(R.id.tv_apply_info);
            mRefuse=(Button) itemView.findViewById(R.id.button_refuse);
            mApply=(Button) itemView.findViewById(R.id.button_apply);

        }
    }

}