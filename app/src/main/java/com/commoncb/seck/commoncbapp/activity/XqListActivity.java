package com.commoncb.seck.commoncbapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.commoncb.seck.commoncbapp.DB.DbHelper;
import com.commoncb.seck.commoncbapp.R;
import com.commoncb.seck.commoncbapp.modle1.New_UserMsg;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by ssHss on 2018/1/25.
 */

public class XqListActivity extends BaseActivity {
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

    public static int FLAG = 0;
    public List<New_UserMsg> mList = new ArrayList<>();//线号列表
    private ListAdapter adapter;
    private long mPressedTime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modlelist);
        ButterKnife.bind(this);


        DbHelper.getdb();//获取数据库实例
        btn_1.setVisibility(View.GONE);
        btn_2.setVisibility(View.GONE);
        btn_3.setVisibility(View.GONE);
        btn_4.setVisibility(View.GONE);

//        Bundle bundle = getIntent().getExtras();
//        FLAG = bundle.getInt("flag");
        adapter = new ArrayAdapter(this, R.layout.simple_list_item_1, getXq()) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                return super.getView(position, convertView, parent);
            }
        };
        ListActivity_lv_xq.setAdapter(adapter);
        ListActivity_lv_xq.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                if (System.currentTimeMillis() - mPressedTime > 2000) {
                    Intent intent = new Intent(XqListActivity.this, NewCbActivity.class);
                    intent.putExtra("XH", mList.get(position).getXH());
                    mPressedTime = System.currentTimeMillis();
                    startActivity(intent);
                }
            }
        });
        ListActivity_lv_xq.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                new SweetAlertDialog(XqListActivity.this, SweetAlertDialog.WARNING_TYPE)
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
                                adapter = new ArrayAdapter(XqListActivity.this, android.R.layout.simple_list_item_1, getXq());
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
            new SweetAlertDialog(XqListActivity.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("错误!")
                    .setContentText("未找到数据,请检查数据库文件是否存在！")
                    .show();
        }
        return data;
    }

}
