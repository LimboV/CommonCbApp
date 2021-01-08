package com.commoncb.seck.commoncbapp.modle1;

import java.util.List;

/**
 * Created by limbo on 2018/6/11.
 */

public class New_Login {
    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }


    String flag;
    List<New_UserMsg> datas;


    public List<New_UserMsg> getDatas() {
        return datas;
    }

    public void setDatas(List<New_UserMsg> datas) {
        this.datas = datas;
    }

}
