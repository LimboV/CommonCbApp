package com.commoncb.seck.commoncbapp.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.commoncb.seck.commoncbapp.R;
import com.commoncb.seck.commoncbapp.modle1.PwModle;
import com.commoncb.seck.commoncbapp.utils.HzyUtils;
import com.google.gson.Gson;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ChangePwActivity extends BaseActivity {
    @BindView(R.id.et_oldPw)
    EditText oldPw;
    @BindView(R.id.et_newPw1)
    EditText newPw1;
    @BindView(R.id.et_newPw2)
    EditText newPw2;
    @BindView(R.id.btn_sure)
    Button btn_sure;
    @BindView(R.id.btn_cancel)
    Button btn_cancel;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepw);
        ButterKnife.bind(this);


        btn_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String opw = oldPw.getText().toString().trim();
                String npw1 = newPw1.getText().toString().trim();
                String npw2 = newPw2.getText().toString().trim();
                if (!opw.equals(MainActivity.password)) {
                    Toast.makeText(ChangePwActivity.this, "旧密码输入错误", Toast.LENGTH_LONG).show();
                    return;
                }
                if (!npw1.equals(npw2)) {
                    Toast.makeText(ChangePwActivity.this, "两次输入新密码不一致", Toast.LENGTH_LONG).show();
                    return;
                }
                changePw(npw1,opw);

            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 密码修改
     */
    private void changePw(final String npw1, final String opw) {
        HzyUtils.showProgressDialog(ChangePwActivity.this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象
                    Request request = new Request.Builder()
                            .url(MainActivity.url+"/ChangePwd?" +
                                    "UserID=" + MainActivity.user
                                    + "&OldPassword=" + opw
                                    + "&NewPassword=" + npw1)//请求接口。如果需要传参拼接到接口后面。
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
                        PwModle muser = gson.fromJson(responseX, PwModle.class);
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

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x00:
                    PwModle PwModle = (com.commoncb.seck.commoncbapp.modle1.PwModle) msg.obj;
                    Log.d("limbo", PwModle.getFlag());
                    HzyUtils.closeProgressDialog();
                    if (PwModle.getFlag().equals("success")) {
                        new SweetAlertDialog(ChangePwActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("成功")
                                .setContentText("成功修改密码，请重新登录。")
                                .show();
                    }else {
                        new SweetAlertDialog(ChangePwActivity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("失败")
                                .setContentText("修改密码失败!")
                                .show();
                    }
                    break;
                case 0x99:
                    HzyUtils.closeProgressDialog();
                    new SweetAlertDialog(ChangePwActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("错误")
                            .setContentText("网络异常!")
                            .show();
                    break;
                default:
                    break;
            }
        }
    };
}
