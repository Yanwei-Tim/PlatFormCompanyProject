package com.yuefeng.features.modle.video;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2018/4/28.
 */

public class VideoEquipmentBean implements Serializable {

    /**
     * success : true
     * msgTitle : 成功提示
     * msg : 操作成功!
     */
    private boolean success;
    private String msgTitle;
    private List<ChangeVideoEquipmentDataBean> msg;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMsgTitle() {
        return msgTitle;
    }

    public void setMsgTitle(String msgTitle) {
        this.msgTitle = msgTitle;
    }


    public List<ChangeVideoEquipmentDataBean> getData() {
        return msg;
    }

    public void setData(List<ChangeVideoEquipmentDataBean> data) {
        this.msg = data;
    }
}
