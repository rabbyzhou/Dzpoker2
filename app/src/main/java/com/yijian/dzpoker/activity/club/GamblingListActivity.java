package com.yijian.dzpoker.activity.club;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.yijian.dzpoker.R;
import com.yijian.dzpoker.activity.base.BaseBackActivity;
import com.yijian.dzpoker.adapter.GamblingAdapter;
import com.yijian.dzpoker.baselib.debug.Logger;
import com.yijian.dzpoker.baselib.http.RetrofitApiGenerator;
import com.yijian.dzpoker.http.getclubgames.GetClubGamesApi;
import com.yijian.dzpoker.http.getclubgames.GetClubGamesCons;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by QIPU on 2017/12/24.
 */
public class GamblingListActivity extends BaseBackActivity {

    private static final String TAG = "GamblingListActivity";
    private RecyclerView gamblingRecyclerView;
    private int clubId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setToolbarTitle("牌局");
        getClubId(getIntent());
        getClubGames();
    }

    private void getClubId(Intent intent) {
        if (null != intent) {
            clubId = intent.getIntExtra("club_id", -1);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.gambling_list;
    }

    @Override
    protected void initViews() {
        gamblingRecyclerView = (RecyclerView) findViewById(R.id.gambling_list);
        gamblingRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        gamblingRecyclerView.addItemDecoration(new SpaceItemDecoration(8));
        gamblingRecyclerView.setAdapter(new GamblingAdapter(this, null));
    }

    @Override
    public void onClick(View v) {

    }

    private void getClubGames() {
        GetClubGamesApi getClubGamesApi = RetrofitApiGenerator.createRequestApi(GetClubGamesApi.class);
        try {
            JSONObject params = new JSONObject();
            params.put(GetClubGamesCons.PARAM_KEY_USERID, application.getUserId());
            params.put(GetClubGamesCons.PARAM_KEY_CLUB_ID, clubId);
            Call<ResponseBody> call = getClubGamesApi.getResponse(
                    GetClubGamesCons.FUNC_NAME, params.toString());
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Logger.i(TAG, "onResponse : " + response.body());
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Logger.i(TAG, "onFailure : " + t.getMessage());
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class SpaceItemDecoration extends RecyclerView.ItemDecoration {

        private int space;

        public SpaceItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = space;

            if (0 == parent.getChildLayoutPosition(view)) {
                outRect.top = space;
            } else {
                outRect.top = 0;
            }
        }
    }
}
