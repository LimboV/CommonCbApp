package com.commoncb.seck.commoncbapp.modle1;

import android.graphics.Bitmap;

/**
 * Created by limbo on 2018/5/23.
 */

public class MeterModle {

    public int getMeterId() {
        return MeterId;
    }

    public void setMeterId(int meterId) {
        MeterId = meterId;
    }

    public String getFactoryNumber() {
        return FactoryNumber;
    }

    public void setFactoryNumber(String factoryNumber) {
        FactoryNumber = factoryNumber;
    }

    public String getUserNumber() {
        return UserNumber;
    }

    public void setUserNumber(String userNumber) {
        UserNumber = userNumber;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getUserAddr() {
        return UserAddr;
    }

    public void setUserAddr(String userAddr) {
        UserAddr = userAddr;
    }

    public String getLastData() {
        return LastData;
    }

    public void setLastData(String lastData) {
        LastData = lastData;
    }

    public String getLastDate() {
        return LastDate;
    }

    public void setLastDate(String lastDate) {
        LastDate = lastDate;
    }

    public String getData() {
        return Data;
    }

    public void setData(String data) {
        Data = data;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public Bitmap getPic() {
        return Pic;
    }

    public void setPic(Bitmap pic) {
        Pic = pic;
    }

    /*int XqId;
    int ZCjjId;
    int FCjjId;*/
    int MeterId;
    String FactoryNumber;
    String UserNumber;
    String UserName;
    String UserAddr;
    String LastData;
    String LastDate;
    String Data;
    String Date;
    Bitmap Pic;
    int Flag;

    public int getFlag() {
        return Flag;
    }

    public void setFlag(int flag) {
        Flag = flag;
    }
}
