package com.kuyou.rtcm;

import android.content.Context;

public class RtcmConfigInfo {

    /**
     * action:串口初始化失败<br/>
     */
    public static final int RESULTCODE_SERIALPORT_INIT_FAIL = (1 << 0);
    /**
     * action:串口初始化成功<br/>
     */
    public static final int RESULTCODE_SERIALPORT_INIT_SUCCESS = (1 << 1);

    /**
     * action:开启模块的RTCM协议解析失败<br/>
     */
    public static final int RESULTCODE_CHIP_ENABLE_RTCM_INIT_FAIL = (1 << 2);
    /**
     * action:开启模块的RTCM协议解析成功<br/>
     */
    public static final int RESULTCODE_CHIP_ENABLE_RTCM_INIT_SUCCESS = (1 << 3);

    /**
     * action:开启NEMA相关数据监听失败<br/>
     */
    public static final int RESULTCODE_NMEA_DATA_LISTENER_FAIL = (1 << 4);
    /**
     * action:开启NEMA相关数据监听成功<br/>
     */
    public static final int RESULTCODE_NMEA_DATA_LISTENER_SUCCESS = (1 << 5);

    /**
     * action:差分服务器连接失败<br/>
     */
    public static final int RESULTCODE_NTRIP_CONNECT_FAIL = (1 << 6);
    /**
     * action:差分服务器连接成功<br/>
     */
    public static final int RESULTCODE_NTRIP_CONNECT_SUCCESS = (1 << 7);

    protected Context mContext;
    protected String mUserName;
    protected String mUserPassword;
    protected String mServerIP;
    protected String mServerPort;
    protected String mMountPoint;

    public RtcmConfigInfo(Builder bl) {
        mContext = bl.context;
        mUserName = bl.userName;
        mUserPassword = bl.userPassword;
        mServerIP = bl.serverIP;
        mServerPort = bl.serverPort;
        mMountPoint = bl.mountPoint;
    }

    public static class Builder {
        Context context;
        String userName = null, userPassword, serverIP, serverPort, mountPoint;

        public Builder context(Context val) {
            context = val.getApplicationContext();
            return Builder.this;
        }

        /**
         * <p>
         * action : 差分服务器用户名<br/>
         * author: wuguoxian <br/>
         * date: 20200507 <br/>
         * policy: ntrip <br/>
         */
        public Builder userName(String val) {
            userName = val;
            return Builder.this;
        }

        /**
         * <p>
         * action : 差分服务器用户密码<br/>
         * author: wuguoxian <br/>
         * date: 20200507 <br/>
         * policy: ntrip <br/>
         */
        public Builder userPassword(String val) {
            userPassword = val;
            return Builder.this;
        }

        /**
         * <p>
         * action : 差分服务器IP地址<br/>
         * author: wuguoxian <br/>
         * date: 20200507 <br/>
         * policy: ntrip <br/>
         */
        public Builder serverIP(String val) {
            serverIP = val;
            return Builder.this;
        }

        /**
         * <p>
         * action : 差分服务器端口<br/>
         * author: wuguoxian <br/>
         * date: 20200507 <br/>
         * policy: ntrip <br/>
         */
        public Builder serverPort(String val) {
            serverPort = val;
            return Builder.this;
        }

        /**
         * <p>
         * action : 差分服务器挂载点<br/>
         * author: wuguoxian <br/>
         * date: 20200507 <br/>
         * policy: ntrip <br/>
         * remark:<br/>
         * &nbsp 01 不同挂载点返回的数据对应不同RTCM协议版本<br/>
         */
        public Builder mountPoint(String val) {
            mountPoint = val;
            return Builder.this;
        }

        public RtcmConfigInfo build() {
            return new RtcmConfigInfo(this);
        }
    }
}
