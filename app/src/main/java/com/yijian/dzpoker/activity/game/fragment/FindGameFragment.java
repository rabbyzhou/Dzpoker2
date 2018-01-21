package com.yijian.dzpoker.activity.game.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.yijian.dzpoker.R;
import com.yijian.dzpoker.activity.game.GameActivity;
import com.yijian.dzpoker.activity.game.GameAddActivity;
import com.yijian.dzpoker.baselib.debug.Logger;
import com.yijian.dzpoker.baselib.http.RetrofitApiGenerator;
import com.yijian.dzpoker.entity.MyGamesBean;
import com.yijian.dzpoker.http.getgametype.GetGameTypeApi;
import com.yijian.dzpoker.http.getgametype.GetGameTypeCons;
import com.yijian.dzpoker.http.getmygame.GetMyGameTableApi;
import com.yijian.dzpoker.http.getmygame.GetMyGameTableCons;
import com.yijian.dzpoker.http.getmygame.GetMyMatchApi;
import com.yijian.dzpoker.http.getmygame.GetMyMatchCons;
import com.yijian.dzpoker.util.DzApplication;
import com.yijian.dzpoker.util.ToastUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FindGameFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FindGameFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String TAG = "FindGameFragment";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private RecyclerView recyclerView;

    private Handler mainHandler;

    private GameAndMatchesAdapter adapter;

    private List<MyGamesBean> games = new ArrayList<MyGamesBean>();

    public FindGameFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FindGameFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FindGameFragment newInstance(String param1, String param2) {
        FindGameFragment fragment = new FindGameFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
//        "getmymatch";
//        "getmygametable";——》entertable
    }

    public void getGames(Activity activity) {
        getMyMatches(activity);
        getMyGameTables(activity);
    }


    private void getMyMatches(Activity activity) {
        DzApplication application = (DzApplication) activity.getApplication();
        try {
            GetMyMatchApi getMyMatchApi = RetrofitApiGenerator.createRequestApi(GetMyMatchApi.class);
            JSONObject param = new JSONObject();
            param.put(GetMyMatchCons.PARAM_KEY_USERID, application.getUserId());

            Call<ResponseBody> callForMyMatches = getMyMatchApi.getResponse(GetMyMatchCons.FUNC_NAME, param.toString());
            callForMyMatches.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                    List<?> list2 = JSONArray.toList(array, new Person(), new JsonConfig());

//                    List<ResponseBody> datas = new ArrayList<ResponseBody>();
//
//                    GetClubApplyBean applyBean = response.body();
////                        Logger.i(TAG, "applyBean =" + response.body());
//                    for (GetClubApplyBean.SingleRequestInfo info : applyBean.getRequestinfo()) {
//                        ClubManagerBean bean = new ClubManagerBean();
//                        bean.setMainMsg(info.getNickname() + " 申请加入俱乐部-" + info.getClubname());
//                        bean.setDetailMsg(info.getRequestmsg());
//                        bean.setUserId(info.getUserid());
//                        bean.setClubId(info.getClubid());
//                        bean.setRequestId(info.getRequestid());
//                        datas.add(bean);
//                    }

//                    Logger.i(TAG, "callForMyMatches response : " + response.body().toString());

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void getMyGameTables(Activity activity) {
        DzApplication application = (DzApplication) activity.getApplication();
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

    @Override
    public void onResume() {
        super.onResume();
        getMyMatches(getActivity());
        getMyGameTables(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_find_game, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.game_and_matched_recyclerview);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        DividerItemDecoration decoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        decoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.store_user_level_list_divide_drawable));
        recyclerView.addItemDecoration(decoration);
        adapter = new GameAndMatchesAdapter(games);

        recyclerView.setAdapter(adapter);

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
        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private class GameAndMatchesAdapter extends RecyclerView.Adapter {

        private List<MyGamesBean> datas = new ArrayList<MyGamesBean>();

        public GameAndMatchesAdapter(List<MyGamesBean> data) {
            if (null != data) {
                datas.addAll(data);
            }
        }

        public void updateUI(List<MyGamesBean> data) {
            if (null != datas && null != data) {
                datas.addAll(data);
                notifyDataSetChanged();
            }
        }

        private static final int TYPE_GAME = 0;
        private static final int TYPE_MATCHS = 1;

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.find_game_my_game, parent, false);
            GamesViewHolder gamesViewHolder = new GamesViewHolder(view);

            return gamesViewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            ((GamesViewHolder) holder).tableTimeBig.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            ((GamesViewHolder) holder).tableName.setText(datas.get(position).getName());
            ((GamesViewHolder) holder).tableType.setText(datas.get(position).getType() == 0 ? "Texas" : " ");
            ((GamesViewHolder) holder).tablePeoples.setText(datas.get(position).getCurplayer()+ "" + "/" + "" +datas.get(position).getMaxplayers());
            ((GamesViewHolder) holder).tableTime.setText(String.valueOf(datas.get(position).getDuration()) + "分钟");
            ((GamesViewHolder) holder).tableBlinds.setText(datas.get(position).getMintakeinchips()+ "" + "/" + "" +datas.get(position).getMaxtakeinchips());
            ((GamesViewHolder) holder).tableTimeBig.setText(String.valueOf(datas.get(position).getDuration()) + "分钟");
            ((GamesViewHolder) holder).isPartcipated.setText(datas.get(position).getIsjoin() == 1 ? "参与中" : "");
            ((GamesViewHolder) holder).rootView.setOnClickListener(new View.OnClickListener() {
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
                                        intent.setClass(getContext(), GameActivity.class);
                                        startActivity(intent);
                                    } else {
                                        ToastUtil.showToastInScreenCenter(getActivity(), "当前不支持比赛");
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
            return datas.size();
        }

        @Override
        public int getItemViewType(int position) {

            return super.getItemViewType(position);
        }

        private class GamesViewHolder extends RecyclerView.ViewHolder {

            private RelativeLayout rootView;
            private ImageView tableIcom;
            private TextView tableName;
            private TextView tableType;
            private TextView tablePeoples;
            private TextView tableTime;
            private TextView tableBlinds;
            private TextView tableTimeBig;
            private TextView isPartcipated;

            public GamesViewHolder(View itemView) {
                super(itemView);
                rootView = itemView.findViewById(R.id.find_game_item_root_layout);
                tableIcom = itemView.findViewById(R.id.game_icon);
                tableName = itemView.findViewById(R.id.table_name);
                tableType = itemView.findViewById(R.id.table_type);
                tablePeoples = itemView.findViewById(R.id.table_members);
                tableTime = itemView.findViewById(R.id.table_time);
                tableTimeBig = itemView.findViewById(R.id.table_time_big);
                tableBlinds = itemView.findViewById(R.id.table_cards);
                isPartcipated = itemView.findViewById(R.id.is_participated);
            }
        }

        private class MatchesViewHolder extends RecyclerView.ViewHolder {

            public MatchesViewHolder(View itemView) {
                super(itemView);
            }
        }
    }
}
