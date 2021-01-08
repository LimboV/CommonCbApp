package com.commoncb.seck.commoncbapp.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.commoncb.seck.commoncbapp.DB.DbHelper;
import com.commoncb.seck.commoncbapp.R;
import com.commoncb.seck.commoncbapp.modle1.New_UserMsg;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by ssHss on 2018/6/26.
 */

public class TJActivity extends BaseActivity {
    @BindView(R.id.ListActivity_lv_xq)
    ListView ListActivity_lv_xq;
    @BindView(R.id.btn_1)
    Button btn_1;
    @BindView(R.id.btn_2)
    Button btn_2;
    @BindView(R.id.btn_3)
    Button btn_3;
    @BindView(R.id.btn_4)
    Button btn_4;
    boolean tj = false;

    private List<New_UserMsg> mList = new ArrayList<>();//线号列表
    private List<String> tjList = new ArrayList<>();//线号列表
    private ListAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modlelist);
        ButterKnife.bind(this);
        tj = true;
        int flag = copyFilesFassets(TJActivity.this, "Seck_4Db.db", Environment.getExternalStorageDirectory().getAbsolutePath() + "/SeckLoRaDB");
        switch (flag) {
            case 0://完成
                Toast.makeText(TJActivity.this, "DB文件创建完成", Toast.LENGTH_LONG).show();
                break;
            case 1://不存在
                Toast.makeText(TJActivity.this, "SD卡不存在", Toast.LENGTH_LONG).show();
                break;
            case 2://报错
                Toast.makeText(TJActivity.this, "DB文件创建失败", Toast.LENGTH_LONG).show();
                break;
            case 3://已存在
                //                Toast.makeText(XqListActivity.this, "DB文件已存在", Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }
        btn_1.setVisibility(View.GONE);
        btn_2.setVisibility(View.GONE);
        btn_3.setText("刷新");
        btn_4.setVisibility(View.GONE);
        btn_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter = new ArrayAdapter(TJActivity.this, android.R.layout.simple_list_item_1, getXq()) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        return super.getView(position, convertView, parent);
                    }
                };
                ListActivity_lv_xq.setAdapter(adapter);
            }
        });
        /**
         * 获取对应线号所有表列表
         */
        DbHelper.getdb();//获取数据库实例

        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, getXq()) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                return super.getView(position, convertView, parent);
            }
        };
        ListActivity_lv_xq.setAdapter(adapter);
        ListActivity_lv_xq.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //                Intent intent = new Intent(XqListActivity.this, FunctionActivity.class);
                Intent intent = new Intent(TJActivity.this, NewCbActivity.class);
                intent.putExtra("XH", mList.get(position).getXH());
                startActivity(intent);
            }
        });
        ListActivity_lv_xq.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                new SweetAlertDialog(TJActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("提示")
                        .setContentText("请确认是否要删除该线号?")
                        .setCancelText("取消")
                        .setConfirmText("确认")
                        .showCancelButton(true)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                Log.d("limboV", "sure");
                                sweetAlertDialog.cancel();
                                DbHelper.deleteXHAndMeter(mList.get(position).getXH());
                                getXq();
                                adapter = new ArrayAdapter(TJActivity.this, android.R.layout.simple_list_item_1, getXq());
                                ListActivity_lv_xq.setAdapter(adapter);
                            }
                        })
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                Log.d("limboV", "cancel");
                                /*new SweetAlertDialog(XqListActivity.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("取消!")
                                        .setContentText("你已经取消!")
                                        .show();*/
                                sDialog.cancel();
                            }
                        }).show();
                return true;
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (tj) {
                        Thread.sleep(10000);
                        Message message = new Message();
                        message.what = 0x00;
                        handler.sendMessage(message);
                    }

                } catch (Exception e) {
                    Log.e(BaseActivity.TAG, e.toString());
                }
            }
        }).start();
    }


    private List<String> getXq() {
        try {
         tjList = DbHelper.tjAllXH(MainActivity.user);//从数据库提取出
            mList = DbHelper.getAllXH(MainActivity.user);//从数据库提取出

        } catch (Exception e) {
            Log.e("limbo", e.toString());
            new SweetAlertDialog(TJActivity.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("错误!")
                    .setContentText("未找到数据,请检查数据库文件是否存在！")
                    .show();
        }
        return tjList;
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
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            BaseActivity.closeProgressDialog();
            switch (msg.what) {
                case 0x00:
                    Log.d(BaseActivity.TAG, "刷新统计列表");
                    btn_3.performClick();
                    break;
                default:
                    break;
            }
        }
    };
    @Override
    protected void onDestroy() {
        tj = false;
        super.onDestroy();
    }
}
