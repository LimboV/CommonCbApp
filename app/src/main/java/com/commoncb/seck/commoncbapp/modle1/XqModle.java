package com.commoncb.seck.commoncbapp.modle1;

import java.util.List;

/**
 * Created by ssHss on 2018/1/23.
 */

public class XqModle {

    int XqId;
    String XqName;
    List<ZCjjModle> ZCjjDetails;


    public int getXqId() {
        return XqId;
    }

    public void setXqId(int xqId) {
        XqId = xqId;
    }

    public String getXqName() {
        return XqName;
    }

    public void setXqName(String xqName) {
        XqName = xqName;
    }

    public List<ZCjjModle> getZCjjDetails() {
        return ZCjjDetails;
    }

    public void setZCjjDetails(List<ZCjjModle> ZCjjDetails) {
        this.ZCjjDetails = ZCjjDetails;
    }




}
