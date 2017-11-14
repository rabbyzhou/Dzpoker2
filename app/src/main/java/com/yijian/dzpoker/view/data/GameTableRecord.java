package com.yijian.dzpoker.view.data;

import java.util.List;

/**
 * Created by c_huangl on 0011, 11/11/2017.
 */

public class GameTableRecord {

    public String clubname;
    public String gametablename;
    public int bb;
    public int sb;
    public String endtime;
    public String starttime;
    public String createusernickname;
    public int type;  //0 德州 1 奥马哈
    public int maxplayers;
    public int surancepot;
    public int jackpot;
    public int allrounds;
    public int maxpot;
    public int alltakein;
    public List<GameTableUserInfo> gametableuser;

}
