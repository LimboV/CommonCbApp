package com.commoncb.seck.commoncbapp.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.commoncb.seck.commoncbapp.R;

import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.commoncb.seck.commoncbapp.activity.MainActivity.url;

/**
 * Created by ssHss on 2018/7/17.
 */

public class HisPicActivity extends BaseActivity {
    @BindView(R.id.iv1)
    ImageView iv1;
    @BindView(R.id.iv2)
    ImageView iv2;
    @BindView(R.id.iv3)
    ImageView iv3;

    String PicID1, PicID2, PicID3;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hispic);
        ButterKnife.bind(this);
        Bundle bundle = getIntent().getExtras();
        PicID1 = bundle.getString("PicID1");
        PicID2 = bundle.getString("PicID2");
        PicID3 = bundle.getString("PicID3");
//        PicID1 = "1";
//        PicID2 = "2";
//        PicID3 = "3";
        if (PicID1.length() > 0) {
            getImage(0, PicID1);
        }
        if (PicID2.length() > 0) {
            getImage(1, PicID2);
        }
        if (PicID3.length() > 0) {
            getImage(2, PicID3);
        }
        iv1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent i = new Intent(HisPicActivity.this,ImageViewHisActivity.class);
                i.putExtra("PicID", PicID1);
                startActivity(i);
                return false;
            }
        });
        iv2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent i = new Intent(HisPicActivity.this,ImageViewHisActivity.class);
                i.putExtra("PicID", PicID2);
                startActivity(i);
                return false;
            }
        });
        iv3.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent i = new Intent(HisPicActivity.this,ImageViewHisActivity.class);
                i.putExtra("PicID", PicID3);
                startActivity(i);
                return false;
            }
        });


    }

    private void getImage(final int ivID, final String picID) {
        /**
         * 根据图片ID获取相关图片
         */
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象
                    Request request = null;
                    request = new Request.Builder()
                            .url(url + "/getPicture?PicID=" + picID)//请求接口。如果需要传参拼接到接口后面。
                            .build();//创建Request 对象
                    Response response = null;
                    response = client.newCall(request).execute();//得到Response 对象
                    if (response.isSuccessful()) {
                        //Log.d("limboV", "res==" + response.body().string());
                        //此时的代码执行在子线程，修改UI的操作请使用handler跳转到UI线程。
                        InputStream inputStream = response.body().byteStream();//得到图片的流
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        Message message = new Message();
                        message.what = 0x00;
                        message.arg1 = ivID;
                        message.obj = bitmap;
                        handler.sendMessage(message);
                    } else {
                        Message message = new Message();
                        message.what = 0x98;
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
                    Bitmap bitmap = (Bitmap) msg.obj;
                    int ivID = msg.arg1;
                    switch (ivID) {
                        case 0:
                            iv1.setImageBitmap(bitmap);
                            break;
                        case 1:
                            iv2.setImageBitmap(bitmap);
                            break;
                        case 2:
                            iv3.setImageBitmap(bitmap);
                            break;
                        default:
                            break;
                    }
                    break;
                case 0x98:
                    Toast.makeText(HisPicActivity.this, "服务器查询错误。", Toast.LENGTH_LONG).show();
                    break;
                case 0x99:
                    Toast.makeText(HisPicActivity.this, "服务器连接失败，请确认网络连接及重试。", Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        }
    };
}
