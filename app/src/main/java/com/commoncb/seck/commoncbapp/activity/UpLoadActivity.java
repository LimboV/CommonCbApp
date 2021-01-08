package com.commoncb.seck.commoncbapp.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.commoncb.seck.commoncbapp.DB.DbHelper;
import com.commoncb.seck.commoncbapp.R;
import com.commoncb.seck.commoncbapp.modle1.Count;
import com.commoncb.seck.commoncbapp.modle1.New_Meter;
import com.commoncb.seck.commoncbapp.modle1.New_UserMsg;
import com.commoncb.seck.commoncbapp.modle1.Upload_Meter;
import com.commoncb.seck.commoncbapp.utils.HintDialog;
import com.commoncb.seck.commoncbapp.utils.HzyUtils;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by limbo on 2018/5/27.
 */

public class UpLoadActivity extends BaseActivity {
    @BindView(R.id.NS_XqSelectActivity_lv_xqlist)
    ListView NS_XqSelectActivity_lv_xqlist;
    @BindView(R.id.NS_XqSelectActivity_btn_confirm)
    Button NS_XqSelectActivity_btn_confirm;
    @BindView(R.id.NS_XqSelectActivity_btn_cancel)
    Button NS_XqSelectActivity_btn_cancel;
    @BindView(R.id.NS_XqSelectActivity_btn_selectAll)
    Button NS_XqSelectActivity_btn_selectAll;
    @BindView(R.id.NS_XqSelectActivity_btn_deleteAll)
    Button NS_XqSelectActivity_btn_deleteAll;
    public List<New_UserMsg> mList = new ArrayList<>();//线号列表
    private ListAdapter adapter;
    private ProgressDialog progressBar = null;

