package com.commoncb.seck.commoncbapp.DB;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.commoncb.seck.commoncbapp.activity.BaseActivity;
import com.commoncb.seck.commoncbapp.activity.MainActivity;
import com.commoncb.seck.commoncbapp.activity.MenuActivity;
import com.commoncb.seck.commoncbapp.modle1.CntXH;
import com.commoncb.seck.commoncbapp.modle1.New_Meter;
import com.commoncb.seck.commoncbapp.modle1.New_UserMsg;
import com.commoncb.seck.commoncbapp.utils.HzyUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.commoncb.seck.commoncbapp.activity.NewCbActivity.LOCATION;
import static com.commoncb.seck.commoncbapp.activity.NewCbActivity.bitmap;
import static com.commoncb.seck.commoncbapp.activity.NewCbActivity.mNew_meters;

/**
 * Created by ssHss on 2018/1/24.
 */

/**
 * DB文件路径
 */
public class DbHelper {
    public static String dbName = "Seck_4Db.db";
    public static SQLiteDatabase db;

    public static SQLiteDatabase getdb() {
        try {
            String fileName = getSDPath() + "/" + dbName;
            File x = new File(fileName.toString());
            if (x.exists()) {
                Log.d("limbo", "数据库文件存在");
            } else {
                Log.d("limbo", "数据库文件不存在,请确认是否授予程序权限。");
            }
            db = SQLiteDatabase.openOrCreateDatabase(fileName, null);
            db.disableWriteAheadLogging();
        } catch (Exception e) {
            Log.d("limbo", e.toString());
        }
        return db;
    }


    /**
     * 根据线号获取所有小区
     */
    public static List<String> getAllXqNum(String XH) {
        try {
            ArrayList<String> xqList = new ArrayList<>();
            Cursor c = db.rawQuery("SELECT XqNum  FROM   Meter where XH=? and XqNum is not null and XqNum != ''", new String[]{XH});
            //        Cursor c = db.rawQuery("SELECT XqNum  FROM   Meter where XH=" + XH, null);
            while (c.moveToNext()) {
                String xq = c.getString(c.getColumnIndex("XqNum")).trim();
                xqList.add(xq);
                //                Log.d("limbo", xq);
            }
            c.close();
            return xqList;
        } catch (Exception e) {
            Log.e("limbo", e.toString());
            return null;
        }
    }

    /**
     * 根据线号和小区号获取所有采集机
     */
    public static List<String> getAllCjjNum(String XH, String XqNum) {
        try {
            //        Log.d("limbo", XH + "-" + XqNum);
            ArrayList<String> cjjList = new ArrayList<>();
            Cursor c = db.rawQuery("SELECT CjjNum  FROM   Meter where XH=? and XqNum=? and CjjNum is not null and CjjNum != ''", new String[]{XH, XqNum});

            while (c.moveToNext()) {
                String cjj = c.getString(c.getColumnIndex("CjjNum")).trim();
                cjjList.add(cjj);
            }
            c.close();
            return cjjList;
        } catch (Exception e) {
            Log.e("limbo", e.toString());
            return null;
        }
    }

