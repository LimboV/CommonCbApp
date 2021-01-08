package com.commoncb.seck.commoncbapp.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Window;

public class BaseActivity extends Activity {
    public static String TAG = "limbo";
    private static volatile Activity mCurrentActivity;
    private static ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Log.d(TAG, "当前启动的Activity名称为: " + getClass().getSimpleName());
        setCurrentActivity(this);
    }
    @Override
    protected void onResume() {
        setCurrentActivity(this);
        super.onResume();
    }
    //显示进度条
    public static void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(BaseActivity.getCurrentActivity());
            progressDialog.setMessage("请等待...");
            progressDialog.setCanceledOnTouchOutside(false);
            //progressDialog.setCancelable(false);// 设置ProgressDialog 是否可以按退回按键取消
        }
        progressDialog.show();
    }
    //显示进度条
    public static void showProgressDialog1( String str) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(BaseActivity.getCurrentActivity());
            progressDialog.setMessage(str);
            progressDialog.setCanceledOnTouchOutside(false);
            // 设置ProgressDialog 是否可以按退回按键取消
            //            progressDialog.setCancelable(false);
        } else {
            closeProgressDialog();
            progressDialog = new ProgressDialog(BaseActivity.getCurrentActivity());
            progressDialog.setMessage(str);
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }
    //显示百分比
    public static void showProgressDialog2( int size, String str) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(BaseActivity.getCurrentActivity());
            // 设置进度条风格，风格为长形
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            // 设置ProgressDialog 标题
            progressDialog.setTitle("更新进度");
            // 设置ProgressDialog 提示信息
            progressDialog.setMessage(str);
            // 设置ProgressDialog 进度条进度
            progressDialog.setMax(size);
            // 设置ProgressDialog 的进度条是否不明确
            progressDialog.setIndeterminate(false);
            // 设置ProgressDialog 是否可以按退回按键取消
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }
    /**
     * 关闭进度条
     */
    public static void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        progressDialog = null;
    }

    public static Activity getCurrentActivity() {
        return mCurrentActivity;
    }

    private void setCurrentActivity(Activity activity) {
        mCurrentActivity = activity;
    }
    //为了不受到系统字体影响
   /* @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }*/
}
