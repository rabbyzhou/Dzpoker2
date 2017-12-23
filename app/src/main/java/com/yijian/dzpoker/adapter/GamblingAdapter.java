package com.yijian.dzpoker.adapter;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yijian.dzpoker.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by QIPU on 2017/12/24.
 */
public class GamblingAdapter extends RecyclerView.Adapter {

    private static final String KEY_TYPE = "type";
    private static final String KEY_TITLE = "title";
    private static final String KEY_PEOPLE= "people";
    private static final String KEY_TIME = "time";
    private static final String KEY_CARDS = "cards";

    private Context context;

    private List<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();

    public GamblingAdapter(Context context, List<HashMap<String, String>> d) {
        this.context = context;
        if (null != d) {
            this.data.addAll(d);
        }
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(KEY_TYPE, "11");
        data.add(map);
        HashMap<String, String> map2 = new HashMap<String, String>();
        map2.put(KEY_TYPE, "11");
        data.add(map2);
        HashMap<String, String> map3 = new HashMap<String, String>();
        map3.put(KEY_TYPE, "11");
        data.add(map3);
        HashMap<String, String> map4 = new HashMap<String, String>();
        map4.put(KEY_TYPE, "11");
        data.add(map4);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (ITEM_TYPE.ITEM_TYPE_CREATE.ordinal() == viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.gambing_list_item_create_gambling, parent, false);
            return new CreateTypeViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.gambling_list_item, parent, false);
            return new ExistTypeViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CreateTypeViewHolder) {
            ((CreateTypeViewHolder) holder).createGambling.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gotoCreateGamblingPage();
                }
            });
        } else if (holder instanceof ExistTypeViewHolder) {
            setGamblingInfo((ExistTypeViewHolder) holder, position);
        }
    }

    private void gotoCreateGamblingPage() {
        Intent intent = new Intent();
        intent.setClass(context, com.yijian.dzpoker.activity.game.GameSetActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(intent);
    }

    private void setGamblingInfo(ExistTypeViewHolder holder, int position) {
//        holder.type.setText(data.get(position).get(KEY_TYPE));
    }

    @Override
    public int getItemCount() {
        return null != data ? data.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        return 0 == position ? ITEM_TYPE.ITEM_TYPE_CREATE.ordinal() : ITEM_TYPE.ITEM_TYPE_EXIST.ordinal();
    }

    static class ExistTypeViewHolder extends RecyclerView.ViewHolder {

        TextView type;
        TextView title;
        TextView people;
        TextView cards;

        public ExistTypeViewHolder(View itemView) {
            super(itemView);
            type = (TextView) itemView.findViewById(R.id.gambling_list_item_type);
            title = (TextView) itemView.findViewById(R.id.gambling_list_item_title);
            people = (TextView) itemView.findViewById(R.id.gambling_list_item_people);
            cards = (TextView) itemView.findViewById(R.id.gambling_list_item_cards);
        }
    }

    static class CreateTypeViewHolder extends RecyclerView.ViewHolder {

        LinearLayout createGambling;

        public CreateTypeViewHolder(View itemView) {
            super(itemView);
            createGambling = (LinearLayout) itemView.findViewById(R.id.create_gambling);
        }
    }

    public enum ITEM_TYPE {
        ITEM_TYPE_CREATE,
        ITEM_TYPE_EXIST
    }
}
