package com.kuyou.rtcm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.kuyou.rtcm.service.GetLocationService;
import com.kuyou.rtcm.service.RtcmServer;
import com.kuyou.rtcm.chip.ChipMXT906A;
import com.kuyou.rtcm.chip.RtcmChiP;
import com.kuyou.rtcm.utils.UtilNtrip;
import com.kuyou.rtcm.utils.Utils;

import java.util.ArrayList;

/**
 * <p>
 * action : 差分功能控制器<br/>
 * author: wuguoxian <br/>
 * date: 20200510 <br/>
 * version:1.02 <br/>
 */
public class RtcmControl {

    private static final String TAG = "CorseRtcm";
    private final String ACTION_GETNMEA_STATUS = "com.BDMessage.sendNmeaBroadCast";
    private final String ACTION_GETLOCATION = "com.BDMessage.sendLocationBroadCast";

    private static RtcmControl sMain;
    private RtcmConfigInfo mRtcmConfigInfo;
    private IRtcmConfigCallBack mRtcmConfigCallBack;
    private IRtcmDataListener mRtcmDataListener;

    public static interface IRtcmConfigCallBack {
        /**
         * <p>
         * action : 差分配置初始化结果<br/>
         * author: wuguoxian <br/>
         * date: 20200514 <br/>
         * </p>
         *
         * @param resultCode 初始化项具体执行结果，定义详见 RtcmConfigInfo
         */
        void onRtcmConfig(int resultCode);

        /**
         * <p>
         * action : 差分和定位状态变化<br/>
         * author: wuguoxian <br/>
         * date: 20200514 <br/>
         * </p>
         *
         * @param statusCode 定义详见 STATUS_POSITION_xxxxxx 相关常量
         */
        void onRtcmStatusChange(int statusCode);

        /**
         * <p>
         * action : 差分服务器具体配置<br/>
         * author: wuguoxian <br/>
         * date: 20200514 <br/>
         * remark: RtcmConfigInfo初始化示例  <br/>
         * &nbsp return new RtcmConfigInfo.Builder() <br/>
         * &nbsp &nbsp &nbsp .context(getApplicationContext()) <br/>
         * &nbsp &nbsp &nbsp .userName("xxx") //差分账号 <br/>
         * &nbsp &nbsp &nbsp .userPassword("xxxxxx") //差分账号密码 <br/>
         * &nbsp &nbsp &nbsp .serverIP("xxx.xxx.xxx.xxx") //差分服务器地址 <br/>
         * &nbsp &nbsp &nbsp .serverPort("xxx") //差分服务器端口 <br/>
         * &nbsp &nbsp &nbsp .mountPoint("xxxxx") //差分服务器挂载点 <br/>
         * &nbsp &nbsp &nbsp .build(); <br/>
         *
         * @return RtcmConfigInfo 差分服务器具体配置
         */
        RtcmConfigInfo getRtcmConfig();
    }

    /**
     * <p>
     * action : 差分补偿参数监听器<br/>
     * author: wuguoxian <br/>
     * date: 20200514 <br/>
     * remark:调试用<br/>
     *
     */
    public static interface IRtcmDataListener {
        void onRtcmParameterUpdate(byte[] data);
    }

    private RtcmControl(IRtcmConfigCallBack listener) {
        mRtcmConfigCallBack = listener;
        if (null != listener)
            mRtcmConfigInfo = listener.getRtcmConfig();
    }

    /**
     * <p>
     * action : 获取差分功能控制器示例<br/>
     * author: wuguoxian <br/>
     * date: 20200514 <br/>
     */
    public static RtcmControl getInstance(IRtcmConfigCallBack listener) {
        if (null == sMain) {
            sMain = new RtcmControl(listener);
        }
        return sMain;
    }

    /**
     * <p>
     * action : 初始化差分配置，成功后默认开启差分<br/>
     * author: wuguoxian <br/>
     * date: 20200514 <br/>
     */
    public void init() {
        initRtcmChipConfig(mRtcmConfigInfo);
        initNmeaDataListener(mRtcmConfigInfo);
        initRtcmServer(mRtcmConfigInfo);
    }

