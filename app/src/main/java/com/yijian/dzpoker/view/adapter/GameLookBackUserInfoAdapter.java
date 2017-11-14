package com.yijian.dzpoker.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
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
import com.yijian.dzpoker.activity.user.GameRecordLookBack;
import com.yijian.dzpoker.util.CardsUtil;
import com.yijian.dzpoker.util.DzApplication;
import com.yijian.dzpoker.view.CircleTransform;
import com.yijian.dzpoker.view.data.CardInfo;
import com.yijian.dzpoker.view.data.RoundUserInfo;

/**
 * Created by c_huangl on 0008, 11/08/2017.
 */

public class GameLookBackUserInfoAdapter extends BaseListAdapter<RoundUserInfo, GameLookBackUserInfoAdapter.ViewHolder> {

    private Context mContext;
    private CardInfo[] mCardInfo;

    public GameLookBackUserInfoAdapter(Context context) {
        super(context);
        mContext = context;
    }

    public GameLookBackUserInfoAdapter(Context context, CardInfo[] card) {
        this(context);
        mCardInfo = card;
    }

    @Override
    public GameLookBackUserInfoAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_lookback, parent, false);

        return new GameLookBackUserInfoAdapter.ViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(GameLookBackUserInfoAdapter.ViewHolder holder, int position) {
        final RoundUserInfo data = mData.get(position);

        if (data == null) return;

        Activity activity = ((GameRecordLookBack)mContext);
        int useId = ((DzApplication)activity.getApplication()).getUserId();

        holder.nickName.setText(data.nickname);
        holder.lastaction.setText( "(" + data.lastaction + ")");
        holder.wincard.setText(data.wincard + "");
        holder.winchips.setText(data.winchips + "");

        if (data.headpic != null && !data.headpic.isEmpty()){
            Picasso.with(mContext)
                    .load(data.headpic)
                    .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    //.resize(100, 100)
                    .transform(new CircleTransform())
                    .into(holder.headpic);
        }

        //登录者可以看到牌桌的手牌
        if (data.userid == useId || data.isshowcard == true){
            //牌桌的手牌
            holder.table_card_1.setVisibility(View.GONE);
            holder.table_card_2.setVisibility(View.GONE);
            holder.table_card_3.setVisibility(View.GONE);
            holder.table_card_4.setVisibility(View.GONE);
            holder.table_card_5.setVisibility(View.GONE);

            for (int i = 0; i< mCardInfo.length; i++) {
                CardInfo info = mCardInfo[i];

                if (info == null) break;
                if (info.suit < 0 || info.suit > 3) break;
                if (info.member < 2 || info.member > 14 ) break;

                Bitmap bitmap = CardsUtil.drawSingleCard(mContext, info.suit, info.member);

                switch (i+1){
                    case 1:
                        holder.table_card_1.setImageBitmap(bitmap);
                        holder.table_card_1.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        holder.table_card_2.setImageBitmap(bitmap);
                        holder.table_card_2.setVisibility(View.VISIBLE);
                        break;
                    case 3:
                        holder.table_card_3.setImageBitmap(bitmap);
                        holder.table_card_3.setVisibility(View.VISIBLE);
                        break;
                    case 4:
                        holder.table_card_4.setImageBitmap(bitmap);
                        holder.table_card_4.setVisibility(View.VISIBLE);
                        break;
                    case 5:
                        holder.table_card_5.setImageBitmap(bitmap);
                        holder.table_card_5.setVisibility(View.VISIBLE);
                        break;
                }
            }
        }

        //游戏者的手牌
        holder.self_card_1.setVisibility(View.GONE);
        holder.self_card_2.setVisibility(View.GONE);
        holder.self_card_3.setVisibility(View.GONE);
        holder.self_card_4.setVisibility(View.GONE);
        holder.self_card_5.setVisibility(View.GONE);

        CardInfo[] cardInfo = data.card;
        for (int i = 0; i< cardInfo.length; i++) {
            CardInfo info = cardInfo[i];

            if (info == null) break;
            if (info.suit < 0 || info.suit > 3) break;
            if (info.member < 2 || info.member > 14 ) break;

            switch (i+1){
                case 1:
                    holder.self_card_1.setVisibility(View.VISIBLE);
                    if( data.userid == useId || data.isshowcard == true){
                        Bitmap bitmap = CardsUtil.drawSingleCard(mContext, info.suit, info.member);
                        holder.self_card_1.setImageBitmap(bitmap);
                    }
                    break;
                case 2:
                    holder.self_card_2.setVisibility(View.VISIBLE);
                    if(data.userid == useId || data.isshowcard == true){
                        Bitmap bitmap = CardsUtil.drawSingleCard(mContext, info.suit, info.member);
                        holder.self_card_2.setImageBitmap(bitmap);
                    }
                    break;
                case 3:
                    holder.self_card_3.setVisibility(View.VISIBLE);
                    if(data.userid == useId || data.isshowcard == true){
                        Bitmap bitmap = CardsUtil.drawSingleCard(mContext, info.suit, info.member);
                        holder.self_card_3.setImageBitmap(bitmap);
                    }
                    break;
                case 4:
                    holder.self_card_4.setVisibility(View.VISIBLE);
                    if(data.userid == useId || data.isshowcard == true){
                        Bitmap bitmap = CardsUtil.drawSingleCard(mContext, info.suit, info.member);
                        holder.self_card_4.setImageBitmap(bitmap);
                    }
                    break;
                case 5:
                    holder.self_card_5.setVisibility(View.VISIBLE);
                    if(data.userid == useId || data.isshowcard == true){
                        Bitmap bitmap = CardsUtil.drawSingleCard(mContext, info.suit, info.member);
                        holder.self_card_5.setImageBitmap(bitmap);
                    }
                    break;
            }
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder  {

        public TextView nickName;
        public TextView lastaction;
        public TextView wincard;
        public TextView winchips;

        public ImageView headpic;

        public ImageView self_card_1;
        public ImageView self_card_2;
        public ImageView self_card_3;
        public ImageView self_card_4;
        public ImageView self_card_5;

        public ImageView table_card_1;
        public ImageView table_card_2;
        public ImageView table_card_3;
        public ImageView table_card_4;
        public ImageView table_card_5;

        public View mItemView;

        public ViewHolder(View itemView) {
            super(itemView);
            mItemView = itemView;
            nickName=(TextView)itemView.findViewById(R.id.nickName);
            lastaction=(TextView) itemView.findViewById(R.id.lastaction);
            winchips=(TextView) itemView.findViewById(R.id.winchips);
            wincard=(TextView) itemView.findViewById(R.id.wincard);

            headpic=(ImageView) itemView.findViewById(R.id.headpic);

            self_card_1=(ImageView) itemView.findViewById(R.id.self_card_1);
            self_card_2=(ImageView) itemView.findViewById(R.id.self_card_2);
            self_card_3=(ImageView) itemView.findViewById(R.id.self_card_3);
            self_card_4=(ImageView) itemView.findViewById(R.id.self_card_4);
            self_card_5=(ImageView) itemView.findViewById(R.id.self_card_5);

            table_card_1=(ImageView) itemView.findViewById(R.id.table_card_1);
            table_card_2=(ImageView) itemView.findViewById(R.id.table_card_2);
            table_card_3=(ImageView) itemView.findViewById(R.id.table_card_3);
            table_card_4=(ImageView) itemView.findViewById(R.id.table_card_4);
            table_card_5=(ImageView) itemView.findViewById(R.id.table_card_5);

        }
    }
}
