package com.commoncb.seck.commoncbapp.modle1;

import java.util.List;

/**
 * Created by limbo on 2018/6/3.
 */

public class Count {


    public String getCount() {
        return Count;
    }

    public void setCount(String count) {
        Count = count;
    }

    String Count;
    String Msg;

    public String getMsg() {
        return Msg;
    }

    public void setMsg(String msg) {
        Msg = msg;
    }

    public List<String> getFailList() {
        return FailList;
    }

    public void setFailList(List<String> failList) {
        FailList = failList;
    }

    List<String> FailList;
}
