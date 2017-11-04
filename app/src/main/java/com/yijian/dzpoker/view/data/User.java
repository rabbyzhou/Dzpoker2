package com.yijian.dzpoker.view.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by koyabr on 10/22/15.
 */
public class User implements Serializable {
    public int userId;
    public String userLoginName;
    public String userHeadPic;
    public String nickName;
    public String sex;
    public String personalTip;
    public String province;
    public String city;
    public int goldcoin;
    public int diamond;
    public int levelid;
    public String lastlogingpsx;
    public String lastlogingpsy;
    public String lastloginip;
    public String levelname;
    public String password;

    /*[{"id":2,"nickname":"无名","loginname":"13801865121","password":"666666","mobilephone":"13801865121","email":null,"sex":"男",
                    "province":null,"city":null,"personaltip":null,"headpic":null,"goldcoin":1000,"diamond":100,"type":0,
                    "levelid":1,"levelenddate":"2017-08-10T00:00:00","lastlogintime":"2017-08-30T11:25:59","lastlogingpsx":null,
                    "lastlogingpsy":null,"lastloginip":null,"registertime":"2017-08-02T15:50:23","id1":1,"level":1,"levelname":"黄金会员",
                    "levelabstract":"黄金","durationdays":100,"costdiamonds":100,"maxclubs":5}]*/

    public List<ClubInfo> arrClub;

    public User(){
        arrClub=new ArrayList<ClubInfo>();
    }


}
