package com.commoncb.seck.commoncbapp.modle1;

import java.util.List;

/**
 * Created by limbo on 2018/5/23.
 */

public class ZCjjModle {
    //    int XqId;
    int ZCjjId;
    String ZCjjAddr;
    List<FCjjModle> FCjjDetails;

    public List<FCjjModle> getFCjjDetails() {
        return FCjjDetails;
    }

    public void setFCjjDetails(List<FCjjModle> FCjjDetails) {
        this.FCjjDetails = FCjjDetails;
    }

    public int getZCjjId() {
        return ZCjjId;
    }

    public void setZCjjId(int ZCjjId) {
        this.ZCjjId = ZCjjId;
    }

    public String getZCjjAddr() {
        return ZCjjAddr;
    }

    public void setZCjjAddr(String ZCjjAddr) {
        this.ZCjjAddr = ZCjjAddr;
    }


}
