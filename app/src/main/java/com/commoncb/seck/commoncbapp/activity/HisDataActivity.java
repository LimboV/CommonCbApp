package com.commoncb.seck.commoncbapp.activity;

import android.content.Intent;
import android.os.Bundle;
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
import android.widget.TextView;
import android.widget.Toast;

import com.commoncb.seck.commoncbapp.R;
import com.commoncb.seck.commoncbapp.modle1.HisDataList;
import com.commoncb.seck.commoncbapp.modle1.HisMeterDataAndPicID;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.commoncb.seck.commoncbapp.activity.MainActivity.url;

/**
 * Created by ssHss on 2018/7/16.
 */

public class HisDataActivity extends BaseActivity {

    @BindView(R.id.lv_hisData)
    ListView lv_hisData;
    @BindView(R.id.btn_reGet)
    Button btn_reGet;
    @BindView(R.id.tv_Msg)
    TextView tv_Msg;
    String KH;
    private ListAdapter ad;
    List<String> stringList = new ArrayList<>();
    HisDataList hisDataList = new HisDataList();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_his);
        ButterKnife.bind(this);
        Bundle bundle = getIntent().getExtras();
        KH = bundle.getString("KH");
//        KH = "004469";//测试
        getHis(KH);
        btn_reGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getHis(KH);

            }
        });
        lv_hisData.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                if (hisDataList.getDatas().get(position).getPicID1().length() > 0 ||
//                        hisDataList.getDatas().get(position).getPicID2().length() > 0 ||
//                        hisDataList.getDatas().get(position).getPicID3().length() > 0) {
                    Intent i = new Intent(HisDataActivity.this, HisPicActivity.class);
                i.putExtra("PicID1",hisDataList.getDatas().get(position).getPicID1());
                i.putExtra("PicID2",hisDataList.getDatas().get(position).getPicID2());
                i.putExtra("PicID3",hisDataList.getDatas().get(position).getPicID3());
                    startActivity(i);
//                }else {
//                    Toast.makeText(HisDataActivity.this, "该月无图片记录", Toast.LENGTH_LONG).show();
//                    tv_Msg.setText("该月无图片记录");
//                }

            }
        });
    }

    /**
     * 登陆验证
     */
    private void getHis(final String KH) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象
                    Request request = new Request.Builder()
                            .url(url + "/GetDatas?KH=" + KH)//请求接口。如果需要传参拼接到接口后面。
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
                        HisDataList hisDataList = gson.fromJson(responseX, HisDataList.class);
                        Message message = new Message();
                        message.what = 0x00;
                        message.obj = hisDataList;
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
                    hisDataList = (HisDataList) msg.obj;
                    if (hisDataList.getFlag().equals("success") && hisDataList != null) {
                        if (hisDataList.getDatas().size() == 0) {
                            tv_Msg.setText("成功连接服务器，但是未查询到该表最近6个月数据及图片。");
                        } else {
                            tv_Msg.setText("成功连接服务器，请点击对应月份数据查看图片。");
                            stringList.clear();
                            for (int i = 0; i < hisDataList.getDatas().size(); i++) {
                                HisMeterDataAndPicID hisMeterDataAndPicID = hisDataList.getDatas().get(i);
                                stringList.add("------------------------------------------" +
                                        "\n数据时间:" + hisMeterDataAndPicID.getDateTime() +
                                        "\n抄见:" + hisMeterDataAndPicID.getData() +
                                        "\n用水:" + hisMeterDataAndPicID.getWaterVolume() +
                                        "\n备注:" + hisMeterDataAndPicID.getNote() +
                                        "\n------------------------------------------"
                                );
                            }
                            ad = new ArrayAdapter(HisDataActivity.this, R.layout.list_textview, stringList) {
                                @Override
                                public View getView(int position, View convertView, ViewGroup parent) {
                                    return super.getView(position, convertView, parent);
                                }
                            };
                            lv_hisData.setAdapter(ad);

                        }


                    } else {
                        tv_Msg.setText("服务器查找数据失败");
                        Toast.makeText(HisDataActivity.this, "服务器查找数据失败", Toast.LENGTH_LONG).show();
                        Log.d("limbo", "fail");
                    }
                    break;
                case 0x99:
                    Toast.makeText(HisDataActivity.this, "服务器连接失败，请确认网络连接及重试。", Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
