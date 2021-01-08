package com.commoncb.seck.commoncbapp.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.commoncb.seck.commoncbapp.R;
import com.commoncb.seck.commoncbapp.modle1.New_Login;
import com.commoncb.seck.commoncbapp.modle1.UpdataModle;
import com.commoncb.seck.commoncbapp.service.UpdateService;
import com.commoncb.seck.commoncbapp.utils.HzyUtils;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends BaseActivity {
    @BindView(R.id.et_user)
    EditText et_user;
    @BindView(R.id.et_password)
    EditText et_password;
    @BindView(R.id.et_url)
    EditText et_url;
    @BindView(R.id.btn_login)
    Button btn_login;
    @BindView(R.id.btn_exit)
    ImageButton btn_exit;
    @BindView(R.id.cb_save)
    CheckBox cb_save;
    public static String url = "", user = "", password = "";
    public static New_Login muser = new New_Login();
    private final int SDK_PERMISSION_REQUEST = 127;

    public static boolean upLoadAuto = true;//自动上传标志位
    public static boolean AllBtAuto = true;//蓝牙自动接收标志位
    public static boolean timeOut = false;
    private long mPressedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        AllBtAuto = true;
        upLoadAuto = true;
        cb_save.setChecked(true);
        /**
         * 登陆
         */
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (System.currentTimeMillis() - mPressedTime > 2000) {
                    user = et_user.getText().toString().trim();
                    password = et_password.getText().toString().trim();
                    if(!et_url.getText().toString().trim().contains("http://")){
                        et_url.setText("http://"+et_url.getText().toString().trim());
                    }
                    url =et_url.getText().toString();
                    if (!isLocationEnabled()) {
                        new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("提示!")
                                .setContentText("请在系统设置中打开定位服务!")
                                .show();
                        return;
                    } else {
                        login();
                    }
                    mPressedTime = System.currentTimeMillis();
                }
            }
        });

        /**
         * 清除输入
         */
        et_user.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    et_user.clearFocus();
                }
                return false;
            }
        });
        et_password.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    et_password.clearFocus();
                }
                return false;
            }
        });

        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("提示")
                        .setContentText("请确认是否要退出?")
                        .setCancelText("取消")
                        .setConfirmText("确认")
                        .showCancelButton(true)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                Log.d("limboV", "sure");
                                sweetAlertDialog.cancel();
                                finish();
                            }
                        })
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                Log.d("limboV", "cancel");
                               /* new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("取消!")
                                        .setContentText("你已经取消退出!")
                                        .show();*/
                                sDialog.cancel();
                            }
                        }).show();
            }
        });
        loadMsg();
        //        if (et_url.getText().toString().equals("http://211.143.169.106:6050/TestPhoneService/CbService.asmx")) {
        //            测试版
        //            getUpdataMsg("http://114.215.237.235:89/");
        //        } else {
        //正式版
        getUpdataMsg("http://114.215.237.235:88/");
        //        }


        Drawable drawable = getResources().getDrawable(R.drawable.login_user);
        drawable.setBounds(0, 0, 30, 30);
        et_user.setCompoundDrawables(drawable, null, null, null);
        drawable = getResources().getDrawable(R.drawable.login_pass);
        drawable.setBounds(0, 0, 30, 30);
        et_password.setCompoundDrawables(drawable, null, null, null);
        drawable = getResources().getDrawable(R.drawable.login_http);
        drawable.setBounds(0, 0, 30, 30);
        et_url.setCompoundDrawables(drawable, null, null, null);
        getPersimmions();
    }

    @Override
    public void onBackPressed() {
        btn_exit.performClick();
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        upLoadAuto = false;
        AllBtAuto = false;
        super.onDestroy();
    }

    /**
     * 使用SharePreferences保存用户信息
     */
    public void saveMsg(String username, String password, String http) {

        SharedPreferences.Editor editor = getSharedPreferences("user_msg", MODE_PRIVATE).edit();
        editor.putString("username", username);
        editor.putString("password", password);
        editor.putString("http", http);
        editor.commit();
    }


    /**
     * 加载用户信息
     */

    public void loadMsg() {
        SharedPreferences pref = getSharedPreferences("user_msg", MODE_PRIVATE);
        et_user.setText(pref.getString("username", ""));
        et_password.setText(pref.getString("password", ""));
        et_url.setText(pref.getString("http", ""));
        if (et_url.getText().toString().equals("")) {
            et_url.setText("http://:6050/CbService.asmx");//泉州抄表
        }
//        et_url.setText("http://222.77.71.74:6050/CbService.asmx");//泉州正式抄表
//                        et_url.setText("http://211.143.169.106:6050/TestPhoneService/CbService.asmx");//泉州巡查
//                et_url.setText("http://222.77.71.74:6050/DemoPhoneService/CbService.asmx");//测试1
//                et_url.setText("http://218.75.104.131:9606/QZPhoneService/PhoneWebService.asmx");//测试2
        //        et_url.setVisibility(View.GONE);
    }

    /**
     * 登陆验证
     */
    private void login() {
        HzyUtils.showProgressDialog1(this,"正在登陆...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象
                    Request request = new Request.Builder()
                            .url(url + "/GetXhs?UserID=" + user + "&Password=" + password)//请求接口。如果需要传参拼接到接口后面。
                            .build();//创建Request 对象
                    Log.d(BaseActivity.TAG,url + "/GetXhs?UserID=" + user + "&Password=" + password);
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
                            Toast.makeText(MainActivity.this, "无用户信息", Toast.LENGTH_LONG).show();
                            Log.d("limbo", "fail");
                        } else {
                            Gson gson = new Gson();//使用Gson解析
                            New_Login muser = gson.fromJson(responseX, New_Login.class);
                            Message message = new Message();
                            message.what = 0x00;
                            message.obj = muser;
                            handler.sendMessage(message);
                        }

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

    /**
     * 更新情况获取
     */
    private void getUpdataMsg(final String url) {
        HzyUtils.showProgressDialog1(this,"正在获取更新信息...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象
                    Request request = new Request.Builder()
                            .url(url + "version/Service1.asmx/getVersion")//请求接口。如果需要传参拼接到接口后面。
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
                        UpdataModle updataModle = gson.fromJson(responseX, UpdataModle.class);
                        Message message = new Message();
                        message.what = 0x01;
                        message.obj = updataModle;
                        handler.sendMessage(message);
                    } else {
                        Message message = new Message();
                        message.what = 0x98;
                        handler.sendMessage(message);
                    }
                } catch (Exception e) {
                    Log.d("limbo", e.toString());
                    Message message = new Message();
                    message.what = 0x98;
                    handler.sendMessage(message);
                }
            }
        }).start();

    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x00:
                    HzyUtils.closeProgressDialog();
                    muser = (New_Login) msg.obj;
                    if (muser.getFlag().equals("success") && muser != null) {
                        Log.d("limbo", "success:userid=" + HzyUtils.isNull(muser.getDatas().get(0).getUserID()));
                        if (cb_save.isChecked()) {
                            Log.d("limbo", "save");
                            saveMsg(user, password, url);
                        } else {
                            Log.d("limbo", "not save");
                            //                            saveMsg(user, password, "");
                        }

                        Intent i = new Intent(MainActivity.this, MenuActivity.class);
                        startActivity(i);
                    } else {
                        Toast.makeText(MainActivity.this, "无用户信息", Toast.LENGTH_LONG).show();
                        Log.d("limbo", "fail");
                    }
                    break;

                //更新app
                case 0x01:
                    HzyUtils.closeProgressDialog();
                    final UpdataModle updataModle = (UpdataModle) msg.obj;
                    String note = updataModle.getUpgradeinfo();//更新备注
                    if (updataModle.getAppname().equals("泉州自来水有限公司智能抄表系统")) {
                        int versionCode = MenuActivity.getVersionCode(MainActivity.this);
                        if (versionCode < Integer.parseInt(updataModle.getServerVersion())) {
                            //是否需要更新
                            if (updataModle.getLastForce().equals("1")) {
                                //强制升级
                            } else {
                                //不强制升级
                            }
                            new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("有新版本")
                                    .setContentText("更新日志:" + note + "\n是否需要升级?")
                                    .setCancelText("取消")
                                    .setConfirmText("升级")
                                    .showCancelButton(true)
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            Log.d("limboV", "sure");
                                            sweetAlertDialog.cancel();

                                            Intent mIntent = new Intent(MainActivity.this, UpdateService.class);
                                            mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            //传递数据
                                            mIntent.putExtra("appname", updataModle.getAppname());
                                            mIntent.putExtra("appurl", updataModle.getUpdateurl());
                                            MainActivity.this.startService(mIntent);
                                        }
                                    })
                                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            Log.d("limboV", "cancel");
                               /* new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("取消!")
                                        .setContentText("你已经取消退出!")
                                        .show();*/
                                            sDialog.cancel();
                                            clearUpateFile(MainActivity.this);
                                        }
                                    }).show();

                        }
                    }


                    break;
                case 0x98:
                    HzyUtils.closeProgressDialog();
                    new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("错误")
                            .setContentText("获取更新版本失败,下次登陆时会重新尝试获取。")
                            .show();
                    break;
                case 0x99:
                    HzyUtils.closeProgressDialog();
                    new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("错误")
                            .setContentText("登录失败!\n请确认密码、账号是否正确，\n网络连接是否正常。")
                            .show();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 清理升级文件
     *
     * @param context
     */
    private void clearUpateFile(final Context context) {
        File updateDir;
        File updateFile;
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {
            updateDir = new File(Environment.getExternalStorageDirectory(), UpdataModle.downloadDir);
        } else {
            updateDir = context.getFilesDir();
        }
        updateFile = new File(updateDir.getPath(), context.getResources()
                .getString(R.string.app_name) + ".apk");
        if (updateFile.exists()) {
            Log.d("update", "升级包存在，删除升级包");
            updateFile.delete();
        } else {
            Log.d("update", "升级包不存在，不用删除升级包");
        }
    }

    public boolean isLocationEnabled() {
        int locationMode = 0;
        String locationProviders;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        } else {
            locationProviders = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }
    @TargetApi(23)
    private void getPersimmions() {
        //        Log.d("limbo", "Build.VERSION.SDK_INT：" + Build.VERSION.SDK_INT);
        //        Log.d("limbo", "Build.VERSION_CODES.M：" + Build.VERSION_CODES.M);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //Android6.0以上，需要动态申请运行时权限
            ArrayList<String> permissions = new ArrayList<String>();
            /***
             * 定位权限为必须权限，用户如果禁止，则每次进入都会申请
             */
            // 定位精确位置
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_NETWORK_STATE);
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_WIFI_STATE);
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CHANGE_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.CHANGE_WIFI_STATE);
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.INTERNET);
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS);
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS);
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.READ_PHONE_STATE);
            }// 读取电话状态权限
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.CAMERA);
            }// 相机
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.VIBRATE) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.VIBRATE);
            }// 相机
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.BLUETOOTH);
            }// 藍牙
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.BLUETOOTH_ADMIN);
            }// 藍牙
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.RECORD_AUDIO);
            }//录音
            //8.0适配
            boolean haveInstallPermission;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                haveInstallPermission = getPackageManager().canRequestPackageInstalls();
                //没有权限则进行权限请求
                if (!haveInstallPermission) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.REQUEST_INSTALL_PACKAGES) != PackageManager.PERMISSION_GRANTED) {
                        permissions.add(Manifest.permission.REQUEST_INSTALL_PACKAGES);
                    }// 允许安装位置来源应用
                }
            }

            /*
             * 读写权限和电话状态权限非必要权限(建议授予)只会申请一次，用户同意或者禁止，只会弹一次
             */
            // 读写权限
            /*if (addPermission(permissions, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissionInfo += "Manifest.permission.WRITE_EXTERNAL_STORAGE Deny \n";
            }
            if (addPermission(permissions, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                permissionInfo += "Manifest.permission.READ_EXTERNAL_STORAGE Deny \n";
            }*/
            /*if (addPermission(permissions, Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS)) {
                permissionInfo += "Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS Deny \n";
            }
            // 读取电话状态权限
            if (addPermission(permissions, Manifest.permission.READ_PHONE_STATE)) {
                permissionInfo += "Manifest.permission.READ_PHONE_STATE Deny \n";
            }*/

            if (permissions.size() > 0) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), SDK_PERMISSION_REQUEST);
            }
        } else {
            //Android6.0以下
        }
    }

    @TargetApi(23)
    private boolean addPermission(ArrayList<String> permissionsList, String permission) {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) { // 如果应用没有获得对应权限,则添加到列表中,准备批量申请
            if (shouldShowRequestPermissionRationale(permission)) {
                return true;
            } else {
                permissionsList.add(permission);
                return false;
            }

        } else {
            return true;
        }
    }

    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // TODO Auto-generated method stub
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


}