    /**
     * <p>
     * action : 关闭差分<br/>
     * author: wuguoxian <br/>
     * date: 20200514 <br/>
     */
    public void exit() {
        destroySerialPort();
        disConnectRtcmServer();
    }

    /**
     * <p>
     * action : 设定差分补偿参数监听器<br/>
     * author: wuguoxian <br/>
     * date: 20200514 <br/>
     */
    public void setRtcmDataListener(IRtcmDataListener listener) {
        mRtcmDataListener = listener;
    }

    /**
     * action : 说明详见 RtcmControl.RtcmDataListener <br/>
     */
    private void onRtcmParameterUpdate(byte[] data) {
        if (null == mRtcmDataListener)
            return;
        mRtcmDataListener.onRtcmParameterUpdate(data);
    }

    /**
     * action : 说明详见 RtcmControl.RtcmConfigCallBack <br/>
     */
    private void onRtcmConfig(int resultCode) {
        if (null == mRtcmConfigCallBack)
            return;
        mRtcmConfigCallBack.onRtcmConfig(resultCode);
    }

    /**
     * action : 说明详见 RtcmControl.RtcmConfigCallBack <br/>
     */
    private void onRtcmStatusChange(int statusCode) {
        if (null == mRtcmConfigCallBack)
            return;
        mRtcmConfigCallBack.onRtcmStatusChange(statusCode);
    }

    // =================== 差分定位模块相关配置 ========================
    private RtcmChiP mRtcmChip;

    /**
     * <p>
     * action : 返回差分定位模块的具体配置<br/>
     * author: wuguoxian <br/>
     * date: 20200514 <br/>
     */
    private RtcmChiP getRtcmChip() {
        if (null == mRtcmChip) {
            mRtcmChip = new ChipMXT906A();
//            mRtcmChip = new ChipUBLOX();
//            mRtcmChip = new ChipATGM33();
//            mRtcmChip = new ChipUM220();
        }
        return mRtcmChip;
    }

