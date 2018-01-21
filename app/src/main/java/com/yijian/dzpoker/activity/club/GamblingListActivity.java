package com.yijian.dzpoker.activity.club;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yijian.dzpoker.R;
import com.yijian.dzpoker.activity.base.BaseBackActivity;
import com.yijian.dzpoker.activity.game.GameActivity;
import com.yijian.dzpoker.activity.game.fragment.FindGameFragment;
import com.yijian.dzpoker.adapter.GamblingAdapter;
import com.yijian.dzpoker.baselib.debug.Logger;
import com.yijian.dzpoker.baselib.http.RetrofitApiGenerator;
import com.yijian.dzpoker.entity.MyGamesBean;
import com.yijian.dzpoker.http.getclubgames.GetClubGamesApi;
import com.yijian.dzpoker.http.getclubgames.GetClubGamesCons;
import com.yijian.dzpoker.http.getgametype.GetGameTypeApi;
import com.yijian.dzpoker.http.getgametype.GetGameTypeCons;
import com.yijian.dzpoker.http.getmygame.GetMyGameTableApi;
import com.yijian.dzpoker.http.getmygame.GetMyGameTableCons;
import com.yijian.dzpoker.util.DzApplication;
import com.yijian.dzpoker.util.ToastUtil;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

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

    private List<MyGamesBean> games = new ArrayList<MyGamesBean>();

    private Handler mainHandler;

    private GamblingAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setToolbarTitle("牌局");
        getClubId(getIntent());
        getClubGames();
        getMyGameTables();

        mainHandler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                switch (message.what) {
                    case 0:
                        adapter.updateUI(games);
                        break;
                    default:
                        break;
                }
            }
        };
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
        adapter = new GamblingAdapter(this, games);
        gamblingRecyclerView.setAdapter(adapter);
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

    private void getMyGameTables() {
        DzApplication application = (DzApplication) getApplication();
        try {
            GetMyGameTableApi getMyGameTableApi = RetrofitApiGenerator.createRequestApi(GetMyGameTableApi.class);
            JSONObject param = new JSONObject();
            param.put(GetMyGameTableCons.PARAM_KEY_USERID, application.getUserId());

            Call<ResponseBody> callForMyGameTables = getMyGameTableApi.getResponse(GetMyGameTableCons.FUNC_NAME, param.toString());
            callForMyGameTables.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        Gson gson = new Gson();
                        Type type = new TypeToken<List<MyGamesBean>>(){}.getType();
                        List<MyGamesBean> datas = gson.fromJson(response.body().string(), type);
                        if (null != datas) {
                            games.addAll(datas);
//                            Message message = new Message();
//                            message.what = 0;
                            mainHandler.sendEmptyMessage(0);
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
//                    Logger.i(TAG, "callForMyGameTables response : " + response.body().toString());

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
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
