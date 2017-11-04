package com.yijian.dzpoker.view.data;

/**
 * Created by rabby on 2017/10/12.
 */

public class TablePlayerInfo {
    public int userid;
    public String nickname;
    public String headpic;
    public int remainchips;
    public int takeinchips;
    public int amountchips;
    public CardInfo[] cards;
    public int seatindex;
    public int curwaitactiontime;
    public int waitactiontime;
    public int needwaitactiontime;
    public boolean isconnected;
    public int lastplayaction;
    public WaitAction waitactionparam;

}