    /**
     * <p>
     * action : 初始化硬件模块的差分配置<br/>
     * author: wuguoxian <br/>
     * date: 20200514 <br/>
     *
     * @param info 差分配置信息
     */
    private void initRtcmChipConfig(RtcmConfigInfo info) {
        if (null == getRtcmChip())
            return;
        getRtcmChip().initChipConfig(new Handler(info.mContext.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    /*
                    case RtcmChiP.MSG_CHIP_CONFIG_INIT_FIAL:
                        onRtcmConfig(RtcmConfigInfo.RESULTCODE_CHIP_ENABLE_RTCM_INIT_FAIL);
                        break;
                    case RtcmChiP.MSG_CHIP_CONFIG_INIT_SUCCESS:
                        onRtcmConfig(RtcmConfigInfo.RESULTCODE_CHIP_ENABLE_RTCM_INIT_SUCCESS);
                        break;
                    */
                    case RtcmChiP.MSG_CHIP_SERIALPORT_INIT_FIAL:
                        onRtcmConfig(RtcmConfigInfo.RESULTCODE_SERIALPORT_INIT_FAIL);
                        break;
                    case RtcmChiP.MSG_CHIP_SERIALPORT_INIT_SUCCESS:
                        onRtcmConfig(RtcmConfigInfo.RESULTCODE_SERIALPORT_INIT_SUCCESS);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    /**
     * <p>
     * action : 断开模块连接<br/>
     * author: wuguoxian <br/>
     * date: 20200514
     */
    private void destroySerialPort() {
        getRtcmChip().destroySerialPort();
    }

    /**
     * <p>
     * action : 给差分定位模块发送命令<br/>
     * author: wuguoxian <br/>
     * date: 20200507 <br/>
     * </p>
     */
    public void sendChipCmd(String cmd) {
        sendChipCmd(cmd.getBytes());
    }

    /**
     * <p>
     * action : 给差分定位模块发送命令<br/>
     * author: wuguoxian <br/>
     * date: 20200507 <br/>
     * </p>
     */
    public void sendChipCmd(byte[] cmd) {
        if (null == getRtcmChip())
            return;
        getRtcmChip().sendCmdByOutputStream(cmd);
    }

    // =================== NMEA 数据监听，处理 ========================
    private String mGGA = "";
    /**
     * action:未定位，未差分<br/>
     */
    public static final int STATUS_POSITION_NONE_RTCM_NONE = (1 << 6);
    /**
     * action:已定位，未差分<br/>
     */
    public static final int STATUS_POSITION_HAVE_RTCM_NONE = (1 << 7);
    /**
     * action:已定位，已差分<br/>
     */
    public static final int STATUS_POSITION_HAVE_RTCM_HAVE = (1 << 8);

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ACTION_GETNMEA_STATUS)) {
                Bundle bundle = intent.getExtras();
                if (bundle == null) {
                    return;
                }
                mGGA = bundle.getString("NMEA");
                if (mGGA.contains(",")) {
                    String[] array = mGGA.split(",");
                    if (array.length > 7) {
                        if (array[6].equals("1")) {
                            onRtcmStatusChange(STATUS_POSITION_HAVE_RTCM_NONE);
                            Log.d(TAG, "状态:已定位，未差分");
                        } else if (array[6].equals("2")) {
                            onRtcmStatusChange(STATUS_POSITION_HAVE_RTCM_HAVE);
                            Log.d(TAG, "状态:已定位，已差分");
                        } else if (array[6].equals("0")) {
                            onRtcmStatusChange(STATUS_POSITION_NONE_RTCM_NONE);
                            Log.d(TAG, "状态:未定位，未差分");
                        }
                    }
                }
                sendData2Server(mGGA);
            }
        }
    };

    /**
     * <p>
     * action : 初始化模块GGA数据监听<br/>
     * author: wuguoxian <br/>
     * date: 20200514 <br/>
     *
     * @param info 差分配置信息
     */
    private void initNmeaDataListener(RtcmConfigInfo info) {
        Intent intent = new Intent(info.mContext, GetLocationService.class);
        info.mContext.startService(intent);

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_GETNMEA_STATUS);
        filter.addAction(ACTION_GETLOCATION);
        info.mContext.registerReceiver(mReceiver, filter);

