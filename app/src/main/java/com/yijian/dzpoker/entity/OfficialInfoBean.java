package com.yijian.dzpoker.entity;

/**
 * Created by qipu.qp on 2017/12/26.
 */

public class OfficialInfoBean {

    private String iconUrl;

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
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

    private String mainMsg;
    private String detailMsg;

}
