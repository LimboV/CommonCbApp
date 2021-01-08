package com.commoncb.seck.commoncbapp.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.commoncb.seck.commoncbapp.DB.DbHelper;
import com.commoncb.seck.commoncbapp.R;
import com.commoncb.seck.commoncbapp.modle1.New_DownLoad;
import com.commoncb.seck.commoncbapp.modle1.New_Login;
import com.commoncb.seck.commoncbapp.modle1.New_UserMsg;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.commoncb.seck.commoncbapp.activity.MainActivity.timeOut;

/**
 * Created by limbo on 2018/5/27.
 */

public class DownLoadActivity extends BaseActivity {
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
    private ProgressDialog progressBar = null;
    private List<String> list = new ArrayList<>();//存放小区展示列表
    private List<String> IDlist = new ArrayList<>();//存放小区展示列表


    private String errorMsg = "";//存放下载失败信息
    private String xMsg = "";//存放下载重复信息
    private String successMsg = "";//存放下载成功信息

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xqselect);
        ButterKnife.bind(this);


        int flag = copyFilesFassets(DownLoadActivity.this, "Seck_4Db.db", Environment.getExternalStorageDirectory().getAbsolutePath() + "/SeckLoRaDB");
        switch (flag) {
            case 0://完成
                Toast.makeText(DownLoadActivity.this, "DB文件创建完成", Toast.LENGTH_LONG).show();
                break;
            case 1://不存在
                Toast.makeText(DownLoadActivity.this, "SD卡不存在", Toast.LENGTH_LONG).show();
                break;
            case 2://报错
                Toast.makeText(DownLoadActivity.this, "DB文件创建失败", Toast.LENGTH_LONG).show();
                break;
            case 3://已存在
                //                Toast.makeText(DownLoadActivity.this, "DB文件已存在", Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }

        DbHelper.getdb();//获取数据库实例
        progressBar = new ProgressDialog(this);
        progressBar.setCancelable(false);
        progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        NS_XqSelectActivity_lv_xqlist.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        getXqList();

        NS_XqSelectActivity_btn_deleteAll.setVisibility(View.GONE);
        NS_XqSelectActivity_btn_confirm.setText("下载");
        NS_XqSelectActivity_btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadData();
            }
        });
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
                for (int i = 0; i < list.size(); i++) {
                    NS_XqSelectActivity_lv_xqlist.performItemClick(NS_XqSelectActivity_lv_xqlist.getChildAt(i),
                            i, NS_XqSelectActivity_lv_xqlist.getItemIdAtPosition(i));

                }
            }
        });
    }

    /**
     * 初始化获得小区列表 http://192.168.1.236:85/Reading/CbService.asmx?op=GetXhs
     */
    private void getXqList() {
        prepareTimeStart(30);//超时处理
        progressBar.setIndeterminate(true);
        progressBar.setMessage("获取小区信息");
        progressBar.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    /*OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象
                    Request request = new Request.Builder()
                            .url(MainActivity.url + "/GetXqs?UserID=" + MainActivity.user)//请求接口。如果需要传参拼接到接口后面。
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
                        Gson gson = new Gson();//使用Gson解析
                        New_Login new_login = gson.fromJson(responseX, new TypeToken<New_Login>() {
                        }.getType());
                        for (New_UserMsg new_userMsg : new_login.getDatas()) {
                            list.add("线号:" + new_userMsg.getXH() );
                            IDlist.add(new_userMsg.getXH());
                        }
                        Message message = new Message();
                        message.what = 0x00;
                        handler.sendMessage(message);
                    }*/
                    New_Login new_login = MainActivity.muser;
                    for (New_UserMsg new_userMsg : new_login.getDatas()) {
                        list.add("线号:" + new_userMsg.getXH());
                        IDlist.add(new_userMsg.getXH());
                    }
                    Message message = new Message();
                    message.what = 0x00;
                    handler.sendMessage(message);
                } catch (Exception e) {
                    Log.d("limbo", e.toString());
                    Message message = new Message();
                    message.what = 0x99;
                    handler.sendMessage(message);
                }
            }
        }).start();

    }

    /**
     * 下载小区
     */
    private void downloadData() {
        errorMsg = "";
        successMsg = "";
        xMsg = "";
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

        if (positionArr.size() == 0) {
            Log.d("limbo", "未选择小区的情况下点击下载");
            return;
        }

        if (selectItems.size() <= 0) {
            Toast.makeText(DownLoadActivity.this, "未选择小区!", Toast.LENGTH_LONG).show();
            return;
        }
        progressBar.setIndeterminate(false);
        progressBar.setMax(selectItems.size());
        progressBar.setMessage("下载进度");
        progressBar.setProgress(0);
        progressBar.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    List<New_UserMsg> new_userMsgs = DbHelper.getAllXH(MainActivity.user);
                    for (int i = 0; i < selectItems.size(); i++) {

                        boolean flag = false;
                        for (New_UserMsg new_userMsg : new_userMsgs) {
                            if (new_userMsg.getXH().equals(IDlist.get(Integer.parseInt(selectItems.get(i).toString())))) {
                                flag = true;
                            }
                        }
                        if (!flag) {
                            progressBar.setProgress(i);
//                            OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象
                            OkHttpClient client = new OkHttpClient.Builder()
                                    .connectTimeout(15, TimeUnit.SECONDS)//设置连接超时时间
                                    .readTimeout(45, TimeUnit.SECONDS)//设置读取超时时间
                                    .build();
                            Request request = new Request.Builder()
                                    .url(MainActivity.url + "/GetSbDetails?Xh=" + IDlist.get(Integer.parseInt(selectItems.get(i).toString())))//请求接口。如果需要传参拼接到接口后面。
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
                                Gson gson = new Gson();//使用Gson解析
                                //                            New_DownLoad new_downLoad = gson.fromJson(responseX,New_DownLoad.class);//获取单个小区完整信息
                                New_DownLoad new_downLoad = gson.fromJson(responseX, New_DownLoad.class);//获取单个小区完整信息
                                String xh = IDlist.get(Integer.parseInt(selectItems.get(i).toString()));
                                if (new_downLoad.getFlag().equals("success")) {
                                    Log.d("limbo", new_downLoad.getFlag() + " 线号:" + xh);
                                    DbHelper.addXHandMeter(xh, new_downLoad.getDatas());
                                    successMsg = successMsg + "\n线号:" + xh;
                                } else {
                                    Log.d("limbo", new_downLoad.getFlag() + " 线号:" + xh);
                                    errorMsg = errorMsg + "\n线号:" + xh;
                                }

                                Thread.sleep(1000);
                                Message message = new Message();
                                message.obj = i;
                                message.what = 0x01;
                                handler.sendMessage(message);

                            }
                        } else {
                            xMsg = xMsg + "\n线号:" + IDlist.get(Integer.parseInt(selectItems.get(i).toString()));
                            //                            Message message = new Message();
                            //                            message.what = 0x96;
                            //                            message.obj = IDlist.get(Integer.parseInt(selectItems.get(i).toString()));
                            //                            handler.sendMessage(message);
                            //                            Toast.makeText(DownLoadActivity.this, "该线程已存在", Toast.LENGTH_LONG).show();
                            //                            return;
                        }


                    }

                    Message message = new Message();
                    message.what = 0x02;
                    handler.sendMessage(message);

                } catch (Exception e) {
                    Log.d("limbo", e.toString());
                    Message message = new Message();
                    message.what = 0x99;
                    handler.sendMessage(message);
                }
            }
        }).start();
    }

    /**
     * 开始协议设定时间
     */
    private void prepareTimeStart(final int timeMax) {
        timeOut = false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < timeMax && !timeOut; i++) {
                        Thread.sleep(100);
                    }
                    if (!timeOut) {
                        Message message = new Message();
                        message.what = 0x98;
                        handler.sendMessage(message);
                    }
                    timeOut = true;
                } catch (Exception e) {

                }
            }
        }).start();
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                /**
                 * 下载小区列表
                 */
                case 0x00:

                    progressBar.dismiss();
                    timeOut = true;
                    NS_XqSelectActivity_lv_xqlist.setAdapter(new ArrayAdapter<String>(DownLoadActivity.this,
                            android.R.layout.simple_list_item_multiple_choice, list));
                    break;
                /**
                 * 更新进度条
                 */
                case 0x01:
                    int responseX = (int) msg.obj;
                    progressBar.setProgress(responseX + 1);
                    timeOut = true;
                    break;
                /**
                 * 下载勾选小区
                 */
                case 0x02:

                    if (errorMsg.length() > 1) {
                        new SweetAlertDialog(DownLoadActivity.this, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("存在空线号！")
                                .setContentText(errorMsg)
                                .show();
                    }
                    if (xMsg.length() > 1) {
                        new SweetAlertDialog(DownLoadActivity.this, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("存在已下载线号！")
                                .setContentText(xMsg + "  ")
                                .show();
                    }
                    if (successMsg.length() > 1) {
                        new SweetAlertDialog(DownLoadActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("下载完成！")
                                .setContentText(successMsg)
                                .show();
                    }
                    timeOut = true;
                    progressBar.dismiss();

                    break;
                case 0x03:

                    break;
                case 0x96:
                    String x = (String) msg.obj;
                    Toast.makeText(DownLoadActivity.this, x + "号线程已存在", Toast.LENGTH_LONG).show();
                    break;
                case 0x98:
                    progressBar.dismiss();
                    new SweetAlertDialog(DownLoadActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("错误")
                            .setContentText("无法访问!")
                            .show();
                    break;
                case 0x99:
                    timeOut = true;
                    progressBar.dismiss();
                    new SweetAlertDialog(DownLoadActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("错误")
                            .setContentText("网络异常!")
                            .show();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        timeOut = true;
        super.onDestroy();
    }

    /**
     * 从assets目录中复制整个文件夹内容
     *
     * @param context Context 使用CopyFiles类的Activity
     * @param oldPath String  原文件路径  如：/aa
     * @param newPath String  复制后路径  如：xx:/bb/cc
     */
    public int copyFilesFassets(Context context, String oldPath, String newPath) {
        try {
            boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
            if (sdCardExist) {
                String fileNames[] = context.getAssets().list(oldPath);//获取assets目录下的所有文件及目录名
                if (fileNames.length > 0) {//如果是目录
                    File file = new File(newPath);
                    file.mkdirs();//如果文件夹不存在，则递归
                    for (String fileName : fileNames) {
                        Log.d("limbo", fileName);
                        copyFilesFassets(context, oldPath + "/" + fileName, newPath + "/" + fileName);
                    }
                } else {//如果是文件
                    File sdDir = new File(newPath);// 获取SD卡的path
                    if (!sdDir.exists()) {
                        sdDir.mkdirs();
                    }
                    sdDir = new File(newPath + "/" + oldPath);
                    if (!sdDir.exists()) {
                        InputStream is = context.getAssets().open(oldPath);
                        FileOutputStream fos = new FileOutputStream(new File(newPath + "/" + oldPath));
                        byte[] buffer = new byte[1024];
                        int byteCount = 0;
                        while ((byteCount = is.read(buffer)) != -1) {//循环从输入流读取 buffer字节
                            fos.write(buffer, 0, byteCount);//将读取的输入流写入到输出流
                        }
                        fos.flush();//刷新缓冲区
                        is.close();
                        fos.close();
                        return 0;
                    } else {
                        return 3;
                    }
                }

            } else {
                return 1;
            }


        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.d("limbo", e.toString());
            //如果捕捉到错误则通知UI线程
        }
        return 2;
    }
}
