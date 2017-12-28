package com.yijian.dzpoker.entity;

/**
 * Created by qipu.qp on 2017/12/27.
 */

public class ClubManagerBean {

    private String mainMsg;
    private String detailMsg;

    private int userId;
    private int requestId;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public String getMainMsg() {
        return mainMsg;
    }

    public void setMainMsg(String mainMsg) {
        this.mainMsg = mainMsg;
    }

    public String getDetailMsg() {
        return detailMsg;
    }

    public void setDetailMsg(String detailMsg) {
        this.detailMsg = detailMsg;
    }
}
