package com.commoncb.seck.commoncbapp.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
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
 * Created by ssHss on 2018/8/2.
 */

public class ImageViewHisActivity extends BaseActivity {
    String PicID;
    @BindView(R.id.iv_pic)
    ImageView iv_pic;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        ButterKnife.bind(this);
        Bundle bundle = getIntent().getExtras();
        PicID = bundle.getString("PicID");

        getImage(PicID);
    }

    private void getImage( final String picID) {
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
                    iv_pic.setImageBitmap(bitmap);
                    break;
                case 0x98:
                    Toast.makeText(ImageViewHisActivity.this, "服务器查询错误。", Toast.LENGTH_LONG).show();
                    break;
                case 0x99:
                    Toast.makeText(ImageViewHisActivity.this, "服务器连接失败，请确认网络连接及重试。", Toast.LENGTH_LONG).show();
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
