package com.kuyou.rtcm.chip;

import android.os.Handler;
import android.util.Log;

import com.android.serialport.SerialPort;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public abstract class RtcmChiP {
    private static final String TAG = "RtcmChiP";
    public static final int MSG_CHIP_CONFIG_INIT_FIAL = 866;
    public static final int MSG_CHIP_CONFIG_INIT_SUCCESS = 867;
    public static final int MSG_CHIP_SERIALPORT_INIT_FIAL = 868;
    public static final int MSG_CHIP_SERIALPORT_INIT_SUCCESS = 869;

    protected OutputStream mOutputStream;
    protected SerialPort mSerialPort;

    /**
     * <p>
     * action : 串口路径<br/>
     * author: wuguoxian <br/>
     * date: 20200512 <br/>
     * </p>
     */
    public String getPathUart() {
        return "null";
    }

    /**
     * <p>
     * action : 串口波特率<br/>
     * author: wuguoxian <br/>
     * date: 20200512 <br/>
     * </p>
     */
    public int getBaudrate() {
        return -1;
    }

    /**
     * <p>
     * action : 通过串口给模块发送初始化差分配置命令<br/>
     * author: wuguoxian <br/>
     * date: 20200512 <br/>
     * </p>
     */
    public void initChipConfig(final Handler handler) {
        handler.sendEmptyMessage(initSerialPort()
                ?MSG_CHIP_SERIALPORT_INIT_SUCCESS:MSG_CHIP_SERIALPORT_INIT_FIAL);
    }

    public boolean sendCmdByOutputStream(String cmd) {
        byte[] cmdByte = cmd.getBytes();
        return sendCmdByOutputStream(cmdByte);
    }

    public boolean sendCmdByOutputStream(byte[] cmdByte) {
        byte[] CFGDYNC_BYTE = new byte[cmdByte.length + 2];
        System.arraycopy(cmdByte, 0, CFGDYNC_BYTE, 0, cmdByte.length);
        CFGDYNC_BYTE[cmdByte.length] = 0x0D;
        CFGDYNC_BYTE[cmdByte.length + 1] = 0x0A;
        try {
            if (null != mOutputStream) {
                mOutputStream.write(CFGDYNC_BYTE);
                Log.d(TAG, "mOutputStream write sucess!!");
            } else {
                Log.e(TAG, "mOutputStream is null =======================");
            }
            return true;
        } catch (IOException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        return false;
    }


    private boolean initSerialPort() {
        try {
            if (null == mSerialPort) {
                mSerialPort = new SerialPort(new File(getPathUart()), getBaudrate(), 0);
            }
            mOutputStream = mSerialPort.getOutputStream();
            return true;
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } catch (java.lang.UnsatisfiedLinkError e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        return false;
    }

    public void destroySerialPort() {
        try {
            if (null != mOutputStream) {
                mOutputStream.close();
            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        if (mSerialPort != null) {
            mSerialPort.close();
            mSerialPort = null;
        }
    }
}
