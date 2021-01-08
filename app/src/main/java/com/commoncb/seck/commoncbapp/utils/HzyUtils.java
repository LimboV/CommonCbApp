package com.commoncb.seck.commoncbapp.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.util.Base64;
import android.util.Log;

import com.commoncb.seck.commoncbapp.activity.MenuActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by ssHss on 2015/9/10.
 */
public class HzyUtils {

    public static ProgressDialog progressDialog;

    /**
     * 显示进度条
     */
    public static void showProgressDialog(Context context) {

        if (progressDialog == null) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("请等待...");
            progressDialog.setCanceledOnTouchOutside(false);
            // 设置ProgressDialog 是否可以按退回按键取消
            //            progressDialog.setCancelable(false);
        }
        progressDialog.show();


    }

    /**
     * 显示进度条
     */
    public static void showProgressDialog1(Context context, String str) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage(str);
            progressDialog.setCanceledOnTouchOutside(false);
            // 设置ProgressDialog 是否可以按退回按键取消
            //            progressDialog.setCancelable(false);
        } else {
            closeProgressDialog();
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage(str);
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /**
     * 显示百分比
     */
    public static void showProgressDialog2(Context context, int size, String str) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(context);
            // 设置进度条风格，风格为长形
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            // 设置ProgressDialog 标题
            progressDialog.setTitle("更新进度");
            // 设置ProgressDialog 提示信息
            progressDialog.setMessage(str);
            // 设置ProgressDialog 进度条进度
            progressDialog.setMax(size);
            // 设置ProgressDialog 的进度条是否不明确
            progressDialog.setIndeterminate(false);
            // 设置ProgressDialog 是否可以按退回按键取消
            progressDialog.setCanceledOnTouchOutside(false);

        }
        progressDialog.show();
    }


    /**
     * 关闭进度条
     */
    public static void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        progressDialog = null;
    }

    /**
     * 校验和
     * x为字符串
     * y为待对比校验和
     */
    public static boolean checkSum(String a, String b) {
        byte[] x;
        String msg = a;
        int length = a.length() / 2;
        x = HzyUtils.getHexBytes(msg);
        byte y = HzyUtils.countSum(x, 0, length);
        int vvv = y & 0xFF;
        String hv = Integer.toHexString(vvv);
        if (hv.length() < 2) {
            hv = "0" + hv;
        }
        Log.d("limbo", hv + "--" + b);
        return hv.equals(b);
    }

    /**
     * 计算指定位置的校验和
     *
     * @param desSTR
     * @param pos
     * @param len
     * @return
     */
    public static byte countSum(byte[] desSTR, int pos, int len) {
        byte sum = 0;
        int i;
        for (i = 0; i < len; i++) {
            sum += desSTR[pos + i];
        }
        // sum = 255 - sum;
        return sum;
    }

    /**
     * 计算校验和
     */
    public static String getCheckSum(String a) {
        byte[] x;
        String msg = a;
        int length = a.length() / 2;
        x = HzyUtils.getHexBytes(msg);
        byte y = HzyUtils.countSum(x, 0, length);
        int vvv = y & 0xFF;
        String hv = Integer.toHexString(vvv);
        if (hv.length() < 2) {
            hv = "0" + hv;
        }
        return hv;
    }

    /**
     * 计算16位进制字符串校验码
     */
    public static String countSum(String str) {
        byte sum = 0;
        byte[] desSTR = HzyUtils.getHexBytes(str);
        for (int i = 0; i < desSTR.length; i++) {
            sum += desSTR[0 + i];
        }
        int vvv = sum & 0xFF;
        String hv = Integer.toHexString(vvv);
        if (hv.length() < 2) {
            hv = "0" + hv;
        }
        Log.d("limbo", "校验和:" + hv);
        return hv;
    }

    /**
     * 计算16位进制字符串校验码
     */
    public static String countSum1(String str) {
        Log.d("limbo",str);
        int sum = 0;
        byte[] desSTR = HzyUtils.getHexBytes(str);
        for (int i = 0; i < desSTR.length; i++) {
            sum += desSTR[i] & 0xff;
        }
        Log.d("limbo","sum="+sum);
        int vvvH = sum / 256 & 0xFF;
        int vvvL = sum % 256 & 0xFF;

        String hv1 = Integer.toHexString(vvvH);
        String hv2 = Integer.toHexString(vvvL);
        if (hv1.length() < 2) {
            hv1 = "0" + hv1;
        }
        if (hv2.length() < 2) {
            hv2 = "0" + hv2;
        }
        String hv = hv1 + hv2;
        Log.d("limbo", "校验和:" + hv);
        return hv;
    }

    public static byte[] GetCRC16(byte[] buf, int len) {
        int uiCRC = 0x0000ffff;
        byte[] crc16 = new byte[2];
        for (int i = 0; i < len; i++) {
            uiCRC ^= buf[i] & 0xFF;

            for (int j = 0; j < 8; j++) {
                if ((uiCRC & 1) != 0) {
                    uiCRC >>= 1;
                    uiCRC ^= 0xA001;
                } else {
                    uiCRC >>= 1;
                }
            }
        }
        crc16[0] = (byte) uiCRC;
        crc16[1] = (byte) (uiCRC >> 8);
        return crc16;
    }

    /**
     * CRC16校验码
     */
    public static String CRC16(String crc) {
        int crc16 = LoRa_CRCUtils.calcCrc16(HzyUtils.getHexBytes(crc));
        String crc16Result = Integer.toHexString(crc16);
        while (crc16Result.length() != 4) {
            crc16Result = "0" + crc16Result;
        }
        StringBuilder ssb = new StringBuilder("");
        for (int ii = 0; ii < 4; ii += 2) {
            ssb.append(crc16Result.substring(ii + 1, ii + 2));
            ssb.append(crc16Result.substring(ii, ii + 1));
        }
        crc16Result = ssb.toString();
        StringBuilder sb = new StringBuilder(crc16Result);
        sb.reverse();
        crc16Result = sb.toString();
        return crc16Result;
    }

    /**
     * 从字符串到16进制byte数组转换
     * String to HEX
     */
    public static byte[] getHexBytes(String message) {
        if (message == null || message.equals("")) {
            return null;
        }
        message = message.trim();
        int len = message.length() / 2;
        char[] chars = message.toCharArray();
        String[] hexStr = new String[len];
        byte[] bytes = new byte[len];
        for (int i = 0, j = 0; j < len; i += 2, j++) {
            hexStr[j] = "" + chars[i] + chars[i + 1];
            bytes[j] = (byte) Integer.parseInt(hexStr[j], 16);
        }
        return bytes;
    }

    /**
     * 从字节数组到16进制字符串转换
     * byte[] to HEXString
     */
    public static String bytes2HexString(byte[] b) {
        if (b == null || b.length <= 0) {
            return null;
        }
        StringBuilder ret = new StringBuilder("");
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() < 2) {
                hex = '0' + hex;
            }
            ret.append(hex.toUpperCase());
        }
        return ret.toString();
    }

    /**
     * 将字符串调换
     */
    public static String changeString(String newID) {
        while (newID.length() != 14) {
            newID = "0" + newID;
        }
        StringBuilder ssb = new StringBuilder("");
        for (int ii = 0; ii < 14; ii += 2) {
            ssb.append(newID.substring(ii + 1, ii + 2));
            ssb.append(newID.substring(ii, ii + 1));
        }
        newID = ssb.toString();
        StringBuilder sb = new StringBuilder(newID);
        sb.reverse();
        newID = sb.toString();
        return newID;
    }

    /**
     * 将字符串两两调换
     */
    public static String changeString1(String newID) {
        if (newID.length() == 4) {
            while (newID.length() % 2 != 0) {
                newID = "0" + newID;
            }
            String x = newID.substring(0, 2);
            String y = newID.substring(2, 4);
            return y + x;
        } else {
            return newID;
        }

    }


    /**
     * 字符串变为16位进制字符串
     */
    public static String convertStringToHex(String str) {
        char[] chars;
        StringBuffer hex;
        if (HzyUtils.isEmpty(str)) {
            str = "0";

        }
        chars = str.toCharArray();
        hex = new StringBuffer();
        for (int i = 0; i < chars.length; i++) {
            hex.append(Integer.toHexString((int) chars[i]));
        }
        return hex.toString();
    }

    /**
     * 判断网络是否开启
     *
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        NetworkInfo netInfo = ((ConnectivityManager) context.getSystemService(
                Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if ((netInfo != null) && (netInfo.isAvailable())) {
            return true;
        }
        return false;
    }

    /**
     * 获得当前的系统时间
     *
     * @return
     */
    public String getSysTime() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    /**
     * from byte to int, because of byte in java is signed
     */
    public static int[] toIntArray(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        int[] data = new int[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            data[i] = bytes[i] >= 0 ? (int) bytes[i] : (int) (bytes[i] + 256);
        }
        return data;
    }

    /**
     * 10进制转16进制
     * Integer.toHexString()
     */
    public static String toHexString(String str) {
        if (HzyUtils.isEmpty(str)) {
            return "0";
        } else {
            return Long.toHexString(Long.parseLong(str));
        }
    }

    public static String toHexString(byte[] byteArray, int size) {
        if (byteArray == null || byteArray.length < 1)
            throw new IllegalArgumentException(
                    "this byteArray must not be null or empty");
        final StringBuilder hexString = new StringBuilder(2 * size);
        for (int i = 0; i < size; i++) {
            if ((byteArray[i] & 0xff) < 0x10)//
                hexString.append("0");
            hexString.append(Integer.toHexString(0xFF & byteArray[i]));
            if (i != (byteArray.length - 1))
                hexString.append(" ");
        }
        return hexString.toString().toUpperCase();
    }

    /**
     * 16进制转10进制
     * Integer.parseInt("8C",16);
     */
    public static String HextoString(String str) {
        if (HzyUtils.isEmpty(str)) {
            return "0";
        } else {
            return Integer.parseInt(str, 16) + "";
        }

    }

    /**
     * 16位进制字符串转字符串
     */
    public static String toStringHex(String s) {
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            s = new String(baKeyword, "utf-8");//UTF-16le:Not
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return s;
    }

    /**
     * 16位进制字符串转汉字字符串
     */
    public static String toStringHex1(String s) {
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            s = new String(baKeyword, "GB2312");//UTF-16le:Not
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return s;
    }

    /**
     * 字符串，每两位之间加空格
     */
    public static String addPlace(String input) {
        String regex = "(.{2})";
        input = input.replaceAll(regex, "$1 ");
        return input;
    }

    /**
     * SD卡中创建文件
     * 返回: false --创建失败
     * true--成功
     */
    public static boolean exportTxt(String s, String name) {
        try {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Seck";
            Log.d("limbo", "创建文件夹" + path);
            File file = new File(path);
            if (!file.exists()) {
                try {
                    file.mkdirs();
                } catch (Exception e) {
                    Log.d("limbo", "file create error");
                }

            }

            String filePath = path + "/" + name + ".txt";
            Log.d("limbo", "创建文件" + filePath);
            File f = new File(filePath);
            if (!f.exists()) {
                try {
                    f.createNewFile();
                    Log.d("limbo", "file create success:" + name);
                } catch (Exception e) {
                    Log.d("limbo", "file create error");
                    return false;
                }
            }

            FileOutputStream outStream = new FileOutputStream(f);
            OutputStreamWriter writer = new OutputStreamWriter(outStream, "gb2312");
            writer.write(s);
            writer.flush();
            writer.close();//记得关闭

            outStream.close();
            Log.d("limbo", "file write success");
            return true;
        } catch (Exception e) {
            Log.d("limbo", "file write error");
            return false;
        }
    }

    /**
     * 判断字符是否为空
     */
    public static boolean isEmpty(CharSequence str) {
        if (str == null || str.length() == 0)
            return true;
        else
            return false;
    }
    /**
     * 判断字符是否为空
     */
    public static String isNull(String str) {
        if (str == null || str.length() == 0)
            return "";
        else
            return str;
    }

    /**
     * 得到所有的存储路径（内部存储+外部存储）
     *
     * @param context
     * @return
     */
    public static String[] getAllSdPaths(Context context) {
        Method mMethodGetPaths = null;
        String[] paths = null;
        //通过调用类的实例mStorageManager的getClass()获取StorageManager类对应的Class对象
        //getMethod("getVolumePaths")返回StorageManager类对应的Class对象的getVolumePaths方法，这里不带参数
        StorageManager mStorageManager = (StorageManager) context
                .getSystemService(context.STORAGE_SERVICE);//storage
        try {
            mMethodGetPaths = mStorageManager.getClass().getMethod("getVolumePaths");
            paths = (String[]) mMethodGetPaths.invoke(mStorageManager);
            for (int i = 0; i < paths.length; i++) {
                Log.d("limbo", "x:   " + paths[i]);
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return paths;
    }

    /**
     * 返回当前时间
     */
    public static String returenNowTime() {
        Calendar c = Calendar.getInstance();
        Date date = c.getTime();
        DateFormat df = new SimpleDateFormat("yyyy年MM月dd日HH点mm分ss秒");
        String s = df.format(date);
        return s;
    }

    /**
     * 用以判断当前表设置表频率是否超过了当前表类型的设置范围
     */
    public static boolean isConformToRange(String s) {
        s = s.toLowerCase();
        if (s.contains("a") || s.contains("b") || s.contains("c") || s.contains("d") || s.contains("e") || s.contains("f")) {
            s = Integer.parseInt(s, 16) + "";
        }
        Log.d("limbo", "isConformToRange--" + s);
        if (HzyUtils.isEmpty(s)) {
            return true;
        }
        long i = Long.parseLong(s);
        if (i > 510000 || i < 400000) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断字符串长度是否足够,不够就补足
     */
    public static String isLength(String str, int len) {
        while (str.length() < len) {
            str = "0" + str;
        }
        return str;
    }

    /**
     * 判断字符串长度是否足够,不够就补足--后面补0
     */
    public static String isLength1(String str, int len) {
        while (str.length() < len) {
            str = str + "0";
        }
        return str;
    }

    /**
     * 十六进制字符串转化为byte数组
     */
    public static byte[] toByteArray(String hexString) {
        hexString = hexString.toUpperCase();
        final byte[] byteArray = new byte[(hexString.length() + 1) / 3];
        int k = 0;
        for (int i = 0; i < byteArray.length; i++) {
            byte high = (byte) (Character.digit(hexString.charAt(k), 16) & 0xff);
            byte low = (byte) (Character.digit(hexString.charAt(k + 1), 16) & 0xff);
            byteArray[i] = (byte) (high << 4 | low);
            k += 3;
        }
        return byteArray;
    }

    public static String GetBlueToothMsg() {
        synchronized (MenuActivity.Cjj_CB_MSG) {
            if (MenuActivity.Cjj_CB_MSG.length() > 0) {
                String x = MenuActivity.Cjj_CB_MSG;
                MenuActivity.Cjj_CB_MSG = "";
                return x;
            } else {
                return "";
            }
        }


    }

    public static byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    public static Bitmap Bytes2Bitmap(byte[] b) {
        if (b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        } else {
            return null;
        }
    }

    /**
     * bitmap转为base64
     *
     * @param bitmap
     * @return
     */
    public static String bitmapToBase64(Bitmap bitmap) {

        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);


              /*  int options = 100;
                Log.d("limbo", baos.toByteArray().length / 1024 + "");
                while (baos.toByteArray().length / 1024 > 100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
                    baos.reset();//重置baos即清空baos
                    options -= 10;//每次都减少10
                    bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中

                    Log.d("limbo", "压缩为" + options + "%:" + baos.toByteArray().length / 1024 + "");
                }
*/
                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);

                baos.flush();
                baos.close();

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * base64转为bitmap
     *
     * @param base64Data
     * @return
     */
    public static Bitmap base64ToBitmap(String base64Data) {
        byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
