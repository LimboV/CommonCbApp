package com.commoncb.seck.commoncbapp.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.commoncb.seck.commoncbapp.DB.DbHelper;
import com.commoncb.seck.commoncbapp.R;
import com.commoncb.seck.commoncbapp.modle1.BxModle;
import com.commoncb.seck.commoncbapp.modle1.New_Meter;
import com.commoncb.seck.commoncbapp.utils.HintDialog;
import com.commoncb.seck.commoncbapp.utils.HzyUtils;
import com.google.gson.Gson;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class BXActivity extends BaseActivity {
    @BindView(R.id.userno)
    EditText et_userno;
    @BindView(R.id.cardno)
    EditText et_cardno;
    @BindView(R.id.cardname)
    EditText et_cardname;
    @BindView(R.id.sheetno)
    EditText et_sheetno;
    @BindView(R.id.address)
    EditText et_address;
    @BindView(R.id.cjxh)
    EditText et_cjxh;
    @BindView(R.id.hwx)
    EditText et_hwx;
    @BindView(R.id.readmeteruser)
    EditText et_readmeteruser;
    @BindView(R.id.newtel)
    EditText et_newtel;
    @BindView(R.id.cmetertype)
    EditText et_cmetertype;
    @BindView(R.id.gzlx)
    Spinner sp_gzlx;
    @BindView(R.id.wellno)
    EditText et_wellno;
    @BindView(R.id.ifchangemeter)
    Spinner sp_ifchangemeter;
    @BindView(R.id.remark)
    EditText et_remark;
    @BindView(R.id.btn_up)
    Button btn_up;
    @BindView(R.id.ll_1)
    LinearLayout ll_1;
    @BindView(R.id.ll_2)
    LinearLayout ll_2;
    Thread mThread;

    String userno, cardno, cardname, sheetno, address, cjxh, hwx, wxjg, readmeteruser, gzyy, newtel, cmetertype, gzlx,
            wellno, ifchangemeter, remark;
    int int_gzlx = 0, int_ifchangemeter = 0;
    private final String[] string_sp_gzlx = {"水表故障", "水表对调", "采集箱故障", "无数据（或无远传设备）", "数据不符（基表与传输数据）", "供电故障 （填写数字）", "采集箱故障","数据线对调","其他"};
    private final String[] string_sp_ifchangemeter = {"是", "否"};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bx);
        ButterKnife.bind(this);
        ll_1.setVisibility(View.GONE);
        ll_2.setVisibility(View.GONE);

        New_Meter meter = NewCbActivity.mNew_meters.get(NewCbActivity.LOCATION);
        et_userno.setText(MainActivity.user);
        et_cardno.setText(meter.getHH());
        et_cardname.setText(meter.getUserName());
        et_sheetno.setText(meter.getXH());
        et_address.setText(meter.getAddr());
        et_cjxh.setText(meter.getCjjNum());
        et_readmeteruser.setText(HzyUtils.isNull(MainActivity.muser.getDatas().get(0).getUserName()));
        et_newtel.setText(meter.getUserPhone2());
        et_remark.setText(meter.getNote());

        et_userno.setFocusable(false);
        et_cardno.setFocusable(false);
        et_cardname.setFocusable(false);
        et_sheetno.setFocusable(false);
        et_address.setFocusable(false);
        et_cjxh.setFocusable(false);
        et_readmeteruser.setFocusable(false);
        //        et_newtel.setFocusable(false);
        //        et_remark.setFocusable(false);

        final BxModle bxModle = new BxModle();
        bxModle.setClientid("WX");
        bxModle.setKey("WX");
        bxModle.setUserno(MainActivity.user);
        bxModle.setBasecode("1");
        bxModle.setBustype("1");
        bxModle.setFlowcode("MetReadModiManag");
        bxModle.setAppuserno(MainActivity.user);
        bxModle.setColumnname("meterphotos");
        bxModle.setAttachList("");
        bxModle.setFiletype("");
        bxModle.setFiles("");
        bxModle.setFilesuffix("");
        bxModle.setFilecontent("");
        bxModle.setCardno(meter.getHH());
        bxModle.setCardname(meter.getUserName().trim());
        bxModle.setSheetno(meter.getXH());
        bxModle.setAddress(meter.getAddr().trim());
        bxModle.setCjjbh(meter.getCjjNum());
        bxModle.setReadmeteruser(HzyUtils.isNull(MainActivity.muser.getDatas().get(0).getUserName()));
        bxModle.setNewtel(meter.getUserPhone2());
        bxModle.setRemark(meter.getNote());

        ArrayAdapter<String> ad_gzlx = new ArrayAdapter<String>(BXActivity.this,
                R.layout.spinner_item, string_sp_gzlx) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                textView.setTextColor(BXActivity.this.getResources().getColor(R.color.black));
                return textView;
            }
        };
        ad_gzlx.setDropDownViewResource(R.layout.spinner_item);
        sp_gzlx.setAdapter(ad_gzlx);
        sp_gzlx.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            /**
             * 选中某一项时
             */
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {
                int_gzlx = position;
                bxModle.setGzlx(int_gzlx + "");
            }

            /**
             * 未选中时
             */
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<String> ad_ifchangemeter = new ArrayAdapter<String>(BXActivity.this,
                R.layout.spinner_item, string_sp_ifchangemeter) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                textView.setTextColor(BXActivity.this.getResources().getColor(R.color.black));
                return textView;
            }
        };
        ad_ifchangemeter.setDropDownViewResource(R.layout.spinner_item);
        sp_ifchangemeter.setAdapter(ad_ifchangemeter);
        sp_ifchangemeter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            /**
             * 选中某一项时
             */
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {
                int_ifchangemeter = position;
            }

            /**
             * 未选中时
             */
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        try {
            byte[] bitmap = DbHelper.getPicByKH(meter.getKH(), 0);
            if (bitmap != null) {
                String base64 = Base64.encodeToString(bitmap, Base64.DEFAULT);
                //                                    Log.d("limbo", "上传图片大小：" + base64.length() / 1024 + "K");
                bxModle.setFilecontent(base64);
            }


        } catch (Exception e) {
            Log.e("limbo", "pic1Error:" + e.toString());
        }
        btn_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SweetAlertDialog(BXActivity.this, SweetAlertDialog.WARNING_TYPE)
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
                                bxModle.setCmetertype(et_cmetertype.getText().toString());
                                bxModle.setHwx(et_hwx.getText().toString());
                                bxModle.setWellno(et_wellno.getText().toString());
                                bxModle.setRemark(et_remark.getText().toString());
                                switch (int_ifchangemeter) {
                                    case 0:
                                        bxModle.setIfchangemeter("是");
                                        break;
                                    case 1:
                                        bxModle.setIfchangemeter("否");
                                        break;
                                    default:
                                        break;
                                }
                                up(bxModle);
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
    }

    private void up(final BxModle bxModle) {
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //json为String类型的json数据
                    String gsonString = null;
                    Gson gson = new Gson();
                    if (gson != null) {
                        /** Json格式数据*/
                        gsonString = gson.toJson(bxModle);
                        //                        gsonString = "{'address':'云鹿路蔚蓝海岸2010','appuserno':'0604','attachList':'','basecode':'1','bustype':'1','cardname':'倪梅玲','cardno':'13800263','cjxh':'9','clientid':'WX','columnname':'meterphotos','filecontent':'','files':'','filesuffix':'','filetype':'','flowcode':'MetReadModiManag','gzlx':'0','ifchangemeter':'是','key':'WX','newtel':'13506093528 ','readmeteruser':'林真真','remark':'','sheetno':'9596','userno':'0604','xqmc':'蔚蓝海岸'}";
                        Log.d("limbo", gsonString);
                        /** 命名空间. */
                        String NAME_SPACE = "http://service.webservice.com";
                        /** 方法. */
                        String METHOD_NAME = "submit";
                        /** WSDL文件的URL. */
                        String WSDL_URL = "http://222.77.71.74:8086/workflow/services/WorkFlowWsService?wsdl";
                        //1. 指定WebService的命名空间和调用的方法名
                        SoapObject request = new SoapObject(NAME_SPACE, METHOD_NAME);
                        //2. 设置调用方法的参数值，这一步是可选的，如果方法没有参数，可以省略这一步
                        request.addProperty("data", gsonString);
                        //3. 生成调用WebService方法的SOAP请求信息。该信息由SoapSerializationEnvelope对象描述
                        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);
                        envelope.bodyOut = request;
                        //4. 创建HttpTransportSE对象。通过HttpTransportSE类的构造方法可以指定WebService的WSDL文档的URL
                        HttpTransportSE ht = new HttpTransportSE(WSDL_URL);
                        ht.debug = true;
                        //5. 使用call方法调用WebService方法
                        ht.call(NAME_SPACE + "/" + METHOD_NAME, envelope, null);// 注意，如果不需要头文件，只需要传递前两个参数即可
                        //6. 使用getResponse方法获得WebService方法的返回结果
                        //                        SoapObject result = (SoapObject) envelope.bodyIn;
                        //                        SoapObject result = (SoapObject) envelope.getResponse();
                        Object result = (Object) envelope.getResponse();
                        String resultString = result.toString();
                        Log.d("limbo", resultString);
                        Message message = new Message();
                        message.what = 0x00;
                        message.obj = resultString;
                        handler.sendMessage(message);



                        /*OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象
                        Response response = null;
                        try {
                            response = client.newCall(request).execute();//得到Response 对象
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        //判断请求是否成功
                        if (response.isSuccessful()) {
                            //打印服务端返回结果
                            String responseX = null;
                            try {
                                responseX = response.body().string();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Log.d("limbo", "成功报修:" + responseX);
                            Gson gson1 = new Gson();//使用Gson解析
                            //                            Count c = gson1.fromJson(responseX, Count.class);
                        } else {
                            Log.d("limbo", "报修失败" + response.toString());
                        }*/
                    }
                } catch (Exception e) {
                    Log.d("limbo", "报修出现异常:" + e.toString());
                    Message message = new Message();
                    message.what = 0x01;
                    message.obj = e.toString();
                    handler.sendMessage(message);
                }
            }
        });
        mThread.start();
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x00:
                    String resultString = (String) msg.obj;
                    HintDialog.ShowHintDialog(BXActivity.this, "提交完成" + resultString, "success");
                    break;
                case 0x01:
                    resultString = (String) msg.obj;
                    HintDialog.ShowHintDialog(BXActivity.this, "报修出现异常:" + resultString, "error");
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
