package com.commoncb.seck.commoncbapp.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.commoncb.seck.commoncbapp.BuildConfig;
import com.commoncb.seck.commoncbapp.DB.DbHelper;
import com.commoncb.seck.commoncbapp.R;
import com.commoncb.seck.commoncbapp.modle1.New_Meter;
import com.commoncb.seck.commoncbapp.utils.HzyUtils;
import com.commoncb.seck.commoncbapp.utils.ImageUtil;
import com.commoncb.seck.commoncbapp.utils.ViewClickVibrate;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
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

/**
 * Created by limbo on 2018/6/17.
 */

public class NewCbActivity extends BaseActivity implements android.view.GestureDetector.OnGestureListener {
    @BindView(R.id.tv_hm)
    TextView tv_hm;
    @BindView(R.id.tv_meterNumber)
    TextView tv_meterNumber;
    @BindView(R.id.tv_meterState)
    TextView tv_meterState;
    @BindView(R.id.tv_phone1)
    TextView tv_phone1;
    @BindView(R.id.tv_phone2)
    TextView tv_phone2;
    @BindView(R.id.tv_userState)
    TextView tv_userState;
    @BindView(R.id.tv_number)
    TextView tv_number;
    @BindView(R.id.tv_userNumber)
    TextView tv_userNumber;
    @BindView(R.id.tv_caliber)
    TextView tv_caliber;
    @BindView(R.id.tv_XLH)
    TextView tv_XLH;
    @BindView(R.id.tv_IrDAPort)
    TextView tv_IrDAPort;
    @BindView(R.id.tv_addr)
    TextView tv_addr;
    @BindView(R.id.tv_Msg)
    TextView tv_Msg;
    @BindView(R.id.et_IrDAData)
    EditText et_IrDAData;
    @BindView(R.id.et_LastUserWater)
    EditText et_LastUserWater;
    @BindView(R.id.et_LastData)
    EditText et_LastData;
    @BindView(R.id.sp_meterState)
    Spinner sp_meterState;
    @BindView(R.id.sp_cbState)
    Spinner sp_cbState;
    @BindView(R.id.sp_tj)
    Spinner sp_tj;
    @BindView(R.id.et_thisMouthData)
    EditText et_thisMouthData;
    @BindView(R.id.et_thisMouthUserWater)
    EditText et_thisMouthUserWater;
    @BindView(R.id.et_note)
    EditText et_note;
    @BindView(R.id.btn_save)
    Button btn_save;
    @BindView(R.id.btn_next)
    Button btn_next;
    @BindView(R.id.btn_last)
    Button btn_last;
    @BindView(R.id.btn_IrDACB)
    Button btn_IrDACB;
    @BindView(R.id.btn_location)
    Button btn_location;
    @BindView(R.id.btn_fx)
    Button btn_fx;
    /**
     * 键盘
     */
    @BindView(R.id.btn_showInput)
    Button btn_showInput;
    @BindView(R.id.btn_0)
    Button btn_0;
    @BindView(R.id.btn_1)
    Button btn_1;
    @BindView(R.id.btn_2)
    Button btn_2;
    @BindView(R.id.btn_3)
    Button btn_3;
    @BindView(R.id.btn_4)
    Button btn_4;
    @BindView(R.id.btn_5)
    Button btn_5;
    @BindView(R.id.btn_6)
    Button btn_6;
    @BindView(R.id.btn_7)
    Button btn_7;
    @BindView(R.id.btn_8)
    Button btn_8;
    @BindView(R.id.btn_9)
    Button btn_9;
    @BindView(R.id.btn_fh)
    Button btn_fh;
    @BindView(R.id.btn_qxfh)
    Button btn_qxfh;
    @BindView(R.id.btn_wbj)
    Button btn_wbj;
    @BindView(R.id.btn_ybj)
    Button btn_ybj;
    @BindView(R.id.btn_delete)
    Button btn_delete;
    @BindView(R.id.btn_search1)
    Button btn_search1;
    @BindView(R.id.btn_his)
    Button btn_his;
    @BindView(R.id.btn_voicemodel)
    Button btn_voiceModel;
    @BindView(R.id.btn_pic1)
    Button btn_pic1;
    @BindView(R.id.btn_pic2)
    Button btn_pic2;
    @BindView(R.id.btn_pic3)
    Button btn_pic3;

    @BindView(R.id.iv_pic)
    ImageView iv_pic;
    @BindView(R.id.iv_pic2)
    ImageView iv_pic2;
    @BindView(R.id.iv_pic3)
    ImageView iv_pic3;
    @BindView(R.id.btn_pic)
    Button btn_pic;
    @BindView(R.id.btn_video)
    Button btn_video;

    boolean showFlag = false;
    boolean meterState = true;//用来判断是否第一次加载表信息
    public static Bitmap bitmap;//保存拍照图片
    private int spinnerX_MeterState = 0;
    private int spinnerX_CbState = 0;
    private final String[] string_cbState = {"正常", "估抄", "异常"};
    private final String[] string_meterState1 = {"正常", "偏大", "偏小", "淹", "埋", "堆或压", "水雾", "户内", "红外摄像故障", "连续相同用水"};//正常
    private final String[] string_meterState2 = {"周期换表", "表盘黑或失灵", "淹", "埋", "堆或压", "水雾", "户内", "表丢失", "倒装", "回流", "其他"};//估抄
    private final String[] string_meterState3 = {"欠费停仍有水", "报停仍有水", "用户擅自移位", "无铅封", "用户擅自拆表"};//异常
    private final String[] string_userState = {"正常", "欠费停", "违章停", "报停", "销户", "校验"};
    private final String[] string_tj = {"所有表", "已抄表", "未抄表", "可疑表", "已复核表", "偏大表", "偏小表", "未拍照表", "负用水表", "未上传表", "已标记表", "已备注表"};
    public static ArrayList<New_Meter> mNew_meters = new ArrayList<>();
    public static int LOCATION = 0;//用以当前表列表位置
    public static int LAET_LOCATION = 0;//用以当前表列表位置
    int LOCATION_Max = 0;//用以记录列表最大数
    String XH;
    private int POSITION;
    public static int X_FLAG = 0;//拍照标志位
    public static int PIC_FLAG = 0;//返回的照片更新标志位
    boolean AJFlag = true;//按键是否显示 false - 不显示
    int xqNum = 0;//保存选择的小区序号
    //定义手势检测器实例
    GestureDetector detector;
    boolean xflag = false;//不允许多次滑动下一条
    boolean yflag = true;//标记输入本月抄见还是本月实用

    public final static int ALBUM_REQUEST_CODE = 1;
    public final static int CROP_REQUEST = 2;
    public final static int CAMERA_REQUEST_CODE = 3;
    boolean changePageFlag = false;//翻页标志位
    private String PHOTO_FOLDER = new File(Environment.getExternalStorageDirectory(), "").getPath() + "/SeckLoRaDB/";
    private String PHOTO_NAME = "seckPic";
    private static boolean VOICE_FLAG = false;
    // 语音识别对象
    private com.iflytek.cloud.SpeechRecognizer mAsr;
    //识别出来的句子
    StringBuilder sentence = null;
    //对话框
    private SweetAlertDialog msweetAlertDialog;
    //对话框是否存在
    private boolean isDialogExist = false;
    boolean tipFlag = true;//是否弹出提示
    //听写监听器
    private RecognizerListener recognizerListener = new RecognizerListener() {
        @Override
        public void onVolumeChanged(int i, byte[] bytes) {
            //            Toast.makeText(BaseActivity.getCurrentActivity(), "当前正在说话，音量大小：" + i, Toast.LENGTH_SHORT).show();
            //            Log.d(BaseActivity.TAG, "返回音频数据：" + bytes.length);
        }

        /**
         * 开始录音
         */
        @Override
        public void onBeginOfSpeech() {
        }

        /**
         * 结束录音
         */
        @Override
        public void onEndOfSpeech() {
            //结束录音后，根据识别出来的句子，通过语音合成进行反馈
            //            startRecord.setImageResource(R.mipmap.voice_full);
            //            analysisSentences(false);
            if (VOICE_FLAG)
                mAsr.startListening(recognizerListener);
            else
                mAsr.stopListening();
        }

        /**
         * 听写结果回调接口 , 返回Json格式结果
         * @param recognizerResult  json结果对象
         * @param b                 等于true时会话结束
         */
        @Override
        public void onResult(RecognizerResult recognizerResult, boolean b) {
            String result = recognizerResult.getResultString();
            try {
                //处理json结果
                JSONObject jsonObject = new JSONObject(result);
                JSONArray words = jsonObject.getJSONArray("ws");
                //拼成句子
                sentence = new StringBuilder("");
                for (int i = 0; i < words.length(); i++) {
                    JSONObject word = words.getJSONObject(i);
                    JSONArray subArray = word.getJSONArray("cw");
                    JSONObject subWord = subArray.getJSONObject(0);
                    String character = subWord.getString("w");
                    sentence.append(character);
                }
                try {
                    analysisSentences();
                } catch (Exception e) {
                    Log.d("limbo", e.toString());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 会话发生错误回调接口
         * @param speechError
         */
        @Override
        public void onError(SpeechError speechError) {
            Log.e("limbo", speechError.getErrorDescription());
        }

        /**
         * 扩展用接口
         */
        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }
    };

    //初始化监听器。
    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            Log.d("TAG", "SpeechRecognizer init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                Toast.makeText(NewCbActivity.this, "初始化失败，错误码：" + code, Toast.LENGTH_SHORT).show();
            }
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newcb);
        ButterKnife.bind(this);
        //根据APPID创建语音配置对象
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=5bc59c36");
        Bundle bundle = getIntent().getExtras();
        XH = bundle.getString("XH");
        LOCATION = 0;
        LOCATION_Max = 0;
        //new一个手势检测器
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
            detector = new GestureDetector(NewCbActivity.this, this);
        }
        //初始化语音对象
        mAsr = SpeechRecognizer.createRecognizer(this, mInitListener);
        //设置听写参数
        mAsr.setParameter(SpeechConstant.DOMAIN, "iat");
        //设置为中文
        mAsr.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        //设置为普通话
        mAsr.setParameter(SpeechConstant.ACCENT, "mandarin ");
        /**
         * 获取对应线号所有表列表
         */

