package com.commoncb.seck.commoncbapp.modle1;

import android.graphics.Bitmap;

/**
 * Created by limbo on 2018/6/11.
 */

public class New_Meter {
    String XH;
    String JBH;
    String KH;
    String HH;
    String UserName;
    String UserPhone1;
    String UserPhone2;
    String MeterNumber;
    String Caliber;
    String XqNum;
    String CjjNum;
    String MeterNum;
    String LastUserWater;
    String Last2MouthUserWater;
    String Last3MouthUserWater;
    String Addr;
    String BtFlag;


    public int getBjFlag() {
        return BjFlag;
    }

    public void setBjFlag(int bjFlag) {
        BjFlag = bjFlag;
    }

    int BjFlag;
    String MeterState;
    String UserState;
    String CbState;
    String Data;
    String ReadData;
    String LastData;
    String LastReadData;
    String IrDAData;
    String Lon;
    String Lat;
    String PLon;
    String PLat;
    String UploadTime;
    String UploadFlag;
    String CbFlag;
    Bitmap Pic1;
    Bitmap Pic2;
    Bitmap Pic3;
    String Note;

    public String getUserState() {
        return UserState;
    }

    public void setUserState(String userState) {
        UserState = userState;
    }

    public String getCbState() {
        return CbState;
    }

    public void setCbState(String cbState) {
        CbState = cbState;
    }

    public String getPLon() {
        return PLon;
    }

    public void setPLon(String PLon) {
        this.PLon = PLon;
    }

    public String getPLat() {
        return PLat;
    }

    public void setPLat(String PLat) {
        this.PLat = PLat;
    }

    public Bitmap getPic1() {
        return Pic1;
    }

    public void setPic1(Bitmap pic1) {
        Pic1 = pic1;
    }

    public Bitmap getPic2() {
        return Pic2;
    }

    public void setPic2(Bitmap pic2) {
        Pic2 = pic2;
    }

    public Bitmap getPic3() {
        return Pic3;
    }

    public void setPic3(Bitmap pic3) {
        Pic3 = pic3;
    }

    public String getNote() {
        if (Note == null){
            Note = "";
        }
        return Note;
    }

    public void setNote(String note) {
        Note = note;
    }

    public String getHH() {
        return HH;
    }

    public void setHH(String HH) {
        this.HH = HH;
    }

    public String getKH() {
        return KH;
    }

    public void setKH(String KH) {
        this.KH = KH;
    }

    public String getLon() {
        return Lon;
    }

    public void setLon(String lon) {
        if (lon == null) {
            lon = "";
        }
        Lon = lon;
    }

    public String getLat() {
        return Lat;
    }

    public void setLat(String lat) {
        if (lat == null) {
            lat = "";
        }
        Lat = lat;
    }

    public String getUploadTime() {
        return UploadTime;
    }

    public void setUploadTime(String uploadTime) {
        UploadTime = uploadTime;
    }

    public String getUploadFlag() {
        return UploadFlag;
    }

    public void setUploadFlag(String uploadFlag) {
        UploadFlag = uploadFlag;
    }

    public String getCbFlag() {
        return CbFlag;
    }

    public void setCbFlag(String CBFlag) {
        this.CbFlag = CBFlag;
    }


    public String getXH() {
        return XH;
    }

    public void setXH(String XH) {
        this.XH = XH;
    }

    public String getJBH() {
        return JBH;
    }

    public void setJBH(String JBH) {
        this.JBH = JBH;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getUserPhone1() {
        return UserPhone1;
    }

    public void setUserPhone1(String userPhone1) {
        UserPhone1 = userPhone1;
    }

    public String getUserPhone2() {
        return UserPhone2;
    }

    public void setUserPhone2(String userPhone2) {
        UserPhone2 = userPhone2;
    }

    public String getMeterNumber() {
        return MeterNumber;
    }

    public void setMeterNumber(String meterNumber) {
        MeterNumber = meterNumber;
    }

    public String getCaliber() {
        return Caliber;
    }

    public void setCaliber(String caliber) {
        Caliber = caliber;
    }

    public String getXqNum() {
        return XqNum;
    }

    public void setXqNum(String xqNum) {
        XqNum = xqNum;
    }

    public String getCjjNum() {
        return CjjNum;
    }

    public void setCjjNum(String cjjNum) {
        CjjNum = cjjNum;
    }

    public String getMeterNum() {
        return MeterNum;
    }

    public void setMeterNum(String meterNum) {
        MeterNum = meterNum;
    }

    public String getLastUserWater() {
        return LastUserWater;
    }

    public void setLastUserWater(String lastUserWater) {
        LastUserWater = lastUserWater;
    }

    public String getLast2MouthUserWater() {
        return Last2MouthUserWater;
    }

    public void setLast2MouthUserWater(String last2MouthUserWater) {
        Last2MouthUserWater = last2MouthUserWater;
    }

    public String getLast3MouthUserWater() {
        return Last3MouthUserWater;
    }

    public void setLast3MouthUserWater(String last3MouthUserWater) {
        Last3MouthUserWater = last3MouthUserWater;
    }

    public String getAddr() {
        return Addr;
    }

    public void setAddr(String addr) {
        Addr = addr;
    }

    public String getBtFlag() {
        return BtFlag;
    }

    public void setBtFlag(String btFlag) {
        BtFlag = btFlag;
    }

    public String getMeterState() {
        return MeterState;
    }

    public void setMeterState(String meterState) {
        MeterState = meterState;
    }

    public String getData() {
        if (Data == null){
            Data = "";
        }
        return Data;
    }

    public void setData(String data) {
        Data = data;
    }

    public String getReadData() {
        if (ReadData == null){
            ReadData = "";
        }
        return ReadData;
    }

    public void setReadData(String readData) {
        ReadData = readData;
    }

    public String getLastData() {
        return LastData;
    }

    public void setLastData(String lastData) {
        LastData = lastData;
    }

    public String getLastReadData() {
        return LastReadData;
    }

    public void setLastReadData(String lastReadData) {
        LastReadData = lastReadData;
    }

    public String getIrDAData() {
        return IrDAData;
    }

    public void setIrDAData(String irDAData) {
        IrDAData = irDAData;
    }
}
