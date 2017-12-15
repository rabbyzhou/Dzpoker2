package com.yijian.dzpoker.activity.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.yijian.dzpoker.R;
import com.yijian.dzpoker.activity.user.StoreActivity;
import com.yijian.dzpoker.util.DzApplication;
import com.yijian.dzpoker.util.ToastUtil;
import com.yijian.dzpoker.util.Util;
import com.yijian.dzpoker.utils.DialogCreator;
import com.yijian.dzpoker.view.adapter.UserLevelAdapter;
import com.yijian.dzpoker.view.adapter.UserLevelItemDecoration;
import com.yijian.dzpoker.view.data.UserLevel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import okhttp3.Request;
import okhttp3.Response;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnLevelDealDoneListener}
 * interface.
 */
public class UserLevelFragment extends Fragment {

    private final static String TAG = "UserLevelFragment";
    private OnLevelDealDoneListener mListener;

    private RecyclerView mListView;
    private Dialog mConfirmBuyDg;

    private UserLevelAdapter mAdapter;
    private Dialog mBuyingDg;
    private ArrayList<UserLevel> mList=new ArrayList<UserLevel>();
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */

    private final int MESSAGE_BUY_DONE = 0;
    private final int MESSAGE_BUY_FAILED = 1;
    private final int MESSAGE_NO_ENOUGH_DIAMOND = 2;


