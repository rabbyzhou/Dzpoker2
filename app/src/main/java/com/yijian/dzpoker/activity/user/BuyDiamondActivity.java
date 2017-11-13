package com.yijian.dzpoker.activity.user;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.yijian.dzpoker.R;
import com.yijian.dzpoker.activity.base.BaseBackActivity;
import com.yijian.dzpoker.activity.fragment.BottomMenuFragment;
import com.yijian.dzpoker.util.DzApplication;
import com.yijian.dzpoker.util.ToastUtil;
import com.yijian.dzpoker.view.adapter.DiamonsListAdapter;
import com.yijian.dzpoker.view.data.DiamondStoreGoods;
import com.yijian.dzpoker.view.data.PayMenuItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import main.air.com.youpay.bean.OrderInfo;
import main.air.com.youpay.http.OnYPayCodeListener;
import main.air.com.youpay.util.MD5;
import main.air.com.youpay.util.YPServiceSDK;
import okhttp3.Request;
import okhttp3.Response;

public class BuyDiamondActivity extends BaseBackActivity {

    private RecyclerView rv_diamond_list;
    private DiamonsListAdapter mAdapter;
    private List<DiamondStoreGoods> mDiamondStoreGoodsList=new ArrayList<DiamondStoreGoods>();

    private final int MESAGE_GET_GOODS_OK=0x1001;
    private final int MESAGE_GET_PAYID_OK=0x1002;
    private final int MESAGE_BUY_FAILED=0x1003;

    private final static int MENU_TITLE_ITEM = -1;
    private String[] mPayNames = {"","微信","支付宝"};
    private int[] mPayTypes = {MENU_TITLE_ITEM,11,12};

