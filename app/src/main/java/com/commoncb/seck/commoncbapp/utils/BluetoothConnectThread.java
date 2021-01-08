package com.commoncb.seck.commoncbapp.utils;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.commoncb.seck.commoncbapp.activity.MainActivity;
import com.commoncb.seck.commoncbapp.activity.MenuActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static com.commoncb.seck.commoncbapp.activity.IrDACbActivity.deBugMsg;


/**
 * Created by ssHss on 2016/7/18.
 */
public class BluetoothConnectThread extends Thread {

    static public final int NETWORK_MESSAGE_READ = 0x00;
    static public final int NETWORK_CONNECTED = 0x01;
    static public final int NETWORK_FAILED = 0x22;
    static public final int METER = 0x03;//表类型更新提示


    public String deviceName = "";
    public static BluetoothSocket mmSocket;
    public static InputStream mmInStream;
    public static OutputStream mmOutStream;

    private long timeRef = 0;//用于超时设定

    private byte[] mmRevbuffer = new byte[1024000];
    private Queue_Index mmRQ = new Queue_Index();

    /**
     * 创建蓝牙连接
     */
    public BluetoothConnectThread(BluetoothSocket socket) {
        mmSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
            Log.v("limbo", e.toString());
        }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }

    @Override
    public void run() {
        int retnum = 0;
        byte[] buffer = new byte[1024]; // buffer store for the stream
        //        while(true){
        String msg;
        while (MainActivity.AllBtAuto) {
            try {
                retnum = mmInStream.read(buffer);
                msg = b2sn(buffer, retnum);
                MenuActivity.Cjj_CB_MSG = MenuActivity.Cjj_CB_MSG + msg.toLowerCase();
                deBugMsg = deBugMsg + msg;
                Log.d("limbo", msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class Queue_Index {
        int l, r;
    }

    public void queueInsert(byte b) {
        synchronized (mmRQ) {
            mmRevbuffer[mmRQ.r++] = b;

            // Cicle array.
            if (mmRQ.r >= mmRevbuffer.length)
                mmRQ.r = 0;
        }
    }

    public static String b2sn(byte[] src, int n) {
        if (src == null || src.length <= 0) {
            return null;
        }
        String saddr = "";

        for (int i = 0; i < n; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                hv = "0" + hv;
            }
            saddr = saddr + " 0x" + hv;
        }
        return saddr;
    }

    public void cancel() throws IOException {
        mmSocket.close();
        this.interrupt();
    }

    public void write(byte[] bytes) throws IOException {
        mmOutStream.write(bytes);
    }

    /**
     * 具备默认补齐方式的转换Addr获取
     *
     * @param hexString
     * @return
     */
    public byte[] getSBAddr(String hexString) {
        int hexlen = 14; // 7位地址

        hexString = hexString.toUpperCase();
        while (hexString.length() < hexlen) {
            hexString = "0" + hexString;
        }

        byte[] addr_raw = HzyUtils.getHexBytes(hexString);
        return addr_raw;
    }
}
