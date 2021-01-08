package com.commoncb.seck.commoncbapp.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.commoncb.seck.commoncbapp.DB.DbHelper;
import com.commoncb.seck.commoncbapp.R;
import com.commoncb.seck.commoncbapp.modle1.Xq;
import com.commoncb.seck.commoncbapp.utils.BlueToothActivity;
import com.commoncb.seck.commoncbapp.utils.HintDialog;
import com.commoncb.seck.commoncbapp.utils.HzyUtils;
import com.commoncb.seck.commoncbapp.utils.NetworkLayer;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.commoncb.seck.commoncbapp.activity.MainActivity.timeOut;
import static com.commoncb.seck.commoncbapp.activity.MenuActivity.netThread;
import static com.commoncb.seck.commoncbapp.activity.MenuActivity.sXqList;

/**
 * Created by ssHss on 2018/7/27.
 */

public class ZCjjActivity extends BaseActivity {
    @BindView(R.id.btn_getIrDA)
    Button btn_getIrDA;
    @BindView(R.id.btn_saveIrDA)
    Button btn_saveIrDA;

    @BindView(R.id.lv_cjjList)
    ListView lv_cjjList;
    @BindView(R.id.rb_1)
    RadioButton rb_1;
    @BindView(R.id.rb_2)
    RadioButton rb_2;
    @BindView(R.id.tv_deBugMsg)
    TextView tv_deBugMsg;


    String XH;//传入线号
    String sendMsg = "";
    String resultMsg = "";

    String cbStyle = "EZC";
    int radioInt = 0;

    List<String> cjjList = new ArrayList<>();//存放抄表数据
    private ListAdapter ad;

