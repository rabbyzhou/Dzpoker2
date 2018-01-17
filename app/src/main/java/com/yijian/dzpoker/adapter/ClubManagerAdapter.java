package com.yijian.dzpoker.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yijian.dzpoker.R;
import com.yijian.dzpoker.baselib.debug.Logger;
import com.yijian.dzpoker.baselib.http.RetrofitApiGenerator;
import com.yijian.dzpoker.entity.ClubManagerBean;
import com.yijian.dzpoker.http.clubapplyres.ClubApplyResponseApi;
import com.yijian.dzpoker.http.clubapplyres.ClubApplyResponseCons;
import com.yijian.dzpoker.http.getclubinfo.GetClubInfoApi;
import com.yijian.dzpoker.http.getclubinfo.GetClubInfoCons;
import com.yijian.dzpoker.util.DzApplication;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import cn.jpush.im.android.api.JMessageClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by qipu.qp on 2017/12/27.
 */

public class ClubManagerAdapter extends RecyclerView.Adapter {

    private static final String TAG = "ClubManagerAdapter";

    private List<ClubManagerBean> data = new ArrayList<ClubManagerBean>();

    private Activity activity;

    public ClubManagerAdapter(List<ClubManagerBean> data, Activity a) {
        this.data = data;
        this.activity = a;
    }

    public void updateData(ArrayList<ClubManagerBean> list) {
        if (null != list) {
            if (null != data) {
                data.addAll(list);
                notifyDataSetChanged();
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.club_manager_page_item, parent, false);
        return new ClubManagerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (null == data) {
            return;
        }
        ((ClubManagerViewHolder) holder).mainMsg.setText(data.get(position).getMainMsg());
        ((ClubManagerViewHolder) holder).detailMsg.setText(data.get(position).getDetailMsg());
        ((ClubManagerViewHolder) holder).rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleApply(data.get(position), false);
            }
        });
        ((ClubManagerViewHolder) holder).agreeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleApply(data.get(position), true);
            }
        });
    }

    @Override
    public int getItemCount() {
        return null != data ? data.size() : 0;
    }

    private void handleApply(final ClubManagerBean bean, boolean reject) {
        ClubApplyResponseApi clubApplyResponseApi = RetrofitApiGenerator.createRequestApi(ClubApplyResponseApi.class);

        try {
            final JSONObject params = new JSONObject();
            params.put(ClubApplyResponseCons.PARAM_KEY_USERID, bean.getUserId());
            params.put(ClubApplyResponseCons.PARAM_KEY_REQUESTID, bean.getRequestId());
            params.put(ClubApplyResponseCons.PARAM_KEY_PERMIT, reject);

            Call<ResponseBody> call = clubApplyResponseApi.getResponse(ClubApplyResponseCons.FUNC_NAME, params.toString());
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Logger.d(TAG, "onResponse : " + response.body());
//                    同意之后调用获取clubinfo的接口，获取groupid，然后将用户加入到im group
                    putUserIntoClubIMGroup(bean);

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });


        } catch (Exception e) {
            e.printStackTrace();
            Logger.e(TAG, "exception e : " + e);
        }

    }

    private void putUserIntoClubIMGroup(final ClubManagerBean bean) {
        DzApplication application = (DzApplication) activity.getApplication();
        try {
            GetClubInfoApi getClubInfoApi = RetrofitApiGenerator.createRequestApi(GetClubInfoApi.class);
            JSONObject param = new JSONObject();
            param.put(GetClubInfoCons.PARAM_KEY_USERID, application.getUserId());

            Call<ResponseBody> callForClubInfo = getClubInfoApi.getResponse(GetClubInfoCons.FUNC_NAME, param.toString());
            callForClubInfo.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                    Logger.i(TAG, "callForClubInfo response : " + response.body().toString());
                    try {
                        JSONArray jsonArray = new JSONArray(response.body().string());
                        if (null != jsonArray && jsonArray.length() > 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                                if (null != jsonObject) {
                                    if (jsonObject.optInt("clubid") == bean.getClubId()) {
                                        int groupId = jsonObject.optInt("imgroupid");

                                        List<String> userList = new ArrayList<String>();
                                        userList.add(String.valueOf(bean.getUserId()));
                                        JMessageClient.addGroupMembers(groupId, userList, null);
                                    }
                                }
                            }
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
    }

    static class ClubManagerViewHolder extends RecyclerView.ViewHolder {

        public TextView mainMsg;
        public TextView detailMsg;
        public TextView rejectButton;
        public TextView agreeButton;

        public ClubManagerViewHolder(View itemView) {
            super(itemView);
            mainMsg = (TextView) itemView.findViewById(R.id.club_manager_item_main_msg);
            detailMsg = (TextView) itemView.findViewById(R.id.club_manager_item_detail_msg);
            rejectButton = (TextView) itemView.findViewById(R.id.club_manager_item_reject);
            agreeButton = (TextView) itemView.findViewById(R.id.club_manager_item_agree);
        }
    }
}
