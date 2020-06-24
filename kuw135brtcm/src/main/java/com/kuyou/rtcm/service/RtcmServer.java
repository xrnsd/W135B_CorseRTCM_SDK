package com.kuyou.rtcm.service;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class RtcmServer implements Runnable {
    private String TAG = "ClientConnService";

    public static final int MSG_SERVER_RETURNS_DATA = 123;
    public static final int MSG_SERVER_SOCKET_CONNECTION_INIT_COMPLETE = 200;
    public static final int MSG_SEND_DATA_TO_THE_SERVER_COMPLETE = 303;
    public static final int MSG_SERVER_SOCKET_CONNECTION_EXCEPTION = 404;
    public static final int MSG_SEND_DATA_BY_NTRIP = 521;

    private Socket mSocket = null;
    private Handler mHandlerClient = null;
    private BufferedReader mBufferedReader = null;
    private OutputStream mOutputStream = null;
    private String serverAddres;
    private int serverPort;

    public RtcmServer(Handler handler, String serveradr, int port) {
        mHandlerClient = handler;
        serverAddres = serveradr;
        serverPort = port;
    }

    /**
     * <p>
     * action : 断开差分服务器连接<br/>
     * author: wuguoxian <br/>
     * date: 20200514 <br/>
     *
     */
    public void disConnect() {
        try {
            if (mBufferedReader != null) {
                mBufferedReader.close();
                mBufferedReader = null;
            }
            if (mSocket != null) {
                mSocket.close();
                mSocket = null;
            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    /**
     * <p>
     * action : 发送数据到差分服务器<br/>
     * author: wuguoxian <br/>
     * date: 20200514 <br/>
     *
     * @param sendByte 待发送到差分服务器的数据
     */
    public void sendBytes(byte[] sendByte) {
        try {
            String sendStrByte = "";
            for (int i = 0; i < sendByte.length; i++) {
                sendStrByte += sendByte[i] + " ";
            }
            String str = new String(sendByte);
            Log.d(TAG, "sendMessage ==  " + str);
            Log.d(TAG, "sendByte ==  " + sendStrByte);
            mOutputStream.write(sendByte);
            mHandlerClient.sendEmptyMessage(MSG_SEND_DATA_TO_THE_SERVER_COMPLETE);
            Log.d(TAG, "os.write Success!!!");
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
            mHandlerClient.sendEmptyMessageDelayed(MSG_SERVER_SOCKET_CONNECTION_EXCEPTION, 1000);
        }
    }

    @Override
    public void run() {
        try {
            Log.v(TAG, "ClientThread enter ClientThread serverAddres = " + serverAddres + "serverPort = " + serverPort);
            mSocket = new Socket(serverAddres, serverPort);
            Log.v(TAG, "ClientThread Socket create succes");
            mBufferedReader = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
            Log.v(TAG, "ClientThread mBufferedReader create succes");
            mOutputStream = mSocket.getOutputStream();
            Log.v(TAG, "ClientThread os create succes");
            mHandlerClient.sendEmptyMessage(MSG_SERVER_SOCKET_CONNECTION_INIT_COMPLETE);

            new Thread() {
                @Override
                public void run() {
                    Log.v(TAG, "TestClientConn Thread is enter");
                    try {
                        int isFirst = 1;
                        while (true) {
                            byte[] buffer = new byte[1024];
                            int len = mSocket.getInputStream().read(buffer);
                            if (len > 0) {
                                Message msg = new Message();
                                msg.what = MSG_SERVER_RETURNS_DATA;
                                msg.obj = buffer;
                                msg.arg1 = len;
                                msg.arg2 = isFirst;
                                Log.v(TAG, "ClientThread msg.obj is:" + msg.obj);
                                Log.v(TAG, "ClientThread enter rev thread");
                                mHandlerClient.sendMessage(msg);
                            }
                            isFirst = 0;
                        }
                    } catch (IOException e) {
                        Log.e(TAG, Log.getStackTraceString(e));
                        if (e.getMessage().contains("ETIMEDOUT") || e.getMessage().contains("ENETUNREACH")) {
                            mHandlerClient.sendEmptyMessageDelayed(MSG_SERVER_SOCKET_CONNECTION_EXCEPTION, 1000);
                        }
                    }
                }
            }.start();

        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
            mHandlerClient.sendEmptyMessageDelayed(MSG_SERVER_SOCKET_CONNECTION_EXCEPTION, 1000);
        }
    }

}
