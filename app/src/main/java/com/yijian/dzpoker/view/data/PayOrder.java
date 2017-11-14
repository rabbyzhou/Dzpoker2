package com.yijian.dzpoker.view.data;

/**
 * Created by c_huangl on 0009, 11/09/2017.
 */

public class PayOrder {

    public int userid;  //提交订单用户id
    public String store;  //当前只有diamondstore是需要人民币支付，此处默认填t_diamondstore
    public int goodsid; //购买货物在store内的id
    public int payrmb; //支付人民币

}
