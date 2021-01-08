package com.commoncb.seck.commoncbapp.activity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.commoncb.seck.commoncbapp.DB.DbHelper;
import com.commoncb.seck.commoncbapp.R;
import com.commoncb.seck.commoncbapp.modle1.New_Meter;
import com.commoncb.seck.commoncbapp.modle1.Xq;
import com.commoncb.seck.commoncbapp.utils.BlueToothActivity;
import com.commoncb.seck.commoncbapp.utils.BluetoothConnectThread;
import com.commoncb.seck.commoncbapp.utils.HintDialog;
import com.commoncb.seck.commoncbapp.utils.HzyUtils;
import com.commoncb.seck.commoncbapp.utils.NetworkLayer;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.commoncb.seck.commoncbapp.activity.MainActivity.timeOut;
import static com.commoncb.seck.commoncbapp.activity.MenuActivity.GetXqName;
import static com.commoncb.seck.commoncbapp.activity.MenuActivity.netThread;
import static com.commoncb.seck.commoncbapp.activity.MenuActivity.sXqList;
import static java.lang.Integer.parseInt;

/**
 * Created by limbo on 2018/6/21.
 */

public class IrDACbActivity extends BaseActivity {
    @BindView(R.id.tv_xqNum)
    TextView tv_xqNum;
    @BindView(R.id.tv_cjjNum)
    TextView tv_cjjNum;
    @BindView(R.id.tv_hx)
    TextView tv_hx;
    @BindView(R.id.tv_electric)
    TextView tv_electric;
    @BindView(R.id.btn_getIrDA)
    Button btn_getIrDA;
    @BindView(R.id.btn_saveIrDA)
    Button btn_saveIrDA;
    @BindView(R.id.btn_err)
    Button btn_err;
    @BindView(R.id.et_fcStart)
    EditText et_fcStart;
    @BindView(R.id.et_fcEnd)
    EditText et_fcEnd;
    @BindView(R.id.lv_dataList)
    ListView lv_dataList;
    @BindView(R.id.rb_1)
    RadioButton rb_1;
    @BindView(R.id.rb_2)
    RadioButton rb_2;
    @BindView(R.id.rb_3)
    RadioButton rb_3;
    @BindView(R.id.rb_4)
    RadioButton rb_4;
    @BindView(R.id.tv_deBugMsg)
    TextView tv_deBugMsg;
    @BindView(R.id.sv_1)
    ScrollView sv_1;
    @BindView(R.id.ll_zccb)
    LinearLayout ll_zccb;
    int radioInt = 0;

    int lastReadData;//上月抄见
    int lastData;//上月实用
    int readData;//本月抄见 -- 红外
    int data;//本月实用
    cbStateMsg cbStateMsg = new cbStateMsg();//记录点下确认时小区号及采集机号，避免抄表中途修改

    List<String> xqList;//所有小区信息
    List<String> XqList;//用以存放筛取出的小区信息
    List<String> cjjList;//所有采集机信息
    List<String> CjjList;//用以存放筛取出的采集机信息
    List<New_Meter> meterList = new ArrayList<>();//存放抄表数据
    int xqNum = 0;//保存选择的小区序号
    int cjjNum = 0;//保存选择的采集机序号
    String xqNumMsg = "";
    String cjjNumMsg = "";
    int X_FLAG = 0;
    String XH;
    String sendMsg = "";
    String resultMsg = "";
    String cbStyle = "E";
    private int POSITION;
    public static int selectionXq = 0;
    public static int selectionCjj = 0;
    private int POSITIONX;//修改项坐标

    public List<String> dataList = new ArrayList<>();//线程列表
    private ListAdapter ad;
    List<String> xlist;//存放小区号列表
    boolean flag = false;//记录退出时是否更新表列表