        DbHelper.getdb();//获取数据库实例
        uiset();
    }

    private void save(boolean flag) {
        LAET_LOCATION = LOCATION;
        String data = et_thisMouthData.getText().toString();//本月读数
        String lastData = et_LastData.getText().toString();//上月读数
        String LastUserWater = et_LastUserWater.getText().toString();//上月用水
        String thisUseWater = et_thisMouthUserWater.getText().toString();
        //        Log.d(BaseActivity.TAG,"本月用水"+et_thisMouthUserWater.getText().toString() + "上月用水"+et_LastUserWater.getText().toString());
        boolean err;
        if (!HzyUtils.isEmpty(thisUseWater) && !HzyUtils.isEmpty(data)) {
            err = true;
        } else {
            err = false;
        }

        if (err) {
            if (HzyUtils.isEmpty(lastData)) {
                lastData = "0";
            }
            if (HzyUtils.isEmpty(LastUserWater)) {
                LastUserWater = "0";
            }
            float thisMouthData;
            float lastMouthData;
            if (isCV()) {
                try {
                    if (yflag) {
                        thisMouthData = Integer.parseInt(data) - Integer.parseInt(lastData);
                    } else {
                        thisMouthData = Integer.parseInt(et_thisMouthUserWater.getText().toString());
                    }
                    lastMouthData = Integer.parseInt(LastUserWater);
                } catch (Exception e) {
                    Toast.makeText(NewCbActivity.this, "数值异常", Toast.LENGTH_LONG).show();
                    return;
                }
                if (lastMouthData == 0) {
                    if (thisMouthData > 50) {
                        spinnerX_MeterState = 1;
                        sp_meterState.setSelection(spinnerX_MeterState);
                    } else if (thisMouthData < 0) {
                        spinnerX_MeterState = 2;
                        sp_meterState.setSelection(spinnerX_MeterState);
                    } else {
                        spinnerX_MeterState = 0;
                        sp_meterState.setSelection(spinnerX_MeterState);
                    }
                } else {
                    if (thisMouthData <= 0) {
                        spinnerX_MeterState = 2;
                        sp_meterState.setSelection(spinnerX_MeterState);
                    } else if (thisMouthData > 0) {
                        if (lastMouthData <= 5 && lastMouthData > 0) {
                            if ((thisMouthData / lastMouthData) <= 4) {
                                spinnerX_MeterState = 0;
                            } else if ((thisMouthData / lastMouthData) == 0) {
                                spinnerX_MeterState = 2;
                            } else {
                                spinnerX_MeterState = 1;
                            }
                        } else if (lastMouthData <= 10 && lastMouthData > 5) {
                            if ((thisMouthData / lastMouthData) <= 3 && (thisMouthData / lastMouthData) > 0.4) {
                                spinnerX_MeterState = 0;
                            } else if ((thisMouthData / lastMouthData) < 0.4) {
                                spinnerX_MeterState = 2;
                            } else if ((thisMouthData / lastMouthData) > 3) {
                                spinnerX_MeterState = 1;
                            }
                        } else if (lastMouthData <= 15 && lastMouthData > 10) {
                            if ((thisMouthData / lastMouthData) <= 2 && (thisMouthData / lastMouthData) > 0.4) {
                                spinnerX_MeterState = 0;
                            } else if ((thisMouthData / lastMouthData) < 0.4) {
                                spinnerX_MeterState = 2;
                            } else if ((thisMouthData / lastMouthData) > 2) {
                                spinnerX_MeterState = 1;
                            }
                        } else if (lastMouthData <= 40 && lastMouthData > 15) {
                            if ((thisMouthData / lastMouthData) <= 1.6 && (thisMouthData / lastMouthData) > 0.4) {
                                spinnerX_MeterState = 0;
                            } else if ((thisMouthData / lastMouthData) < 0.4) {
                                spinnerX_MeterState = 2;
                            } else if ((thisMouthData / lastMouthData) > 1.6) {
                                spinnerX_MeterState = 1;
                            }
                        } else if (lastMouthData > 40) {
                            if ((thisMouthData / lastMouthData) <= 1.3 && (thisMouthData / lastMouthData) > 0.7) {
                                spinnerX_MeterState = 0;
                            } else if ((thisMouthData / lastMouthData) < 0.7) {
                                spinnerX_MeterState = 2;
                            } else if ((thisMouthData / lastMouthData) > 1.3) {
                                spinnerX_MeterState = 1;
                            }
                        }
                        sp_meterState.setSelection(spinnerX_MeterState);
                    }
                }
                et_thisMouthUserWater.setText((int) thisMouthData + "");
            } else {
                thisMouthData = Integer.parseInt(et_thisMouthUserWater.getText().toString());
            }
            Calendar c = Calendar.getInstance();
            Date d = c.getTime();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            mNew_meters.get(LOCATION).setUploadTime(df.format(d));//数据修改时间
            mNew_meters.get(LOCATION).setReadData(Integer.parseInt(data) + "");//本月读数
            mNew_meters.get(LOCATION).setMeterState(spinnerX_MeterState + "");//水表状态
            mNew_meters.get(LOCATION).setCbState(spinnerX_CbState + "");//抄表状态
            mNew_meters.get(LOCATION).setData((int) thisMouthData + "");//本月实用
            mNew_meters.get(LOCATION).setNote(et_note.getText().toString());//现场备注

            DbHelper.newChangeUser(mNew_meters.get(LOCATION), flag);
            showMeter(LOCATION);
        } else {
            //            clearMeterData(LOCATION);
        }
    }

    void tip() {
        new SweetAlertDialog(NewCbActivity.this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("警告")
                .setContentText("关键数据为空!")
                .setConfirmText("确认")
                .showCancelButton(true)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        Log.d("limboV", "sure");
                        sweetAlertDialog.cancel();
                    }
                }).show();

    }

    void clearMeterData(int location) {
        if (isCV()) {
            spinnerX_MeterState = 0;
            sp_meterState.setSelection(spinnerX_MeterState);
            mNew_meters.get(location).setMeterState(spinnerX_MeterState + "");

            spinnerX_CbState = 0;
            sp_cbState.setSelection(spinnerX_CbState);
            mNew_meters.get(location).setCbState(spinnerX_CbState + "");

        }
        mNew_meters.get(location).setData("");
        mNew_meters.get(location).setReadData("");
        mNew_meters.get(location).setCbFlag("未抄");
        mNew_meters.get(location).setNote(et_note.getText().toString().trim());
        DbHelper.clearMeterData(mNew_meters.get(location).getKH(), mNew_meters.get(location).getNote());

    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x00:
                    int arg1 = msg.arg1;
                    if (mNew_meters == null || mNew_meters.size() == 0) {
                        Toast.makeText(NewCbActivity.this, "未找到对应条件表数据", Toast.LENGTH_LONG).show();
                        sp_tj.setSelection(0);
                    } else {
                        LOCATION_Max = mNew_meters.size();
                        Log.d("limbo", "LOCATION_Max:" + LOCATION_Max);
                        if (LOCATION_Max == 0) {
                            Log.d("limbo", LOCATION_Max + "");
                            Toast.makeText(NewCbActivity.this, "该线号下无表数据", Toast.LENGTH_LONG).show();
                            finish();
                        }
                        if (arg1 == 0) {
                            loadMsg();
                        } else {
                            showMeter(LOCATION);
                            if (arg1 == 4 && mNew_meters.size() > 0 && AJFlag) {
                                btn_showInput.performClick();
                            }
                        }
                    }
                    HzyUtils.closeProgressDialog();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 保存图片
     */
    private void savePic(String time, int x_FLAG) {
        if (iv_pic.getDrawable() != null || iv_pic2.getDrawable() != null || iv_pic3.getDrawable() != null) {
            if (bitmap != null) {
                mNew_meters.get(LOCATION).setUploadTime(time);//数据修改时间
                mNew_meters.get(LOCATION).setPic1(bitmap);//照片
                mNew_meters.get(LOCATION).setNote(et_note.getText().toString());//现场备注
                mNew_meters.get(LOCATION).setLon(MenuActivity.lon);
                mNew_meters.get(LOCATION).setLat(MenuActivity.lat);
                mNew_meters.get(LOCATION).setCbState(mNew_meters.get(LOCATION).getCbState());//抄表状态
                mNew_meters.get(LOCATION).setMeterState(mNew_meters.get(LOCATION).getMeterState());//抄表状态

                DbHelper.newChangePic(mNew_meters.get(LOCATION), x_FLAG);
                bitmap = DbHelper.getPicByKH1(mNew_meters.get(LOCATION).getKH(), x_FLAG);
                switch (x_FLAG) {
                    case 0:
                        iv_pic.setImageBitmap(bitmap);//照片
                        break;
                    case 1:
                        iv_pic2.setImageBitmap(bitmap);//照片
                        break;
                    case 2:
                        iv_pic3.setImageBitmap(bitmap);//照片
                        break;
                    default:
                        break;
                }

                btn_save.performClick();
                Log.d("limbo", "lon:" + MenuActivity.lon + "    lat:" + MenuActivity.lat);

            } else {
            }
        }
    }

    /**
     * 处理拍照返回
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                if (resultCode == 2){
                    Bundle bundle = data.getExtras();
                    String Result = bundle.getString("result");
                    sp_tj.setSelection(0);
                    NewCbActivity.mNew_meters = DbHelper.getMeterByXH(XH);
                    for (int i = 0; i < mNew_meters.size(); i++) {
                        if (mNew_meters.get(i).getKH().equals(Result)) {
                            LOCATION = i;
                            showMeter(LOCATION);
                        }
                    }
                }else if (resultCode == 3){
                    sp_tj.setSelection(0);
                    NewCbActivity.mNew_meters = DbHelper.getMeterByXH(XH);
                    showMeter(LOCATION);
                }
                break;
            case 1:
                /**
                 * 获取的是缩略图，保存在SD卡空间中，保存名为SeckApp1
                 */
                if (X_FLAG ==0 && resultCode == RESULT_OK){
                    try {
                        //1.加载位图
                        InputStream is = new FileInputStream(PHOTO_FOLDER + PHOTO_NAME);
                        //2.为位图设置100K的缓存
                        BitmapFactory.Options opts = new BitmapFactory.Options();
                        //3.设置位图颜色显示优化方式
                        opts.inTempStorage = new byte[100 * 1024];
                        //ALPHA_8：每个像素占用1byte内存（8位）
                        //ARGB_4444:每个像素占用2byte内存（16位）
                        //ARGB_8888:每个像素占用4byte内存（32位）
                        //RGB_565:每个像素占用2byte内存（16位）
                        //Android默认的颜色模式为ARGB_8888，这个颜色模式色彩最细腻，显示质量最高。但同样的，占用的内存
                        // 也最大。也就意味着一个像素点占用4个字节的内存。我们来做一个简单的计算题：3200*2400*4 bytes
                        // =30M。如此惊人的数字！哪怕生命周期超不过10s，Android也不会答应的。
                        opts.inPreferredConfig = Bitmap.Config.RGB_565;
                        //4.设置图片可以被回收，创建Bitmap用于存储Pixel的内存空间在系统内存不足时可以被回收
                        opts.inPurgeable = true;
                        //5.设置位图缩放比例
                        //width，hight设为原来的四分一（该参数请使用2的整数倍）,这也减小了位图占用的内存大小；例如，一张
                        // 分辨率为2048*1536px的图像使用inSampleSize值为4的设置来解码，产生的Bitmap大小约为
                        // 512*384px。相较于完整图片占用12M的内存，这种方式只需0.75M内存(假设Bitmap配置为ARGB_8888)。
                        opts.inSampleSize = 4;
                        //7.解码位图
                        Bitmap btp = BitmapFactory.decodeStream(is, null, opts);
                        //8.显示位图
                        Calendar c = Calendar.getInstance();
                        Date d = c.getTime();
                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String time = df.format(d);
                        //                                                    bitmap = resizeBitmap(getBitmapForFile(PHOTO_FOLDER + PHOTO_NAME), 720);
                        bitmap = ImageUtil.drawTextToRightTop(NewCbActivity.this, btp, "户号:" + mNew_meters.get(LOCATION).getHH(), 12, Color.rgb(255, 255, 0), 0, 0);
                        bitmap = ImageUtil.drawTextToRightBottom(NewCbActivity.this, bitmap, time, 12, Color.rgb(255, 255, 0), 0, 0);
                        bitmap = ImageUtil.drawTextToLeftTop(NewCbActivity.this, bitmap, "表号:" + mNew_meters.get(LOCATION).getMeterNumber(), 12, Color.rgb(255, 255, 0), 0, 0);
                        /**
                         * 压缩图片
                         */
                            /*ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
                            int options = 100;
                            Log.d("limbo", "原图大小："+baos.toByteArray().length / 1024 + "");
                            while (baos.toByteArray().length / 1024 > 100) {  //循环判断如果压缩后图片是否大于60kb,大于继续压缩
                                baos.reset();//重置baos即清空baos
                                options -= 20;//每次都减少10
                                bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
                            }
                            Log.d("limbo", "压缩为" + options + "%:" + baos.toByteArray().length / 1024 + "");
                            ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
                            bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片*/
                        //                            iv_pic.setImageBitmap(bitmap);

                        savePic(time, PIC_FLAG);
                    } catch (Exception e) {
                        Log.e("limbo", e.toString());
                        e.printStackTrace();

                        Toast.makeText(NewCbActivity.this, "图片保存错误", Toast.LENGTH_LONG).show();
                    }
                }

                break;
            case 3:
                break;
            default:

                break;
        }
    }


    public static Bitmap getBitmapForFile(String filePath) {
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        return bitmap;
    }


    //用GestureDetector处理在该activity上发生的所有触碰事件
    public boolean onTouchEvent(MotionEvent me) {
        return detector.onTouchEvent(me);
    }

    @Override
    public boolean onDown(MotionEvent arg0) {
        return false;
    }

    /**
     * 滑屏监测
     */
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        float minMove = 120;         //定义最小滑动距离
        float minVelocity = 0;      //定义最小滑动速度
        float beginX = e1.getX();
        float endX = e2.getX();
        float beginY = e1.getY();
        float endY = e2.getY();

        if (!changePageFlag && !showFlag) {
            changePageFlag = true;//正在翻页

            if (beginX - endX > minMove && Math.abs(velocityX) > minVelocity) {   //左滑
                /**
                 * 查看下一条
                 */
                if (!xflag) {
                    changePageFlag = true;//正在翻页
                    save(false);
                    String LastUserWater = mNew_meters.get(LOCATION).getLastReadData();//上月用水
                    String thisMouthUserWater = et_thisMouthUserWater.getText().toString();
                    Log.d("limbo", "上月用水" + LastUserWater +
                            "     本月用水" + thisMouthUserWater);
                    try {
                        if ((spinnerX_MeterState != 0 || spinnerX_CbState != 0) && tipFlag) {
                            xflag = true;
                            String msg1 = "";
                            String msg2 = "";
                            switch (spinnerX_CbState) {
                                case 0:
                                    msg1 = string_cbState[spinnerX_CbState];
                                    msg2 = string_meterState1[spinnerX_MeterState];
                                    break;
                                case 1:
                                    msg1 = string_cbState[spinnerX_CbState];
                                    msg2 = string_meterState2[spinnerX_MeterState];
                                    break;
                                case 2:
                                    msg1 = string_cbState[spinnerX_CbState];
                                    msg2 = string_meterState3[spinnerX_MeterState];
                                    break;
                            }
                            String dataMsg = "";
                            if (et_thisMouthData.getText().toString().equals("") || et_thisMouthUserWater.getText().toString().equals("")) {
                                dataMsg = "缺少关键数据!";
                            }
                            new SweetAlertDialog(NewCbActivity.this, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("警告")
                                    .setContentText("表状态不正常：" + msg1 + ":" + msg2 + "  本月用水:" + mNew_meters.get(LOCATION).getData() + "    \n" + "  本月抄见:" + mNew_meters.get(LOCATION).getReadData() + "    \n" + dataMsg)
                                    .setCancelText("返回")
                                    .setConfirmText("确认,查看下一只表")
                                    .showCancelButton(true)
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            Log.d("limboV", "sure");
                                            sweetAlertDialog.cancel();
                                            if (LOCATION < LOCATION_Max - 1) {
                                                LOCATION++;
                                                showMeter(LOCATION);
                                            } else {
                                                Toast.makeText(NewCbActivity.this, "已经是最后一条", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    })
                                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            Log.d("limboV", "cancel");
                                            sDialog.cancel();
                                        }
                                    }).show();
                        } else if (!HzyUtils.isEmpty(thisMouthUserWater) &&
                                Integer.parseInt(thisMouthUserWater) > 0 &&
                                Integer.parseInt(mNew_meters.get(LOCATION).getLastData()) == 0 &&
                                Integer.parseInt(mNew_meters.get(LOCATION).getLastReadData()) == 0 && tipFlag) {
                            new SweetAlertDialog(NewCbActivity.this, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("警告")
                                    .setContentText("表上月用水为0,  本月用水:" + mNew_meters.get(LOCATION).getData() + "    \n" + "  本月抄见:" + mNew_meters.get(LOCATION).getReadData() + "    \n")
                                    .setCancelText("返回")
                                    .setConfirmText("确认,查看下一只表")
                                    .showCancelButton(true)
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            Log.d("limboV", "sure");
                                            sweetAlertDialog.cancel();
                                            if (LOCATION < LOCATION_Max - 1) {
                                                LOCATION++;
                                                showMeter(LOCATION);
                                            } else {
                                                Toast.makeText(NewCbActivity.this, "已经是最后一条", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    })
                                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            Log.d("limboV", "cancel");
                                            sDialog.cancel();
                                        }
                                    }).show();
                        } else {
                            if (LOCATION < LOCATION_Max - 1) {
                                LOCATION++;
                                showMeter(LOCATION);
                            } else {
                                Toast.makeText(NewCbActivity.this, "已经是最后一条", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (Exception e) {
                        Log.e("limbo", e.toString());
                    }


                    xflag = false;
                }

                //            Toast.makeText(this, "左滑", Toast.LENGTH_SHORT).show();  //此处可以更改为当前动作下你想要做的事情
            } else if (endX - beginX > minMove && Math.abs(velocityX) > minVelocity) {   //右滑
                /**
                 * 查看上一条
                 */
                if (!xflag) {
                    changePageFlag = true;//正在翻页
                    save(false);
                    String LastUserWater = mNew_meters.get(LOCATION).getLastReadData();//上月用水
                    String thisMouthUserWater = et_thisMouthUserWater.getText().toString();
                    Log.d("limbo", "上月用水" + LastUserWater +
                            "       本月用水" + thisMouthUserWater);
                    try {
                        if ((spinnerX_MeterState != 0 || spinnerX_CbState != 0) && tipFlag) {
                            xflag = true;
                            String msg1 = "";
                            String msg2 = "";
                            switch (spinnerX_CbState) {
                                case 0:
                                    msg1 = string_cbState[spinnerX_CbState];
                                    msg2 = string_meterState1[spinnerX_MeterState];
                                    break;
                                case 1:
                                    msg1 = string_cbState[spinnerX_CbState];
                                    msg2 = string_meterState2[spinnerX_MeterState];
                                    break;
                                case 2:
                                    msg1 = string_cbState[spinnerX_CbState];
                                    msg2 = string_meterState3[spinnerX_MeterState];
                                    break;
                            }
                            String dataMsg = "";
                            if (et_thisMouthData.getText().toString().equals("") || et_thisMouthUserWater.getText().toString().equals("")) {
                                dataMsg = "缺少关键数据!";
                            }
                            new SweetAlertDialog(NewCbActivity.this, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("警告")
                                    .setContentText("表状态不正常：" + msg1 + ":" + msg2 + "  本月用水:" + mNew_meters.get(LOCATION).getData() + "    \n" + "  本月抄见:" + mNew_meters.get(LOCATION).getReadData() + "    \n" + dataMsg)
                                    .setCancelText("返回")
                                    .setConfirmText("确认,查看上一只表")
                                    .showCancelButton(true)
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            Log.d("limboV", "sure");
                                            sweetAlertDialog.cancel();
                                            if (LOCATION > 0) {
                                                LOCATION--;
                                                showMeter(LOCATION);
                                            } else {
                                                Toast.makeText(NewCbActivity.this, "已经是第一条", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    })
                                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            Log.d("limboV", "cancel");
                                            sDialog.cancel();
                                        }
                                    }).show();
                        } else if (!HzyUtils.isEmpty(thisMouthUserWater) &&
                                Integer.parseInt(thisMouthUserWater) > 0 &&
                                Integer.parseInt(mNew_meters.get(LOCATION).getLastData()) == 0 &&
                                Integer.parseInt(mNew_meters.get(LOCATION).getLastReadData()) == 0 && tipFlag) {
                            new SweetAlertDialog(NewCbActivity.this, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("警告")
                                    .setContentText("表上月用水为0,  本月用水:" + mNew_meters.get(LOCATION).getData() + "    \n" + "  本月抄见:" + mNew_meters.get(LOCATION).getReadData() + "    \n")
                                    .setCancelText("返回")
                                    .setConfirmText("确认,查看上一只表")
                                    .showCancelButton(true)
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            Log.d("limboV", "sure");
                                            sweetAlertDialog.cancel();
                                            if (LOCATION > 0) {
                                                LOCATION--;
                                                showMeter(LOCATION);
                                            } else {
                                                Toast.makeText(NewCbActivity.this, "已经是第一条", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    })
                                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            Log.d("limboV", "cancel");
                                            sDialog.cancel();
                                        }
                                    }).show();
                        } else {
                            if (LOCATION > 0) {
                                LOCATION--;
                                showMeter(LOCATION);
                            } else {
                                Toast.makeText(NewCbActivity.this, "已经是第一条", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (Exception e) {
                        Log.e("limbo", e.toString());
                    }


                    xflag = false;
                }


                //            Toast.makeText(this, "右滑", Toast.LENGTH_SHORT).show();  //此处可以更改为当前动作下你想要做的事情
            } else if (beginY - endY > minMove && Math.abs(velocityY) > minVelocity) {   //上滑
                //            Toast.makeText(this, "上滑", Toast.LENGTH_SHORT).show();  //此处可以更改为当前动作下你想要做的事情
            } else if (endY - beginY > minMove && Math.abs(velocityY) > minVelocity) {   //下滑
                //            Toast.makeText(this, "下滑", Toast.LENGTH_SHORT).show();  //此处可以更改为当前动作下你想要做的事情
            }

            changePageFlag = false;
        }


        return false;
    }

    @Override
    public void onShowPress(MotionEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onSingleTapUp(MotionEvent arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onLongPress(MotionEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float velocityX,
                            float velocityY) {

        return false;
    }

    /**
     * 展示表信息
     */
    private void showMeter(final int location) {
        //        Log.d(BaseActivity.TAG,"showMeter显示刷新");
        HzyUtils.showProgressDialog(NewCbActivity.this);

        if (!showFlag) {
            showFlag = true;
            //            saveMsg(location);
            List<String> strings = DbHelper.getMeterLocationByKH(mNew_meters.get(location).getKH());
            if (strings.size() == 2) {
                mNew_meters.get(location).setLon(strings.get(0));
                mNew_meters.get(location).setLat(strings.get(1));
            }
            tv_hm.setText(mNew_meters.get(location).getUserName().trim());
            tv_meterNumber.setText(mNew_meters.get(location).getMeterNumber());
            /**
             * 未抄表状态下所有表都为正常表
             */
            if (mNew_meters.get(location).getCbFlag() == null || !mNew_meters.get(location).getCbFlag().equals("已抄")) {
                tv_meterState.setText("未抄");
                mNew_meters.get(location).setCbState("0");
                mNew_meters.get(location).setMeterState("0");
            } else {
                tv_meterState.setText(mNew_meters.get(location).getCbFlag());
            }

            /**
             * 用户状态
             */
            if (mNew_meters.get(location).getUserState().equals("36")) {
                tv_userState.setText(string_userState[1]);
            } else if (mNew_meters.get(location).getUserState().equals("31")) {
                tv_userState.setText(string_userState[3]);

            } else if (mNew_meters.get(location).getUserState().equals("43")) {
                tv_userState.setText(string_userState[4]);
            } else {
                tv_userState.setText(string_userState[0]);
            }
            String phone = mNew_meters.get(location).getUserPhone1();
            if (phone == null) {
                tv_phone1.setText("无");
            } else {
                tv_phone1.setText(phone);
            }
            phone = mNew_meters.get(location).getUserPhone2();
            if (phone == null) {
                tv_phone2.setText("无");
            } else {
                tv_phone2.setText(phone);
            }
            if (mNew_meters.get(location).getBtFlag().equals("0")) {
                btn_fh.setVisibility(View.VISIBLE);
                btn_qxfh.setVisibility(View.GONE);
            } else {
                btn_fh.setVisibility(View.GONE);
                btn_qxfh.setVisibility(View.VISIBLE);
            }
            if (mNew_meters.get(location).getBjFlag() == 0) {
                btn_wbj.setVisibility(View.VISIBLE);
                btn_ybj.setVisibility(View.GONE);
            } else {
                btn_wbj.setVisibility(View.GONE);
                btn_ybj.setVisibility(View.VISIBLE);
            }
            tv_number.setText((location + 1) + "/" + LOCATION_Max);
            tv_userNumber.setText(mNew_meters.get(location).getHH());
            tv_meterNumber.setText(mNew_meters.get(location).getMeterNumber());
            tv_caliber.setText(mNew_meters.get(location).getCaliber());
            tv_XLH.setText(mNew_meters.get(location).getXH() + mNew_meters.get(location).getJBH());
            tv_IrDAPort.setText(mNew_meters.get(location).getXqNum() + "-"
                    + mNew_meters.get(location).getCjjNum() + "-"
                    + mNew_meters.get(location).getMeterNum());
            tv_addr.setText(mNew_meters.get(location).getAddr());
            tv_Msg.setText("近三月实用：一、" + mNew_meters.get(location).getLastUserWater() +
                    "   二、" + mNew_meters.get(location).getLast2MouthUserWater() +
                    "   三、" + mNew_meters.get(location).getLast3MouthUserWater());
            et_IrDAData.setText(mNew_meters.get(location).getIrDAData());
            et_LastData.setText(mNew_meters.get(location).getLastData());
            et_LastUserWater.setText(mNew_meters.get(location).getLastReadData());
            et_thisMouthData.setText(mNew_meters.get(location).getReadData());
            et_thisMouthUserWater.setText(mNew_meters.get(location).getData());
            //现场备注
            et_note.setText(mNew_meters.get(location).getNote());
            et_note.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    new SweetAlertDialog(NewCbActivity.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("警告")
                            .setContentText("是否清空备注")
                            .setCancelText("取消")
                            .setConfirmText("确认")
                            .showCancelButton(true)
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    Log.d("limboV", "sure");
                                    sweetAlertDialog.cancel();
                                    et_note.setText("");
                                }
                            })
                            .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    Log.d("limboV", "cancel");
                                    sDialog.cancel();
                                }
                            }).show();
                    return false;
                }
            });
            mNew_meters.get(location).setPic1(bitmap);
            String cbstate = mNew_meters.get(location).getCbState();
            if (cbstate.equals("")) {
                cbstate = "0";
            }
            if (!cbstate.equals("0")) {
                meterState = true;//重置第一次打开表
            }
            spinnerX_CbState = Integer.parseInt(cbstate);
            sp_cbState.setSelection(spinnerX_CbState, true);

            String meterstate = mNew_meters.get(location).getMeterState();
            if (meterstate.equals("")) {
                meterstate = "0";
            }
            spinnerX_MeterState = Integer.parseInt(meterstate);
            switch (spinnerX_CbState) {
                case 0:
                    /**
                     * 表状况
                     */
                    ArrayAdapter<String> ad_meterState = new ArrayAdapter<String>(NewCbActivity.this,
                            R.layout.spinner_item, string_meterState1) {
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            TextView textView = (TextView) super.getView(position, convertView, parent);
                            textView.setTextColor(NewCbActivity.this.getResources().getColor(R.color.black));
                            return textView;
                        }
                    };
                    ad_meterState.setDropDownViewResource(R.layout.spinner_item);
                    sp_meterState.setAdapter(ad_meterState);
                    ad_meterState.notifyDataSetChanged();
                    sp_meterState.setSelection(spinnerX_MeterState);
                    break;
                case 1:
                    /**
                     * 表状况
                     */
                    ad_meterState = new ArrayAdapter<String>(NewCbActivity.this,
                            R.layout.spinner_item, string_meterState2) {
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            TextView textView = (TextView) super.getView(position, convertView, parent);
                            textView.setTextColor(NewCbActivity.this.getResources().getColor(R.color.black));
                            return textView;
                        }
                    };
                    ad_meterState.setDropDownViewResource(R.layout.spinner_item);
                    sp_meterState.setAdapter(ad_meterState);
                    ad_meterState.notifyDataSetChanged();
                    sp_meterState.setSelection(spinnerX_MeterState);
                    break;
                case 2:
                    /**
                     * 表状况
                     */
                    ad_meterState = new ArrayAdapter<String>(NewCbActivity.this,
                            R.layout.spinner_item, string_meterState3) {
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            TextView textView = (TextView) super.getView(position, convertView, parent);
                            textView.setTextColor(NewCbActivity.this.getResources().getColor(R.color.black));
                            return textView;
                        }
                    };
                    ad_meterState.setDropDownViewResource(R.layout.spinner_item);
                    sp_meterState.setAdapter(ad_meterState);
                    ad_meterState.notifyDataSetChanged();
                    sp_meterState.setSelection(spinnerX_MeterState);
                    break;
                default:
                    break;
            }
            bitmap = DbHelper.getPicByKH1(mNew_meters.get(location).getKH(), 0);
            iv_pic.setImageBitmap(bitmap);//照片
            bitmap = DbHelper.getPicByKH1(mNew_meters.get(location).getKH(), 1);
            iv_pic2.setImageBitmap(bitmap);//照片
            bitmap = DbHelper.getPicByKH1(mNew_meters.get(location).getKH(), 2);
            iv_pic3.setImageBitmap(bitmap);//照片
            et_thisMouthData.performClick();
            showFlag = false;
            HzyUtils.closeProgressDialog();
        }
    }

    private void uiset() {
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!changePageFlag && !showFlag) {
                    changePageFlag = true;//正在翻页
                    save(false);
                    String LastUserWater = mNew_meters.get(LOCATION).getLastReadData();//上月用水
                    String thisMouthUserWater = et_thisMouthUserWater.getText().toString();
                    //                    Log.d("limbo", "上月用水" + LastUserWater +
                    //                            "\n本月用水" + thisMouthUserWater);
                    //                    Log.d("limbo", "上月用水" + mNew_meters.get(LOCATION).getLastReadData() +
                    //                            "\n上月读数" + mNew_meters.get(LOCATION).getLastData());
                    try {
                        if ((spinnerX_MeterState != 0 || spinnerX_CbState != 0) && tipFlag) {
                            String msg1 = "";
                            String msg2 = "";
                            switch (spinnerX_CbState) {
                                case 0:
                                    msg1 = string_cbState[spinnerX_CbState];
                                    msg2 = string_meterState1[spinnerX_MeterState];
                                    break;
                                case 1:
                                    msg1 = string_cbState[spinnerX_CbState];
                                    msg2 = string_meterState2[spinnerX_MeterState];
                                    break;
                                case 2:
                                    msg1 = string_cbState[spinnerX_CbState];
                                    msg2 = string_meterState3[spinnerX_MeterState];
                                    break;
                            }
                            String dataMsg = "";
                            if (HzyUtils.isEmpty(et_thisMouthData.getText().toString()) || HzyUtils.isEmpty(thisMouthUserWater)) {
                                dataMsg = "缺少关键数据!";
                            }
                            new SweetAlertDialog(NewCbActivity.this, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("警告")
                                    .setContentText("表状态不正常：" + msg1 + ":" + msg2 + "  本月用水:" + mNew_meters.get(LOCATION).getData() + "    \n" + "  本月抄见:" + mNew_meters.get(LOCATION).getReadData() + "    \n" + dataMsg)
                                    .setCancelText("返回")
                                    .setConfirmText("确认,查看下一只表")
                                    .showCancelButton(true)
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            Log.d("limboV", "sure");
                                            sweetAlertDialog.cancel();
                                            if (LOCATION < LOCATION_Max - 1) {
                                                LOCATION++;
                                                showMeter(LOCATION);
                                            } else {
                                                Toast.makeText(NewCbActivity.this, "已经是最后一条", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    })
                                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            Log.d("limboV", "cancel");
                                            sDialog.cancel();
                                        }
                                    }).show();
                        } else if (!HzyUtils.isEmpty(thisMouthUserWater) &&
                                Integer.parseInt(thisMouthUserWater) > 0 &&
                                Integer.parseInt(mNew_meters.get(LOCATION).getLastData()) == 0 &&
                                Integer.parseInt(mNew_meters.get(LOCATION).getLastReadData()) == 0 && tipFlag) {
                            new SweetAlertDialog(NewCbActivity.this, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("警告")
                                    .setContentText("表上月用水为0,  本月用水:" + mNew_meters.get(LOCATION).getData() + "    \n" + "  本月抄见:" + mNew_meters.get(LOCATION).getReadData() + "    \n")
                                    .setCancelText("返回")
                                    .setConfirmText("确认,查看下一只表")
                                    .showCancelButton(true)
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            Log.d("limboV", "sure");
                                            sweetAlertDialog.cancel();
                                            if (LOCATION < LOCATION_Max - 1) {
                                                LOCATION++;
                                                showMeter(LOCATION);
                                            } else {
                                                Toast.makeText(NewCbActivity.this, "已经是最后一条", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    })
                                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            Log.d("limboV", "cancel");
                                            sDialog.cancel();
                                        }
                                    }).show();
                        } else {
                            if (LOCATION < LOCATION_Max - 1) {
                                LOCATION++;
                                showMeter(LOCATION);
                            } else {
                                Toast.makeText(NewCbActivity.this, "已经是最后一条", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (Exception e) {
                        Log.e("limbo", e.toString());
                    }

                    changePageFlag = false;
                }
            }
        });
        btn_last.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!changePageFlag && !showFlag) {
                    changePageFlag = true;//正在翻页
                    save(false);
                    String LastUserWater = mNew_meters.get(LOCATION).getLastReadData();//上月用水
                    String thisMouthUserWater = et_thisMouthUserWater.getText().toString();
                    Log.d("limbo", "上月用水" + LastUserWater +
                            "\n本月用水" + thisMouthUserWater);
                    try {
                        if ((spinnerX_MeterState != 0 || spinnerX_CbState != 0) && tipFlag) {
                            String msg1 = "";
                            String msg2 = "";
                            switch (spinnerX_CbState) {
                                case 0:
                                    msg1 = string_cbState[spinnerX_CbState];
                                    msg2 = string_meterState1[spinnerX_MeterState];
                                    break;
                                case 1:
                                    msg1 = string_cbState[spinnerX_CbState];
                                    msg2 = string_meterState2[spinnerX_MeterState];
                                    break;
                                case 2:
                                    msg1 = string_cbState[spinnerX_CbState];
                                    msg2 = string_meterState3[spinnerX_MeterState];
                                    break;
                            }
                            String dataMsg = "";
                            if (et_thisMouthData.getText().toString().equals("") || et_thisMouthUserWater.getText().toString().equals("")) {
                                dataMsg = "缺少关键数据!";
                            }
                            new SweetAlertDialog(NewCbActivity.this, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("警告")
                                    .setContentText("表状态不正常：" + msg1 + ":" + msg2 + "  本月用水:" + mNew_meters.get(LOCATION).getData() + "    \n" + "  本月抄见:" + mNew_meters.get(LOCATION).getReadData() + "    \n" + dataMsg)
                                    .setCancelText("返回")
                                    .setConfirmText("确认,查看上一只表")
                                    .showCancelButton(true)
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            Log.d("limboV", "sure");
                                            sweetAlertDialog.cancel();
                                            if (LOCATION > 0) {
                                                LOCATION--;
                                                showMeter(LOCATION);
                                            } else {
                                                Toast.makeText(NewCbActivity.this, "已经是第一条", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    })
                                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            Log.d("limboV", "cancel");
                                            sDialog.cancel();
                                        }
                                    }).show();
                        } else if (!HzyUtils.isEmpty(thisMouthUserWater) &&
                                Integer.parseInt(thisMouthUserWater) > 0 &&
                                Integer.parseInt(mNew_meters.get(LOCATION).getLastData()) == 0 &&
                                Integer.parseInt(mNew_meters.get(LOCATION).getLastReadData()) == 0 && tipFlag) {
                            new SweetAlertDialog(NewCbActivity.this, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("警告")
                                    .setContentText("表上月用水为0,  本月用水:" + mNew_meters.get(LOCATION).getData() + "    \n" + "  本月抄见:" + mNew_meters.get(LOCATION).getReadData() + "    \n")
                                    .setCancelText("返回")
                                    .setConfirmText("确认,查看上一只表")
                                    .showCancelButton(true)
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            Log.d("limboV", "sure");
                                            sweetAlertDialog.cancel();
                                            if (LOCATION > 0) {
                                                LOCATION--;
                                                showMeter(LOCATION);
                                            } else {
                                                Toast.makeText(NewCbActivity.this, "已经是第一条", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    })
                                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            Log.d("limboV", "cancel");
                                            sDialog.cancel();
                                        }
                                    }).show();
                        } else {
                            if (LOCATION > 0) {
                                LOCATION--;
                                showMeter(LOCATION);
                            } else {
                                Toast.makeText(NewCbActivity.this, "已经是第一条", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (Exception e) {
                        Log.e("limbo", e.toString());
                    }

                    changePageFlag = false;
                }


            }
        });
        btn_wbj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNew_meters.get(LOCATION).setBjFlag(1);
                btn_wbj.setVisibility(View.GONE);
                btn_ybj.setVisibility(View.VISIBLE);
                DbHelper.newChangeBjFlag(mNew_meters.get(LOCATION));
            }
        });
        btn_ybj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNew_meters.get(LOCATION).setBjFlag(0);
                btn_wbj.setVisibility(View.VISIBLE);
                btn_ybj.setVisibility(View.GONE);
                DbHelper.newChangeBjFlag(mNew_meters.get(LOCATION));
            }
        });
        btn_pic1.setVisibility(View.GONE);
        btn_pic2.setVisibility(View.GONE);
        btn_pic3.setVisibility(View.GONE);
        btn_qxfh.setVisibility(View.GONE);
        et_thisMouthData.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                detector.onTouchEvent(event);//;//在这里先处理下你的手势左右滑动事件
                return false;
            }
        });
        et_thisMouthUserWater.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                detector.onTouchEvent(event);//;//在这里先处理下你的手势左右滑动事件
                return false;
            }

        });
        iv_pic.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                detector.onTouchEvent(event);//;//在这里先处理下你的手势左右滑动事件
                return false;
            }

        });
        tv_phone1.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                detector.onTouchEvent(event);//;//在这里先处理下你的手势左右滑动事件
                return false;
            }

        });
        tv_phone2.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                detector.onTouchEvent(event);//;//在这里先处理下你的手势左右滑动事件
                return false;
            }
        });
        sp_meterState.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                detector.onTouchEvent(event);//;//在这里先处理下你的手势左右滑动事件
                return false;
            }

        });
        et_IrDAData.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                detector.onTouchEvent(event);//;//在这里先处理下你的手势左右滑动事件
                return false;
            }

        });
        et_LastData.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                detector.onTouchEvent(event);//;//在这里先处理下你的手势左右滑动事件
                return false;
            }

        });
        et_note.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                detector.onTouchEvent(event);//;//在这里先处理下你的手势左右滑动事件
                return false;
            }
        });
        et_LastUserWater.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                detector.onTouchEvent(event);//;//在这里先处理下你的手势左右滑动事件
                return false;
            }

        });
        et_thisMouthData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                yflag = true;
                et_thisMouthData.setBackgroundResource(R.drawable.bg_edittext);
                et_thisMouthUserWater.setBackgroundResource(R.color.gray);

            }
        });

        et_thisMouthUserWater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (spinnerX_MeterState >= 3 || spinnerX_CbState != 0) {
                    yflag = false;
                    et_thisMouthData.setBackgroundResource(R.color.gray);
                    et_thisMouthUserWater.setBackgroundResource(R.drawable.bg_edittext);
                } else {
                    yflag = true;
                    et_thisMouthData.setBackgroundResource(R.drawable.bg_edittext);
                    et_thisMouthUserWater.setBackgroundResource(R.color.gray);
                }

            }
        });
        //表类型选择-统计
        ArrayAdapter<String> ad_tj = new ArrayAdapter<String>(NewCbActivity.this, android.R.layout.simple_spinner_item, string_tj) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                textView.setTextColor(NewCbActivity.this.getResources().getColor(R.color.black));
                return textView;
            }
        };
        ad_tj.setDropDownViewResource(R.layout.spinner_item);
        sp_tj.setAdapter(ad_tj);
        //实现选择项事件(使用匿名类实现接口)
        sp_tj.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            /**
             * 选中某一项时
             */
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {
                LOCATION = 0;
                HzyUtils.showProgressDialog(NewCbActivity.this);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        switch (position) {
                            case 0:
                                mNew_meters = DbHelper.getMeterByXH(XH);//从数据库提取出
                                tipFlag = true;
                                break;
                            case 1:
                                mNew_meters = DbHelper.getYcMeterByXH(XH);//从数据库提取出
                                tipFlag = true;
                                break;
                            case 2:
                                mNew_meters = DbHelper.getWcMeterByXH(XH);//从数据库提取出
                                tipFlag = true;
                                break;
                            case 3:
                                mNew_meters = DbHelper.getKyMeterByXH(XH);//从数据库提取出
                                tipFlag = false;
                                break;
                            case 4:
                                mNew_meters = DbHelper.getFhMeterByXH(XH);//从数据库提取出
                                tipFlag = false;
                                break;
                            case 5:
                                mNew_meters = DbHelper.getPDMeterByXH(XH);//从数据库提取出
                                tipFlag = false;
                                break;
                            case 6:
                                mNew_meters = DbHelper.getPXMeterByXH(XH);//从数据库提取出
                                tipFlag = false;
                                break;
                            case 7:
                                mNew_meters = DbHelper.getWPZMeterByXH(XH);//从数据库提取出
                                tipFlag = false;
                                break;
                            case 8:
                                mNew_meters = DbHelper.getPXFYSMeterByXH(XH);//从数据库提取出
                                tipFlag = false;
                                break;
                            case 9:
                                mNew_meters = DbHelper.getWscMeterByXH(XH);//从数据库提取出
                                tipFlag = false;
                                break;
                            case 10:
                                mNew_meters = DbHelper.getWYbjMeterByXH(XH);//从数据库提取出
                                tipFlag = false;
                                break;
                            case 11:
                                mNew_meters = DbHelper.getBZMeterByXH(XH);//从数据库提取出
                                tipFlag = false;
                                break;
                            default:
                                break;
                        }

                        Message message = new Message();
                        message.what = 0x00;
                        message.arg1 = position;
                        mHandler.sendMessage(message);
                    }
                }).start();

            }

            /**
             * 未选中时
             */
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        // 抄读状况
        ArrayAdapter<String> ad_cbState = new ArrayAdapter<String>(NewCbActivity.this,
                R.layout.spinner_item, string_cbState) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                textView.setTextColor(NewCbActivity.this.getResources().getColor(R.color.black));
                return textView;
            }
        };
        ad_cbState.setDropDownViewResource(R.layout.spinner_item);
        sp_cbState.setAdapter(ad_cbState);

        /**
         * 实现选择项事件(使用匿名类实现接口)
         */
        sp_cbState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            /**
             * 选中某一项时
             */
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerX_CbState = position;
                if (!meterState) {
                    spinnerX_MeterState = 0;
                    switch (position) {
                        case 0:
                            /**
                             * 表状况
                             */
                            ArrayAdapter<String> ad_meterState = new ArrayAdapter<String>(NewCbActivity.this,
                                    R.layout.spinner_item, string_meterState1) {
                                @Override
                                public View getView(int position, View convertView, ViewGroup parent) {
                                    TextView textView = (TextView) super.getView(position, convertView, parent);
                                    textView.setTextColor(NewCbActivity.this.getResources().getColor(R.color.black));
                                    return textView;
                                }
                            };
                            ad_meterState.setDropDownViewResource(R.layout.spinner_item);
                            sp_meterState.setAdapter(ad_meterState);
                            ad_meterState.notifyDataSetChanged();
                            break;
                        case 1:
                            /**
                             * 表状况
                             */
                            ad_meterState = new ArrayAdapter<String>(NewCbActivity.this,
                                    R.layout.spinner_item, string_meterState2) {
                                @Override
                                public View getView(int position, View convertView, ViewGroup parent) {
                                    TextView textView = (TextView) super.getView(position, convertView, parent);
                                    textView.setTextColor(NewCbActivity.this.getResources().getColor(R.color.black));
                                    return textView;
                                }
                            };
                            ad_meterState.setDropDownViewResource(R.layout.spinner_item);
                            sp_meterState.setAdapter(ad_meterState);
                            ad_meterState.notifyDataSetChanged();
                            break;
                        case 2:
                            /**
                             * 表状况
                             */
                            ad_meterState = new ArrayAdapter<String>(NewCbActivity.this,
                                    R.layout.spinner_item, string_meterState3) {
                                @Override
                                public View getView(int position, View convertView, ViewGroup parent) {
                                    TextView textView = (TextView) super.getView(position, convertView, parent);
                                    textView.setTextColor(NewCbActivity.this.getResources().getColor(R.color.black));
                                    return textView;
                                }
                            };
                            ad_meterState.setDropDownViewResource(R.layout.spinner_item);
                            sp_meterState.setAdapter(ad_meterState);
                            ad_meterState.notifyDataSetChanged();
                            break;
                        default:
                            break;
                    }

                } else {
                    meterState = false;
                }
            }

            /**
             * 未选中时
             */
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        /**
         * 实现选择项事件(使用匿名类实现接口)
         */
        sp_meterState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            /**
             * 选中某一项时
             */
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerX_MeterState = position;
                if (isCV()) {
                    yflag = true;
                    et_thisMouthData.setBackgroundResource(R.drawable.bg_edittext);
                    et_thisMouthUserWater.setBackgroundResource(R.color.gray);
                    cv();
                }
            }

            /**
             * 未选中时
             */
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        /**
         * 打电话1
         */
        tv_phone1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new SweetAlertDialog(NewCbActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("提示")
                        .setContentText("是否呼叫该用户?\n号码:" + mNew_meters.get(LOCATION).getUserPhone1())
                        .setCancelText("取消")
                        .setConfirmText("确认")
                        .showCancelButton(true)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                Log.d("limboV", "sure");
                                sweetAlertDialog.cancel();

                                String phone = mNew_meters.get(LOCATION).getUserPhone1();
                                if (phone != null && phone.trim().length() > 0) {
                                    //这里"tel:"+电话号码 是固定格式，系统一看是以"tel:"开头的，就知道后面应该是电话号码。
                                    Intent intent = new Intent("android.intent.action.CALL", Uri.parse("tel:" + phone.trim()));
                                    startActivity(intent);//调用上面这个intent实现拨号
                                } else {
                                    Toast.makeText(NewCbActivity.this, "电话号码不能为空", Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                Log.d("limboV", "cancel");
                                sDialog.cancel();

                            }
                        }).show();

                return false;
            }
        });

        /**
         * 打电话2
         */
        tv_phone2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new SweetAlertDialog(NewCbActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("提示")
                        .setContentText("是否呼叫该用户?\n号码:" + mNew_meters.get(LOCATION).getUserPhone2())
                        .setCancelText("取消")
                        .setConfirmText("确认")
                        .showCancelButton(true)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                Log.d("limboV", "sure");
                                sweetAlertDialog.cancel();

                                String phone = mNew_meters.get(LOCATION).getUserPhone2();
                                if (phone != null && phone.trim().length() > 0) {
                                    //这里"tel:"+电话号码 是固定格式，系统一看是以"tel:"开头的，就知道后面应该是电话号码。
                                    Intent intent = new Intent("android.intent.action.CALL", Uri.parse("tel:" + phone.trim()));
                                    startActivity(intent);//调用上面这个intent实现拨号
                                } else {
                                    Toast.makeText(NewCbActivity.this, "电话号码不能为空", Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                Log.d("limboV", "cancel");
                                sDialog.cancel();

                            }
                        }).show();
                return false;
            }
        });

        /**
         * 复核
         */
        btn_fh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNew_meters.get(LOCATION).setBtFlag("1");
                btn_fh.setVisibility(View.GONE);
                btn_qxfh.setVisibility(View.VISIBLE);
                DbHelper.newChangeFhFlag(mNew_meters.get(LOCATION));
            }
        });

        /**
         * 取消复核
         */
        btn_qxfh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNew_meters.get(LOCATION).setBtFlag("0");
                btn_fh.setVisibility(View.VISIBLE);
                btn_qxfh.setVisibility(View.GONE);
                DbHelper.newChangeFhFlag(mNew_meters.get(LOCATION));
            }
        });
        /**
         * 历史数据
         */
        btn_his.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(NewCbActivity.this, HisDataActivity.class);
                i.putExtra("KH", mNew_meters.get(LOCATION).getKH());
                startActivity(i);
            }
        });
        /**
         * 保存
         */
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (HzyUtils.isEmpty(et_thisMouthData.getText().toString()) || HzyUtils.isEmpty(et_thisMouthUserWater.getText().toString())) {
                    //当前表为空，更新为未抄表
                    tip();
                } else {
                    save(true);
                }

            }
        });
        btn_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mNew_meters.get(LOCATION).getLon().equals("") || mNew_meters.get(LOCATION).getLat().equals("")) {
                    Toast.makeText(NewCbActivity.this, "缺少定位信息", Toast.LENGTH_LONG).show();
                } else if (MenuActivity.lon.equals("") || MenuActivity.lat.equals("")) {
                    Toast.makeText(NewCbActivity.this, "水表档案无定位信息", Toast.LENGTH_LONG).show();
                } else {
                    Intent i = new Intent(NewCbActivity.this, LocationFilter.class);
                    startActivity(i);
                }
            }
        });
        /**
         * 红外抄表
         */
        btn_IrDACB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                X_FLAG = 2;
                Intent i = new Intent(NewCbActivity.this, IrDACbActivity.class);
                i.putExtra("XH", XH);
                startActivityForResult(i, 0);
            }
        });
        /**
         * 查询
         */
        btn_search1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search();
            }
        });
        /**
         * 手动按键
         */
        btn_showInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AJFlag = !AJFlag;
                if (AJFlag) {
                    btn_showInput.setText("隐藏按键");
                    btn_0.setVisibility(View.VISIBLE);
                    btn_1.setVisibility(View.VISIBLE);
                    btn_2.setVisibility(View.VISIBLE);
                    btn_3.setVisibility(View.VISIBLE);
                    btn_4.setVisibility(View.VISIBLE);
                    btn_5.setVisibility(View.VISIBLE);
                    btn_6.setVisibility(View.VISIBLE);
                    btn_7.setVisibility(View.VISIBLE);
                    btn_8.setVisibility(View.VISIBLE);
                    btn_9.setVisibility(View.VISIBLE);
                    btn_delete.setVisibility(View.VISIBLE);
                    btn_search1.setVisibility(View.VISIBLE);
                    btn_pic1.setVisibility(View.GONE);
                    btn_pic2.setVisibility(View.GONE);
                    btn_pic3.setVisibility(View.GONE);
                    btn_video.setVisibility(View.GONE);
                    btn_IrDACB.setVisibility(View.VISIBLE);
                    btn_his.setVisibility(View.VISIBLE);
                    btn_pic.setVisibility(View.VISIBLE);
                    btn_location.setVisibility(View.VISIBLE);
                    btn_fx.setVisibility(View.VISIBLE);
                    btn_voiceModel.setVisibility(View.VISIBLE);
                } else {
                    btn_showInput.setText("显示按键");
                    btn_0.setVisibility(View.GONE);
                    btn_1.setVisibility(View.GONE);
                    btn_2.setVisibility(View.GONE);
                    btn_3.setVisibility(View.GONE);
                    btn_4.setVisibility(View.GONE);
                    btn_5.setVisibility(View.GONE);
                    btn_6.setVisibility(View.GONE);
                    btn_7.setVisibility(View.GONE);
                    btn_8.setVisibility(View.GONE);
                    btn_9.setVisibility(View.GONE);
                    btn_delete.setVisibility(View.GONE);
                    //                    iv_pic.setVisibility(View.VISIBLE);
                    btn_pic1.setVisibility(View.VISIBLE);
                    btn_pic2.setVisibility(View.VISIBLE);
                    btn_pic3.setVisibility(View.VISIBLE);
                    btn_video.setVisibility(View.VISIBLE);
                    btn_search1.setVisibility(View.GONE);

                    btn_IrDACB.setVisibility(View.GONE);
                    btn_his.setVisibility(View.GONE);
                    btn_pic.setVisibility(View.GONE);
                    btn_location.setVisibility(View.GONE);
                    btn_fx.setVisibility(View.GONE);
                    btn_voiceModel.setVisibility(View.GONE);
                }

            }
        });
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (yflag) {
                    String x = et_thisMouthData.getText().toString().trim();
                    if (x.length() != 0) {
                        et_thisMouthData.setText(x.substring(0, x.length() - 1));
                    }
                } else {
                    String x = et_thisMouthUserWater.getText().toString().trim();
                    if (x.length() != 0) {
                        et_thisMouthUserWater.setText(x.substring(0, x.length() - 1));
                    }
                }
                if (isCV()) {
                    cv();
                }
            }
        });
        btn_delete.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                if (yflag) {
                    et_thisMouthData.setText("");
                } else {
                    et_thisMouthUserWater.setText("");
                }
                return false;
            }
        });
        btn_0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (yflag) {
                    et_thisMouthData.append("0");
                } else {
                    et_thisMouthUserWater.append("0");
                }
                if (isCV()) {
                    cv();
                }

            }
        });
        btn_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (yflag) {
                    et_thisMouthData.append("1");
                } else {
                    et_thisMouthUserWater.append("1");
                }
                if (isCV()) {
                    cv();
                }

            }
        });
        btn_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (yflag) {
                    et_thisMouthData.append("2");
                } else {
                    et_thisMouthUserWater.append("2");
                }
                if (isCV()) {
                    cv();
                }

            }
        });
        btn_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (yflag) {
                    et_thisMouthData.append("3");
                } else {
                    et_thisMouthUserWater.append("3");
                }
                if (isCV()) {
                    cv();
                }

            }
        });
        btn_4.setOnClickListener(new ViewClickVibrate() {
            @Override
            public void onClick(View v) {

                if (yflag) {
                    et_thisMouthData.append("4");
                } else {
                    et_thisMouthUserWater.append("4");
                }
                if (isCV()) {
                    cv();
                }

            }
        });
        btn_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (yflag) {
                    et_thisMouthData.append("5");
                } else {
                    et_thisMouthUserWater.append("5");
                }
                if (isCV()) {
                    cv();
                }

            }
        });
        btn_6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (yflag) {
                    et_thisMouthData.append("6");
                } else {
                    et_thisMouthUserWater.append("6");
                }
                if (isCV()) {
                    cv();
                }

            }
        });
        btn_7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (yflag) {
                    et_thisMouthData.append("7");
                } else {
                    et_thisMouthUserWater.append("7");
                }
                if (isCV()) {
                    cv();
                }

            }
        });
        btn_8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (yflag) {
                    et_thisMouthData.append("8");
                } else {
                    et_thisMouthUserWater.append("8");
                }
                if (isCV()) {
                    cv();
                }

            }
        });
        btn_9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (yflag) {
                    et_thisMouthData.append("9");
                } else {
                    et_thisMouthUserWater.append("9");
                }
                if (isCV()) {
                    cv();
                }

            }
        });
        btn_0.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                detector.onTouchEvent(event);//;//在这里先处理下你的手势左右滑动事件
                return false;
            }
        });
        btn_1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                detector.onTouchEvent(event);//;//在这里先处理下你的手势左右滑动事件
                return false;
            }
        });
        btn_2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                detector.onTouchEvent(event);//;//在这里先处理下你的手势左右滑动事件
                return false;
            }
        });
        btn_3.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                detector.onTouchEvent(event);//;//在这里先处理下你的手势左右滑动事件
                return false;
            }
        });
        btn_4.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                detector.onTouchEvent(event);//;//在这里先处理下你的手势左右滑动事件
                return false;
            }
        });
        btn_5.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                detector.onTouchEvent(event);//;//在这里先处理下你的手势左右滑动事件
                return false;
            }
        });
        btn_6.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                detector.onTouchEvent(event);//;//在这里先处理下你的手势左右滑动事件
                return false;
            }
        });
        btn_7.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                detector.onTouchEvent(event);//;//在这里先处理下你的手势左右滑动事件
                return false;
            }
        });
        btn_8.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                detector.onTouchEvent(event);//;//在这里先处理下你的手势左右滑动事件
                return false;
            }
        });
        btn_9.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                detector.onTouchEvent(event);//;//在这里先处理下你的手势左右滑动事件
                return false;
            }
        });
        btn_showInput.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                detector.onTouchEvent(event);//;//在这里先处理下你的手势左右滑动事件
                return false;
            }
        });
        btn_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //                if (FLAG_LOCATION) {
                X_FLAG = 0;
                PIC_FLAG = 0;
                camera();
            }
        });
        //拍多张图片
        btn_pic1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                X_FLAG = 0;
                PIC_FLAG = 0;
                camera();
            }
        });
        btn_pic2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PIC_FLAG = 1;
                camera();
            }
        });
        btn_pic3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PIC_FLAG = 2;
                camera();
            }
        });
        btn_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("video/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent,3);
            }
        });
        btn_video.setVisibility(View.GONE);
        btn_fx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SweetAlertDialog(NewCbActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("报修")
                        .setContentText("是否报修当前水表？")
                        .setCancelText("否")
                        .setConfirmText("确认")
                        .showCancelButton(true)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                Log.d("limboV", "sure");
                                sweetAlertDialog.cancel();
                                Intent i = new Intent(NewCbActivity.this, BXActivity.class);

                                startActivity(i);
                            }
                        })
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                Log.d("limboV", "cancel");
                                sDialog.cancel();
                            }
                        }).show();
            }
        });
        iv_pic.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent i = new Intent(NewCbActivity.this, ImageViewActivity.class);
                i.putExtra("LOCATION", LOCATION);
                i.putExtra("id", 0);
                startActivity(i);
                return false;
            }
        });
        iv_pic2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent i = new Intent(NewCbActivity.this, ImageViewActivity.class);
                i.putExtra("LOCATION", LOCATION);
                i.putExtra("id", 1);
                startActivity(i);
                return false;
            }
        });
        iv_pic3.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent i = new Intent(NewCbActivity.this, ImageViewActivity.class);
                i.putExtra("LOCATION", LOCATION);
                i.putExtra("id", 2);
                startActivity(i);
                return false;
            }
        });
        btn_voiceModel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!VOICE_FLAG) {
                    mAsr.startListening(recognizerListener);
                    btn_voiceModel.setText("关闭语音");
                    VOICE_FLAG = true;
                } else {
                    mAsr.stopListening();
                    btn_voiceModel.setText("语音");
                    VOICE_FLAG = false;
                }
            }
        });
    }

    private void camera() {
        // 执行拍照前，应该先判断SD卡是否存在
        String SDState = Environment.getExternalStorageState();
        if (!SDState.equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(NewCbActivity.this, "内存卡不存在", Toast.LENGTH_LONG).show();
            return;
        }
        /**
         * 拍照
         * android7.0以上适配
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            File outputFile = new File(PHOTO_FOLDER + PHOTO_NAME);
            if (!outputFile.getParentFile().exists()) {
                outputFile.getParentFile().mkdir();
            }
            Uri contentUri = FileProvider.getUriForFile(NewCbActivity.this,
                    BuildConfig.APPLICATION_ID + ".myprovider", outputFile);

            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);

            //                    intent.putExtra("android.intent.extra.quickCapture",true);//启用快捷拍照
            startActivityForResult(intent, 1);
        } else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(PHOTO_FOLDER + PHOTO_NAME)));
            startActivityForResult(intent, 100);
        }
    }

    private int search() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(NewCbActivity.this);
        // 通过LayoutInflater来加载一个xml的布局文件作为一个View对象
        View view = LayoutInflater.from(NewCbActivity.this).inflate(R.layout.dialog_msg2, null);
        // 设置我们自己定义的布局文件作为弹出框的Content
        builder.setView(view);
        //这个位置十分重要，只有位于这个位置逻辑才是正确的
        final AlertDialog dialog = builder.show();

        final Spinner sp1 = (Spinner) view.findViewById(R.id.sp1);
        final EditText et2 = (EditText) view.findViewById(R.id.edThreshold2);
        final String[] arr = {"户号查询", "表号查询", "最后一户", "第一户", "序号查询", "地址查询", "备注查询", "户名查询"};

        ArrayAdapter<String> ad_1 = new ArrayAdapter<String>(NewCbActivity.this, R.layout.spinner_item, arr) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                textView.setTextColor(NewCbActivity.this.getResources().getColor(R.color.white));
                return textView;
            }
        };

        ad_1.setDropDownViewResource(R.layout.spinner_item);
        sp1.setAdapter(ad_1);
        sp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            /**
             * 选中某一项时
             */
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                POSITION = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        view.findViewById(R.id.btn_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //确认
                String msg = et2.getText().toString().trim();
                switch (POSITION) {
                    case 0:
                        if (msg.length() == 0) {
                            LOCATION = 0;
                            break;
                        }
                        for (int i = 0; i < mNew_meters.size(); i++) {
                            if (mNew_meters.get(i).getHH().contains(msg)) {
                                LOCATION = i;
                                break;
                            }
                        }
                        break;

                    case 1:
                        if (msg.length() == 0) {
                            LOCATION = 0;
                            break;
                        }
                        for (int i = 0; i < mNew_meters.size(); i++) {
                            if (mNew_meters.get(i).getMeterNumber().contains(msg)) {
                                LOCATION = i;
                                break;
                            }
                        }
                        break;

                    case 2:
                        LOCATION = LOCATION_Max - 1;
                        break;
                    case 3:
                        LOCATION = 0;
                        break;
                    case 4:
                        if (msg.length() == 0 || msg.equals("0")) {
                            LOCATION = 0;
                            break;

                        } else {
                            LOCATION = Integer.parseInt(msg) - 1;
                            break;
                        }
                    case 5:
                        if (msg.length() == 0) {
                            break;
                        }
                        for (int i = 0; i < mNew_meters.size(); i++) {
                            if (mNew_meters.get(i).getAddr().replaceAll(" ","").contains(msg)) {
                                LOCATION = i;
                                break;
                            }
                        }
                        break;
                    case 6:
                        if (msg.length() == 0) {
                            break;
                        }
                        for (int i = 0; i < mNew_meters.size(); i++) {
                            if (mNew_meters.get(i).getNote().contains(msg)) {
                                LOCATION = i;
                                break;
                            }
                        }
                        break;
                    case 7:
                        if (msg.length() == 0) {
                            break;
                        }
                        for (int i = 0; i < mNew_meters.size(); i++) {
                            if (mNew_meters.get(i).getUserName().contains(msg)) {
                                LOCATION = i;
                                break;
                            }
                        }
                        break;
                    default:
                        break;
                }
                showMeter(LOCATION);
                /**
                 * 隐藏软键盘
                 */
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                dialog.dismiss();

            }
        });
        view.findViewById(R.id.btn_cancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * 隐藏软键盘
                 */
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                //取消+关闭对话框
                dialog.dismiss();
                //写相关的服务代码

            }
        });
        /**
         * 显示软键盘
         */
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(200);
                    et2.setFocusable(true);
                    et2.setFocusableInTouchMode(true);
                    et2.requestFocus();
                    InputMethodManager inputManager = (InputMethodManager) et2.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.showSoftInput(et2, 0);
                } catch (Exception e) {
                    Log.e("limbo", "弹出软键盘:" + e.toString());
                }

            }
        }).start();


        return 0;
    }

    private void analysisSentences() throws Exception {
        if (sentence == null) {
            mAsr.stopListening();
            mAsr.startListening(recognizerListener);
            return;
        }
        if (!sentence.toString().contains("。")) {
            mAsr.stopListening();
            Log.e("TAG", sentence.toString());
            if (sentence.indexOf("确定") != -1) {
                //do something
                if (isDialogExist) {
                    View v = getWindow().getDecorView();
                    v.setId(cn.pedant.SweetAlert.R.id.confirm_button);
                    msweetAlertDialog.onClick(v);
                } else
                    btn_next.performClick();
                mAsr.startListening(recognizerListener);
            } else if (sentence.indexOf("取消") != -1) {
                if (isDialogExist) {
                    View v = getWindow().getDecorView();
                    v.setId(cn.pedant.SweetAlert.R.id.cancel_button);
                    msweetAlertDialog.onClick(v);
                }
                if (yflag) {
                    et_thisMouthData.setText("");
                } else {
                    et_thisMouthUserWater.setText("");
                }
                if (isCV()) {
                    cv();
                }
            } else if (judgeSentence(sentence.toString())) {
                sentence = filterWords(sentence);
                if (yflag) {
                    et_thisMouthData.setText(sentence);
                } else {
                    et_thisMouthUserWater.setText(sentence);
                }
                if (isCV()) {
                    cv();
                }
                //                et_thisMouthData.setText(sentence);
            } else if (sentence.toString().contains("结束")) {
                mAsr.stopListening();
                btn_voiceModel.setText("语音");
                VOICE_FLAG = false;
                return;
            }
        }
        mAsr.stopListening();
        mAsr.startListening(recognizerListener);
    }

    //判断语句是否合格
    private boolean judgeSentence(String s) {
        if (s.contains("1") || s.contains("2") || s.contains("3") || s.contains("4") ||
                s.contains("5") || s.contains("6") || s.contains("7") || s.contains("8")
                || s.contains("9") || s.contains("0") || s.contains("零") || s.contains("一") ||
                s.contains("二") || s.contains("三") || s.contains("四") || s.contains("五") ||
                s.contains("六") || s.contains("七") || s.contains("八") || s.contains("九") || s.contains("十")) {
            return true;
        }
        return false;
    }

    //过滤识别的字符
    private StringBuilder filterWords(StringBuilder s) {
        try {
            String num = "0123456789";
            String[] ch_num = {"零", "一", "二", "三", "四", "五", "六", "七", "八", "九", "十"};
            for (String tmps : ch_num) {
                if (s.toString().equals(tmps)) {
                    if (tmps.equals("零")) {
                        s = new StringBuilder("0");
                    } else
                        s = toNum(s);
                    return s;
                }

            }
            int len = s.length();
            for (int i = 0; i < len; i++) {
                char tmpchar = s.charAt(i);
                if (!num.contains(String.valueOf(tmpchar))) {
                    s.deleteCharAt(i);
                    len--;
                    i--;
                }
            }
            return s;
        } catch (Exception e) {
            Log.e("limbo", e.toString());
            return null;
        }

    }

    //将中文转为数字
    private StringBuilder toNum(StringBuilder stringBuilder) {
        String tmpString = stringBuilder.toString();
        int result = 0;
        int temp = 1;//存放一个单位的数字如：十万
        int count = 0;//判断是否有chArr
        char[] cnArr = new char[]{'一', '二', '三', '四', '五', '六', '七', '八', '九'};
        char[] chArr = new char[]{'十', '百', '千', '万', '亿'};
        for (int i = 0; i < tmpString.length(); i++) {
            boolean b = true;//判断是否是chArr
            char c = tmpString.charAt(i);
            for (int j = 0; j < cnArr.length; j++) {//非单位，即数字
                if (c == cnArr[j]) {
                    if (0 != count) {//添加下一个单位之前，先把上一个单位值添加到结果中
                        result += temp;
                        temp = 1;
                        count = 0;
                    }
                    // 下标+1，就是对应的值
                    temp = j + 1;
                    b = false;
                    break;
                }
            }
            if (b) {//单位{'十','百','千','万','亿'}
                for (int j = 0; j < chArr.length; j++) {
                    if (c == chArr[j]) {
                        switch (j) {
                            case 0:
                                temp *= 10;
                                break;
                            case 1:
                                temp *= 100;
                                break;
                            case 2:
                                temp *= 1000;
                                break;
                            case 3:
                                temp *= 10000;
                                break;
                            case 4:
                                temp *= 100000000;
                                break;
                            default:
                                break;
                        }
                        count++;
                    }
                }
            }
            if (i == tmpString.length() - 1) {//遍历到最后一个字符
                result += temp;
            }
        }
        return new StringBuilder(String.valueOf(result));
    }

    private void cv() {
        String data = et_thisMouthData.getText().toString();//本月读数
        String lastData = et_LastData.getText().toString();//上月读数
        String LastUserWater = et_LastUserWater.getText().toString();//上月用水
        //        String thisUseWater = et_thisMouthUserWater.getText().toString();

        float thisMouthData;
        float lastMouthData;
        if (data.length() != 0) {
            if ((spinnerX_CbState == 0 && spinnerX_MeterState < 3) || (spinnerX_MeterState == 9)) {
                try {
                    if (yflag) {
                        thisMouthData = Integer.parseInt(data) - Integer.parseInt(lastData);
                    } else {
                        thisMouthData = Integer.parseInt(et_thisMouthUserWater.getText().toString());
                    }
                    lastMouthData = Integer.parseInt(LastUserWater);
                    Log.d("limbo", "thisMouthData:" + thisMouthData);
                    //                    Log.d("limbo", "lastMouthData:" + lastMouthData);
                } catch (Exception e) {
                    Toast.makeText(NewCbActivity.this, "数值异常", Toast.LENGTH_LONG).show();
                    return;
                }
                if (!HzyUtils.isEmpty(mNew_meters.get(LOCATION).getLastUserWater())
                        && !HzyUtils.isEmpty(mNew_meters.get(LOCATION).getLast2MouthUserWater())
                        && thisMouthData == lastMouthData
                        && Float.parseFloat(mNew_meters.get(LOCATION).getLastUserWater()) == thisMouthData
                        && thisMouthData != 0) {
                    spinnerX_MeterState = 9;
                    sp_meterState.setSelection(spinnerX_MeterState);
                } else {
                    if (lastMouthData == 0) {
                        if (thisMouthData > 50) {
                            spinnerX_MeterState = 1;
                            sp_meterState.setSelection(spinnerX_MeterState);
                        } else if (thisMouthData < 0) {
                            spinnerX_MeterState = 2;
                            sp_meterState.setSelection(spinnerX_MeterState);
                        } else {
                            spinnerX_MeterState = 0;
                            sp_meterState.setSelection(spinnerX_MeterState);
                        }
                    } else {
                        if (thisMouthData <= 0) {
                            spinnerX_MeterState = 2;
                            sp_meterState.setSelection(spinnerX_MeterState);
                        } else if (thisMouthData > 0) {
                            if (lastMouthData <= 5 && lastMouthData > 0) {
                                if ((thisMouthData / lastMouthData) <= 4) {
                                    spinnerX_MeterState = 0;
                                } else if ((thisMouthData / lastMouthData) == 0) {
                                    //                            et_waterStyle.setText("用水量偏小");
                                    spinnerX_MeterState = 2;
                                } else {
                                    spinnerX_MeterState = 1;
                                }
                            } else if (lastMouthData <= 10 && lastMouthData > 5) {
                                if ((thisMouthData / lastMouthData) <= 3 && (thisMouthData / lastMouthData) > 0.4) {
                                    //                            et_waterStyle.setText("正常");
                                    spinnerX_MeterState = 0;
                                } else if ((thisMouthData / lastMouthData) < 0.4) {
                                    //                            et_waterStyle.setText("用水量偏小");
                                    spinnerX_MeterState = 2;
                                } else if ((thisMouthData / lastMouthData) > 3) {
                                    //                            et_waterStyle.setText("用水量偏大");
                                    spinnerX_MeterState = 1;
                                }
                            } else if (lastMouthData <= 15 && lastMouthData > 10) {
                                if ((thisMouthData / lastMouthData) <= 2 && (thisMouthData / lastMouthData) > 0.4) {
                                    spinnerX_MeterState = 0;
                                } else if ((thisMouthData / lastMouthData) < 0.4) {
                                    spinnerX_MeterState = 2;
                                } else if ((thisMouthData / lastMouthData) > 2) {
                                    spinnerX_MeterState = 1;
                                }
                            } else if (lastMouthData <= 40 && lastMouthData > 15) {
                                if ((thisMouthData / lastMouthData) <= 1.6 && (thisMouthData / lastMouthData) > 0.4) {
                                    spinnerX_MeterState = 0;
                                } else if ((thisMouthData / lastMouthData) < 0.4) {
                                    spinnerX_MeterState = 2;
                                } else if ((thisMouthData / lastMouthData) > 1.6) {
                                    spinnerX_MeterState = 1;
                                }
                            } else if (lastMouthData > 40) {
                                if ((thisMouthData / lastMouthData) <= 1.3 && (thisMouthData / lastMouthData) > 0.7) {
                                    spinnerX_MeterState = 0;
                                } else if ((thisMouthData / lastMouthData) < 0.7) {
                                    spinnerX_MeterState = 2;
                                } else if ((thisMouthData / lastMouthData) > 1.3) {
                                    spinnerX_MeterState = 1;
                                }
                            }
                            sp_meterState.setSelection(spinnerX_MeterState);
                        }
                    }
                }
                et_thisMouthUserWater.setText((int) thisMouthData + "");


            }

        }
    }

    /**
     * 使用SharePreferences保存用户信息
     */
    public void saveMsg(int xhInt) {

        SharedPreferences.Editor editor = getSharedPreferences("user_msg", MODE_PRIVATE).edit();
        editor.putInt("xhInt" + XH, xhInt);
        editor.commit();
    }

    /**
     * 加载用户信息
     */
    public void loadMsg() {
        SharedPreferences pref = getSharedPreferences("user_msg", MODE_PRIVATE);
        int i = pref.getInt("xhInt" + XH, 0);
        if (i < LOCATION_Max) {
            LOCATION = i;
            showMeter(i);
        } else {
            showMeter(0);
        }

    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        saveMsg(LOCATION);
        IrDACbActivity.selectionXq = 0;
        IrDACbActivity.selectionCjj = 0;
        super.onDestroy();
    }

    boolean isCV() {
        if ((spinnerX_CbState == 0 && spinnerX_MeterState < 3) || (spinnerX_MeterState == 9)) {
            return true;
        } else {
            return false;
        }

    }
}
