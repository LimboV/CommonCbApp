package com.commoncb.seck.commoncbapp.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.commoncb.seck.commoncbapp.R;
import com.commoncb.seck.commoncbapp.service.LocationApplication;
import com.commoncb.seck.commoncbapp.service.LocationService;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static java.lang.Double.parseDouble;

/**
 * Created by limbo on 2018/6/20.
 */
public class LocationFilter extends BaseActivity {
    private MapView mMapView = null;
    private BaiduMap mBaiduMap;
    private Button btn_baidu;
    private LocationService locService;
    private LinkedList<LocationEntity> locationList = new LinkedList<LocationEntity>(); // 存放历史定位结果的链表，最大存放当前结果的前5次定位结果
    LatLng point1, point2;
    LocationClient mLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        initMap();

        //跳转百度
        btn_baidu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if (isAvilible(LocationFilter.this, "com.baidu.BaiduMap")) {//传入指定应用包名
                    try {
                        intent = Intent.getIntent("intent://map/direction?" +
                                "destination=latlng:" + point1.latitude + "," + point1.longitude +
                                "|name:" + "水表位置" + "&mode=driving&src=某某公司#Intent;" + "scheme=bdapp;package=com.baidu.BaiduMap;end");
                        startActivity(intent);

                    } catch (URISyntaxException e) {
                        Log.e("limbo", e.getMessage());
                    }
                } else {//未安装
                    //market为路径，id为包名
                    //显示手机上所有的market商店
                    Toast.makeText(LocationFilter.this, "您尚未安装百度地图", Toast.LENGTH_LONG).show();
                    Uri uri = Uri.parse("market://details?id=com.baidu.BaiduMap");
                    intent = new Intent(Intent.ACTION_VIEW, uri);
                    LocationFilter.this.startActivity(intent);
                }
            }
        });
    }

    private void initMap() {
        setContentView(R.layout.locationfilter);
        btn_baidu = findViewById(R.id.btn_baidu);
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(15));
        mBaiduMap.setMyLocationEnabled(true);
        locService = ((LocationApplication) getApplication()).locationService;
        LocationClientOption mOption = locService.getDefaultLocationClientOption();
        mOption.setLocationMode(LocationClientOption.LocationMode.Battery_Saving);
        mOption.setCoorType("bd09ll");

        point1 = new LatLng(parseDouble(NewCbActivity.mNew_meters.get(NewCbActivity.LOCATION).getLat()), parseDouble(NewCbActivity.mNew_meters.get(NewCbActivity.LOCATION).getLon()));
        point2 = new LatLng(Double.parseDouble(MenuActivity.lat), Double.parseDouble(MenuActivity.lon));

        Message locMsg = locHander.obtainMessage();
        locHander.sendMessage(locMsg);
        location();
    }

    /**
     * 检查手机上是否安装了指定的软件
     *
     * @param context
     * @param packageName：应用包名
     * @return
     */
    public static boolean isAvilible(Context context, String packageName) {
        //获取packagemanager
        final PackageManager packageManager = context.getPackageManager();
        //获取所有已安装程序的包信息
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        //用于存储所有已安装程序的包名
        List<String> packageNames = new ArrayList<String>();
        //从pinfo中将包名字逐一取出，压入pName list中
        if (packageInfos != null) {
            for (int i = 0; i < packageInfos.size(); i++) {
                String packName = packageInfos.get(i).packageName;
                packageNames.add(packName);
            }
        }
        //判断packageNames中是否有目标程序的包名，有TRUE，没有FALSE
        return packageNames.contains(packageName);
    }

    /***
     * 接收定位结果消息，并显示在地图上
     */
    private Handler locHander = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            try {
                // 构建Marker图标
                BitmapDescriptor bitmap1 = null, bitmap2 = null;
                Bitmap bit1 = BitmapFactory.decodeResource(getResources(), R.drawable.meter);
                bitmap1 = BitmapDescriptorFactory.fromBitmap(zoomImg(bit1, 80, 80));

                //                Bitmap bit2 = BitmapFactory.decodeResource(getResources(), R.drawable.people);
                //                bitmap2 = BitmapDescriptorFactory.fromBitmap(zoomImg(bit2, 80, 80));

                    /*if (iscal == 0) {
                        bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_openmap_mark); // 非推算结果
                    } else {
                        bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_openmap_focuse_mark); // 推算结果
                    }*/

                // 构建MarkerOption，用于在地图上添加Marker
                OverlayOptions option1 = new MarkerOptions().position(point1).icon(bitmap1);
                //                OverlayOptions option2 = new MarkerOptions().position(point2).icon(bitmap2);
                // 在地图上添加Marker，并显示
                mBaiduMap.addOverlay(option1);
                //                mBaiduMap.addOverlay(option2);
                mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(point1));
                //                mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(point2));
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    };

    /**
     * 处理图片
     *
     * @param bm 所要转换的bitmap
     * @return 指定宽高的bitmap
     */
    public static Bitmap zoomImg(Bitmap bm, int newWidth, int newHeight) {
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newbm;
    }

    private void location() {
        mLocationClient = new LocationClient(this);

        //通过LocationClientOption设置LocationClient相关参数
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);

        //设置locationClientOption
        mLocationClient.setLocOption(option);

        //注册LocationListener监听器
        MyLocationListener myLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(myLocationListener);
        //开启地图定位图层
        mLocationClient.start();
    }

    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //mapView 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(location.getDirection()).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
        }
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mLocationClient.stop();
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
    }

    /**
     * 封装定位结果和时间的实体类
     *
     * @author baidu
     */
    class LocationEntity {
        BDLocation location;
        long time;
    }
}
