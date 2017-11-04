package com.yijian.dzpoker.view.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by koyabr on 10/22/15.
 */
public class ClubInfo implements Serializable{
    public int clubID;
    public String clubName;//俱乐部名称
    public String clubLevelName;//俱乐部级别名称
    public String clubHeadPic;//俱乐部头像
    public String  location;
    public int maxCLubMemberNumber;
    public int clubMemberNumber;
    public int actualPokerTableNumber;
    public boolean bInClub;
    public int createuserid;
    public String nickname ;//创建用户昵称
    public String headpic1 ;//创建用户头像
    public List<User> arrUser;
    public String  clubabstract;//俱乐部简介
    public String  clubCreatetime; //俱乐部创建时间
    public Boolean bGetClubNotice;//是否获取俱乐部通知
    public String levelpic;//俱乐部等级的图片
    public String province;
    public String city;
    public ClubInfo(){
        arrUser=new ArrayList<User>();
    }
    public long imgroupid;

}
