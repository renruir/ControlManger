package com.ctftek.controlmanager;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.Socket;

/**
 * Created by renrui on 2017/7/14.
 */

public class AppUtils {
    private static final String TAG = "AppUtils";

    private int start[] = {0, 0} ;
    private int end[] = {0, 0} ;


    private byte sentData[] = new byte[16];
    private static AppUtils appUtils;
    private Context mContext;
    private volatile String socketIP;
    private volatile int socketPort;
    private Socket socket;
    private OutputStream os;
    private byte mStart;
    private byte mEnd;

    public static final AppUtils getIntance(Context context){
        if(appUtils == null){
            appUtils = new AppUtils(context);
        }
        return appUtils;
    }
    public AppUtils(Context context){
        mContext = context;
    }

    public void setClickedInit(int sRow, int sColumn, int eRow, int eColumn){
        start[0] = sRow;
        start[1] = sColumn;
        end[0] = eRow;
        end[1] = eColumn;
    }

    public void setStartEnd(byte start, byte end){
        this.mStart = start;
        this.mEnd = end;
    }

    public void setIpPort(String ip, int port){
        socketIP = ip;
        socketPort = port;
    }

    public void sendMessage(final byte function, final byte index){
        sentData[0] = (byte)0x0e;
        sentData[1] = (byte)0x01;
        sentData[2] = function;
        sentData[3] = index;
        sentData[4] = (byte)0x00;
        sentData[5] = (byte)0x00;
        sentData[6] = (byte)0x00;
        sentData[7] = (byte)0x00;
        sentData[8] = (byte)0x00;
        sentData[9] = (byte)0x00;
        sentData[10] = (byte)0x00;
        sentData[11] = (byte)0x00;
        sentData[12] = (byte)0x00;
        sentData[13] = (byte)0x00;
        sentData[14] = (byte)0x00;

        int sum = 0;
        for(int i = 0; i < sentData.length -2; i++){
            sum += sentData[i];
        }

        sentData[15] = (byte)sum;

        send(sentData);
    }

    public void send(final byte[] sentData){
        Log.d(TAG, "send data: " + byte2hex(sentData));
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d(TAG, "socketIP: " +  socketIP);
                    Log.d(TAG, "socketPort: " + socketPort);

                    socket = new Socket(socketIP, socketPort);
                    os = socket.getOutputStream();

                    os.write(sentData);
                    os.flush();
                    socket.close();
                } catch (Exception e) {
                    Log.e(TAG, "==========error===========");
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static String byte2hex(byte[] buffer) {
        String h = "";
        for (int i = 0; i < buffer.length; i++) {
            String temp = Integer.toHexString(buffer[i] & 0xFF);
            if (temp.length() == 1) {
                temp = "0" + temp;
            }
            h = h + " " + temp;
        }
        return h;
    }


    public static boolean checkDeviceHasNavigationBar(Context context) {
        boolean hasNavigationBar = false;
        try {
            Resources rs = context.getResources();
            int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
            if (id > 0) {
                hasNavigationBar = rs.getBoolean(id);
            }
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {

        }
        return hasNavigationBar;
    }
}
