package com.yijian.dzpoker.view.data;

import java.util.List;

/**
 * Created by c_huangl on 0012, 11/12/2017.
 */

public class RoundInfo {

    public String starttime;
    public int pot;
    public int jackpot;
    public int surancepot;
    public CardInfo[] cards;
    public List<RoundUserInfo> rounduserinfo;
    public boolean iscollect;

}
