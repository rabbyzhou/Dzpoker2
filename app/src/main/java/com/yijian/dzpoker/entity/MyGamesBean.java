package com.yijian.dzpoker.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by qipu.qp on 2018/1/20.
 */

public class MyGamesBean {


    /**
     * id : 100405
     * name : 啊
     * type : 0
     * createuserid : 1003
     * clubid : 0
     * matchid : 0
     * duration : 30
     * starttime : 2018-01-20T15:21:21
     * endtime : null
     * BB : 2
     * SB : 1
     * maxplayers : 9
     * mintakeinchips : 200
     * isinsurance : 1
     * isstraddle : 0
     * is27 : 1
     * ante : 0
     * ischangeahead : 0
     * isiprestrict : 0
     * isgpsrestrict : 0
     * iscontroltakein : 1
     * takeinchipsuplimit : 0
     * maxtakeinchips : 1000
     * serverip : 106.14.221.253
     * serverport : 11820
     * isclosed : 0
     * curplayer : 0
     * surancepot : 0
     * 27jackpot : 0
     * curstate : start
     * sharecode : 1418
     * isjoin : 0
     * clubname : null
     * createusername : 海飞海
     * permituserid : 0
     * permitclubid : 0
     */

    private int id;
    private String name;
    private int type;
    private int createuserid;
    private int clubid;
    private int matchid;
    private int duration;
    private String starttime;
    private Object endtime;
    private int BB;
    private int SB;
    private int maxplayers;
    private int mintakeinchips;
    private int isinsurance;
    private int isstraddle;
    private int is27;
    private int ante;
    private int ischangeahead;
    private int isiprestrict;
    private int isgpsrestrict;
    private int iscontroltakein;
    private int takeinchipsuplimit;
    private int maxtakeinchips;
    private String serverip;
    private int serverport;
    private int isclosed;
    private int curplayer;
    private int surancepot;
    @SerializedName("27jackpot")
    private int _$27jackpot;
    private String curstate;
    private int sharecode;
    private int isjoin;
    private Object clubname;
    private String createusername;
    private int permituserid;
    private int permitclubid;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getCreateuserid() {
        return createuserid;
    }

    public void setCreateuserid(int createuserid) {
        this.createuserid = createuserid;
    }

    public int getClubid() {
        return clubid;
    }

    public void setClubid(int clubid) {
        this.clubid = clubid;
    }

    public int getMatchid() {
        return matchid;
    }

    public void setMatchid(int matchid) {
        this.matchid = matchid;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public Object getEndtime() {
        return endtime;
    }

    public void setEndtime(Object endtime) {
        this.endtime = endtime;
    }

    public int getBB() {
        return BB;
    }

    public void setBB(int BB) {
        this.BB = BB;
    }

    public int getSB() {
        return SB;
    }

    public void setSB(int SB) {
        this.SB = SB;
    }

    public int getMaxplayers() {
        return maxplayers;
    }

    public void setMaxplayers(int maxplayers) {
        this.maxplayers = maxplayers;
    }

    public int getMintakeinchips() {
        return mintakeinchips;
    }

    public void setMintakeinchips(int mintakeinchips) {
        this.mintakeinchips = mintakeinchips;
    }

    public int getIsinsurance() {
        return isinsurance;
    }

    public void setIsinsurance(int isinsurance) {
        this.isinsurance = isinsurance;
    }

    public int getIsstraddle() {
        return isstraddle;
    }

    public void setIsstraddle(int isstraddle) {
        this.isstraddle = isstraddle;
    }

    public int getIs27() {
        return is27;
    }

    public void setIs27(int is27) {
        this.is27 = is27;
    }

    public int getAnte() {
        return ante;
    }

    public void setAnte(int ante) {
        this.ante = ante;
    }

    public int getIschangeahead() {
        return ischangeahead;
    }

    public void setIschangeahead(int ischangeahead) {
        this.ischangeahead = ischangeahead;
    }

    public int getIsiprestrict() {
        return isiprestrict;
    }

    public void setIsiprestrict(int isiprestrict) {
        this.isiprestrict = isiprestrict;
    }

    public int getIsgpsrestrict() {
        return isgpsrestrict;
    }

    public void setIsgpsrestrict(int isgpsrestrict) {
        this.isgpsrestrict = isgpsrestrict;
    }

    public int getIscontroltakein() {
        return iscontroltakein;
    }

    public void setIscontroltakein(int iscontroltakein) {
        this.iscontroltakein = iscontroltakein;
    }

    public int getTakeinchipsuplimit() {
        return takeinchipsuplimit;
    }

    public void setTakeinchipsuplimit(int takeinchipsuplimit) {
        this.takeinchipsuplimit = takeinchipsuplimit;
    }

    public int getMaxtakeinchips() {
        return maxtakeinchips;
    }

    public void setMaxtakeinchips(int maxtakeinchips) {
        this.maxtakeinchips = maxtakeinchips;
    }

    public String getServerip() {
        return serverip;
    }

    public void setServerip(String serverip) {
        this.serverip = serverip;
    }

    public int getServerport() {
        return serverport;
    }

    public void setServerport(int serverport) {
        this.serverport = serverport;
    }

    public int getIsclosed() {
        return isclosed;
    }

    public void setIsclosed(int isclosed) {
        this.isclosed = isclosed;
    }

    public int getCurplayer() {
        return curplayer;
    }

    public void setCurplayer(int curplayer) {
        this.curplayer = curplayer;
    }

    public int getSurancepot() {
        return surancepot;
    }

    public void setSurancepot(int surancepot) {
        this.surancepot = surancepot;
    }

    public int get_$27jackpot() {
        return _$27jackpot;
    }

    public void set_$27jackpot(int _$27jackpot) {
        this._$27jackpot = _$27jackpot;
    }

    public String getCurstate() {
        return curstate;
    }

    public void setCurstate(String curstate) {
        this.curstate = curstate;
    }

    public int getSharecode() {
        return sharecode;
    }

    public void setSharecode(int sharecode) {
        this.sharecode = sharecode;
    }

    public int getIsjoin() {
        return isjoin;
    }

    public void setIsjoin(int isjoin) {
        this.isjoin = isjoin;
    }

    public Object getClubname() {
        return clubname;
    }

    public void setClubname(Object clubname) {
        this.clubname = clubname;
    }

    public String getCreateusername() {
        return createusername;
    }

    public void setCreateusername(String createusername) {
        this.createusername = createusername;
    }

    public int getPermituserid() {
        return permituserid;
    }

    public void setPermituserid(int permituserid) {
        this.permituserid = permituserid;
    }

    public int getPermitclubid() {
        return permitclubid;
    }

    public void setPermitclubid(int permitclubid) {
        this.permitclubid = permitclubid;
    }
}
