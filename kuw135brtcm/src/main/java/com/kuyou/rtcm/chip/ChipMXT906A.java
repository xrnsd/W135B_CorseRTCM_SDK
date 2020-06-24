package com.kuyou.rtcm.chip;

import android.os.Handler;

/**
 * <p>
 * action 定位模块MXT906A <br/>
 * author: wuguoxian <br/>
 * 厂商: 梦芯 <br/>
 * </p>
 */
public class ChipMXT906A extends RtcmChiP {

    private static final String PATH_UART = "/dev/ttyMT0";
    private static final int BAUDRATE = 115200;

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
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (!sendCmdByOutputStream("$CFGPRT,1,115200,h01,h01"))
                    handler.sendEmptyMessage(MSG_CHIP_CONFIG_INIT_FIAL);
            }
        });
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!sendCmdByOutputStream("$CFGPRT,0,115200,h07,h01"))
                    handler.sendEmptyMessage(MSG_CHIP_CONFIG_INIT_FIAL);
            }
        }, 3000);
    }
}
