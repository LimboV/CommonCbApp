package com.commoncb.seck.commoncbapp.activity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.commoncb.seck.commoncbapp.DB.DbHelper;
import com.commoncb.seck.commoncbapp.R;
import com.commoncb.seck.commoncbapp.model.Login;
import com.commoncb.seck.commoncbapp.modle1.Count;
import com.commoncb.seck.commoncbapp.modle1.New_Meter;
import com.commoncb.seck.commoncbapp.modle1.Upload_Meter;
import com.commoncb.seck.commoncbapp.modle1.Xq;
import com.commoncb.seck.commoncbapp.service.LocationApplication;
import com.commoncb.seck.commoncbapp.service.LocationService;
import com.commoncb.seck.commoncbapp.utils.BlueToothActivity;
import com.commoncb.seck.commoncbapp.utils.BluetoothConnectThread;
import com.commoncb.seck.commoncbapp.utils.HintDialog;
import com.commoncb.seck.commoncbapp.utils.HzyUtils;
import com.commoncb.seck.commoncbapp.utils.NetworkLayer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.commoncb.seck.commoncbapp.activity.MainActivity.muser;
import static com.commoncb.seck.commoncbapp.activity.MainActivity.url;
import static com.commoncb.seck.commoncbapp.activity.MainActivity.user;
import static com.commoncb.seck.commoncbapp.activity.UpLoadActivity.client;

/**
 * Created by limbo on 2018/5/23.
 */

public class MenuActivity extends BaseActivity {
    @BindView(R.id.gvMain)
    GridView gvMain;

    @BindView(R.id.tv_topMsg)
    TextView tv_topMsg;
    @BindView(R.id.textView)
    TextView textView;

    @BindView(R.id.menuActivity_btn_exit)
    Button menuActivity_btn_exit;

    public static String scanResult = "";
    public static int X_FLAG = 0;

    private long mPressedTime;
    private int month;

    private LocationService locationService;
    public static boolean locationStartFlag = false;//定位线程标志位

    public static boolean btAuto = false;//蓝牙自动接收标志位
    public static boolean blutoothEnabled = false, TIMEOUT = false;
    private String permissionInfo;
    static public BluetoothConnectThread netThread = null;
    public static String Cjj_CB_MSG = "";//存放数据
    public static int timeDelayMax = 8000;//超时时间长度

    public static String lon = "", lat = "";
    public static List<Xq> sXqList;

