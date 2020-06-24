package com.kuyou.rtcm.chip;

import android.os.Handler;

/**
 * <p>
 * action 定位模块ATGM33 <br/>
 * author: wuguoxian <br/>
 * 厂商: 中科微 <br/>
 * </p>
 */
public class ChipATGM33 extends RtcmChiP {

    private static final String PATH_UART = "/dev/ttyMT0";
    private static final int BAUDRATE = 9600;

    @Override
    public String getPathUart() {
        return PATH_UART;
    }

    @Override
    public int getBaudrate() {
        return BAUDRATE;
    }

    @Override
    public void initChipConfig(final Handler handler) {
        super.initChipConfig(handler);
    }
}