    private Handler mHander = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case MESSAGE_BUY_DONE:
                    mListener.onLevelDealDone();
                    updateStateAfterDeal();
                    ToastUtil.showToastInScreenCenter(getActivity(),"您已购买成功");
                    break;
                case MESSAGE_BUY_FAILED:
                    updateStateAfterDeal();
                    ToastUtil.showToastInScreenCenter(getActivity(),"购买失败，请稍后重试！");
                    break;
                case MESSAGE_NO_ENOUGH_DIAMOND:
                    updateStateAfterDeal();
                    ToastUtil.showToastInScreenCenter(getActivity(),"钻石不足,不能购买!");
                default:
                    break;
            }
        }
    };

    private void updateStateAfterDeal(){
        //可以再次购买
        mListView.setClickable(true);
        //取消对话框
        if (mBuyingDg.isShowing()) {
            mBuyingDg.cancel();
        }
    }

    public UserLevelFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static UserLevelFragment newInstance() {
        UserLevelFragment fragment = new UserLevelFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_userlevel_deal, container, false);

        Log.d(TAG, "onCreateView");

        mListView = (RecyclerView)layout.findViewById(R.id.list_userlevel);
        mListView.setLayoutManager(new GridLayoutManager(getActivity(),1));
        mListView.setHasFixedSize(true);
        DividerItemDecoration decoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        mListView.addItemDecoration(decoration);

        mAdapter = new UserLevelAdapter(getActivity(), new UserLevelAdapter.OnRecordSelectListener() {
            @Override
            public void onRecordSelected(final UserLevel userLevel) {
                buyLevel(userLevel);
            }
        });

        mListView.setAdapter(mAdapter);
        new QueryDataTask().execute();
        return layout;
    }

    private void buyLevel(final UserLevel userLevel){
        int costDiamonds = userLevel.costDiamonds;

        if ( !isDiamondEnough(costDiamonds)){
            ToastUtil.showToastInScreenCenter(getActivity(),"钻石不足,不能购买!");
        } else if ( isUnderCurrentLevel(userLevel)) {
            ToastUtil.showToastInScreenCenter(getActivity(),"低于目前特权, 无需购买!");
        } else {
                View.OnClickListener listener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (v.getId()) {
                            case R.id.jmui_cancel_btn:
                                mConfirmBuyDg.dismiss();
                                break;
                            case R.id.jmui_commit_btn:
                                mConfirmBuyDg.dismiss();
                                realBuy(userLevel);
                                break;
                        }
                    }
                };
                mConfirmBuyDg = DialogCreator.createBaseDialogWithTitle(getActivity(), "您即将购买" + userLevel.levelName, listener);
                mConfirmBuyDg.getWindow().setLayout((int) (0.8 * Util.getScreenWidth(getActivity())), WindowManager.LayoutParams.WRAP_CONTENT);
                mConfirmBuyDg.setCanceledOnTouchOutside(false);
                mConfirmBuyDg.show();
        }
    }

    private boolean isDiamondEnough(int costDiamond){
        int ownDiamond = ((StoreActivity)getActivity()).mDiamond;
        if ( ownDiamond >= costDiamond) return true;
        else return false;

    }

    private boolean isUnderCurrentLevel(UserLevel level) {
        int currentLevel = ((StoreActivity)getActivity()).application.getUser().levelid;
        if ( currentLevel >= level.level ) return true;
        else return false;
    }

    private void realBuy(final UserLevel userLevel){

        //购买时, 禁止再次购买
        mListView.setClickable(false);

        //显示对话框
        mBuyingDg = DialogCreator.createLoadingDialog(getActivity(),getActivity().getResources().getString(R.string.userlevel_buying));
        mBuyingDg.show();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                try{
                    //购买操作
                    //拼装url字符串
                    JSONObject jsonObj = new JSONObject();
                    jsonObj.put("userid",((StoreActivity)getActivity()).application.getUser().userId);
                    jsonObj.put("userlevelid",userLevel.level);

                    String strURL=getString(R.string.url_remote);
                    strURL+="func=buymyuserlevel&param="+jsonObj.toString();

                    URL url = new URL(strURL);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();// 利用HttpURLConnection对象,我们可以从网络中获取网页数据.
                    conn.setConnectTimeout(5 * 1000);   // 单位是毫秒，设置超时时间为5秒
                    conn.setRequestMethod("GET");       // HttpURLConnection是通过HTTP协议请求path路径的，所以需要设置请求方式,可以不设置，因为默认为GET
                    if (conn.getResponseCode() == 200) {// 判断请求码是否是200码，否则失败
                        InputStream is = conn.getInputStream(); // 获取输入流
                        byte[] data = Util.readStream(is);   // 把输入流转换成字符数组
                        String result = new String(data);        // 把字符数组转换成字符串
                        //这里返回json字符串
                        JSONObject jsonResult = new JSONObject(result);
                        int ret=jsonResult.getInt("ret");
                        if (ret==0) {
                            //购买成功, 需要重新获取用户基本数据, 更新内存缓存.
                            //此操作在 StoreActvity 的回调中统一完成
                            mListener.onLevelDealDone();

                            mHander.sendMessage(mHander.obtainMessage(MESSAGE_BUY_DONE));
                        } else if ( ret == 1) { //没有足够钻石
                            mHander.sendMessage(mHander.obtainMessage(MESSAGE_NO_ENOUGH_DIAMOND));
                        }else{
                            mHander.sendMessage(mHander.obtainMessage(MESSAGE_BUY_FAILED));
                        }
                    }

                }catch (Exception e){
                    mHander.sendMessage(mHander.obtainMessage(MESSAGE_BUY_FAILED));
                }
            }
        });

        thread.start();
    }

    private void getUserLevelData(){

        Log.d(TAG, "getUserLevelData");
        try{
            JSONObject jsonObj = new JSONObject();

            String strURL=getString(R.string.url_remote);
            strURL+="func=getuserlevel&param="+jsonObj.toString();

            Request request = new Request.Builder().url(strURL).build();
            Response response = DzApplication.getHttpClient().newCall(request).execute();

            String result=response.body().string();
            JSONArray jsonNewList=new JSONArray(result);

            Log.d(TAG, "Query successful ? " + response.isSuccessful());
            Log.d(TAG, " ----  " + jsonNewList.toString());

            for(int i=0;i<jsonNewList.length();i++){
                UserLevel level = new UserLevel();
                JSONObject jsonObject=new JSONObject(jsonNewList.get(i).toString());

                level.level=jsonObject.getInt("level");
                level.id = jsonObject.getInt("id");
                level.levelName=jsonObject.getString("levelname");
                level.levelAbstract=jsonObject.getString("levelabstract");
                level.durationDay=jsonObject.getInt("durationdays");
                level.costDiamonds= jsonObject.getInt("costdiamonds");
                level.maxClubs=jsonObject.getInt("maxclubs");
                level.maxGameTables=jsonObject.getInt("maxgametables");
                level.recordHandDays=jsonObject.getInt("recordhanddays");
                level.collectHands=jsonObject.getInt("collecthands");
                level.pic=jsonObject.getString("pic");
                level.giveCoins=jsonObject.getInt("givecoins");

                mList.add(level);
            }
            Log.d(TAG, " ----  " + mList.toString());
        } catch (Exception e){
            Log.d(TAG, "getUserLevelFromServer: " + e.getMessage());
        }
    }

    private class QueryDataTask extends AsyncTask<String, Integer, String> {

        private Dialog dialog;
        @Override
        protected void onPreExecute() {
            Log.d(TAG, "onPreExecute");
            dialog = DialogCreator.createLoadingDialog(getActivity(),getActivity().getResources().getString(R.string.jmui_loading));
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            Log.d(TAG, "doInBackground");
            getUserLevelData();
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            Log.d(TAG, "onPostExecute");
            if ( dialog.isShowing()) dialog.cancel();

            mAdapter.setData(mList);
            super.onPostExecute(s);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnLevelDealDoneListener) {
            mListener = (OnLevelDealDoneListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnLevelDealDoneListener {
        // TODO: Update argument type and name
        void onLevelDealDone();
    }
}
