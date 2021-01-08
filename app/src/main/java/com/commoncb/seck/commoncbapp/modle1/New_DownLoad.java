package com.commoncb.seck.commoncbapp.modle1;

import java.util.List;

/**
 * Created by limbo on 2018/6/13.
 */

public class New_DownLoad {
    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    String flag;

    public List<New_Meter> getDatas() {
        return datas;
    }

    public void setDatas(List<New_Meter> datas) {
        this.datas = datas;
    }

    List<New_Meter> datas;
}
