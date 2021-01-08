package com.commoncb.seck.commoncbapp.modle1;

import android.graphics.Bitmap;

/**
 * Created by ssHss on 2018/1/23.
 */

public class UserModle {
    public String getUserNumber() {
        return UserNumber;
    }

    public void setUserNumber(String userNumber) {
        UserNumber = userNumber;
    }

    public String getScanNum() {
        return ScanNum;
    }

    public void setScanNum(String scanNum) {
        ScanNum = scanNum;
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

    public String getMeterAddr() {
        return MeterAddr;
    }

    public void setMeterAddr(String meterAddr) {
        MeterAddr = meterAddr;
    }

    public String getTelePhone() {
        return TelePhone;
    }

    public void setTelePhone(String telePhone) {
        TelePhone = telePhone;
    }

    public String getWaterStyle() {
        return WaterStyle;
    }

    public void setWaterStyle(String waterStyle) {
        WaterStyle = waterStyle;
    }

    public String getUserState() {
        return UserState;
    }

    public void setUserState(String userState) {
        UserState = userState;
    }

    public String getMeterNumber() {
        return MeterNumber;
    }

    public void setMeterNumber(String meterNumber) {
        MeterNumber = meterNumber;
    }

    public String getData() {
        return Data;
    }

    public void setData(String data) {
        Data = data;
    }

    public String getLastOneData() {
        return LastOneData;
    }

    public void setLastOneData(String lastOneData) {
        LastOneData = lastOneData;
    }

    public String getLastTwoData() {
        return LastTwoData;
    }

    public void setLastTwoData(String lastTwoData) {
        LastTwoData = lastTwoData;
    }

    public String getWaterConsumption() {
        return WaterConsumption;
    }

    public void setWaterConsumption(String waterConsumption) {
        WaterConsumption = waterConsumption;
    }

    public String getLastOneConsumption() {
        return LastOneConsumption;
    }

    public void setLastOneConsumption(String lastOneConsumption) {
        LastOneConsumption = lastOneConsumption;
    }

    public String getLastTwoConsumption() {
        return LastTwoConsumption;
    }

    public void setLastTwoConsumption(String lastTwoConsumption) {
        LastTwoConsumption = lastTwoConsumption;
    }

    public String getLastThreeConsumption() {
        return LastThreeConsumption;
    }

    public void setLastThreeConsumption(String lastThreeConsumption) {
        LastThreeConsumption = lastThreeConsumption;
    }

    public int getXqNum() {
        return XqNum;
    }

    public void setXqNum(int xqNum) {
        XqNum = xqNum;
    }

    public String getXqName() {
        return XqName;
    }

    public void setXqName(String xqName) {
        XqName = xqName;
    }
    public String getMeterNumberX() {
        return MeterNumberX;
    }

    public void setMeterNumberX(String meterNumberX) {
        MeterNumberX = meterNumberX;
    }
    int XqNum;//小区号
    String XqName;//小区名
    String UserNumber;//用户号
    String ScanNum;//抄见码
    String UserName;//用户名称
    String UserAddr;//用户地址
    String MeterAddr;//装表地址
    String TelePhone;//移动电话
    String WaterStyle;//用水性质
    String UserState;//用户状态
    String MeterNumber;//表身号
    String MeterNumberX;//表号
    String Data;//本期读数
    String LastOneData;//前一期读数
    String LastTwoData;//前二期读数
    String WaterConsumption;//本期水量
    String LastOneConsumption;//前一期水量
    String LastTwoConsumption;//前两期水量
    String LastThreeConsumption;//前三期水量
    Bitmap Pic1;

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

    Bitmap Pic2;
    Bitmap Pic3;


}
