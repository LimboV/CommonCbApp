package com.commoncb.seck.commoncbapp.modle1;

/**
 * Created by ssHss on 2018/6/27.
 */

public class UpdataModle {
    String appname;//服务器版本号
    String serverVersion;//服务器上APK版本号
    String serverFlag;//服务器标志
    String lastForce;//是否强制更新
    String updateurl;//下载链接
    String upgradeinfo;//更新备注


        public static String downloadDir = "SeckLoRaDB" ;// 下载目录

    public String getAppname() {
        return appname;
    }

    public void setAppname(String appname) {
        this.appname = appname;
    }

    public String getServerVersion() {
        return serverVersion;
    }

    public void setServerVersion(String serverVersion) {
        this.serverVersion = serverVersion;
    }

    public String getServerFlag() {
        return serverFlag;
    }

    public void setServerFlag(String serverFlag) {
        this.serverFlag = serverFlag;
    }

    public String getLastForce() {
        return lastForce;
    }

    public void setLastForce(String lastForce) {
        this.lastForce = lastForce;
    }

    public String getUpdateurl() {
        return updateurl;
    }

    public void setUpdateurl(String updateurl) {
        this.updateurl = updateurl;
    }

    public String getUpgradeinfo() {
        return upgradeinfo;
    }

    public void setUpgradeinfo(String upgradeinfo) {
        this.upgradeinfo = upgradeinfo;
    }

}