    public final static int CONNECT_TIMEOUT = 1000;
    public final static int READ_TIMEOUT = 15000;
    public final static int WRITE_TIMEOUT = 15000;
    public static final OkHttpClient client = new OkHttpClient.Builder()
            .readTimeout(READ_TIMEOUT, TimeUnit.MILLISECONDS)//设置读取超时时间
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.MILLISECONDS)//设置写的超时时间
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)//设置连接超时时间
            //            .retryOnConnectionFailure(false)
            .build();
    Count c;

    int updataInt = 0;
    String noPicResult;
    String PicResult;
    int dataCount = 0;
    int dataCountYC = 0;
    int picCount = 0;
    int picCountYC = 0;
    Thread thread;
    boolean upLoadFlag = false;//是否正在上传

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xqselect);
        ButterKnife.bind(this);
        progressBar = new ProgressDialog(this);
        progressBar.setCancelable(false);
        progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        DbHelper.getdb();//获取数据库实例
        adapter = new ArrayAdapter<String>(UpLoadActivity.this, android.R.layout.simple_list_item_multiple_choice, getXq());
        NS_XqSelectActivity_lv_xqlist.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        NS_XqSelectActivity_lv_xqlist.setAdapter(adapter);
        //        NS_XqSelectActivity_lv_xqlist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
        //            @Override
        //            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        //                new SweetAlertDialog(UpLoadActivity.this, SweetAlertDialog.WARNING_TYPE)
        //                        .setTitleText("提示")
        //                        .setContentText("请确认是否要删除该线号?  ")
        //                        .setCancelText("取消")
        //                        .setConfirmText("确认")
        //                        .showCancelButton(true)
        //                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
        //                            @Override
        //                            public void onClick(SweetAlertDialog sweetAlertDialog) {
        //                                Log.d("limboV", "sure");
        //                                sweetAlertDialog.cancel();
        //                                DbHelper.deleteXHAndMeter(mList.get(position).getXH());
        //                                getXq();
        //                                adapter = new ArrayAdapter(UpLoadActivity.this, R.layout.simple_list_item_1, getXq());
        //                                NS_XqSelectActivity_lv_xqlist.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        //                                NS_XqSelectActivity_lv_xqlist.setAdapter(adapter);
        //                            }
        //                        })
        //                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
        //                            @Override
        //                            public void onClick(SweetAlertDialog sDialog) {
        //                                Log.d("limboV", "cancel");
        //                                /*new SweetAlertDialog(UpLoadActivity.this, SweetAlertDialog.ERROR_TYPE)
        //                                        .setTitleText("取消!")
        //                                        .setContentText("你已经取消!")
        //                                        .show();*/
        //                                sDialog.cancel();
        //                            }
        //                        }).show();
        //                return true;
        //            }
        //        });
        /**
         * 删除
         */
        NS_XqSelectActivity_btn_deleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final List<String> selectItems = new ArrayList<>();
                SparseBooleanArray positionArr = NS_XqSelectActivity_lv_xqlist.getCheckedItemPositions();
                for (int i = 0; i < positionArr.size(); i++) {
                    if (positionArr.valueAt(i)) {
                        int idx = positionArr.keyAt(i);
                        selectItems.add(idx + "");
                    }
                }

                if (selectItems.size() == 0) {
                    return;
                }


                new SweetAlertDialog(UpLoadActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("提示")
                        .setContentText("请确认是否要删除该线号?  ")
                        .setCancelText("取消")
                        .setConfirmText("确认")
                        .showCancelButton(true)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                Log.d("limboV", "sure");
                                sweetAlertDialog.cancel();
                                progressBar.setIndeterminate(false);
                                progressBar.setMax(selectItems.size());
                                progressBar.setMessage("删除进度");
                                progressBar.setProgress(0);
                                progressBar.show();
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        for (int x = 0; x < selectItems.size(); x++) {
                                            final int i = Integer.parseInt(selectItems.get(x).toString());
                                            Log.d("limbo", mList.get(i).getXH());
                                            String XH = mList.get(i).getXH();
                                            DbHelper.deleteXHAndMeter(XH);
                                            Message message = new Message();
                                            message.what = 0x01;
                                            message.arg1 = i;
                                            handler.sendMessage(message);
                                        }
                                        Message message = new Message();
                                        message.what = 0x00;
                                        handler.sendMessage(message);
                                    }
                                }).start();
                            }
                        })
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                Log.d("limboV", "cancel");
                                sDialog.cancel();
                                /*new SweetAlertDialog(UpLoadActivity.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("取消!")
                                        .setContentText("你已经取消!")
                                        .show();*/

                            }
                        }).show();

            }
        });
        /**
         * 上传
         */
        NS_XqSelectActivity_btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new SweetAlertDialog(UpLoadActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("提示")
                        .setContentText("请选择上传模式!强制上传只上传表数据。")
                        .setConfirmText("强制上传")
                        //                        .setCancelText("强制上传")
                        //                        .setConfirmText("常规上传")
                        .showCancelButton(false)
                        //                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        //                            @Override
                        //                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                        //                                Log.d("limboV", "常规上传");
                        //                                sweetAlertDialog.cancel();
                        //                                final List<String> selectItems = new ArrayList<>();
                        //                                SparseBooleanArray positionArr = NS_XqSelectActivity_lv_xqlist.getCheckedItemPositions();
                        //                                for (int i = 0; i < positionArr.size(); i++) {
                        //                                    if (positionArr.valueAt(i)) {
                        //                                        int idx = positionArr.keyAt(i);
                        //                                        selectItems.add(idx + "");
                        //                                    }
                        //                                }
                        //                                for (int i = 0; i < selectItems.size(); i++) {
                        //                                    Log.d("limbo", "选中项:" + selectItems.get(i).toString());
                        //                                }
                        //                                final int count = selectItems.size();
                        //                               /* List<CntXH> dataCounts = DbHelper.getDataCount();
                        //                                List<CntXH> dataCountYCs = DbHelper.getDataCountYC();
                        //                                List<CntXH> picCounts = DbHelper.getPicCount();
                        //                                List<CntXH> picCountYCs = DbHelper.getPicCountYC();
                        //                                dataCount = 0;
                        //                                dataCountYC = 0;
                        //                                picCount = 0;
                        //                                picCountYC = 0;
                        //
                        //                                for (int x = 0; x < selectItems.size(); x++) {
                        //                                    final int i = Integer.parseInt(selectItems.get(x).toString());
                        //                                    String XH = mList.get(i).getXH();
                        //                                    picCount = picCount + picCounts.get(i).getCnt();//获取线号下 所有有图，未上传表
                        //                                    picCountYC = picCountYC + picCountYCs.get(i).getCnt();
                        //                                    dataCount = dataCount + dataCounts.get(i).getCnt();
                        //                                    dataCountYC = dataCountYC + dataCountYCs.get(i).getCnt();
                        //                                }*/
                        //
                        //                                dataCount = 0;
                        //                                dataCountYC = 0;
                        //                                picCount = 0;
                        //                                picCountYC = 0;
                        //
                        //                                for (int x = 0; x < selectItems.size(); x++) {
                        //                                    final int i = Integer.parseInt(selectItems.get(x).toString());
                        //                                    String XH = mList.get(i).getXH();
                        //
                        //                                    picCount = picCount + DbHelper.getPic1Count(XH);//获取线号下 所有有图，未上传表
                        //                                    picCountYC = picCountYC + DbHelper.getPic1CountYC(XH);
                        //                                    dataCount = dataCount + DbHelper.getDataCount(XH);
                        //                                    dataCountYC = dataCountYC + DbHelper.getDataCountYC(XH);
                        //                                }
                        //                                new SweetAlertDialog(UpLoadActivity.this, SweetAlertDialog.WARNING_TYPE)
                        //                                        .setTitleText("提示：常规上传")
                        //                                        .setContentText("请确认上传数据数量:     \n本次上传表数:" + (dataCount + picCount) +
                        //                                                "   \n本次上传图片数:" + picCount +
                        //                                                "   \n数据库中已上传表数：" + (dataCountYC + picCountYC) +
                        //                                                "   \n数据库中已上传图片:" + picCountYC)
                        //                                        .setCancelText("取消")
                        //                                        .setConfirmText("确定")
                        //                                        .showCancelButton(true)
                        //                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        //                                            @Override
                        //                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                        //                                                sweetAlertDialog.cancel();
                        //                                                if (!upLoadFlag) {
                        //                                                    //                                                    upLoadFlag = true;
                        //                                                    //                                                    uploadData(false);
                        //                                                }
                        //                                            }
                        //                                        })
                        //                                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        //                                            @Override
                        //                                            public void onClick(SweetAlertDialog sDialog) {
                        //                                                sDialog.cancel();
                        //                                            }
                        //                                        }).show();
                        //                            }
                        //                        })
                        .setConfirmClickListener((new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                Log.d("limboV", "强制上传");
                                sDialog.cancel();
                                final List<String> selectItems = new ArrayList<>();
                                SparseBooleanArray positionArr = NS_XqSelectActivity_lv_xqlist.getCheckedItemPositions();
                                for (int i = 0; i < positionArr.size(); i++) {
                                    if (positionArr.valueAt(i)) {
                                        int idx = positionArr.keyAt(i);
                                        selectItems.add(idx + "");
                                    }
                                }
                                for (int i = 0; i < selectItems.size(); i++) {
                                    Log.d("limbo", "选中项:" + selectItems.get(i).toString());
                                }
                                /*final int count = selectItems.size();
                                List<CntXH> dataCounts = DbHelper.getDataCount();
                                List<CntXH> dataCountYCs = DbHelper.getDataCountYC();
                                List<CntXH> picCounts = DbHelper.getPicCount();
                                List<CntXH> picCountYCs = DbHelper.getPicCountYC();
                                dataCount = 0;
                                dataCountYC = 0;
                                picCount = 0;
                                picCountYC = 0;

                                for (int x = 0; x < selectItems.size(); x++) {
                                    final int i = Integer.parseInt(selectItems.get(x).toString());
                                    String XH = mList.get(i).getXH();
                                    picCount = picCount + picCounts.get(i).getCnt();//获取线号下 所有有图，未上传表
                                    picCountYC = picCountYC + picCountYCs.get(i).getCnt();
                                    dataCount = dataCount + dataCounts.get(i).getCnt();
                                    dataCountYC = dataCountYC + dataCountYCs.get(i).getCnt();
                                }*/
                                dataCount = 0;
                                dataCountYC = 0;
                                picCount = 0;
                                picCountYC = 0;
                                for (int x = 0; x < selectItems.size(); x++) {
                                    final int i = Integer.parseInt(selectItems.get(x).toString());
                                    String XH = mList.get(i).getXH();
                                    picCount = picCount + DbHelper.getPic1Count(XH);//获取线号下 所有有图，未上传表
                                    picCountYC = picCountYC + DbHelper.getPic1CountYC(XH);
                                    dataCount = dataCount + DbHelper.getDataCount(XH);
                                    dataCountYC = dataCountYC + DbHelper.getDataCountYC(XH);
                                }
                                new SweetAlertDialog(UpLoadActivity.this, SweetAlertDialog.WARNING_TYPE)
                                        .setTitleText("提示：强制上传")
                                        .setContentText("请确认上传数据数量:\n本次上传表数:" + (picCount + picCountYC + dataCount + dataCountYC) +
                                                "   \n本次上传图片数:" + (picCount + picCountYC) +
                                                "   \n数据库中已上传表数：" + (dataCountYC + picCountYC) +
                                                "   \n数据库中已上传图片:" + picCountYC)
                                        .setCancelText("取消")
                                        .setConfirmText("确定")
                                        .showCancelButton(true)
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                sweetAlertDialog.cancel();
                                                int timeoutcout = 0;
                                                while (MainActivity.upLoadAuto) {
                                                    if (timeoutcout == 5) {
                                                        Toast.makeText(UpLoadActivity.this,"目前无法上传",Toast.LENGTH_SHORT).show();
                                                        break;
                                                    }
                                                    try {
                                                        Thread.sleep(1000);
                                                    } catch (InterruptedException e) {
                                                        Log.d("limbo", e.getMessage());
                                                    } finally {
                                                        timeoutcout++;
                                                    }
                                                }
                                                if (!upLoadFlag && !MainActivity.upLoadAuto) {
                                                    upLoadFlag = true;
                                                    uploadData(true);
                                                }
                                            }
                                        })
                                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                sweetAlertDialog.cancel();
                                            }
                                        }).show();
                            }
                        })).show();
            }
        });
        /**
         * 退出
         */
        NS_XqSelectActivity_btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        /**
         * 全选
         */
        NS_XqSelectActivity_btn_selectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < mList.size(); i++) {
                    NS_XqSelectActivity_lv_xqlist.performItemClick(NS_XqSelectActivity_lv_xqlist.getChildAt(i),
                            i, NS_XqSelectActivity_lv_xqlist.getItemIdAtPosition(i));
                }
            }
        });
    }

    /**
     * 上传小区
     */
    private void uploadData(final boolean flag) {
        final List<String> selectItems = new ArrayList<>();
        SparseBooleanArray positionArr = NS_XqSelectActivity_lv_xqlist.getCheckedItemPositions();
        for (int i = 0; i < positionArr.size(); i++) {
            if (positionArr.valueAt(i)) {
                int idx = positionArr.keyAt(i);
                selectItems.add(idx + "");
            }
        }
        for (int i = 0; i < selectItems.size(); i++) {
            Log.d("limbo", "选中项:" + selectItems.get(i).toString());
        }
        final int count = selectItems.size();

        if (positionArr.size() == 0) {
            Log.d("limbo", "未选择线号的情况下点击上传");
            return;
        }

        if (selectItems.size() <= 0) {
            Toast.makeText(UpLoadActivity.this, "未选择线号!", Toast.LENGTH_LONG).show();
            return;
        }
        try {
            progressBar.setIndeterminate(false);
            if (flag) {
                progressBar.setMax((picCount + picCountYC + dataCount + dataCountYC));
            } else {
                progressBar.setMax((picCount + dataCount));
            }
            progressBar.setMessage("上传进度");
            updataInt = 0;
            progressBar.setProgress(0);
            progressBar.show();
        } catch (Exception e) {
            Log.d("limbo", e.toString());
        }
        /*if (thread != null && upLoadFlag == true) {
            thread.stop();
        }*/
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    TelephonyManager tm = (TelephonyManager) UpLoadActivity.this.getSystemService(Context.TELEPHONY_SERVICE);
                    if (ActivityCompat.checkSelfPermission(UpLoadActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        return;
                    }
                    String deviceid = tm.getDeviceId();//获取智能设备唯一编号
                    if (ActivityCompat.checkSelfPermission(UpLoadActivity.this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(UpLoadActivity.this, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(UpLoadActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        return;
                    }
                    String te1 = tm.getLine1Number();//获取本机号码
                    String imei = tm.getSimSerialNumber();//获得SIM卡的序号
                    //        String imsi = tm.getSubscriberId();//得到用户Id
                    if (HzyUtils.isEmpty(te1)) {
                        te1 = "";
                    }
                    if (HzyUtils.isEmpty(imei)) {
                        imei = "";
                    }
                    Log.d("limbo", te1);
                    Log.d("limbo", imei);
                    noPicResult = "无图数据部分";
                    PicResult = "\n含图数据部分";
                    Message message = new Message();
                    message.what = 0x01;
                    message.arg1 = 0;
                    handler.sendMessage(message);
                    for (int x = 0; x < selectItems.size(); x++) {
                        int countPic = 0;//保存上传照片条数
                        final int i = Integer.parseInt(selectItems.get(x).toString());
                        String XH = mList.get(i).getXH();
                        ArrayList<New_Meter> new_meterNoPicMeter = DbHelper.getAllMeterByXH(XH, flag);//获取线号下 所有无图，未上传表
                        noPicResult = noPicResult + "\n线号：" + XH + "上传" + new_meterNoPicMeter.size() + "条数据";
                        ArrayList<Upload_Meter> upload_meters = new ArrayList<Upload_Meter>();

                        for (New_Meter new_meter : new_meterNoPicMeter) {
                            Upload_Meter meter = new Upload_Meter();
                            meter.setXH(new_meter.getXH());
                            meter.setKH(new_meter.getKH());
                            meter.setHH(new_meter.getHH());
                            meter.setJBH(new_meter.getJBH());
                            meter.setUserName(new_meter.getUserName().trim());
                            meter.setUserPhone1(new_meter.getUserPhone1());
                            meter.setUserPhone2(new_meter.getUserPhone2());
                            meter.setMeterNumber(new_meter.getMeterNumber());
                            meter.setCaliber(new_meter.getCaliber());
                            meter.setXqNum(new_meter.getXqNum());
                            meter.setCjjNum(new_meter.getCjjNum());
                            meter.setMeterNum(new_meter.getMeterNum());
                            meter.setLastUserWater(new_meter.getLastUserWater());
                            meter.setLast2MouthUserWater(new_meter.getLast2MouthUserWater());
                            meter.setLast3MouthUserWater(new_meter.getLast3MouthUserWater());
                            meter.setAddr(new_meter.getAddr().trim());
                            meter.setBtFlag(new_meter.getBtFlag());
                            meter.setMeterState(new_meter.getMeterState());
                            meter.setUserState(new_meter.getUserState());
                            //                            meter.setUserState("26");
                            meter.setCbState(new_meter.getCbState());
                            meter.setData(new_meter.getData());
                            meter.setReadData(new_meter.getReadData());
                            meter.setLastData(new_meter.getLastData());
                            meter.setLastReadData(new_meter.getLastReadData());
                            meter.setIrDAData(new_meter.getIrDAData());
                            meter.setLon(new_meter.getLon());
                            meter.setLat(new_meter.getLat());
                            meter.setPLon(new_meter.getPLon());
                            meter.setPLat(new_meter.getPLat());
                            meter.setUploadTime(new_meter.getUploadTime());
                            meter.setUploadFlag(new_meter.getUploadFlag());
                            meter.setCbFlag(new_meter.getCbFlag());
                            meter.setNote(new_meter.getNote());
                            meter.setPicString1("");
                            meter.setPicString2("");
                            meter.setPicString3("");
                            meter.setDeviceid(deviceid);
                            meter.setTe1(te1);
                            meter.setImei(imei);

                            if (flag || meter.getUploadFlag() == null || !meter.getUploadFlag().equals("已上传")) {
                                upload_meters.add(meter);
                            }
                        }

                        //json为String类型的json数据
                        String gsonString = null;
                        Gson gson = new Gson();
                        if (gson != null) {
                            gsonString = gson.toJson(upload_meters);
                            Log.d("limbo", "updata:" + gsonString);
                            FormBody formBody = new FormBody
                                    .Builder()
                                    .add("xq", gsonString)//设置参数名称和参数值
                                    .build();
                            //创建一个请求对象
                            Request request = new Request.Builder()
                                    .url(MainActivity.url + "/UpLoadReadData")// + MenuActivity.ns_id
                                    .post(formBody)
                                    .build();
                            //发送请求获取响应
                            Response response = null;
                            try {
                                response = client.newCall(request).execute();//得到Response 对象
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            //                        Response response = okHttpClient.newCall(request).execute();

                            //判断请求是否成功
                            if (response.isSuccessful()) {
                                //打印服务端返回结果
                                String responseX = null;
                                try {
                                    responseX = response.body().string();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                responseX = responseX.replace("<string xmlns=\"http://tempuri.org/\">", "");
                                responseX = responseX.replace("<?xml version=\"1.0\" encoding=\"utf-8\"?>", "");
                                responseX = responseX.replace("</string>", "");
                                Log.d("limbo", responseX);
                                Gson gson1 = new Gson();//使用Gson解析
                                c = gson1.fromJson(responseX, Count.class);
                                //                            responseX = "0";
                                if (Integer.parseInt(c.getCount()) == 0) {
                                    //                                    noPicResult = noPicResult + "(无新数据)  ";
                                } else if (Integer.parseInt(c.getCount()) < 0) {
                                    //                                    noPicResult = noPicResult + "(服务器接收错误)  ";
                                } else {
                                    /**
                                     * 更改上传标志位
                                     */
                                    DbHelper.newChangeUploadFlag(new_meterNoPicMeter, c.getFailList());
                                    //noPicResult = noPicResult + "\n线号:" + XH + "(成功)  ";
                                    for (int j = 0; j < Integer.parseInt(c.getCount()); j++) {
                                        message = new Message();
                                        message.what = 0x01;
                                        handler.sendMessage(message);
                                    }
                                }
                            } else {
                                //                                noPicResult = noPicResult + "(服务器未响应)  ";
                                try {
                                    throw new IOException("limbo:" + response);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        /**
                         * --------------------传图
                         */
                        /*ArrayList<New_Meter> new_meterPicMeter = DbHelper.getYcWscMeterPicByXH(XH, flag);//获取线号下 所有有图，未上传表
                        countPic = 0;
                        for (New_Meter new_meter : new_meterPicMeter) {
                            Upload_Meter meter = new Upload_Meter();
                            meter.setXH(new_meter.getXH());
                            meter.setKH(new_meter.getKH());
                            meter.setHH(new_meter.getHH());
                            meter.setJBH(new_meter.getJBH());
                            meter.setUserName(new_meter.getUserName());
                            meter.setUserPhone1(new_meter.getUserPhone1());
                            meter.setUserPhone2(new_meter.getUserPhone2());
                            meter.setMeterNumber(new_meter.getMeterNumber());
                            meter.setCaliber(new_meter.getCaliber());
                            meter.setXqNum(new_meter.getXqNum());
                            meter.setCjjNum(new_meter.getCjjNum());
                            meter.setMeterNum(new_meter.getMeterNum());
                            meter.setLastUserWater(new_meter.getLastUserWater());
                            meter.setLast2MouthUserWater(new_meter.getLast2MouthUserWater());
                            meter.setLast3MouthUserWater(new_meter.getLast3MouthUserWater());
                            meter.setAddr(new_meter.getAddr());
                            meter.setBtFlag(new_meter.getBtFlag());
                            meter.setMeterState(new_meter.getMeterState());
                            meter.setCbState(new_meter.getCbState());
                            meter.setUserState(new_meter.getUserState());
                            //                            meter.setUserState("26");
                            meter.setData(new_meter.getData());
                            meter.setReadData(new_meter.getReadData());
                            meter.setLastData(new_meter.getLastData());
                            meter.setLastReadData(new_meter.getLastReadData());
                            meter.setIrDAData(new_meter.getIrDAData());
                            meter.setLon(new_meter.getLon());
                            meter.setLat(new_meter.getLat());
                            meter.setPLon(new_meter.getPLon());
                            meter.setPLat(new_meter.getPLat());
                            meter.setUploadTime(new_meter.getUploadTime());
                            meter.setCbFlag(new_meter.getCbFlag());
                            meter.setNote(new_meter.getNote());
                            //                            meter.setPicString(HzyUtils.bitmapToBase64(DbHelper.getYcMeterPicByKH(new_meter.getKH())));
//                            String base64 = HzyUtils.bitmapToBase64(DbHelper.getPicByKH(new_meter.getKH()));
                            String base64 = Base64.encodeToString(DbHelper.getPicByKH(new_meter.getKH(),0), Base64.DEFAULT);
                            //                                    Log.d("limbo", base64.length() / 1024 + "K");
                            meter.setPicString1(base64);
                            base64 = Base64.encodeToString(DbHelper.getPicByKH(new_meter.getKH(),1), Base64.DEFAULT);
                            meter.setPicString2(base64);
                            base64 = Base64.encodeToString(DbHelper.getPicByKH(new_meter.getKH(),2), Base64.DEFAULT);
                            meter.setPicString3(base64);
                            meter.setDeviceid(deviceid);
                            meter.setTe1(te1);
                            meter.setImei(imei);
                            if (flag || meter.getUploadFlag() == null || !meter.getUploadFlag().equals("已上传")) {
                                //申明给服务端传递一个json串
                                //创建一个OkHttpClient对象
                                //创建一个RequestBody(参数1：数据类型 参数2传递的json串)
                                //json为String类型的json数据
                                gson = new Gson();
                                if (gson != null) {
                                    gsonString = gson.toJson(meter);
                                    Log.d("limbo", "updata:" + "[" + gsonString + "]");
                                    FormBody formBody = new FormBody
                                            .Builder()
                                            .add("xq", "[" + gsonString + "]")//设置参数名称和参数值
                                            .build();
                                    //创建一个请求对象
                                    Request request = new Request.Builder()
                                            .url(MainActivity.url + "/UpLoadReadData")// + MenuActivity.ns_id
                                            .post(formBody)
                                            .build();
                                    //发送请求获取响应
                                    Response response = null;
                                    try {
                                        response = client.newCall(request).execute();//得到Response 对象
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    //                        Response response = okHttpClient.newCall(request).execute();

                                    //判断请求是否成功
                                    if (response.isSuccessful()) {
                                        message = new Message();
                                        message.what = 0x01;
                                        handler.sendMessage(message);
                                        //打印服务端返回结果
                                        String responseX = null;
                                        try {
                                            responseX = response.body().string();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        responseX = responseX.replace("<string xmlns=\"http://tempuri.org/\">", "");
                                        responseX = responseX.replace("<?xml version=\"1.0\" encoding=\"utf-8\"?>", "");
                                        responseX = responseX.replace("</string>", "");
                                        Log.d("limbo", responseX);
                                        Gson gson1 = new Gson();//使用Gson解析
                                        c = gson1.fromJson(responseX, Count.class);
                                        //                            responseX = "0";
                                        if (Integer.parseInt(c.getCount()) == 0) {
                                            //                                            PicResult = PicResult + "\n表号：" + meter.getMeterNumber() + "(无新数据)  ";
                                        } else if (Integer.parseInt(c.getCount()) < 0) {
                                            //                                            PicResult = PicResult + "\n表号：" + meter.getMeterNumber() + "(服务器接收错误)  ";
                                        } else {
                                            //                                    PicResult = PicResult + "\n线号:" + XH + "(成功)  ";
                                            *//**
                         * 更改上传标志位
                         *//*
                                            countPic++;
                                            DbHelper.newChangeUploadFlag(new_meter);
                                        }
                                    } else {

                                        //                                        PicResult = PicResult + "\n表号：" + meter.getMeterNumber() + "(服务器未响应)  ";
                                        try {
                                            throw new IOException("limbo:" + response);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }
                        PicResult = PicResult + "\n线号:" + XH + "    上传成功带图片数据:" + countPic + "条";*/

                    }
                    message = new Message();
                    message.what = 0x05;
                    if (!noPicResult.contains("线号")) {
                        noPicResult = noPicResult + "\n上传完成!";
                    }
                    if (!PicResult.contains("表号")) {
                        PicResult = PicResult + "\n上传完成!";
                    }
                    //                    message.obj = noPicResult + "\n" + PicResult;
                    message.obj = noPicResult;
                    handler.sendMessage(message);

                } catch (Exception e) {
                    Log.d("limbo", e.toString());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Message message = new Message();
                            message.what = 0x99;
                            handler.sendMessage(message);
                        }
                    });
                }
            }
        });
        thread.start();
    }


    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                /**
                 *
                 */
                case 0x00:

                    NS_XqSelectActivity_lv_xqlist.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                    adapter = new ArrayAdapter(UpLoadActivity.this, android.R.layout.simple_list_item_1, getXq());
                    NS_XqSelectActivity_lv_xqlist.setAdapter(adapter);
                    progressBar.dismiss();
                    break;
                /**
                 * 更新进度条
                 */
                case 0x01:
                    updataInt++;
                    progressBar.setProgress(updataInt);

                    break;
                /**
                 * 提示用户上传数据量
                 */
                case 0x02:
                    int dataCount = msg.arg1;
                    int picCount = msg.arg2;
                    HintDialog.ShowHintDialog(UpLoadActivity.this, "总上传表数:" + (dataCount + picCount)
                            + "    \n图片数:" + picCount, "上传数据统计");

                    break;
                case 0x05:
                    String x = (String) msg.obj;
                    new SweetAlertDialog(UpLoadActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("完成")
                            .setContentText(x)
                            .show();
                    progressBar.dismiss();
                    upLoadFlag = false;
                    break;
                case 0x96:
                    progressBar.dismiss();
                    new SweetAlertDialog(UpLoadActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("已取消")
                            .setContentText("已取消上传")
                            .show();
                    upLoadFlag = false;
                case 0x97:
                    x = (String) msg.obj;
                    progressBar.dismiss();
                    new SweetAlertDialog(UpLoadActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("失败")
                            .setContentText(x)
                            .show();
                    upLoadFlag = false;
                    break;
                case 0x98:
                    progressBar.dismiss();
                    try {
                        new SweetAlertDialog(UpLoadActivity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("错误")
                                .setContentText("上传失败!")
                                .show();
                    } catch (Exception e) {
                        Log.d("limbo", e.toString());
                        Toast.makeText(UpLoadActivity.this, "上传失败", Toast.LENGTH_LONG).show();
                    }
                    upLoadFlag = false;
                    break;
                case 0x99:
                    MainActivity.timeOut = true;
                    progressBar.dismiss();
                    try {
                        new SweetAlertDialog(UpLoadActivity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("错误")
                                .setContentText("网络异常!")
                                .show();

                    } catch (Exception e) {
                        Log.d("limbo", e.toString());
                        Toast.makeText(UpLoadActivity.this, "网络异常", Toast.LENGTH_LONG).show();
                    }
                    upLoadFlag = false;
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onBackPressed() {
        if (upLoadFlag == true) {
            thread.stop();
            Message message = new Message();
            message.what = 0x96;
            handler.sendMessage(message);

        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        MainActivity.timeOut = true;
        super.onDestroy();
    }

    private List<String> getXq() {
        List<String> data = new ArrayList<>();
        try {
            mList = DbHelper.getAllXH(MainActivity.user);//从数据库提取出
            for (New_UserMsg new_userMsg : mList) {
                data.add("线号:" + new_userMsg.getXH());
            }
        } catch (Exception e) {
            Log.e("limbo", e.toString());
            new SweetAlertDialog(UpLoadActivity.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("错误!")
                    .setContentText("未找到数据,请检查数据库文件是否存在！")
                    .show();
        }
        return data;
    }
}