//        new Handler(info.mContext.getMainLooper()).postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                onRtcmConfig((null==mGGA||mGGA.length()<1)
//                        ?RtcmConfigInfo.RESULTCODE_NMEA_DATA_LISTENER_FAIL
//                        :RtcmConfigInfo.RESULTCODE_NMEA_DATA_LISTENER_SUCCESS);
//            }
//        },5000);
    }

    // =================== 差分服务器数据交互处理 ========================

    private RtcmServer mRtcmServer;
    private Handler mHandlerRtcmServer;
    private boolean isSendMSG = false;

    /**
     * <p>
     * action : 初始化差分服务器连接<br/>
     * author: wuguoxian <br/>
     * date: 20200514 <br/>
     *
     * @param info 差分配置信息
     */
    private void initRtcmServer(final RtcmConfigInfo info) {
        Log.d(TAG, "enter initRtcmServer");
        if (mRtcmServer != null) {
            try {
                mRtcmServer.disConnect();
            } catch (Exception e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }
        }
        if (null == mHandlerRtcmServer) {
            mHandlerRtcmServer = new Handler(info.mContext.getMainLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    switch (msg.what) {
                        case RtcmServer.MSG_SERVER_RETURNS_DATA:
                            serverReturnDataProcess(msg);
                            break;
                        case RtcmServer.MSG_SERVER_SOCKET_CONNECTION_INIT_COMPLETE:
                            mHandlerRtcmServer.sendEmptyMessageDelayed(RtcmServer.MSG_SEND_DATA_BY_NTRIP, 1000);
                            break;
                        case RtcmServer.MSG_SERVER_SOCKET_CONNECTION_EXCEPTION:
                            isSendMSG = false;
                            Log.d(TAG, "数据中心连接失败...正在重新连接...");
                            initRtcmServer(info);
                            onRtcmConfig(RtcmConfigInfo.RESULTCODE_NTRIP_CONNECT_FAIL);
                            break;
                        case RtcmServer.MSG_SEND_DATA_BY_NTRIP:
                            mRtcmServer.sendBytes(UtilNtrip.CreateHttpRequsets(info.mMountPoint,
                                    info.mUserName, info.mUserPassword));
                            Log.d(TAG, "ClientConnService> userId" + info.mUserName + "password" + info.mUserPassword);
                            Log.d(TAG, "正在接入服务器.....");
                            break;
                        default:
                            break;
                    }
                }
            };
        }
        try {
            mRtcmServer = new RtcmServer(mHandlerRtcmServer,
                    mRtcmConfigInfo.mServerIP, Integer.valueOf(mRtcmConfigInfo.mServerPort));
            new Thread(mRtcmServer).start();
            Log.d(TAG, "开始连接数据中心.....");
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    /**
     * <p>
     * action : 发送GGA数据到差分服务器<br/>
     * author: wuguoxian <br/>
     * date: 20200514 <br/>
     *
     * @param data GGA数据
     */
    private void sendData2Server(String data) {
        Log.d(TAG, "mRtcmServer = " + mRtcmServer + "; isSendMSG = " + isSendMSG);
        if (mRtcmServer != null && isSendMSG) {
            mRtcmServer.sendBytes(data.getBytes());
            Log.d(TAG, "正在发送GGA数据>mGGA= " + data);
        }
    }

    /**
     * <p>
     * action : 处理差分服务器返回的数据<br/>
     * author: wuguoxian <br/>
     * date: 20200514 <br/>
     *
     * @param msg 处理差分服务器返回的数据的Message
     */
    private void serverReturnDataProcess(Message msg) {
        try {
            int length = msg.arg1, isFirst = msg.arg2;
            Log.d(TAG, "接收到数据长度   " + length);
            if (length <= 0) {
                return;
            }
            byte[] bytesd = (byte[]) msg.obj;
            byte[] bytes = new byte[length];
            System.arraycopy(bytesd, 0, bytes, 0, length);
            String mSourceList = new String(bytes);
            String hexContent = Utils.bytesArrayToHex(bytes);
            Log.d(TAG, "content.length" + length);
            Log.d(TAG, "mSourceList bytes = " + hexContent);
            Log.d(TAG, "mSourceList = " + mSourceList);
            ArrayList<String> sourceList = new ArrayList<String>();
            sourceList.add(mSourceList);
            for (int i = 0; i < length; i++) {
                if (bytes[i] == 0x0D && bytes[i + 1] == 0x0A) {
                    String srtTemp = new String(bytes, i + 2, length - i - 2);
                    if (srtTemp != null && srtTemp.trim().length() > 2) {
                        sourceList.add(srtTemp);
                    }
                }
            }
            for (int i = 0; i < sourceList.size(); i++) {
                Log.d(TAG, "mSourceList sourceList[" + i + "] = " + sourceList.get(i));
            }
            if (mSourceList.startsWith("ICY 200 OK")) {
                Log.d(TAG, "数据中心登录成功.....");
                isSendMSG = true;
                onRtcmConfig(RtcmConfigInfo.RESULTCODE_NTRIP_CONNECT_SUCCESS);
            } else {
                if(1==isFirst){
                    onRtcmConfig(RtcmConfigInfo.RESULTCODE_NTRIP_CONNECT_FAIL);
                }
                if (bytes[0] == -45) {
                    //差分服务器返回的补偿参数写入定位模块
                    sendChipCmd(bytes);
                    onRtcmParameterUpdate(bytes);
                } else {
                    Log.d(TAG, "连接结果：" + mSourceList);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    /**
     * <p>
     * action : 断开差分服务器连接<br/>
     * author: wuguoxian <br/>
     * date: 20200514 <br/>
     *
     * @param msg 处理差分服务器返回的数据的Message
     *
     */
    private void disConnectRtcmServer() {
        isSendMSG = false;
        if (null != mRtcmServer) {
            mRtcmServer.disConnect();
        }
    }
}
