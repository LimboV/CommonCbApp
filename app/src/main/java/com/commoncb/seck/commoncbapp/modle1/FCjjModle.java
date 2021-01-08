package com.commoncb.seck.commoncbapp.modle1;

import java.util.List;

/**
 * Created by limbo on 2018/5/23.
 */

public class FCjjModle {
   /* int XqId;
    int ZCjjId;*/
    int FCjjId;
    String FCjjAddr;


    public int getFCjjType() {
        return FCjjType;
    }

    public void setFCjjType(int FCjjType) {
        this.FCjjType = FCjjType;
    }

    int FCjjType;


    List<MeterModle> MeterDetails;

    public int getFCjjId() {
        return FCjjId;
    }

    public void setFCjjId(int FCjjId) {
        this.FCjjId = FCjjId;
    }

    public String getFCjjAddr() {
        return FCjjAddr;
    }

    public void setFCjjAddr(String FCjjAddr) {
        this.FCjjAddr = FCjjAddr;
    }

    public List<MeterModle> getMeterDetails() {
        return MeterDetails;
    }

    public void setMeterDetails(List<MeterModle> meterDetails) {
        MeterDetails = meterDetails;
    }



}