    public static String deBugMsg = "";
    String errMsg = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_irdacb);
        ButterKnife.bind(this);
        Bundle bundle = getIntent().getExtras();
        XH = bundle.getString("XH");
        MenuActivity.btAuto = true;
        sv_1.setVisibility(View.GONE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (MenuActivity.btAuto) {
                    try {
                        Thread.sleep(500);
                        resultMsg = MenuActivity.Cjj_CB_MSG.toLowerCase().replaceAll("0x", "").replaceAll(" ", "");
                        if (resultMsg.contains("fbfb")) {
                            resultMsg = resultMsg.substring(resultMsg.indexOf("fbfb"));
                        } else if (resultMsg.contains("2a7b7b7b")) {
                            resultMsg = resultMsg.substring(resultMsg.indexOf("2a7b7b7b"));
                        }
                        if (resultMsg.length() > 0) {
                            if (resultMsg.length() > 0 && resultMsg.contains("2a7b7b7b") && resultMsg.contains("5d5d5d")) {

                                Log.d("limbo", "get:" + resultMsg);
                                if (resultMsg.contains("fbfb")) {
                                    resultMsg = resultMsg.substring(resultMsg.indexOf("fbfb"), resultMsg.indexOf("5d5d5d") + 6);
                                } else {
                                    resultMsg = resultMsg.substring(resultMsg.indexOf("2a7b7b7b"), resultMsg.indexOf("5d5d5d") + 6);
                                }
                                Message message = new Message();
                                if (cbStyle.equals("EA")) {

                                } else if (cbStyle.equals("ZC")) {
                                    xqNumMsg = XqList.get(xqNum);
                                } else {
                                    xqNumMsg = XqList.get(xqNum);
                                    cjjNumMsg = CjjList.get(cjjNum);
                                }

                                message.obj = resultMsg;
                                message.what = 0x00;
                                mHandler.sendMessage(message);
                                resultMsg = "";
                                MenuActivity.Cjj_CB_MSG = "";
                            } else if (resultMsg.length() > 0 && resultMsg.contains("2aa3a3a3") && resultMsg.contains("5d5d5d")) {
                                resultMsg = resultMsg.replace("5b5b5b3f3fa3a3a399999999643f5d5d5d", "");
                                Log.d("limbo", "get:" + resultMsg);
                                resultMsg = resultMsg.substring(resultMsg.indexOf("2aa3a3a3"), resultMsg.indexOf("5d5d5d") + 6);
                                Message message = new Message();
                                xqNumMsg = resultMsg.substring(resultMsg.indexOf("2aa3a3a3") + 18, resultMsg.indexOf("2aa3a3a3") + 22);
                                cjjNumMsg = resultMsg.substring(resultMsg.indexOf("2aa3a3a3") + 22, resultMsg.indexOf("2aa3a3a3") + 26);
                                cbStateMsg.setXq(xqNumMsg);
                                cbStateMsg.setCjj(cjjNumMsg);
                                message.obj = resultMsg;
                                message.what = 0x01;
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
        if (sXqList == null || sXqList.size() == 0) {
            GetXqName();
        }
        if (xlist.size() == 0) {
            Toast.makeText(IrDACbActivity.this, "该线号下无红外表", Toast.LENGTH_LONG).show();
            finish();
        }
        rb_1.setOnClickListener(new View.OnClickListener() {//E型
            @Override
            public void onClick(View v) {
                cbStyle = "E";
                radioInt = 0;
                ll_zccb.setVisibility(View.GONE);
            }
        });
        rb_2.setOnClickListener(new View.OnClickListener() {//A型
            @Override
            public void onClick(View v) {
                cbStyle = "A";
                radioInt = 1;
                ll_zccb.setVisibility(View.GONE);
            }
        });
        rb_3.setOnClickListener(new View.OnClickListener() {//E一键抄表
            @Override
            public void onClick(View v) {
                cbStyle = "EA";
                radioInt = 2;
                ll_zccb.setVisibility(View.GONE);
            }
        });
        rb_4.setOnClickListener(new View.OnClickListener() {//总采
            @Override
            public void onClick(View v) {
                cbStyle = "ZC";
                radioInt = 3;
                ll_zccb.setVisibility(View.VISIBLE);
            }
        });
        loadMsg();
        String[] arr = xlist.toArray(new String[xlist.size()]);
        ArrayAdapter<String> ad_1 = new ArrayAdapter<String>(IrDACbActivity.this, R.layout.spinner_item, arr) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                textView.setTextColor(IrDACbActivity.this.getResources().getColor(R.color.white));
                return textView;
            }
        };

        ad_1.setDropDownViewResource(R.layout.spinner_item);

        btn_getIrDA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (netThread == null) {
                    X_FLAG = 1;
                    MenuActivity.resetNetwork();
                    networkConnect();
                    return;
                } else {
                    tv_deBugMsg.setText("");
                    deBugMsg = "";
                    cbStateMsg = new cbStateMsg();
                    if (cbStyle.equals("EA")) {
                        prepareTimeStart(150);
                        HzyUtils.showProgressDialog(IrDACbActivity.this);
                        cbs();
                    } else if (cbStyle.equals("ZC")) {//总采抄表
                        zccb();
                    } else {
                        sureMsg();
                    }
                }
            }
        });

        lv_dataList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (dataList != null && dataList.size() > 0) {
                    X_FLAG = 2;
                    Intent i = new Intent(IrDACbActivity.this, ChangeDataActivity.class);
                    i.putExtra("data", meterList.get(position).getReadData());
                    POSITIONX = position;
                    startActivityForResult(i, 0);

                }
            }
        });
        btn_saveIrDA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (meterList.size() == 0) {
                    Toast.makeText(IrDACbActivity.this, "请先抄表", Toast.LENGTH_LONG).show();
                    return;
                }
                if (cbStyle.equals("ZC")) {
                    Calendar c = Calendar.getInstance();
                    Date d = c.getTime();
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                    for (int i = 0; i < meterList.size(); i++) {
                        DbHelper.IrDAMeterSave(meterList.get(i), df.format(d));
                    }
                    Toast.makeText(IrDACbActivity.this, "保存成功", Toast.LENGTH_LONG).show();
                    Intent resultIntent = new Intent();
                    Bundle bundle = new Bundle();
                    String h = meterList.get(0).getKH();
                    bundle.putString("result", h);
                    resultIntent.putExtras(bundle);
                    IrDACbActivity.this.setResult(2, resultIntent);
                    IrDACbActivity.this.finish();
                } else {
                    new SweetAlertDialog(IrDACbActivity.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("提示")
                            .setContentText("请选择操作方式")
                            .setCancelText("仅保存")
                            .setConfirmText("保存并查看")
                            .showCancelButton(true)
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    Log.d("limboV", "sure");
                                    sweetAlertDialog.cancel();
                                    Calendar c = Calendar.getInstance();
                                    Date d = c.getTime();
                                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                                    if (cbStyle.equals("EA")) {
                                        for (int i = 0; i < meterList.size(); i++) {
                                            Log.d("limbo", meterList.get(i).getCbFlag());
                                            DbHelper.newIrDAChangeUser(XH,
                                                    cbStateMsg.getXq() ,
                                                    cbStateMsg.getCjj() ,
                                                    meterList.get(i).getMeterNum() + "",
                                                    meterList.get(i),
                                                    df.format(d));
                                        }
                                    } else {
                                        for (int i = 0; i < meterList.size(); i++) {
                                            Log.d("limbo", meterList.get(i).getCbFlag());
                                            DbHelper.newIrDAChangeUser(XH,
                                                    cbStateMsg.getXq() ,
                                                    cbStateMsg.getCjj() ,
                                                    meterList.get(i).getMeterNum() + "",
                                                    meterList.get(i),
                                                    df.format(d));
                                        }
                                    }


                                    Toast.makeText(IrDACbActivity.this, "保存成功", Toast.LENGTH_LONG).show();


                                    Intent resultIntent = new Intent();
                                    Bundle bundle = new Bundle();
                                    String h = meterList.get(0).getKH();
                                    bundle.putString("result", h);
                                    resultIntent.putExtras(bundle);
                                    IrDACbActivity.this.setResult(2, resultIntent);
                                    IrDACbActivity.this.finish();
                                }
                            })
                            .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    Log.d("limboV", "cancel");
                                    sDialog.cancel();
                                    Calendar c = Calendar.getInstance();
                                    Date d = c.getTime();
                                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    if (cbStyle.equals("EA")) {
                                        for (int i = 0; i < meterList.size(); i++) {
                                            Log.d("limbo", meterList.get(i).getCbFlag());
                                            DbHelper.newIrDAChangeUser(XH,
                                                    cbStateMsg.getXq() ,
                                                    cbStateMsg.getCjj() ,
                                                    meterList.get(i).getMeterNum() + "",
                                                    meterList.get(i),
                                                    df.format(d));
                                        }
                                    } else {
                                        for (int i = 0; i < meterList.size(); i++) {
                                            Log.d("limbo", meterList.get(i).getCbFlag());
                                            DbHelper.newIrDAChangeUser(XH,
                                                    cbStateMsg.getXq() ,
                                                    cbStateMsg.getCjj() ,
                                                    meterList.get(i).getMeterNum() + "",
                                                    meterList.get(i),
                                                    df.format(d));
                                        }
                                    }
                                    //                                NewCbActivity.mNew_meters = DbHelper.getKyMeterByXH(XH);
                                    Toast.makeText(IrDACbActivity.this, "保存成功", Toast.LENGTH_LONG).show();
                                }
                            }).show();
                }

            }
        });
        /**
         * 上报错误信息
         */
        btn_err.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!HzyUtils.isEmpty(errMsg)) {
                    TelephonyManager tm = (TelephonyManager) IrDACbActivity.this.getSystemService(Context.TELEPHONY_SERVICE);
                    if (ActivityCompat.checkSelfPermission(IrDACbActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        ActivityCompat.requestPermissions(IrDACbActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
                        return;
                    }
                    String imei = tm.getDeviceId();//获得SIM卡的序号-国际移动用户识别码。
                    if (HzyUtils.isEmpty(imei)) {
                        imei = "";
                    }
                    err(MainActivity.user, imei, errMsg);
                } else {
                    HintDialog.ShowHintDialog(IrDACbActivity.this, "请先抄表", "请先抄表");
                }
            }
        });
    }

    private void err(final String user, final String imei, final String msg) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d("limbo", "上报错误");
                    OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象
                    Request request = new Request.Builder()
                            .url(MainActivity.url + "/PhoneLog?UserID=" + user + "&imei=" + imei + "&msg=" + msg)//请求接口。如果需要传参拼接到接口后面。
                            .build();//创建Request 对象
                    Response response = null;
                    response = client.newCall(request).execute();//得到Response 对象
                    if (response.isSuccessful()) {
                        Log.d("limboV", "response.code()==" + response.code());
                        Log.d("limboV", "response.message()==" + response.message());
                        //                        Log.d("limboV", "res==" + response.body().string());
                        //此时的代码执行在子线程，修改UI的操作请使用handler跳转到UI线程。
                        String responseX = response.body().string().replace("<?xml version=\"1.0\" encoding=\"utf-8\"?>", "");
                        responseX = responseX.replace("<string xmlns=\"http://tempuri.org/\">", "");
                        responseX = responseX.replace("</string>", "");
                        Log.d("limbo", responseX);
                        if (responseX.contains("fail")) {
                            Toast.makeText(IrDACbActivity.this, "上报错误接口出错，请稍后重试。", Toast.LENGTH_LONG).show();
                            Log.d("limbo", "fail");
                        } else {
                            Message message = new Message();
                            message.what = 0x20;
                            mHandler.sendMessage(message);
                        }

                    } else {
                        Message message = new Message();
                        message.what = 0x98;
                        mHandler.sendMessage(message);
                    }
                } catch (Exception e) {
                    Log.d("limbo", e.toString());
                    Message message = new Message();
                    message.what = 0x98;
                    mHandler.sendMessage(message);

                }
            }
        }).start();
    }

    private void zccb() {
        if (HzyUtils.isEmpty(et_fcStart.getText().toString()) || HzyUtils.isEmpty(et_fcEnd.getText().toString())) {
            Toast.makeText(IrDACbActivity.this, "起始及结束采集机号不可为空", Toast.LENGTH_LONG).show();
            return;
        }
        final int cjjStart = Integer.parseInt(et_fcStart.getText().toString());//起始采集机号
        final int cjjEnd = Integer.parseInt(et_fcEnd.getText().toString());//结束采集机号
        if (cjjEnd < cjjStart) {
            Toast.makeText(IrDACbActivity.this, "起始采集机号应小于结束采集机号", Toast.LENGTH_LONG).show();
            return;
        }
        //选择小区
        final AlertDialog.Builder builder = new AlertDialog.Builder(IrDACbActivity.this);
        // 通过LayoutInflater来加载一个xml的布局文件作为一个View对象
        View view = LayoutInflater.from(IrDACbActivity.this).inflate(R.layout.dialog_msg3, null);
        // 设置我们自己定义的布局文件作为弹出框的Content
        builder.setView(view);
        //这个位置十分重要，只有位于这个位置逻辑才是正确的
        final AlertDialog dialog = builder.show();

        final Spinner sp1 = (Spinner) view.findViewById(R.id.sp1);

        String[] arr = xlist.toArray(new String[xlist.size()]);
        ArrayAdapter<String> ad_1 = new ArrayAdapter<String>(IrDACbActivity.this, R.layout.spinner_item, arr) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                textView.setTextColor(IrDACbActivity.this.getResources().getColor(R.color.white));
                return textView;
            }
        };

        ad_1.setDropDownViewResource(R.layout.spinner_item);
        sp1.setAdapter(ad_1);
        sp1.setSelection(selectionXq);
        /**
         * 实现选择项事件(使用匿名类实现接口)
         */
        sp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            /**
             * 选中某一项时
             */
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                xqNum = position;
                selectionXq = position;
                Log.d("limbo", "xqNum" + XqList.get(position));

                cbStateMsg.setXq(XqList.get(position));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        view.findViewById(R.id.btn_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //确认
                dialog.dismiss();
                Log.d("limbo", "小区号:" + XqList.get(xqNum) + "起始采集机号:" + cjjStart + "结束采集机号:" + cjjEnd);

                getCjj(XH, XqList.get(xqNum));

                prepareTimeStart(200 + (cjjEnd - cjjStart) * 90);
                if (meterList != null) {
                    meterList.clear();
                }
                dataList.clear();
                ad = new ArrayAdapter(IrDACbActivity.this, R.layout.list_textview, dataList) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        return super.getView(position, convertView, parent);
                    }
                };
                lv_dataList.setAdapter(ad);


                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Message message = new Message();
                            message.what = 0x02;
                            mHandler.sendMessage(message);
                            for (int i = 0; i < 300; i++) {
                                Thread.sleep(25);
                                MenuActivity.sendCmd("55");
                            }
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            message = new Message();
                            message.what = 0x03;
                            mHandler.sendMessage(message);
                            for (int i = 0; i <= (cjjEnd - cjjStart); i++) {

                                message = new Message();
                                message.what = 0x02;
                                mHandler.sendMessage(message);
                                //                            for (int i = 0; i <= 5; i++) {
                                resultMsg = "";
                                MenuActivity.Cjj_CB_MSG = "";
                                cjjNumMsg = (cjjStart + i) + "";
                                String cjjid, hid, fid;//hid:户号,fid:户号取反
                                cjjid = HzyUtils.isLength((cjjStart + i) + "", 4);
                                //                                cjjid = HzyUtils.isLength((cjjStart) + "", 4);
                                cjjid = HzyUtils.isLength(HzyUtils.toHexString(cjjid), 4);//将站号转为2字节，16进制
                                Log.d("limbo", cjjid);
                                int x = Integer.parseInt(cjjid.substring(2, 4), 16);//低位字节站号
                                fid = HzyUtils.isLength(HzyUtils.toHexString(255 - x + ""), 2);//低位字节站号反码
                                Log.d("limbo", fid);
                                hid = cjjid.substring(0, 2);
                                String A = hid +
                                        "010000";
                                String checkNum = HzyUtils.countSum(A);
                                sendMsg = "ffffffffffffffffff" + "5b5b5b3f3f" +
                                        "fafafa" + A + checkNum +
                                        "7b7b7b"
                                        + cjjid.substring(2, 4)
                                        + fid + "3f5d5d5d";
                                resultMsg = "";
                                MenuActivity.Cjj_CB_MSG = "";
                                MenuActivity.sendCmd(sendMsg.toLowerCase());
                                Thread.sleep(9000);
                            }
                            message = new Message();
                            message.what = 0x03;
                            mHandler.sendMessage(message);

                        } catch (Exception e) {
                            Log.d("limbo", e.toString());
                            e.printStackTrace();
                        }
                    }
                }).start();


            }
        });
        view.findViewById(R.id.btn_cancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //取消+关闭对话框

                //写相关的服务代码

                dialog.dismiss();
            }
        });
    }

    private void sureMsg() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(IrDACbActivity.this);
        // 通过LayoutInflater来加载一个xml的布局文件作为一个View对象
        View view = LayoutInflater.from(IrDACbActivity.this).inflate(R.layout.dialog_msg, null);
        // 设置我们自己定义的布局文件作为弹出框的Content
        builder.setView(view);
        //这个位置十分重要，只有位于这个位置逻辑才是正确的
        final AlertDialog dialog = builder.show();

        final Spinner sp1 = (Spinner) view.findViewById(R.id.sp1);
        final Spinner sp2 = (Spinner) view.findViewById(R.id.sp2);

        String[] arr = xlist.toArray(new String[xlist.size()]);
        ArrayAdapter<String> ad_1 = new ArrayAdapter<String>(IrDACbActivity.this, R.layout.spinner_item, arr) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                textView.setTextColor(IrDACbActivity.this.getResources().getColor(R.color.white));
                return textView;
            }
        };

        ad_1.setDropDownViewResource(R.layout.spinner_item);
        sp1.setAdapter(ad_1);
        sp1.setSelection(selectionXq);
        /**
         * 实现选择项事件(使用匿名类实现接口)
         */
        sp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            /**
             * 选中某一项时
             */
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                xqNum = position;
                selectionXq = position;
                Log.d("limbo", "xqNum" + XqList.get(position));
                selectionCjj = 0;

                List<String> ylist = getCjj(XH, XqList.get(xqNum));//采集机列表
                if (CjjList != null && CjjList.size() > 0) {
                    String[] arr2 = ylist.toArray(new String[ylist.size()]);
                    ArrayAdapter<String> ad_2 = new ArrayAdapter<String>(IrDACbActivity.this,
                            R.layout.spinner_item, arr2) {
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            TextView textView = (TextView) super.getView(position, convertView, parent);
                            textView.setTextColor(IrDACbActivity.this.getResources().getColor(R.color.white));
                            return textView;
                        }
                    };

                    ad_2.setDropDownViewResource(R.layout.spinner_item);
                    sp2.setAdapter(ad_2);
                } else {
                    Toast.makeText(IrDACbActivity.this, "该线号下无红外表", Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        List<String> ylist = getCjj(XH, XqList.get(selectionXq));//采集机列表
        if (ylist != null && ylist.size() > 0) {
            String[] arr2 = ylist.toArray(new String[ylist.size()]);
            ArrayAdapter<String> ad_2 = new ArrayAdapter<String>(IrDACbActivity.this,
                    R.layout.spinner_item, arr2) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    TextView textView = (TextView) super.getView(position, convertView, parent);
                    textView.setTextColor(IrDACbActivity.this.getResources().getColor(R.color.white));
                    return textView;
                }
            };

            ad_2.setDropDownViewResource(R.layout.spinner_item);
            sp2.setAdapter(ad_2);
            if (selectionCjj < CjjList.size()) {
                sp2.setSelection(selectionCjj);
            } else {
                sp2.setSelection(0);
            }

            /**
             * 实现选择项事件(使用匿名类实现接口)
             */
            sp2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Log.d("limbo", "cjjNum" + CjjList.get(position));
                    cjjNum = position;
                    selectionCjj = position;
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        } else {
            Toast.makeText(IrDACbActivity.this, "该线号下无红外表", Toast.LENGTH_LONG).show();
            dialog.dismiss();
        }

        view.findViewById(R.id.btn_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //确认
                dialog.dismiss();
                if (cbStyle.equals("E")) {
                    prepareTimeStart(200);
                } else if (cbStyle.equals("EA")) {
                    prepareTimeStart(140);
                } else {
                    prepareTimeStart(60);
                }
                getCjj(XH, XqList.get(xqNum));
                HzyUtils.showProgressDialog(IrDACbActivity.this);
                Log.d("limbo", "选择了第几个选项：" + cjjNum + "采集机号为：" + CjjList.get(cjjNum) + "");
                cbStateMsg.setXq(XqList.get(xqNum));
                cbStateMsg.setCjj(CjjList.get(cjjNum));
                cb(cbStateMsg.getXq() , cbStateMsg.getCjj() );
            }
        });
        view.findViewById(R.id.btn_cancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //取消+关闭对话框

                //写相关的服务代码

                dialog.dismiss();
            }
        });

    }

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
                if (sXqList != null && sXqList.size() > 0) {
                    for (Xq xq : MenuActivity.sXqList) {
                        if (xq.getXqNum().trim().equals(x.trim())) {
                            Log.d("limbo", "小区名:" + xq.getXqName().trim() + "小区号:" + x);
                            data.add("小区名:" + xq.getXqName().trim() + "小区号:" + x);
                        }
                    }
                } else {
                    Log.d("limbo", "小区号:" + x);
                    data.add("小区号:" + x);
                }

            }

        } catch (Exception e) {
            Log.e("limbo", e.toString());
            new SweetAlertDialog(IrDACbActivity.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("错误")
                    .setContentText("未找到相关数据!")
                    .show();
        }
        return data;

    }

    /**
     * 筛取采集机
     */
    private List<String> getCjj(String XH, String XqNum) {
        List<String> data = new ArrayList<>();
        try {
            cjjList = DbHelper.getAllCjjNum(XH, XqNum);//从数据库提取出
            int flag;
            CjjList = new ArrayList<>();
            for (int i = 0; i < cjjList.size(); i++) {
                flag = 0;
                if (CjjList.size() == 0) {
                    CjjList.add(cjjList.get(i));
                } else {
                    for (int j = 0; j < CjjList.size(); j++) {
                        if (CjjList.get(j).toString().equals(cjjList.get(i).toString())) {
                        } else {
                            flag++;
                        }
                    }
                    if (flag == CjjList.size()) {
                        CjjList.add(cjjList.get(i));
                    }

                }
            }
            for (String x : CjjList) {
                data.add("采集机号:" + x);
            }
        } catch (Exception e) {
            Log.e("limbo", e.toString());
            new SweetAlertDialog(IrDACbActivity.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("错误")
                    .setContentText("未找到相关数据!")
                    .show();
        }
        return data;

    }


    private void cbs() {
        if (meterList != null) {
            meterList.clear();
        }
        dataList.clear();
        ad = new ArrayAdapter(IrDACbActivity.this, R.layout.list_textview, dataList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                return super.getView(position, convertView, parent);
            }
        };
        lv_dataList.setAdapter(ad);
        resultMsg = "";
        MenuActivity.Cjj_CB_MSG = "";
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < 450; i++) {
                        MenuActivity.sendCmd("55");
                        Log.d(BaseActivity.TAG,"55");
                    }

                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (cbStyle.equals("EA")) {
                        reSendCmd(55);
                    }
                    resultMsg = "";
                    MenuActivity.Cjj_CB_MSG = "";
                    MenuActivity.sendCmd("5b5b5b3f3fa3a3a399999999" + HzyUtils.countSum("99999999") + "3f5d5d5d");

                    Log.d(BaseActivity.TAG,"5b5b5b3f3fa3a3a399999999" + HzyUtils.countSum("99999999") + "3f5d5d5d");

                } catch (Exception e) {
                    Log.d("limbo", e.toString());
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void cb(final String xqNum, final String cjjNum) {
        if (xqNum.trim().equals("") || cjjNum.trim().equals("")) {
            Toast.makeText(IrDACbActivity.this, "请选择小区号及采集机号", Toast.LENGTH_LONG).show();
            return;
        }

        if (meterList != null) {
            meterList.clear();
        }
        dataList.clear();
        ad = new ArrayAdapter(IrDACbActivity.this, R.layout.list_textview, dataList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                return super.getView(position, convertView, parent);
            }
        };
        lv_dataList.setAdapter(ad);

        resultMsg = "";
        MenuActivity.Cjj_CB_MSG = "";
        String cjjid, hid, fid;//hid:户号,fid:户号取反
        cjjid = HzyUtils.isLength(cjjNum + "", 4);
        //        cjjid = "0061";
        cjjid = HzyUtils.isLength(HzyUtils.toHexString(cjjid), 4);//将站号转为2字节，16进制
        Log.d("limbo", cjjid);
        int x = Integer.parseInt(cjjid.substring(2, 4), 16);//低位字节站号
        fid = HzyUtils.isLength(HzyUtils.toHexString(255 - x + ""), 2);//低位字节站号反码
        Log.d("limbo", fid);
        hid = cjjid.substring(0, 2);
        String A = hid +
                "010000";
        String checkNum = HzyUtils.countSum(A);
        //        sendMsg = "ffffffffffffffffff" +
        if (cbStyle.equals("E") || cbStyle.equals("EA")) {
            sendMsg = "ffffffffffffffffff" + "5b5b5b3f3f" +
                    "fafafa" + A + checkNum +
                    "7b7b7b"
                    + cjjid.substring(2, 4)
                    + fid + "3f5d5d5d";
        } else {
            sendMsg = "ffffffffffffffffff" + "5b5b5b3f3f" +
                    "7b7b7b"
                    + cjjid.substring(2, 4)
                    + fid + "3f5d5d5d";
        }


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (cbStyle.equals("E")) {
                        for (int i = 0; i < 450; i++) {
                            MenuActivity.sendCmd("55");
                            Log.d(BaseActivity.TAG,"55");
                        }
                        try {
                            Thread.sleep(5400);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        reSendCmd(100);
                    } /*else if (cbStyle.equals("EA")) {
                        for (int i = 0; i < 450; i++) {
                            MenuActivity.sendCmd("55");
                        }
                        try {
                            Thread.sleep(6000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        reSendCmd(40);
                    } */ else {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    resultMsg = "";
                    MenuActivity.Cjj_CB_MSG = "";
                    Log.d(BaseActivity.TAG,sendMsg.toLowerCase());
                    MenuActivity.sendCmd(sendMsg.toLowerCase());


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
                        //                        tv_deBugMsg.setText("成功接收:" + result);
                        String xqNum = cbStateMsg.getXq() ;
                        String cjjNum = cbStateMsg.getCjj();
                        Log.d("limbo", result);
                        errMsg = result;
                        /*Calendar c = Calendar.getInstance();
                        Date d = c.getTime();
                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");*/
                        if (result.contains("fbfb")) {
                            Log.d("limbo", "新协议");
                            //含标志B
                            /**
                             * 添加CRC16校验位
                             */
                            String crc16MSG = result.substring(result.indexOf("7b7b7b") + 6, result.indexOf("5d5d5d") - 4);
                            String crc16 = HzyUtils.countSum1(crc16MSG);//计算返回数据的CRC16值
                            String crc16back = result.substring(result.indexOf("5d5d5d") - 4, result.indexOf("5d5d5d"));
                            if (!crc16.equals(crc16back)) {
                                //CRC16校验失败
                                Log.d("limbo", "CRC16校验失败:计算值" + crc16 + "  返回" + crc16back);
                                HintDialog.ShowHintDialog(IrDACbActivity.this, "校验错误请重新抄表", "通讯异常");
                            } else {
                                Log.d("limbo", "CRC16校验成功:计算值" + crc16 + "  返回" + crc16back);
                                cjjNum = Integer.parseInt(result.substring(result.indexOf("fbfb") + 4, result.indexOf("fbfb") + 6)
                                        + HzyUtils.isLength("" + Integer.parseInt(result.substring(result.indexOf("fbfb") + 24, result.indexOf("fbfb") + 26), 16), 2)) + "";//采集机号
                                cbStateMsg.setCjj(cjjNum);
                                String num = result.substring(result.indexOf("fbfb") + 10, result.indexOf("fbfb") + 12);//户数
                                String electric = result.substring(result.indexOf("fbfb") + 8, result.indexOf("fbfb") + 10);//电压
                                electric = HzyUtils.isLength(parseInt(electric, 16) + "", 3);
                                electric = parseInt(electric.substring(0, 2)) + "." + electric.substring(2, 3);
                                String gd = result.substring(result.indexOf("fbfb") + 12, result.indexOf("fbfb") + 14);//表度数量
                                //                                int meterLen = parseInt(gd.substring(0, 1)) * 2;//每表字符数
                                int meterLen = 6;//每表字符数
                                int errLen = parseInt(gd.substring(1, 2));//错误字节数
                                Log.d("limbo", "每表字符数：" + meterLen + "\n错误字节数：" + errLen);
                                int userNum = parseInt(num);
                                int start = result.indexOf("7b7b7b");
                                String allData = result.substring(start + 10, start + 10 + userNum * meterLen);//数据区
                                Log.d("limbo", "户数：" + num + "\n数据区:" + allData);

                                /**
                                 * 判断是否解析正确
                                 */
                                if (!TextUtils.isDigitsOnly(allData)) {
                                    Toast.makeText(IrDACbActivity.this, "抄读失败请重试，数据区长度不足", Toast.LENGTH_LONG).show();
                                    break;
                                }
                                tv_xqNum.setText("小区号:" + xqNum);
                                tv_cjjNum.setText("集中器号:" + cjjNum);
                                tv_hx.setText("户   型:" + num + "户");
                                tv_electric.setText("电池电压:" + electric + "V");
                                for (int i = 0; i < userNum * meterLen; i = i + meterLen) {
                                    POSITION = i / meterLen;
                                    String cutData = allData.substring(POSITION * meterLen, POSITION * meterLen + meterLen);
                                    New_Meter nb = getSingleMeter(xqNum.trim(), cjjNum.trim(), (POSITION + 1) + "");
                                    if (nb != null) {
                                        errMsg = errMsg + "\n找到第" + i + "只表";
                                        lastReadData = parseInt(nb.getLastReadData());//上月实用
                                        lastData = parseInt(nb.getLastData());//上月抄见
                                        readData = parseInt(cutData.substring(0, meterLen - 1));//本月抄见 -- 红外
                                        data = readData - lastData;//本月实用

                                        nb.setReadData(readData + "");
                                        nb.setIrDAData(readData + "");
                                        nb.setData(data + "");
                                        nb.setLastData(lastData + "");
                                        nb.setLastReadData(lastReadData + "");
                                        nb.setCbFlag("已抄");
                                        nb.setUploadFlag("未上传");

                                        try {
                                            float lastMouthData = lastReadData;
                                            float thisMouthData = data;
                                            Log.d("limbo", "本月实用：" + thisMouthData);
                                            Log.d("limbo", "上月实用：" + lastMouthData);
                                            if (lastMouthData <= 0) {
                                                if (thisMouthData > 50) {
                                                    nb.setMeterState("1");
                                                } else if (thisMouthData < 0) {
                                                    nb.setMeterState("2");
                                                } else {
                                                    nb.setMeterState("0");
                                                }
                                            } else {//lastMouthData>0
                                                if (thisMouthData <= 0) {
                                                    nb.setMeterState("2");
                                                } else if (thisMouthData > 0) {
                                                    if (lastMouthData <= 5 && lastMouthData > 0) {
                                                        if ((thisMouthData / lastMouthData) <= 4) {
                                                            nb.setMeterState("0");
                                                        } else if ((thisMouthData / lastMouthData) == 0) {
                                                            nb.setMeterState("2");
                                                        } else {
                                                            nb.setMeterState("1");
                                                        }
                                                    } else if (lastMouthData <= 10 && lastMouthData > 5) {
                                                        if ((thisMouthData / lastMouthData) <= 3 && (thisMouthData / lastMouthData) > 0.4) {
                                                            nb.setMeterState("0");
                                                        } else if ((thisMouthData / lastMouthData) < 0.4) {
                                                            nb.setMeterState("2");
                                                        } else if ((thisMouthData / lastMouthData) > 3) {
                                                            nb.setMeterState("1");
                                                        }
                                                    } else if (lastMouthData <= 15 && lastMouthData > 10) {
                                                        if ((thisMouthData / lastMouthData) <= 2 && (thisMouthData / lastMouthData) > 0.4) {
                                                            nb.setMeterState("0");
                                                        } else if ((thisMouthData / lastMouthData) < 0.4) {
                                                            nb.setMeterState("2");
                                                        } else if ((thisMouthData / lastMouthData) > 2) {
                                                            nb.setMeterState("1");
                                                        }
                                                    } else if (lastMouthData <= 40 && lastMouthData > 15) {
                                                        if ((thisMouthData / lastMouthData) <= 1.6 && (thisMouthData / lastMouthData) > 0.4) {
                                                            nb.setMeterState("0");
                                                        } else if ((thisMouthData / lastMouthData) < 0.4) {
                                                            nb.setMeterState("2");
                                                        } else if ((thisMouthData / lastMouthData) > 1.6) {
                                                            nb.setMeterState("1");
                                                        }
                                                    } else if (lastMouthData > 40) {
                                                        if ((thisMouthData / lastMouthData) <= 1.3 && (thisMouthData / lastMouthData) > 0.7) {
                                                            nb.setMeterState("0");
                                                        } else if ((thisMouthData / lastMouthData) < 0.7) {
                                                            nb.setMeterState("2");
                                                        } else if ((thisMouthData / lastMouthData) > 1.3) {
                                                            nb.setMeterState("1");
                                                        }
                                                    }
                                                }
                                            }

                                        } catch (Exception e) {
                                            errMsg = errMsg + "\n第" + i + "只表error:" + e.toString();
                                            Log.d("limbo", e.toString());
                                        }
                                        dataList.add("第" + (POSITION + 1) + "户： "
                                                + "\n户号:" + nb.getHH()
                                                + "\n表号:" + nb.getMeterNumber()
                                                + "\n本月抄见:" + readData
                                                + "\n上月抄见:" + lastData
                                                + "\n本月实用:" + data
                                                + "\n上月实用:" + lastReadData);
                                        meterList.add(nb);
                                    } else {
                                        Log.d("limbo", "数据库中未找到   小区号:" + xqNum + " 采集机号:" + cjjNum + " 表序号:" + (POSITION + 1) + " 的表");

                                    }
                                }

                                ad = new ArrayAdapter(IrDACbActivity.this, R.layout.list_textview, dataList) {
                                    @Override
                                    public View getView(int position, View convertView, ViewGroup parent) {
                                        return super.getView(position, convertView, parent);
                                    }
                                };
                                lv_dataList.setAdapter(ad);
                                Toast.makeText(IrDACbActivity.this, "完成", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Log.d("limbo", "老协议");
                            //不含标志B
                            /**
                             * 添加CRC16校验位
                             */
                            String crc16MSG = result.substring(result.indexOf("7b7b7b") + 6, result.indexOf("5d5d5d") - 4);
                            String crc16 = HzyUtils.countSum1(crc16MSG);//计算返回数据的CRC16值
                            String crc16back = result.substring(result.indexOf("5d5d5d") - 4, result.indexOf("5d5d5d"));
                            if (!crc16.equals(crc16back)) {
                                //CRC16校验失败
                                Log.d("limbo", "CRC16校验失败:计算值" + crc16 + "  返回" + crc16back);
                                HintDialog.ShowHintDialog(IrDACbActivity.this, "校验错误请重新抄表", "通讯异常");
                            } else {
                                Log.d("limbo", "CRC16校验成功:计算值" + crc16 + "  返回" + crc16back);
                                cjjNum = Integer.parseInt(result.substring(result.indexOf("7b7b7b") + 6, result.indexOf("7b7b7b") + 8), 16) + "";//采集机号
                                cbStateMsg.setCjj(cjjNum);
                                String num = result.substring(result.length() - 12, result.length() - 10);//户数
                                int userNum = parseInt(num, 16);
                                int start = result.indexOf("7b7b7b");
                                String allData = result.substring(start +
                                        10, start + 10 + userNum * 6);//数据区
                                Log.d("limbo", "户数：" + userNum + "\n数据区:" + allData);
                                /*if (userNum == 24) {
                                    if (result.length() != (30 + userNum * 6 + 6)) {
                                        Log.d("limbo", "长度错误");
                                        HintDialog.ShowHintDialog(IrDACbActivity.this, "长度错误请重新抄表", "长度错误");
                                        return;
                                    }
                                } else {
                                    if (result.length() != (30 + userNum * 6 + 4) || result.length() != (30 + 24 * 6 + 6)) {
                                        Log.d("limbo", "长度错误");
                                        HintDialog.ShowHintDialog(IrDACbActivity.this, "长度错误请重新抄表", "长度错误");
                                        return;
                                    }
                                }*/

                                /**
                                 * 判断是否解析正确
                                 */
                                if (!TextUtils.isDigitsOnly(allData)) {
                                    Toast.makeText(IrDACbActivity.this, "抄读失败请重试，数据区长度不足", Toast.LENGTH_LONG).show();
                                    break;
                                }
                                tv_xqNum.setText("小区号:" + xqNum);
                                tv_cjjNum.setText("集中器号:" + cjjNum);
                                tv_hx.setText("户   型:" + userNum + "户");

                                int meterLen = 6;
                                for (int i = 0; i < userNum * meterLen; i = i + meterLen) {
                                    POSITION = i / meterLen;
                                    String cutData = allData.substring(POSITION * meterLen, POSITION * meterLen + meterLen);
                                    New_Meter nb = getSingleMeter(xqNum, cjjNum, (POSITION + 1) + "");
                                    if (nb != null) {
                                        lastReadData = parseInt(nb.getLastReadData());//上月实用
                                        lastData = parseInt(nb.getLastData());//上月抄见
                                        readData = parseInt(cutData.substring(0, 5));//本月抄见 -- 红外
                                        data = readData - lastData;//本月实用

                                        nb.setReadData(readData + "");
                                        nb.setIrDAData(readData + "");
                                        nb.setData(data + "");
                                        nb.setLastData(lastData + "");
                                        nb.setLastReadData(lastReadData + "");
                                        nb.setCbFlag("已抄");
                                        nb.setUploadFlag("未上传");
                                        try {
                                            float lastMouthData = lastReadData;
                                            float thisMouthData = data;
                                            Log.d("limbo", "本月实用：" + thisMouthData);
                                            Log.d("limbo", "上月实用：" + lastMouthData);
                                            //                                        Log.d("limbo", "比例：" + thisMouthData / lastMouthData);
                                            if (lastMouthData <= 0) {
                                                if (thisMouthData > 50) {
                                                    nb.setMeterState("1");
                                                } else if (thisMouthData < 0) {
                                                    nb.setMeterState("2");
                                                } else {
                                                    nb.setMeterState("0");
                                                }

                                            } else {//lastMouthData>0
                                                if (thisMouthData <= 0) {
                                                    nb.setMeterState("2");
                                                } else {
                                                    if (lastMouthData <= 5 && lastMouthData > 0) {
                                                        if ((thisMouthData / lastMouthData) <= 4) {
                                                            nb.setMeterState("0");
                                                        } else if ((thisMouthData / lastMouthData) == 0) {
                                                            nb.setMeterState("2");
                                                        } else {
                                                            nb.setMeterState("1");
                                                        }
                                                    } else if (lastMouthData <= 10 && lastMouthData > 5) {
                                                        if ((thisMouthData / lastMouthData) <= 3 && (thisMouthData / lastMouthData) > 0.4) {
                                                            nb.setMeterState("0");
                                                        } else if ((thisMouthData / lastMouthData) < 0.4) {
                                                            nb.setMeterState("2");
                                                        } else if ((thisMouthData / lastMouthData) > 3) {
                                                            nb.setMeterState("1");
                                                        }
                                                    } else if (lastMouthData <= 15 && lastMouthData > 10) {
                                                        if ((thisMouthData / lastMouthData) <= 2 && (thisMouthData / lastMouthData) > 0.4) {
                                                            nb.setMeterState("0");
                                                        } else if ((thisMouthData / lastMouthData) < 0.4) {
                                                            nb.setMeterState("2");
                                                        } else if ((thisMouthData / lastMouthData) > 2) {
                                                            nb.setMeterState("1");
                                                        }
                                                    } else if (lastMouthData <= 40 && lastMouthData > 15) {
                                                        if ((thisMouthData / lastMouthData) <= 1.6 && (thisMouthData / lastMouthData) > 0.4) {
                                                            nb.setMeterState("0");
                                                        } else if ((thisMouthData / lastMouthData) < 0.4) {
                                                            nb.setMeterState("2");
                                                        } else if ((thisMouthData / lastMouthData) > 1.6) {
                                                            nb.setMeterState("1");
                                                        }
                                                    } else if (lastMouthData > 40) {
                                                        if ((thisMouthData / lastMouthData) <= 1.3 && (thisMouthData / lastMouthData) > 0.7) {
                                                            nb.setMeterState("0");
                                                        } else if ((thisMouthData / lastMouthData) < 0.7) {
                                                            nb.setMeterState("2");
                                                        } else if ((thisMouthData / lastMouthData) > 1.3) {
                                                            nb.setMeterState("1");
                                                        }
                                                    }
                                                }
                                            }

                                        } catch (Exception e) {
                                            errMsg = errMsg + "\n第" + i + "只表error:" + e.toString();
                                            Log.d("limbo", e.toString());
                                        }
                                        if (nb.getMeterState().equals("1")) {
                                            dataList.add("第" + (POSITION + 1) + "户： "
                                                    + "\n表状态:本月数据偏大"
                                                    + "\n户号:" + nb.getHH()
                                                    + "\n表号:" + nb.getMeterNumber()
                                                    + "\n本月抄见:" + readData
                                                    + "\n上月抄见:" + lastData
                                                    + "\n本月实用:" + data
                                                    + "\n上月实用:" + lastReadData);
                                        } else if (nb.getMeterState().equals("2")) {
                                            dataList.add("第" + (POSITION + 1) + "户： "
                                                    + "\n表状态:本月数据偏小"
                                                    + "\n户号:" + nb.getHH()
                                                    + "\n表号:" + nb.getMeterNumber()
                                                    + "\n本月抄见:" + readData
                                                    + "\n上月抄见:" + lastData
                                                    + "\n本月实用:" + data
                                                    + "\n上月实用:" + lastReadData);
                                        } else {
                                            dataList.add("第" + (POSITION + 1) + "户： "
                                                    + "\n表状态:本月数据正常"
                                                    + "\n户号:" + nb.getHH()
                                                    + "\n表号:" + nb.getMeterNumber()
                                                    + "\n本月抄见:" + readData
                                                    + "\n上月抄见:" + lastData
                                                    + "\n本月实用:" + data
                                                    + "\n上月实用:" + lastReadData);
                                        }
                                        meterList.add(nb);
                                    }
                                    //                                else {
                                    //                                    Log.d("limbo", "数据库中未找到   小区号:" + xqNum + " 采集机号:" + cjjNum + " 表序号:" + (POSITION + 1) + " 的表");
                                    //                                                                        tv_deBugMsg.append("数据库中未找到   小区号:" + xqNum + " 采集机号:" + cjjNum + " 表序号:" + (POSITION + 1) + " 的表\n");
                                    //
                                    //                                }
                                }

                                cbStateMsg.setMeter(meterList);
                                ad = new ArrayAdapter(IrDACbActivity.this, R.layout.list_textview, dataList) {
                                    @Override
                                    public View getView(int position, View convertView, ViewGroup parent) {
                                        return super.getView(position, convertView, parent);
                                    }
                                };
                                lv_dataList.setAdapter(ad);
                                Toast.makeText(IrDACbActivity.this, "完成", Toast.LENGTH_LONG).show();
                            }
                        }
                        timeOut = true;
                        HzyUtils.closeProgressDialog();
                    } catch (Exception e) {
                        HzyUtils.closeProgressDialog();
                        Toast.makeText(IrDACbActivity.this, "数据缺失请重抄", Toast.LENGTH_LONG).show();
                    }

                    break;
                case 0x01:
                    String result = (String) msg.obj;
                    tv_xqNum.setText("小区号:" + cbStateMsg.getXq());
                    tv_cjjNum.setText("集中器号:" + cbStateMsg.getCjj());
                    Log.d("limbo", result);
                    cb(cbStateMsg.getXq() + "", cbStateMsg.getCjj() + "");


                    break;
                case 0x02:
                    HzyUtils.showProgressDialog(IrDACbActivity.this);
                    break;
                case 0x03:
                    HzyUtils.closeProgressDialog();
                    break;
                case 0x20:
                    new SweetAlertDialog(IrDACbActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("成功")
                            .setContentText("异常上报完成!")
                            .show();
                    break;
                case 0x98:
                    new SweetAlertDialog(IrDACbActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("错误")
                            .setContentText("网络异常,请重新上报!")
                            .show();
                    break;
                case 0x99:
                    timeOut = true;
                    tv_deBugMsg.setText(deBugMsg);
                    deBugMsg = "";

                    HzyUtils.closeProgressDialog();
                    /*new SweetAlertDialog(IrDACbActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("提示!")
                            .setContentText("无数据返回，请重试!")
                            .show();*/

                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 根据小区号，采集机号，表号找出new_Meters表中对应表
     */
    private New_Meter getSingleMeter(String XqNum, String CjjNum, String MeterNum) {
        CjjNum = Integer.parseInt(CjjNum) + "";
        New_Meter nb = DbHelper.getMeterByMeterMsg(XqNum, CjjNum, MeterNum);
//        Log.d("limbo", "Null MeterNum:" + MeterNum);
        return nb;
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("limbo", "requestCode =" + requestCode);
        Log.d("limbo", "resultCode =" + resultCode);
        switch (X_FLAG) {
            case 1:
                if (resultCode == BluetoothConnectThread.NETWORK_FAILED) {
                    HintDialog.ShowHintDialog(this, "设备连接错误", "错误");
                    MenuActivity.resetNetwork();
                } else if (resultCode == BluetoothConnectThread.NETWORK_CONNECTED) {
                    HintDialog.ShowHintDialog(this, "蓝牙连接成功", "提示");
                } else if (resultCode == BluetoothConnectThread.METER) {
                }
                break;
            case 2:
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    String Result = bundle.getString("result");
                    meterList.get(POSITIONX).setReadData(Result);
                    int x = Integer.parseInt(meterList.get(POSITIONX).getReadData()) - Integer.parseInt(meterList.get(POSITIONX).getLastData());
                    meterList.get(POSITIONX).setData(x + "");
                    Log.d("limbo", Result);
                    dataList.set(POSITIONX, "第" + (POSITIONX + 1) + "户："
                            + "\n户号:" + meterList.get(POSITIONX).getUserPhone2()
                            + "\n表号:" + meterList.get(POSITIONX).getMeterNumber()
                            + "\n本月抄见:" + meterList.get(POSITIONX).getReadData()
                            + "\n上月抄见:" + meterList.get(POSITIONX).getLastData()
                            + "\n本月实用:" + meterList.get(POSITIONX).getData()
                            + "\n上月实用:" + meterList.get(POSITIONX).getLastReadData());
                    ad = new ArrayAdapter(IrDACbActivity.this, R.layout.list_textview, dataList) {
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            return super.getView(position, convertView, parent);
                        }
                    };
                    lv_dataList.setAdapter(ad);
                }
                break;
            default:
                break;
        }
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

    /**
     * 开始协议设定时间
     * timeMax 1 = 0.1s
     * 11秒
     */
    private void reSendCmd(int timeMax) {
        final int finalTimeMax = timeMax;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < finalTimeMax && !timeOut; i++) {
                        Thread.sleep(100);
                        if (meterList.size() > 0) {
                            break;
                        }
                    }
                    /**
                     * 如果检测时还未通过判断
                     */
                    if (meterList.size() == 0) {
                        resultMsg = "";
                        MenuActivity.Cjj_CB_MSG = "";
                        MenuActivity.sendCmd(sendMsg.toLowerCase());
                    }
                } catch (Exception e) {
                    Log.d("limbo", e.toString());
                }
            }
        }).start();

    }

    /**
     * 使用SharePreferences保存用户信息
     */
    public void saveMsg(int radioInt) {

        SharedPreferences.Editor editor = getSharedPreferences("user_msg", MODE_PRIVATE).edit();
        editor.putInt("radioInt", radioInt);
        editor.commit();
    }


    /**
     * 加载用户信息
     */

    public void loadMsg() {
        SharedPreferences pref = getSharedPreferences("user_msg", MODE_PRIVATE);
        int i = pref.getInt("radioInt", 0);
        switch (i) {
            case 0:
                rb_1.performClick();
                break;
            case 1:
                rb_2.performClick();
                break;
            case 2:
                rb_3.performClick();
                break;
            case 3:
                rb_4.performClick();
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent resultIntent = new Intent();
        IrDACbActivity.this.setResult(3, resultIntent);
        IrDACbActivity.this.finish();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        saveMsg(radioInt);
        MenuActivity.btAuto = false;
        super.onDestroy();
    }
}

/**
 * 需要保存的水表列表信息类
 */
class cbStateMsg {

    private String xq;
    private String cjj;
    private List<New_Meter> meter;

    public List<New_Meter> getMeter() {
        return meter;
    }

    public void setMeter(List<New_Meter> meter) {
        this.meter = meter;
    }

    public String getXq() {
        return xq;
    }

    public void setXq(String xq) {
        this.xq = xq;
    }

    public String getCjj() {
        return cjj;
    }

    public void setCjj(String cjj) {
        this.cjj = cjj;
    }
}