    Thread mThread;//实时上传
    public static Thread monitorThread; // 线程监控
    public static boolean activityDestoried = false; //activity销毁标志
    public static boolean threadErrorFlag = false; //线程报错标志
    public static boolean monitorFlag = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newmenu);
        ButterKnife.bind(this);

        Calendar c = Calendar.getInstance();
        final Date d = c.getTime();
        final DateFormat df1 = new SimpleDateFormat("MM");
        final DateFormat df2 = new SimpleDateFormat("dd");
        loadMsg();
        if (month != Integer.parseInt(df1.format(d))) {
            if (Integer.parseInt(df2.format(d)) > 25) {
                new SweetAlertDialog(MenuActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("提示")
                        .setContentText("下载新线号前请删除原有线号！")
                        .setConfirmText("确认")
                        .setCancelText("本月不再提醒")
                        .showCancelButton(true)
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.cancel();
                                saveMsg(Integer.parseInt(df1.format(d)));
                            }
                        })
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.cancel();
                            }
                        }).show();
            }
        }


        int flag = copyFilesFassets(MenuActivity.this, "Seck_4Db.db", Environment.getExternalStorageDirectory().getAbsolutePath() + "/SeckLoRaDB");
        switch (flag) {
            case 0://完成
                Toast.makeText(MenuActivity.this, "DB文件创建完成", Toast.LENGTH_LONG).show();
                break;
            case 1://不存在
                Toast.makeText(MenuActivity.this, "SD卡不存在", Toast.LENGTH_LONG).show();
                break;
            case 2://报错
                Toast.makeText(MenuActivity.this, "DB文件创建失败", Toast.LENGTH_LONG).show();
                break;
            case 3://已存在
                //                Toast.makeText(MenuActivity.this, "DB文件已存在", Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }
        DbHelper.getdb();//获取数据库实例
        tv_topMsg.setText("抄表员:" + HzyUtils.isNull(muser.getDatas().get(0).getUserName()));
        textView.setText("版本:" + this.getAppVersionName(this).substring(this.getAppVersionName(this).indexOf(".") + 1));
        if (MainActivity.url.equals("http://222.77.71.74:6050/CbService.asmx")) {
            textView.append("\n正式版IP端口");
        } else if (MainActivity.url.equals("http://222.77.71.74:6050/TestPhoneService/CbService.asmx")) {
            textView.append("\n巡查版IP端口");
        } else if (MainActivity.url.equals("http://222.77.71.74:6050/DemoPhoneService/CbService.asmx")) {
            textView.append("\n测试版IP端口");
        }
        gvMain.setAdapter(new imageAdapter(this));
        gvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent i;
                if (System.currentTimeMillis() - mPressedTime > 2000) {
                    switch (position) {
                        /**
                         * 蓝牙连接
                         */
                        case 0:
                            if (MenuActivity.netThread == null) {
                                MenuActivity.resetNetwork();
                                networkConnect();
                                return;
                            } else {
                                MenuActivity.resetNetwork();
                                HintDialog.ShowHintDialog(MenuActivity.this, "蓝牙连接已断开", "提示");
                                gvMain.setAdapter(new imageAdapter(MenuActivity.this));
                            }
                            break;

                        /**
                         * 数据上传
                         */
                        case 1:
                            i = new Intent(MenuActivity.this, UpLoadActivity.class);
                            startActivity(i);
                            break;
                        /**
                         * 下载小区档案
                         */
                        case 2:
                            i = new Intent(MenuActivity.this, DownLoadActivity.class);
                            startActivity(i);
                            break;
                        /**
                         * 水表抄表
                         */
                        case 3:
                            i = new Intent(MenuActivity.this, XqListActivity.class);
                            i.putExtra("flag", 1);
                            startActivity(i);
                            break;
                        /**
                         * 统计分析
                         */
                        case 4:
                            i = new Intent(MenuActivity.this, TJActivity.class);
                            i.putExtra("flag", 0);
                            startActivity(i);
                            break;
                        /**
                         * 修改密码
                         */
                        case 5:
                            i = new Intent(MenuActivity.this, ChangePwActivity.class);
                            startActivity(i);
                            break;
                    }
                    mPressedTime = System.currentTimeMillis();
                }
            }
        });


        menuActivity_btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if (!locationStartFlag) {
            locationStartFlag = true;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Log.d("limbo", "打开定位服务");
                        if (locationService == null) {
                            locationService = ((LocationApplication) getApplication()).locationService;
                            //获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
                            locationService.registerListener(mListener);
                            //注册监听
                            int type = getIntent().getIntExtra("from", 0);
                            if (type == 0) {
                                locationService.setLocationOption(locationService.getDefaultLocationClientOption());
                            } else if (type == 1) {
                                locationService.setLocationOption(locationService.getOption());
                            }
                            //locationService.start();// 定位SDK,开始抄表后开始定位
                        }
                    } catch (Exception e) {
                        Log.d("limbo", e.toString());
                    }
                }
            }).start();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (locationStartFlag) {
                            Thread.sleep(5000);
                            locationService.start();// 定位SDK,开始抄表后开始定位
                            // start之后会默认发起一次定位请求，开发者无须判断isstart并主动调用request
                            Thread.sleep(25000);
                            locationService.stop();

                            //                        Thread.sleep(5000);
                        }
                    } catch (Exception e) {
                        Log.d("limbo", e.toString());
                    }
                }
            }).start();
        }
        GetXqName();
        if (mThread == null) {
            upload();
        } else if (!MainActivity.upLoadAuto) {
            MainActivity.upLoadAuto = true;
            upload();
        }

    }

    private class imageAdapter extends BaseAdapter {
        Context mContext;
        //        private String Titls[] = {"未连接", "工单查询", "事件记录", "数据上传", "数据下载", "水表抄表", "统计分析", "小区档案", "信息浏览"};
        //        private String Titls2[] = {"已连接", "工单查询", "事件记录", "数据上传", "数据下载", "水表抄表", "统计分析", "小区档案", "信息浏览"};
        private String Titls[] = {"未连接", "数据上传", "数据下载", "水表抄表", "统计分析", "修改密码"};
        private String Titls2[] = {"已连接", "数据上传", "数据下载", "水表抄表", "统计分析", "修改密码"};
        /*private int Imgs[] = {R.drawable.blueno, R.drawable.menu_item2, R.drawable.menu_item3, R.drawable.menu_item4, R.drawable.menu_item5,
                R.drawable.menu_item6, R.drawable.menu_item7, R.drawable.menu_item8, R.drawable.menu_item9};*/
        private int Imgs[] = {R.drawable.blutoothno, R.drawable.menu_item4, R.drawable.menu_item5,
                R.drawable.menu_item6, R.drawable.menu_item7, R.drawable.changepw};

        public imageAdapter(Context c) {
            mContext = c;
        }

        @Override
        public int getCount() {
            return 6;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            View v;
            if (convertView == null) {
                LayoutInflater li = getLayoutInflater();
                v = li.inflate(R.layout.startview, null);
                TextView tv = (TextView) v.findViewById(R.id.icon_text);
                ImageView iv = (ImageView) v.findViewById(R.id.icon_image);

                if (MenuActivity.netThread == null) {
                    tv.setText(Titls[position]);
                } else {
                    tv.setText(Titls2[position]);
                }

                iv.setImageResource(Imgs[position]);

                if (MenuActivity.netThread != null && position == 0)
                    iv.setImageResource(R.drawable.blue);
            } else {
                v = convertView;
            }
            return v;
        }
    }



    /***
     * Stop location service
     */
    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
       /* locationService.unregisterListener(mListener); //注销掉监听
        locationService.stop(); //停止定位服务*/
        super.onStop();
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        // -----------location config ------------
        locationService = ((LocationApplication) getApplication()).locationService;
        //获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
        locationService.registerListener(mListener);
        //注册监听
        int type = getIntent().getIntExtra("from", 0);
        if (type == 0) {
            locationService.setLocationOption(locationService.getDefaultLocationClientOption());
        } else if (type == 1) {
            locationService.setLocationOption(locationService.getOption());
        }
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
                        //                        Log.d("limbo", fileName);
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

    /*****
     *
     * 定位结果回调，重写onReceiveLocation方法，可以直接拷贝如下代码到自己工程中修改
     *
     */
    private BDAbstractLocationListener mListener = new BDAbstractLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // TODO Auto-generated method stub
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                StringBuffer sb = new StringBuffer(256);
                sb.append("time : " + location.getTime());
                /**
                 * 时间也可以使用systemClock.elapsedRealtime()方法 获取的是自从开机以来，每次回调的时间；
                 * location.getTime() 是指服务端出本次结果的时间，如果位置不发生变化，则时间不变
                 */
                double latitude = location.getLatitude();    //获取纬度信息
                double longitude = location.getLongitude();    //获取经度信息
                sb.append("\nlatitude : ");
                sb.append(latitude);
                sb.append("\nlontitude : ");
                sb.append(location.getLongitude());

                sb.append("\n定位类型:" + location.getLocType());//打印定位类型，用于调试

                if ((latitude + "").contains("4.9E-324") || (longitude + "").contains("4.9E-324")) {
                    Log.d(BaseActivity.TAG, "lat:" + lat + "lon:" + lon);
                } else if (location.getLocType() == 161){
                    lat = latitude + "";// 纬度
                    lon = longitude + "";// 经度
                    UploadLoaction(longitude + "", latitude + "", location.getTime(), user);
                    Log.d(BaseActivity.TAG, sb.toString());
                } else {
                    lat = latitude + "";// 纬度
                    lon = longitude + "";// 经度
                    UploadLoaction(location.getLongitude() + "", location.getLatitude() + "", location.getTime(), user);
                    if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                        sb.append("\nspeed : ");
                        sb.append(location.getSpeed());// 速度 单位：km/h
                        sb.append("\nsatellite : ");
                        sb.append(location.getSatelliteNumber());// 卫星数目
                        sb.append("\nheight : ");
                        sb.append(location.getAltitude());// 海拔高度 单位：米
                        sb.append("\ngps status : ");
                        sb.append(location.getGpsAccuracyStatus());// *****gps质量判断*****
                        sb.append("\ndescribe : ");
                        sb.append("gps定位成功");
                    } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                        // 运营商信息
                        if (location.hasAltitude()) {// *****如果有海拔高度*****
                            sb.append("\nheight : ");
                            sb.append(location.getAltitude());// 单位：米
                        }
                        sb.append("\noperationers : ");// 运营商信息
                        sb.append(location.getOperators());
                        sb.append("\ndescribe : ");
                        sb.append("网络定位成功");
                    } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                        sb.append("\ndescribe : ");
                        sb.append("离线定位成功，离线定位结果也是有效的");
                    } else if (location.getLocType() == BDLocation.TypeServerError) {
                        sb.append("\ndescribe : ");
                        sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
                    } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                        sb.append("\ndescribe : ");
                        sb.append("网络不同导致定位失败，请检查网络是否通畅");
                    } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                        sb.append("\ndescribe : ");
                        sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
                    }
                }
                Log.d("limbo", sb.toString());
                locationService.stop();


            } else {
            }
        }

    };

    @Override
    protected void onDestroy() {
        locationStartFlag = false;
        locationService.unregisterListener(mListener);
        locationService.stop();
        activityDestoried = true;
        monitorFlag = false;
        mThread = null;
        MainActivity.upLoadAuto = false;
        resetNetwork();
        super.onDestroy();
    }

    /**
     * 上传GPS
     */
    private void UploadLoaction(final String lng, final String lat, final String time, final String UserId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    /*String time;
                    Calendar c = Calendar.getInstance();
                    Date d = c.getTime();
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    time = df.format(d);*/
                    OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象
                    Request request = new Request.Builder()
                            .url(url + "/UpdateGPS?lng=" + lng + "&lat=" + lat + "&Time=" + time + "&UserId=" + UserId)//请求接口。如果需要传参拼接到接口后面。
                            //                            .url("http://www.baidu.com")//请求接口。如果需要传参拼接到接口后面。
                            .build();//创建Request 对象
                    Response response = null;
                    response = client.newCall(request).execute();//得到Response 对象
                    if (response.isSuccessful()) {
                        //                        Log.d("limboV", "response.code()==" + response.code());
                        //                        Log.d("limboV", "response.message()==" + response.message());
                        //                        Log.d("limboV", "res==" + response.body().string());
                        //此时的代码执行在子线程，修改UI的操作请使用handler跳转到UI线程。
                        String responseX = response.body().string().replace("<?xml version=\"1.0\" encoding=\"utf-8\"?>", "");
                        responseX = responseX.replace("<string xmlns=\"http://tempuri.org/\">", "");
                        responseX = responseX.replace("</string>", "");
                        //                        Log.d("limbo", responseX);
                        Gson gson = new Gson();//使用Gson解析
                        Login muser = gson.fromJson(responseX, Login.class);
                        Message message = new Message();
                        message.what = 0x00;
                        message.obj = muser;
                        handler.sendMessage(message);
                    } else {
                        Message message = new Message();
                        message.what = 0x99;
                        handler.sendMessage(message);
                    }
                } catch (Exception e) {
                    Log.d("limbo", e.toString());
                    Message message = new Message();
                    message.what = 0x99;
                    handler.sendMessage(message);

                }
            }
        }).start();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            /*
             * final String[] menuItems = new String[] { "1. 抄    表",
             * "2. 查    询", "3. 其他选项", "4. 参数设置" };
             */
            if (resultCode == BluetoothConnectThread.NETWORK_FAILED) {
                HintDialog.ShowHintDialog(this, "设备连接错误", "错误");
                MenuActivity.resetNetwork();
            } else if (resultCode == BluetoothConnectThread.NETWORK_CONNECTED) {
                HintDialog.ShowHintDialog(this, "蓝牙连接成功", "提示");
                gvMain.setAdapter(new imageAdapter(this));
            } else if (resultCode == BluetoothConnectThread.METER) {
            }
        }
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
                    Thread.sleep(1000);//两秒后再次检测
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

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x00:
                    //                    Log.d("limbo", "GPS上传完成...");
                    //                    Toast.makeText(MenuActivity.this, "GPS上传完成...", Toast.LENGTH_LONG).show();
                    break;
                case 0x99:
                    Log.d("limbo", "GPS上传失败...");
                    //                    Toast.makeText(MenuActivity.this, "GPS上传失败...", Toast.LENGTH_LONG).show();
                   /* new SweetAlertDialog(MenuActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("错误")
                            .setContentText("网络异常!")
                            .show();*/
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 小区号及列表对照单获取
     */
    public static void GetXqName() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象
                    Request request = new Request.Builder()
                            .url(url + "/GetXqName")//请求接口。如果需要传参拼接到接口后面。
                            //                            .url("http://www.baidu.com")//请求接口。如果需要传参拼接到接口后面。
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
                        sXqList = gson.fromJson(responseX, new TypeToken<List<Xq>>() {
                        }.getType());
                        //                        Message message = new Message();
                        //                        message.what = 0x00;
                        //                        handler.sendMessage(message);
                    } else {
                        Log.d("limbo", "下载小区列表出错");
                        //                        Message message = new Message();
                        //                        message.what = 0x99;
                        //                        handler.sendMessage(message);
                    }
                } catch (Exception e) {
                    Log.d("limbo", "获取小区名对应列表:" + e.toString());
                    //                    Message message = new Message();
                    //                    message.what = 0x99;
                    //                    handler.sendMessage(message);

                }
            }
        }).start();
    }

    /**
     * 重新设置网络状态
     */
    public static void resetNetwork() {
        if (netThread != null) {
            try {
                netThread.cancel();
            } catch (IOException e) {
            }

            netThread = null;
        }
    }

    public static void sendCmd(String sendMsg) {
        //        Log.d("limbo", "sendCmd函数发送:" + sendMsg);
        if (sendMsg.length() % 2 == 1) {
            sendMsg = sendMsg + "0";
        }
        MenuActivity.Cjj_CB_MSG = "";//清空记录
        byte[] bos = HzyUtils.getHexBytes(sendMsg);
        byte[] bos_new = new byte[bos.length];
        int i, n;
        n = 0;
        for (i = 0; i < bos.length; i++) {
            bos_new[n] = bos[i];
            n++;
        }
        try {
            MenuActivity.netThread.write(bos_new);
        } catch (Exception e) {
            Log.d("limbo", e.toString());
        }
    }


    public static String getAppVersionName(Context context) {
        String versionName = "";
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);

            versionName = pi.versionName;

            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
        }
        return versionName;
    }

    /**
     * 获取当前本地apk的版本
     *
     * @param mContext
     * @return
     */
    public static int getVersionCode(Context mContext) {
        int versionCode = 0;
        try {
            //获取软件版本号，对应AndroidManifest.xml下android:versionCode
            versionCode = mContext.getPackageManager().
                    getPackageInfo(mContext.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * 实时上传数据
     */
    private void upload() {
        Log.d("limbo", "开始后台上传.");
        final Runnable uploadrunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    TelephonyManager tm = (TelephonyManager) MenuActivity.this.getSystemService(Context.TELEPHONY_SERVICE);
                    if (ActivityCompat.checkSelfPermission(MenuActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        ActivityCompat.requestPermissions(MenuActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
                        return;
                    }
                    String deviceid = tm.getSimSerialNumber();//获取智能设备唯一编号
                    String te1 = tm.getLine1Number();//获取本机号码
                    String imei = tm.getDeviceId();//获得SIM卡的序号-国际移动用户识别码。
                    if (HzyUtils.isEmpty(te1)) {
                        te1 = "";
                    }
                    if (HzyUtils.isEmpty(imei)) {
                        imei = "";
                    }
                    //        String imsi = tm.getSubscriberId();//得到用户Id

                    while (MainActivity.upLoadAuto) {
                        Log.d("limbo", "新一轮上传");
                        //                        Thread.sleep(60000);//线程休眠等待时间
                        List<New_Meter> new_meterNoPicMeter;
                        new_meterNoPicMeter = DbHelper.getMetersUploadNoPic();
                        ArrayList<Upload_Meter> upload_meters = new ArrayList<>();
                        if (new_meterNoPicMeter != null && new_meterNoPicMeter.size() > 0) {
                            /**
                             * for循环上传表信息，分为-无图 及 有图
                             */
                            for (New_Meter new_meter : new_meterNoPicMeter) {
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
                                meter.setUserState(new_meter.getUserState());
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
                                //                            meter.setPicString(HzyUtils.bitmapToBase64(DbHelper.getPicByKH(new_meter.getKH())));
                                upload_meters.add(meter);
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
                                    Count c = gson1.fromJson(responseX, Count.class);
                                    //                            responseX = "0";
                                    if (Integer.parseInt(c.getCount()) == 0) {
                                        DbHelper.newChangeUploadFlag(new_meterNoPicMeter, c.getFailList());
                                        //                                    noPicResult = noPicResult + "(无新数据)  ";
                                    } else if (Integer.parseInt(c.getCount()) < 0) {
                                        //                                    noPicResult = noPicResult + "(服务器接收错误)  ";
                                    } else {
                                        /**
                                         * 更改上传标志位
                                         */
                                        DbHelper.newChangeUploadFlag(new_meterNoPicMeter, c.getFailList());
                                        //noPicResult = noPicResult + "\n线号:" + XH + "(成功)  ";
                                        Log.d("limbo", "上传无图表数目:" + c.getCount());
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
                        }

                        /**
                         * --------------------传图
                         */
                        List<New_Meter> new_meterPicMeter = DbHelper.getMetersUploadPic();//获取线号下 所有有图，未上传表
                        int countPic = 0;//保存上传照片条数
                        if (new_meterPicMeter != null && new_meterPicMeter.size() > 0) {
                            if (new_meterPicMeter != null) {
                                for (New_Meter new_meter : new_meterPicMeter) {
                                    //                                    if (!MainActivity.upLoadAuto) {
                                    //                                        Log.d("limbo", "线程结束");
                                    //                                        mThread = null;
                                    //                                        return;
                                    //                                    }
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
                                    //                                    meter.setPicString1(HzyUtils.bitmapToBase64(DbHelper.getPicByKH1(new_meter.getKH())));

                                    try {
                                        String base64 = Base64.encodeToString(DbHelper.getPicByKH(new_meter.getKH(), 0), Base64.DEFAULT);
                                        //                                    Log.d("limbo", "上传图片大小：" + base64.length() / 1024 + "K");
                                        meter.setPicString1(base64);
                                    } catch (Exception e) {
                                        Log.e("limbo", "pic1Error:" + e.toString());
                                    }
                                    try {
                                        String base64 = Base64.encodeToString(DbHelper.getPicByKH(new_meter.getKH(), 1), Base64.DEFAULT);
                                        meter.setPicString2(base64);
                                    } catch (Exception e) {
                                        Log.e("limbo", "pic2Error:" + e.toString());
                                    }
                                    try {
                                        String base64 = Base64.encodeToString(DbHelper.getPicByKH(new_meter.getKH(), 2), Base64.DEFAULT);
                                        meter.setPicString3(base64);
                                    } catch (Exception e) {
                                        Log.e("limbo", "pic3Error:" + e.toString());
                                    }
                                    meter.setDeviceid(deviceid);
                                    meter.setTe1(te1);
                                    meter.setImei(imei);

                                    //申明给服务端传递一个json串
                                    //创建一个OkHttpClient对象
                                    //创建一个RequestBody(参数1：数据类型 参数2传递的json串)
                                    //json为String类型的json数据
                                    Gson gson = new Gson();
                                    if (gson != null) {
                                        String gsonString = gson.toJson(meter);
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
                                            Count c = gson1.fromJson(responseX, Count.class);
                                            //                            responseX = "0";
                                            if (Integer.parseInt(c.getCount()) == 0) {
                                                DbHelper.newChangeUploadFlag(new_meter);
                                                //                                            PicResult = PicResult + "\n表号：" + meter.getMeterNumber() + "(无新数据)  ";
                                            } else if (Integer.parseInt(c.getCount()) < 0) {
                                                //                                            PicResult = PicResult + "\n表号：" + meter.getMeterNumber() + "(服务器接收错误)  ";
                                            } else {
                                                //                                    PicResult = PicResult + "\n线号:" + XH + "(成功)  ";
                                                /**
                                                 * 更改上传标志位
                                                 */
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
                                    Thread.sleep(300);//线程休眠等待时间
                                }
                                Log.d("limbo", "上传照片数:" + countPic);
                            }

                        }


                        MainActivity.upLoadAuto = false;

                        new_meterNoPicMeter = DbHelper.getMetersUploadNoPic();
                        if (new_meterNoPicMeter.size() < 30) {
                            for (int i = 1; i < 60; i++) {
                                if (!activityDestoried) {
                                    Thread.sleep(1000);
                                    //                                    Log.d("limbo", "正在等待的第" + i + "秒");
                                }
                            }
                            Log.d("limbo", "结束本轮上传.");
                        } else {
                            Log.d("limbo", "剩余表数大于30只，立刻开始下一轮上传.");
                        }

                        if (!activityDestoried)
                            MainActivity.upLoadAuto = true;
                        else
                            MainActivity.upLoadAuto = false;
                    }
                } catch (Exception e) {
                    Log.d("limbo", e.toString());
                    Log.d("limbo", "实时上传出现异常...");
                    mThread.interrupt();

                }
            }
        };
        monitorThread = new Thread(new Runnable() {
            @Override
            public void run() {
                mThread = new Thread(uploadrunnable);
                mThread.start();
                while (monitorFlag) {
                    Log.d("limob", "threadErrorFlag: " + threadErrorFlag + "mThread.isAlive():" + mThread.isAlive());
                    if (threadErrorFlag || (!mThread.isAlive())) {
                        MainActivity.upLoadAuto = true;
                        threadErrorFlag = false;
                        mThread = new Thread(uploadrunnable);
                        mThread.start();
                        Log.d("limno", "上传出错重新开启线程！");
                    }
                    for (int i = 1; i < 60; i++) {
                        if (!activityDestoried) {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            //                            Log.d("limbo", "监听线程正在等待的第" + i + "秒");
                        }
                    }
                }
            }
        });
        monitorThread.start();

    }

    /**
     * 使用SharePreferences保存用户信息
     */
    public void saveMsg(int month) {

        SharedPreferences.Editor editor = getSharedPreferences("user_msg", MODE_PRIVATE).edit();
        editor.putInt("month", month);
        editor.commit();
    }


    /**
     * 加载用户信息
     */

    public void loadMsg() {
        SharedPreferences pref = getSharedPreferences("user_msg", MODE_PRIVATE);
        month = pref.getInt("month", 0);
    }
}