    /**
     * 根据唯一号更新表信息
     */
    public static void IrDAMeterSave(New_Meter meter, String time) {
        try {
            db.beginTransaction();
            try {
                ContentValues cv = new ContentValues();//图片以byte数组字符串形式放入数据库
                cv.put("IrDAData", meter.getIrDAData());
                cv.put("ReadData", meter.getReadData());
                cv.put("UploadTime", time);
                cv.put("CbState", "0");
                cv.put("MeterState", meter.getMeterState());
                cv.put("Data", meter.getData());
                if (meter.getLon().equals("") || meter.getLat().equals("")) {
                    Log.d("limbo", "抄表模式-给表设置坐标");
                    cv.put("Lon", MenuActivity.lon);
                    cv.put("Lat", MenuActivity.lat);
                }
                cv.put("PLon", MenuActivity.lon);
                cv.put("PLat", MenuActivity.lat);
                cv.put("CBFlag", "已抄");
                cv.put("UploadFlag", "未上传");
                String[] args = {String.valueOf(meter.getKH())};//将需要更新的数据放入
                db.update("Meter", cv, "KH = ?", args);//更新数据表中特定数据
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        } catch (Exception e) {
            Log.d("limbo", e.toString());
        }
    }

    /**
     * 根据小区号，采集机号，表号保存相应红外抄表信息
     */
    public static void newIrDAChangeUser(String xh, String XqNum, String CjjNum, String MeterNum, New_Meter meter, String time) {
        try {
            db.beginTransaction();
            try {
                ContentValues cv = new ContentValues();//图片以byte数组字符串形式放入数据库
                cv.put("IrDAData", meter.getIrDAData());
                cv.put("ReadData", meter.getReadData());
                cv.put("UploadTime", time);
                cv.put("CbState", "0");
                cv.put("MeterState", meter.getMeterState());
                cv.put("Data", meter.getData());
                if (meter.getLon().equals("") || meter.getLat().equals("")) {
                    Log.d("limbo", "抄表模式-给表设置坐标");
                    cv.put("Lon", MenuActivity.lon);
                    cv.put("Lat", MenuActivity.lat);
                }
                cv.put("PLon", MenuActivity.lon);
                cv.put("PLat", MenuActivity.lat);
                cv.put("CBFlag", "已抄");
                cv.put("UploadFlag", "未上传");
                String[] args = {String.valueOf(xh), String.valueOf(XqNum), String.valueOf(CjjNum), String.valueOf(MeterNum)};//将需要更新的数据放入
                db.update("Meter", cv, "XH = ? and XqNum=? and CjjNum=? and MeterNum=? ", args);//更新数据表中特定数据
                Log.d("limbo", "updata  xh:" + xh + "xq:" + XqNum + "cjj:" + CjjNum + "meter:" + MeterNum);

                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        } catch (Exception e) {
            Log.d("limbo", e.toString());
        }
    }

    /**
     * 添加线号和水表信息
     */
    public static void addXHandMeter(String xh, List<New_Meter> meters) {
        try {
            db.beginTransaction();
            ContentValues cv = new ContentValues();
            /*Log.d(BaseActivity.TAG, MainActivity.muser.getDatas().get(0).getUserName().trim()
                    + MainActivity.muser.getDatas().get(0).getUserID().trim()
                    + xh);*/
            cv.put("UserName", HzyUtils.isNull(MainActivity.muser.getDatas().get(0).getUserName()).trim());
            cv.put("UserID", HzyUtils.isNull(MainActivity.muser.getDatas().get(0).getUserID()).trim());
            cv.put("XH", xh);
            db.insert("XH", null, cv);//添加采集机表数据
            for (New_Meter meter : meters) {
                ContentValues cv1 = new ContentValues();
                cv1.put("XH", meter.getXH());
                cv1.put("KH", meter.getKH());
                cv1.put("HH", meter.getHH());
                cv1.put("JBH", meter.getJBH());
                if (HzyUtils.isEmpty(meter.getUserName())){
                    cv1.put("UserName", "");
                }else {
                    cv1.put("UserName", meter.getUserName().trim());
                }
                if (HzyUtils.isEmpty(meter.getUserPhone1())){
                    cv1.put("UserPhone1", "");
                }else {
                    cv1.put("UserPhone1", meter.getUserPhone1().trim());
                }
                if (HzyUtils.isEmpty(meter.getUserPhone2())){
                    cv1.put("UserPhone2", "");
                }else {
                    cv1.put("UserPhone2", meter.getUserPhone2().trim());
                }
                if (HzyUtils.isEmpty(meter.getMeterNumber())){
                    cv1.put("MeterNumber", "");
                }else {
                    cv1.put("MeterNumber", meter.getMeterNumber().trim());
                }
                if (HzyUtils.isEmpty(meter.getCaliber())){
                    cv1.put("Caliber", "");
                }else {
                    cv1.put("Caliber", meter.getCaliber().trim());
                }
                if (HzyUtils.isEmpty(meter.getXqNum())){
                    cv1.put("XqNum", "");
                }else {
                    cv1.put("XqNum", meter.getXqNum().trim());
                }
                if (HzyUtils.isEmpty(meter.getCjjNum())){
                    cv1.put("CjjNum", "");
                }else {
                    cv1.put("CjjNum", meter.getCjjNum().trim());
                }
                if (HzyUtils.isEmpty(meter.getMeterNum())){
                    cv1.put("MeterNum", "");
                }else {
                    cv1.put("MeterNum", meter.getMeterNum().trim());
                }
                if (HzyUtils.isEmpty(meter.getLastUserWater())){
                    cv1.put("LastUserWater", "");
                }else {
                    cv1.put("LastUserWater", meter.getLastUserWater().trim());
                }
                if (HzyUtils.isEmpty(meter.getLast2MouthUserWater())){
                    cv1.put("Last2MouthUserWater", "");
                }else {
                    cv1.put("Last2MouthUserWater", meter.getLast2MouthUserWater().trim());
                }
                if (HzyUtils.isEmpty(meter.getLast3MouthUserWater())){
                    cv1.put("Last3MouthUserWater", "");
                }else {
                    cv1.put("Last3MouthUserWater", meter.getLast3MouthUserWater().trim());
                }
                if (HzyUtils.isEmpty(meter.getAddr())){
                    cv1.put("Addr", "");
                }else {
                    cv1.put("Addr", meter.getAddr().trim());
                }
                if (HzyUtils.isEmpty(meter.getUserState())){
                    cv1.put("UserState", "");
                }else {
                    cv1.put("UserState", meter.getUserState().trim());
                }
                if (HzyUtils.isEmpty(meter.getData())){
                    cv1.put("Data", "");
                }else {
                    cv1.put("Data", meter.getData().trim());
                }
                if (HzyUtils.isEmpty(meter.getReadData())){
                    cv1.put("ReadData", "");
                }else {
                    cv1.put("ReadData", meter.getReadData().trim());
                }
                if (HzyUtils.isEmpty(meter.getLastData())){
                    cv1.put("LastData", "");
                }else {
                    cv1.put("LastData", meter.getLastData().trim());
                }
                if (HzyUtils.isEmpty(meter.getLastReadData())){
                    cv1.put("LastReadData", "0");
                }else {
                    cv1.put("LastReadData", meter.getLastReadData().trim());
                }
                if (HzyUtils.isEmpty(meter.getNote())){
                    cv1.put("Note", "");
                }else {
                    cv1.put("Note", meter.getNote().trim());
                }
                if (HzyUtils.isEmpty(meter.getIrDAData())){
                    cv1.put("IrDAData", "");
                }else {
                    cv1.put("IrDAData", meter.getIrDAData().trim());
                }
                if (HzyUtils.isEmpty(meter.getLat())){
                    cv1.put("Lat", "");
                }else {
                    cv1.put("Lat", meter.getLat().trim());
                }
                if (HzyUtils.isEmpty(meter.getLon())){
                    cv1.put("Lon", "");
                }else {
                    cv1.put("Lon", meter.getLon().trim());
                }

                cv1.put("BtFlag", "0");
                cv1.put("MeterState", "0");
                cv1.put("CbState", "0");
                cv1.put("UploadTime", "");
                /*if (meter.getPic1() != null) {
                    Bitmap pic = meter.getPic1();
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    pic.compress(Bitmap.CompressFormat.JPEG, 100, bos);//无损耗压缩图片为输出流
                    byte[] picBytes = bos.toByteArray();
                    cv1.put("Pic1", picBytes);
                }*/
                db.insert("Meter", null, cv1);//添加采集机表数据
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(BaseActivity.TAG, BaseActivity.getCurrentActivity() + "--addXHandMeter:" + e.toString());
        } finally {
            db.endTransaction();
        }
    }


    /**
     * 获取所有线号
     */
    public static ArrayList<New_UserMsg> getAllXH(String UserID) {
        try {
            ArrayList<New_UserMsg> new_userMsgs = new ArrayList<>();
            Cursor c = db.rawQuery("SELECT  *  FROM   XH where UserID=?", new String[]{UserID});
            while (c.moveToNext()) {
                New_UserMsg new_userMsg = new New_UserMsg();
                new_userMsg.setXH(c.getString(c.getColumnIndex("XH")));
                new_userMsg.setUserName(c.getString(c.getColumnIndex("UserName")));
                new_userMsg.setUserID(c.getString(c.getColumnIndex("UserID")));
                new_userMsgs.add(new_userMsg);
            }
            return new_userMsgs;
        } catch (Exception e) {
            Log.e("limbo", e.toString());
            return null;
        }
    }

    /**
     * 统计所有线号
     */
    public static ArrayList<String> tjAllXH(String UserID) {
        try {
            ArrayList<String> new_userMsgs = new ArrayList<>();
            Cursor c = db.rawQuery("SELECT  *  FROM  XH where UserID=?", new String[]{UserID});
            /**
             * cntXHs = data + dataYC
             */
            //            List<CntXH> pic = getPicCount();//图片总数--未上传
            //            List<CntXH> picYC = getPicCountYC();//图片总数--已上传
            //        List<CntXH> dataYC = getDataCountYC();//数据总数--已上传
            //            int picCount = 0;
            //            int picCountYC = 0;
            while (c.moveToNext()) {
                New_UserMsg new_userMsg = new New_UserMsg();
                new_userMsg.setXH(c.getString(c.getColumnIndex("XH")));
                new_userMsg.setUserName(c.getString(c.getColumnIndex("UserName")));
                new_userMsg.setUserID(c.getString(c.getColumnIndex("UserID")));

                int dataCount = getDataCount1(new_userMsg.getXH());//未上传表数量
                int dataCountYC = getDataCountYC1(new_userMsg.getXH());


                String msg = "线号:" + new_userMsg.getXH()
                        + "\n   总表数:" + tjMeterAll(new_userMsg.getXH())
                        + "\n   已抄表:" + tjMeterByYc(new_userMsg.getXH())
                        + "\n   未抄表:" + tjMeterByWc(new_userMsg.getXH())
                        + "\n   可疑表:" + tjMeterByKy(new_userMsg.getXH())
                        + "\n   已复核表:" + tjMeterByFh(new_userMsg.getXH())
                        + "\n   已上传表数:" + dataCountYC
                        //                            + "\n   已上传图片数:"+picYC.get(i).getCnt()
                        + "\n   未上传表数:" + dataCount
                        + "\n   已拍照表数:" + DbHelper.getPic1CountAll(new_userMsg.getXH())
                        //                            + "\n   未上传图片数:"+pic.get(i).getCnt()
                        ;

                new_userMsgs.add(msg);
            }
            return new_userMsgs;
        } catch (Exception e) {
            Log.e("limbo", e.toString());
            return null;
        }
    }

    /**
     * 获取所有表数量
     */
    public static int tjMeterAll(String xh) {
        try {
            int i = 0;
            Cursor c = db.rawQuery("SELECT count(*) as cnt  FROM   Meter where XH=?", new String[]{xh});
            while (c.moveToNext()) {
                i = c.getInt(c.getColumnIndex("cnt"));
            }
            return i;
        } catch (Exception e) {
            Log.e("limbo", e.toString());
            return 0;
        }
    }

    /**
     * 获取已抄表数量
     */
    public static int tjMeterByYc(String xh) {
        try {
            int i = 0;
            Cursor c = db.rawQuery("SELECT count(*) as cnt  FROM   Meter where XH=? and CBFlag = '已抄'", new String[]{xh});
            while (c.moveToNext()) {
                i = c.getInt(c.getColumnIndex("cnt"));
            }
            return i;
        } catch (Exception e) {
            Log.e("limbo", e.toString());
            return 0;
        }
    }

    /**
     * 获取未抄表数量
     */
    public static int tjMeterByWc(String xh) {

        try {
            int i = 0;
            Cursor c = db.rawQuery("SELECT count(*) as cnt  FROM   Meter where XH=? and (CBFlag is null or CBFlag != '已抄')", new String[]{xh});
            while (c.moveToNext()) {
                i = c.getInt(c.getColumnIndex("cnt"));
            }
            return i;
        } catch (Exception e) {
            Log.e("limbo", e.toString());
            return 0;
        }
    }

    /**
     * 获取可疑表数量
     */
    public static int tjMeterByKy(String xh) {
        try {
            int i = 0;
            Cursor c = db.rawQuery("SELECT count(*) as cnt  FROM   Meter where XH=? and CBFlag = '已抄' and (CbState != '0' or MeterState != '0') and BtFlag != '1'", new String[]{xh});
            while (c.moveToNext()) {
                i = c.getInt(c.getColumnIndex("cnt"));
            }
            return i;
        } catch (Exception e) {
            Log.e("limbo", e.toString());
            return 0;
        }
    }

    /**
     * 获取已复核表数量
     */
    public static int tjMeterByFh(String xh) {
        try {
            int i = 0;
            Cursor c = db.rawQuery("SELECT count(*) as cnt  FROM   Meter where XH=? and CBFlag = '已抄' and (BtFlag = '1')", new String[]{xh});
            while (c.moveToNext()) {
                i = c.getInt(c.getColumnIndex("cnt"));
            }
            return i;
        } catch (Exception e) {
            Log.e("limbo", e.toString());
            return 0;
        }
    }

    /**
     * 根据线号获取对应线号所有表
     */
    public static ArrayList<New_Meter> getMeterByXH(String xh) {
        try {
            ArrayList<New_Meter> meters = new ArrayList<>();
            Cursor c = db.rawQuery("SELECT  *  FROM   Meter where XH=?", new String[]{xh});
            while (c.moveToNext()) {
                New_Meter new_meter = new New_Meter();
                new_meter.setXH(c.getString(c.getColumnIndex("XH")));
                new_meter.setKH(c.getString(c.getColumnIndex("KH")));
                new_meter.setHH(c.getString(c.getColumnIndex("HH")));
                new_meter.setJBH(c.getString(c.getColumnIndex("JBH")));
                new_meter.setUserName(c.getString(c.getColumnIndex("UserName")));
                new_meter.setUserPhone1(c.getString(c.getColumnIndex("UserPhone1")));
                new_meter.setUserPhone2(c.getString(c.getColumnIndex("UserPhone2")));
                new_meter.setMeterNumber(c.getString(c.getColumnIndex("MeterNumber")));
                new_meter.setCaliber(c.getString(c.getColumnIndex("Caliber")));
                new_meter.setXqNum(c.getString(c.getColumnIndex("XqNum")).trim());
                new_meter.setCjjNum(c.getString(c.getColumnIndex("CjjNum")).trim());
                new_meter.setMeterNum(c.getString(c.getColumnIndex("MeterNum")).trim());
                new_meter.setLastUserWater(c.getString(c.getColumnIndex("LastUserWater")));
                new_meter.setLast2MouthUserWater(c.getString(c.getColumnIndex("Last2MouthUserWater")));
                new_meter.setLast3MouthUserWater(c.getString(c.getColumnIndex("Last3MouthUserWater")));
                new_meter.setAddr(c.getString(c.getColumnIndex("Addr")));
                new_meter.setBtFlag(c.getString(c.getColumnIndex("BtFlag")));
                new_meter.setBjFlag(c.getInt(c.getColumnIndex("BjFlag")));
                new_meter.setCbFlag(c.getString(c.getColumnIndex("CBFlag")));
                new_meter.setReadData(c.getString(c.getColumnIndex("ReadData")));//本月读数
                new_meter.setData(c.getString(c.getColumnIndex("Data")));//本月实用
                new_meter.setNote(c.getString(c.getColumnIndex("Note")));//现场备注
                new_meter.setMeterState(c.getString(c.getColumnIndex("MeterState")));
                new_meter.setCbState(c.getString(c.getColumnIndex("CbState")));
                new_meter.setUserState(c.getString(c.getColumnIndex("UserState")));
                new_meter.setLastData(c.getString(c.getColumnIndex("LastData")));
                new_meter.setLastReadData(c.getString(c.getColumnIndex("LastReadData")));
                new_meter.setIrDAData(c.getString(c.getColumnIndex("IrDAData")));
                new_meter.setLon(c.getString(c.getColumnIndex("Lon")));
                new_meter.setLat(c.getString(c.getColumnIndex("Lat")));
                new_meter.setPLon(c.getString(c.getColumnIndex("PLon")));
                new_meter.setPLat(c.getString(c.getColumnIndex("PLat")));
                new_meter.setUploadTime(c.getString(c.getColumnIndex("UploadTime")));
                new_meter.setUploadFlag(c.getString(c.getColumnIndex("UploadFlag")));
                meters.add(new_meter);
            }
            return meters;

        } catch (Exception e) {
            Log.e("limbo", e.toString());
            return null;
        }
    }

    /**
     * 根据线号获取对应线号所有  已抄表~
     */
    public static ArrayList<New_Meter> getYcMeterByXH(String xh) {
        try {
            ArrayList<New_Meter> meters = new ArrayList<>();
            Cursor c = db.rawQuery("SELECT  *  FROM   Meter where XH=? and CBFlag ='已抄'", new String[]{xh});
            while (c.moveToNext()) {
                New_Meter new_meter = new New_Meter();
                new_meter.setXH(c.getString(c.getColumnIndex("XH")));
                new_meter.setKH(c.getString(c.getColumnIndex("KH")));
                new_meter.setHH(c.getString(c.getColumnIndex("HH")));
                new_meter.setJBH(c.getString(c.getColumnIndex("JBH")));
                new_meter.setUserName(c.getString(c.getColumnIndex("UserName")));
                new_meter.setUserPhone1(c.getString(c.getColumnIndex("UserPhone1")));
                new_meter.setUserPhone2(c.getString(c.getColumnIndex("UserPhone2")));
                new_meter.setMeterNumber(c.getString(c.getColumnIndex("MeterNumber")));
                new_meter.setCaliber(c.getString(c.getColumnIndex("Caliber")));
                new_meter.setXqNum(c.getString(c.getColumnIndex("XqNum")).trim());
                new_meter.setCjjNum(c.getString(c.getColumnIndex("CjjNum")).trim());
                new_meter.setMeterNum(c.getString(c.getColumnIndex("MeterNum")).trim());
                new_meter.setLastUserWater(c.getString(c.getColumnIndex("LastUserWater")));
                new_meter.setLast2MouthUserWater(c.getString(c.getColumnIndex("Last2MouthUserWater")));
                new_meter.setLast3MouthUserWater(c.getString(c.getColumnIndex("Last3MouthUserWater")));
                new_meter.setAddr(c.getString(c.getColumnIndex("Addr")));
                new_meter.setBtFlag(c.getString(c.getColumnIndex("BtFlag")));
                new_meter.setBjFlag(c.getInt(c.getColumnIndex("BjFlag")));
                new_meter.setMeterState(c.getString(c.getColumnIndex("MeterState")));
                new_meter.setCbState(c.getString(c.getColumnIndex("CbState")));
                new_meter.setUserState(c.getString(c.getColumnIndex("UserState")));
                new_meter.setData(c.getString(c.getColumnIndex("Data")));
                new_meter.setReadData(c.getString(c.getColumnIndex("ReadData")));
                new_meter.setLastData(c.getString(c.getColumnIndex("LastData")));
                new_meter.setLastReadData(c.getString(c.getColumnIndex("LastReadData")));
                new_meter.setIrDAData(c.getString(c.getColumnIndex("IrDAData")));
                new_meter.setLon(c.getString(c.getColumnIndex("Lon")));
                new_meter.setLat(c.getString(c.getColumnIndex("Lat")));
                new_meter.setPLon(c.getString(c.getColumnIndex("PLon")));
                new_meter.setPLat(c.getString(c.getColumnIndex("PLat")));
                new_meter.setUploadTime(c.getString(c.getColumnIndex("UploadTime")));
                new_meter.setUploadFlag(c.getString(c.getColumnIndex("UploadFlag")));
                new_meter.setCbFlag(c.getString(c.getColumnIndex("CBFlag")));
                new_meter.setNote(c.getString(c.getColumnIndex("Note")));//现场备注
                meters.add(new_meter);

            }
            return meters;
        } catch (Exception e) {
            Log.e("limbo", e.toString());
            return null;
        }
    }

    /**
     * 根据线号获取对应线号所有  已抄表~
     */
    public static ArrayList<New_Meter> getWscMeterByXH(String xh) {
        try {
            ArrayList<New_Meter> meters = new ArrayList<>();
            Cursor c = db.rawQuery("SELECT  *  FROM   Meter where XH=? and CBFlag ='已抄' and (UploadFlag is null or UploadFlag != '已上传')", new String[]{xh});
            while (c.moveToNext()) {
                New_Meter new_meter = new New_Meter();
                new_meter.setXH(c.getString(c.getColumnIndex("XH")));
                new_meter.setKH(c.getString(c.getColumnIndex("KH")));
                new_meter.setHH(c.getString(c.getColumnIndex("HH")));
                new_meter.setJBH(c.getString(c.getColumnIndex("JBH")));
                new_meter.setUserName(c.getString(c.getColumnIndex("UserName")));
                new_meter.setUserPhone1(c.getString(c.getColumnIndex("UserPhone1")));
                new_meter.setUserPhone2(c.getString(c.getColumnIndex("UserPhone2")));
                new_meter.setMeterNumber(c.getString(c.getColumnIndex("MeterNumber")));
                new_meter.setCaliber(c.getString(c.getColumnIndex("Caliber")));
                new_meter.setXqNum(c.getString(c.getColumnIndex("XqNum")).trim());
                new_meter.setCjjNum(c.getString(c.getColumnIndex("CjjNum")).trim());
                new_meter.setMeterNum(c.getString(c.getColumnIndex("MeterNum")).trim());
                new_meter.setLastUserWater(c.getString(c.getColumnIndex("LastUserWater")));
                new_meter.setLast2MouthUserWater(c.getString(c.getColumnIndex("Last2MouthUserWater")));
                new_meter.setLast3MouthUserWater(c.getString(c.getColumnIndex("Last3MouthUserWater")));
                new_meter.setAddr(c.getString(c.getColumnIndex("Addr")));
                new_meter.setBtFlag(c.getString(c.getColumnIndex("BtFlag")));
                new_meter.setBjFlag(c.getInt(c.getColumnIndex("BjFlag")));
                new_meter.setMeterState(c.getString(c.getColumnIndex("MeterState")));
                new_meter.setCbState(c.getString(c.getColumnIndex("CbState")));
                new_meter.setUserState(c.getString(c.getColumnIndex("UserState")));
                new_meter.setData(c.getString(c.getColumnIndex("Data")));
                new_meter.setReadData(c.getString(c.getColumnIndex("ReadData")));
                new_meter.setLastData(c.getString(c.getColumnIndex("LastData")));
                new_meter.setLastReadData(c.getString(c.getColumnIndex("LastReadData")));
                new_meter.setIrDAData(c.getString(c.getColumnIndex("IrDAData")));
                new_meter.setLon(c.getString(c.getColumnIndex("Lon")));
                new_meter.setLat(c.getString(c.getColumnIndex("Lat")));
                new_meter.setPLon(c.getString(c.getColumnIndex("PLon")));
                new_meter.setPLat(c.getString(c.getColumnIndex("PLat")));
                new_meter.setUploadTime(c.getString(c.getColumnIndex("UploadTime")));
                new_meter.setUploadFlag(c.getString(c.getColumnIndex("UploadFlag")));
                new_meter.setCbFlag(c.getString(c.getColumnIndex("CBFlag")));
                new_meter.setNote(c.getString(c.getColumnIndex("Note")));//现场备注
                meters.add(new_meter);

            }
            return meters;
        } catch (Exception e) {
            Log.e("limbo", e.toString());
            return null;
        }
    }

    /**
     * 根据线号获取对应线号所有  已标记表~
     */
    public static ArrayList<New_Meter> getWYbjMeterByXH(String xh) {
        try {
            ArrayList<New_Meter> meters = new ArrayList<>();
            Cursor c = db.rawQuery("SELECT  *  FROM   Meter where XH=? and BjFlag = 1", new String[]{xh});
            while (c.moveToNext()) {
                New_Meter new_meter = new New_Meter();
                new_meter.setXH(c.getString(c.getColumnIndex("XH")));
                new_meter.setKH(c.getString(c.getColumnIndex("KH")));
                new_meter.setHH(c.getString(c.getColumnIndex("HH")));
                new_meter.setJBH(c.getString(c.getColumnIndex("JBH")));
                new_meter.setUserName(c.getString(c.getColumnIndex("UserName")));
                new_meter.setUserPhone1(c.getString(c.getColumnIndex("UserPhone1")));
                new_meter.setUserPhone2(c.getString(c.getColumnIndex("UserPhone2")));
                new_meter.setMeterNumber(c.getString(c.getColumnIndex("MeterNumber")));
                new_meter.setCaliber(c.getString(c.getColumnIndex("Caliber")));
                new_meter.setXqNum(c.getString(c.getColumnIndex("XqNum")).trim());
                new_meter.setCjjNum(c.getString(c.getColumnIndex("CjjNum")).trim());
                new_meter.setMeterNum(c.getString(c.getColumnIndex("MeterNum")).trim());
                new_meter.setLastUserWater(c.getString(c.getColumnIndex("LastUserWater")));
                new_meter.setLast2MouthUserWater(c.getString(c.getColumnIndex("Last2MouthUserWater")));
                new_meter.setLast3MouthUserWater(c.getString(c.getColumnIndex("Last3MouthUserWater")));
                new_meter.setAddr(c.getString(c.getColumnIndex("Addr")));
                new_meter.setBtFlag(c.getString(c.getColumnIndex("BtFlag")));
                new_meter.setBjFlag(c.getInt(c.getColumnIndex("BjFlag")));
                new_meter.setMeterState(c.getString(c.getColumnIndex("MeterState")));
                new_meter.setCbState(c.getString(c.getColumnIndex("CbState")));
                new_meter.setUserState(c.getString(c.getColumnIndex("UserState")));
                new_meter.setData(c.getString(c.getColumnIndex("Data")));
                new_meter.setReadData(c.getString(c.getColumnIndex("ReadData")));
                new_meter.setLastData(c.getString(c.getColumnIndex("LastData")));
                new_meter.setLastReadData(c.getString(c.getColumnIndex("LastReadData")));
                new_meter.setIrDAData(c.getString(c.getColumnIndex("IrDAData")));
                new_meter.setLon(c.getString(c.getColumnIndex("Lon")));
                new_meter.setLat(c.getString(c.getColumnIndex("Lat")));
                new_meter.setPLon(c.getString(c.getColumnIndex("PLon")));
                new_meter.setPLat(c.getString(c.getColumnIndex("PLat")));
                new_meter.setUploadTime(c.getString(c.getColumnIndex("UploadTime")));
                new_meter.setUploadFlag(c.getString(c.getColumnIndex("UploadFlag")));
                new_meter.setCbFlag(c.getString(c.getColumnIndex("CBFlag")));
                new_meter.setNote(c.getString(c.getColumnIndex("Note")));//现场备注
                meters.add(new_meter);

            }
            return meters;
        } catch (Exception e) {
            Log.e("limbo", e.toString());
            return null;
        }
    }

    /**
     * 根据线号获取对应线号所有  已备注表~
     */
    public static ArrayList<New_Meter> getBZMeterByXH(String xh) {
        try {
            ArrayList<New_Meter> meters = new ArrayList<>();
            Cursor c = db.rawQuery("SELECT  *  FROM   Meter where XH=? and (Note is not null and Note != '')", new String[]{xh});
            while (c.moveToNext()) {
                New_Meter new_meter = new New_Meter();
                new_meter.setXH(c.getString(c.getColumnIndex("XH")));
                new_meter.setKH(c.getString(c.getColumnIndex("KH")));
                new_meter.setHH(c.getString(c.getColumnIndex("HH")));
                new_meter.setJBH(c.getString(c.getColumnIndex("JBH")));
                new_meter.setUserName(c.getString(c.getColumnIndex("UserName")));
                new_meter.setUserPhone1(c.getString(c.getColumnIndex("UserPhone1")));
                new_meter.setUserPhone2(c.getString(c.getColumnIndex("UserPhone2")));
                new_meter.setMeterNumber(c.getString(c.getColumnIndex("MeterNumber")));
                new_meter.setCaliber(c.getString(c.getColumnIndex("Caliber")));
                new_meter.setXqNum(c.getString(c.getColumnIndex("XqNum")).trim());
                new_meter.setCjjNum(c.getString(c.getColumnIndex("CjjNum")).trim());
                new_meter.setMeterNum(c.getString(c.getColumnIndex("MeterNum")).trim());
                new_meter.setLastUserWater(c.getString(c.getColumnIndex("LastUserWater")));
                new_meter.setLast2MouthUserWater(c.getString(c.getColumnIndex("Last2MouthUserWater")));
                new_meter.setLast3MouthUserWater(c.getString(c.getColumnIndex("Last3MouthUserWater")));
                new_meter.setAddr(c.getString(c.getColumnIndex("Addr")));
                new_meter.setBtFlag(c.getString(c.getColumnIndex("BtFlag")));
                new_meter.setBjFlag(c.getInt(c.getColumnIndex("BjFlag")));
                new_meter.setMeterState(c.getString(c.getColumnIndex("MeterState")));
                new_meter.setCbState(c.getString(c.getColumnIndex("CbState")));
                new_meter.setUserState(c.getString(c.getColumnIndex("UserState")));
                new_meter.setData(c.getString(c.getColumnIndex("Data")));
                new_meter.setReadData(c.getString(c.getColumnIndex("ReadData")));
                new_meter.setLastData(c.getString(c.getColumnIndex("LastData")));
                new_meter.setLastReadData(c.getString(c.getColumnIndex("LastReadData")));
                new_meter.setIrDAData(c.getString(c.getColumnIndex("IrDAData")));
                new_meter.setLon(c.getString(c.getColumnIndex("Lon")));
                new_meter.setLat(c.getString(c.getColumnIndex("Lat")));
                new_meter.setPLon(c.getString(c.getColumnIndex("PLon")));
                new_meter.setPLat(c.getString(c.getColumnIndex("PLat")));
                new_meter.setUploadTime(c.getString(c.getColumnIndex("UploadTime")));
                new_meter.setUploadFlag(c.getString(c.getColumnIndex("UploadFlag")));
                new_meter.setCbFlag(c.getString(c.getColumnIndex("CBFlag")));
                new_meter.setNote(c.getString(c.getColumnIndex("Note")));//现场备注
                meters.add(new_meter);
            }
            return meters;
        } catch (Exception e) {
            Log.e("limbo", e.toString());
            return null;
        }
    }

    /**
     * 根据线号获取对应线号所有  未抄表~
     */
    public static ArrayList<New_Meter> getWcMeterByXH(String xh) {
        try {
            ArrayList<New_Meter> meters = new ArrayList<>();
            Cursor c = db.rawQuery("SELECT  *  FROM   Meter where XH=? and  (CBFlag is null or CBFlag != '已抄')", new String[]{xh});
            while (c.moveToNext()) {
                New_Meter new_meter = new New_Meter();
                new_meter.setXH(c.getString(c.getColumnIndex("XH")));
                new_meter.setKH(c.getString(c.getColumnIndex("KH")));
                new_meter.setHH(c.getString(c.getColumnIndex("HH")));
                new_meter.setJBH(c.getString(c.getColumnIndex("JBH")));
                new_meter.setUserName(c.getString(c.getColumnIndex("UserName")));
                new_meter.setUserPhone1(c.getString(c.getColumnIndex("UserPhone1")));
                new_meter.setUserPhone2(c.getString(c.getColumnIndex("UserPhone2")));
                new_meter.setMeterNumber(c.getString(c.getColumnIndex("MeterNumber")));
                new_meter.setCaliber(c.getString(c.getColumnIndex("Caliber")));
                new_meter.setXqNum(c.getString(c.getColumnIndex("XqNum")).trim());
                new_meter.setCjjNum(c.getString(c.getColumnIndex("CjjNum")).trim());
                new_meter.setMeterNum(c.getString(c.getColumnIndex("MeterNum")).trim());
                new_meter.setLastUserWater(c.getString(c.getColumnIndex("LastUserWater")));
                new_meter.setLast2MouthUserWater(c.getString(c.getColumnIndex("Last2MouthUserWater")));
                new_meter.setLast3MouthUserWater(c.getString(c.getColumnIndex("Last3MouthUserWater")));
                new_meter.setAddr(c.getString(c.getColumnIndex("Addr")));
                new_meter.setBtFlag(c.getString(c.getColumnIndex("BtFlag")));
                new_meter.setBjFlag(c.getInt(c.getColumnIndex("BjFlag")));
                new_meter.setMeterState(c.getString(c.getColumnIndex("MeterState")));
                new_meter.setCbState(c.getString(c.getColumnIndex("CbState")));
                new_meter.setUserState(c.getString(c.getColumnIndex("UserState")));
                new_meter.setData(c.getString(c.getColumnIndex("Data")));
                new_meter.setReadData(c.getString(c.getColumnIndex("ReadData")));
                new_meter.setLastData(c.getString(c.getColumnIndex("LastData")));
                new_meter.setLastReadData(c.getString(c.getColumnIndex("LastReadData")));
                new_meter.setIrDAData(c.getString(c.getColumnIndex("IrDAData")));
                new_meter.setLon(c.getString(c.getColumnIndex("Lon")));
                new_meter.setLat(c.getString(c.getColumnIndex("Lat")));
                new_meter.setPLon(c.getString(c.getColumnIndex("PLon")));
                new_meter.setPLat(c.getString(c.getColumnIndex("PLat")));
                new_meter.setUploadTime(c.getString(c.getColumnIndex("UploadTime")));
                new_meter.setUploadFlag(c.getString(c.getColumnIndex("UploadFlag")));
                new_meter.setCbFlag(c.getString(c.getColumnIndex("CBFlag")));
                new_meter.setNote(c.getString(c.getColumnIndex("Note")));//现场备注
                meters.add(new_meter);

            }
            return meters;
        } catch (Exception e) {
            Log.e("limbo", e.toString());
            return null;
        }

    }

    /**
     * 根据线号获取对应线号所有  可疑表~
     */
    public static ArrayList<New_Meter> getKyMeterByXH(String xh) {
        try {
            ArrayList<New_Meter> meters = new ArrayList<>();
            Cursor c = db.rawQuery("SELECT  *  FROM   Meter where XH=? and( CbState != '0' or MeterState != '0' )and CBFlag = '已抄' and BtFlag != '1'", new String[]{xh});
            while (c.moveToNext()) {
                New_Meter new_meter = new New_Meter();
                new_meter.setXH(c.getString(c.getColumnIndex("XH")));
                new_meter.setKH(c.getString(c.getColumnIndex("KH")));
                new_meter.setHH(c.getString(c.getColumnIndex("HH")));
                new_meter.setJBH(c.getString(c.getColumnIndex("JBH")));
                new_meter.setUserName(c.getString(c.getColumnIndex("UserName")));
                new_meter.setUserPhone1(c.getString(c.getColumnIndex("UserPhone1")));
                new_meter.setUserPhone2(c.getString(c.getColumnIndex("UserPhone2")));
                new_meter.setMeterNumber(c.getString(c.getColumnIndex("MeterNumber")));
                new_meter.setCaliber(c.getString(c.getColumnIndex("Caliber")));
                new_meter.setXqNum(c.getString(c.getColumnIndex("XqNum")).trim());
                new_meter.setCjjNum(c.getString(c.getColumnIndex("CjjNum")).trim());
                new_meter.setMeterNum(c.getString(c.getColumnIndex("MeterNum")).trim());
                new_meter.setLastUserWater(c.getString(c.getColumnIndex("LastUserWater")));
                new_meter.setLast2MouthUserWater(c.getString(c.getColumnIndex("Last2MouthUserWater")));
                new_meter.setLast3MouthUserWater(c.getString(c.getColumnIndex("Last3MouthUserWater")));
                new_meter.setAddr(c.getString(c.getColumnIndex("Addr")));
                new_meter.setBtFlag(c.getString(c.getColumnIndex("BtFlag")));
                new_meter.setBjFlag(c.getInt(c.getColumnIndex("BjFlag")));
                new_meter.setMeterState(c.getString(c.getColumnIndex("MeterState")));
                new_meter.setCbState(c.getString(c.getColumnIndex("CbState")));
                new_meter.setUserState(c.getString(c.getColumnIndex("UserState")));
                new_meter.setData(c.getString(c.getColumnIndex("Data")));
                new_meter.setReadData(c.getString(c.getColumnIndex("ReadData")));
                new_meter.setLastData(c.getString(c.getColumnIndex("LastData")));
                new_meter.setLastReadData(c.getString(c.getColumnIndex("LastReadData")));
                new_meter.setIrDAData(c.getString(c.getColumnIndex("IrDAData")));
                new_meter.setLon(c.getString(c.getColumnIndex("Lon")));
                new_meter.setLat(c.getString(c.getColumnIndex("Lat")));
                new_meter.setPLon(c.getString(c.getColumnIndex("PLon")));
                new_meter.setPLat(c.getString(c.getColumnIndex("PLat")));
                new_meter.setUploadTime(c.getString(c.getColumnIndex("UploadTime")));
                new_meter.setUploadFlag(c.getString(c.getColumnIndex("UploadFlag")));
                new_meter.setCbFlag(c.getString(c.getColumnIndex("CBFlag")));
                new_meter.setNote(c.getString(c.getColumnIndex("Note")));//现场备注
                meters.add(new_meter);

            }
            return meters;
        } catch (Exception e) {
            Log.e("limbo", e.toString());
            return null;
        }
    }

    /**
     * 根据线号获取对应线号所有  偏大表~
     */
    public static ArrayList<New_Meter> getPDMeterByXH(String xh) {
        try {
            ArrayList<New_Meter> meters = new ArrayList<>();
            Cursor c = db.rawQuery("SELECT  *  FROM   Meter where XH=? and( CbState == '0' and MeterState == '1' )and CBFlag = '已抄'", new String[]{xh});
            while (c.moveToNext()) {
                New_Meter new_meter = new New_Meter();
                new_meter.setXH(c.getString(c.getColumnIndex("XH")));
                new_meter.setKH(c.getString(c.getColumnIndex("KH")));
                new_meter.setHH(c.getString(c.getColumnIndex("HH")));
                new_meter.setJBH(c.getString(c.getColumnIndex("JBH")));
                new_meter.setUserName(c.getString(c.getColumnIndex("UserName")));
                new_meter.setUserPhone1(c.getString(c.getColumnIndex("UserPhone1")));
                new_meter.setUserPhone2(c.getString(c.getColumnIndex("UserPhone2")));
                new_meter.setMeterNumber(c.getString(c.getColumnIndex("MeterNumber")));
                new_meter.setCaliber(c.getString(c.getColumnIndex("Caliber")));
                new_meter.setXqNum(c.getString(c.getColumnIndex("XqNum")).trim());
                new_meter.setCjjNum(c.getString(c.getColumnIndex("CjjNum")).trim());
                new_meter.setMeterNum(c.getString(c.getColumnIndex("MeterNum")).trim());
                new_meter.setLastUserWater(c.getString(c.getColumnIndex("LastUserWater")));
                new_meter.setLast2MouthUserWater(c.getString(c.getColumnIndex("Last2MouthUserWater")));
                new_meter.setLast3MouthUserWater(c.getString(c.getColumnIndex("Last3MouthUserWater")));
                new_meter.setAddr(c.getString(c.getColumnIndex("Addr")));
                new_meter.setBtFlag(c.getString(c.getColumnIndex("BtFlag")));
                new_meter.setBjFlag(c.getInt(c.getColumnIndex("BjFlag")));
                new_meter.setMeterState(c.getString(c.getColumnIndex("MeterState")));
                new_meter.setCbState(c.getString(c.getColumnIndex("CbState")));
                new_meter.setUserState(c.getString(c.getColumnIndex("UserState")));
                new_meter.setData(c.getString(c.getColumnIndex("Data")));
                new_meter.setReadData(c.getString(c.getColumnIndex("ReadData")));
                new_meter.setLastData(c.getString(c.getColumnIndex("LastData")));
                new_meter.setLastReadData(c.getString(c.getColumnIndex("LastReadData")));
                new_meter.setIrDAData(c.getString(c.getColumnIndex("IrDAData")));
                new_meter.setLon(c.getString(c.getColumnIndex("Lon")));
                new_meter.setLat(c.getString(c.getColumnIndex("Lat")));
                new_meter.setPLon(c.getString(c.getColumnIndex("PLon")));
                new_meter.setPLat(c.getString(c.getColumnIndex("PLat")));
                new_meter.setUploadTime(c.getString(c.getColumnIndex("UploadTime")));
                new_meter.setUploadFlag(c.getString(c.getColumnIndex("UploadFlag")));
                new_meter.setCbFlag(c.getString(c.getColumnIndex("CBFlag")));
                new_meter.setNote(c.getString(c.getColumnIndex("Note")));//现场备注
                meters.add(new_meter);

            }
            return meters;
        } catch (Exception e) {
            Log.e("limbo", e.toString());
            return null;
        }
    }

    /**
     * 根据线号获取对应线号所有  偏小表~
     */
    public static ArrayList<New_Meter> getPXMeterByXH(String xh) {
        try {
            ArrayList<New_Meter> meters = new ArrayList<>();
            Cursor c = db.rawQuery("SELECT  *  FROM   Meter where XH=? and( CbState == '0' and MeterState == '2' )and CBFlag = '已抄'", new String[]{xh});
            while (c.moveToNext()) {
                New_Meter new_meter = new New_Meter();
                new_meter.setXH(c.getString(c.getColumnIndex("XH")));
                new_meter.setKH(c.getString(c.getColumnIndex("KH")));
                new_meter.setHH(c.getString(c.getColumnIndex("HH")));
                new_meter.setJBH(c.getString(c.getColumnIndex("JBH")));
                new_meter.setUserName(c.getString(c.getColumnIndex("UserName")));
                new_meter.setUserPhone1(c.getString(c.getColumnIndex("UserPhone1")));
                new_meter.setUserPhone2(c.getString(c.getColumnIndex("UserPhone2")));
                new_meter.setMeterNumber(c.getString(c.getColumnIndex("MeterNumber")));
                new_meter.setCaliber(c.getString(c.getColumnIndex("Caliber")));
                new_meter.setXqNum(c.getString(c.getColumnIndex("XqNum")).trim());
                new_meter.setCjjNum(c.getString(c.getColumnIndex("CjjNum")).trim());
                new_meter.setMeterNum(c.getString(c.getColumnIndex("MeterNum")).trim());
                new_meter.setLastUserWater(c.getString(c.getColumnIndex("LastUserWater")));
                new_meter.setLast2MouthUserWater(c.getString(c.getColumnIndex("Last2MouthUserWater")));
                new_meter.setLast3MouthUserWater(c.getString(c.getColumnIndex("Last3MouthUserWater")));
                new_meter.setAddr(c.getString(c.getColumnIndex("Addr")));
                new_meter.setBtFlag(c.getString(c.getColumnIndex("BtFlag")));
                new_meter.setBjFlag(c.getInt(c.getColumnIndex("BjFlag")));
                new_meter.setMeterState(c.getString(c.getColumnIndex("MeterState")));
                new_meter.setCbState(c.getString(c.getColumnIndex("CbState")));
                new_meter.setUserState(c.getString(c.getColumnIndex("UserState")));
                new_meter.setData(c.getString(c.getColumnIndex("Data")));
                new_meter.setReadData(c.getString(c.getColumnIndex("ReadData")));
                new_meter.setLastData(c.getString(c.getColumnIndex("LastData")));
                new_meter.setLastReadData(c.getString(c.getColumnIndex("LastReadData")));
                new_meter.setIrDAData(c.getString(c.getColumnIndex("IrDAData")));
                new_meter.setLon(c.getString(c.getColumnIndex("Lon")));
                new_meter.setLat(c.getString(c.getColumnIndex("Lat")));
                new_meter.setPLon(c.getString(c.getColumnIndex("PLon")));
                new_meter.setPLat(c.getString(c.getColumnIndex("PLat")));
                new_meter.setUploadTime(c.getString(c.getColumnIndex("UploadTime")));
                new_meter.setUploadFlag(c.getString(c.getColumnIndex("UploadFlag")));
                new_meter.setCbFlag(c.getString(c.getColumnIndex("CBFlag")));
                new_meter.setNote(c.getString(c.getColumnIndex("Note")));//现场备注
                meters.add(new_meter);

            }
            return meters;
        } catch (Exception e) {
            Log.e("limbo", e.toString());
            return null;
        }
    }

    /**
     * 根据线号获取对应线号所有  偏小表~负用水
     */
    public static ArrayList<New_Meter> getPXFYSMeterByXH(String xh) {
        try {
            ArrayList<New_Meter> meters = new ArrayList<>();
            Cursor c = db.rawQuery("SELECT  *  FROM   Meter where XH=? and( CbState == '0' and MeterState == '2' )and CBFlag = '已抄'", new String[]{xh});
            while (c.moveToNext()) {
                New_Meter new_meter = new New_Meter();
                new_meter.setXH(c.getString(c.getColumnIndex("XH")));
                new_meter.setKH(c.getString(c.getColumnIndex("KH")));
                new_meter.setHH(c.getString(c.getColumnIndex("HH")));
                new_meter.setJBH(c.getString(c.getColumnIndex("JBH")));
                new_meter.setUserName(c.getString(c.getColumnIndex("UserName")));
                new_meter.setUserPhone1(c.getString(c.getColumnIndex("UserPhone1")));
                new_meter.setUserPhone2(c.getString(c.getColumnIndex("UserPhone2")));
                new_meter.setMeterNumber(c.getString(c.getColumnIndex("MeterNumber")));
                new_meter.setCaliber(c.getString(c.getColumnIndex("Caliber")));
                new_meter.setXqNum(c.getString(c.getColumnIndex("XqNum")).trim());
                new_meter.setCjjNum(c.getString(c.getColumnIndex("CjjNum")).trim());
                new_meter.setMeterNum(c.getString(c.getColumnIndex("MeterNum")).trim());
                new_meter.setLastUserWater(c.getString(c.getColumnIndex("LastUserWater")));
                new_meter.setLast2MouthUserWater(c.getString(c.getColumnIndex("Last2MouthUserWater")));
                new_meter.setLast3MouthUserWater(c.getString(c.getColumnIndex("Last3MouthUserWater")));
                new_meter.setAddr(c.getString(c.getColumnIndex("Addr")));
                new_meter.setBtFlag(c.getString(c.getColumnIndex("BtFlag")));
                new_meter.setBjFlag(c.getInt(c.getColumnIndex("BjFlag")));
                new_meter.setMeterState(c.getString(c.getColumnIndex("MeterState")));
                new_meter.setCbState(c.getString(c.getColumnIndex("CbState")));
                new_meter.setUserState(c.getString(c.getColumnIndex("UserState")));
                new_meter.setData(c.getString(c.getColumnIndex("Data")));
                new_meter.setReadData(c.getString(c.getColumnIndex("ReadData")));
                new_meter.setLastData(c.getString(c.getColumnIndex("LastData")));
                new_meter.setLastReadData(c.getString(c.getColumnIndex("LastReadData")));
                new_meter.setIrDAData(c.getString(c.getColumnIndex("IrDAData")));
                new_meter.setLon(c.getString(c.getColumnIndex("Lon")));
                new_meter.setLat(c.getString(c.getColumnIndex("Lat")));
                new_meter.setPLon(c.getString(c.getColumnIndex("PLon")));
                new_meter.setPLat(c.getString(c.getColumnIndex("PLat")));
                new_meter.setUploadTime(c.getString(c.getColumnIndex("UploadTime")));
                new_meter.setUploadFlag(c.getString(c.getColumnIndex("UploadFlag")));
                new_meter.setCbFlag(c.getString(c.getColumnIndex("CBFlag")));
                new_meter.setNote(c.getString(c.getColumnIndex("Note")));//现场备注
                if (new_meter.getData().contains("-")) {
                    meters.add(new_meter);
                }
            }
            return meters;
        } catch (Exception e) {
            Log.e("limbo", e.toString());
            return null;
        }
    }

    /**
     * 根据线号获取对应线号所有  未拍照表~
     */
    public static ArrayList<New_Meter> getWPZMeterByXH(String xh) {
        try {
            ArrayList<New_Meter> meters = new ArrayList<>();
            Cursor c = db.rawQuery("SELECT  *  FROM   Meter where XH=? and (Pic1 is null and Pic2 is null and Pic3 is null) and CBFlag = '已抄'", new String[]{xh});
            while (c.moveToNext()) {
                New_Meter new_meter = new New_Meter();
                new_meter.setXH(c.getString(c.getColumnIndex("XH")));
                new_meter.setKH(c.getString(c.getColumnIndex("KH")));
                new_meter.setHH(c.getString(c.getColumnIndex("HH")));
                new_meter.setJBH(c.getString(c.getColumnIndex("JBH")));
                new_meter.setUserName(c.getString(c.getColumnIndex("UserName")));
                new_meter.setUserPhone1(c.getString(c.getColumnIndex("UserPhone1")));
                new_meter.setUserPhone2(c.getString(c.getColumnIndex("UserPhone2")));
                new_meter.setMeterNumber(c.getString(c.getColumnIndex("MeterNumber")));
                new_meter.setCaliber(c.getString(c.getColumnIndex("Caliber")));
                new_meter.setXqNum(c.getString(c.getColumnIndex("XqNum")).trim());
                new_meter.setCjjNum(c.getString(c.getColumnIndex("CjjNum")).trim());
                new_meter.setMeterNum(c.getString(c.getColumnIndex("MeterNum")).trim());
                new_meter.setLastUserWater(c.getString(c.getColumnIndex("LastUserWater")));
                new_meter.setLast2MouthUserWater(c.getString(c.getColumnIndex("Last2MouthUserWater")));
                new_meter.setLast3MouthUserWater(c.getString(c.getColumnIndex("Last3MouthUserWater")));
                new_meter.setAddr(c.getString(c.getColumnIndex("Addr")));
                new_meter.setBtFlag(c.getString(c.getColumnIndex("BtFlag")));
                new_meter.setBjFlag(c.getInt(c.getColumnIndex("BjFlag")));
                new_meter.setMeterState(c.getString(c.getColumnIndex("MeterState")));
                new_meter.setCbState(c.getString(c.getColumnIndex("CbState")));
                new_meter.setUserState(c.getString(c.getColumnIndex("UserState")));
                new_meter.setData(c.getString(c.getColumnIndex("Data")));
                new_meter.setReadData(c.getString(c.getColumnIndex("ReadData")));
                new_meter.setLastData(c.getString(c.getColumnIndex("LastData")));
                new_meter.setLastReadData(c.getString(c.getColumnIndex("LastReadData")));
                new_meter.setIrDAData(c.getString(c.getColumnIndex("IrDAData")));
                new_meter.setLon(c.getString(c.getColumnIndex("Lon")));
                new_meter.setLat(c.getString(c.getColumnIndex("Lat")));
                new_meter.setPLon(c.getString(c.getColumnIndex("PLon")));
                new_meter.setPLat(c.getString(c.getColumnIndex("PLat")));
                new_meter.setUploadTime(c.getString(c.getColumnIndex("UploadTime")));
                new_meter.setUploadFlag(c.getString(c.getColumnIndex("UploadFlag")));
                new_meter.setCbFlag(c.getString(c.getColumnIndex("CBFlag")));
                new_meter.setNote(c.getString(c.getColumnIndex("Note")));//现场备注
                meters.add(new_meter);

            }
            return meters;
        } catch (Exception e) {
            Log.e("limbo", e.toString());
            return null;
        }
    }

    /**
     * 根据线号获取对应线号所有  复核表~
     */
    public static ArrayList<New_Meter> getFhMeterByXH(String xh) {
        try {
            ArrayList<New_Meter> meters = new ArrayList<>();
            Cursor c = db.rawQuery("SELECT  *  FROM   Meter where XH=? and BtFlag = '1'", new String[]{xh});
            while (c.moveToNext()) {
                New_Meter new_meter = new New_Meter();
                new_meter.setXH(c.getString(c.getColumnIndex("XH")));
                new_meter.setKH(c.getString(c.getColumnIndex("KH")));
                new_meter.setHH(c.getString(c.getColumnIndex("HH")));
                new_meter.setJBH(c.getString(c.getColumnIndex("JBH")));
                new_meter.setUserName(c.getString(c.getColumnIndex("UserName")));
                new_meter.setUserPhone1(c.getString(c.getColumnIndex("UserPhone1")));
                new_meter.setUserPhone2(c.getString(c.getColumnIndex("UserPhone2")));
                new_meter.setMeterNumber(c.getString(c.getColumnIndex("MeterNumber")));
                new_meter.setCaliber(c.getString(c.getColumnIndex("Caliber")));
                new_meter.setXqNum(c.getString(c.getColumnIndex("XqNum")).trim());
                new_meter.setCjjNum(c.getString(c.getColumnIndex("CjjNum")).trim());
                new_meter.setMeterNum(c.getString(c.getColumnIndex("MeterNum")).trim());
                new_meter.setLastUserWater(c.getString(c.getColumnIndex("LastUserWater")));
                new_meter.setLast2MouthUserWater(c.getString(c.getColumnIndex("Last2MouthUserWater")));
                new_meter.setLast3MouthUserWater(c.getString(c.getColumnIndex("Last3MouthUserWater")));
                new_meter.setAddr(c.getString(c.getColumnIndex("Addr")));
                new_meter.setBtFlag(c.getString(c.getColumnIndex("BtFlag")));
                new_meter.setBjFlag(c.getInt(c.getColumnIndex("BjFlag")));
                new_meter.setMeterState(c.getString(c.getColumnIndex("MeterState")));
                new_meter.setCbState(c.getString(c.getColumnIndex("CbState")));
                new_meter.setUserState(c.getString(c.getColumnIndex("UserState")));
                new_meter.setData(c.getString(c.getColumnIndex("Data")));
                new_meter.setReadData(c.getString(c.getColumnIndex("ReadData")));
                new_meter.setLastData(c.getString(c.getColumnIndex("LastData")));
                new_meter.setLastReadData(c.getString(c.getColumnIndex("LastReadData")));
                new_meter.setIrDAData(c.getString(c.getColumnIndex("IrDAData")));
                new_meter.setLon(c.getString(c.getColumnIndex("Lon")));
                new_meter.setLat(c.getString(c.getColumnIndex("Lat")));
                new_meter.setPLon(c.getString(c.getColumnIndex("PLon")));
                new_meter.setPLat(c.getString(c.getColumnIndex("PLat")));
                new_meter.setUploadTime(c.getString(c.getColumnIndex("UploadTime")));
                new_meter.setUploadFlag(c.getString(c.getColumnIndex("UploadFlag")));
                new_meter.setCbFlag(c.getString(c.getColumnIndex("CBFlag")));
                new_meter.setNote(c.getString(c.getColumnIndex("Note")));//现场备注
                meters.add(new_meter);
            }
            return meters;
        } catch (Exception e) {
            Log.e("limbo", e.toString());
            return null;
        }
    }

    /**
     * 图片总数--未上传
     */
    public static int getPic1Count(String xh) {
        try {
            int i = 0;
            Cursor c = db.rawQuery("SELECT  count(*) as cnt FROM   Meter where XH=? and (Pic1 is not null or Pic2 is not null or Pic3 is not null) and CBFlag = '已抄' and (UploadFlag is null or UploadFlag != '已上传')", new String[]{xh});

            while (c.moveToNext()) {
                i = c.getInt(c.getColumnIndex("cnt"));
            }
            Log.d("limbo", "图片总数--未上传:" + i);
            return i;
        } catch (Exception e) {
            Log.e("limbo", e.toString());
            return 0;
        }
    }

    /**
     * 图片总数--已上传
     */
    public static int getPic1CountYC(String xh) {
        try {
            int i = 0;
            Cursor c = db.rawQuery("SELECT  count(*) as cnt FROM   Meter where XH=? and (Pic1 is not null or Pic2 is not null or Pic3 is not null) and CBFlag = '已抄' and UploadFlag = '已上传'", new String[]{xh});

            while (c.moveToNext()) {
                i = c.getInt(c.getColumnIndex("cnt"));
            }

            Log.d("limbo", "图片总数--已上传:" + i);
            return i;
        } catch (Exception e) {
            Log.e("limbo", e.toString());
            return 0;
        }
    }

    /**
     * 含有图片总表数
     */
    public static int getPic1CountAll(String xh) {
        try {
            int i = 0;
            Cursor c = db.rawQuery("SELECT  count(*) as cnt FROM   Meter where XH=? and (Pic1 is not null or Pic2 is not null or Pic3 is not null) and CBFlag = '已抄' ", new String[]{xh});

            while (c.moveToNext()) {
                i = c.getInt(c.getColumnIndex("cnt"));
            }

            Log.d("limbo", "图片总数--已上传:" + i);
            return i;
        } catch (Exception e) {
            Log.e("limbo", e.toString());
            return 0;
        }
    }

    /**
     * 无图片数据总数--未上传
     */
    public static int getDataCount(String xh) {
        try {
            int i = 0;
            Cursor c = db.rawQuery("SELECT  count(*) as cnt FROM   Meter where XH=? and (Pic1 is null and Pic2 is null and Pic3 is null) and CBFlag = '已抄' and (UploadFlag is null or UploadFlag != '已上传')", new String[]{xh});

            while (c.moveToNext()) {
                i = c.getInt(c.getColumnIndex("cnt"));
            }

            Log.d("limbo", "无图片数据总数--未上传:" + i);
            return i;
        } catch (Exception e) {
            Log.e("limbo", e.toString());
            return 0;
        }
    }

    /**
     * 无图片数据总数-已上传
     */
    public static int getDataCountYC(String xh) {
        try {
            int i = 0;
            Cursor c = db.rawQuery("SELECT  count(*) as cnt FROM   Meter where XH=? and (Pic1 is null and Pic2 is null and Pic3 is null) and CBFlag = '已抄' and UploadFlag = '已上传'", new String[]{xh});

            while (c.moveToNext()) {
                i = c.getInt(c.getColumnIndex("cnt"));
            }

            Log.d("limbo", "无图片数据总数-已上传:" + i);
            return i;
        } catch (Exception e) {
            Log.e("limbo", e.toString());
            return 0;
        }
    }

    /**
     * 图片总数--未上传
     */
    public static List<CntXH> getPicCount() {
        try {
            //        Cursor c = db.rawQuery("SELECT  count(*) as cnt FROM   Meter where XH=? and (Pic1 is not null or Pic2 is not null or Pic3 is not null) and CBFlag = '已抄' and UploadFlag != '已上传'", new String[]{xh});
            //        Cursor c = db.rawQuery("SELECT  count(*) as cnt ,XH FROM   Meter where  (Pic1 is not null or Pic2 is not null or Pic3 is not null) and CBFlag = '已抄' and (UploadFlag is null or UploadFlag != '已上传') group by  XH ", null);
            Cursor c = db.rawQuery("select XH, Count(1) cnt, sum(ysc) YCCnt from(select x.XH,m.KH,case when ((Pic1 is not null or Pic2 is not null or Pic3 is not null) and CBFlag = '已抄' and (UploadFlag is null or UploadFlag != '已上传')) then 1 else 0 end ysc from XH x left join Meter m on x.xh = m.xh) t group by t.XH", null);

            List<CntXH> cntXHs = new ArrayList<>();
            while (c.moveToNext()) {
                CntXH cntXH = new CntXH();
                cntXH.setCnt(c.getInt(c.getColumnIndex("cnt")));
                cntXH.setXH(c.getString(c.getColumnIndex("XH")));
                cntXHs.add(cntXH);
                Log.d("limbo", "图片总数--未上传:" + cntXH.getCnt());
            }
            return cntXHs;
        } catch (Exception e) {
            Log.e("limbo", e.toString());
            return null;
        }
    }

    /**
     * 图片总数--已上传
     */
    public static List<CntXH> getPicCountYC() {
        try {
            //        Cursor c = db.rawQuery("SELECT  count(*) as cnt FROM   Meter where XH=? and (Pic1 is not null or Pic2 is not null or Pic3 is not null) and CBFlag = '已抄' and UploadFlag = '已上传'", new String[]{xh});
            //        Cursor c = db.rawQuery("SELECT  count(*) as cnt ,XH FROM   Meter where  (Pic1 is not null or Pic2 is not null or Pic3 is not null) and CBFlag = '已抄' and UploadFlag = '已上传' group by  XH ", null);
            Cursor c = db.rawQuery("select XH, Count(1) cnt, sum(ysc) YCCnt" +
                    " from " +
                    "(" +
                    "select x.XH,m.KH,case when ((Pic1 is not null or Pic2 is not null or Pic3 is not null) and CBFlag = '已抄' and UploadFlag = '已上传') then 1 else 0 end ysc " +
                    "from XH x left join Meter m on x.xh = m.xh" +
                    ") t group by t.XH", null);
            List<CntXH> cntXHs = new ArrayList<>();
            while (c.moveToNext()) {
                CntXH cntXH = new CntXH();
                cntXH.setCnt(c.getInt(c.getColumnIndex("cnt")));
                cntXH.setXH(c.getString(c.getColumnIndex("XH")));
                cntXHs.add(cntXH);
                Log.d("limbo", "图片总数--已上传:" + cntXH.getCnt());
            }
            return cntXHs;
        } catch (Exception e) {
            Log.e("limbo", e.toString());
            return null;
        }
    }

    /**
     * 已抄表数据总数
     */
    public static List<CntXH> getAllDataCount() {
        try {
            //        Cursor c = db.rawQuery("SELECT  count(*) as cnt FROM   Meter where XH=? and (Pic1 is null and Pic2 is null and Pic3 is null) and CBFlag = '已抄' and UploadFlag != '已上传'", new String[]{xh});
            Cursor c = db.rawQuery("SELECT  count(*) as cnt ,XH FROM   Meter where  CBFlag = '已抄'  group by  XH ", null);
            List<CntXH> cntXHs = new ArrayList<>();
            while (c.moveToNext()) {
                CntXH cntXH = new CntXH();
                cntXH.setCnt(c.getInt(c.getColumnIndex("cnt")));
                cntXH.setXH(c.getString(c.getColumnIndex("XH")));
                cntXHs.add(cntXH);
                Log.d("limbo", "已抄表数据总数:" + cntXH.getCnt());
            }
            return cntXHs;
        } catch (Exception e) {
            Log.e("limbo", e.toString());
            return null;
        }
    }

    /**
     * 无图片数据总数--未上传
     */
    public static int getDataCount1(String xh) {
        try {
            int i = 0;
            Cursor c = db.rawQuery("SELECT  count(*) as cnt FROM   Meter where XH =? and CBFlag = '已抄' and (UploadFlag is null or UploadFlag != '已上传')", new String[]{xh});
            //        Cursor c = db.rawQuery("SELECT  count(*) as cnt ,XH FROM   Meter where XH =? and CBFlag = '已抄' and (UploadFlag is null or UploadFlag != '已上传') group by  XH ", new String[]{xh});
            //        Cursor c = db.rawQuery("select XH, Count(1) cnt, sum(ysc) YCCnt from (select x.XH,m.KH,case when((Pic1 is null and Pic2 is null and Pic3 is null) and CBFlag = '已抄' and (UploadFlag is null or UploadFlag != '已上传')) then 1 else 0 end ysc from XH x left join Meter m on x.xh = m.xh) t group by t.XH", null);
            while (c.moveToNext()) {
                i = c.getInt(c.getColumnIndex("cnt"));
            }
            Log.d("limbo", "\n\n\n-------线号:" + xh + "--------\n数据总数--未上传:" + i);
            return i;
        } catch (Exception e) {
            Log.e("limbo", e.toString());
            return 0;

        }
    }

    /**
     * 无图片数据总数-已上传
     */
    public static int getDataCountYC1(String xh) {
        try {
            int i = 0;
            Cursor c = db.rawQuery("SELECT  count(*) as cnt FROM   Meter where XH =? and CBFlag = '已抄' and  UploadFlag = '已上传'", new String[]{xh});
            //        Cursor c = db.rawQuery("SELECT  count(*) as cnt ,XH FROM   Meter where XH =? and CBFlag = '已抄' and  UploadFlag = '已上传' group by  XH ", new String[]{xh});
            //        Cursor c = db.rawQuery("select XH, Count(1) cnt, sum(ysc) YCCnt from (select x.XH,m.KH,case when ((Pic1 is null and Pic2 is null and Pic3 is null) and CBFlag = '已抄' and UploadFlag = '已上传') then 1 else 0 end ysc from XH x left join Meter m on x.xh = m.xh) t group by t.XH", null);
            while (c.moveToNext()) {
                i = c.getInt(c.getColumnIndex("cnt"));
            }
            Log.d("limbo", "数据总数--已上传:" + i);
            return i;
        } catch (Exception e) {
            Log.e("limbo", e.toString());
            return 0;
        }

    }

    /**
     * 根据线号获取对应线号所有  已抄表
     * flag -true为强制上传，无视上传标志位
     */
    public static ArrayList<New_Meter> getAllMeterByXH(String xh, boolean flag) {
        try {
            ArrayList<New_Meter> meters = new ArrayList<>();
            Cursor c;
            if (flag) {
                c = db.rawQuery("SELECT  *  FROM  Meter where XH=? and CBFlag = '已抄' ", new String[]{xh});
            } else {
                c = db.rawQuery("SELECT  *  FROM  Meter where XH=? and CBFlag = '已抄'and UploadFlag != '已上传' ", new String[]{xh});
            }
            while (c.moveToNext()) {
                New_Meter new_meter = new New_Meter();
                new_meter.setXH(c.getString(c.getColumnIndex("XH")));
                new_meter.setKH(c.getString(c.getColumnIndex("KH")));
                new_meter.setHH(c.getString(c.getColumnIndex("HH")));
                new_meter.setJBH(c.getString(c.getColumnIndex("JBH")));
                new_meter.setUserName(c.getString(c.getColumnIndex("UserName")));
                new_meter.setUserPhone1(c.getString(c.getColumnIndex("UserPhone1")));
                new_meter.setUserPhone2(c.getString(c.getColumnIndex("UserPhone2")));
                new_meter.setMeterNumber(c.getString(c.getColumnIndex("MeterNumber")));
                new_meter.setCaliber(c.getString(c.getColumnIndex("Caliber")));
                new_meter.setXqNum(c.getString(c.getColumnIndex("XqNum")));
                new_meter.setCjjNum(c.getString(c.getColumnIndex("CjjNum")));
                new_meter.setMeterNum(c.getString(c.getColumnIndex("MeterNum")));
                new_meter.setLastUserWater(c.getString(c.getColumnIndex("LastUserWater")));
                new_meter.setLast2MouthUserWater(c.getString(c.getColumnIndex("Last2MouthUserWater")));
                new_meter.setLast3MouthUserWater(c.getString(c.getColumnIndex("Last3MouthUserWater")));
                new_meter.setAddr(c.getString(c.getColumnIndex("Addr")));
                new_meter.setBtFlag(c.getString(c.getColumnIndex("BtFlag")));
                new_meter.setMeterState(c.getInt(c.getColumnIndex("MeterState")) + 1 + "");
                new_meter.setCbState(c.getInt(c.getColumnIndex("CbState")) + 1 + "");
                new_meter.setUserState(c.getString(c.getColumnIndex("UserState")));
                new_meter.setData(c.getString(c.getColumnIndex("Data")));
                new_meter.setReadData(c.getString(c.getColumnIndex("ReadData")));
                new_meter.setLastData(c.getString(c.getColumnIndex("LastData")));
                new_meter.setLastReadData(c.getString(c.getColumnIndex("LastReadData")));
                new_meter.setIrDAData(c.getString(c.getColumnIndex("IrDAData")));
                new_meter.setLon(c.getString(c.getColumnIndex("Lon")));
                new_meter.setLat(c.getString(c.getColumnIndex("Lat")));
                new_meter.setPLon(c.getString(c.getColumnIndex("PLon")));
                new_meter.setPLat(c.getString(c.getColumnIndex("PLat")));
                new_meter.setUploadTime(c.getString(c.getColumnIndex("UploadTime")));
                new_meter.setUploadFlag(c.getString(c.getColumnIndex("UploadFlag")));
                new_meter.setCbFlag(c.getString(c.getColumnIndex("CBFlag")));
                new_meter.setNote(c.getString(c.getColumnIndex("Note")));
                meters.add(new_meter);
            }
            return meters;
        } catch (Exception e) {
            Log.e("limbo", e.toString());
            return null;
        }

    }

    /**
     * 根据线号获取对应线号所有  已抄表~并且未上传,并且没有图
     * flag -true为强制上传，无视上传标志位
     */
    public static ArrayList<New_Meter> getYcWscMeterByXH(String xh, boolean flag) {
        try {
            ArrayList<New_Meter> meters = new ArrayList<>();
            Cursor c;
            if (flag) {
                c = db.rawQuery("SELECT  *  FROM  Meter where XH=? and (Pic1 is null and Pic2 is null and Pic3 is null) and CBFlag = '已抄'", new String[]{xh});
            } else {
                c = db.rawQuery("SELECT  *  FROM  Meter where XH=? and (Pic1 is null and Pic2 is null and Pic3 is null) and CBFlag = '已抄'and UploadFlag != '已上传'", new String[]{xh});
            }
            while (c.moveToNext()) {
                New_Meter new_meter = new New_Meter();
                new_meter.setXH(c.getString(c.getColumnIndex("XH")));
                new_meter.setKH(c.getString(c.getColumnIndex("KH")));
                new_meter.setHH(c.getString(c.getColumnIndex("HH")));
                new_meter.setJBH(c.getString(c.getColumnIndex("JBH")));
                new_meter.setUserName(c.getString(c.getColumnIndex("UserName")));
                new_meter.setUserPhone1(c.getString(c.getColumnIndex("UserPhone1")));
                new_meter.setUserPhone2(c.getString(c.getColumnIndex("UserPhone2")));
                new_meter.setMeterNumber(c.getString(c.getColumnIndex("MeterNumber")));
                new_meter.setCaliber(c.getString(c.getColumnIndex("Caliber")));
                new_meter.setXqNum(c.getString(c.getColumnIndex("XqNum")));
                new_meter.setCjjNum(c.getString(c.getColumnIndex("CjjNum")));
                new_meter.setMeterNum(c.getString(c.getColumnIndex("MeterNum")));
                new_meter.setLastUserWater(c.getString(c.getColumnIndex("LastUserWater")));
                new_meter.setLast2MouthUserWater(c.getString(c.getColumnIndex("Last2MouthUserWater")));
                new_meter.setLast3MouthUserWater(c.getString(c.getColumnIndex("Last3MouthUserWater")));
                new_meter.setAddr(c.getString(c.getColumnIndex("Addr")));
                new_meter.setBtFlag(c.getString(c.getColumnIndex("BtFlag")));
                new_meter.setMeterState(c.getInt(c.getColumnIndex("MeterState")) + 1 + "");
                new_meter.setCbState(c.getInt(c.getColumnIndex("CbState")) + 1 + "");
                new_meter.setUserState(c.getString(c.getColumnIndex("UserState")));
                new_meter.setData(c.getString(c.getColumnIndex("Data")));
                new_meter.setReadData(c.getString(c.getColumnIndex("ReadData")));
                new_meter.setLastData(c.getString(c.getColumnIndex("LastData")));
                new_meter.setLastReadData(c.getString(c.getColumnIndex("LastReadData")));
                new_meter.setIrDAData(c.getString(c.getColumnIndex("IrDAData")));
                new_meter.setLon(c.getString(c.getColumnIndex("Lon")));
                new_meter.setLat(c.getString(c.getColumnIndex("Lat")));
                new_meter.setPLon(c.getString(c.getColumnIndex("PLon")));
                new_meter.setPLat(c.getString(c.getColumnIndex("PLat")));
                new_meter.setUploadTime(c.getString(c.getColumnIndex("UploadTime")));
                new_meter.setUploadFlag(c.getString(c.getColumnIndex("UploadFlag")));
                new_meter.setCbFlag(c.getString(c.getColumnIndex("CBFlag")));
                new_meter.setNote(c.getString(c.getColumnIndex("Note")));
                meters.add(new_meter);
            }
            return meters;
        } catch (Exception e) {
            Log.e("limbo", e.toString());
            return null;
        }

    }

    /**
     * 将一只表设定为未抄表
     */
    public static void clearMeterData(String kh, String note) {
        try {
            db.beginTransaction();
            try {
                ContentValues cv = new ContentValues();//图片以byte数组字符串形式放入数据库
                cv.put("UploadTime", "");
                cv.put("ReadData", "");
                cv.put("Data", "");
                cv.put("CbState", "0");
                cv.put("MeterState", "0");
                cv.put("CBFlag", "未抄");
                cv.put("Note", note);
                String[] args = {String.valueOf(kh)};//将需要更新的数据放入
                db.update("Meter", cv, "KH=?", args);//更新数据表中特定数据
                db.setTransactionSuccessful();

            } finally {
                db.endTransaction();
            }
        } catch (Exception e) {
            Log.d("limbo", e.toString());
        }
    }

    /**
     * 根据线号获取对应线号所有  已抄表~并且未上传,并且有图
     * flag - true - 强制上传标志位
     */
    public static ArrayList<New_Meter> getYcWscMeterPicByXH(String xh, boolean flag) {
        try {
            ArrayList<New_Meter> meters = new ArrayList<>();
            Cursor c;
            if (flag) {
                c = db.rawQuery("SELECT  *  FROM   Meter where XH=? and CBFlag = '已抄'", new String[]{xh});
            } else {
                c = db.rawQuery("SELECT  *  FROM   Meter where XH=? and CBFlag = '已抄'and UploadFlag != '已上传'", new String[]{xh});
            }
            /*if (flag) {
                c = db.rawQuery("SELECT  *  FROM   Meter where XH=? and (Pic1 is not null or Pic2 is not null or Pic3 is not null) and CBFlag = '已抄'", new String[]{xh});
            } else {
                c = db.rawQuery("SELECT  *  FROM   Meter where XH=? and (Pic1 is not null or Pic2 is not null or Pic3 is not null) and CBFlag = '已抄'and UploadFlag != '已上传'", new String[]{xh});
            }*/
            while (c.moveToNext()) {
                New_Meter new_meter = new New_Meter();
                new_meter.setXH(c.getString(c.getColumnIndex("XH")));
                new_meter.setKH(c.getString(c.getColumnIndex("KH")));
                new_meter.setHH(c.getString(c.getColumnIndex("HH")));
                new_meter.setJBH(c.getString(c.getColumnIndex("JBH")));
                new_meter.setUserName(c.getString(c.getColumnIndex("UserName")));
                new_meter.setUserPhone1(c.getString(c.getColumnIndex("UserPhone1")));
                new_meter.setUserPhone2(c.getString(c.getColumnIndex("UserPhone2")));
                new_meter.setMeterNumber(c.getString(c.getColumnIndex("MeterNumber")));
                new_meter.setCaliber(c.getString(c.getColumnIndex("Caliber")));
                new_meter.setXqNum(c.getString(c.getColumnIndex("XqNum")));
                new_meter.setCjjNum(c.getString(c.getColumnIndex("CjjNum")));
                new_meter.setMeterNum(c.getString(c.getColumnIndex("MeterNum")));
                new_meter.setLastUserWater(c.getString(c.getColumnIndex("LastUserWater")));
                new_meter.setLast2MouthUserWater(c.getString(c.getColumnIndex("Last2MouthUserWater")));
                new_meter.setLast3MouthUserWater(c.getString(c.getColumnIndex("Last3MouthUserWater")));
                new_meter.setAddr(c.getString(c.getColumnIndex("Addr")));
                new_meter.setBtFlag(c.getString(c.getColumnIndex("BtFlag")));
                String meterstate = c.getString(c.getColumnIndex("MeterState"));
                if (meterstate.equals("")) {
                    new_meter.setMeterState("");
                } else {
                    new_meter.setMeterState(c.getInt(c.getColumnIndex("MeterState")) + 1 + "");
                }
                String cbstate = c.getString(c.getColumnIndex("CbState"));
                if (cbstate.equals("")) {
                    new_meter.setCbState("");
                } else {
                    new_meter.setCbState(c.getInt(c.getColumnIndex("CbState")) + 1 + "");
                }

                new_meter.setUserState(c.getString(c.getColumnIndex("UserState")));
                new_meter.setData(c.getString(c.getColumnIndex("Data")));
                new_meter.setReadData(c.getString(c.getColumnIndex("ReadData")));
                new_meter.setLastData(c.getString(c.getColumnIndex("LastData")));
                new_meter.setLastReadData(c.getString(c.getColumnIndex("LastReadData")));
                new_meter.setIrDAData(c.getString(c.getColumnIndex("IrDAData")));
                new_meter.setLon(c.getString(c.getColumnIndex("Lon")));
                new_meter.setLat(c.getString(c.getColumnIndex("Lat")));
                new_meter.setPLon(c.getString(c.getColumnIndex("PLon")));
                new_meter.setPLat(c.getString(c.getColumnIndex("PLat")));
                new_meter.setUploadTime(c.getString(c.getColumnIndex("UploadTime")));
                new_meter.setUploadFlag(c.getString(c.getColumnIndex("UploadFlag")));
                new_meter.setCbFlag(c.getString(c.getColumnIndex("CBFlag")));
                new_meter.setNote(c.getString(c.getColumnIndex("Note")));
                byte[] picBytes = c.getBlob(c.getColumnIndex("Pic1"));
                if (picBytes != null) {
                    new_meter.setPic1(BitmapFactory.decodeByteArray(picBytes, 0, picBytes.length));
                }
                picBytes = c.getBlob(c.getColumnIndex("Pic2"));
                if (picBytes != null) {
                    new_meter.setPic2(BitmapFactory.decodeByteArray(picBytes, 0, picBytes.length));
                }
                picBytes = c.getBlob(c.getColumnIndex("Pic3"));
                if (picBytes != null) {
                    new_meter.setPic1(BitmapFactory.decodeByteArray(picBytes, 0, picBytes.length));
                }
                meters.add(new_meter);
            }
            return meters;
        } catch (Exception e) {
            Log.e("limbo", "getYcWscMeterPicByXH error:" + e.toString());
            return null;
        }

    }


    /**
     * 根据KH号获取图片
     */
    public static byte[] getPicByKH(String kh, int x_Flag) {
        try {
            Cursor c = db.rawQuery("SELECT  *  FROM   Meter where KH=?", new String[]{kh});
            while (c.moveToNext()) {
                byte[] picBytes = null;
                switch (x_Flag) {
                    case 0:
                        picBytes = c.getBlob(c.getColumnIndex("Pic1"));
                        break;
                    case 1:
                        picBytes = c.getBlob(c.getColumnIndex("Pic2"));
                        break;
                    case 2:
                        picBytes = c.getBlob(c.getColumnIndex("Pic3"));
                        break;
                    default:
                        break;
                }
                if (picBytes == null) {
                    return null;
                } else {
                    Log.d("limbo", "上传图片大小：" + (picBytes.length / 1024) + "K");
                    //                return BitmapFactory.decodeByteArray(picBytes, 0, picBytes.length);
                    return picBytes;
                }

            }
            return null;
        } catch (Exception e) {
            Log.e("limbo", e.toString());
            return null;
        }

    }

    /**
     * 根据KH号获取图片
     */
    public static Bitmap getPicByKH1(String kh, int xFlag) {
        try {
            Cursor c = db.rawQuery("SELECT  *  FROM   Meter where KH=?", new String[]{kh});
            while (c.moveToNext()) {
                byte[] picBytes = null;
                switch (xFlag) {
                    case 0:
                        picBytes = c.getBlob(c.getColumnIndex("Pic1"));
                        break;
                    case 1:
                        picBytes = c.getBlob(c.getColumnIndex("Pic2"));
                        break;
                    case 2:
                        picBytes = c.getBlob(c.getColumnIndex("Pic3"));
                        break;
                    default:
                        break;
                }
                if (picBytes == null) {
                    return null;
                } else {
                    Log.d("limbo", "上传图片大小：" + (picBytes.length / 1024) + "K");
                    return BitmapFactory.decodeByteArray(picBytes, 0, picBytes.length);
                    //                return picBytes;
                }
            }
            return null;
        } catch (Exception e) {
            Log.e("limbo", e.toString());
            return null;
        }

    }

    /**
     * 根据KH号获取表经纬度
     */
    public static List<String> getMeterLocationByKH(String kh) {
        try {
            List<String> strings = new ArrayList<>();
            Cursor c = db.rawQuery("SELECT  *  FROM   Meter where KH=?", new String[]{kh});
            while (c.moveToNext()) {
                strings.add(c.getString(c.getColumnIndex("Lon")));
                strings.add(c.getString(c.getColumnIndex("Lat")));

                break;
            }
            return strings;
        } catch (Exception e) {
            Log.e("limbo", e.toString());
            return null;
        }
    }

    /**
     * 根据KH号获取表
     */
    public static New_Meter getMeterByKH(String kh) {
        try {
            Cursor c = db.rawQuery("SELECT  *  FROM   Meter where KH=?", new String[]{kh});
            New_Meter new_meter = new New_Meter();
            while (c.moveToNext()) {
                new_meter.setKH(c.getString(c.getColumnIndex("KH")));
                new_meter.setMeterState(c.getString(c.getColumnIndex("MeterState")));
                new_meter.setCbState(c.getString(c.getColumnIndex("CbState")));
                new_meter.setData(c.getString(c.getColumnIndex("Data")));
                new_meter.setReadData(c.getString(c.getColumnIndex("ReadData")));
                String note = c.getString(c.getColumnIndex("Note"));
                if (note == null) {
                    new_meter.setNote("");
                } else {
                    new_meter.setNote(note);
                }

                break;
            }
            return new_meter;
        } catch (Exception e) {
            Log.e("limbo", e.toString());
            return null;
        }
    }


    public static void deleteXHAndMeter(String XH) {
        try {
            db.beginTransaction();
            try {
                String[] args = {String.valueOf(XH)};//将需要更新的字段放入
                db.delete("XH", "XH=?", args);
                db.delete("Meter", "XH=?", args);
                db.setTransactionSuccessful();
                // 设置事务的标志为true，调用此方法会在执行到endTransaction()方法是提交事务，
                // 若没有调用此方法会在执行到endTransaction()方法回滚事务。
            } finally {
                db.endTransaction();
            }
        } catch (Exception e) {
            Log.d("limbo", e.toString());
        }

    }

    public static String getSDPath() {
        try {
            File sdDir;
            boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED); // 判断SD卡是否存在,及是否具有读写的权利
            if (sdCardExist) {
                //                Log.d("limbo", "exist");
                sdDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/SeckLoRaDB");// 获取SD卡的path

                //                Log.d("limbo", "return sdPath:" + sdDir.toString());

                return sdDir.toString();//将path转化为string类型返回
            } else {
                //                Log.d("limbo", "no exist");
                return "/storage/sdcard0";
            }
        } catch (Exception e) {
            Log.e("limbo", "getSDPath:" + e.toString());
            return "";
        }
    }


    /**
     * 改变1个对应用户图片
     */
    public static void newChangePic(New_Meter m, int x_Flag) {
        try {
            db.beginTransaction();
            try {
                ContentValues cv = new ContentValues();//图片以byte数组字符串形式放入数据库
                cv.put("UploadTime", m.getUploadTime());
                cv.put("Note", m.getNote());

                if (m.getPic1() != null) {
                    Bitmap pic = m.getPic1();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    pic.compress(Bitmap.CompressFormat.JPEG, 100, baos);//无损耗压缩图片为输出流
                    int options = 100;
                    Log.d("limbo", "原图大小：" + baos.toByteArray().length / 1024 + "");
                    while (baos.toByteArray().length / 1024 > 100) {  //循环判断如果压缩后图片是否大于60kb,大于继续压缩
                        baos.reset();//重置baos即清空baos
                        options -= 30;//每次都减少30
                        bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
                    }
//                    Log.d("limbo", "压缩为" + options + "%:" + baos.toByteArray().length / 1024 + "");
                    byte[] picBytes = baos.toByteArray();
                    switch (x_Flag) {
                        case 0:
                            cv.put("Pic1", picBytes);
                            break;
                        case 1:
                            cv.put("Pic2", picBytes);
                            break;
                        case 2:
                            cv.put("Pic3", picBytes);
                            break;
                        default:
                            break;
                    }
                    cv.put("Lon", m.getLon());
                    cv.put("Lat", m.getLat());

                    cv.put("UploadFlag", "未上传");
                    String[] args = {String.valueOf(m.getKH())};//将需要更新的数据放入
                    db.update("Meter", cv, "KH=?", args);//更新数据表中特定数据
                } else {
                    Log.d("limbo", "图片为空");
                }

                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        } catch (Exception e) {
            Log.d("limbo", e.toString());
        }
    }

    /**
     * 改变1个对应用户信息
     */
    public static void newChangeUser(New_Meter m, boolean flag) {
        try {
            db.beginTransaction();
            try {
                boolean changeFlag = false;
                New_Meter mx = getMeterByKH(m.getKH());
                    /*Log.d("limbo", "KH:" + mx.getKH()
                            + "\nData:" + mx.getData()
                            + "\nReadData:" + mx.getReadData()
                            + "\nCbState:" + mx.getCbState()
                            + "\nMeterState:" + mx.getMeterState()
                            + "\nNote:" + mx.getNote());
                    Log.d("limbo", "KH:" + m.getKH()
                            + "\nData:" + m.getData()
                            + "\nReadData:" + m.getReadData()
                            + "\nCbState:" + m.getCbState()
                            + "\nMeterState:" + m.getMeterState()
                            + "\nNote:" + m.getNote());*/

                ContentValues cv = new ContentValues();//图片以byte数组字符串形式放入数据库
                if (!mx.getData().equals(m.getData())) {
                    changeFlag = true;
                    Log.d("limbo", "Data changed");
                }
                if (!mx.getReadData().equals(m.getReadData())) {
                    changeFlag = true;
                    Log.d("limbo", "ReadData changed");
                }
                if (!mx.getCbState().equals(m.getCbState())) {
                    changeFlag = true;
                    Log.d("limbo", "CbState changed");
                }
                if (!mx.getMeterState().equals(m.getMeterState())) {
                    changeFlag = true;
                    Log.d("limbo", "MeterState changed");
                }
                if (!mx.getNote().equals(m.getNote())) {
                    changeFlag = true;
                    Log.d("limbo", "Note changed");
                }
                if (changeFlag) {
                    Log.d("limbo", "save meter data");
                    cv.put("Data", m.getData());
                    cv.put("ReadData", m.getReadData());
                    cv.put("CbState", m.getCbState());
                    cv.put("Note", m.getNote());
                    cv.put("MeterState", m.getMeterState());
                    cv.put("UploadTime", m.getUploadTime());
                    if (m.getCbFlag() == null || !m.getCbFlag().equals("已抄")) {
                        if (m.getLon().equals("") || m.getLat().equals("")) {
                            Log.d("limbo", "抄表模式-给表设置坐标");
                            cv.put("Lon", MenuActivity.lon);
                            cv.put("Lat", MenuActivity.lat);
                            mNew_meters.get(LOCATION).setLon(MenuActivity.lon);//抄表状态
                            mNew_meters.get(LOCATION).setLat(MenuActivity.lat);//抄表状态
                        }
                    }
                    m.setCbFlag("已抄");//抄表状态
                    cv.put("CBFlag", m.getCbFlag());
                    cv.put("PLon", MenuActivity.lon);
                    cv.put("PLat", MenuActivity.lat);
                    cv.put("UploadFlag", "未上传");

                    String[] args = {String.valueOf(m.getKH())};//将需要更新的数据放入
                    db.update("Meter", cv, "KH=?", args);//更新数据表中特定数据
                } else {
                    if (flag) {//重新上传标志位
                        cv.put("UploadFlag", "未上传");
                    }
                    m.setCbFlag("已抄");//抄表状态
                    cv.put("CBFlag", m.getCbFlag());
                    String[] args = {String.valueOf(m.getKH())};//将需要更新的数据放入
                    db.update("Meter", cv, "KH=?", args);//更新数据表中特定数据
                }

                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();

            }
        } catch (Exception e) {
            Log.d("limbo", e.toString());
        }
    }

    /**
     * 根据小区号 采集机号 表号 查找对应表
     */
    public static New_Meter getMeterByMeterMsg(String XqNum, String CjjNum, String MeterNum) {
        try {
            Cursor c = db.rawQuery("SELECT  *  FROM   Meter where XqNum=? and CjjNum=? and MeterNum=?", new String[]{XqNum, CjjNum, MeterNum});
            while (c.moveToNext()) {
                New_Meter new_meter = new New_Meter();
                new_meter.setXH(c.getString(c.getColumnIndex("XH")));
                new_meter.setKH(c.getString(c.getColumnIndex("KH")));
                new_meter.setHH(c.getString(c.getColumnIndex("HH")));
                new_meter.setJBH(c.getString(c.getColumnIndex("JBH")));
                new_meter.setUserName(c.getString(c.getColumnIndex("UserName")));
                new_meter.setUserPhone1(c.getString(c.getColumnIndex("UserPhone1")));
                new_meter.setUserPhone2(c.getString(c.getColumnIndex("UserPhone2")));
                new_meter.setMeterNumber(c.getString(c.getColumnIndex("MeterNumber")));
                new_meter.setCaliber(c.getString(c.getColumnIndex("Caliber")));
                new_meter.setXqNum(c.getString(c.getColumnIndex("XqNum")).trim());
                new_meter.setCjjNum(c.getString(c.getColumnIndex("CjjNum")).trim());
                new_meter.setMeterNum(c.getString(c.getColumnIndex("MeterNum")).trim());
                new_meter.setLastUserWater(c.getString(c.getColumnIndex("LastUserWater")));
                new_meter.setLast2MouthUserWater(c.getString(c.getColumnIndex("Last2MouthUserWater")));
                new_meter.setLast3MouthUserWater(c.getString(c.getColumnIndex("Last3MouthUserWater")));
                new_meter.setAddr(c.getString(c.getColumnIndex("Addr")));
                new_meter.setBtFlag(c.getString(c.getColumnIndex("BtFlag")));
                new_meter.setBjFlag(c.getInt(c.getColumnIndex("BjFlag")));
                new_meter.setCbFlag(c.getString(c.getColumnIndex("CBFlag")));
                new_meter.setReadData(c.getString(c.getColumnIndex("ReadData")));//本月读数
                new_meter.setData(c.getString(c.getColumnIndex("Data")));//本月实用
                new_meter.setNote(c.getString(c.getColumnIndex("Note")));//现场备注
                new_meter.setMeterState(c.getString(c.getColumnIndex("MeterState")));
                new_meter.setCbState(c.getString(c.getColumnIndex("CbState")));
                new_meter.setUserState(c.getString(c.getColumnIndex("UserState")));
                new_meter.setLastData(c.getString(c.getColumnIndex("LastData")));
                new_meter.setLastReadData(c.getString(c.getColumnIndex("LastReadData")));
                new_meter.setIrDAData(c.getString(c.getColumnIndex("IrDAData")));
                new_meter.setLon(c.getString(c.getColumnIndex("Lon")));
                new_meter.setLat(c.getString(c.getColumnIndex("Lat")));
                new_meter.setPLon(c.getString(c.getColumnIndex("PLon")));
                new_meter.setPLat(c.getString(c.getColumnIndex("PLat")));
                new_meter.setUploadTime(c.getString(c.getColumnIndex("UploadTime")));
                new_meter.setUploadFlag(c.getString(c.getColumnIndex("UploadFlag")));
                return new_meter;
            }
        } catch (Exception e) {
            Log.e("limbo", e.toString());
            return null;
        }
        return null;
    }

    /**
     * 改变多个对应用户上传标志位-已上传
     */
    public static void newChangeUploadFlag(List<New_Meter> ms, List<String> failList) {
        try {
            db.beginTransaction();
            try {
                ContentValues cv;//图片以byte数组字符串形式放入数据库
                for (New_Meter m : ms) {
                    boolean flag = true;
                    for (String s : failList) {
                        if (m.getKH().equals(s)) {
                            flag = false;
                            Log.d("limbo", "上传失败表:" + m.getKH());
                            break;
                        }
                    }
                    New_Meter mx = getMeterByKH(m.getKH());
                  /*  Log.d("limbo", "KH:" + mx.getKH()
                            + "\nData:" + mx.getData()
                            + "\nReadData:" + mx.getReadData()
                            + "\nCbState:" + mx.getCbState()
                            + "\nMeterState:" + mx.getMeterState()
                            + "\nNote:" + mx.getNote());
                    Log.d("limbo", "KH:" + m.getKH()
                            + "\nData:" + m.getData()
                            + "\nReadData:" + m.getReadData()
                            + "\nCbState:" + m.getCbState()
                            + "\nMeterState:" + m.getMeterState()
                            + "\nNote:" + m.getNote());*/
                    if (!m.getData().equals(mx.getData()) ||
                            !m.getReadData().equals(mx.getReadData()) ||
                            !m.getNote().equals(mx.getNote()) ||
                            !((Integer.parseInt(m.getCbState()) - 1) + "").equals(mx.getCbState()) ||
                            !((Integer.parseInt(m.getMeterState()) - 1) + "").equals(mx.getMeterState())) {
                        flag = false;
                        Log.d("limbo", "表数据被修改，重新上传");
                    }
                    if (flag) {
                        cv = new ContentValues();
                        cv.put("UploadFlag", "已上传");
                        String[] args = {String.valueOf(m.getKH())};//将需要更新的数据放入
                        Log.d("limbo", "数据标志改为已上传");
                        db.update("Meter", cv, "KH=?", args);//更新数据表中特定数据
                    } else {
                        Log.d("limbo", "flag = false");

                    }
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        } catch (
                Exception e)

        {
            Log.d("limbo", "newChangeUploadFlag:" + e.toString());
        }

    }

    /**
     * 改变1个对应用户上传标志位-已上传
     */
    public static void newChangeUploadFlag(New_Meter m) {
        try {
            db.beginTransaction();
            try {
                New_Meter mx = getMeterByKH(m.getKH());
                  /*  Log.d("limbo", "KH:" + mx.getKH()
                            + "\nData:" + mx.getData()
                            + "\nReadData:" + mx.getReadData()
                            + "\nCbState:" + mx.getCbState()
                            + "\nMeterState:" + mx.getMeterState()
                            + "\nNote:" + mx.getNote());
                    Log.d("limbo", "KH:" + m.getKH()
                            + "\nData:" + m.getData()
                            + "\nReadData:" + m.getReadData()
                            + "\nCbState:" + m.getCbState()
                            + "\nMeterState:" + m.getMeterState()
                            + "\nNote:" + m.getNote());*/
                if (!m.getData().equals(mx.getData()) ||
                        !m.getReadData().equals(mx.getReadData()) ||
                        !m.getNote().equals(mx.getNote()) ||
                        !((Integer.parseInt(m.getCbState()) - 1) + "").equals(mx.getCbState()) ||
                        !((Integer.parseInt(m.getMeterState()) - 1) + "").equals(mx.getMeterState())) {
                    Log.d("limbo", "表数据被修改，重新上传");
                } else {
                    ContentValues cv;//图片以byte数组字符串形式放入数据库
                    cv = new ContentValues();
                    cv.put("UploadFlag", "已上传");
                    String[] args = {String.valueOf(m.getKH())};//将需要更新的数据放入
                    Log.d("limbo", "数据标志改为已上传");
                    db.update("Meter", cv, "KH=?", args);//更新数据表中特定数据
                    db.setTransactionSuccessful();
                }
            } finally {
                db.endTransaction();

            }
        } catch (Exception e) {
            Log.d("limbo", e.toString());
        }
    }

    /**
     * 改变1个对应用户上传标志位-未上传
     */
    public static void newChangeNoUploadFlag(New_Meter m) {
        try {
            db.beginTransaction();
            try {
                ContentValues cv = new ContentValues();//图片以byte数组字符串形式放入数据库
                cv.put("UploadFlag", "未上传");
                String[] args = {String.valueOf(m.getKH())};//将需要更新的数据放入
                db.update("Meter", cv, "KH=?", args);//更新数据表中特定数据
                db.setTransactionSuccessful();

            } finally {
                db.endTransaction();

            }
        } catch (Exception e) {
            Log.d("limbo", e.toString());
        }
    }

    /**
     * 改变1个对应用户的复核标记
     */
    public static void newChangeFhFlag(New_Meter m) {
        try {
            db.beginTransaction();
            try {
                ContentValues cv = new ContentValues();//图片以byte数组字符串形式放入数据库
                cv.put("BtFlag", m.getBtFlag());
                cv.put("UploadFlag", "未上传");
                String[] args = {String.valueOf(m.getKH())};//将需要更新的数据放入
                db.update("Meter", cv, "KH=?", args);//更新数据表中特定数据
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        } catch (Exception e) {
            Log.d("limbo", e.toString());
        }
    }

    /**
     * 改变1个对应用户的抄表员标记
     */
    public static void newChangeBjFlag(New_Meter m) {
        try {
            db.beginTransaction();
            try {
                ContentValues cv = new ContentValues();//图片以byte数组字符串形式放入数据库
                cv.put("BjFlag", m.getBjFlag());
                String[] args = {String.valueOf(m.getKH())};//将需要更新的数据放入
                db.update("Meter", cv, "KH=?", args);//更新数据表中特定数据
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        } catch (Exception e) {
            Log.d("limbo", e.toString());
        }
    }

    /**
     * 实时上传，每分钟从数据库获取所有目前已抄数据上传-无图
     */
    public static List<New_Meter> getMetersUploadNoPic() {
        try {
            List<New_Meter> new_meters = new ArrayList<>();
            Cursor c = db.rawQuery("SELECT  *  FROM   Meter where  CBFlag = '已抄' and (Pic1 is null and Pic2 is null and Pic3 is null) and (UploadFlag is null or UploadFlag != '已上传') and (UploadTime is not null)", null);
            int i = 0;
            while (c.moveToNext()) {
                New_Meter new_meter = new New_Meter();
                new_meter.setXH(c.getString(c.getColumnIndex("XH")));
                new_meter.setKH(c.getString(c.getColumnIndex("KH")));
                new_meter.setHH(c.getString(c.getColumnIndex("HH")));
                new_meter.setJBH(c.getString(c.getColumnIndex("JBH")));
                new_meter.setUserName(c.getString(c.getColumnIndex("UserName")));
                new_meter.setUserPhone1(c.getString(c.getColumnIndex("UserPhone1")));
                new_meter.setUserPhone2(c.getString(c.getColumnIndex("UserPhone2")));
                new_meter.setMeterNumber(c.getString(c.getColumnIndex("MeterNumber")));
                new_meter.setCaliber(c.getString(c.getColumnIndex("Caliber")));
                new_meter.setXqNum(c.getString(c.getColumnIndex("XqNum")));
                new_meter.setCjjNum(c.getString(c.getColumnIndex("CjjNum")));
                new_meter.setMeterNum(c.getString(c.getColumnIndex("MeterNum")));
                new_meter.setLastUserWater(c.getString(c.getColumnIndex("LastUserWater")));
                new_meter.setLast2MouthUserWater(c.getString(c.getColumnIndex("Last2MouthUserWater")));
                new_meter.setLast3MouthUserWater(c.getString(c.getColumnIndex("Last3MouthUserWater")));
                new_meter.setAddr(c.getString(c.getColumnIndex("Addr")));
                new_meter.setBtFlag(c.getString(c.getColumnIndex("BtFlag")));
                new_meter.setMeterState(c.getInt(c.getColumnIndex("MeterState")) + 1 + "");
                new_meter.setCbState(c.getInt(c.getColumnIndex("CbState")) + 1 + "");
                new_meter.setUserState(c.getString(c.getColumnIndex("UserState")));
                new_meter.setData(c.getString(c.getColumnIndex("Data")));
                new_meter.setReadData(c.getString(c.getColumnIndex("ReadData")));
                new_meter.setLastData(c.getString(c.getColumnIndex("LastData")));
                new_meter.setLastReadData(c.getString(c.getColumnIndex("LastReadData")));
                new_meter.setIrDAData(c.getString(c.getColumnIndex("IrDAData")));
                new_meter.setLon(c.getString(c.getColumnIndex("Lon")));
                new_meter.setLat(c.getString(c.getColumnIndex("Lat")));
                new_meter.setPLon(c.getString(c.getColumnIndex("PLon")));
                new_meter.setPLat(c.getString(c.getColumnIndex("PLat")));
                new_meter.setUploadTime(c.getString(c.getColumnIndex("UploadTime")));
                new_meter.setUploadFlag(c.getString(c.getColumnIndex("UploadFlag")));
                new_meter.setCbFlag(c.getString(c.getColumnIndex("CBFlag")));
                new_meter.setNote(c.getString(c.getColumnIndex("Note")));
                new_meter.setPic1(null);
                new_meter.setPic2(null);
                new_meter.setPic3(null);
                new_meters.add(new_meter);
                i++;
                if (i >= 30) {
                    break;
                }
            }
            Log.d("limbo", "从数据库获取实时上传表-无图:" + i);
            return new_meters;
        } catch (Exception e) {
            Log.e("limbo", "getMetersUploadNoPic:" + e.toString());
        }
        return null;

    }

    /**
     * 实时上传，每分钟从数据库获取所有目前已抄数据上传-有图
     */
    public static List<New_Meter> getMetersUploadPic() {
        try {
            List<New_Meter> new_meters = new ArrayList<>();
            Cursor c = db.rawQuery("SELECT  *  FROM   Meter where CBFlag = '已抄' and (Pic1 is not null or Pic2 is not null or Pic3 is not null) and (UploadFlag is null or UploadFlag != '已上传') and (UploadTime is not null)", null);
            int i = 0;

            while (c.moveToNext()) {
                New_Meter new_meter = new New_Meter();
                new_meter.setXH(c.getString(c.getColumnIndex("XH")));
                new_meter.setKH(c.getString(c.getColumnIndex("KH")));
                new_meter.setHH(c.getString(c.getColumnIndex("HH")));
                new_meter.setJBH(c.getString(c.getColumnIndex("JBH")));
                new_meter.setUserName(c.getString(c.getColumnIndex("UserName")));
                new_meter.setUserPhone1(c.getString(c.getColumnIndex("UserPhone1")));
                new_meter.setUserPhone2(c.getString(c.getColumnIndex("UserPhone2")));
                new_meter.setMeterNumber(c.getString(c.getColumnIndex("MeterNumber")));
                new_meter.setCaliber(c.getString(c.getColumnIndex("Caliber")));
                new_meter.setXqNum(c.getString(c.getColumnIndex("XqNum")));
                new_meter.setCjjNum(c.getString(c.getColumnIndex("CjjNum")));
                new_meter.setMeterNum(c.getString(c.getColumnIndex("MeterNum")));
                new_meter.setLastUserWater(c.getString(c.getColumnIndex("LastUserWater")));
                new_meter.setLast2MouthUserWater(c.getString(c.getColumnIndex("Last2MouthUserWater")));
                new_meter.setLast3MouthUserWater(c.getString(c.getColumnIndex("Last3MouthUserWater")));
                new_meter.setAddr(c.getString(c.getColumnIndex("Addr")));
                new_meter.setBtFlag(c.getString(c.getColumnIndex("BtFlag")));
                String meterstate = c.getString(c.getColumnIndex("MeterState"));
                new_meter.setMeterState(c.getInt(c.getColumnIndex("MeterState")) + 1 + "");
                String cbstate = c.getString(c.getColumnIndex("CbState"));
                new_meter.setCbState(c.getInt(c.getColumnIndex("CbState")) + 1 + "");
                new_meter.setUserState(c.getString(c.getColumnIndex("UserState")));
                new_meter.setData(c.getString(c.getColumnIndex("Data")));
                new_meter.setReadData(c.getString(c.getColumnIndex("ReadData")));
                new_meter.setLastData(c.getString(c.getColumnIndex("LastData")));
                new_meter.setLastReadData(c.getString(c.getColumnIndex("LastReadData")));
                new_meter.setIrDAData(c.getString(c.getColumnIndex("IrDAData")));
                new_meter.setLon(c.getString(c.getColumnIndex("Lon")));
                new_meter.setLat(c.getString(c.getColumnIndex("Lat")));
                new_meter.setPLon(c.getString(c.getColumnIndex("PLon")));
                new_meter.setPLat(c.getString(c.getColumnIndex("PLat")));
                new_meter.setUploadTime(c.getString(c.getColumnIndex("UploadTime")));
                new_meter.setUploadFlag(c.getString(c.getColumnIndex("UploadFlag")));
                new_meter.setCbFlag(c.getString(c.getColumnIndex("CBFlag")));
                new_meter.setNote(c.getString(c.getColumnIndex("Note")));
              /*  byte[] picBytes = c.getBlob(c.getColumnIndex("Pic1"));
                new_meter.setPic1(BitmapFactory.decodeByteArray(picBytes, 0, picBytes.length));
                picBytes = c.getBlob(c.getColumnIndex("Pic2"));
                new_meter.setPic2(BitmapFactory.decodeByteArray(picBytes, 0, picBytes.length));
                picBytes = c.getBlob(c.getColumnIndex("Pic3"));
                new_meter.setPic3(BitmapFactory.decodeByteArray(picBytes, 0, picBytes.length));*/
                new_meters.add(new_meter);
                i++;
            }
            Log.d("limbo", "从数据库获取实时上传表-有图:" + i);
            return new_meters;
        } catch (Exception e) {
            Log.e("limbo", "getMetersUploadPic:" + e.toString());
        }
        return null;
    }
}
