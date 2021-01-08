package com.commoncb.seck.commoncbapp.utils;

import android.view.View;

public class ViewClickVibrate implements View.OnClickListener {
    /** 按钮震动时间 */
    private final int VIBRATE_TIME = 500;


    @Override
    public void onClick(View v) {
        // TODO 根据设置中的标记判断是否执行震动
        VibrateHelp.vSimple(v.getContext(), VIBRATE_TIME);
    }
}
