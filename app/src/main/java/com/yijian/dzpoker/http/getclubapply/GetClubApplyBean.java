package com.yijian.dzpoker.http.getclubapply;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by qipu.qp on 2017/12/25.
 */
public class GetClubApplyBean {
    /**
     * requestid : 18
     * userid : 1003
     * nickname : 海飞海
     * clubid : 1016
     * clubname : dddd
     * requesttime : 2017-12-23T22:12:44
     * requestmsg : h zh
     * ispermit : 2
     */

    private Integer requestid;
    private Integer userid;
    private String nickname;
    private Integer clubid;
    private String clubname;
    private String requesttime;
    private String requestmsg;
    private Integer ispermit;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public Integer getRequestid() {
        return requestid;
    }

    public void setRequestid(Integer requestid) {
        this.requestid = requestid;
    }

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Integer getClubid() {
        return clubid;
    }

    public void setClubid(Integer clubid) {
        this.clubid = clubid;
    }

    public String getClubname() {
        return clubname;
    }

    public void setClubname(String clubname) {
        this.clubname = clubname;
    }

    public String getRequesttime() {
        return requesttime;
    }

    public void setRequesttime(String requesttime) {
        this.requesttime = requesttime;
    }

    public String getRequestmsg() {
        return requestmsg;
    }

    public void setRequestmsg(String requestmsg) {
        this.requestmsg = requestmsg;
    }

    public Integer getIspermit() {
        return ispermit;
    }

    public void setIspermit(Integer ispermit) {
        this.ispermit = ispermit;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
