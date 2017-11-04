package com.yijian.dzpoker.activity.user;

import android.app.Application;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.yijian.dzpoker.R;
import com.yijian.dzpoker.activity.base.BaseBackActivity;
import com.yijian.dzpoker.activity.club.ClubMemberManageActivity;
import com.yijian.dzpoker.util.DzApplication;
import com.yijian.dzpoker.util.ToastUtil;
import com.yijian.dzpoker.view.OnItemClickListener;
import com.yijian.dzpoker.view.adapter.DiamonsListAdapter;
import com.yijian.dzpoker.view.adapter.SelectCityAdapter;
import com.yijian.dzpoker.view.data.City;
import com.yijian.dzpoker.view.data.DiamondStoreGoods;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;
import okhttp3.Response;

public class BuyDiamondActivity extends BaseBackActivity {

    private RecyclerView rv_diamond_list;
    private DiamonsListAdapter mAdapter;
    private List<DiamondStoreGoods> mDiamondStoreGoodsList=new ArrayList<DiamondStoreGoods>();



    private final int MESAGE_GET_GOODS_OK=0x1001;
    private final int MESAGE_SEND_BUY_OK=0x1002;


    private Handler handler = new Handler() {

        // 该方法运行在主线程中
        // 接收到handler发送的消息，对UI进行操作
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what){
                case MESAGE_GET_GOODS_OK:
                    mAdapter.setData(mDiamondStoreGoodsList);
                    break;
                case MESAGE_SEND_BUY_OK:
                    //给用户加上钻石
                    application.getUser().diamond+=msg.arg1;
                    break;
            }


        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new DiamonsListAdapter(this, new DiamonsListAdapter.OnRecordSelectListener() {
            @Override
            public void onRecordSelected(DiamondStoreGoods diamondStoreGoods) {

               //选择了购买 .付费成功后，提交后台，购买成功后，更改用户数据
               //此处应该弹出选择第三方支付的界面，选择后调用 SendBuyInfo(diamondStoreGoods,0);
                SendBuyInfo(diamondStoreGoods,0);
                /*public class BuyDiamondParam
                    {
                        public int userid;
                        public int usermb;
                        public int getdiamond;
                        public string tradetip;
                        public int paytype;

                    }
                */


            }
        });
        rv_diamond_list.setLayoutManager(new LinearLayoutManager(this));
        rv_diamond_list.setHasFixedSize(true);
        rv_diamond_list.addItemDecoration(new DividerItemDecoration(
                this, DividerItemDecoration.VERTICAL));
        rv_diamond_list.setAdapter(mAdapter);
        //从服务器取购买钻石货物的列表
        Thread thread=new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try{
                    JSONObject jsonParam= new JSONObject();
                    String strURL=getString(R.string.url_remote)+"func=getdiamondstore&param="+jsonParam.toString();
                    URL url = new URL(strURL);
                    Request request = new Request.Builder().url(strURL).build();
                    Response response = DzApplication.getHttpClient().newCall(request).execute();
                    String result=response.body().string();

                    JSONArray jsonArray=new JSONArray(result);
                    mDiamondStoreGoodsList=new ArrayList<DiamondStoreGoods>();
                    for (int i=0;i<jsonArray.length();i++){
                        DiamondStoreGoods obj=new DiamondStoreGoods();
                        JSONObject jsonObject=new JSONObject(jsonArray.get(i).toString());
                        obj.id=jsonObject.getInt("id");

                        if (jsonObject.getString("pic")!=null && !jsonObject.getString("pic").equals("null")){
                            obj.pic=jsonObject.getString("pic");
                        }
                        obj.costrmb=jsonObject.getInt("costrmb");
                        obj.diamonds=jsonObject.getInt("diamonds");
                        mDiamondStoreGoodsList.add(obj);

                    }
                    handler.sendEmptyMessage(MESAGE_GET_GOODS_OK);

                }catch (Exception e){
                    ToastUtil.showToastInScreenCenter(BuyDiamondActivity.this,"从服务取钻石商店数据出错，请稍后重试!");
                }
            }
        });
        thread.start();

    }

    private void SendBuyInfo(final DiamondStoreGoods diamondStoreGoods,final int buyType){
        Thread thread=new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try{

                    JSONObject jsonParam= new JSONObject();
                    /*public class BuyDiamondParam
                        {
                            public int userid;
                            public int usermb;
                            public int getdiamond;
                            public string tradetip;
                            public int paytype;
                        }
                    */
                    jsonParam.put("userid",application.getUserId());
                    jsonParam.put("paytype",buyType);
                    jsonParam.put("getdiamond",diamondStoreGoods.diamonds);
                    jsonParam.put("usermb",diamondStoreGoods.costrmb);
                    jsonParam.put("tradetip","花费"+diamondStoreGoods.costrmb+"购买"+diamondStoreGoods.diamonds+"钻石");

                    String strURL=getString(R.string.url_remote)+"func=buydiamond&param="+jsonParam.toString();
                    URL url = new URL(strURL);
                    Request request = new Request.Builder().url(strURL).build();
                    Response response = DzApplication.getHttpClient().newCall(request).execute();
                    String result=response.body().string();

                    Message message = Message.obtain();
                    message.arg1 = diamondStoreGoods.diamonds;
                    message.what = MESAGE_SEND_BUY_OK;
                    handler.sendMessage(message);
                }catch (Exception e){
                    ToastUtil.showToastInScreenCenter(BuyDiamondActivity.this,"从服务取钻石商店数据出错，请稍后重试!");
                }
            }
        });
        thread.start();

        }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_buy_diamond;
    }

    @Override
    protected void initViews() {
        super.initViews();
        rv_diamond_list=(RecyclerView)findViewById(R.id.rv_diamond_list);
        tv_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_back:
                finish();
        }

    }
}
