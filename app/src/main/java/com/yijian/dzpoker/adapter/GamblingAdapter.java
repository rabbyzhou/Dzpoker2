package com.yijian.dzpoker.adapter;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yijian.dzpoker.R;
import com.yijian.dzpoker.activity.game.GameActivity;
import com.yijian.dzpoker.baselib.http.RetrofitApiGenerator;
import com.yijian.dzpoker.entity.MyGamesBean;
import com.yijian.dzpoker.http.getgametype.GetGameTypeApi;
import com.yijian.dzpoker.http.getgametype.GetGameTypeCons;
import com.yijian.dzpoker.util.ToastUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    private List<MyGamesBean> datas = new ArrayList<MyGamesBean>();

    public GamblingAdapter(Context context, List<MyGamesBean> d) {
        this.context = context;
        if (null != d) {
            this.datas.addAll(d);
        }
    }

    public void updateUI(List<MyGamesBean> data) {
        if (null != datas && null != data) {
            datas.addAll(data);
            notifyDataSetChanged();
        }
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

    private void setGamblingInfo(ExistTypeViewHolder holder, final int position) {
//        holder.type.setText(data.get(position).get(KEY_TYPE));

        ((ExistTypeViewHolder) holder).title.setText(datas.get(position).getName());
        ((ExistTypeViewHolder) holder).type.setText(datas.get(position).getType() == 0 ? "Texas" : " ");
        ((ExistTypeViewHolder) holder).people.setText(datas.get(position).getCurplayer()+ "" + "/" + "" +datas.get(position).getMaxplayers());
        ((ExistTypeViewHolder) holder).time.setText(String.valueOf(datas.get(position).getDuration()) + "分钟");
        ((ExistTypeViewHolder) holder).cards.setText(datas.get(position).getMintakeinchips()+ "" + "/" + "" +datas.get(position).getMaxtakeinchips());
        ((ExistTypeViewHolder) holder).rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    GetGameTypeApi getClubInfoApi = RetrofitApiGenerator.createRequestApi(GetGameTypeApi.class);
                    JSONObject param = new JSONObject();
                    param.put(GetGameTypeCons.PARAM_KEY_SHARE_CODE, datas.get(position).getSharecode());

                    Call<ResponseBody> getGameTypeCall = getClubInfoApi.getResponse(GetGameTypeCons.FUNC_NAME, param.toString());
                    getGameTypeCall.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                    Logger.i(TAG, "getGameTypeCall response : " + response.body().toString());
                            try {
                                JSONObject jsonObject = new JSONObject(response.body().string());
                                int gameType = jsonObject.optInt("gametype");
                                int matchType = jsonObject.optInt("matchtype");
                                int gameId = jsonObject.optInt("gameid");
                                String ip = jsonObject.optString("ip");
                                int port = jsonObject.optInt("port");
                                if (matchType == -1) {
                                    Intent intent = new Intent();
                                    intent.putExtra("operation", 2);//1表示创建牌局，2表示加入牌局
                                    intent.putExtra("gameid", gameId);
                                    intent.putExtra("ip", ip);
                                    intent.putExtra("port", port);
                                    intent.setClass(context, GameActivity.class);
                                    context.startActivity(intent);
                                } else {
                                    ToastUtil.showToastInScreenCenter((Activity) context, "当前不支持比赛");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {

                        }
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
//                    Intent intent = new Intent();
//                    intent.setClass(getContext(), GameActivity.class);
//                    intent.putExtra("operation", 2);//1表示创建牌局，2表示加入牌局
//                    intent.putExtra("gameid", datas.get(position).g);
//                    intent.putExtra("ip", ip);
//                    intent.putExtra("port", port);
//                    startActivity(intent);
//
//                    Intent intent = new Intent();
//                    intent.putExtra("operation", 2);//1表示创建牌局，2表示加入牌局
//                    intent.putExtra("gameid", gameId);
//                    intent.putExtra("ip", ip);
//                    intent.putExtra("port", port);
//                    intent.setClass(GameAddActivity.this, GameActivity.class);
//                    startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return null != datas ? datas.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        return 0 == position ? ITEM_TYPE.ITEM_TYPE_CREATE.ordinal() : ITEM_TYPE.ITEM_TYPE_EXIST.ordinal();
    }

    static class ExistTypeViewHolder extends RecyclerView.ViewHolder {

        LinearLayout rootView;
        TextView type;
        TextView title;
        TextView people;
        TextView cards;
        TextView time;

        public ExistTypeViewHolder(View itemView) {
            super(itemView);
            rootView = itemView.findViewById(R.id.gambling_list_item_root_view);
            type = (TextView) itemView.findViewById(R.id.gambling_list_item_type);
            title = (TextView) itemView.findViewById(R.id.gambling_list_item_title);
            people = (TextView) itemView.findViewById(R.id.gambling_list_item_people);
            cards = (TextView) itemView.findViewById(R.id.gambling_list_item_cards);
            time = (TextView) itemView.findViewById(R.id.gambling_list_item_time);
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