    private Dialog mBuyingDg;

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
                case MESAGE_GET_PAYID_OK:
                    Bundle bundle = msg.getData();
                    payBill(bundle.getInt("appId"), bundle.getString("url"), bundle.getInt("payType"), (DiamondStoreGoods) msg.obj);
                    break;
                case MESAGE_BUY_FAILED:
                    updateStateAfterDeal();
                    ToastUtil.showToastInScreenCenter(BuyDiamondActivity.this,"购买失败，请稍后重试！");
                    break;
                default:
                    break;
            }
        }
    };


    private void updateStateAfterDeal(){
        //可以再次购买
        rv_diamond_list.setClickable(true);
        //取消对话框
        //if (mBuyingDg.isShowing()) {
        //    mBuyingDg.cancel();
        //}
    }

    private void  confirmToPay(final DiamondStoreGoods goods){

        final BottomMenuFragment bottomMenuFragment = new BottomMenuFragment();

        List<PayMenuItem> menuItemList = new ArrayList<PayMenuItem>();
        for(int i=0; i< mPayNames.length; i++){
            PayMenuItem item = new PayMenuItem();

            //用户第一行显示金额
            item.setPayType(mPayTypes[i]);
            if (mPayNames[i].equals("")){
                item.setText(getString(R.string.costs_rmb, goods.costrmb));
                item.setStyle(PayMenuItem.MenuItemStyle.COMMON);
                item.setMenuItemOnClickListener(new MenuItemOnClickListener(bottomMenuFragment, item) {
                    @Override
                    public void onClickMenuItem(View v, PayMenuItem menuItem) {
                        return;
                    }
                });

            } else {
                item.setText(mPayNames[i]);
                item.setMenuItemOnClickListener(new MenuItemOnClickListener(bottomMenuFragment, item) {
                    @Override
                    public void onClickMenuItem(View v, PayMenuItem menuItem) {
                        // 开始支付, 流程
                        // 1. 向服务器申请支付订单号.
                        // 2. 拿到订单号, 调用第三方接口支付, 成功后服务端会直接充值.
                        StartBuying(goods,menuItem.getPayType());
                        bottomMenuFragment.dismiss();
                    }
                });
            }
            menuItemList.add(item);
        }

        bottomMenuFragment.setMenuItems(menuItemList);
        bottomMenuFragment.show(getFragmentManager(), "BottomMenuFragment");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new DiamonsListAdapter(this, new DiamonsListAdapter.OnRecordSelectListener() {
            @Override
            public void onRecordSelected(DiamondStoreGoods diamondStoreGoods) {

                //确定是否购买, 获取payType
                confirmToPay(diamondStoreGoods);

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

    private void StartBuying(final DiamondStoreGoods diamondStoreGoods,final int payType){

        //购买时, 禁止再次购买
        rv_diamond_list.setClickable(false);

        //显示对话框
        //mBuyingDg = DialogCreator.createLoadingDialog(this,getResources().getString(R.string.userlevel_buying));
        //mBuyingDg.show();

        Thread thread=new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try{
                    //1. 从服务器拿到订单id
                    JSONObject jsonOrder= new JSONObject();
                    /**
                     public class PayOrder {
                         public int userid;  //提交订单用户id
                         public String store;  //当前只有diamondstore是需要人民币支付，此处默认填t_diamondstore
                         public int goodsid; //购买货物在store内的id
                         public int payrmb; //支付人民币
                     }*/

                    jsonOrder.put("userid",application.getUserId());
                    jsonOrder.put("store","t_diamondstore");
                    jsonOrder.put("goodsid",diamondStoreGoods.id);
                    jsonOrder.put("payrmb",diamondStoreGoods.costrmb);

                    String orderUrl=getString(R.string.url_remote)+"func=getpayorder&param="+jsonOrder.toString();
                    Request orderRequest = new Request.Builder().url(orderUrl).build();
                    Response orderResponse = DzApplication.getHttpClient().newCall(orderRequest).execute();
                    String orderRet=orderResponse.body().string();
                    JSONObject orderJson=new JSONObject(orderRet);

                    Log.d("BuyDiamond", " ==== " + jsonOrder.toString());
                    Log.d("BuyDiamond", " -- " + orderJson.toString());

                    int ret = orderJson.getInt("ret");
                    int appId = orderJson.getInt("orderid");
                    String payUrl = orderJson.getString("url");

                    //申请成功
                    if (ret == 0){
                        Message message = Message.obtain();
                        message.what = MESAGE_GET_PAYID_OK;

                        Bundle bundle = new Bundle();
                        bundle.putInt("appId",appId);
                        bundle.putInt("payType",payType);
                        bundle.putString("url",payUrl);
                        message.setData(bundle);

                        message.obj = diamondStoreGoods;
                        handler.sendMessage(message);

                    } else {
                        handler.sendMessage(handler.obtainMessage(MESAGE_BUY_FAILED));
                    }

                    /** 服务端操作, 客户端不需要申请
                    // 3. 充值
                    JSONObject jsonParam= new JSONObject();

                    jsonParam.put("userid",application.getUserId());
                    jsonParam.put("paytype",payType);
                    jsonParam.put("getdiamond",diamondStoreGoods.diamonds);
                    jsonParam.put("usermb",diamondStoreGoods.costrmb * 100);
                    jsonParam.put("tradetip","花费"+diamondStoreGoods.costrmb+"元购买"+diamondStoreGoods.diamonds+"颗钻石");

                    String strURL=getString(R.string.url_remote)+"func=buydiamond&param="+jsonParam.toString();
                    Request request = new Request.Builder().url(strURL).build();
                    Response response = DzApplication.getHttpClient().newCall(request).execute();
                    String result=response.body().string();
                    JSONObject jsonObject=new JSONObject(result);

                    //支付成功
                    if (jsonObject.getInt("ret") == 0){
                        Message message = Message.obtain();
                        message.arg1 = diamondStoreGoods.diamonds;
                        message.what = MESAGE_GET_PAYID_OK;
                        handler.sendMessage(message);
                    } else {
                        handler.sendMessage(handler.obtainMessage(MESAGE_BUY_FAILED));
                    }**/

                }catch (Exception e){
                    handler.sendMessage(handler.obtainMessage(MESAGE_BUY_FAILED));
                }
            }
        });
        thread.start();

        }

    private boolean payBill(int appID, String payUrl, int payType, DiamondStoreGoods diamondStoreGoods){
        boolean ret = false;

        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        OrderInfo info = new OrderInfo();
        info.setAppid(appID);
        info.setOrderid(df.format(System.currentTimeMillis()));
        info.setSubject("购买" + diamondStoreGoods.diamonds + "钻石");
        info.setFee(diamondStoreGoods.costrmb * 100 + "");
        info.setTongbu_url(payUrl);
        info.setClientip(YPServiceSDK.getLocalIpAddress(this));
        //如果没有传入appKey，sign参数不能为空
        info.setAppKey(getString(R.string.app_key));
        info.setBack_url(payUrl);
        info.setPaytype(payType);
        String sign = MD5.md5(info.getAppid() + info.getOrderid() + info.getFee() + info.getTongbu_url() + info.getAppKey());
        //info.setSign("");
        info.setSign(sign);

        YPServiceSDK.YPServiceStart(this, info, new OnYPayCodeListener() {
            @Override
            public void onYPayCode(String code) {
                updateStateAfterDeal();
                if ( code.equals("1")){ //支付成功
                    ToastUtil.showToastInScreenCenter(BuyDiamondActivity.this,"您已购买成功");
                } else {
                    ToastUtil.showToastInScreenCenter(BuyDiamondActivity.this,"购买失败，请稍后重试！");
                }
            }
        });
        return ret;
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ){
            setResult(RESULT_OK);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
