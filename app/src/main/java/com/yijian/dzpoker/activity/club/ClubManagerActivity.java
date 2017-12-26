package com.yijian.dzpoker.activity.club;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.yijian.dzpoker.R;
import com.yijian.dzpoker.activity.base.BaseBackActivity;
import com.yijian.dzpoker.adapter.ClubManagerAdapter;
import com.yijian.dzpoker.baselib.debug.Logger;
import com.yijian.dzpoker.baselib.http.RetrofitApiGenerator;
import com.yijian.dzpoker.baselib.widget.DzProgressBar;
import com.yijian.dzpoker.entity.ClubManagerBean;
import com.yijian.dzpoker.http.getclubapply.GetClubApplyApi;
import com.yijian.dzpoker.http.getclubapply.GetClubApplyBean;
import com.yijian.dzpoker.http.getclubapply.GetClubApplyRequestInfo;
import com.yijian.dzpoker.util.DzApplication;
import com.yijian.dzpoker.util.ToastUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by qipu.qp on 2017/12/27.
 */

public class ClubManagerActivity extends BaseBackActivity {

    private static final String TAG = "ClubManagerActivity";

    private RecyclerView clubInfoCyclerView;
    private List<ClubManagerBean> datas = new ArrayList<ClubManagerBean>();
    private DzProgressBar progressBar;
    private ClubManagerAdapter adapter;

    private Handler mainHandler;

    private static final int UPDATE_UI = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        mainHandler = new Handler() {

            @Override
            public void handleMessage(Message message) {
                int event = message.what;
                switch (event) {
                    case UPDATE_UI:
                        adapter.updateData(datas);
                        progressBar.setVisibility(View.GONE);
                        break;
                    default:
                        break;
                }
            }
        };
        setToolbarTitle("俱乐部管理");

    }

    @Override
    public void initViews() {
        clubInfoCyclerView = (RecyclerView) findViewById(R.id.club_manager_page_recycler_view);
        adapter = new ClubManagerAdapter(datas);
        clubInfoCyclerView.setLayoutManager(new LinearLayoutManager(this));
        clubInfoCyclerView.setHasFixedSize(true);
        DividerItemDecoration decoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        decoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.store_user_level_list_divide_drawable));
        clubInfoCyclerView.addItemDecoration(decoration);
        clubInfoCyclerView.setAdapter(adapter);

        progressBar = (DzProgressBar) findViewById(R.id.club_manager_page_progressbar);
        progressBar.setVisibility(View.VISIBLE);
    }


    private void initData() {
        sendRequest();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.club_manager_page;
    }

    @Override
    public void onClick(View view) {

    }
    private void sendRequest() {
        GetClubApplyApi getClubApplyApi = RetrofitApiGenerator.createRequestApi(GetClubApplyApi.class);

        DzApplication application = (DzApplication) getApplication();
        if (null == application) {
            return;
        }
        try {
            JSONObject params = new JSONObject();
            params.put(GetClubApplyRequestInfo.PARAM_KEY_USER_ID, application.getUserId());
            Logger.i(TAG, "response=" + params.toString());
            Call<ResponseBody> call = getClubApplyApi.getResponse(GetClubApplyRequestInfo.FUNC_NAME, params.toString());

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        ResponseBody applyBean = response.body();
//                        Logger.i(TAG, "responseid =" + response.body().getRequestid());
//                        ClubManagerBean bean = new ClubManagerBean();
//                        bean.setMainMsg(applyBean.getNickname() + " 申请加入俱乐部-" + applyBean.getClubname());
//                        bean.setDetailMsg(applyBean.getRequestmsg());
//                        datas.add(bean);
//                        mainHandler.sendEmptyMessage(UPDATE_UI);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    ToastUtil.showToastInScreenCenter(ClubManagerActivity.this, "发送请求失败，请尝试重新添加！");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