    List<String> xqList;//所有小区信息
    List<String> XqList;//用以存放筛取出的小区信息
    List<String> xlist;//存放小区号列表
    public static String deBugMsg = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zc);
        ButterKnife.bind(this);
        Bundle bundle = getIntent().getExtras();
        XH = bundle.getString("XH");
        MenuActivity.btAuto = true;

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (MenuActivity.btAuto) {
                    try {
                        Thread.sleep(500);
                        resultMsg = MenuActivity.Cjj_CB_MSG.toLowerCase().replaceAll("0x", "").replaceAll(" ", "");
                        if (resultMsg.contains("2ac8c8c8")) {
                            resultMsg = resultMsg.substring(resultMsg.indexOf("2ac8c8c8"));
                        }
                        if (resultMsg.length() > 0) {
                            if (resultMsg.length() > 0 && resultMsg.contains("2ac8c8c8") && resultMsg.contains("5d5d5d")) {
                                Log.d("limbo", "get:" + resultMsg);
                                resultMsg = resultMsg.substring(resultMsg.indexOf("2ac8c8c8"), resultMsg.indexOf("5d5d5d") + 6);
                                Message message = new Message();
                                message.obj = resultMsg;
                                message.what = 0x00;
                                mHandler.sendMessage(message);
                                resultMsg = "";
                                MenuActivity.Cjj_CB_MSG = "";

                            }
                        }
                    } catch (Exception e) {
                        Log.d("limbo", e.toString());
                        e.printStackTrace();
                    }


                }
            }
        }).start();

        xlist = getXq(XH);//获取最新小区列表
        if (xlist.size() == 0) {
            Toast.makeText(ZCjjActivity.this, "该线号下无红外表", Toast.LENGTH_LONG).show();
            finish();
        }

        rb_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cbStyle = "EZC";
                radioInt = 0;
            }
        });
        rb_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cbStyle = "AZC";
                radioInt = 1;
            }
        });
        loadMsg();
        btn_getIrDA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (netThread == null) {
                    MenuActivity.resetNetwork();
                    networkConnect();
                    return;
                } else {



                }
            }
        });

    }

    private void cb(){
        HzyUtils.showProgressDialog(ZCjjActivity.this);
        tv_deBugMsg.setText("");
        deBugMsg = "";
        if (cjjList != null) {
            cjjList.clear();
        }
        ad = new ArrayAdapter(ZCjjActivity.this, R.layout.list_textview, cjjList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                return super.getView(position, convertView, parent);
            }
        };
        lv_cjjList.setAdapter(ad);
        resultMsg = "";
        MenuActivity.Cjj_CB_MSG = "";
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (cbStyle.equals("EZC")) {//E型表总采
                        prepareTimeStart(160);
                        for (int i = 0; i < 450; i++) {
                            MenuActivity.sendCmd("55");
                        }
                        try {
                            Thread.sleep(6000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        //补抄操作


                    } else {//A型总采
                        prepareTimeStart(100);
                    }

                    resultMsg = "";
                    MenuActivity.Cjj_CB_MSG = "";
                    MenuActivity.sendCmd("5b5b5b3f3fc8c8c801fe09015555555555555555a93f5d5d5d");



                } catch (Exception e) {
                    Log.d("limbo", e.toString());
                    e.printStackTrace();
                }
            }
        }).start();


    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x00:
                    try {
                        String result = (String) msg.obj;





                    } catch (Exception e) {
                        HzyUtils.closeProgressDialog();
                        Toast.makeText(ZCjjActivity.this, "数据缺失请重抄", Toast.LENGTH_LONG).show();
                    }

                    break;
                case 0x99:
                    tv_deBugMsg.setText(deBugMsg);
                    deBugMsg = "";

                    HzyUtils.closeProgressDialog();
                    new SweetAlertDialog(ZCjjActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("提示!")
                            .setContentText("无数据返回，请重试!")
                            .show();

                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 筛取小区
     */
    private List<String> getXq(String XH) {
        List<String> data = new ArrayList<>();
        try {
            xqList = DbHelper.getAllXqNum(XH);//从数据库提取出
            int flag;
            XqList = new ArrayList<>();
            for (int i = 0; i < xqList.size(); i++) {
                flag = 0;
                if (XqList.size() == 0) {
                    XqList.add(xqList.get(i));
                } else {
                    for (int j = 0; j < XqList.size(); j++) {
                        if (XqList.get(j).toString().equals(xqList.get(i).toString())) {
                        } else {
                            flag++;
                        }
                    }
                    if (flag == XqList.size()) {
                        XqList.add(xqList.get(i));
                    }
                }
            }
            Log.d("limbo", "小区列表长度" + XqList.size() + "");
            for (String x : XqList) {
                if (sXqList.size() > 0) {
                    for (Xq xq : sXqList) {
                        if (xq.getXqNum().equals(x)) {
                            data.add("小区名:" + xq.getXqName().trim() + "小区号:" + x);
                        }
                    }
                } else {
                    data.add("小区号:" + x);
                }
            }
        } catch (Exception e) {
            Log.e("limbo", e.toString());
            new SweetAlertDialog(ZCjjActivity.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("错误")
                    .setContentText("未找到相关数据!")
                    .show();
        }
        return data;

    }

    /**
     * 开始协议设定时间
     * timeMax 1 = 0.1s
     */
    private void prepareTimeStart(int timeMax) {
        timeOut = false;
        final int finalTimeMax = timeMax;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < finalTimeMax; i++) {
                        Thread.sleep(100);
                        if (timeOut) {
                            break;
                        }
                    }
                    if (!timeOut) {
                        HzyUtils.closeProgressDialog();
                        Message message = new Message();
                        message.what = 0x99;
                        mHandler.sendMessage(message);
                    }
                    resultMsg = "";
                    MenuActivity.Cjj_CB_MSG = "";
                    timeOut = true;
                } catch (Exception e) {
                    Log.d("limbo", e.toString());
                }
            }
        }).start();
    }

    public void networkConnect() {
        NetworkLayer.adapter = BluetoothAdapter.getDefaultAdapter();
        if (NetworkLayer.adapter != null) {
            MenuActivity.blutoothEnabled = NetworkLayer.adapter.isEnabled();
        }
        Toast.makeText(getApplicationContext(), "正在查询蓝牙设备", Toast.LENGTH_LONG).show();
        if (NetworkLayer.adapter == null) {
            HintDialog.ShowHintDialog(this, "无法获得蓝牙设备", "系统错误");
        } else {
            NetworkLayer.adapter.enable();
            if (NetworkLayer.adapter.getState() != BluetoothAdapter.STATE_ON) {
                Log.v("BluetoothEvent", " 等待蓝牙开启");
                try {
                    Thread.sleep(1000);//1秒后再次检测
                } catch (InterruptedException e) {
                    Log.d("limbo", e.toString());
                    e.printStackTrace();
                }
            } else {
                Intent intent = new Intent(this, BlueToothActivity.class);
                startActivityForResult(intent, 0);
            }

        }
    }

    /**
     * 使用SharePreferences保存用户信息
     */
    public void saveMsg(int radioInt) {

        SharedPreferences.Editor editor = getSharedPreferences("user_msg", MODE_PRIVATE).edit();
        editor.putInt("zcjj", radioInt);
        editor.commit();
    }


    /**
     * 加载用户信息
     */

    public void loadMsg() {
        SharedPreferences pref = getSharedPreferences("user_msg", MODE_PRIVATE);
        int i = pref.getInt("zcjj", 0);
        switch (i) {
            case 0:
                rb_1.performClick();
                break;
            case 1:
                rb_2.performClick();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        saveMsg(radioInt);
        MenuActivity.btAuto = false;
        super.onDestroy();
    }
}
